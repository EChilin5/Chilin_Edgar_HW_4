import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Game1_part2 {

    private static List<PlayerInfo> list = Collections.synchronizedList(new ArrayList<>());
    private static List<PlayerInfo> Endlist = Collections.synchronizedList(new ArrayList<>());
    private static Hashtable<String, Integer>  memorized = new Hashtable<String, Integer>();

    private static int count = 0;
    private static int count2 = -1;
    private static int setSize =0;
    private static int newSize;

    private static  final Object lock2 = new Object();
    private static final Object lock3 = new Object();
    private static boolean start = false;
    private static Random r = new Random();

    //private static PlayerInfo[] battle = new PlayerInfo[20];


    private static class PlayerCreate implements Runnable {
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
                for (int i = 0; i < 5; i++) {
                    Count();
                    int move = arr[r.nextInt(arr.length)];
                    int num = count;
                    String id = name + " " + num;

                    list2.add(new PlayerInfo(id, move, score, inGame, win));
                    System.out.println("Thread create player " + id);

                    if (list2.size() == setSize) {
                        System.out.println("max");
                        start = true;
                        awake();
                    }
                }

            }

        }
    }

    ///


    public static class PlayerValidation implements Runnable {
        private String name;
        BlockingQueue<PlayerInfo> player;
        private int validate;
        private List<PlayerInfo> list4;
        private List<PlayerInfo> Enlist;
        private Hashtable<String, Integer>  memo;
        Object lock1 = new Object();
        int[] arr;
        static ReentrantLock counterLock2 = new ReentrantLock(true); // enable fairness policy


        private PlayerValidation(String name, List<PlayerInfo> list4,
                                 List<PlayerInfo> Enlist,
                                 Hashtable<String, Integer>  memo,
                                 int[] arr, int validate
                                 //PlayerInfo[] battle
        ) {
            this.name = name;
            this.arr = arr;
            this.player = player;
            this.memo = memo;
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
                Match2();
            } catch (Exception e) {

            }
        }


        private void Match2() throws InterruptedException {
            synchronized (lock3) {
                System.out.println("Asleep");
               lock3.wait();
               System.out.println("Awake");
              //  while (true) {
                    while (!list4.isEmpty()) {
                        String pname = "";
                        int score = 0;
                        int move = 0;
                        boolean inGame = false;

                        Count2();
                        try {
                            if(count2 < list4.size()) {
                                pname = list4.get(count2).getName();
                                score = list4.get(count2).getScore();
                                move = list4.get(count2).getMove();
                                inGame = list4.get(count2).isInGame();
                            }
                        } catch (Exception e) {
                            System.out.println(e.getCause());
                            start = false;

                        }
                        if(start == false){
                            break;
                        }

                        if (inGame == true) {
                            for (int j = 0; j < list4.size(); j++) {
                                int opponentMove = list4.get(j).getMove();
                                String combo = ""+move + opponentMove;

                                if(memo.containsKey(combo)){
                                    System.out.println("true");
                                    Integer val = memo.get(combo);
                                    if(val == 1){
                                        score++;
                                    }else if(val == 0){
                                        score = score;
                                    }else{
                                        score --;
                                    }

                                }else {
                                    if (move == opponentMove) {
                                        memo.put(combo, 0);

                                    } else if (move == 1 && opponentMove == 3) {
                                        score++;
                                        memo.put(combo, 1);
                                    } else if (move == 2 && opponentMove == 1) {
                                        score++;
                                        memo.put(combo, 1);

                                    } else if (move == 3 && opponentMove == 2) {
                                        score++;
                                        memo.put(combo, 1);

                                    } else {
                                        score--;
                                    }
                                }
                            }
                            System.out.println(count2);
                            try {
                                //list4.remove(0);
                                // Enlist.add(new PlayerInfo(pname, move, score, true, true));
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
                                System.out.println("values are equivalent ");
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

                    }
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
                Winner();
            } catch (Exception e) {
            }
        }

        private void Winner() throws InterruptedException {
            synchronized (lock3) {
                System.out.println("hello");
                lock3.wait();
                System.out.println("assleep again");
                lock3.wait();
                int Max = 0;
                String name = " ";

                for (PlayerInfo i : list4) {
                    System.out.println(i.getName() + " score " + i.getScore() + " move: " + i.getMove());
                }

                System.out.println();
                for (PlayerInfo i : list4) {
                    if (i.getScore() > Max) {
                        Max = i.getScore();
                        name = i.getName();
                    }
                }
                System.out.println("winner" + name + " score " + Max );
            }
        }
    }


    public static void main(String[] args) {
        setSize = 20;

        newSize = setSize-1;
        Random r = new Random();
        int[] arr = {1, 2, 3};

        int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
        ExecutorService service = Executors.newFixedThreadPool(coreCount);
        service.execute(new PlayerWinner(Endlist));


     //   }

        for (int i = 0; i < 4; i++) {
            service.execute(new PlayerCreate(list, "Player", arr));
        }
        for (int i = 0; i < 2; i++) {
            service.execute(new PlayerValidation("match" , list, Endlist, memorized, arr, r.nextInt(20)));

        }


        service.shutdown();

    }


}
