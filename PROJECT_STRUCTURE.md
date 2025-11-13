# 项目结构说明

本项目由5个微服务组成：

1. ad-management-platform - 广告管理平台（后端）
2. shopping-website - 购物网站（后端）
3. video-website - 视频网站（后端）
4. news-website - 新闻网站（后端）
5. ad-tracker-service - 广告追踪服务（后端）

每个后端服务都基于Spring Boot构建，采用标准的Maven项目结构。

前端框架：

所有网站均使用Vue 3 + JavaScript：

1. shopping-website/frontend - 购物网站前端
2. video-website/frontend - 视频网站前端
3. news-website/frontend - 新闻网站前端


所有服务共享一个数据库：advertising_system，表结构定义在SQL目录下。
