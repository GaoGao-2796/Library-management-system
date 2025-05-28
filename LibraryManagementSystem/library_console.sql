-- 员工表
CREATE TABLE staff (
                       staff_id INT AUTO_INCREMENT PRIMARY KEY,
                       staff_number CHAR(12) NOT NULL UNIQUE COMMENT '员工编号(12位数字，前两位为入职年份)',
                       name VARCHAR(20) NOT NULL,
                       gender ENUM('男','女') NOT NULL,
                       age TINYINT UNSIGNED COMMENT '年龄(<99)',
                       join_date DATE NOT NULL COMMENT '入职时间',
                       position ENUM('员工','管理员') NOT NULL DEFAULT '员工',
                       salary DECIMAL(10,2) UNSIGNED COMMENT '薪资(<1000000)',
                       status ENUM('工作','离职','退休','病逝') NOT NULL DEFAULT '工作',
                       INDEX idx_staff_number (staff_number)
) COMMENT '员工信息表';

-- 图书表（抽象）
CREATE TABLE books (
                       book_id INT AUTO_INCREMENT PRIMARY KEY,
                       isbn VARCHAR(20) NOT NULL COMMENT '国际标准书号',
                       title VARCHAR(100) NOT NULL COMMENT '书名(通用名称)',
                       original_title VARCHAR(100) COMMENT '原标题(外文书籍适用)',
                       UNIQUE KEY uk_isbn (isbn)
) COMMENT '图书基本信息表';

-- 图书版本表（具体版本信息）
CREATE TABLE book_versions (
                               version_id INT AUTO_INCREMENT PRIMARY KEY,
                               book_id INT NOT NULL COMMENT '关联图书表',
                               book_code CHAR(24) NOT NULL UNIQUE COMMENT '24位唯一编码',
                               authors VARCHAR(200) NOT NULL COMMENT '作者(多个作者用逗号分隔)',
                               edition VARCHAR(50) COMMENT '版次(第1版/修订版等)',
                               publish_date DATE COMMENT '出版日期',
                               language VARCHAR(20) DEFAULT '中文',
                               page_count INT UNSIGNED COMMENT '页数',
                               price DECIMAL(10,2) UNSIGNED COMMENT '定价',
                               stock_quantity INT UNSIGNED DEFAULT 0 COMMENT '当前库存量',
                               FOREIGN KEY (book_id) REFERENCES books(book_id),
                               INDEX idx_book_code (book_code)
) COMMENT '图书版本信息表';

-- 借阅者表
CREATE TABLE borrowers (
                           borrower_id INT AUTO_INCREMENT PRIMARY KEY,
                           borrower_number CHAR(12) NOT NULL UNIQUE COMMENT '借阅者编号(12位数字，前两位为办理年份)',
                           name VARCHAR(20) NOT NULL,
                           gender ENUM('男','女') NOT NULL,
                           age TINYINT UNSIGNED COMMENT '年龄(<99)',
                           register_date DATE NOT NULL COMMENT '注册时间',
                           status ENUM('未注册','正常','已注销') NOT NULL DEFAULT '未注册',
                           INDEX idx_borrower_number (borrower_number)
) COMMENT '借阅者信息表';

-- 书本借阅事务表（保留冗余字段方便查询）
CREATE TABLE borrow_records (
                                record_id INT AUTO_INCREMENT PRIMARY KEY,
                                borrower_id INT NOT NULL COMMENT '关联借阅者',
                                borrower_number CHAR(12) NOT NULL COMMENT '借阅者编号(冗余)',
                                transaction_number BIGINT UNSIGNED NOT NULL COMMENT '64位事务编号',
                                book_title VARCHAR(100) NOT NULL COMMENT '书名(冗余)',
                                isbn VARCHAR(20) NOT NULL COMMENT 'ISBN编号(冗余)',
                                book_code CHAR(24) NOT NULL COMMENT '24位图书编码(关联版本表)',
                                borrow_date DATE NOT NULL COMMENT '借阅日期',
                                due_date DATE NOT NULL COMMENT '应归还日期',
                                status ENUM('借阅中','逾期','已归还') NOT NULL DEFAULT '借阅中',
                                FOREIGN KEY (borrower_id) REFERENCES borrowers(borrower_id),
                                FOREIGN KEY (book_code) REFERENCES book_versions(book_code),
                                INDEX idx_borrower (borrower_id),
                                INDEX idx_book (book_code),
                                INDEX idx_due_date (due_date),
                                INDEX idx_status (status)
) COMMENT '图书借阅记录表';