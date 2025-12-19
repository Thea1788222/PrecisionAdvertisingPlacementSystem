# 精准广告投放生态系统 - API接口文档

## 1. 概述

本系统是一个完整的广告投放生态系统，包含5个核心服务：

1. **广告管理平台** (ad-management-platform) - 管理广告商、广告素材、投放策略、数据分析
2. **购物网站** (shopping-website) - 电商平台，展示商品并投放广告
3. **视频网站** (video-website) - 视频分享平台，支持视频中插广告
4. **新闻网站** (news-website) - 新闻资讯平台，投放原生广告
5. **广告追踪服务** (ad-tracker-service) - 核心服务，实现用户行为追踪、广告推荐算法

## 2. 技术架构

### 2.1 后端技术栈
- Spring Boot 3.1.x
- Spring Data JPA
- MySQL 8.0
- Maven 3.8+

### 2.2 前端技术栈
- Vue 3.x
- Vue Router 4.x
- Pinia 2.x
- Vite 4.x

### 2.3 服务端口分配
| 服务名称 | 端口   |
|---------|------|
| ad-management-platform | 8085 |
| shopping-website | 8081 |
| video-website | 8082 |
| news-website | 8083 |
| ad-tracker-service | 8084 |

## 3. 数据库设计

### 3.1 广告商表 (advertisers)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 广告商名称 |
| contact | VARCHAR(100) | 联系人 |
| email | VARCHAR(100) | 邮箱 |
| balance | DECIMAL(10,2) | 余额 |
| status | TINYINT | 状态(1:启用, 0:禁用) |
| created_at | TIMESTAMP | 创建时间 |

### 3.2 广告素材表 (ad_materials)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| advertiser_id | BIGINT | 广告商ID |
| title | VARCHAR(200) | 广告标题 |
| type | ENUM | 广告类型(banner, video, native) |
| image_url | VARCHAR(500) | 图片URL |
| video_url | VARCHAR(500) | 视频URL |
| link_url | VARCHAR(500) | 跳转链接 |
| width | INT | 宽度 |
| height | INT | 高度 |
| duration | INT | 视频时长(秒) |
| category | VARCHAR(50) | 商品类别 |
| target_interest | VARCHAR(200) | 目标兴趣 |
| bid_price | DECIMAL(8,4) | 出价 |
| status | TINYINT | 状态(1:启用, 0:禁用) |
| created_at | TIMESTAMP | 创建时间 |

### 3.3 用户行为表 (user_behaviors)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| user_fingerprint | VARCHAR(64) | 浏览器指纹 |
| cookie_id | VARCHAR(64) | Cookie ID |
| website | VARCHAR(50) | 网站标识 |
| action_type | VARCHAR(50) | 行为类型 |
| target_id | VARCHAR(100) | 目标ID |
| category | VARCHAR(50) | 类别 |
| keywords | VARCHAR(500) | 关键词 |
| duration | INT | 停留时长 |
| ip_address | VARCHAR(50) | IP地址 |
| user_agent | TEXT | User Agent |
| created_at | TIMESTAMP | 创建时间 |

### 3.4 用户画像表 (user_profiles)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| cookie_id | VARCHAR(64) | Cookie ID |
| user_fingerprint | VARCHAR(64) | 浏览器指纹 |
| interests | JSON | 兴趣标签 |
| categories | JSON | 浏览类别 |
| behavior_score | INT | 行为分数 |
| last_active | TIMESTAMP | 最后活跃时间 |
| created_at | TIMESTAMP | 创建时间 |

### 3.5 广告投放记录表 (ad_impressions)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| ad_id | BIGINT | 广告ID |
| cookie_id | VARCHAR(64) | Cookie ID |
| website | VARCHAR(50) | 网站标识 |
| position | VARCHAR(50) | 广告位 |
| is_clicked | TINYINT | 是否点击 |
| bid_price | DECIMAL(8,4) | 出价 |
| created_at | TIMESTAMP | 创建时间 |

### 3.6 广告位配置表 (ad_positions)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| website | VARCHAR(50) | 网站标识 |
| position_key | VARCHAR(50) | 位置标识 |
| position_name | VARCHAR(100) | 位置名称 |
| width | INT | 宽度 |
| height | INT | 高度 |
| description | TEXT | 描述 |
| created_at | TIMESTAMP | 创建时间 |

