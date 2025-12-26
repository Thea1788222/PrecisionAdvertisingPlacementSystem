-- 新增广告商数据
INSERT INTO advertisers (name, contact, email, balance, status) VALUES
('旅游出行有限公司', '赵经理', 'zhao@travel.com', 12000.00, 1),
('教育培训集团', '孙总监', 'sun@edu.com', 9000.00, 1),
('金融理财服务', '周主管', 'zhou@finance.com', 15000.00, 1),
('健康医疗用品', '吴经理', 'wu@health.com', 8500.00, 1),
('美妆个护品牌', '郑总监', 'zheng@beauty.com', 11000.00, 1);

-- 新增广告素材测试数据
INSERT INTO ad_materials (advertiser_id, title, type, image_url, video_url, link_url, width, height, duration, category, target_interest, bid_price, status) VALUES
-- 电子数码类广告 (electronics)
(1, '平板电脑促销', 'native', 'https://example.com/images/tablet_ad.jpg', NULL, 'https://shop.example.com/tablets', NULL, NULL, NULL, 'electronics', 'technology', 1.6000, 1),
(1, '无线耳机新品', 'native', 'https://example.com/images/headphones_ad.jpg', NULL, 'https://shop.example.com/headphones', NULL, NULL, NULL, 'electronics', 'technology', 1.2000, 1),
(1, 'VR眼镜体验', 'native', 'https://example.com/images/vr_headset.jpg', NULL, 'https://shop.example.com/vr', NULL, NULL, NULL, 'electronics', 'technology', 2.0000, 1),
(1, '数码相机特价', 'native', 'https://example.com/images/camera_deal.jpg', NULL, 'https://shop.example.com/cameras', NULL, NULL, NULL, 'electronics', 'photography', 2.3000, 1),
(1, '游戏手柄推荐', 'native', 'https://example.com/images/game_controller.jpg', NULL, 'https://shop.example.com/gaming', NULL, NULL, NULL, 'electronics', 'gaming', 1.8000, 1),

-- 时尚服装类广告 (fashion)
(2, '秋冬大衣精选', 'native', 'https://example.com/images/coat_ad.jpg', NULL, 'https://fashion.example.com/winter', NULL, NULL, NULL, 'fashion', 'fashion', 1.3000, 1),
(2, '配饰搭配推荐', 'native', 'https://example.com/images/accessories_ad.jpg', NULL, 'https://fashion.example.com/accessories', NULL, NULL, NULL, 'fashion', 'fashion', 0.8000, 1),
(2, '时尚搭配视频', 'video', NULL, 'https://example.com/videos/fashion_tips.mp4', 'https://fashion.example.com/tips', NULL, NULL, 60, 'fashion', 'fashion', 2.0000, 1),
(2, '春季新品上市', 'native', 'https://example.com/images/spring_collection.jpg', NULL, 'https://fashion.example.com/spring', NULL, NULL, NULL, 'fashion', 'fashion', 1.4000, 1),
(2, '运动服装系列', 'native', 'https://example.com/images/sportswear_collection.jpg', NULL, 'https://fashion.example.com/sportswear', NULL, NULL, NULL, 'fashion', 'sports', 1.5000, 1),
(2, '鞋履新品', 'native', 'https://example.com/images/shoe_collection.jpg', NULL, 'https://fashion.example.com/shoes', NULL, NULL, NULL, 'fashion', 'fashion', 1.6000, 1),

-- 运动健康类广告 (sports)
(3, '户外运动装备', 'native', 'https://example.com/images/outdoor_equipment.jpg', NULL, 'https://sports.example.com/outdoor', NULL, NULL, NULL, 'sports', 'sports', 1.5000, 1),
(3, '健身会员卡优惠', 'native', 'https://example.com/images/gym_membership.jpg', NULL, 'https://sports.example.com/membership', NULL, NULL, NULL, 'sports', 'sports', 1.1000, 1),
(3, '运动营养补剂', 'video', NULL, 'https://example.com/videos/nutrition_tips.mp4', 'https://sports.example.com/nutrition', NULL, NULL, 50, 'sports', 'sports', 2.2000, 1),
(3, '户外鞋推荐', 'native', 'https://example.com/images/outdoor_shoes.jpg', NULL, 'https://sports.example.com/outdoor-shoes', NULL, NULL, NULL, 'sports', 'sports', 1.4000, 1),
(3, '运动服装套装', 'native', 'https://example.com/images/sport_outfit.jpg', NULL, 'https://sports.example.com/outfit', NULL, NULL, NULL, 'sports', 'sports', 1.7000, 1),
(3, '瑜伽垫推荐', 'native', 'https://example.com/images/yoga_mat.jpg', NULL, 'https://sports.example.com/yoga', NULL, NULL, NULL, 'sports', 'yoga', 1.2000, 1),

