<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="site-header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo">
          <h1>🛍️ 优购商城</h1>
        </div>

        <!-- 搜索框 -->
        <div class="search-container">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索商品/品牌"
              class="search-input"
              @keyup.enter="searchProducts"
            />
            <button class="search-btn" @click="searchProducts">
              <span class="search-icon">🔍</span> 搜索
            </button>
          </div>
          <div class="search-hotwords">
            <span>热门搜索：</span>
            <a href="#" @click.prevent="setSearchKeyword('iPhone')">iPhone 15</a>
            <a href="#" @click.prevent="setSearchKeyword('运动鞋')">运动鞋</a>
            <a href="#" @click.prevent="setSearchKeyword('羽绒服')">羽绒服</a>
            <a href="#" @click.prevent="setSearchKeyword('笔记本')">笔记本电脑</a>
          </div>
        </div>

        <!-- 用户操作 -->
        <div class="user-actions">
          <a href="#" class="action-item" @click.prevent="showCart">
            <span class="icon">🛒</span>
            <span class="text">购物车</span>
            <span class="badge" v-if="cartCount > 0">{{ cartCount }}</span>
          </a>
          <a href="#" class="action-item" @click.prevent="showLogin">
            <span class="icon">👤</span>
            <span class="text">我的账户</span>
          </a>
        </div>
      </div>
    </header>

    <!-- 主分类导航 -->
    <nav class="main-category">
      <div class="category-container">
        <a
          href="#"
          v-for="(name, key) in categoryMap"
          :key="key"
          @click.prevent="switchCategory(key)"
          :class="{ active: selectedCategory === key }"
          class="category-item"
        >
          {{ name }}
        </a>
      </div>
    </nav>

    <!-- 主要内容区 -->
    <main class="main-content">
      <!-- 横幅广告 -->
      <div class="banner-section">
        <div class="main-banner">
          <a href="#" @click.prevent="handleAdClick(bannerAd, '横幅')" class="banner-link">
            <img :src="bannerAd?.imageUrl || 'https://picsum.photos/1200/300?random=1'"
                 alt="广告"
                 class="banner-img" />
            <div class="banner-text">
              <span class="banner-tag">🔥 热门推荐</span>
              <h3>{{ bannerAd?.title || '超值商品限时抢购' }}</h3>
              <p>{{ bannerAd?.description || '爆款商品5折起，点击查看详情' }}</p>
            </div>
          </a>
        </div>
      </div>

      <!-- 商品列表区 -->
      <div class="product-section">
        <div class="section-header">
          <h2>猜你喜欢</h2>
          <div class="sort-options">
            <button @click="sortByPrice('asc')" :class="{ active: sortOption === 'price_asc' }">
              价格从低到高
            </button>
            <button @click="sortByPrice('desc')" :class="{ active: sortOption === 'price_desc' }">
              价格从高到低
            </button>
            <button @click="sortByPopularity()" :class="{ active: sortOption === 'popular' }">
              人气推荐
            </button>
          </div>
        </div>

        <!-- 商品网格 -->
        <div class="product-grid">
          <div
            v-for="(item, index) in getProductsWithAds()"
            :key="index"
            class="product-item-wrapper"
          >
            <!-- 商品卡片 -->
            <div v-if="item.type === 'product'" class="product-item">
              <div class="product-image">
                <img
                  :src="item.data.imageUrl || 'https://picsum.photos/280/280?random=' + index"
                  :alt="item.data.name"
                  class="product-img"
                  @click="viewProductDetail(item.data)"
                />
                <div class="product-tags">
                  <span v-if="item.data.price > 1000" class="tag premium">旗舰</span>
                  <span v-if="item.data.category === 'electronics'" class="tag hot">热销</span>
                  <span class="tag discount" v-if="item.data.price > 500">新品</span>
                </div>
                <button class="quick-cart" @click.stop="addToCart(item.data)">
                  <span>+</span>
                </button>
              </div>
              <div class="product-info">
                <h3 class="product-title" @click="viewProductDetail(item.data)">
                  {{ item.data.name }}
                </h3>
                <p class="product-desc">{{ truncateDescription(item.data.description) }}</p>
                <div class="product-price">
                  <div class="price-main">
                    <span class="currency">¥</span>
                    <span class="price">{{ item.data.price.toFixed(2) }}</span>
                  </div>
                  <div class="price-extra">
                    <span class="original-price" v-if="item.data.price > 500">
                      ¥{{ (item.data.price * 1.2).toFixed(2) }}
                    </span>
                    <span class="sold">已售{{ Math.floor(Math.random() * 1000) + 100 }}</span>
                  </div>
                </div>
                <div class="product-actions">
                  <button class="btn-view" @click.stop="viewProductDetail(item.data)">
                    查看详情
                  </button>
                  <button class="btn-cart" @click.stop="addToCart(item.data)">
                    加入购物车
                  </button>
                </div>
                <div class="seller-info">
                  <span class="seller">优购官方旗舰店</span>
                  <span class="rating">⭐ 4.9</span>
                </div>
              </div>
            </div>

            <!-- 信息流广告 -->
            <div v-else-if="item.type === 'ad'" class="feed-ad">
              <div class="ad-header">
                <span class="ad-badge">广告</span>
                <span class="ad-sponsor">品牌赞助</span>
              </div>
              <a href="#" @click.prevent="handleAdClick(item.data, '信息流')" class="ad-content">
                <img :src="item.data.imageUrl" alt="广告" class="ad-img" />
                <div class="ad-info">
                  <h4>{{ item.data.title }}</h4>
                  <p>{{ item.data.description || '官方正品，质量保证' }}</p>
                  <!-- 移除价格显示，只保留按钮 -->
                  <div class="ad-actions">
                    <span class="btn-buy">查看详情</span>
                  </div>
                </div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 商品详情弹窗 -->
    <div v-if="selectedProduct" class="product-modal" @click.self="selectedProduct = null">
      <div class="modal-container">
        <div class="modal-content">
          <button class="modal-close" @click="selectedProduct = null">×</button>

          <div class="modal-product">
            <div class="product-gallery">
              <img :src="selectedProduct.imageUrl" :alt="selectedProduct.name" class="main-image" />
              <div class="image-thumbs">
                <div class="thumb" v-for="n in 3" :key="n">
                  <img :src="`https://picsum.photos/80/80?random=${n}`" alt="" />
                </div>
              </div>
            </div>

            <div class="product-details">
              <h2 class="modal-title">{{ selectedProduct.name }}</h2>
              <div class="modal-subtitle">
                <span class="brand">优购官方旗舰店</span>
                <span class="rating">⭐ 4.9 分</span>
                <span class="sales">月销{{ Math.floor(Math.random() * 5000) + 1000 }}件</span>
              </div>

              <div class="modal-price">
                <div class="price-current">
                  <span class="price">¥{{ selectedProduct.price.toFixed(2) }}</span>
                  <span class="discount" v-if="selectedProduct.price > 500">
                    省¥{{ (selectedProduct.price * 0.2).toFixed(2) }}
                  </span>
                </div>
                <div class="price-original" v-if="selectedProduct.price > 500">
                  价格：<del>¥{{ (selectedProduct.price * 1.2).toFixed(2) }}</del>
                </div>
              </div>

              <div class="product-specs">
                <div class="spec-item">
                  <span class="spec-label">发货：</span>
                  <span class="spec-value">24小时内发货</span>
                </div>
                <div class="spec-item">
                  <span class="spec-label">快递：</span>
                  <span class="spec-value">包邮</span>
                </div>
                <div class="spec-item">
                  <span class="spec-label">服务：</span>
                  <span class="spec-value">7天无理由退货</span>
                </div>
              </div>

              <div class="product-actions-modal">
                <div class="quantity-selector">
                  <button @click="decreaseQuantity">-</button>
                  <input v-model="quantity" type="text" readonly />
                  <button @click="increaseQuantity">+</button>
                </div>
                <button class="btn-add-cart" @click="addToCartModal">
                  🛒 加入购物车
                </button>
                <button class="btn-buy-now" @click="buyProduct(selectedProduct)">
                  💳 立即购买
                </button>
              </div>

              <div class="product-description">
                <h4>商品描述</h4>
                <p>{{ selectedProduct.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车提示 -->
    <div v-if="showCartToast" class="cart-toast">
      🎉 商品已成功加入购物车！
    </div>

    <!-- 底部信息 -->
    <footer class="site-footer">
      <div class="footer-content">
        <div class="footer-links">
          <div class="link-group">
            <h4>购物指南</h4>
            <a href="#">购物流程</a>
            <a href="#">会员介绍</a>
            <a href="#">常见问题</a>
          </div>
          <div class="link-group">
            <h4>配送方式</h4>
            <a href="#">上门自提</a>
            <a href="#">配送服务查询</a>
            <a href="#">配送费收取标准</a>
          </div>
          <div class="link-group">
            <h4>支付方式</h4>
            <a href="#">货到付款</a>
            <a href="#">在线支付</a>
            <a href="#">分期付款</a>
          </div>
        </div>
        <div class="footer-copyright">
          <p>© 2024 优购商城 版权所有 | 客服电话：400-888-8888</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import adTracker from '@/utils/adTracker'

export default {
  name: 'HomeView',
  setup() {
    // 数据状态
    const products = ref([])
    const displayedProducts = ref([])
    const searchKeyword = ref('')
    const selectedCategory = ref('all')
    const selectedProduct = ref(null)
    const showCartToast = ref(false)
    const sortOption = ref('')
    const quantity = ref(1)
    const cartItems = ref([])


    // 广告数据
    const bannerAd = ref(null)
    const feedAds = ref([])

    // 防抖定时器变量
    let refreshTimeout = null

    const handleAdError = (error, context) => {
      console.error(`❌ ${context}失败:`, error)
      // 返回空数组，让调用方处理降级逻辑
      return []
    }

    // 分类映射
    const categoryMap = {
      'all': '全部商品',
      'electronics': '手机数码',
      'fashion': '服装鞋包',
      'sports': '运动户外',
      'food': '食品饮料',
      'home': '家居生活',
      'education': '图书教育'
    }

    // 计算属性
    const cartCount = computed(() => {
      return cartItems.value.reduce((total, item) => total + item.quantity, 0)
    })

    // 初始化广告追踪
    // 初始化广告追踪

    // 加载商品数据
    const loadProducts = async () => {
      try {
        const response = await fetch('http://10.100.164.23:8081/api/products')
        const data = await response.json()
        if (data.success) {
          products.value = data.data
          displayedProducts.value = data.data
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        products.value = getMockProducts()
        displayedProducts.value = products.value
      }
    }

    // 模拟数据 - 每个商品都有对应类别的真实图片
    const getMockProducts = () => {
      return [
        {
          id: 1,
          name: 'Apple iPhone 15 Pro Max 256GB',
          description: '全新钛金属设计，A17 Pro芯片，超视网膜XDR显示屏，4800万像素主摄像头',
          price: 9999.00,
          category: 'electronics',
          imageUrl: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 2,
          name: '华为MateBook X Pro 2024',
          description: '13代酷睿处理器，3.1K全面屏，超薄金属机身，专业办公笔记本',
          price: 8999.00,
          category: 'electronics',
          imageUrl: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 3,
          name: '男士冬季羽绒服',
          description: '90%白鸭绒填充，防风防水面料，保暖舒适，多色可选',
          price: 699.00,
          category: 'fashion',
          imageUrl: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 4,
          name: '耐克气垫跑步鞋',
          description: 'React缓震技术，透气网面，防滑耐磨，专业运动设计',
          price: 799.00,
          category: 'sports',
          imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 5,
          name: '进口坚果大礼包',
          description: '5种进口坚果组合，独立小包装，健康营养零食',
          price: 299.00,
          category: 'food',
          imageUrl: 'https://images.unsplash.com/photo-1626074353765-517a681e40be?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 6,
          name: '智能空气净化器',
          description: '高效HEPA滤网，智能感应，静音设计，适用60平米空间',
          price: 1999.00,
          category: 'home',
          imageUrl: 'https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 7,
          name: '全屋智能家居套装',
          description: '智能灯光+安防+环境控制，手机APP远程管理',
          price: 4999.00,
          category: 'home',
          imageUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 8,
          name: '索尼降噪耳机WH-1000XM5',
          description: '业界领先降噪技术，30小时续航，高清音质',
          price: 2499.00,
          category: 'electronics',
          imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 9,
          name: '运动智能手表',
          description: '心率监测，GPS定位，50米防水，运动模式',
          price: 1299.00,
          category: 'sports',
          imageUrl: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 10,
          name: '三星QLED 4K电视 75英寸',
          description: '量子点技术，HDR10+，智能语音控制，影院级视听体验',
          price: 8999.00,
          category: 'electronics',
          imageUrl: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 11,
          name: '女士时尚连衣裙',
          description: '夏季新款，透气面料，修身剪裁，多种颜色可选',
          price: 399.00,
          category: 'fashion',
          imageUrl: 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 12,
          name: '户外露营帐篷套装',
          description: '防水防风，4人帐，便携易搭建，附送睡袋',
          price: 1299.00,
          category: 'sports',
          imageUrl: 'https://images.unsplash.com/photo-1504851149312-7a075b496cc7?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 13,
          name: '有机牛奶礼盒装',
          description: '高原有机牧场，巴氏杀菌，高钙高蛋白，12盒装',
          price: 129.00,
          category: 'food',
          imageUrl: 'https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 14,
          name: '现代简约布艺沙发',
          description: '北欧风格，可拆洗布套，舒适靠背，三人位',
          price: 2999.00,
          category: 'home',
          imageUrl: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 15,
          name: '编程入门到精通',
          description: '全套编程教程，实战案例，适合零基础学习',
          price: 199.00,
          category: 'education',
          imageUrl: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 16,
          name: '小米智能扫地机器人',
          description: '激光导航，自动回充，APP控制，超强吸力',
          price: 1999.00,
          category: 'home',
          imageUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 17,
          name: '迪卡侬羽毛球拍套装',
          description: '碳纤维材质，专业级球拍，附送6个羽毛球',
          price: 499.00,
          category: 'sports',
          imageUrl: 'https://images.unsplash.com/photo-1516042438748-d5f43b6913ed?w=400&h=400&fit=crop&q=80'
        },
        {
          id: 18,
          name: '进口黑巧克力礼盒',
          description: '70%可可含量，纯手工制作，精美礼品包装',
          price: 199.00,
          category: 'food',
          imageUrl: 'https://images.unsplash.com/photo-1511381939415-e44015466834?w=400&h=400&fit=crop&q=80'
        }
      ]
    }
    // 加载广告
    const loadAds = async () => {
      try {
        // 优化：一次性获取所有广告（1次请求）
        const allAds = await adTracker.getRecommendedAds({
          positions: ['banner_top', 'product_feed', 'product_feed', 'product_feed'],
          type: 'native',
          count: 4  // 一次性获取4个广告
        }).catch(error => {
          // 使用辅助函数处理错误
          return handleAdError(error, '批量获取广告')
        })

        if (allAds && allAds.length > 0) {
          // 第一个广告作为横幅广告
          if (allAds.length > 0) {
            bannerAd.value = allAds[0]
            await adTracker.trackAdImpression(bannerAd.value, 'banner_top')
          }

          // 剩余3个广告作为信息流广告
          feedAds.value = allAds.slice(1, 4) || []
          feedAds.value.forEach((ad, index) => {
            adTracker.trackAdImpression(ad, `product_feed_${index}`)
          })

          console.log('✅ 批量获取广告成功:', {
            banner: bannerAd.value?.title,
            feedAds: feedAds.value.length
          })
        } else {
          console.warn('⚠️ 广告API返回空数据，使用模拟广告')
          setMockAds()
        }
      } catch (error) {
        console.error('❌ 加载广告失败:', error)
        setMockAds()
      }
    }

    // 新增：模拟广告设置函数
    const setMockAds = () => {
      bannerAd.value = {
        id: 'mock_banner',
        title: '年终大促，全场5折起',
        description: '爆款商品限时抢购，错过等一年',
        imageUrl: 'https://picsum.photos/1200/300?random=10'
      }
      feedAds.value = [
        {
          id: 'ad1',
          title: '大牌服饰限时特惠',
          description: '精选秋冬装，温暖过冬',
          imageUrl: 'https://picsum.photos/280/280?random=11'
        },
        {
          id: 'ad2',
          title: '智能家电节',
          description: '智能家居产品大促',
          imageUrl: 'https://picsum.photos/280/280?random=12'
        },
        {
          id: 'ad3',
          title: '运动户外装备',
          description: '专业运动装备推荐',
          imageUrl: 'https://picsum.photos/280/280?random=13'
        }
      ]
    }

    // 智能刷新函数
    const smartRefreshAds = () => {
      // 1. 清除之前的定时器
      if (refreshTimeout) {
        clearTimeout(refreshTimeout)
      }

      // 2. 设置新的定时器（1.5秒后刷新）
      refreshTimeout = setTimeout(() => {
        console.log('🔄 用户停止操作，开始刷新广告...')
        refreshAds()  // 调用刷新函数
      }, 1500)  // 1500毫秒 = 1.5秒
    }


    // 广告刷新函数
    const refreshAds = async () => {
      console.log('🔄 正在刷新广告...')

      try {
        // 一次性获取4个广告
        const allAds = await adTracker.getRecommendedAds({
          positions: ['banner_top', 'product_feed', 'product_feed', 'product_feed'],
          type: 'native',
          count: 4
        }).catch(error => {
          // 使用辅助函数处理错误
          return handleAdError(error, '刷新广告')
        })

        if (allAds && allAds.length > 0) {
          // 更新横幅广告
          if (allAds.length > 0) {
            bannerAd.value = allAds[0]
            await adTracker.trackAdImpression(bannerAd.value, 'banner_top')
            console.log('✅ 横幅广告已刷新:', bannerAd.value.title)
          }

          // 更新信息流广告
          feedAds.value = allAds.slice(1, 4)
          feedAds.value.forEach((ad, index) => {
            adTracker.trackAdImpression(ad, `product_feed_${index}`)
          })

          console.log('✅ 信息流广告已刷新:', feedAds.value.length, '个')
        } else {
          console.warn('⚠️ 刷新广告返回空数据')
        }
      } catch (error) {
        console.error('❌ 刷新广告失败:', error)
      }
    }

    // 搜索功能
    const searchProducts = async () => {
      if (!searchKeyword.value.trim()) {
        displayedProducts.value = products.value
        return
      }

      adTracker.trackSearch(searchKeyword.value);

      try {
        const response = await fetch(`http://10.100.164.23:8081/api/products/search?keyword=${encodeURIComponent(searchKeyword.value)}`)
        const data = await response.json()
        if (data.success) {
          displayedProducts.value = data.data
        }
      } catch (error) {
        console.error('搜索失败:', error)
        const keyword = searchKeyword.value.toLowerCase()
        displayedProducts.value = products.value.filter(p =>
          p.name.toLowerCase().includes(keyword) ||
          p.description.toLowerCase().includes(keyword) ||
          p.category.toLowerCase().includes(keyword)
        )
      }
    }

    // 设置搜索关键词
    const setSearchKeyword = (keyword) => {
      searchKeyword.value = keyword
      searchProducts()
    }

    // 切换分类
    const switchCategory = (category) => {
      selectedCategory.value = category
      if (category === 'all') {
        displayedProducts.value = products.value
      } else {
        displayedProducts.value = products.value.filter(p => p.category === category)
      }
    }

    // 查看商品详情
    const viewProductDetail = (product) => {
      selectedProduct.value = product
      quantity.value = 1
      adTracker.trackProductView(product);

      // 🔥 在这里添加这一行 ↓
      smartRefreshAds()  // 用户浏览商品也触发刷新
    }

    // 加入购物车
    const addToCart = (product) => {
      //广告追踪
      adTracker.trackProductClick(product);

      // 原有的购物车逻辑...
      const existingItem = cartItems.value.find(item => item.id === product.id)
      if (existingItem) {
        existingItem.quantity += 1
      } else {
        cartItems.value.push({
          ...product,
          quantity: 1
        })
      }

      showCartToast.value = true
      setTimeout(() => {
        showCartToast.value = false
      }, 2000)

      console.log('购物车:', cartItems.value)
      // 🔥 在这里添加这一行 ↓
      smartRefreshAds()  // 用户点击商品后触发智能刷新

      // 🔥 新增：用户点击后刷新广告
      console.log(`🎯 用户点击${product.name}，1秒后刷新广告`)

      // 延迟1秒刷新，给后端处理时间
      setTimeout(() => {
        refreshAds()  // 这里使用了refreshAds，警告会消失
      }, 1000)
    }

    const addToCartModal = () => {
      if (selectedProduct.value) {
        addToCart(selectedProduct.value)
      }
    }

    // 购买商品
    const buyProduct = (product) => {
      console.log('购买商品:', product.id)
      alert(`感谢购买 ${product.name}`)
      selectedProduct.value = null
    }

    // 购物车操作
    const showCart = () => {
      alert(`购物车中有 ${cartCount.value} 件商品，功能开发中...`)
      adTracker.trackUserBehavior({
        actionType: 'click',
        targetId: 'cart_button',
        category: 'navigation'
      });
    }

    // 登录弹窗
    const showLogin = () => {
      alert('登录功能开发中...')
    }

    // 排序功能
    const sortByPrice = (order) => {
      sortOption.value = `price_${order}`
      const sorted = [...displayedProducts.value].sort((a, b) => {
        return order === 'asc' ? a.price - b.price : b.price - a.price
      })
      displayedProducts.value = sorted
    }

    const sortByPopularity = () => {
      sortOption.value = 'popular'
      displayedProducts.value = [...displayedProducts.value].sort(() => Math.random() - 0.5)
    }

    // 数量选择
    const increaseQuantity = () => {
      quantity.value += 1
    }

    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value -= 1
      }
    }

    // 广告点击处理
    const handleAdClick = (ad, adType) => {
      if (ad.id) {
        adTracker.trackAdClick(ad.id);
      }
      console.log(`${adType}广告被点击:`, ad.title);
    }

    // 在商品列表中插入信息流广告
    const getProductsWithAds = () => {
      const result = [];
      let adIndex = 0;

      for (let i = 0; i < displayedProducts.value.length; i++) {
        result.push({
          type: 'product',
          data: displayedProducts.value[i]
        });

        // 每4个商品插入一个广告
        if ((i + 1) % 4 === 0 && adIndex < feedAds.value.length) {
          result.push({
            type: 'ad',
            data: feedAds.value[adIndex]
          });
          adIndex++;
        }
      }

      return result;
    }

    // 截断描述文本
    const truncateDescription = (desc) => {
      if (!desc) return ''
      return desc.length > 50 ? desc.substring(0, 50) + '...' : desc
    }

    // 页面加载时初始化
// 删除整个 initAdTracker 函数，改为：

    onMounted(async () => {
      console.log('🚀 页面加载开始...')

      try {
        // 1. 初始化广告系统
        console.log('1. 初始化广告系统...')
        const initialized = adTracker.init()
        if (initialized) {
          console.log('✅ 广告追踪初始化成功')
        }

        // 2. 加载商品
        console.log('2. 加载商品数据...')
        await loadProducts()

        // 3. 加载广告
        console.log('3. 加载广告...')
        await loadAds()

        // 4. 加载购物车
        console.log('4. 恢复购物车...')
        const savedCart = localStorage.getItem('shopping_cart')
        if (savedCart) {
          cartItems.value = JSON.parse(savedCart)
        }

        console.log('✅ 所有初始化完成')

      } catch (error) {
        console.error('❌ 页面初始化失败:', error)
      }
    })

    return {
      products,
      displayedProducts,
      searchKeyword,
      selectedCategory,
      selectedProduct,
      showCartToast,
      sortOption,
      quantity,
      cartCount,
      bannerAd,
      feedAds,
      categoryMap,
      searchProducts,
      setSearchKeyword,
      switchCategory,
      viewProductDetail,
      addToCart,
      addToCartModal,
      buyProduct,
      showCart,
      showLogin,
      sortByPrice,
      sortByPopularity,
      increaseQuantity,
      decreaseQuantity,
      handleAdClick,
      getProductsWithAds,
      truncateDescription
    }
  }
}
</script>

<style scoped>
/* 首页容器 */
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  width: 100%;
}