### 3.7 广告统计数据表 (ad_statistics)
| 字段名 | 类型 | 描述 |
|-------|------|-----|
| id | BIGINT | 主键 |
| ad_id | BIGINT | 广告ID |
| impressions_count | INT | 展示次数 |
| clicks_count | INT | 点击次数 |
| conversions_count | INT | 转化次数 |
| cost | DECIMAL(10,4) | 成本 |
| revenue | DECIMAL(10,4) | 收入 |
| date | DATE | 日期 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

## 4. 广告追踪服务接口 (ad-tracker-service)

### 4.1 用户行为追踪
#### 记录用户行为
- **URL**: `/api/track/behavior`
- **方法**: POST
- **描述**: 记录用户在网站上的行为（浏览、搜索、观看等）
- **请求参数**:
```json
{
  "id": 0,
  "userFingerprint": "string",
  "website": "string",
  "actionType": "string",
  "targetId": "string",
  "category": "string",
  "keywords": "string",
  "duration": 0,
  "ipAddress": "string",
  "userAgent": "string",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```
- **响应**:
```json
{
  "success": true,
  "message": "行为记录成功"
}
```

#### 记录广告展示
- **URL**: `/api/track/impression`
- **方法**: POST
- **描述**: 记录广告展示事件
- **请求参数**:
```json
{
  "id": 0,
  "adId": 0,
  "userFingerprint": "string",
  "website": "string",
  "position": "string",
  "isClicked": 0,
  "bidPrice": 0.0,
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```
- **响应**:
```json
{
  "success": true,
  "impressionId": 0,
  "message": "展示记录成功"
}
```

#### 记录广告点击
- **URL**: `/api/track/click`
- **方法**: POST
- **描述**: 记录广告点击事件
- **请求参数**:
```json
{
  "impressionId": 0,
  "userFingerprint": "string"
}
```
- **响应**:
```json
{
  "success": true,
  "message": "点击记录成功"
}
```

### 4.2 用户画像管理
#### 获取用户画像
- **URL**: `/api/user/profile/{userFingerprint}`
- **方法**: GET
- **描述**: 根据浏览器指纹获取用户画像
- **路径参数**:
  - userFingerprint: 用户浏览器指纹
