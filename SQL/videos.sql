USE video_website;

CREATE TABLE video (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       duration INT NOT NULL COMMENT '秒',
                       play_url VARCHAR(512) NOT NULL COMMENT '视频播放地址',
                       cover_url VARCHAR(512) COMMENT '封面图地址',
                       format VARCHAR(20) DEFAULT 'mp4',
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
