-- 广告商测试数据
INSERT INTO advertisers (name, contact, email, balance, status) VALUES
('科技数码有限公司', '张经理', 'zhang@tech.com', 10000.00, 1),
('时尚服装集团', '李总监', 'li@fashion.com', 5000.00, 1),
('运动健康品牌', '王主管', 'wang@sports.com', 8000.00, 1),
('家居生活用品', '陈经理', 'chen@home.com', 6000.00, 1),
('美食餐饮连锁', '刘经理', 'liu@food.com', 7000.00, 1);

-- 广告位配置测试数据
INSERT INTO ad_positions (website, position_key, position_name, width, height, description) VALUES
('shopping', 'top-banner', '顶部横幅', 970, 90, '购物网站首页顶部横幅广告位'),
('shopping', 'sidebar', '侧边栏', 300, 250, '购物网站商品列表页侧边栏广告位'),
('shopping', 'product-detail', '商品详情页', 728, 90, '购物网站商品详情页底部横幅'),
('video', 'pre-roll', '前贴片', 1920, 1080, '视频播放前的贴片广告'),
('video', 'mid-roll', '中插广告', 1920, 1080, '视频播放中的插播广告'),
('video', 'post-roll', '后贴片', 1920, 1080, '视频播放后的贴片广告'),
('news', 'top-banner', '顶部横幅', 728, 90, '新闻网站首页顶部横幅'),
('news', 'middle', '文章中部', 300, 250, '新闻文章页面中部广告位'),
('news', 'sidebar', '侧边栏', 160, 600, '新闻网站侧边栏广告位');

-- 广告素材测试数据
INSERT INTO ad_materials (advertiser_id, title, type, image_url, video_url, link_url, width, height, duration, category, target_interest, bid_price, status) VALUES
-- 科技数码类广告
(1, '最新智能手机促销', 'banner', 'https://example.com/images/phone_ad.jpg', NULL, 'https://shop.example.com/phones', 300, 250, NULL, 'electronics', 'technology', 1.5000, 1),
(1, '笔记本电脑特惠', 'banner', 'https://example.com/images/laptop_ad.jpg', NULL, 'https://shop.example.com/laptops', 728, 90, NULL, 'electronics', 'technology', 2.0000, 1),
(1, '智能手表新品发布', 'video', NULL, 'https://example.com/videos/smartwatch.mp4', 'https://shop.example.com/smartwatches', NULL, NULL, 30, 'electronics', 'technology', 3.5000, 1),

-- 时尚服装类广告
(2, '夏季新款女装', 'banner', 'https://example.com/images/summer_dress.jpg', NULL, 'https://fashion.example.com/women', 300, 250, NULL, 'fashion', 'fashion', 1.2000, 1),
(2, '男士商务套装', 'banner', 'https://example.com/images/suit_ad.jpg', NULL, 'https://fashion.example.com/men', 728, 90, NULL, 'fashion', 'fashion', 1.8000, 1),
(2, '运动休闲系列', 'native', 'https://example.com/images/sportswear.jpg', NULL, 'https://fashion.example.com/sports', NULL, NULL, NULL, 'fashion', 'sports', 1.0000, 1),

-- 运动健康类广告
(3, '专业跑鞋推荐', 'banner', 'https://example.com/images/running_shoes.jpg', NULL, 'https://sports.example.com/shoes', 300, 250, NULL, 'sports', 'sports', 1.3000, 1),
(3, '健身器材大促', 'banner', 'https://example.com/images/fitness_equipment.jpg', NULL, 'https://sports.example.com/equipment', 728, 90, NULL, 'sports', 'sports', 2.2000, 1),
(3, '瑜伽课程体验', 'video', NULL, 'https://example.com/videos/yoga_class.mp4', 'https://sports.example.com/yoga', NULL, NULL, 60, 'sports', 'sports', 2.8000, 1),

-- 家居生活类广告
(4, '智能家居套装', 'banner', 'https://example.com/images/smart_home.jpg', NULL, 'https://home.example.com/smarthome', 300, 250, NULL, 'home', 'home', 1.7000, 1),
(4, '厨房用具特卖', 'native', 'https://example.com/images/kitchen_tools.jpg', NULL, 'https://home.example.com/kitchen', NULL, NULL, NULL, 'home', 'home', 0.9000, 1),

-- 美食餐饮类广告
(5, '精品咖啡体验', 'banner', 'https://example.com/images/coffee.jpg', NULL, 'https://food.example.com/coffee', 300, 250, NULL, 'food', 'food', 1.1000, 1),
(5, '特色餐厅推荐', 'native', 'https://example.com/images/restaurant.jpg', NULL, 'https://food.example.com/restaurants', NULL, NULL, NULL, 'food', 'food', 1.4000, 1);

