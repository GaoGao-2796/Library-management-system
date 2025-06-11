package ServerSide.service.networkCommunicationService;

import ServerSide.domain.*;
import ServerSide.protocol.*;
import ServerSide.service.tableService.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class RequestDispatcher {

    private static final RegistrationloginoutServices registrationService = new RegistrationloginoutServices();
    private static final PersonnelManagementServices personnelService = new PersonnelManagementServices();
    private static final BorrowingReturningService borrowService = new BorrowingReturningService();
    private static final BookManagementService bookService = new BookManagementService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.sql.Date.class, new TypeAdapter<java.sql.Date>() {
                private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public void write(JsonWriter out, java.sql.Date value) throws IOException {
                    out.value(sdf.format(value));
                }

                @Override
                public java.sql.Date read(JsonReader in) throws IOException {
                    try {
                        String dateStr = in.nextString();
                        java.util.Date utilDate = sdf.parse(dateStr);
                        return new java.sql.Date(utilDate.getTime());
                    } catch (Exception e) {
                        throw new IOException("无法解析日期: " + in, e);
                    }
                }
            })
            .create();

    public static Response dispatch(Request request) {
        try {
            OperationType type = request.getOperationType();
            Object payload = request.getPayload();

            switch (type) {
                case REGISTER -> {
                    borrower b = gson.fromJson(gson.toJson(payload), borrower.class);
                    boolean result = registrationService.userRegistration(b);
                    return new Response(result, result ? "注册成功" : "注册失败", null);
                }
                case BORROWER_LOGIN -> {
                    List<String> loginParams = gson.fromJson(gson.toJson(payload), new TypeToken<List<String>>(){}.getType());
                    String number = loginParams.get(0);
                    String password = loginParams.get(1);

                    try {
                        borrower user = registrationService.borrowerLogin(number, password);
                        if (user != null) {
                            return new Response(true, "登录成功", user);
                        } else {
                            return new Response(false, "账号或密码错误", null);
                        }
                    } catch (IllegalStateException e) {
                        return new Response(false, e.getMessage(), null); // 返回状态提示
                    }
                }

                case STAFF_LOGIN -> {
                    List<String> args = gson.fromJson(gson.toJson(payload), new TypeToken<List<String>>(){}.getType());
                    String number = args.get(0);
                    String pwd = args.get(1);

                    try {
                        staff s = registrationService.staffLogin(number, pwd);
                        boolean success = s != null;
                        return new Response(success, success ? "登录成功" : "账号或密码错误", s);
                    } catch (IllegalStateException e) {
                        return new Response(false, e.getMessage(), null); // 提示“该员工当前状态为：离职/停职...”
                    }
                }

                case BORROW -> {
                    JsonObject obj = gson.toJsonTree(payload).getAsJsonObject();
                    String borrowerNumber = obj.get("borrowerNumber").getAsString();
                    String bookCode = obj.get("bookCode").getAsString();

                    boolean result = borrowService.borrowBook(borrowerNumber, bookCode);
                    return new Response(result, result ? "借书成功" : "借书失败", null);
                }
                case RETURN -> {
                    // 1. 将 payload 转为 JSON 字符串
                    String json = gson.toJson(payload);

                    // 2. 转为 Map 获取 transactionNumber
                    Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());

                    // 3. 提取并构造 BigInteger
                    BigInteger transactionNumber = new BigInteger(map.get("transactionNumber"));

                    // 4. 执行归还逻辑
                    boolean result = borrowService.returnBook(transactionNumber);

                    // 5. 返回结果
                    return new Response(result, result ? "还书成功" : "还书失败", null);
                }
                case LIST_BOOKS -> {
                    List<bookVersion> list = bookService.getAllBooksWithVersions();
                    return new Response(true, "查询成功", list);
                }
                case ADD_BOOK -> {
                    // payload 是一个 List<LinkedTreeMap>
                    List<?> objList = (List<?>) payload;

                    // 第一个对象是 book
                    Map<String, Object> bookMap = (Map<String, Object>) objList.get(0);
                    book b = new book();
                    b.setIsbn((String) bookMap.get("isbn"));
                    b.setTitle((String) bookMap.get("title"));

                    // 第二个对象是 bookVersion
                    Map<String, Object> versionMap = (Map<String, Object>) objList.get(1);
                    bookVersion v = new bookVersion();
                    v.setBookCode((String) versionMap.get("bookCode"));
                    v.setAuthors((String) versionMap.get("authors"));
                    v.setEdition((String) versionMap.get("edition"));

                    // 👇 日期解析手动处理，必须是 yyyy-MM-dd 格式
                    try {
                        String dateStr = (String) versionMap.get("publishDate");
                        java.sql.Date publishDate = java.sql.Date.valueOf(dateStr); // throws IllegalArgumentException if format invalid
                        v.setPublishDate(publishDate);
                    } catch (Exception e) {
                        return new Response(false, "日期格式错误，应为 yyyy-MM-dd", null);
                    }

                    v.setLanguage((String) versionMap.get("language"));
                    v.setPageCount(((Number) versionMap.get("pageCount")).intValue());
                    v.setPrice(new BigDecimal(versionMap.get("price").toString()));
                    v.setStockQuantity(((Number) versionMap.get("stockQuantity")).intValue());

                    boolean result = bookService.addBookWithVersion(b, v);
                    return new Response(result, result ? "添加成功" : "添加失败", null);
                }


                case DELETE_BOOK_VERSION -> {
                    String code = (String) payload;
                    boolean result = bookService.deleteBookVersion(code);
                    return new Response(result, result ? "删除成功" : "删除失败", null);
                }
                case UPDATE_BOOK_VERSION -> {
                    Map<String, Object> map = (Map<String, Object>) payload;
                    bookVersion v = new bookVersion();

                    v.setBookCode((String) map.get("bookCode"));
                    v.setAuthors((String) map.get("authors"));
                    v.setEdition((String) map.get("edition"));

                    try {
                        String dateStr = (String) map.get("publishDate");
                        java.sql.Date publishDate = java.sql.Date.valueOf(dateStr); // 格式必须为 yyyy-MM-dd
                        v.setPublishDate(publishDate);
                    } catch (Exception e) {
                        return new Response(false, "日期格式错误，应为 yyyy-MM-dd", null);
                    }

                    v.setLanguage((String) map.get("language"));
                    v.setPageCount(((Number) map.get("pageCount")).intValue());
                    v.setPrice(new BigDecimal(map.get("price").toString()));
                    v.setStockQuantity(((Number) map.get("stockQuantity")).intValue());

                    boolean result = bookService.updateBookVersion(v);
                    return new Response(result, result ? "修改成功" : "修改失败", null);
                }
                case ADD_STAFF -> {
                    Map<String, Object> map = (Map<String, Object>) payload;
                    staff s = new staff();

                    s.setStaffNumber((String) map.get("staffNumber"));
                    s.setName((String) map.get("name"));
                    s.setGender((String) map.get("gender"));
                    s.setAge(((Number) map.get("age")).intValue());

                    try {
                        s.setJoinDate(java.sql.Date.valueOf((String) map.get("joinDate")));
                    } catch (Exception e) {
                        return new Response(false, "日期格式错误，应为 yyyy-MM-dd", null);
                    }

                    s.setPosition((String) map.get("position"));
                    s.setSalary(new BigDecimal(map.get("salary").toString()));
                    s.setStatus((String) map.get("status"));

                    // 可选字段：版本号和密码
                    if (map.containsKey("version")) {
                        s.setVersion(((Number) map.get("version")).intValue());
                    }
                    if (map.containsKey("password")) {
                        s.setPassword((String) map.get("password"));
                    }

                    boolean result = personnelService.addStaff(s);
                    return new Response(result, result ? "添加员工成功" : "添加失败", null);
                }

                case RESIGN_STAFF -> {
                    String number = (String) payload;
                    boolean result = personnelService.logicResignStaff(number);
                    return new Response(result, result ? "离职成功" : "离职失败", null);
                }
                case UPDATE_STAFF -> {
                    Map<String, Object> map = (Map<String, Object>) payload;
                    staff s = new staff();

                    s.setStaffNumber((String) map.get("staffNumber"));
                    s.setName((String) map.get("name"));
                    s.setGender((String) map.get("gender"));
                    s.setAge(((Number) map.get("age")).intValue());
                    s.setPosition((String) map.get("position"));
                    s.setSalary(new BigDecimal(map.get("salary").toString()));
                    s.setStatus((String) map.get("status"));

                    if (map.containsKey("version")) {
                        s.setVersion(((Number) map.get("version")).intValue());
                    }
                    if (map.containsKey("password")) {
                        s.setPassword((String) map.get("password"));
                    }

                    boolean result = personnelService.updateStaff(s);
                    return new Response(result, result ? "修改成功" : "修改失败", null);
                }

                case LIST_STAFF -> {
                    List<staff> list = personnelService.listAllStaff();
                    return new Response(true, "查询成功", list);
                }
                case SET_BORROWER_STATUS -> {
                    String[] p = (String[]) payload;
                    boolean result = personnelService.setBorrowerStatus(p[0], p[1]);
                    return new Response(result, result ? "设置成功" : "设置失败", null);
                }
                case VIEW_BORROW_RECORDS -> {
                    // Step 1: 将 payload 转成 Map
                    Map<String, Object> payloadMap = gson.fromJson(gson.toJson(payload), new TypeToken<Map<String, Object>>(){}.getType());

                    // Step 2: 提取 borrowerNumber（注意转成 String）
                    String borrowerNumber = String.valueOf(payloadMap.get("borrowerNumber"));

                    // Step 3: 查询记录
                    List<borrowRecord> records = borrowService.getBorrowRecordsByBorrowerNumber(borrowerNumber);

                    return new Response(true, "查询成功", records);
                }
                default -> {
                    return new Response(false, "不支持的操作类型", null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "服务端异常: " + e.getMessage(), null);
        }
    }
}