/* 顶部导航栏 - 全宽 */
.site-header {
  background: #ff4400;
  color: white;
  padding: 10px 0;
  width: 100%;
}

.header-container {
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;  /* 改为100% */
  box-sizing: border-box;  /* 添加这个 */
}

.logo h1 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: white;
  white-space: nowrap;
}

.search-container {
  flex: 1;
  margin: 0 40px;
  max-width: 800px;
}

.search-box {
  display: flex;
  background: white;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width:100%;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: none;
  font-size: 14px;
  outline: none;
  min-width: 200px;
}

.search-btn {
  padding: 0 24px;
  background: #ff6b00;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;
  white-space: nowrap;
}

.search-btn:hover {
  background: #e55a00;
}

.search-hotwords {
  margin-top: 8px;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.search-hotwords span {
  opacity: 0.8;
}

.search-hotwords a {
  color: white;
  text-decoration: none;
  margin: 0 8px;
  opacity: 0.9;
  white-space: nowrap;
}

.search-hotwords a:hover {
  opacity: 1;
  text-decoration: underline;
}

.user-actions {
  display: flex;
  gap: 20px;
  white-space: nowrap;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  text-decoration: none;
  font-size: 12px;
  position: relative;
}

.action-item .icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.action-item .badge {
  position: absolute;
  top: -5px;
  right: 0;
  background: white;
  color: #ff4400;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: bold;
  min-width: 16px;
  text-align: center;
}

/* 主分类导航 - 全宽 */
.main-category {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: 100%;
}

.category-container {
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  overflow-x: auto;
  width: 100%;  /* 改为100% */
  box-sizing: border-box;  /* 添加这个 */
  scrollbar-width: thin;
}

.category-item {
  padding: 16px 20px;
  color: #333;
  text-decoration: none;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s;
  border-bottom: 3px solid transparent;
  flex-shrink: 0;
}

.category-item:hover {
  color: #ff4400;
}

.category-item.active {
  color: #ff4400;
  border-bottom-color: #ff4400;
  font-weight: bold;
}

/* 主要内容区 - 全宽且居中 */
.main-content {
  flex: 1;
  width: 100%;
  padding: 20px 0;
  box-sizing: border-box;
}

/* 主要内容区 - 全宽 */
.main-content {
  flex: 1;
  width: 100%;
  padding: 20px 0;
  box-sizing: border-box;
}

/* 横幅广告 - 全宽容器，内容居中 */
.banner-section {
  width: 100%;
  margin-bottom: 30px;
  padding: 0;  /* 改为0，移除左右内边距 */
  box-sizing: border-box;
}

.main-banner {
  border-radius: 0;  /* 改为0，去掉圆角 */
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 100%;  /* 改为100% */
  margin: 0;
}

.banner-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  display: block;
}

