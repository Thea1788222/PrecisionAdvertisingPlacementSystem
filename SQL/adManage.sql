CREATE DATABASE IF NOT EXISTS advertising_system;
USE advertising_system;
-- 广告商表
CREATE TABLE advertisers (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '广告商ID',
                             name VARCHAR(100) NOT NULL COMMENT '广告商名称',
                             contact VARCHAR(100) COMMENT '联系人',
                             email VARCHAR(100) COMMENT '邮箱',
                             balance DECIMAL(10,2) DEFAULT 0 COMMENT '账户余额',
                             status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 广告素材表
CREATE TABLE ad_materials (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '广告素材ID',
                              advertiser_id BIGINT COMMENT '广告商ID',
                              title VARCHAR(200) COMMENT '广告标题',
                              type ENUM('banner', 'video', 'native') NOT NULL COMMENT '广告类型：banner-横幅，video-视频，native-原生',
                              image_url VARCHAR(500) COMMENT '图片URL',
                              video_url VARCHAR(500) COMMENT '视频URL',
                              link_url VARCHAR(500) COMMENT '跳转链接',
                              width INT COMMENT '宽度(px)',
                              height INT COMMENT '高度(px)',
                              duration INT COMMENT '视频时长(秒)',
                              category VARCHAR(50) COMMENT '商品类别',
                              target_interest VARCHAR(200) COMMENT '目标兴趣',
                              bid_price DECIMAL(8,4) COMMENT '出价(每次展示)',
                              status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              FOREIGN KEY (advertiser_id) REFERENCES advertisers(id)
);

-- 用户行为表
CREATE TABLE user_behaviors (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '行为记录ID',
                                user_fingerprint VARCHAR(64) NOT NULL COMMENT '浏览器指纹',
                                cookie_id VARCHAR(64) NOT NULL COMMENT 'Cookie ID',
                                website VARCHAR(50) COMMENT '网站类型：shop/video/news',
                                action_type VARCHAR(50) COMMENT '行为类型：view/click/search/watch',
                                target_id VARCHAR(100) COMMENT '目标ID：商品ID/视频ID/新闻ID',
                                category VARCHAR(50) COMMENT '分类',
                                keywords VARCHAR(500) COMMENT '搜索关键词',
                                duration INT COMMENT '停留时长(秒)',
                                ip_address VARCHAR(50) COMMENT 'IP地址',
                                user_agent TEXT COMMENT '用户代理信息',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                INDEX idx_cookie (cookie_id),
                                INDEX idx_fingerprint (user_fingerprint),
                                INDEX idx_created (created_at)
);

-- 用户画像表
CREATE TABLE user_profiles (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户画像ID',
                               cookie_id VARCHAR(64) UNIQUE NOT NULL COMMENT 'Cookie ID',
                               user_fingerprint VARCHAR(64) COMMENT '浏览器指纹',
                               interests JSON COMMENT '兴趣标签及权重 {"electronics": 0.8, "sports": 0.6}',
                               categories JSON COMMENT '浏览过的类别',
                               behavior_score INT DEFAULT 0 COMMENT '行为评分',
                               last_active TIMESTAMP COMMENT '最后活跃时间',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               INDEX idx_cookie (cookie_id)
);

-- 广告投放记录
CREATE TABLE ad_impressions (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投放记录ID',
                                ad_id BIGINT COMMENT '广告ID',
                                cookie_id VARCHAR(64) COMMENT '用户Cookie ID',
                                website VARCHAR(50) COMMENT '投放网站',
                                position VARCHAR(50) COMMENT '广告位置：top/sidebar/mid-roll',
                                is_clicked TINYINT DEFAULT 0 COMMENT '是否点击：0-未点击，1-已点击',
                                bid_price DECIMAL(8,4) COMMENT '实际出价',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                FOREIGN KEY (ad_id) REFERENCES ad_materials(id),
                                INDEX idx_ad (ad_id),
                                INDEX idx_cookie (cookie_id)
);

-- 广告位配置表
CREATE TABLE ad_positions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '广告位ID',
                              website VARCHAR(50) NOT NULL COMMENT '网站标识',
                              position_key VARCHAR(50) NOT NULL COMMENT '位置标识',
                              position_name VARCHAR(100) NOT NULL COMMENT '位置名称',
                              width INT COMMENT '宽度(px)',
                              height INT COMMENT '高度(px)',
                              description TEXT COMMENT '描述信息',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              UNIQUE KEY uk_website_position (website, position_key)
);

-- 广告统计数据表
CREATE TABLE ad_statistics (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计记录ID',
                               ad_id BIGINT NOT NULL COMMENT '广告ID',
                               impressions_count INT DEFAULT 0 COMMENT '展示次数',
                               clicks_count INT DEFAULT 0 COMMENT '点击次数',
                               conversions_count INT DEFAULT 0 COMMENT '转化次数',
                               cost DECIMAL(10,4) DEFAULT 0 COMMENT '花费金额',
                               revenue DECIMAL(10,4) DEFAULT 0 COMMENT '收入金额',
                               date DATE NOT NULL COMMENT '统计日期',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               FOREIGN KEY (ad_id) REFERENCES ad_materials(id),
                               UNIQUE KEY uk_ad_date (ad_id, date)
);