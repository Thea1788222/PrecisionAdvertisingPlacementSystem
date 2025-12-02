# 广告追踪SDK集成指南

本文档旨在指导开发者如何在各个业务网站中集成广告追踪SDK，实现用户行为追踪、广告展示与点击追踪以及个性化广告推荐功能。

## 1. SDK概述

广告追踪SDK是一个轻量级的JavaScript库，提供了以下核心功能：
- 用户行为追踪（页面浏览、点击等）
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

### 3.2 初始化SDK

在页面加载完成后初始化SDK：

```javascript
adTracker.init({
  trackerServer: 'http://track.yourdomain.com',  // 追踪服务地址
  website: 'your-website-name'                   // 网站标识
});
```

参数说明：
- `trackerServer`: 广告追踪服务的完整URL地址
- `website`: 当前网站的唯一标识符，用于区分不同网站的数据

### 3.3 页面浏览追踪

在页面加载完成后调用此方法记录页面浏览：

```javascript
adTracker.trackPageView({
  targetId: 'page-url-or-id',    // 页面URL或ID（可选，默认为当前页面路径）
  category: 'page-category',     // 页面分类（可选）
  keywords: 'keyword1,keyword2'  // 关键词（可选）
});
```

### 3.4 点击事件追踪

为需要追踪的元素添加点击事件监听器：

```javascript
document.getElementById('button-id').addEventListener('click', function() {
  adTracker.trackClick({
    targetId: 'button-id',      // 点击目标ID
    category: 'button-category' // 点击目标分类（可选）
  });
});
```

### 3.5 获取推荐广告

获取个性化推荐广告并在页面上展示：

```javascript
adTracker.getRecommendedAds({
  positions: ['top-banner', 'sidebar'],  // 广告位标识数组
  category: 'electronics',               // 广告类别（可选）
  count: 5                               // 获取广告数量（可选，默认为5）
}).then(function(ads) {
  // 渲染广告到页面
  renderAds(ads);
}).catch(function(error) {
  console.error('获取推荐广告失败:', error);
});
```

### 3.6 广告展示追踪

当广告即将展示给用户时调用此方法：

```javascript
adTracker.trackAdImpression(adId, position, bidPrice);
```

参数说明：
- `adId`: 广告ID
- `position`: 广告位标识
- `bidPrice`: 广告出价

### 3.7 广告点击追踪

当用户点击广告时调用此方法：

```javascript
adTracker.trackAdClick(impressionId);
```

参数说明：
- `impressionId`: 广告展示ID（在广告展示时由服务端返回）

## 4. 各业务网站集成示例

### 4.1 购物网站集成示例

```html
<!DOCTYPE html>
<html>
<head>
  <title>购物网站</title>
  <script src="http://track.yourdomain.com/ad-tracker.js"></script>
</head>
<body>
  <div id="product-list">
    <!-- 商品列表 -->
  </div>
  
  <div id="top-banner-ad">
    <!-- 顶部横幅广告容器 -->
  </div>
  
  <script>
    // 初始化SDK
    adTracker.init({
      trackerServer: 'http://track.yourdomain.com',
      website: 'shopping-website'
    });
    
    // 记录页面浏览
    adTracker.trackPageView({
      targetId: window.location.pathname,
      category: 'product-listing'
    });
    
    // 获取推荐广告
    adTracker.getRecommendedAds({
      positions: ['top-banner'],
      category: 'electronics',
      count: 3
    }).then(function(ads) {
      // 渲染顶部横幅广告
      renderBannerAds(ads, 'top-banner-ad');
    });
    
    // 为商品添加点击追踪
    document.querySelectorAll('.product-item').forEach(function(item) {
      item.addEventListener('click', function() {
        adTracker.trackClick({
          targetId: this.dataset.productId,
          category: 'product'
        });
      });
    });
  </script>
</body>
</html>
```

### 4.2 视频网站集成示例

```html
<!DOCTYPE html>
<html>
<head>
  <title>视频网站</title>
  <script src="http://track.yourdomain.com/ad-tracker.js"></script>
</head>
<body>
  <div id="video-player">
    <!-- 视频播放器 -->
  </div>
  
  <div id="video-list">
    <!-- 视频列表 -->
  </div>
  
  <script>
    // 初始化SDK
    adTracker.init({
      trackerServer: 'http://track.yourdomain.com',
      website: 'video-website'
    });
    
    // 记录页面浏览
    adTracker.trackPageView({
      targetId: window.location.pathname,
      category: 'video-watching'
    });
    
    // 视频播放时追踪
    function onVideoPlay(videoId) {
      adTracker.trackClick({
        targetId: videoId,
        category: 'video-play'
      });
    }
    
    // 视频结束时追踪
    function onVideoComplete(videoId) {
      adTracker.trackClick({
        targetId: videoId,
        category: 'video-complete'
      });
    }
  </script>
</body>
</html>
```

### 4.3 新闻网站集成示例

```html
<!DOCTYPE html>
<html>
<head>
  <title>新闻网站</title>
  <script src="http://track.yourdomain.com/ad-tracker.js"></script>
</head>
<body>
  <article id="news-content">
    <!-- 新闻内容 -->
  </article>
  
  <aside id="sidebar-ads">
    <!-- 侧边栏广告容器 -->
  </aside>
  
  <script>
    // 初始化SDK
    adTracker.init({
      trackerServer: 'http://track.yourdomain.com',
      website: 'news-website'
    });
    
    // 记录页面浏览
    adTracker.trackPageView({
      targetId: window.location.pathname,
      category: 'news-reading'
    });
    
    // 获取侧边栏推荐广告
    adTracker.getRecommendedAds({
      positions: ['sidebar'],
      category: 'news',
      count: 5
    }).then(function(ads) {
      // 渲染侧边栏广告
      renderSidebarAds(ads, 'sidebar-ads');
    });
    
    // 文章分享按钮点击追踪
    document.getElementById('share-button').addEventListener('click', function() {
      adTracker.trackClick({
        targetId: 'share-button',
        category: 'social-share'
      });
    });
  </script>
</body>
</html>
```

## 5. 跨域用户识别配置

为了在多个网站间实现统一的用户识别，请确保：

1. 所有网站的追踪服务地址配置相同
2. 确保广告追踪服务支持浏览器指纹进行用户识别
3. 在本地开发环境中，配置正确的hosts文件以模拟多域名环境

## 6. 最佳实践

### 6.1 性能优化

- 将SDK文件放在页面底部，避免阻塞页面渲染
- 使用异步加载方式引入SDK
- 避免频繁调用追踪方法，适当合并事件

### 6.2 错误处理

- 在调用SDK方法时添加适当的错误处理
- 监控网络请求状态，确保数据正确发送到服务端

### 6.3 隐私保护

- 遵守相关法律法规，如GDPR、CCPA等
- 在收集用户数据前提供明确的通知和选择退出机制
- 仅收集必要的用户行为数据

## 7. 故障排除

### 7.1 SDK未正确加载

检查：
- 网络连接是否正常
- SDK文件URL是否正确
- 浏览器控制台是否有加载错误

### 7.2 数据未正确上报

检查：
- 初始化配置是否正确
- 网络请求是否被防火墙或广告拦截器阻止
- 服务端接口是否正常工作

### 7.3 跨域识别问题

检查：
- 域名配置是否正确
- Cookie或浏览器指纹是否正常工作
- 是否遵守了浏览器的安全策略

## 8. 技术支持

如有任何集成问题，请联系技术支持团队：
- 邮箱：support@adtracking.com
- 电话：400-xxx-xxxx