.banner-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  padding: 30px;
  color: white;
}

.banner-tag {
  background: #ff4400;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 10px;
  display: inline-block;
}

.banner-text h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: bold;
}

.banner-text p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

/* 商品列表区 - 全宽容器 */
.product-section {
  background: white;
  border-radius: 0;  /* 改为0，去掉圆角 */
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: 100%;  /* 改为100% */
  margin: 0;  /* 改为0，移除自动居中 */
  box-sizing: border-box;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.sort-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sort-options button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.sort-options button:hover {
  border-color: #ff4400;
  color: #ff4400;
}

.sort-options button.active {
  background: #ff4400;
  border-color: #ff4400;
  color: white;
}

/* 商品网格 - 自适应列数 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 20px;
  width: 100%;
}

.product-item-wrapper {
  position: relative;
}

/* 商品卡片 - 固定高度，布局更紧凑 */
.product-item {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 380px;
}

.product-item:hover {
  border-color: #ff4400;
  box-shadow: 0 4px 12px rgba(255,68,0,0.1);
  transform: translateY(-4px);
}

.product-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-item:hover .product-img {
  transform: scale(1.05);
}

.product-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: bold;
  color: white;
}

.tag.premium {
  background: #ff4400;
}

.tag.hot {
  background: #ff6b00;
}

