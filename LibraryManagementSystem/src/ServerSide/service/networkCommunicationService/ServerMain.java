package ServerSide.service.networkCommunicationService;


import ServerSide.protocol.*;




import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端主程序：监听端口，处理客户端请求
 */
public class ServerMain {

    private static final int PORT = 9999;
    private static final int THREAD_POOL_SIZE = 20;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ 服务端启动成功，端口：" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每个客户端连接的处理线程
     */
    static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())
            ) {
                // 读取客户端请求对象
                Request request = (Request) ois.readObject();
                System.out.println("🔁 收到请求: " + request.getOperationType());

                // 交给分发器处理业务逻辑
                Response response = RequestDispatcher.dispatch(request);

                // 返回处理结果给客户端
                oos.writeObject(response);
                oos.flush();

            } catch (Exception e) {
                System.err.println("❌ 客户端连接异常: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (Exception ignore) {}
            }
        }
    }
}

