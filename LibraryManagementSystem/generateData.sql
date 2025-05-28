-- 员工1（管理员）
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('220001000001', '张伟', '男', 35, '2022-01-15', '管理员', 8500.00, '工作');

-- 员工2
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('220002000002', '李娜', '女', 28, '2022-03-22', '员工', 6200.00, '工作');

-- 员工3
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('220003000003', '王芳', '女', 42, '2022-05-10', '员工', 7200.00, '工作');

-- 员工4
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('230004000004', '赵国强', '男', 31, '2023-02-18', '员工', 5800.00, '工作');

-- 员工5
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('230005000005', '陈静', '女', 26, '2023-04-05', '员工', 5500.00, '离职');

-- 员工6（管理员）
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('230006000006', '刘建军', '男', 45, '2023-06-30', '管理员', 9200.00, '工作');

-- 员工7
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('240007000007', '孙丽', '女', 29, '2024-01-12', '员工', 6000.00, '工作');

-- 员工8
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('240008000008', '周涛', '男', 38, '2024-02-25', '员工', 6800.00, '工作');

-- 员工9
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('240009000009', '吴小红', '女', 33, '2024-03-08', '员工', 6500.00, '退休');

-- 员工10
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('240010000010', '郑阳', '男', 27, '2024-04-15', '员工', 5900.00, '工作');

-- 员工11
INSERT INTO staff (staff_number, name, gender, age, join_date, position, salary, status)
VALUES ('240010000011', '高橦', '男', 21, '2025-05-25', '管理员', 10000.00, '工作');

-- ---------------------------------------------------------------------------------------------------------
-- books
-- 图书1-5：文学类
INSERT INTO books (isbn, title, original_title) VALUES ('9787020034636', '红楼梦', 'Dream of the Red Chamber');
INSERT INTO books (isbn, title, original_title) VALUES ('9787020046080', '三国演义', 'Romance of the Three Kingdoms');
INSERT INTO books (isbn, title, original_title) VALUES ('9787020053231', '西游记', 'Journey to the West');
INSERT INTO books (isbn, title, original_title) VALUES ('9787020065395', '水浒传', 'Water Margin');
INSERT INTO books (isbn, title, original_title) VALUES ('9787532739543', '三体', 'The Three-Body Problem');

-- 图书6-10：科技类
INSERT INTO books (isbn, title, original_title) VALUES ('9787111636665', 'Python编程从入门到实践', 'Python Crash Course');
INSERT INTO books (isbn, title, original_title) VALUES ('9787115470669', '算法图解', 'Grokking Algorithms');
INSERT INTO books (isbn, title, original_title) VALUES ('9787121361975', '人工智能简史', 'AI: A Modern Approach');
INSERT INTO books (isbn, title, original_title) VALUES ('9787302455106', '大数据技术原理与应用', 'Big Data: Principles and Applications');
INSERT INTO books (isbn, title, original_title) VALUES ('9787115428028', '深度学习', 'Deep Learning');

-- 图书11-15：历史类
INSERT INTO books (isbn, title, original_title) VALUES ('9787108053011', '明朝那些事儿', NULL);
INSERT INTO books (isbn, title, original_title) VALUES ('9787508647357', '人类简史', 'Sapiens: A Brief History of Humankind');
INSERT INTO books (isbn, title, original_title) VALUES ('9787508684031', '未来简史', 'Homo Deus: A Brief History of Tomorrow');
INSERT INTO books (isbn, title, original_title) VALUES ('9787108042459', '中国通史', NULL);
INSERT INTO books (isbn, title, original_title) VALUES ('9787505738968', '世界文明史', 'The Story of Civilization');

-- 图书16-20：小说类
INSERT INTO books (isbn, title, original_title) VALUES ('9787544258974', '百年孤独', 'Cien años de soledad');
INSERT INTO books (isbn, title, original_title) VALUES ('9787544267617', '霍乱时期的爱情', 'El amor en los tiempos del cólera');
INSERT INTO books (isbn, title, original_title) VALUES ('9787544281101', '追风筝的人', 'The Kite Runner');
INSERT INTO books (isbn, title, original_title) VALUES ('9787544245554', '挪威的森林', 'Norwegian Wood');
INSERT INTO books (isbn, title, original_title) VALUES ('9787544258219', '解忧杂货店', 'The Miracles of the Namiya General Store');



-- -------------------------------------------------
-- book_versions
-- 三体(book_id=5)的3个版本
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (5, 'TS2010PB001', '刘慈欣', '第一版', '2010-05-01', '中文', 302, 45.00, 5);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (5, 'TS2018HB002', '刘慈欣', '精装纪念版', '2018-01-15', '中文', 350, 89.00, 3);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (5, 'TS2020EN003', 'Liu Cixin, Ken Liu(Translator)', '英文版', '2020-11-20', '英文', 400, 120.00, 2);

-- Python编程(book_id=6)的2个版本
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (6, 'PY2016PB001', 'Eric Matthes', '第一版', '2016-07-01', '中文', 459, 89.00, 8);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (6, 'PY2020PB002', 'Eric Matthes', '第二版', '2020-05-15', '中文', 496, 99.00, 6);

