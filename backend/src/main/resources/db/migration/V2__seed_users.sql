-- V2__seed_users.sql
-- 种子账号：解决"全新数据库没有管理员，注册用户永远无人审核"的死锁问题。
-- 账号与前端 mock 演示口径保持一致（密码均为 123456，BCrypt 加密）：
--   wenwen   / 123456  -> admin（管理员）
--   xiaoming / 123456  -> user（已审核普通用户）

INSERT INTO `user` (username, password, email, role, status)
VALUES
    ('wenwen',   '$2a$10$G6jIybVzFu61ncekCo3Osuws1xfPOytXR7LJZt8VZgnTE.4/3ufjC', 'wenwen@example.com',   'admin', 'approved'),
    ('xiaoming', '$2a$10$Q7b4kyZWYN.yECNDwkRjhuxpKKDIugxVOUEFYN5NozRooFbQTCyXO', 'xiaoming@example.com', 'user',  'approved');