- **响应**:
```json
{
  "id": 0,
  "userFingerprint": "string",
  "interests": "string",
  "categories": "string",
  "behaviorScore": 0,
  "lastActive": "2023-01-01T00:00:00.000Z",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 更新用户画像
- **URL**: `/api/user/profile`
- **方法**: PUT
- **描述**: 更新用户画像信息
- **请求参数**:
```json
{
  "id": 0,
  "userFingerprint": "string",
  "interests": "string",
  "categories": "string",
  "behaviorScore": 0,
  "lastActive": "2023-01-01T00:00:00.000Z",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```
- **响应**:
```json
{
  "success": true,
  "message": "用户画像更新成功"
}
```

### 4.3 广告推荐
#### 获取推荐广告
- **URL**: `/api/ad/recommend`
- **方法**: POST
- **描述**: 根据用户画像和上下文信息获取推荐广告
- **请求参数**:
```json
{
  "userFingerprint": "string",
  "website": "string",
  "positions": ["string"],
  "category": "string",
  "count": 0
}
```
- **响应**:
```json
{
  "ads": [
    {
      "id": 0,
      "advertiserId": 0,
      "title": "string",
      "type": "string",
      "imageUrl": "string",
      "videoUrl": "string",
      "linkUrl": "string",
      "width": 0,
      "height": 0,
      "duration": 0,
      "category": "string",
      "targetInterest": "string",
      "bidPrice": 0.0,
      "status": 0,
      "createdAt": "2023-01-01T00:00:00.000Z"
    }
  ]
}
```

## 5. 广告管理平台接口 (ad-management-platform)

### 5.1 用户认证
#### 用户注册
- **URL**: `/api/auth/register`
- **方法**: POST
- **描述**: 管理员用户注册（首次使用或添加管理员）
- **请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "fullName": "string"
}
```
- **响应**:
```json
{
  "id": 0,
  "username": "string",
  "email": "string",
  "fullName": "string",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 用户登录
- **URL**: `/api/auth/login`
- **方法**: POST
- **描述**: 管理员用户登录获取访问令牌
- **请求参数**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**:
```json
{
  "token": "string",
  "expiresIn": 0,
  "user": {
    "id": 0,
    "username": "string",
    "email": "string",
    "fullName": "string"
  }
}
```

#### 用户登出
- **URL**: `/api/auth/logout`
- **方法**: POST
- **描述**: 用户登出系统
- **请求头**:
  - Authorization: Bearer \<token\>
- **响应**:
```json
{
  "success": true,
  "message": "登出成功"
}
```

### 5.2 广告商管理
#### 获取广告商列表
- **URL**: `/api/advertisers`
- **方法**: GET
- **描述**: 分页获取广告商列表
- **查询参数**:
  - page: 页码（默认1）
  - size: 每页大小（默认10）
  - name: 广告商名称（模糊搜索）
- **响应**:
```json
{
  "content": [
    {
      "id": 0,
      "name": "string",
      "contact": "string",
      "email": "string",
      "balance": 0.0,
      "status": 0,
      "createdAt": "2023-01-01T00:00:00.000Z"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

#### 创建广告商
- **URL**: `/api/advertisers`
- **方法**: POST
- **描述**: 创建新的广告商
- **请求参数**:
```json
{
  "name": "string",
  "contact": "string",
  "email": "string"
}
```
- **响应**:
```json
{
  "id": 0,
  "name": "string",
  "contact": "string",
  "email": "string",
  "balance": 0.0,
  "status": 0,
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 更新广告商
- **URL**: `/api/advertisers/{id}`
- **方法**: PUT
- **描述**: 更新广告商信息
- **路径参数**:
  - id: 广告商ID
- **请求参数**:
```json
{
  "name": "string",
  "contact": "string",
  "email": "string",
  "status": 0
}
```
- **响应**:
```json
{
  "id": 0,
  "name": "string",
  "contact": "string",
  "email": "string",
  "balance": 0.0,
  "status": 0,
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 删除广告商
- **URL**: `/api/advertisers/{id}`
- **方法**: DELETE
- **描述**: 删除广告商
- **路径参数**:
  - id: 广告商ID
- **响应**:
```json
{
  "success": true,
  "message": "广告商删除成功"
}
```

### 5.2 广告素材管理
#### 获取广告素材列表
- **URL**: `/api/materials`
- **方法**: GET
- **描述**: 分页获取广告素材列表
- **查询参数**:
  - page: 页码（默认1）
  - size: 每页大小（默认10）
  - advertiserId: 广告商ID
  - type: 广告类型
  - category: 类别
  - status: 状态
- **响应**:
```json
{
  "content": [
    {
      "id": 0,
      "advertiserId": 0,
      "advertiserName": "string",
      "title": "string",
      "type": "string",
      "imageUrl": "string",
      "videoUrl": "string",
      "linkUrl": "string",
      "width": 0,
      "height": 0,
      "duration": 0,
      "category": "string",
      "targetInterest": "string",
      "bidPrice": 0.0,
      "status": 0,
      "createdAt": "2023-01-01T00:00:00.000Z"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

#### 创建广告素材
- **URL**: `/api/materials`
- **方法**: POST
- **描述**: 创建新的广告素材
- **请求参数**:
```json
{
  "advertiserId": 0,
  "title": "string",
  "type": "string",
  "imageUrl": "string",
  "videoUrl": "string",
  "linkUrl": "string",
  "width": 0,
  "height": 0,
  "duration": 0,
  "category": "string",
  "targetInterest": "string",
  "bidPrice": 0.0
}
```
- **响应**:
```json
{
  "id": 0,
  "advertiserId": 0,
  "title": "string",
  "type": "string",
  "imageUrl": "string",
  "videoUrl": "string",
  "linkUrl": "string",
  "width": 0,
  "height": 0,
  "duration": 0,
  "category": "string",
  "targetInterest": "string",
  "bidPrice": 0.0,
  "status": 0,
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 更新广告素材
- **URL**: `/api/materials/{id}`
- **方法**: PUT
- **描述**: 更新广告素材信息
- **路径参数**:
  - id: 广告素材ID
- **请求参数**:
```json
{
  "title": "string",
  "imageUrl": "string",
  "videoUrl": "string",
  "linkUrl": "string",
  "width": 0,
  "height": 0,
  "duration": 0,
  "category": "string",
  "targetInterest": "string",
  "bidPrice": 0.0,
  "status": 0
}
```
- **响应**:
```json
{
  "id": 0,
  "advertiserId": 0,
  "title": "string",
  "type": "string",
  "imageUrl": "string",
  "videoUrl": "string",
  "linkUrl": "string",
  "width": 0,
  "height": 0,
  "duration": 0,
  "category": "string",
  "targetInterest": "string",
  "bidPrice": 0.0,
  "status": 0,
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 删除广告素材
- **URL**: `/api/materials/{id}`
- **方法**: DELETE
- **描述**: 删除广告素材
- **路径参数**:
  - id: 广告素材ID
- **响应**:
```json
{
  "success": true,
  "message": "广告素材删除成功"
}
```

### 5.3 广告位配置
#### 获取广告位列表
- **URL**: `/api/positions`
- **方法**: GET
- **描述**: 获取广告位配置列表
- **查询参数**:
  - website: 网站标识
- **响应**:
```json
[
  {
    "id": 0,
    "website": "string",
    "positionKey": "string",
    "positionName": "string",
    "width": 0,
    "height": 0,
    "description": "string",
    "createdAt": "2023-01-01T00:00:00.000Z"
  }
]
```

#### 创建广告位
- **URL**: `/api/positions`
- **方法**: POST
- **描述**: 创建新的广告位配置
- **请求参数**:
```json
{
  "website": "string",
  "positionKey": "string",
  "positionName": "string",
  "width": 0,
  "height": 0,
  "description": "string"
}
```
- **响应**:
```json
{
  "id": 0,
  "website": "string",
  "positionKey": "string",
  "positionName": "string",
  "width": 0,
  "height": 0,
  "description": "string",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 更新广告位
- **URL**: `/api/positions/{id}`
- **方法**: PUT
- **描述**: 更新广告位配置
- **路径参数**:
  - id: 广告位ID
- **请求参数**:
```json
{
  "positionName": "string",
  "width": 0,
  "height": 0,
  "description": "string"
}
```
- **响应**:
```json
{
  "id": 0,
  "website": "string",
  "positionKey": "string",
  "positionName": "string",
  "width": 0,
  "height": 0,
  "description": "string",
  "createdAt": "2023-01-01T00:00:00.000Z"
}
```

#### 删除广告位
- **URL**: `/api/positions/{id}`
- **方法**: DELETE
- **描述**: 删除广告位配置
- **路径参数**:
  - id: 广告位ID
- **响应**:
```json
{
  "success": true,
  "message": "广告位配置删除成功"
}
```

### 5.4 数据统计
#### 获取广告统计数据
- **URL**: `/api/statistics/ads`
- **方法**: GET
- **描述**: 获取广告统计数据
- **查询参数**:
  - adId: 广告ID
  - startDate: 开始日期
  - endDate: 结束日期
- **响应**:
```json
[
  {
    "id": 0,
    "adId": 0,
    "adTitle": "string",
    "impressionsCount": 0,
    "clicksCount": 0,
    "conversionsCount": 0,
    "cost": 0.0,
    "revenue": 0.0,
    "ctr": 0.0,
    "date": "2023-01-01"
  }
]
```

#### 获取网站流量统计
- **URL**: `/api/statistics/traffic`
- **方法**: GET
- **描述**: 获取网站流量统计数据
- **查询参数**:
  - website: 网站标识
  - startDate: 开始日期
  - endDate: 结束日期
- **响应**:
```json
[
  {
    "date": "2023-01-01",
    "website": "string",
    "visits": 0,
    "uniqueVisitors": 0,
    "pageViews": 0
  }
]
```

#### 获取统计摘要数据
- **URL**: `/api/statistics/summary`
- **方法**: GET
- **描述**: 获取聚合统计数据（总展示次数、总点击次数、总收入等）
- **查询参数**:
  - startDate: 开始日期
  - endDate: 结束日期
  - website: 网站标识
- **响应**:
```json
{
  "totalImpressions": 125480,
  "totalClicks": 8752,
  "totalRevenue": 15680.00,
  "averageCtr": 6.98,
  "impressionsChange": 12.5,
  "clicksChange": 8.3,
  "revenueChange": 15.7,
  "ctrChange": -1.2
}
```

#### 获取趋势统计数据
- **URL**: `/api/statistics/trends`
- **方法**: GET
- **描述**: 获取按日期聚合的趋势数据
- **查询参数**:
  - startDate: 开始日期
  - endDate: 结束日期
  - website: 网站标识
- **响应**:
```json
[
  {
    "date": "2023-06-01",
    "impressions": 47370,
    "clicks": 3800,
    "revenue": 6550.00
  },
  {
    "date": "2023-06-02",
    "impressions": 51900,
    "clicks": 4200,
    "revenue": 7250.00
  }
]
```

#### 获取分布统计数据
- **URL**: `/api/statistics/distribution`
- **方法**: GET
- **描述**: 获取按网站或其他维度聚合的分布数据
- **查询参数**:
  - startDate: 开始日期
  - endDate: 结束日期
  - dimension: 维度 (website|ad)
  - metric: 指标 (impressions|clicks|revenue)
- **响应**:
```json
[
  {
    "dimension": "shopping",
    "metricValue": 31500,
    "percentage": 35.2
  },
  {
    "dimension": "video",
    "metricValue": 47750,
    "percentage": 53.3
  },
  {
    "dimension": "news",
    "metricValue": 10300,
    "percentage": 11.5
  }
]
```

## 6. 购物网站接口 (shopping-website)

### 6.1 商品管理
#### 获取商品列表
- **URL**: `/api/products`
- **方法**: GET
- **描述**: 分页获取商品列表
- **查询参数**:
  - page: 页码（默认1）
  - size: 每页大小（默认20）
  - category: 类别
  - keyword: 关键词搜索
- **响应**:
```json
{
  "content": [
    {
      "id": "string",
      "name": "string",
      "description": "string",
      "price": 0.0,
      "category": "string",
      "imageUrl": "string"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

#### 获取商品详情
- **URL**: `/api/products/{id}`
- **方法**: GET
- **描述**: 获取商品详细信息
- **路径参数**:
  - id: 商品ID
- **响应**:
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": 0.0,
  "category": "string",
  "imageUrl": "string",
  "details": "string"
}
```

### 6.2 搜索功能
#### 搜索商品
- **URL**: `/api/search`
- **方法**: GET
- **描述**: 搜索商品
- **查询参数**:
  - keyword: 搜索关键词
  - category: 类别筛选
  - page: 页码
  - size: 每页大小
- **响应**:
```json
{
  "content": [
    {
      "id": "string",
      "name": "string",
      "description": "string",
      "price": 0.0,
      "category": "string",
      "imageUrl": "string"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

## 7. 视频网站接口 (video-website)

### 7.1 视频管理
#### 获取视频列表
- **URL**: `/api/videos`
- **方法**: GET
- **描述**: 分页获取视频列表
- **查询参数**:
  - page: 页码（默认1）
  - size: 每页大小（默认20）
  - category: 类别
  - keyword: 关键词搜索
- **响应**:
```json
{
  "content": [
    {
      "id": "string",
      "title": "string",
      "description": "string",
      "duration": 0,
      "category": "string",
      "thumbnailUrl": "string",
      "videoUrl": "string"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

#### 获取视频详情
- **URL**: `/api/videos/{id}`
- **方法**: GET
- **描述**: 获取视频详细信息
- **路径参数**:
  - id: 视频ID
- **响应**:
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "duration": 0,
  "category": "string",
  "thumbnailUrl": "string",
  "videoUrl": "string",
  "views": 0,
  "likes": 0
}
```

## 8. 新闻网站接口 (news-website)

### 8.1 新闻管理
#### 获取新闻列表
- **URL**: `/api/news`
- **方法**: GET
- **描述**: 分页获取新闻列表
- **查询参数**:
  - page: 页码（默认1）
  - size: 每页大小（默认20）
  - category: 类别
  - keyword: 关键词搜索
- **响应**:
```json
{
  "content": [
    {
      "id": "string",
      "title": "string",
      "summary": "string",
      "content": "string",
      "category": "string",
      "imageUrl": "string",
      "publishTime": "2023-01-01T00:00:00.000Z"
    }
  ],
  "page": 0,
  "size": 0,
  "total": 0
}
```

#### 获取新闻详情
- **URL**: `/api/news/{id}`
- **方法**: GET
- **描述**: 获取新闻详细信息
- **路径参数**:
  - id: 新闻ID
- **响应**:
```json
{
  "id": "string",
  "title": "string",
  "summary": "string",
  "content": "string",
  "category": "string",
  "imageUrl": "string",
  "publishTime": "2023-01-01T00:00:00.000Z",
  "views": 0
}
```

## 9. 前端集成接口

### 9.1 广告追踪SDK (ad-tracker.js)
前端网站需要集成广告追踪SDK，主要功能包括：

1. **用户标识管理**
   - 生成和管理Cookie ID
   - 生成浏览器指纹
   - 跨域同步用户标识

2. **行为追踪**
   - 页面浏览追踪
   - 点击事件追踪
   - 停留时长计算
   - 搜索行为追踪

3. **广告交互追踪**
   - 广告展示追踪
   - 广告点击追踪
   - 视频广告观看追踪

4. **广告渲染**
   - 从广告追踪服务获取推荐广告
   - 根据广告位渲染广告内容
   - 处理广告跳转链接

### 9.2 SDK主要方法
```javascript
// 初始化SDK
adTracker.init({
  trackerServer: 'http://服务器IP:8084',
  website: 'shopping'
});

// 记录页面浏览
adTracker.trackPageView({
  category: 'electronics',
  targetId: 'product-123'
});

// 记录点击事件
adTracker.trackClick({
  targetId: 'button-456',
  targetType: 'button'
});

// 获取推荐广告
adTracker.getRecommendedAds({
  positions: ['top-banner', 'sidebar'],
  category: 'electronics'
}).then(ads => {
  // 渲染广告
});
```

## 10. 错误处理

所有API接口都遵循统一的错误响应格式：

```json
{
  "success": false,
  "errorCode": "string",
  "message": "错误描述",
  "timestamp": "2023-01-01T00:00:00.000Z"
}
```

常见错误码：
- `INVALID_PARAMETER`: 参数错误
- `RESOURCE_NOT_FOUND`: 资源未找到
- `PERMISSION_DENIED`: 权限不足
- `INTERNAL_ERROR`: 内部错误
- `SERVICE_UNAVAILABLE`: 服务不可用

## 11. 安全考虑

1. **跨域资源共享(CORS)**: 各服务需要正确配置CORS策略
2. **身份验证**: 管理平台接口需要JWT身份验证
3. **数据传输安全**: 所有接口应通过HTTPS访问
4. **输入验证**: 所有输入参数都需要进行验证和过滤
5. **速率限制**: 对API接口进行适当的速率限制以防止滥用

## 12. 部署配置

### 12.1 环境变量
各服务需要配置以下环境变量：
- `DATABASE_URL`: 数据库连接URL
- `SERVER_PORT`: 服务端口
- `JWT_SECRET`: JWT密钥
- `TRACKER_SERVER_URL`: 广告追踪服务URL

### 12.2 服务器部署配置
建议将5个服务分别部署在不同的物理服务器上，每台服务器配置独立的IP地址：
- `广告管理平台`: 服务器1 (如: 192.168.1.10:8080)
- `购物网站`: 服务器2 (如: 192.168.1.11:8081)
- `视频网站`: 服务器3 (如: 192.168.1.12:8082)
- `新闻网站`: 服务器4 (如: 192.168.1.13:8083)
- `广告追踪服务`: 服务器5 (如: 192.168.1.14:8084)

## 13. 监控和日志

### 13.1 日志级别
- `DEBUG`: 调试信息
- `INFO`: 一般信息
- `WARN`: 警告信息
- `ERROR`: 错误信息

### 13.2 监控指标
- API响应时间
- 数据库查询性能
- 服务可用性
- 用户行为统计
- 广告投放效果