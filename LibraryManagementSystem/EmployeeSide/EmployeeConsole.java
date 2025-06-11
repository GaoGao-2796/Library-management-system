package EmployeeSide;

import ServerSide.domain.book;
import ServerSide.domain.bookVersion;
import ServerSide.domain.staff;
import ServerSide.protocol.OperationType;
import ServerSide.protocol.Request;
import ServerSide.protocol.Response;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EmployeeConsole {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;


    private static staff currentStaff;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BigInteger.class, new JsonDeserializer<BigInteger>() {
                @Override
                public BigInteger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    try {
                        // 如果是数字，先用 BigDecimal 再转为 BigInteger（避免精度丢失）
                        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                            return new BigDecimal(json.getAsString()).toBigInteger();
                        }
                        // 如果是字符串，尝试直接转 BigInteger
                        return new BigInteger(json.getAsString());
                    } catch (Exception e) {
                        throw new JsonParseException("无法解析 BigInteger：" + json.toString(), e);
                    }
                }
            })
            .registerTypeAdapter(BigInteger.class, new JsonSerializer<BigInteger>() {
                @Override
                public JsonElement serialize(BigInteger src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .setDateFormat("yyyy-MM-dd")
            .create();
    public static void main(String[] args) {
        login();
    }

    private static void login() {
        while (true) {
            try {
                System.out.print("员工编号（输入 0 退出）：");
                String number = scanner.nextLine().trim();
                if ("0".equals(number)) {
                    System.out.println("👋 已退出登录流程");
                    return;
                }

                System.out.print("密码：");
                String password = scanner.nextLine().trim();

                List<String> payload = List.of(number, password);
                Response r = sendRequest(new Request(OperationType.STAFF_LOGIN, payload));

                if (r != null && r.isSuccess()) {
                    currentStaff = gson.fromJson(gson.toJson(r.getData()), staff.class);
                    System.out.println("🎉 登录成功，欢迎 " + currentStaff.getName());
                    menu();  // 进入菜单
                    return;  // 退出登录循环
                } else {
                    System.out.println("❌ 登录失败: " + (r != null ? r.getMessage() : "未知错误，请重试"));
                }

            } catch (Exception e) {
                System.out.println("❌ 登录过程中发生错误: " + e.getMessage());
            }
        }
    }


    private static void menu() {
        while (true) {
            System.out.println("\n========= 员工菜单 =========");
            System.out.println("1. 查看图书");
            System.out.println("2. 帮助借阅者借书");
            System.out.println("3. 帮助借阅者还书");
            System.out.println("4. 添加图书版本");
            System.out.println("5. 修改图书版本");
            System.out.println("6. 删除图书版本");
            if ("管理员".equals(currentStaff.getPosition())) {
                System.out.println("7. 添加员工");
                System.out.println("8. 更新员工");
                System.out.println("9. 删除员工（逻辑离职）");
                System.out.println("10. 查看所有员工");
            }
            System.out.println("0. 退出系统");
            System.out.print("请选择操作：");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> listBooks();
                    case 2 -> borrowForUser();
                    case 3 -> returnForUser();
                    case 4 -> addBookVersion();
                    case 5 -> updateBookVersion();
                    case 6 -> deleteBookVersion();
                    case 7 -> {
                        if (isAdmin()) addStaff();
                        else deny();
                    }
                    case 8 -> {
                        if (isAdmin()) updateStaff();
                        else deny();
                    }
                    case 9 -> {
                        if (isAdmin()) resignStaff();
                        else deny();
                    }
                    case 10 -> {
                        if (isAdmin()) listStaff();
                        else deny();
                    }
                    case 0 -> {
                        System.out.println("👋 再见！");
                        return;
                    }
                    default -> System.out.println("❌ 无效选项");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ 输入必须是数字！");
            } catch (Exception e) {
                System.out.println("❌ 操作失败：" + e.getMessage());
            }
        }
    }

    private static boolean isAdmin() {
        return "管理员".equals(currentStaff.getPosition());
    }

    private static void deny() {
        System.out.println("⚠️ 权限不足，仅管理员可执行此操作！");
    }

    private static Response sendRequest(Request req) {
        try (
                Socket socket = new Socket(HOST, PORT);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream()
        ) {
            out.write(gson.toJson(req).getBytes());
            out.flush();

            byte[] buffer = new byte[8192];
            int len = in.read(buffer);
            if (len != -1) {
                String json = new String(buffer, 0, len);
                return gson.fromJson(json, Response.class);
            }
        } catch (Exception e) {
            System.out.println("❌ 通信失败: " + e.getMessage());
        }
        return null;
    }

    // 员工功能实现
    private static void listBooks() {
        Response r = sendRequest(new Request(OperationType.LIST_BOOKS, null));
        System.out.println("📚 图书列表：");
        System.out.println(gson.toJson(r.getData()));
    }

    private static void borrowForUser() {
        System.out.print("借阅者编号：");
        String number = scanner.nextLine();
        System.out.print("图书编号：");
        String code = scanner.nextLine();

        Response r = sendRequest(new Request(OperationType.BORROW, List.of(number, code)));
        System.out.println(r.getMessage());
    }

    private static void returnForUser() {
        System.out.print("请输入借阅流水号：");
        String transactionNumber = scanner.nextLine();

        Response r = sendRequest(new Request(OperationType.RETURN, transactionNumber));
        System.out.println(r.getMessage());
    }

    private static void addBookVersion() {
        System.out.print("ISBN：");
        String isbn = scanner.nextLine();
        System.out.print("书名：");
        String title = scanner.nextLine();

        book book = new book(null, isbn, title, null);

        System.out.print("图书编号（唯一）: ");
        String code = scanner.nextLine();
        System.out.print("作者：");
        String authors = scanner.nextLine();
        System.out.print("版本说明：");
        String edition = scanner.nextLine();
        System.out.print("出版日期（yyyy-MM-dd）：");
        String date = scanner.nextLine();
        System.out.print("语言：");
        String lang = scanner.nextLine();
        System.out.print("页数：");
        int pages = Integer.parseInt(scanner.nextLine());
        System.out.print("价格：");
        BigDecimal price = new BigDecimal(scanner.nextLine());
        System.out.print("库存数量：");
        int qty = Integer.parseInt(scanner.nextLine());

        bookVersion version = new bookVersion(null, null, code, authors, edition,
                Date.valueOf(date), lang, pages, price, qty);

        Response r = sendRequest(new Request(OperationType.ADD_BOOK, List.of(book, version)));
        System.out.println(r.getMessage());
    }

    private static void updateBookVersion() {
        System.out.print("图书编号：");
        String code = scanner.nextLine();

        bookVersion version = new bookVersion();
        version.setBookCode(code);
        System.out.print("新作者：");
        version.setAuthors(scanner.nextLine());
        System.out.print("新版本说明：");
        version.setEdition(scanner.nextLine());
        System.out.print("出版日期（yyyy-MM-dd）：");
        version.setPublishDate(Date.valueOf(scanner.nextLine()));
        System.out.print("语言：");
        version.setLanguage(scanner.nextLine());
        System.out.print("页数：");
        version.setPageCount(Integer.parseInt(scanner.nextLine()));
        System.out.print("价格：");
        version.setPrice(new BigDecimal(scanner.nextLine()));
        System.out.print("库存数量：");
        version.setStockQuantity(Integer.parseInt(scanner.nextLine()));

        Response r = sendRequest(new Request(OperationType.UPDATE_BOOK_VERSION, version));
        System.out.println(r.getMessage());
    }

    private static void deleteBookVersion() {
        System.out.print("图书编号：");
        String code = scanner.nextLine();

        Response r = sendRequest(new Request(OperationType.DELETE_BOOK_VERSION, code));
        System.out.println(r.getMessage());
    }

    // 管理员功能实现
    private static void addStaff() {
        staff s = new staff();
        System.out.print("员工编号：");
        s.setStaffNumber(scanner.nextLine());
        System.out.print("姓名：");
        s.setName(scanner.nextLine());
        System.out.print("性别：");
        s.setGender(scanner.nextLine());
        System.out.print("年龄：");
        s.setAge(Integer.parseInt(scanner.nextLine()));
        s.setJoinDate(Date.valueOf(LocalDate.now()));
        System.out.print("职位（普通/管理员）：");
        s.setPosition(scanner.nextLine());
        System.out.print("工资：");
        s.setSalary(new BigDecimal(scanner.nextLine()));
        s.setStatus("工作");
        System.out.print("密码：");
        s.setPassword(scanner.nextLine());

        Response r = sendRequest(new Request(OperationType.ADD_STAFF, s));
        System.out.println(r.getMessage());
    }

    private static void updateStaff() {
        staff s = new staff();
        System.out.print("员工编号：");
        s.setStaffNumber(scanner.nextLine());
        System.out.print("新姓名：");
        s.setName(scanner.nextLine());
        System.out.print("新性别：");
        s.setGender(scanner.nextLine());
        System.out.print("新年龄：");
        s.setAge(Integer.parseInt(scanner.nextLine()));
        System.out.print("职位（普通/管理员）：");
        s.setPosition(scanner.nextLine());
        System.out.print("新工资：");
        s.setSalary(new BigDecimal(scanner.nextLine()));
        System.out.print("状态（工作/离职）：");
        s.setStatus(scanner.nextLine());
        System.out.print("新密码：");
        s.setPassword(scanner.nextLine());

        Response r = sendRequest(new Request(OperationType.UPDATE_STAFF, s));
        System.out.println(r.getMessage());
    }

    private static void resignStaff() {
        System.out.print("离职员工编号：");
        String number = scanner.nextLine();

        Response r = sendRequest(new Request(OperationType.RESIGN_STAFF, number));
        System.out.println(r.getMessage());
    }

    private static void listStaff() {
        Response r = sendRequest(new Request(OperationType.LIST_STAFF, null));
        System.out.println("📋 员工列表：");
        System.out.println(gson.toJson(r.getData()));
    }
}
