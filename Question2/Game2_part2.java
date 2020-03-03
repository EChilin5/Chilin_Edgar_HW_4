import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Game2_part2{

    //private static BlockingQueue<PlayerInfo> queue = new ArrayBlockingQueue<PlayerInfo>(20);
    private static List<PlayerInfo> list = Collections.synchronizedList(new ArrayList<>());
    private static List<PlayerInfo> Endlist = Collections.synchronizedList(new ArrayList<>());
    private static Hashtable<String, Integer>  memorized = new Hashtable<String, Integer>();


    private static int count = 0;
    private static int count2 = -1;
    private static int count3 = -1;
    private static int setSize = 0;
    private static int mod = 0;
    private static int mod2 = 0;
    private static boolean state = true;

    private static Object lock2 = new Object();
    private static Object lock3 = new Object();

    public static class PlayerCreate implements Runnable {
        private String name;
        private int[] arr;
        List<PlayerInfo> list2;
        static ReentrantLock counterLock = new ReentrantLock(true); // enable fairness policy


        private Object lock = new Object();


        public PlayerCreate(List<PlayerInfo> list2, String name, int[] arr) {
            this.name = name;
            this.arr = arr;
            this.list2 = list2;
        }

        @Override
        public void run() {
            try {
                Create();
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
                for (int i = 0; i < 5; i++) {
                    Count();

                    int move = arr[r.nextInt(arr.length)];
                    int num = count;
                    String id = name + " " + num;

                    list2.add(new PlayerInfo(id, move, score, inGame, win));
                    System.out.println("Thread create player " + id);

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
        private  Hashtable<String, Integer>  memo;
        static ReentrantLock counterLock2 = new ReentrantLock(true); // enable fairness policy
        private Random r = new Random();


        public Tournament(List<PlayerInfo> list2, List<PlayerInfo> End,
                          Hashtable<String, Integer>  memo, int[] arr) {
            this.arr = arr;
            this.list2 = list2;
            this.End = End;
        }

        //@Override
        public void run() {
            try {
                TournamentEliminator();
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

        private void TournamentEliminator() throws InterruptedException {
            synchronized (lock2) {
                mod = setSize / 2;

                String opponentOne = "";
                int opOneMove = 0;
                boolean opOne = true;
                // boolean opOneGame = true;
                int LocationOne = 0;

                String opponentTwo = "";
                int opTwoMove = 0;
                boolean opTwo = true;
                //boolean opTwoGame = true;
                int LocationTwo = 0;

                lock2.wait();
                while (state) {
                    for (int i = 0; i < 2; i++) {
                        Count2();
                        if(list2.size() == 1){
                            state = false;
                            lock2.notifyAll();
                        }
                        if (count2 < list2.size()) {
                            LocationOne = count2;
                            opponentOne = list2.get(count2).getName();
                            opOneMove = list2.get(count2).getMove();
                            opOne = list2.get(count2).isInGame();

                        }
                        Count2();
                        if (count2 < list2.size()) {
                            LocationTwo = count2;
                            opponentTwo = list2.get(count2).getName();
                            opTwoMove = list2.get(count2).getMove();
                            opTwo = list2.get(count2).isInGame();
                        }

                        if (!opponentOne.equals("") && !opponentTwo.equals("") && opOne == true && opTwo == true) {
                            System.out.println(opponentOne + " vs " + opponentTwo);

                            while (opOneMove == opTwoMove) {
                                opOneMove = arr[r.nextInt(arr.length)];
                                opTwoMove = arr[r.nextInt(arr.length)];

                            }
                            String combo = ""+opOneMove + opponentTwo;


                            if (opOneMove == 1 && opOneMove == 3 || opOneMove == 3 && opOneMove == 2
                                    || opOneMove == 2 && opOneMove == 1) {
                                System.out.println("                     " + opponentOne + " wins game ");
                                opOne = true;
                                opTwo = false;
                            } else {
                                System.out.println("                     " + opponentTwo + " wins game ");
                                opOne = false;
                                opTwo = true;
                            }
                            if (opOne == true) {
                                End.add(new PlayerInfo(opponentOne,
                                        opOneMove, 0, opOne, true
                                ));
                            }
                            if (opTwo == true) {
                                End.add(new PlayerInfo(opponentTwo,
                                        opTwoMove, 0, opTwo, true));
                            }
                        }
                    }
                    if (mod == End.size()) {
                        if(list2.size() == 1){
                            state = false;
                            lock2.notifyAll();
                            break;
                        }
                        list2.removeAll(list2);
                        System.out.println(" awake lock 2 ");
                        lock2.notifyAll();
                        lock2.wait();
                    }
                    if(list2.size() == 1){
                        state = false;
                        lock2.notifyAll();
                    }

                }
            }
        }

        private void Awake() {
            synchronized (lock3) {
                lock3.notifyAll();
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


        public Cycle(List<PlayerInfo> endlist, List<PlayerInfo> list2, Hashtable<String, Integer>  memo, int[] arr) {
            this.list2 = list2;
            this.endlist = endlist;
            this.memo = memo;
            this.arr = arr;
        }


        @Override
        public void run() {
            try {
                End();
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

        private void End() throws InterruptedException {
            synchronized (lock2) {
                System.out.println(" end");
                while (state) {

                    while (Endlist.size() != mod || mod == 0) {
                        lock2.wait();
                    }

                    System.out.println();
                    System.out.println("awake end");
                    if (endlist.size() == 1) {
                        System.out.println("Done");
                        state = false;
                    } else {
                        String opponentOne = "";

                        int opOneMove = 0;
                        boolean opOne = true;
                        // boolean opOneGame = true;
                        int LocationOne = 0;

                        String opponentTwo = "";
                        int opTwoMove = 0;
                        boolean opTwo = true;
                        //boolean opTwoGame = true;
                        int LocationTwo = 0;
                        for (int i = 0; i < 2; i++) {
                            Count3();
                            if (count3 < endlist.size()) {
                                LocationOne = count3;
                                opponentOne = endlist.get(count3).getName();
                                opOneMove = endlist.get(count3).getMove();
                                opOne = endlist.get(count3).isInGame();

                            }
                            Count3();
                            if (count3 < Endlist.size()) {
                                LocationTwo = count3;
                                opponentTwo = endlist.get(count3).getName();
                                opTwoMove = endlist.get(count3).getMove();
                                opTwo = endlist.get(count3).isInGame();
                            }


                            if (!opponentOne.equals("") && !opponentTwo.equals("") && opOne == true && opTwo == true) {
                                System.out.println(opponentOne + " vs " + opponentTwo);
                                while (opOneMove == opTwoMove) {
                                    opOneMove = arr[r.nextInt(arr.length)];
                                    opTwoMove = arr[r.nextInt(arr.length)];

                                }
                                if (opOneMove == 1 && opOneMove == 3 || opOneMove == 3 && opOneMove == 2
                                        || opOneMove == 2 && opOneMove == 1) {
                                    System.out.println("                     " + opponentOne + " wins game ");
                                    opOne=true;
                                    opTwo = false;
                                } else {
                                    System.out.println("                     " + opponentTwo + " wins game ");
                                    opOne=false;
                                    opTwo = true;
                                }
                                if (opOne == true) {
                                    list2.add(new PlayerInfo(opponentOne,
                                            opOneMove, 0, opOne, true
                                    ));
                                }
                                if (opTwo == true) {
                                    list2.add(new PlayerInfo(opponentTwo,
                                            opTwoMove, 0, opTwo, true));
                                }
                            }
                        }
                    }
                    if((mod/2) == list2.size()){
                        System.out.println("Assleep");
                        mod =mod/2;
                        endlist.removeAll(endlist);
                        count2 =-1;
                        lock2.notifyAll();
                    }
                    // lock2.wait();
                }

            }
        }
    }


    public static void main(String[] args) {
        setSize = 10;
        int[] arr = {1, 2, 3};

        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
        ExecutorService service = Executors.newFixedThreadPool(coreCount);

        for (int i = 0; i < 2; i++) {
            service.execute(new Game2_part2.PlayerCreate(list, "Player", arr));
        }

        for (int i = 0; i < 2; i++) {
            service.execute(new Game2_part2.Tournament(list, Endlist, memorized, arr));
        }

        for (int i = 0; i < 2; i++) {
            service.execute(new Game2_part2.Cycle(Endlist, list,memorized, arr));

        }

        service.shutdown();

    }


}
