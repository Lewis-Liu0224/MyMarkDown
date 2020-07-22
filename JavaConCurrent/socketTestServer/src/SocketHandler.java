import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @Author:Lius
 * @Date: 2020/7/14 9:44
 */
public class   SocketHandler implements Runnable {

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                int len;
                byte[] buffer = new byte[1024];
                InputStream inputStream = socket.getInputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    System.out.println(new String(buffer, 0, len));
                }
                socket.shutdownInput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
