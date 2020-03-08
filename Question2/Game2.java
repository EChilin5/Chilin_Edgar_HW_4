import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Game2 {

    private static List<PlayerInfo> list = Collections.synchronizedList(new ArrayList<>());
    private static List<PlayerInfo> Endlist = Collections.synchronizedList(new ArrayList<>());
    private static AtomicInteger cycleCount = new AtomicInteger();
    private static AtomicInteger tournamentCount = new AtomicInteger();

private static int occurence = 0;
private static int occurence2 = 0;
    private static int count = 0;
    private static int count2 = -1;
    private static int count3 = -1;
    private static int setSize = 0;
    private static int mod = 0;
    private static boolean state = true;
    private static int create =0;

    private static final Object lock2 = new Object();
    private static final Object lock3 = new Object();

    public static class PlayerCreate implements Runnable {
        private String name;
        private int[] arr;
        List<PlayerInfo> list2;
        static ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy


        private final Object lock = new Object();


        public PlayerCreate(List<PlayerInfo> list2, String name, int[] arr) {
            this.name = name;
            this.arr = arr;
            this.list2 = list2;
        }

        @Override
        public void run() {
            try {
                Instant start = Instant.now();
                Create();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("Create thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (Exception e) {

            }

        }

        private void Count() {
            counterLock.lock();
            try {
                count++;
            } finally {
                counterLock.unlock();
            }

        }

        private void awake() {
            synchronized (lock2) {
                lock2.notifyAll();

            }
        }

        private synchronized void Create() throws InterruptedException {
            synchronized (lock) {
                Random r = new Random();
                boolean inGame = true;
                boolean win = false;
                int score = 0;
                for (int i = 0; i < 2; i++) {
                    Count();

                    int move = arr[r.nextInt(arr.length)];
                    int num = count;
                    String id = name + " " + num;

                    list2.add(new PlayerInfo(id, move, score, inGame, win));

                }
                if (list2.size() == setSize) {
                    System.out.println("maxx");
                    //    start = true;
                    awake();
                }
            }

        }
    }


    public static class Tournament implements Runnable {
        private int[] arr;
        private List<PlayerInfo> list2;
        private List<PlayerInfo> End;
        static ReentrantLock counterLock2 = new ReentrantLock(true); // enable fairness policy
        private Random r = new Random();


        public Tournament(List<PlayerInfo> list2, List<PlayerInfo> End,
                           int[] arr) {
            this.arr = arr;
            this.list2 = list2;
            this.End = End;
        }

        //@Override
        public void run() {
            try {
                Instant start = Instant.now();
                TournamentEliminator();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("tournament thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (InterruptedException e) {

            }
        }

        private void Count2() {
            counterLock2.lock();
            try {
                count2++;
            } finally {
                counterLock2.unlock();
            }

        }

        private void TournamentEliminator() throws InterruptedException, IndexOutOfBoundsException {
            synchronized (lock2) {


                String opponentOne = "";
                int opOneMove = 0;
                boolean opOne = true;

                String opponentTwo = "";
                int opTwoMove = 0;
                boolean opTwo = true;

                lock2.wait();
                while (state) {
                 //   for (int i = 0; i < 2; i++) {
                    if(list2.size() %2 != 0 && occurence2 ==0 ){
                        Count2();
                        End.add(new PlayerInfo(list2.get(count2).getName(),
                                list2.get(count2).getMove(), 0, opOne, true
                        ));
                        tournamentCount.addAndGet(1);
                        occurence2 = 1;
                    }
                       tournamentCount.addAndGet(2);
                        Count2();

                        if (count2 < list2.size()) {

                            opponentOne = list2.get(count2).getName();
                            opOneMove = list2.get(count2).getMove();
                            opOne = list2.get(count2).isInGame();

                        }

                        Count2();
                        if (count2 < list2.size()) {
                            opponentTwo = list2.get(count2).getName();
                            opTwoMove = list2.get(count2).getMove();
                            opTwo = list2.get(count2).isInGame();
                        }

                        if (!opponentOne.equals("") && !opponentTwo.equals("") && opOne && opTwo ) {
                            System.out.println(opponentOne + " vs " + opponentTwo);

                            while (opOneMove == opTwoMove) {
                                opOneMove = arr[r.nextInt(arr.length)];
                                opTwoMove = arr[r.nextInt(arr.length)];

                            }
                            if (opOneMove == 1 && opTwoMove == 3 || opOneMove == 3 && opTwoMove == 2
                                    || opOneMove == 2 && opTwoMove == 1) {
                                System.out.println("                     " + opponentOne + " wins game list 2 ");
                                opOne = true;
                                opTwo = false;
                            } else {
                                System.out.println("                     " + opponentTwo + " wins game " + "list2");
                                opOne = false;
                                opTwo = true;
                            }
                            if (opOne) {
                                End.add(new PlayerInfo(opponentOne,
                                        opOneMove, 0, opOne, true
                                ));
                            }
                            if (opTwo) {
                                End.add(new PlayerInfo(opponentTwo,
                                        opTwoMove, 0, opTwo, true));
                            }
                        }
                    //}
                    if (list2.size() == tournamentCount.get()) {
                      //  mod = mod/2;
                        occurence2 = 0;
                        tournamentCount.set(0);
                        list2.removeAll(list2);

                        if(Endlist.size() == 1){
                            state = false;
                            lock2.notifyAll();
                        }else {
                            System.out.println(" tournament awake lock 2 ");
                            count2=-1;
                            lock2.notifyAll();
                            lock2.wait();
                        }
                    }
                     opponentOne = "";
                     opOneMove = 0;
                     opOne = true;
                     opponentTwo = "";
                     opTwoMove = 0;
                     opTwo = true;

                }
            }
        }

    }

    private static class Cycle implements Runnable {
        private List<PlayerInfo> endlist;
        private List<PlayerInfo> list2;
        private int[] arr;
       private  Hashtable<String, Integer>  memo;
        static ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy
        private Random r = new Random();


        public Cycle(List<PlayerInfo> endlist, List<PlayerInfo> list2,  int[] arr) {
            this.list2 = list2;
            this.endlist = endlist;

            this.arr = arr;
        }

        @Override
        public void run() {
            try {
                Instant start = Instant.now();
                End();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("round thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (InterruptedException e) {

            }
        }

        private void Count3() {
            counterLock.lock();
            try {
                count3++;
            } finally {
                counterLock.unlock();
            }

        }

        private void End() throws InterruptedException , IndexOutOfBoundsException{
            synchronized (lock2) {
                System.out.println(" end");
                lock2.wait();

                while (state) {

                    String opponentOne = "";
                    int opOneMove = 0;
                    boolean opOne = true;

                    String opponentTwo = "";
                    int opTwoMove = 0;
                    boolean opTwo = true;
                    if (Endlist.size() % 2 != 0 && occurence == 0) {
                        Count3();
                        list2.add(new PlayerInfo(endlist.get(count3).getName(),
                                endlist.get(count3).getMove(), 0, opOne, true
                        ));
                        occurence = 1;
                        cycleCount.addAndGet(1);
                    }

                    //for (int i = 0; i < 2; i++) {
                    cycleCount.addAndGet(2);
                    Count3();
                    if (count3 < endlist.size()) {

                        opponentOne = endlist.get(count3).getName();
                        opOneMove = endlist.get(count3).getMove();
                        opOne = endlist.get(count3).isInGame();

                    }
                    Count3();
                    if (count3 < Endlist.size()) {

                        opponentTwo = endlist.get(count3).getName();
                        opTwoMove = endlist.get(count3).getMove();
                        opTwo = endlist.get(count3).isInGame();
                    }

                    if (!opponentOne.equals("") && !opponentTwo.equals("") && opOne && opTwo) {
                        System.out.println(opponentOne + " vs " + opponentTwo);
                        while (opOneMove == opTwoMove) {
                            opOneMove = arr[r.nextInt(arr.length)];
                            opTwoMove = arr[r.nextInt(arr.length)];

                        }
                        if (opOneMove == 1 && opTwoMove == 3 || opOneMove == 3 && opTwoMove == 2
                                || opOneMove == 2 && opTwoMove == 1) {
                            System.out.println("                     " + opponentOne + " wins game ");
                            opOne = true;
                            opTwo = false;
                        } else {
                            System.out.println("                     " + opponentTwo + " wins game ");
                            opOne = false;
                            opTwo = true;
                        }
                        if (opOne) {
                            list2.add(new PlayerInfo(opponentOne,
                                    opOneMove, 0, opOne, true
                            ));
                        }
                        if (opTwo) {
                            list2.add(new PlayerInfo(opponentTwo,
                                    opTwoMove, 0, opTwo, true));
                        }
                    }


                    if (cycleCount.get() == endlist.size()) {
                        mod = mod / 2;
                        occurence = 0;
                        cycleCount.set(0);
                        endlist.removeAll(endlist);
                        if (list2.size() == 1) {
                            state = false;
                            lock2.notifyAll();
                        } else {
                            System.out.println(" cycle awake lock 2 ");
                            count3 = -1;
                            lock2.notifyAll();
                            lock2.wait();
                        }
                    }
                }
                }

            }
        //}
    }


    public static void main(String[] args) {
        setSize = 20;
        tournamentCount.set(0);
        cycleCount.set(0);
        mod =setSize;



        int[] arr = {1, 2, 3};

        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
        ExecutorService service = Executors.newFixedThreadPool(coreCount);

        for (int i = 0; i < 10; i++) {
            service.execute(new Game2.PlayerCreate(list, "Player", arr));
        }

        for (int i = 0; i < 2; i++) {
            service.execute(new Game2.Tournament(list, Endlist,  arr));
        }

        for (int i = 0; i < 2; i++) {
            service.execute(new Game2.Cycle(Endlist, list, arr));

        }

        service.shutdown();

    }


}
