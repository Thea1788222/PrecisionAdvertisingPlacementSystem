USE video_website;

-- Delete existing test data
DELETE FROM video;

-- Insert new video data
INSERT INTO video(title, description, duration, play_url, cover_url, format, category) VALUES

-- Beauty Category
(
    'Beauty Tutorial',
    'Professional beauty and skincare guidance, teaching you how to properly care for your skin and maintain youthful vitality',
    128,
    'http://localhost:8082/videos/beauty1.mp4',
    'http://localhost:8082/videos/beauty1.jpg',
    'mp4',
    'beauty'
),
(
    'Beauty Short Video',
    'Detailed fashion makeup techniques, from basic to advanced, creating perfect makeup looks',
    77,
    'http://localhost:8082/videos/beauty2.mp4',
    'http://localhost:8082/videos/beauty2.jpg',
    'mp4',
    'beauty'
),

-- Education Category
(
    'SDK Introduction',
    'High-quality educational content covering multiple disciplines to help you improve learning abilities',
    217,
    'http://localhost:8082/videos/education1.mp4',
    'http://localhost:8082/videos/education1.jpg',
    'mp4',
    'education'
),
(
    'Cloud Computing Introduction',
    'Practical skills training courses to enhance your professional abilities and workplace competitiveness',
    170,
    'http://localhost:8082/videos/education2.mp4',
    'http://localhost:8082/videos/education2.jpg',
    'mp4',
    'education'
),

-- Electronics Category
(
    'Huawei Conference Highlights',
    'In-depth reviews of the latest electronic products with professional analysis of performance and user experience',
    120,
    'http://localhost:8082/videos/electronic1.mp4',
    'http://localhost:8082/videos/electronic1.jpg',
    'mp4',
    'electronics'
),
(
    'Apple Conference Opening Video',
    'Introduction to cutting-edge technology products, keeping you updated with latest tech trends and innovations',
    95,
    'http://localhost:8082/videos/electronic2.mp4',
    'http://localhost:8082/videos/electronic2.jpg',
    'mp4',
    'electronics'
),

-- Fashion Category
(
    'GUCCI Short Video',
    'Trendy fashion styling techniques to create personal style and improve dressing taste',
    88,
    'http://localhost:8082/videos/fashion1.mp4',
    'http://localhost:8082/videos/fashion1.jpg',
    'mp4',
    'fashion'
),
(
    'Dior Short Video',
    'Professional clothing matching tutorial, from color coordination to style selection, creating perfect looks',
    71,
    'http://localhost:8082/videos/fashion2.mp4',
    'http://localhost:8082/videos/fashion2.jpg',
    'mp4',
    'fashion'
),

-- Finance Category
(
    'Economics Educational Video',
    'Professional financial investment knowledge sharing to help you build correct investment concepts and strategies',
    89,
    'http://localhost:8082/videos/finance1.mp4',
    'http://localhost:8082/videos/finance1.jpg',
    'mp4',
    'finance'
),
(
    'Economics Educational Video 2',
    'In-depth analysis of latest financial news, grasping market dynamics and discovering investment opportunities',
    150,
    'http://localhost:8082/videos/finance2.mp4',
    'http://localhost:8082/videos/finance2.jpg',
    'mp4',
    'finance'
),

-- Food Category
(
    'Food Compilation 1',
    'Exquisite food preparation process, from ingredient selection to cooking, detailed tutorial on making delicious cuisine',
    130,
    'http://localhost:8082/videos/food1.mp4',
    'http://localhost:8082/videos/food1.jpg',
    'mp4',
    'food'
),
(
    'Food Compilation 2',
    'Explore regional specialties, understand different regional food culture and cooking techniques',
    122,
    'http://localhost:8082/videos/food2.mp4',
    'http://localhost:8082/videos/food2.jpg',
    'mp4',
    'food'
),

-- Health Category
(
    'First Aid Knowledge 1',
    'Professional health and wellness knowledge with scientific guidance for daily health care and disease prevention',
    180,
    'http://localhost:8082/videos/health1.mp4',
    'http://localhost:8082/videos/health1.jpg',
    'mp4',
    'health'
),
(
    'First Aid Knowledge 2',
    'Scientific exercise and fitness methods with professional guidance on fitness plans and training techniques for different groups',
    164,
    'http://localhost:8082/videos/health2.mp4',
    'http://localhost:8082/videos/health2.jpg',
    'mp4',
    'health'
),

-- Home Category
(
    'Home Lighting Design',
    'Home decoration design concepts sharing to create a warm and comfortable living environment',
    116,
    'http://localhost:8082/videos/home1.mp4',
    'http://localhost:8082/videos/home1.jpg',
    'mp4',
    'home'
),
(
    'Home Furniture Selection',
    'Practical home life tips to improve living quality and convenience',
    134,
    'http://localhost:8082/videos/home2.mp4',
    'http://localhost:8082/videos/home2.jpg',
    'mp4',
    'home'
),

-- Sports Category
(
    'Sampels High Pressure Highlights',
    'Exciting sports event reviews and skill demonstrations to experience the charm of sports',
    62,
    'http://localhost:8082/videos/sports1.mp4',
    'http://localhost:8082/videos/sports1.jpg',
    'mp4',
    'sports'
),
(
    'Federer Compilation',
    'Outdoor sports activity records and experience sharing, enjoying nature sports time',
    124,
    'http://localhost:8082/videos/sports2.mp4',
    'http://localhost:8082/videos/sports2.jpg',
    'mp4',
    'sports'
),

-- Travel Category
(
    'Hebei Scenery Compilation',
    'Beautiful landscape records and travel experience sharing, explore world beauty without leaving home',
    126,
    'http://localhost:8082/videos/travel1.mp4',
    'http://localhost:8082/videos/travel1.jpg',
    'mp4',
    'travel'
),
(
    'China Scenery Compilation',
    'Practical travel guides and trip planning to help you create perfect travel itineraries',
    133,
    'http://localhost:8082/videos/travel2.mp4',
    'http://localhost:8082/videos/travel2.jpg',
    'mp4',
    'travel'
);
