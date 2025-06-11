package ServerSide.service.tableService;

import ServerSide.dao.*;
import ServerSide.domain.*;
import ServerSide.protocol.OperationType;
import ServerSide.protocol.Request;
import ServerSide.protocol.Response;
import ServerSide.service.networkCommunicationService.RequestDispatcher;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


/*
处理用户注册登录登出相关业务
 */
public class RegistrationloginoutServices {
    private final borrowerDAO borrowerDAO = new borrowerDAO();
    private final staffDAO staffDAO = new staffDAO();

    public staff staffLogin(String staffNumber, String password) {
        String sql = "SELECT * FROM staff WHERE staff_number = ? AND password = ?";
        staff s = staffDAO.querySingle(sql, staff.class, staffNumber, password);

        if (s == null) {
            return null; // 账号或密码错误
        }

        if (!"工作".equals(s.getStatus())) {
            throw new IllegalStateException("该员工当前状态为：" + s.getStatus());
        }

        return s; // 状态为“工作”
    }

    public boolean userRegistration(borrower borrower) {
        // 1. 校验 borrower_number：12位数字
        String number = borrower.getBorrowerNumber();
        if (number == null || !number.matches("\\d{12}")) {
            System.out.println("借阅者编号不合法，必须是12位数字。");
            return false;
        }

        // 2. 校验 name
        String name = borrower.getName();
        if (name == null || name.trim().isEmpty() || name.length() > 20) {
            System.out.println("姓名不能为空，且长度不超过20个字符。");
            return false;
        }

        // 3. 校验 gender
        String gender = borrower.getGender();
        if (!"男".equals(gender) && !"女".equals(gender)) {
            System.out.println("性别必须是 '男' 或 '女'。");
            return false;
        }

        // 4. 校验 age
        Integer age = borrower.getAge();
        if (age != null && (age < 0 || age >= 99)) {
            System.out.println("年龄必须在 0 到 98 之间。");
            return false;
        }

        // 5. 校验 register_date
        if (borrower.getRegisterDate() == null) {
            System.out.println("注册日期不能为空。");
            return false;
        }
        // 1. 检查 borrower_number 是否已存在
        String checkSql = "SELECT * FROM borrowers WHERE borrower_number = ?";
        borrower existing = borrowerDAO.querySingle(checkSql, borrower.class, borrower.getBorrowerNumber());
        if (existing != null) {
            // 编号已存在，注册失败
            return false;
        }

        // 2. 插入新用户，设置默认状态为 “正常”
        String insertSql = "INSERT INTO borrowers (borrower_number, name, gender, age, register_date, password, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = borrowerDAO.insert(insertSql,
                borrower.getBorrowerNumber(),
                borrower.getName(),
                borrower.getGender(),
                borrower.getAge(),
                borrower.getRegisterDate(),
                borrower.getPassword(),
                "正常"  // 默认注册成功状态
        );

        return result > 0;
    }
    public borrower borrowerLogin(String number, String password) {
        String sql = "SELECT * FROM borrowers WHERE borrower_number = ? AND password = ?";
        borrower b = borrowerDAO.querySingle(sql, borrower.class, number, password);

        if (b == null) {
            return null; // 账号或密码错误
        }

        if (!"正常".equals(b.getStatus())) {
            // 不返回 null，而是带上错误状态
            throw new IllegalStateException("当前账户状态为：" + b.getStatus());
        }

        return b; // 合法且状态正常
    }


//=================================================================

//    private RegistrationloginoutServices service;
//
//    @Before
//    public void setUp() {
//        service = new RegistrationloginoutServices();
//    }
//
//    @Test
//    public void testValidRegistration() {
//        borrower borrower = new borrower();
//        borrower.setBorrowerNumber("202500000106"); // 每次测试换一个唯一编号
//        borrower.setName("测试用户5");
//        borrower.setGender("男");
//        borrower.setAge(25);
//        borrower.setPassword("20040317");
//        borrower.setRegisterDate(Date.valueOf(LocalDate.now()));
//
//        boolean result = service.userRegistration(borrower);
//        assertTrue("合法用户注册应成功", result);
//    }
//    @Test
//    public void testStaffLogin() {
//        Request req = new Request(OperationType.STAFF_LOGIN, List.of("202500000111", "123456"));
//        Response res = RequestDispatcher.dispatch(req);
//        System.out.println("登录是否成功：" + res.isSuccess());
//
//
//    }
//    @Test
//    public void testDuplicateBorrowerNumber() {
//        // 再次用相同编号测试重复注册
//        borrower borrower = new borrower();
//        borrower.setBorrowerNumber("202500000101"); // 与上面重复
//        borrower.setName("重复用户");
//        borrower.setGender("女");
//        borrower.setAge(30);
//        borrower.setRegisterDate(Date.valueOf(LocalDate.now()));
//
//        boolean result = service.userRegistration(borrower);
//        assertFalse("重复编号注册应失败", result);
//    }
//
//    @Test
//    public void testInvalidBorrowerNumber() {
//        borrower borrower = new borrower();
//        borrower.setBorrowerNumber("abc123"); // 非法编号
//        borrower.setName("非法编号用户");
//        borrower.setGender("女");
//        borrower.setAge(20);
//        borrower.setRegisterDate(Date.valueOf(LocalDate.now()));
//
//        boolean result = service.userRegistration(borrower);
//        assertFalse("非法 borrower_number 应注册失败", result);
//    }
//
//    @Test
//    public void testInvalidGender() {
//        borrower borrower = new borrower();
//        borrower.setBorrowerNumber("202500000102");
//        borrower.setName("性别错误用户");
//        borrower.setGender("未知"); // 非法性别
//        borrower.setAge(20);
//        borrower.setRegisterDate(Date.valueOf(LocalDate.now()));
//
//        boolean result = service.userRegistration(borrower);
//        assertFalse("非法性别应注册失败", result);
//    }
//
//    @Test
//    public void testInvalidAge() {
//        borrower borrower = new borrower();
//        borrower.setBorrowerNumber("202500000103");
//        borrower.setName("年龄超限用户");
//        borrower.setGender("男");
//        borrower.setAge(150); // 非法年龄
//        borrower.setRegisterDate(Date.valueOf(LocalDate.now()));
//
//        boolean result = service.userRegistration(borrower);
//        assertFalse("非法年龄应注册失败", result);
//    }
//
//    @Test
//    public void testLogin() throws Exception {
//        List<String> payload = List.of("202500000001", "123456");
//        Request request = new Request(OperationType.BORROWER_LOGIN, payload);
//
//        Socket socket = new Socket("localhost", 9999);
//        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        writer.println(new Gson().toJson(request));
//        String responseJson = reader.readLine();
//        System.out.println("📥 返回: " + responseJson);
//        socket.close();
//    }
//
}
