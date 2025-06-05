package EmployeeSide;

import ServerSide.protocol.OperationType;
import ServerSide.protocol.Request;
import ServerSide.protocol.Response;
import ServerSide.domain.book;
import ServerSide.domain.bookVersion;
import ServerSide.domain.staff;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class JsonSocketTest {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final Gson gson = new Gson();

    private Response sendRequest(Request req) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String json = gson.toJson(req);
            writer.write(json + "\n");
            writer.flush();

            String responseJson = reader.readLine();
            return gson.fromJson(responseJson, Response.class);
        }
    }

    // 1. 查询图书列表
    @Test
    public void testListBooks() throws IOException {
        Request req = new Request(OperationType.LIST_BOOKS, null);
        Response resp = sendRequest(req);
        System.out.println("✅ 图书列表查询: " + resp.isSuccess() + " | " + resp.getMessage());
    }

    // 2. 员工代借书
    @Test
    public void testBorrowBook() throws IOException {
        String[] args = {"202512345666", "EMP-BOOK-2025"};
        Request req = new Request(OperationType.BORROW, args);
        Response resp = sendRequest(req);
        System.out.println("📚 代借图书: " + resp.isSuccess() + " | " + resp.getMessage());
    }

    // 3. 添加图书和版本
    @Test
    public void testAddBookWithVersion() throws IOException {
        book b = new book(null, "9999990009999", "员工添加测试书", "Test Title");
        bookVersion v = new bookVersion(null, null, "EMP-BOOK-2025", "测试作者",
                "第1版", new Date(System.currentTimeMillis()), "中文", 320,
                new BigDecimal("59.90"), 6);

        Object[] payload = {b, v};
        Request req = new Request(OperationType.ADD_BOOK, payload);
        Response resp = sendRequest(req);
        System.out.println("📘 添加图书: " + resp.isSuccess() + " | " + resp.getMessage());
    }

    // 4. 添加员工
    @Test
    public void testAddStaff() throws IOException {
        staff s = new staff(null, "202500000111", "测试员工4", "男", 28,
                new Date(System.currentTimeMillis()), "员工", new BigDecimal("5000"), "工作");

        Request req = new Request(OperationType.ADD_STAFF, s);
        Response resp = sendRequest(req);
        System.out.println("👤 添加员工: " + resp.isSuccess() + " | " + resp.getMessage());
    }

    // 5. 设置借阅者状态
    @Test
    public void testSetBorrowerStatus() throws IOException {
        String[] args = {"230019000019", "正常"};
        Request req = new Request(OperationType.SET_BORROWER_STATUS, args);
        Response resp = sendRequest(req);
        System.out.println("🔄 设置借阅者状态: " + resp.isSuccess() + " | " + resp.getMessage());
    }
}
