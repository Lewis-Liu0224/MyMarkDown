import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author:Lius
 * @Date: 2020/7/14 8:47
 */
public class Main {

    public static final int CORE_POOL_SIZE = 3;
    public static final int MAXIMUM_POOL_SIZE = 3;
    public static final int KEEP_ALIVE_TIME = 5;
    public static final int CAPACITY = 5000;

    public static void main(String[] args) throws IOException {
        ServerSocket socketServre = new ServerSocket(6666);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());


        while (true){
            Socket accept = socketServre.accept();
            poolExecutor.submit(new SocketHandler(accept));
        }
    }
}
