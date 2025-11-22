# 广告追踪SDK测试指南

## 1. 测试环境准备

### 1.1 确保服务运行
确保广告追踪服务已在本地运行：
```bash
cd /Users/DaYang/ProjectCode/JavaDevelopProject/PrecisionAdvertisingPlacementSystem/ad-tracker-service
mvn spring-boot:run
```

服务应运行在 `http://localhost:8084`，跨域测试时也可通过 `http://track.test.com:8084` 访问

### 1.2 确认静态资源可访问
确认以下文件可通过浏览器访问：
- `http://localhost:8084/ad-tracker.js` - SDK文件
- `http://localhost:8084/test-sdk.html` - 测试页面

## 2. 手动功能测试

### 2.1 访问测试页面
在浏览器中打开测试页面：
```
http://localhost:8084/test-sdk.html
```

或者在配置了hosts后通过以下地址访问：
```
http://shop.test.com:8084/test-sdk.html
```

### 2.2 逐步执行测试

#### 步骤1：初始化SDK
1. 点击"初始化SDK(带域名)"按钮
2. 检查结果显示区，应显示：
   - SDK初始化成功
   - 追踪服务器地址为 http://track.test.com:8084
   - 网站标识
   - Cookie域为 .test.com
   - 生成的Cookie ID

#### 步骤2：用户行为追踪
1. 点击"记录页面浏览"按钮
2. 观察结果区显示"页面浏览记录成功!"
3. 点击"记录点击事件"按钮
4. 观察结果区显示"点击事件记录成功!"

#### 步骤3：广告交互追踪
1. 点击"记录广告展示"按钮
2. 观察结果区显示广告展示记录成功的详细信息
3. 点击"记录广告点击"按钮
4. 观察结果区显示广告点击记录成功的详细信息

#### 步骤4：广告推荐
1. 点击"获取推荐广告"按钮
2. 观察结果区显示"推荐广告获取成功!"
3. 检查下方是否显示推荐的广告列表

#### 步骤5：用户标识信息
1. 点击"显示用户标识信息"按钮
2. 观察结果区显示Cookie ID和浏览器指纹信息

## 3. 自动化测试

### 3.1 浏览器控制台测试
1. 打开测试页面 `http://localhost:8084/test-sdk.html`
2. 点击"运行自动化测试"按钮
3. 按F12打开开发者工具
4. 切换到Console标签页
5. 查看测试结果输出

## 4. 验证数据库记录

### 4.1 检查用户行为记录
```sql
USE advertising_system;
SELECT * FROM user_behaviors ORDER BY created_at DESC LIMIT 5;
```

应能看到通过SDK记录的页面浏览和点击行为。

### 4.2 检查广告展示记录
```sql
SELECT * FROM ad_impressions ORDER BY created_at DESC LIMIT 5;
```

应能看到通过SDK记录的广告展示和点击记录。

### 4.3 检查用户画像
```sql
SELECT * FROM user_profiles WHERE cookie_id = '你的测试Cookie ID';
```

应能看到对应的用户画像记录。

## 5. 跨域测试

### 5.1 配置本地域名
在 `/etc/hosts` 文件中添加以下内容：
```
127.0.0.1 shop.test.com
127.0.0.1 video.test.com
127.0.0.1 news.test.com
127.0.0.1 track.test.com
```

注意：这些域名使用 `.test.com` 后缀以符合现代浏览器的Cookie安全策略。

### 5.2 测试跨域Cookie共享
1. 访问 `http://shop.test.com:8084/test-sdk.html`
2. 点击"初始化SDK(带域名)"按钮，确保domain设置为`.test.com`
3. 执行一些用户行为操作(如记录页面浏览、点击等)
4. 访问 `http://video.test.com:8084/test-sdk.html`
5. 点击"初始化SDK(带域名)"按钮
6. 验证Cookie ID是否保持一致，确保跨域Cookie共享正常工作

## 6. 常见问题排查

### 6.1 SDK无法加载
- 检查文件路径是否正确
- 确认服务是否正常运行
- 查看浏览器开发者工具的Network标签页，确认文件请求状态

### 6.2 API调用失败
- 检查服务端是否正常运行
- 查看服务端日志确认错误信息
- 确认请求参数格式是否正确

### 6.3 数据未记录到数据库
- 检查API接口是否返回成功响应
- 查看服务端日志确认是否有数据库操作错误
- 确认数据库连接配置是否正确

## 7. 测试完成标准

通过以下所有检查点即表示SDK测试完成：

- [ ] SDK可以成功初始化，且domain设置为.test.com
- [ ] 能够生成唯一的用户标识(Cookie ID和浏览器指纹)
- [ ] 用户行为可以正确追踪(页面浏览、点击等)
- [ ] 广告交互可以正确追踪(展示、点击)
- [ ] 可以成功获取推荐广告
- [ ] 数据正确存储到数据库
- [ ] 跨域环境下用户标识保持一致
- [ ] 所有自动化测试用例通过

## 8. 测试数据清理

测试完成后，如需清理测试数据，可执行以下SQL：

```sql
USE advertising_system;

-- 清理测试用户行为
DELETE FROM user_behaviors WHERE cookie_id LIKE '%test%';

-- 清理测试用户画像
DELETE FROM user_profiles WHERE cookie_id LIKE '%test%';

-- 清理测试广告展示记录
DELETE FROM ad_impressions WHERE cookie_id LIKE '%test%';
```