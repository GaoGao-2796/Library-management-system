package ServerSide.protocol;

import java.io.Serializable;



enum OperationType implements Serializable {
    REGISTER,
    BORROW,
    RETURN,
    ADD_BOOK,
    DELETE_BOOK_VERSION,
    UPDATE_BOOK_VERSION,
    LIST_BOOKS,
    ADD_STAFF,
    RESIGN_STAFF,
    UPDATE_STAFF,
    LIST_STAFF,
    SET_BORROWER_STATUS
}

