# 概述

- /api/videos → 返回视频列表的 JSON
- /api/videos/{id} → 返回单个视频信息 JSON  
- /api/videos/category/{category} → 按分类获取视频列表
- /videos/{filename} → 静态资源 URL，可以直接在浏览器或 <video> 播放器中播放


# 接口1：获取视频列表

```
GET /api/videos

```

## 请求参数

| 参数   | 类型     | 必填 | 说明   |
| ---- | ------ | -- | ---- |
| page | number | 否  | 页码   |
| size | number | 否  | 每页条数 |


## 返回示例
```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "示例视频 1",
        "description": "这是一个测试视频",
        "duration": 300,
        "playUrl": "https://cdn.xxx.com/video/1.mp4",
        "coverUrl": "https://cdn.xxx.com/cover/1.jpg",
        "category": "movie"
      },
      {
        "id": 2,
        "title": "示例视频 2",
        "description": "这是另一个测试视频",
        "duration": 180,
        "playUrl": "https://cdn.xxx.com/video/2.mp4",
        "coverUrl": "https://cdn.xxx.com/cover/2.jpg",
        "category": "tv"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 100
  }
}

```

## 字段说明
| 字段       | 说明    |
| -------- | ----- |
| id       | 视频 ID |
| title    | 视频标题 |
| description | 视频描述 |
| duration | 视频时长(秒) |
| playUrl  | 视频播放地址 |
| coverUrl | 封面图地址 |
| category | 视频分类 |


# 接口2：获取视频播放信息

```
GET /api/videos/{id}

```

## 返回示例

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "title": "示例视频",
    "description": "这是一个测试视频",
    "duration": 300,
    "playUrl": "https://cdn.xxx.com/video/1.mp4",
    "format": "mp4",
    "coverUrl": "https://cdn.xxx.com/cover/1.jpg",
    "category": "movie"
  }
}

```

## 字段说明
| 字段       | 说明    |
| -------- | ----- |
| id       | 视频 ID |
| title    | 视频标题 |
| description | 视频描述 |
| duration | 视频时长(秒) |
| playUrl  | 视频播放地址 |
| format   | 视频格式 |
| coverUrl | 封面图地址 |
| category | 视频分类 |


# 接口3：按分类获取视频列表

```
GET /api/videos/category/{category}

```

## 路径参数

| 参数     | 类型     | 必填 | 说明   |
| ------- | ------ | -- | ---- |
| category | string | 是  | 视频分类，如: movie, tv, anime, music, game, documentary |


## 返回示例

```json
[
  {
    "id": 1,
    "title": "示例电影",
    "description": "这是一个测试电影",
    "duration": 300,
    "playUrl": "https://cdn.xxx.com/video/1.mp4",
    "coverUrl": "https://cdn.xxx.com/cover/1.jpg",
    "category": "movie"
  },
  {
    "id": 2,
    "title": "示例电影2",
    "description": "这是另一个测试电影",
    "duration": 450,
    "playUrl": "https://cdn.xxx.com/video/2.mp4",
    "coverUrl": "https://cdn.xxx.com/cover/2.jpg",
    "category": "movie"
  }
]

```

## 分类说明
| 分类代码 | 说明   |
| ------- | ---- |
| movie   | 电影   |
| tv      | 电视剧 |
| anime   | 动画   |
| music   | 音乐   |
| game    | 游戏   |
| documentary | 纪录片 |