-- 家居生活类广告 (home)
(4, '客厅装饰方案', 'native', 'https://example.com/images/living_room_decor.jpg', NULL, 'https://home.example.com/living-room', NULL, NULL, NULL, 'home', 'home', 1.2000, 1),
(4, '卧室床品套装', 'native', 'https://example.com/images/bedding_set.jpg', NULL, 'https://home.example.com/bedding', NULL, NULL, NULL, 'home', 'home', 0.9500, 1),
(4, '智能家居体验', 'video', NULL, 'https://example.com/videos/smart_home_demo.mp4', 'https://home.example.com/smarthome-demo', NULL, NULL, 70, 'home', 'home', 2.4000, 1),
(4, '厨房电器套装', 'native', 'https://example.com/images/kitchen_appliances.jpg', NULL, 'https://home.example.com/kitchen-appliances', NULL, NULL, NULL, 'home', 'home', 1.8000, 1),
(4, '浴室用品推荐', 'native', 'https://example.com/images/bathroom_supplies.jpg', NULL, 'https://home.example.com/bathroom', NULL, NULL, NULL, 'home', 'home', 1.3000, 1),
(4, '书房家具', 'native', 'https://example.com/images/study_furniture.jpg', NULL, 'https://home.example.com/study', NULL, NULL, NULL, 'home', 'home', 1.9000, 1),

-- 美食餐饮类广告 (food)
(5, '特色小吃推荐', 'native', 'https://example.com/images/local_snacks.jpg', NULL, 'https://food.example.com/snacks', NULL, NULL, NULL, 'food', 'food', 1.0000, 1),
(5, '健康轻食套餐', 'native', 'https://example.com/images/healthy_meals.jpg', NULL, 'https://food.example.com/healthy', NULL, NULL, NULL, 'food', 'food', 1.2500, 1),
(5, '美食制作过程', 'video', NULL, 'https://example.com/videos/cooking_process.mp4', 'https://food.example.com/cooking', NULL, NULL, 80, 'food', 'food', 2.6000, 1),
(5, '外卖优惠券', 'native', 'https://example.com/images/food_delivery_coupon.jpg', NULL, 'https://food.example.com/delivery', NULL, NULL, NULL, 'food', 'food', 0.7500, 1),
(5, '烘焙原料', 'native', 'https://example.com/images/baking_ingredients.jpg', NULL, 'https://food.example.com/baking', NULL, NULL, NULL, 'food', 'cooking', 1.1000, 1),
(5, '进口食品', 'native', 'https://example.com/images/imported_food.jpg', NULL, 'https://food.example.com/imported', NULL, NULL, NULL, 'food', 'food', 1.4000, 1),

-- 旅游出行类广告 (travel)
(6, '热门旅游景点', 'native', 'https://example.com/images/popular_destinations.jpg', NULL, 'https://travel.example.com/destinations', NULL, NULL, NULL, 'travel', 'travel', 1.9000, 1),
(6, '酒店预订优惠', 'native', 'https://example.com/images/hotel_deals.jpg', NULL, 'https://travel.example.com/hotels', NULL, NULL, NULL, 'travel', 'travel', 1.3500, 1),
(6, '旅游攻略视频', 'video', NULL, 'https://example.com/videos/travel_guide.mp4', 'https://travel.example.com/guides', NULL, NULL, 90, 'travel', 'travel', 2.7000, 1),
(6, '机票特价活动', 'native', 'https://example.com/images/flight_deals.jpg', NULL, 'https://travel.example.com/flights', NULL, NULL, NULL, 'travel', 'travel', 1.8500, 1),
(6, '度假村推荐', 'native', 'https://example.com/images/resort_recommend.jpg', NULL, 'https://travel.example.com/resorts', NULL, NULL, NULL, 'travel', 'leisure', 2.1000, 1),
(6, '租车服务', 'native', 'https://example.com/images/car_rental.jpg', NULL, 'https://travel.example.com/rental', NULL, NULL, NULL, 'travel', 'travel', 1.4500, 1),

