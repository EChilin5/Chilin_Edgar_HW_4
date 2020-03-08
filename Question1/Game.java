import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Game {

    private static List<PlayerInfo> list = Collections.synchronizedList(new ArrayList<>());
    private static List<PlayerInfo> Endlist = Collections.synchronizedList(new ArrayList<>());

    private static int count = 0;
    private static int count2 = -1;
    private static int setSize =0;
    private static int newSize;
    private static int create;
    private static boolean state = true;

    private static final Object lock3 = new Object();



    private static class PlayerCreate implements Runnable {
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
                System.out.println();
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
            synchronized (lock3) {
                lock3.notifyAll();

            }
        }

        private synchronized void Create() throws InterruptedException {
            synchronized (lock) {
                Random r = new Random();
                boolean inGame = true;
                boolean win = false;
                int score = 0;
                for (int i = 0; i < create; i++) {
                    Count();
                    int move = arr[r.nextInt(arr.length)];
                    int num = count;
                    String id = name + " " + num;

                    list2.add(new PlayerInfo(id, move, score, true, win));

                }
                if (list2.size() == setSize) {
                    awake();
                }

            }

        }
    }


    public static class PlayerValidation implements Runnable {
        private String name;
        BlockingQueue<PlayerInfo> player;
        private int validate;
        private List<PlayerInfo> list4;
        private List<PlayerInfo> Enlist;
        Object lock1 = new Object();
        int[] arr;
        static ReentrantLock counterLock2 = new ReentrantLock(true); // enable fairness policy


        private PlayerValidation(String name, List<PlayerInfo> list4,
                                 List<PlayerInfo> Enlist,
                                 int[] arr, int validate
        ) {
            this.name = name;
            this.arr = arr;
            this.player = player;
            this.validate = validate;
            this.list4 = list4;
            this.Enlist = Enlist;
        }

        private void Count2() {
            counterLock2.lock();
            try {
                count2++;
            } finally {
                counterLock2.unlock();
            }
        }

        @Override
        public void run() {
            try {
                Instant start = Instant.now();
                Match2();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("Match thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (Exception e) {

            }
        }

        private void Match2() throws InterruptedException {
            synchronized (lock3) {
               lock3.wait();
              //  while (true) {
                    while (!list4.isEmpty()) {
                        String pname = "";
                        int score = 0;
                        int move = 0;
                        boolean inGame = false;

                        Count2();
                            if(count2 < list4.size()) {
                                System.out.println(list4.size());
                                pname = list4.get(count2).getName();
                                score = list4.get(count2).getScore();
                                move = list4.get(count2).getMove();
                                inGame = list4.get(count2).isInGame();
                            }


                       // if (inGame == true) {
                            for (int j = 0; j < list4.size(); j++) {
                                int opponentMove = list4.get(j).getMove();
                                if (move == opponentMove) {
                                } else if (move == 1 && opponentMove == 3) {
                                    score++;
                                } else if (move == 2 && opponentMove == 1) {
                                    score++;
                                } else if (move == 3 && opponentMove == 2) {
                                    score++;
                                } else {
                                    score--;
                                }
                            }
                            try {
                                if (newSize == 2) {
                                ///    System.out.println("executed ");
                                    System.out.println(pname+ " " + move + "  " +score);
                                    Enlist.add(new PlayerInfo(pname, move, score, true, true));


                                }
                                list4.set(count2, new PlayerInfo(pname, move, score, true, true));

                            } catch (Exception e) {
                                System.out.println("unable to update score");
                            }
                            if (count2 == newSize) {
                                count2 = -1;
                                newSize = newSize - 1;
                                setSize = newSize;

                                int location = 0;
                                int lowest = list4.get(0).getScore();
                                for (int i = 1; i < list4.size(); i++) {
                                    if (list4.get(i).getScore() < lowest) {
                                        lowest = list4.get(i).getScore();
                                        location = i;
                                    }
                                }
                                list4.remove(list4.get(location));
                                if(list4.size() == 1){
                                    lock3.notifyAll();
                                }
                            }

                        }

                 //   }
            }
        }
    }

    public static class PlayerWinner implements Runnable {

        private List<PlayerInfo> list4;

        public PlayerWinner(List<PlayerInfo> list4) {
            this.list4 = list4;
        }

        @Override
        public void run() {
            try {
                Instant start = Instant.now();
                Winner();
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("Winner thread name " + Thread.currentThread().getName() + " " +
                        timeElapsed +  " millis");
            } catch (Exception e) {
            }
        }

        private void Winner() throws InterruptedException {
            synchronized (lock3) {
                lock3.wait();
                lock3.wait();
                int Max = 0;
                String name = " ";
                for (PlayerInfo i : list4) {
                    if (i.getScore() > Max) {
                        Max = i.getScore();
                        name = i.getName();
                    }
                }
                System.out.println("winner" + name + " score " + Max );
                list4.removeAll(list4);
            }
        }
    }


    public static void main(String[] args) {
        Random r = new Random();
        int[] arr = {1, 2, 3};
        int x = 0;
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter any number: ");
        x= scan.nextInt();
         setSize =x;
        newSize = setSize-1;
        scan.close();
        int loop = 0;;
        create = 0;
        for(int i = 1; i < 10; i++){
            if(x % i == 0 && x!=i){
                loop = i;
                create = (x/i);
            }
        }


        if(x!=0) {
            int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
            ExecutorService service = Executors.newFixedThreadPool(coreCount);
            service.execute(new PlayerWinner(Endlist));
            for (int i = 0; i < loop; i++) {
                service.execute(new PlayerCreate(list, "Player", arr));
            }

            for (int i = 0; i < 6; i++) {
                service.execute(new PlayerValidation("match", list, Endlist, arr, r.nextInt(20)));

            }
            service.shutdown();
        }
    }

}
