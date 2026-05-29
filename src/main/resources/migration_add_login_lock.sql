ALTER TABLE sys_user
    ADD COLUMN failed_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    ADD COLUMN locked_until DATETIME NULL COMMENT '锁定截止时间';
