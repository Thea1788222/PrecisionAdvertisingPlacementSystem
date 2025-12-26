# 广告追踪SDK集成指南

本文档旨在指导开发者如何在各个业务网站中集成广告追踪SDK，实现用户行为追踪、广告展示与点击追踪以及个性化广告推荐功能。

## 1. SDK概述

广告追踪SDK是一个轻量级的JavaScript库，提供了以下核心功能：
- 用户行为追踪（页面浏览、点击、搜索等）
- 广告展示与点击追踪
- 个性化广告推荐
- 跨域用户识别

## 2. 集成准备

### 2.1 获取SDK文件

SDK文件位于广告追踪服务的静态资源目录中：
```
http://track.yourdomain.com/ad-tracker.js
```

或者将文件直接下载到您的项目中。

### 2.2 基本集成步骤

1. 在每个需要追踪的网页中引入SDK文件
2. 初始化SDK配置
3. 调用相应的追踪方法

## 3. 详细集成步骤

### 3.1 引入SDK

在网页的`<head>`标签或`</body>`标签之前添加以下代码：

```html
<script src="http://track.yourdomain.com/ad-tracker.js"></script>
```


### 3.2 初始化SDK函数

在页面加载完成后调用`adTracker.init`函数初始化SDK：

```javascript
adTracker.init({
  trackerServer: 'http://track.yourdomain.com',  // 追踪服务地址
  website: 'your-website-name'                   // 网站类别，如：shop、video、news
});
```

参数说明：
- `trackerServer`: 广告追踪服务的完整URL地址
- `website`: 当前网站的唯一标识符，用于区分不同网站的数据


### 3.3 页面浏览追踪函数

在用户实际浏览内容时调用`adTracker.trackPageView`函数记录页面浏览：

参数说明：
- `targetId`: 目标ID，必需，建议使用"类型_具体ID"格式，例如：product_123、article_456、video_789等
- `category`: 类别标签，必需，使用预定义类别值，必须传入electronics、fashion、sports、home、food、travel、education、finance、health、beauty等10个类别之一
- `duration`: 1-3600秒，必需，表示用户在该内容上停留的时间

例如：当用户在一个电子产品的区域内停留30秒时，调用以下代码：
```javascript
adTracker.trackPageView({
  targetId: 'product_123',        // 目标ID
  category: 'electronics',        // 类别标签
  duration: 30                    // 页面浏览时长，单位秒（1-3600秒）
});                
```


### 3.4 点击事件追踪函数

为需要追踪的元素添加点击事件监听器，并调用`adTracker.trackClick`函数记录点击：

参数限制：
- `targetId`: 目标ID，必需，建议使用"类型_具体ID"格式，例如：ad_123、product_456、article_789等
- `category`: 类别标签，必需，必须传入electronics、fashion、sports、home、food、travel、education、finance、health、beauty等10个类别之一
- `duration`: 固定为1秒，必需，表示点击操作的持续时间

主要记录广告区域点击事件，例如当一个用户点击了一个运动鞋广告时，调用以下代码：
```javascript
adTracker.trackClick({
    targetId: 'ad_123',              // 目标ID
    category: 'sports',              // 类别标签
    duration: 1                     // 操作时长，单位秒（1秒）
});    
```


### 3.5 搜索事件追踪

当用户执行搜索操作时调用`adTracker.trackSearch`函数记录搜索行为：

参数说明：
- `targetId`: 目标ID，可选
- `category`: 类别标签，可选
- `keywords`: 用户实际输入的搜索内容，必需
- `duration`: 1-30秒，必需，表示搜索操作的持续时间

例如，当用户在搜索框中输入"iPhone 15 Pro"并点击搜索时，调用以下代码：
```javascript
adTracker.trackSearch({
    targetId: 'search_input',       // 搜索框或搜索结果页ID
    category: 'search',             // 类别，固定为'search'
    keywords: 'iPhone 15 Pro',      // 搜索关键词
    duration: 2                     // 搜索操作时长，单位秒（1-30秒）
});
```


