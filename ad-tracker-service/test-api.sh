#!/bin/bash

# API测试脚本

echo "测试广告追踪服务API接口"

# 测试服务是否运行
echo "1. 测试服务是否运行"
curl -v http://localhost:8084/actuator/health

# 测试记录用户行为
echo -e "\n2. 测试记录用户行为"
curl -X POST http://localhost:8084/api/track/behavior \
  -H "Content-Type: application/json" \
  -d '{
    "userFingerprint": "test-fingerprint-123",
    "cookieId": "test-cookie-123",
    "website": "shopping",
    "actionType": "page_view",
    "targetId": "product-456",
    "category": "electronics",
    "keywords": "手机",
    "duration": 30,
    "ipAddress": "127.0.0.1",
    "userAgent": "Mozilla/5.0"
  }'

# 测试记录广告展示 (使用实际存在的广告ID)
echo -e "\n3. 测试记录广告展示"
curl -X POST http://localhost:8084/api/track/impression \
  -H "Content-Type: application/json" \
  -d '{
    "adId": 1,
    "cookieId": "test-cookie-123",
    "website": "shopping",
    "position": "sidebar",
    "bidPrice": 1.50
  }'

# 测试记录广告点击 (使用实际存在的展示ID)
echo -e "\n4. 测试记录广告点击"
curl -X POST http://localhost:8084/api/track/click \
  -H "Content-Type: application/json" \
  -d '{
    "impressionId": 1
  }'

# 测试获取用户画像
echo -e "\n5. 测试获取用户画像"
curl -X GET http://localhost:8084/api/user/profile/test-cookie-123

# 测试更新用户画像
echo -e "\n6. 测试更新用户画像"
curl -X PUT http://localhost:8084/api/user/profile \
  -H "Content-Type: application/json" \
  -d '{
    "cookieId": "test-cookie-123",
    "userFingerprint": "test-fingerprint-123",
    "interests": "{\"electronics\": 0.9, \"technology\": 0.8}",
    "categories": "[\"electronics\", \"technology\"]",
    "behaviorScore": 95
  }'

# 测试获取推荐广告
echo -e "\n7. 测试获取推荐广告"
curl -X POST http://localhost:8084/api/ad/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "cookieId": "test-cookie-123",
    "website": "shopping",
    "positions": ["top-banner", "sidebar"],
    "category": "electronics",
    "count": 5
  }'

echo -e "\n\n测试完成"