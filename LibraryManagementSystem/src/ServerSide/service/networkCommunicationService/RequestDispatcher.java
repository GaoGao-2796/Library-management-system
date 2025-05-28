package ServerSide.service.networkCommunicationService;



import ServerSide.protocol.*;

import ServerSide.domain.*;
import ServerSide.service.tableService.*;

import java.math.BigInteger;
import java.util.List;

public class RequestDispatcher {

    private static final RegistrationloginoutServices registrationService = new RegistrationloginoutServices();
    private static final PersonnelManagementServices personnelService = new PersonnelManagementServices();
    private static final BorrowingReturningService borrowService = new BorrowingReturningService();
    private static final BookManagementService bookService = new BookManagementService();

    public static Response dispatch(Request request) {
        try {
            OperationType type = request.getOperationType();
            Object payload = request.getPayload();

            switch (type) {
                case REGISTER -> {
                    boolean result = registrationService.userRegistration((borrower) payload);
                    return new Response(result, result ? "注册成功" : "注册失败", null);
                }
                case BORROW -> {
                    String[] args = (String[]) payload;
                    boolean result = borrowService.borrowBook(args[0], args[1]);
                    return new Response(result, result ? "借书成功" : "借书失败", null);
                }
                case RETURN -> {
                    BigInteger transactionNumber = new BigInteger(payload.toString());
                    boolean result = borrowService.returnBook(transactionNumber);
                    return new Response(result, result ? "还书成功" : "还书失败", null);
                }
                case LIST_BOOKS -> {
                    List<bookVersion> list = bookService.getAllBooksWithVersions();
                    return new Response(true, "查询成功", list);
                }
                case ADD_BOOK -> {
                    Object[] params = (Object[]) payload;
                    book b = (book) params[0];
                    bookVersion v = (bookVersion) params[1];
                    boolean result = bookService.addBookWithVersion(b, v);
                    return new Response(result, result ? "添加成功" : "添加失败", null);
                }
                case DELETE_BOOK_VERSION -> {
                    String code = (String) payload;
                    boolean result = bookService.deleteBookVersion(code);
                    return new Response(result, result ? "删除成功" : "删除失败", null);
                }
                case UPDATE_BOOK_VERSION -> {
                    boolean result = bookService.updateBookVersion((bookVersion) payload);
                    return new Response(result, result ? "修改成功" : "修改失败", null);
                }
                case ADD_STAFF -> {
                    boolean result = personnelService.addStaff((staff) payload);
                    return new Response(result, result ? "添加员工成功" : "添加失败", null);
                }
                case RESIGN_STAFF -> {
                    boolean result = personnelService.logicResignStaff((String) payload);
                    return new Response(result, result ? "离职成功" : "离职失败", null);
                }
                case UPDATE_STAFF -> {
                    boolean result = personnelService.updateStaffInfoWithVersion((staff) payload);
                    return new Response(result, result ? "修改成功" : "修改失败", null);
                }
                case LIST_STAFF -> {
                    List<staff> list = personnelService.listAllStaff();
                    return new Response(true, "查询成功", list);
                }
                case SET_BORROWER_STATUS -> {
                    Object[] p = (Object[]) payload;
                    boolean result = personnelService.setBorrowerStatus((String) p[0], (String) p[1]);
                    return new Response(result, result ? "设置成功" : "设置失败", null);
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
