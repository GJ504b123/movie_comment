-- V1__init_tables.sql
-- 初始建表：user / movie / review / access_log

CREATE TABLE `user` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录名',
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt密文',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    role        VARCHAR(10)  NOT NULL DEFAULT 'user' COMMENT 'user / admin',
    status      VARCHAR(10)  NOT NULL DEFAULT 'pending' COMMENT 'pending / approved / rejected',
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_time  DATETIME DEFAULT NULL COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE `movie` (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(200) NOT NULL COMMENT '片名',
    description    TEXT         DEFAULT NULL COMMENT '简介',
    director       VARCHAR(100) DEFAULT NULL COMMENT '导演',
    `cast`         VARCHAR(500) DEFAULT NULL COMMENT '主演',
    release_date   DATE         DEFAULT NULL COMMENT '上映日期',
    cover_url      VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    average_score  DECIMAL(3,1) DEFAULT 0.0 COMMENT '平均分(冗余)',
    review_count   INT          DEFAULT 0 COMMENT '评论数(冗余)',
    deleted        TINYINT(1)   DEFAULT 0 COMMENT '软删除标记',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影片';

CREATE TABLE `review` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id    BIGINT NOT NULL COMMENT '影片ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    rating      INT    NOT NULL COMMENT '评分(1-10)',
    comment     TEXT   DEFAULT NULL COMMENT '评论文本',
    like_count  INT    DEFAULT 0 COMMENT '点赞数',
    hidden      TINYINT(1) DEFAULT 0 COMMENT '管理员隐藏标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    INDEX idx_review_movie_id (movie_id),
    INDEX idx_review_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影评/评分';

CREATE TABLE `access_log` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       DEFAULT NULL COMMENT '操作者(游客可空)',
    username    VARCHAR(50)  DEFAULT NULL COMMENT '操作者用户名(冗余，方便查询)',
    action      VARCHAR(30)  NOT NULL COMMENT '动作枚举',
    target_id   BIGINT       DEFAULT NULL COMMENT '目标对象ID',
    ip          VARCHAR(50)  DEFAULT NULL COMMENT '来源IP',
    user_agent  VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间',
    INDEX idx_log_user_id (user_id),
    INDEX idx_log_action (action),
    INDEX idx_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志';