.tag.discount {
  background: #52c41a;
}

.quick-cart {
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: white;
  border: 1px solid #ddd;
  color: #ff4400;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.quick-cart:hover {
  background: #ff4400;
  color: white;
  border-color: #ff4400;
}

.product-info {
  padding: 15px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.product-title {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  cursor: pointer;
  transition: color 0.2s;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-title:hover {
  color: #ff4400;
}

.product-desc {
  color: #999;
  font-size: 12px;
  margin: 0 0 12px 0;
  line-height: 1.4;
  height: 34px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-price {
  margin-bottom: 15px;
  flex-shrink: 0;
}

.price-main {
  display: flex;
  align-items: baseline;
  margin-bottom: 4px;
}

.currency {
  color: #ff4400;
  font-size: 14px;
  font-weight: bold;
}

.price {
  color: #ff4400;
  font-size: 20px;
  font-weight: bold;
  margin-left: 2px;
}

.price-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.sold {
  color: #999;
  font-size: 12px;
}

.product-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  margin-top: auto;
}

.btn-view, .btn-cart {
  flex: 1;
  padding: 8px 0;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-view:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-cart:hover {
  border-color: #ff4400;
  color: #ff4400;
}

.seller-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
  padding-top: 10px;
  border-top: 1px solid #eee;
  flex-shrink: 0;
}

.seller {
  color: #666;
}

.rating {
  color: #ff6b00;
}

/* 信息流广告 */
.feed-ad {
  background: linear-gradient(135deg, #f8f8f8 0%, #fff 100%);
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  height: 380px;
  display: flex;
  flex-direction: column;
}

.feed-ad:hover {
  border-color: #ff4400;
  box-shadow: 0 4px 12px rgba(255,68,0,0.1);
}

.ad-header {
  padding: 8px 15px;
  background: #f8f8f8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.ad-badge {
  background: #ff4400;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: bold;
}

.ad-sponsor {
  color: #666;
  font-size: 10px;
}

.ad-content {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: inherit;
  padding: 15px;
  flex: 1;
}

.ad-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.ad-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.ad-info h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
}

.ad-info p {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 12px;
  line-height: 1.4;
  flex: 1;
}

.ad-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.ad-actions .btn-buy {
  background: #ff4400;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.ad-actions .btn-buy:hover {
  background: #e63c00;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 68, 0, 0.3);
}

/* 商品详情弹窗 */
.product-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
  box-sizing: border-box;
}

.modal-container {
  background: white;
  border-radius: 8px;
  max-width: 1000px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-sizing: border-box;
}

.modal-content {
  padding: 30px;
  position: relative;
  box-sizing: border-box;
}

.modal-close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #ff4400;
  color: white;
}

