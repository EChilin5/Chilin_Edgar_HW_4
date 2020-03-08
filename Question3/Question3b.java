import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class Question3b {
///Users/edgarchilin/Desktop/My Project translate-e91388007601.json
    private static AtomicInteger level = new AtomicInteger();
    private static AtomicInteger stopAt = new AtomicInteger();
    private static AtomicInteger end = new AtomicInteger();

    public static List<TextFileAnalysis> storage = Collections.synchronizedList(new ArrayList<>());;
    public static List<TextFileAnalysis> reverseCopy = Collections.synchronizedList(new ArrayList<>());
    public static final Object lock1 = new Object();
    public static final Object lock3 = new Object();
    private static boolean state = true;
    private static boolean state2 = true;


    private static class READ implements Runnable{
        private List<TextFileAnalysis> store;

        public READ(List<TextFileAnalysis> store) {
            this.store = store;
        }

        @Override
        public void run() {
        try{
            Instant start = Instant.now();
           ReadTextFile();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Create thread name " + Thread.currentThread().getName() + " " +
                    timeElapsed +  " millis");
        }catch (Exception e){

        }
        }
        private void Awake(){
            synchronized (lock3){
                lock3.notifyAll();
            }
        }
        private void ReadTextFile() throws InterruptedException{
            synchronized (lock1){
                int count = 0;
                System.out.println("v1");
                Scanner sc2 = null;
                try {
                    System.out.println("v2");

                    sc2 = new Scanner(new File("Question3/Independence.txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                count = 0;

                while (sc2.hasNextLine()) {
                    count=1;
                    Scanner s2 = new Scanner(sc2.nextLine());
                    while (s2.hasNext()) {
                        String s = s2.next();
                        store.add(new TextFileAnalysis(s, count));
                        count =0;
                    }
                    store.add(new TextFileAnalysis(" ", count));
                    System.out.println("Sleeping this current thread");
                }
              //  System.out.println(store.size());
                Awake();

            }
        }
    }

    private static class Reverse implements Runnable {
        private List<TextFileAnalysis> store;
        private List<TextFileAnalysis> storeRevers;
        static ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy


        private Reverse(List<TextFileAnalysis> store, List<TextFileAnalysis> storeRevers) {
            this.store = store;
            this.storeRevers = storeRevers;
        }

        @Override
        public void run() {
            try {
                Instant start = Instant.now();
                ReOrder();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("reorder thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (Exception e) {

            }
        }



        private void Awake() {
            synchronized (lock1) {
                lock1.notifyAll();
                System.out.println("wakeing a thread read");
            }
        }

        private void ReOrder() throws InterruptedException, IOException {
            synchronized (lock3) {
                System.out.println("asleep");
                lock3.wait();
                //System.out.println("Awwalke");
                stopAt.set(0);
                end.set(1);

                while (state) {
                    int count = 0;
                    for (level.get(); level.get() < reverseCopy.size(); level.addAndGet(1)) {

                        if (reverseCopy.get(level.get()).getNewLine() == 1) {
                            BufferedWriter writer = new BufferedWriter(
                                    new FileWriter("Question1/output_question2.txt", true)  //Set true for append mode
                            );
                            for (int j = level.get() - 1; j >= stopAt.get(); j--) {

                                System.out.print(reverseCopy.get(j).getWord() + " ");
                                writer.write(reverseCopy.get(j).getWord() + " ");
                                count++;
                            }
                            writer.newLine();
                            writer.close();
                            stopAt.set(level.get());
                            if(count < reverseCopy.size()){

                                lock3.notifyAll();

                                System.out.println("thread name is asleep " + Thread.currentThread().getName());
                                lock3.wait();
                                System.out.println("thread name is awake " + Thread.currentThread().getName());
                                if(state == false){
                                    break;
                                }

                            }

                        }
                    }
                    state= false;

                }
                if(end.get() == 1 && state2) {
                    state2=false;
                    end.set(stopAt.get());
                    BufferedWriter writer = new BufferedWriter(
                            new FileWriter("Question1/output_question2.txt", true)  //Set true for append mode
                    );
                    for (int j = reverseCopy.size() - 1; j >= end.get(); j--) {

                      System.out.print(reverseCopy.get(j).getWord() + " ");
                       writer.write(reverseCopy.get(j).getWord() + " ");

                    }
                   writer.close();

                    lock3.notifyAll();
                }else{
                    lock3.notifyAll();
                }
            }
        }
    }


    public static void main(String[] args){
        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
        ExecutorService service = Executors.newFixedThreadPool(coreCount);
        service.execute(new READ(reverseCopy));
        for(int i = 0; i < 10; i++){
            service.execute(new Reverse(storage, reverseCopy));

        }
        service.shutdown();
    }


}
