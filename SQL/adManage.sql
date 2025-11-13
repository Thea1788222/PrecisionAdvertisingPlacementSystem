CREATE DATABASE IF NOT EXISTS advertising_system;
USE advertising_system;
-- 广告商表
CREATE TABLE advertisers (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             name VARCHAR(100) NOT NULL,
                             contact VARCHAR(100),
                             email VARCHAR(100),
                             balance DECIMAL(10,2) DEFAULT 0,
                             status TINYINT DEFAULT 1,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 广告素材表
CREATE TABLE ad_materials (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              advertiser_id BIGINT,
                              title VARCHAR(200),
                              type ENUM('banner', 'video', 'native') NOT NULL,
                              image_url VARCHAR(500),
                              video_url VARCHAR(500),
                              link_url VARCHAR(500),
                              width INT,
                              height INT,
                              duration INT, -- 视频时长(秒)
                              category VARCHAR(50), -- 商品类别
                              target_interest VARCHAR(200), -- 目标兴趣
                              bid_price DECIMAL(8,4), -- 出价(每次展示)
                              status TINYINT DEFAULT 1,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (advertiser_id) REFERENCES advertisers(id)
);

-- 用户行为表
CREATE TABLE user_behaviors (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_fingerprint VARCHAR(64) NOT NULL, -- 浏览器指纹
                                cookie_id VARCHAR(64) NOT NULL,
                                website VARCHAR(50), -- shop/video/news
                                action_type VARCHAR(50), -- view/click/search/watch
                                target_id VARCHAR(100), -- 商品ID/视频ID/新闻ID
                                category VARCHAR(50),
                                keywords VARCHAR(500),
                                duration INT, -- 停留时长
                                ip_address VARCHAR(50),
                                user_agent TEXT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                INDEX idx_cookie (cookie_id),
                                INDEX idx_fingerprint (user_fingerprint),
                                INDEX idx_created (created_at)
);

-- 用户画像表
CREATE TABLE user_profiles (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               cookie_id VARCHAR(64) UNIQUE NOT NULL,
                               user_fingerprint VARCHAR(64),
                               interests JSON, -- {"electronics": 0.8, "sports": 0.6}
                               categories JSON, -- 浏览过的类别
                               behavior_score INT DEFAULT 0,
                               last_active TIMESTAMP,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               INDEX idx_cookie (cookie_id)
);

-- 广告投放记录
CREATE TABLE ad_impressions (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                ad_id BIGINT,
                                cookie_id VARCHAR(64),
                                website VARCHAR(50),
                                position VARCHAR(50), -- top/sidebar/mid-roll
                                is_clicked TINYINT DEFAULT 0,
                                bid_price DECIMAL(8,4),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (ad_id) REFERENCES ad_materials(id),
                                INDEX idx_ad (ad_id),
                                INDEX idx_cookie (cookie_id)
);

-- 广告位配置表
CREATE TABLE ad_positions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              website VARCHAR(50) NOT NULL, -- 网站标识
                              position_key VARCHAR(50) NOT NULL, -- 位置标识
                              position_name VARCHAR(100) NOT NULL, -- 位置名称
                              width INT,
                              height INT,
                              description TEXT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE KEY uk_website_position (website, position_key)
);

-- 广告统计数据表
CREATE TABLE ad_statistics (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               ad_id BIGINT NOT NULL,
                               impressions_count INT DEFAULT 0,
                               clicks_count INT DEFAULT 0,
                               conversions_count INT DEFAULT 0,
                               cost DECIMAL(10,4) DEFAULT 0,
                               revenue DECIMAL(10,4) DEFAULT 0,
                               date DATE NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (ad_id) REFERENCES ad_materials(id),
                               UNIQUE KEY uk_ad_date (ad_id, date)
);