-- 用户画像测试数据
INSERT INTO user_profiles (user_fingerprint, interests, categories, behavior_score, last_active) VALUES
('fingerprint_001', '{"electronics": 0.9, "technology": 0.8}', '["electronics", "technology"]', 95, NOW()),
('fingerprint_002', '{"fashion": 0.7, "sports": 0.6}', '["fashion", "sports"]', 80, NOW()),
('fingerprint_003', '{"home": 0.8, "food": 0.7}', '["home", "food"]', 85, NOW()),
('fingerprint_004', '{"sports": 0.9, "electronics": 0.6}', '["sports", "electronics"]', 90, NOW()),
('fingerprint_005', '{"fashion": 0.8, "home": 0.5}', '["fashion", "home"]', 75, NOW());

-- 用户行为测试数据
INSERT INTO user_behaviors (user_fingerprint, website, action_type, target_id, category, keywords, duration, ip_address, user_agent) VALUES
-- 用户1的行为数据
('fingerprint_001', 'shopping', 'view', 'product_1001', 'electronics', '智能手机', 120, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('fingerprint_001', 'shopping', 'view', 'product_1002', 'electronics', '笔记本电脑', 90, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('fingerprint_001', 'shopping', 'click', 'ad_1', 'electronics', NULL, 5, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('fingerprint_001', 'video', 'watch', 'video_2001', 'technology', '科技评测', 300, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),

-- 用户2的行为数据
('fingerprint_002', 'shopping', 'view', 'product_1003', 'fashion', '夏季女装', 80, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
('fingerprint_002', 'shopping', 'search', NULL, 'fashion', '男士西装', 30, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
('fingerprint_002', 'video', 'watch', 'video_2002', 'sports', '健身教程', 420, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),

-- 用户3的行为数据
('fingerprint_003', 'news', 'view', 'news_3001', 'home', '家居装饰', 150, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/537.36'),
('fingerprint_003', 'news', 'view', 'news_3002', 'food', '美食推荐', 110, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/537.36'),
('fingerprint_003', 'shopping', 'click', 'ad_10', 'food', NULL, 8, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/537.36'),

-- 用户4的行为数据
('fingerprint_004', 'video', 'watch', 'video_2003', 'sports', '篮球教学', 540, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('fingerprint_004', 'shopping', 'view', 'product_1004', 'sports', '运动鞋', 95, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),

-- 用户5的行为数据
('fingerprint_005', 'shopping', 'search', NULL, 'fashion', '连衣裙', 45, '192.168.1.105', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
('fingerprint_005', 'shopping', 'view', 'product_1005', 'home', '家居用品', 75, '192.168.1.105', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36');

-- 广告投放记录测试数据
INSERT INTO ad_impressions (ad_id, user_fingerprint, website, position, is_clicked, bid_price) VALUES
(1, 'fingerprint_001', 'shopping', 'sidebar', 1, 1.5000),
(2, 'fingerprint_001', 'shopping', 'top-banner', 0, 2.0000),
(5, 'fingerprint_002', 'shopping', 'sidebar', 1, 1.8000),
(10, 'fingerprint_003', 'shopping', 'product-detail', 1, 1.1000),
(7, 'fingerprint_004', 'video', 'pre-roll', 0, 2.8000),
(4, 'fingerprint_005', 'shopping', 'sidebar', 0, 1.2000),
(1, 'fingerprint_001', 'shopping', 'sidebar', 1, 1.5000),
(3, 'fingerprint_004', 'video', 'mid-roll', 1, 3.5000);

-- 广告统计数据测试数据
INSERT INTO ad_statistics (ad_id, impressions_count, clicks_count, cost, revenue, date) VALUES
(1, 1500, 45, 2250.0000, 3000.0000, '2025-11-18'),
(2, 1200, 24, 2400.0000, 3200.0000, '2025-11-18'),
(3, 800, 32, 2800.0000, 3800.0000, '2025-11-18'),
(4, 900, 18, 1080.0000, 1500.0000, '2025-11-18'),
(5, 1100, 33, 1980.0000, 2700.0000, '2025-11-18'),
(6, 750, 15, 675.0000, 950.0000, '2025-11-18'),
(7, 2000, 80, 5600.0000, 7500.0000, '2025-11-18'),
(8, 600, 12, 1320.0000, 1800.0000, '2025-11-18'),
(9, 1300, 26, 2210.0000, 3100.0000, '2025-11-18'),
(10, 1600, 48, 1760.0000, 2400.0000, '2025-11-18'),
(11, 950, 19, 855.0000, 1200.0000, '2025-11-18'),
(12, 1400, 42, 1680.0000, 2300.0000, '2025-11-18');