-- 教育培训类广告 (education)
(7, '在线编程课程', 'native', 'https://example.com/images/programming_course.jpg', NULL, 'https://edu.example.com/programming', NULL, NULL, NULL, 'education', 'education', 1.7000, 1),
(7, '语言学习应用', 'native', 'https://example.com/images/language_app.jpg', NULL, 'https://edu.example.com/language', NULL, NULL, NULL, 'education', 'education', 1.1500, 1),
(7, '学习方法视频', 'video', NULL, 'https://example.com/videos/study_tips.mp4', 'https://edu.example.com/tips', NULL, NULL, 65, 'education', 'education', 2.3000, 1),
(7, '职业技能培训', 'native', 'https://example.com/images/skill_training.jpg', NULL, 'https://edu.example.com/skills', NULL, NULL, NULL, 'education', 'education', 1.6000, 1),
(7, '儿童早教课程', 'native', 'https://example.com/images/early_education.jpg', NULL, 'https://edu.example.com/early-childhood', NULL, NULL, NULL, 'education', 'parenting', 1.8000, 1),
(7, '考研辅导班', 'native', 'https://example.com/images/entrance_exam.jpg', NULL, 'https://edu.example.com/entrance', NULL, NULL, NULL, 'education', 'education', 2.0000, 1),

-- 金融理财类广告 (finance)
(8, '理财产品推荐', 'native', 'https://example.com/images/wealth_management.jpg', NULL, 'https://finance.example.com/products', NULL, NULL, NULL, 'finance', 'finance', 2.1000, 1),
(8, '保险产品介绍', 'native', 'https://example.com/images/insurance_products.jpg', NULL, 'https://finance.example.com/insurance', NULL, NULL, NULL, 'finance', 'finance', 1.4500, 1),
(8, '理财规划视频', 'video', NULL, 'https://example.com/videos/financial_planning.mp4', 'https://finance.example.com/planning', NULL, NULL, 75, 'finance', 'finance', 2.8000, 1),
(8, '信用卡优惠', 'native', 'https://example.com/images/credit_card_offers.jpg', NULL, 'https://finance.example.com/credit-cards', NULL, NULL, NULL, 'finance', 'finance', 1.2500, 1),
(8, '股票投资课程', 'native', 'https://example.com/images/stock_investment.jpg', NULL, 'https://finance.example.com/investment', NULL, NULL, NULL, 'finance', 'investment', 2.2000, 1),
(8, '基金定投', 'native', 'https://example.com/images/fund_investment.jpg', NULL, 'https://finance.example.com/funds', NULL, NULL, NULL, 'finance', 'finance', 1.9000, 1),

-- 健康医疗类广告 (health)
(9, '健康体检套餐', 'native', 'https://example.com/images/health_checkup.jpg', NULL, 'https://health.example.com/checkup', NULL, NULL, NULL, 'health', 'health', 1.8000, 1),
(9, '保健品推荐', 'native', 'https://example.com/images/health_supplements.jpg', NULL, 'https://health.example.com/supplements', NULL, NULL, NULL, 'health', 'health', 1.3000, 1),
(9, '健康生活视频', 'video', NULL, 'https://example.com/videos/healthy_lifestyle.mp4', 'https://health.example.com/lifestyle', NULL, NULL, 55, 'health', 'health', 2.1000, 1),
(9, '医疗服务预约', 'native', 'https://example.com/images/medical_appointment.jpg', NULL, 'https://health.example.com/appointments', NULL, NULL, NULL, 'health', 'health', 1.6500, 1),
(9, '医疗器械', 'native', 'https://example.com/images/medical_equipment.jpg', NULL, 'https://health.example.com/equipment', NULL, NULL, NULL, 'health', 'health', 1.9500, 1),
(9, '中医养生', 'native', 'https://example.com/images/traditional_medicine.jpg', NULL, 'https://health.example.com/traditional', NULL, NULL, NULL, 'health', 'wellness', 1.7500, 1),

-- 美妆个护类广告 (beauty)
(10, '护肤套装推荐', 'native', 'https://example.com/images/skincare_set.jpg', NULL, 'https://beauty.example.com/skincare', NULL, NULL, NULL, 'beauty', 'beauty', 1.5500, 1),
(10, '彩妆新品试用', 'native', 'https://example.com/images/makeup_trial.jpg', NULL, 'https://beauty.example.com/makeup', NULL, NULL, NULL, 'beauty', 'beauty', 1.4000, 1),
(10, '美容技巧视频', 'video', NULL, 'https://example.com/videos/beauty_tips.mp4', 'https://beauty.example.com/tips', NULL, NULL, 60, 'beauty', 'beauty', 2.4000, 1),
(10, '个人护理用品', 'native', 'https://example.com/images/personal_care.jpg', NULL, 'https://beauty.example.com/personal-care', NULL, NULL, NULL, 'beauty', 'beauty', 1.2000, 1),
(10, '香水推荐', 'native', 'https://example.com/images/perfume_recommend.jpg', NULL, 'https://beauty.example.com/perfume', NULL, NULL, NULL, 'beauty', 'beauty', 1.6500, 1),
(10, '护发产品', 'native', 'https://example.com/images/hair_care.jpg', NULL, 'https://beauty.example.com/hair', NULL, NULL, NULL, 'beauty', 'beauty', 1.3500, 1);