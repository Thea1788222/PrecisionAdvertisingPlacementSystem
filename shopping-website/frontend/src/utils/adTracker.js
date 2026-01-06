/**
 * 广告追踪工具类
 * 封装杨宇程的SDK调用
 */

class AdTracker {
  constructor() {
    this._isInitialized = false;
    this.impressionMap = new Map();

    // 自动初始化
    this._initialize();
  }

  /**
   * 私有初始化方法
   */
  async _initialize() {
    if (!window.adTracker) {
      console.warn('adTracker SDK 未加载');
      return false;
    }

    try {
      // 初始化杨宇程的SDK
      window.adTracker.init({
        trackerServer: 'http://10.100.164.35:8084',
        website: 'shop'
      });

      this._isInitialized = true;
      console.log('✅ AdTracker 初始化成功，连接到:', window.adTracker.config.trackerServer);
      return true;
    } catch (error) {
      console.error('❌ AdTracker 初始化失败:', error);
      return false;
    }
  }

  /**
   * 公共getter
   */
  get isInitialized() {
    return this._isInitialized;
  }

  /**
   * 公共初始化方法
   */
  init() {
    return this._initialize();
  }

  /**
   * 记录页面浏览
   */
  trackPageView(options = {}) {
    if (!window.adTracker) {
      console.warn('adTracker SDK 未加载，跳过页面浏览记录');
      return;
    }

    try {
      window.adTracker.trackPageView({
        targetId: options.targetId || 'homepage',
        category: options.category || 'home',
        duration: options.duration || 10
      });
      console.log('📊 记录页面浏览:', options.targetId);
    } catch (error) {
      console.error('记录页面浏览失败:', error);
    }
  }

  /**
   * 记录商品浏览
   */
  trackProductView(product) {
    if (!product || !product.id) return;

    this.trackPageView({
      targetId: `product_${product.id}`,
      category: product.category,
      duration: 5
    });
  }

  /**
   * 记录商品点击
   */
  trackProductClick(product) {
    if (!window.adTracker || !product) {
      return;
    }

    try {
      window.adTracker.trackClick({
        targetId: `product_${product.id}`,
        category: product.category,
        duration: 1
      });
      console.log('🖱️ 记录商品点击:', product.name);
    } catch (error) {
      console.error('记录商品点击失败:', error);
    }
  }

  /**
   * 记录搜索行为
   */
  trackSearch(keyword, category = '') {
    if (!window.adTracker || !keyword) {
      return;
    }

    try {
      window.adTracker.trackSearch({
        keywords: keyword,
        category: category,
        targetId: ''
      });
      console.log('🔍 记录搜索行为:', keyword);
    } catch (error) {
      console.error('记录搜索失败:', error);
    }
  }

  /**
   * 获取推荐广告
   */
  async getRecommendedAds(options = {}) {
    console.log('📡 开始获取广告...');

    // 1. 检查杨宇程SDK是否可用
    if (!window.adTracker) {
      console.warn('⚠️ 杨宇程SDK未加载，返回模拟广告');
      return this.getMockAds(options.count || 1);
    }

    // 2. 确保杨宇程SDK已正确初始化
    const currentConfig = window.adTracker.config || {};
    if (!currentConfig.trackerServer || currentConfig.trackerServer.includes('localhost')) {
      console.warn('⚠️ 杨宇程SDK配置不正确，重新初始化...');
      try {
        window.adTracker.init({
          trackerServer: 'http://10.100.164.35:8084',
          website: 'shop'
        });
        console.log('✅ 重新初始化成功');
      } catch (e) {
        console.error('❌ 重新初始化失败:', e);
        return this.getMockAds(options.count || 1);
      }
    }

    // 3. 调用真实API
    try {
      const defaultOptions = {
        positions: options.positions || ['banner_top'],
        category: options.category || '',
        type: options.type || 'native',
        count: options.count || 1
      };

      console.log('📡 请求参数:', defaultOptions);

      const ads = await window.adTracker.getRecommendedAds(defaultOptions);

      if (!ads || ads.length === 0) {
        console.warn('⚠️ 广告API返回空数据，使用模拟广告');
        return this.getMockAds(options.count || 1);
      }

      console.log('✅ 获取真实广告成功，数量:', ads.length, '标题:', ads[0]?.title);
      return ads;
    } catch (error) {
      console.error('❌ 获取真实广告失败，使用模拟数据:', error.message);
      return this.getMockAds(options.count || 1);
    }
  }