### 3.6 通用行为追踪

SDK提供了一个通用方法`trackBehavior`，可以追踪各种类型的行为：

参数说明：
- `actionType`: 行为类型 view、click、search（必需）
- `targetId`: 目标ID，商品id、视频id、新闻id、广告id（必需）
- `category`: 类别，electronics、fashion、sports、home、food、travel、education、finance、health、beauty等（必需）
- `keywords`: 搜索关键词（搜索行为必需）
- `duration`: 持续时长，默认点击1秒，浏览10秒（必需）

```javascript
adTracker.trackBehavior('watch', {  // 'watch'可替换为'view', 'click', 'search'等
    targetId: 'video_456',          // 目标ID（最大长度50字符）
    category: 'entertainment',      // 类别（最大长度30字符）
    keywords: 'movie, comedy',      // 关键词（最大长度200字符）
    duration: 120                   // 持续时间，单位秒（1-3600秒）
});
```


### 3.7 获取推荐广告

获取个性化推荐广告并在页面上展示：

参数说明：
- `positions`: 每个位置标识最大长度30字符，必需
- `category`: 指定推荐广告的类别，可选，为空时服务会根据用户行为和历史数据推荐相关广告
- `type`: 广告类型， video、native（图片），必需
- `count`: 1-10，指定需要获取的广告数量，必需

返回结果：
- `ads`: 包含广告信息的数组，每个广告对象包含以下字段：
  - `adId`: 广告ID
  - `position`: 广告位标识
  - `category`: 广告类别
  - `title`: 广告标题
  - `description`: 广告描述
  - `imageUrl`: 广告图片URL
  - `clickUrl`: 广告点击链接

示例代码：
```javascript
adTracker.getRecommendedAds({
  positions: ['top-banner', 'sidebar'],  // 广告位标识数组（最多10个位置），必需
  category: '',               // 广告类别标签，可选
  type: 'video',            // 广告类型，必需
  count: 3                               // 获取广告数量（1-10个），必需
}).then(function(ads) {
  // 渲染广告到页面
  renderAds(ads);
}).catch(function(error) {
  console.error('获取推荐广告失败:', error);
});
```


### 3.8 广告展示追踪

当广告即将展示给用户时调用此方法：

```javascript
adTracker.trackAdImpression(adId, position, bidPrice);
```

参数说明：
- `adId`: 广告ID（在获取推荐广告时由服务端返回）
- `position`: 广告位标识（在获取推荐广告时由服务端返回）
- `bidPrice`: 广告出价（在获取推荐广告时由服务端返回）

**重要说明：** 服务端会返回广告展示记录的ID（impressionId），此ID需要保存用于后续的广告点击追踪。

### 3.9 广告点击追踪

当用户点击广告时调用此方法：

```javascript
adTracker.trackAdClick(impressionId);
```

参数说明：
- `impressionId`: 广告展示ID（在广告展示时由服务端返回）

**重要说明：** 必须使用在广告展示时服务端返回的impressionId来追踪广告点击，以确保广告展示与点击之间的关联关系。

### 3.10 广告展示-点击关联机制

为了正确追踪广告的展示-点击转化，必须遵循以下流程：

1. 调用[trackAdImpression](file:///Users/DaYang/ProjectCode/JavaDevelopProject/PrecisionAdvertisingPlacementSystem/ad-tracker-service/src/main/resources/static/shopping-test.js#L205-L215)记录广告展示
2. 服务端返回广告展示记录的ID（impressionId）
3. 将impressionId与广告元素进行关联存储
4. 当用户点击广告时，使用对应的impressionId调用[trackAdClick](file:///Users/DaYang/ProjectCode/JavaDevelopProject/PrecisionAdvertisingPlacementSystem/ad-tracker-service/src/main/resources/static/shopping-test.js#L218-L228)
5. 这样可以建立广告展示与点击之间的准确关联关系