-- 人类简史(book_id=12)的3个版本
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (12, 'HS2014PB001', '尤瓦尔·赫拉利', '第一版', '2014-11-01', '中文', 428, 68.00, 4);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (12, 'HS2017HB002', 'Yuval Noah Harari', '精装版', '2017-03-15', '英文', 512, 150.00, 2);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (12, 'HS2019PB003', '尤瓦尔·赫拉利', '修订版', '2019-08-20', '中文', 440, 75.00, 7);

-- 百年孤独(book_id=16)的2个版本
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (16, 'MG1967PB001', '加西亚·马尔克斯', '第一版', '2011-06-01', '中文', 360, 39.50, 6);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (16, 'MG2014HB002', 'Gabriel García Márquez', '50周年纪念版', '2014-04-23', '西班牙文', 368, 180.00, 1);

-- 其他图书各1-2个版本(省略部分字段显示)
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (1, 'HL2010PB001', '曹雪芹,高鹗', '校注版', '2010-01-01', '中文', 1200, 128.00, 4);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (2, 'SG2015PB001', '罗贯中', '白话版', '2015-05-01', '中文', 800, 98.00, 5);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (3, 'XY2018PB001', '吴承恩', '青少年版', '2018-06-01', '中文', 600, 65.00, 7);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (4, 'SH2012PB001', '施耐庵', '全本', '2012-03-01', '中文', 1000, 108.00, 3);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (7, 'AL2017PB001', 'Aditya Bhargava', '第一版', '2017-03-01', '中文', 256, 59.00, 9);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (8, 'AI2018PB001', 'Stuart Russell, Peter Norvig', '第三版', '2018-09-01', '中文', 1152, 168.00, 2);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (9, 'BD2019PB001', '林子雨', '第二版', '2019-01-01', '中文', 320, 55.00, 6);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (10, 'DL2016PB001', 'Ian Goodfellow, Yoshua Bengio, Aaron Courville', '第一版', '2016-11-01', '英文', 800, 299.00, 1);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (11, 'MJ2006PB001', '当年明月', '全套', '2006-09-01', '中文', 2080, 358.00, 3);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (13, 'FJ2017PB001', '尤瓦尔·赫拉利', '第一版', '2017-01-01', '中文', 392, 68.00, 5);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (14, 'ZH2013PB001', '吕思勉', '修订版', '2013-05-01', '中文', 880, 98.00, 4);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (15, 'SJ2018PB001', '威尔·杜兰特', '精编版', '2018-10-01', '中文', 1200, 198.00, 2);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (17, 'HL1985PB001', '加西亚·马尔克斯', '第一版', '2012-09-01', '中文', 368, 45.00, 5);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (18, 'KZ2007PB001', '卡勒德·胡赛尼', '第一版', '2007-04-01', '中文', 362, 39.00, 8);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (19, 'NW1987PB001', '村上春树, 林少华(译)', '精装版', '2018-03-01', '中文', 400, 59.00, 6);
INSERT INTO book_versions (book_id, book_code, authors, edition, publish_date, language, page_count, price, stock_quantity)
VALUES (20, 'JY2012PB001', '东野圭吾, 李盈春(译)', '第一版', '2012-12-01', '中文', 320, 45.00, 7);



-- --------------------------------------------------------------------------------------------
-- borrowers
-- 借阅者1-5
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240001000001', '王小明', '男', 22, '2024-01-05', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240002000002', '李华', '女', 25, '2024-01-12', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240003000003', '张伟', '男', 35, '2024-01-18', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240004000004', '刘芳', '女', 28, '2024-01-25', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240005000005', '陈强', '男', 42, '2024-02-03', '正常');

-- 借阅者6-10
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240006000006', '赵敏', '女', 19, '2024-02-10', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240007000007', '杨光', '男', 31, '2024-02-15', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240008000008', '周雪', '女', 27, '2024-02-20', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240009000009', '吴磊', '男', 23, '2024-02-28', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240010000010', '郑秀英', '女', 45, '2024-03-05', '正常');

-- 借阅者11-15
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240011000011', '孙建国', '男', 38, '2024-03-12', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240012000012', '朱丽叶', '女', 29, '2024-03-18', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240013000013', '胡伟', '男', 33, '2024-03-25', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240014000014', '林小玉', '女', 26, '2024-04-01', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('240015000015', '马超', '男', 30, '2024-04-10', '正常');

-- 借阅者16-20（包含不同状态）
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('230016000016', '黄蓉', '女', 40, '2023-12-15', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('230017000017', '郭靖', '男', 43, '2023-11-20', '已注销');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('230018000018', '令狐冲', '男', 36, '2023-10-25', '正常');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('230019000019', '任盈盈', '女', 32, '2023-09-30', '已注销');
INSERT INTO borrowers (borrower_number, name, gender, age, register_date, status)
VALUES ('230020000020', '岳不群', '男', 50, '2023-08-15', '未注册');
