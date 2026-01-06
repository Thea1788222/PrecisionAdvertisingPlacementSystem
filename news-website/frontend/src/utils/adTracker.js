/**
 * 广告追踪工具类
 * 负责SDK的初始化和获取
 */
export const initAdTracker = () => {
  if (typeof adTracker !== 'undefined') {
    adTracker.init({
      trackerServer: 'http://localhost:8084',  // 广告追踪服务地址
      website: 'news'  // 网站标识，必须是 'news'
    });
    console.log('广告追踪SDK初始化成功');
    return true;
  } else {
    console.error('广告追踪SDK未加载，请检查index.html中的SDK引入');
    return false;
  }
};

export const getAdTracker = () => {
  if (typeof adTracker !== 'undefined') {
    return adTracker;
  }
  return null;
};