  /**
   * 记录广告展示
   */
  async trackAdImpression(ad, position = 'banner_top') {
    if (!window.adTracker || !ad) {
      return null;
    }

    try {
      const impressionId = await window.adTracker.trackAdImpression(
        ad.id,
        position,
        ad.bidPrice || 0.5
      );

      this.impressionMap.set(ad.id, impressionId);
      console.log('📢 记录广告展示:', ad.id, 'impressionId:', impressionId);

      return impressionId;
    } catch (error) {
      console.error('记录广告展示失败:', error);
      return null;
    }
  }

  /**
   * 记录广告点击
   */
  trackAdClick(adId) {
    if (!window.adTracker || !adId) {
      return;
    }

    try {
      const impressionId = this.impressionMap.get(adId);
      if (impressionId) {
        window.adTracker.trackAdClick(impressionId);
        console.log('🖱️ 记录广告点击:', adId);
      } else {
        console.warn('未找到广告对应的impressionId:', adId);
        // 尝试直接使用adId
        window.adTracker.trackClick({
          targetId: `ad_${adId}`,
          category: 'advertisement',
          duration: 1
        });
      }
    } catch (error) {
      console.error('记录广告点击失败:', error);
    }
  }

  /**
   * 获取模拟广告
   */
  getMockAds(count = 1) {
    console.log('🎨 使用模拟广告，数量:', count);

    const mockAds = [
      {
        id: 1001,
        title: '双十一大促，全场5折起',
        description: '爆款商品限时抢购，错过等一年',
        imageUrl: 'https://picsum.photos/1200/300?random=promo',
        linkUrl: '#',
        bidPrice: 0.8,
        category: 'promotion'
      },
      {
        id: 1002,
        title: '新款智能手机',
        description: '旗舰配置，超长续航',
        imageUrl: 'https://picsum.photos/300/250?random=phone',
        linkUrl: '#',
        bidPrice: 0.6,
        category: 'electronics'
      },
      {
        id: 1003,
        title: '时尚服装特卖',
        description: '秋冬新款，温暖过冬',
        imageUrl: 'https://picsum.photos/300/250?random=fashion',
        linkUrl: '#',
        bidPrice: 0.5,
        category: 'fashion'
      },
      {
        id: 1004,
        title: '运动装备推荐',
        description: '专业运动装备，提升运动表现',
        imageUrl: 'https://picsum.photos/300/250?random=sports',
        linkUrl: '#',
        bidPrice: 0.4,
        category: 'sports'
      },
      {
        id: 1005,
        title: '家居生活用品',
        description: '提升生活品质的家居好物',
        imageUrl: 'https://picsum.photos/300/250?random=home',
        linkUrl: '#',
        bidPrice: 0.3,
        category: 'home'
      }
    ];

    return mockAds.slice(0, Math.min(count, mockAds.length));
  }

  /**
   * 生成浏览器指纹
   */
  getFingerprint() {
    if (window.adTracker && typeof window.adTracker.generateFingerprint === 'function') {
      return window.adTracker.generateFingerprint();
    }
    return 'test_fingerprint_' + Math.random().toString(36).substr(2, 9);
  }

  /**
   * 获取当前用户画像（模拟）
   */
  getUserProfile() {
    return {
      interests: ['electronics', 'fashion'],
      lastActive: new Date().toISOString(),
      totalViews: 15,
      totalClicks: 3
    };
  }
}

// 创建单例实例
const adTracker = new AdTracker();

export default adTracker;
