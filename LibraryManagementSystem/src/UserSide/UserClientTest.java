package UserSide;

import ServerSide.domain.borrower;
import ServerSide.protocol.OperationType;
import ServerSide.protocol.Request;
import ServerSide.protocol.Response;

import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.math.BigInteger;
import java.net.Socket;
import java.sql.Date;

public class UserClientTest {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    @Test
    public void testUserRegistration() {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            borrower b = new borrower();
            b.setBorrowerNumber("202500000002");
            b.setName("测试用户2");
            b.setGender("女");
            b.setAge(22);
            b.setRegisterDate(new Date(System.currentTimeMillis()));
            b.setStatus("正常");

            Request req = new Request(OperationType.REGISTER, b);
            oos.writeObject(req);
            oos.flush();

            Response resp = (Response) ois.readObject();
            System.out.println("✅ 注册是否成功: " + resp.isSuccess());
            System.out.println("📦 消息: " + resp.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "客户端异常: " + e.getMessage();
        }
    }

    @Test
    public void testBorrowBook() {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            String[] args = {"202500000002", "TEST-001"};
            Request req = new Request(OperationType.BORROW, args);
            oos.writeObject(req);
            oos.flush();

            Response resp = (Response) ois.readObject();
            System.out.println("✅ 借书是否成功: " + resp.isSuccess());
            System.out.println("📦 消息: " + resp.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "客户端异常: " + e.getMessage();
        }
    }

    @Test
    public void testReturnBook() {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            BigInteger transactionNumber = new BigInteger("1748432205789"); // 替换为有效编号
            Request req = new Request(OperationType.RETURN, transactionNumber);
            oos.writeObject(req);
            oos.flush();

            Response resp = (Response) ois.readObject();
            System.out.println("✅ 还书是否成功: " + resp.isSuccess());
            System.out.println("📦 消息: " + resp.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "客户端异常: " + e.getMessage();
        }
    }

    @Test
    public void testListAllBooks() {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            Request req = new Request(OperationType.LIST_BOOKS, null);
            oos.writeObject(req);
            oos.flush();

            Response resp = (Response) ois.readObject();
            System.out.println("✅ 查询图书是否成功: " + resp.isSuccess());
            System.out.println("📦 消息: " + resp.getMessage());

            if (resp.getData() instanceof java.util.List<?>) {
                for (Object obj : (java.util.List<?>) resp.getData()) {
                    System.out.println(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "客户端异常: " + e.getMessage();
        }
    }
}

