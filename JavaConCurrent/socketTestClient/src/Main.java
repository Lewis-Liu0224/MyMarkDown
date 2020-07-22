import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:Lius
 * @Date: 2020/7/14 8:47
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {


        ExecutorService executorService = Executors.newFixedThreadPool(5);

        AtomicInteger  countNumer = new AtomicInteger(0);

        final long startTime = System.currentTimeMillis();

        for (int i = 0; i < 5000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket("127.0.0.1", 6666);
                        countNumer.getAndIncrement();
                        socket.getOutputStream().write(("这是第"+countNumer.get()+"号线程，距离开始已有"+((System.currentTimeMillis()-startTime)/1000.0)+"秒").getBytes());
                        socket.shutdownOutput();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
//            Thread.sleep(200);
        }
    }
}
