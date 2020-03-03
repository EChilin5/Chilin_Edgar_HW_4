import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutor {

    private static class Task implements Runnable{

        @Override
        public void run() {
            System.out.println("thread name " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args){
       // int coreCount = Runtime.getRuntime().availableProcessors(); // count of cores computer has gets
      //  ExecutorService service = Executors.newFixedThreadPool(10);

      //  for(int i =0; i< 10; i++){
     //       service.execute(new Task());
     //   }
     //   System.out.println("thread name " + Thread.currentThread().getName());
     //   service.shutdown();


        double  x = 0.08;
        double n =0;
        double up =1;
        double down = 1;
        double result = 0;

        double decrement = 0;
        for(int i =0; i < 31; i++){
            for(int j = 0; j < n; j++){
                x = x*x;
             //   System.out.println( " er " +x);
            }
            down = down + x;
            System.out.println(down);
        }

    }

}
