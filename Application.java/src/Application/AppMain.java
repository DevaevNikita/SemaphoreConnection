package Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AppMain {
    public static void main(String[] args) {
        Connection connection = Connection.getConnection();

        ExecutorService es = Executors.newFixedThreadPool(150); // Create ThreadPool
        for(int i = 0; i < 200; i++){
            es.submit(new Runnable() { //anonymous class
                @Override
                public void run() {
                    try {
                        connection.Work();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//class singleton
class Connection{
    private final static Connection connection = new Connection();
    private int ConnectionCount;
    private Semaphore semaphore = new Semaphore(10); // we to limit the number of connect

    private Connection(){

    }

    public static Connection getConnection(){
        return connection;
    }

    public void Work() throws InterruptedException {
        semaphore.acquire();
        try {
            doWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release(); // So that in case of exception we will return the connect
        }
    }

    private void doWork() throws InterruptedException { //imitation of work
        synchronized (this){
            ConnectionCount++;
            System.out.println(ConnectionCount);
        }
        Thread.sleep(5000);

        synchronized (this){
            ConnectionCount--;
        }
    }

}