.modal-product {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.product-gallery .main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}

.image-thumbs {
  display: flex;
  gap: 10px;
}

.thumb {
  width: 80px;
  height: 80px;
  border: 2px solid #eee;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-title {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #333;
  font-weight: bold;
  word-break: break-word;
}

.modal-subtitle {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
  font-size: 14px;
  color: #666;
  flex-wrap: wrap;
}

.brand {
  color: #ff4400;
  font-weight: bold;
}

.modal-price {
  background: #f8f8f8;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
}

.price-current {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 8px;
}

.price-current .price {
  color: #ff4400;
  font-size: 32px;
  font-weight: bold;
}

.discount {
  background: #ff4400;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.price-original {
  color: #999;
  font-size: 14px;
}

.product-specs {
  display: grid;
  gap: 10px;
  margin-bottom: 25px;
}

.spec-item {
  display: flex;
  align-items: center;
}

.spec-label {
  color: #666;
  font-size: 14px;
  width: 60px;
  flex-shrink: 0;
}

.spec-value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.product-actions-modal {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.quantity-selector {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.quantity-selector button {
  width: 36px;
  height: 36px;
  border: none;
  background: #f8f8f8;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.quantity-selector button:hover {
  background: #eee;
}

.quantity-selector input {
  width: 50px;
  height: 36px;
  border: none;
  text-align: center;
  font-size: 14px;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

.btn-add-cart, .btn-buy-now {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 150px;
}

.btn-add-cart {
  background: #ff9500;
  color: white;
}

.btn-add-cart:hover {
  background: #e68500;
}

.btn-buy-now {
  background: #ff4400;
  color: white;
}

.btn-buy-now:hover {
  background: #e63c00;
}

.product-description h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.product-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

/* 购物车提示 */
.cart-toast {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: #52c41a;
  color: white;
  padding: 15px 30px;
  border-radius: 25px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(82,196,26,0.3);
  z-index: 1001;
  animation: slideUp 0.3s ease;
  white-space: nowrap;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

/* 底部信息 - 全宽 */
.site-footer {
  background: #333;
  color: white;
  margin-top: 40px;
  padding: 40px 0 20px;
  width: 100%;
}

.footer-content {
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;  /* 改为100% */
  box-sizing: border-box;  /* 添加这个 */
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 30px;
}

.link-group h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.link-group a {
  display: block;
  color: #ccc;
  text-decoration: none;
  margin-bottom: 8px;
  font-size: 14px;
}

.link-group a:hover {
  color: white;
}

.footer-copyright {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #444;
  color: #999;
  font-size: 12px;
}

/* 响应式设计优化 */
@media (min-width: 1400px) {
  .product-grid {
    grid-template-columns: repeat(6, 1fr);
  }

  .header-container,
  .category-container,
  .product-section {
    max-width: 1400px;
  }
}

@media (min-width: 1600px) {
  .product-grid {
    grid-template-columns: repeat(7, 1fr);
  }
}

@media (min-width: 1800px) {
  .product-grid {
    grid-template-columns: repeat(8, 1fr);
  }
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .modal-product {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-container {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .search-container {
    margin: 0;
    width: 100%;
    max-width: 100%;
  }

  .user-actions {
    justify-content: center;
  }

  .category-container {
    padding: 0 10px;
  }

  .category-item {
    padding: 12px 15px;
    font-size: 13px;
  }

  .product-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .sort-options {
    width: 100%;
    overflow-x: auto;
    padding-bottom: 5px;
  }

  .product-item,
  .feed-ad {
    height: 350px;
  }

  .product-image {
    height: 150px;
  }

  .ad-img {
    height: 100px;
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .product-item,
  .feed-ad {
    height: 320px;
  }

  .product-image {
    height: 140px;
  }

  .product-info {
    padding: 12px;
  }

  .product-title {
    font-size: 13px;
    height: 36px;
  }

  .price {
    font-size: 18px;
  }

  .footer-links {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .product-actions-modal {
    flex-direction: column;
  }

  .quantity-selector {
    width: 100%;
    justify-content: center;
  }

  .btn-add-cart, .btn-buy-now {
    width: 100%;
    min-width: unset;
  }
}

@media (max-width: 400px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .product-item,
  .feed-ad {
    max-width: 280px;
    margin: 0 auto;
  }

  .header-container {
    padding: 0 10px;
  }

  .logo h1 {
    font-size: 22px;
    text-align: center;
  }

  .search-box {
    flex-direction: column;
  }

  .search-input {
    padding: 10px 12px;
  }

  .search-btn {
    padding: 10px 0;
    justify-content: center;
  }
}

/* 滚动条美化 */
.category-container::-webkit-scrollbar {
  height: 6px;
}

.category-container::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.category-container::-webkit-scrollbar-thumb {
  background: #ff4400;
  border-radius: 3px;
}

.category-container::-webkit-scrollbar-thumb:hover {
  background: #e63c00;
}

/* 模态框滚动条 */
.modal-container::-webkit-scrollbar {
  width: 8px;
}

.modal-container::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.modal-container::-webkit-scrollbar-thumb {
  background: #ff4400;
  border-radius: 4px;
}

.modal-container::-webkit-scrollbar-thumb:hover {
  background: #e63c00;
}
</style>
