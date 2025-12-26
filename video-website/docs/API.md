# 概述

- /api/videos → 返回视频列表的 JSON

- /api/videos/{id} → 返回单个视频信息 JSON

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
        "coverUrl": "https://cdn.xxx.com/cover/1.jpg",
        "duration": 300
      },
      {
        "id": 2,
        "title": "示例视频 2",
        "coverUrl": "https://cdn.xxx.com/cover/2.jpg",
        "duration": 180
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
| coverUrl | 封面图   |
| duration | 秒     |


# 接口2：获取视频播放信息

```
GET /api/video/{id}

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
    "posterUrl": "https://cdn.xxx.com/cover/1.jpg"
  }
}

```

