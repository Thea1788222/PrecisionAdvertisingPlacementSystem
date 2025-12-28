// 初始化SDK
adTracker.init({
    trackerServer: 'http://track.test.com:8084',
    website: 'shopping-website'
});

// 调试信息记录
let debugLog = [];
let stats = {
    pageViewCount: 0,
    clickCount: 0,
    searchCount: 0,
    adImpressionCount: 0,
    adClickCount: 0,
    recommendedAdCount: 0
};

// 当前浏览的商品信息
let currentViewedProduct = null;
let viewStartTime = null;
let viewTimer = null;

// 添加调试信息
function addDebugInfo(message) {
    const timestamp = new Date().toLocaleTimeString();
    debugLog.unshift(`[${timestamp}] ${message}`);
    if (debugLog.length > 20) debugLog.pop();
    document.getElementById('debugInfo').textContent = debugLog.join('\n');
}

// 更新统计
function updateStats() {
    document.getElementById('pageViewCount').textContent = stats.pageViewCount;
    document.getElementById('clickCount').textContent = stats.clickCount;
    document.getElementById('searchCount').textContent = stats.searchCount;
    document.getElementById('adImpressionCount').textContent = stats.adImpressionCount;
    document.getElementById('adClickCount').textContent = stats.adClickCount;
    document.getElementById('recommendedAdCount').textContent = stats.recommendedAdCount;
}

// 模拟商品数据
const products = [
    { id: 1, name: 'iPhone 15', price: 5999, category: 'electronics', rating: 5, emoji: '📱', description: '最新款iPhone，配备A17芯片，性能强劲' },
    { id: 2, name: 'MacBook Pro', price: 12999, category: 'electronics', rating: 5, emoji: '💻', description: '专业级笔记本电脑，适合设计师和开发者' },
    { id: 3, name: 'Nike 运动鞋', price: 899, category: 'fashion', rating: 4, emoji: '👟', description: '舒适透气的运动鞋，适合各种运动场景' },
    { id: 4, name: 'Adidas T恤', price: 299, category: 'fashion', rating: 4, emoji: '👕', description: '纯棉材质，舒适亲肤，时尚百搭' },
    { id: 5, name: '有机苹果', price: 29.9, category: 'food', rating: 5, emoji: '🍎', description: '新鲜有机苹果，富含维生素，健康美味' },
    { id: 6, name: '进口牛奶', price: 59.9, category: 'food', rating: 4, emoji: '🥛', description: '优质进口牛奶，营养丰富，口感醇厚' },
    { id: 7, name: '篮球', price: 199, category: 'sports', rating: 5, emoji: '🏀', description: '专业比赛用球，手感舒适，弹跳性好' },
    { id: 8, name: '瑜伽垫', price: 129, category: 'sports', rating: 4, emoji: '🧘', description: '环保材质瑜伽垫，防滑设计，保护关节' },
    { id: 9, name: '沙发', price: 2999, category: 'home', rating: 5, emoji: '🛋️', description: '真皮沙发，舒适耐用，提升客厅品味' },
    { id: 10, name: '餐具套装', price: 399, category: 'home', rating: 4, emoji: '🍽️', description: '不锈钢餐具套装，精美实用，适合家庭使用' },
    { id: 11, name: '编程书籍', price: 89, category: 'education', rating: 5, emoji: '📚', description: '深入浅出的编程指南，适合初学者' },
    { id: 12, name: '英语词典', price: 129, category: 'education', rating: 4, emoji: '📖', description: '权威英语词典，词汇量丰富，查询便捷' },
    { id: 13, name: '旅行背包', price: 399, category: 'travel', rating: 5, emoji: '🎒', description: '大容量旅行背包，防水材质，适合长途旅行' },
    { id: 14, name: '旅行箱', price: 899, category: 'travel', rating: 4, emoji: '🧳', description: '轻便耐用旅行箱，360度旋转轮子，出行首选' },
    { id: 15, name: '理财产品', price: 100, category: 'finance', rating: 4, emoji: '📈', description: '稳健型理财产品，年化收益率4.5%，低风险投资' },
    { id: 16, name: '保险套餐', price: 2999, category: 'finance', rating: 5, emoji: '🛡️', description: '全方位保障套餐，覆盖意外、医疗、财产安全' },
    { id: 17, name: '维生素C', price: 89, category: 'health', rating: 4, emoji: '💊', description: '高浓度维C补充剂，增强免疫力，抗氧化' },
    { id: 18, name: '按摩椅', price: 5999, category: 'health', rating: 5, emoji: '🪑', description: '家用智能按摩椅，全身按摩，舒缓疲劳' },
    { id: 19, name: '护肤套装', price: 599, category: 'beauty', rating: 5, emoji: '🧴', description: '全套护肤产品，深层清洁，滋润保湿' },
    { id: 20, name: '口红', price: 299, category: 'beauty', rating: 4, emoji: '💄', description: '持久显色口红，多种色号可选，打造完美妆容' },
    { id: 21, name: '智能手表', price: 2999, category: 'electronics', rating: 5, emoji: '⌚', description: '多功能智能手表，心率监测，运动追踪' },
    { id: 22, name: '无线耳机', price: 1299, category: 'electronics', rating: 4, emoji: '🎧', description: '降噪无线耳机，高音质享受，便携设计' },
    { id: 23, name: '连衣裙', price: 599, category: 'fashion', rating: 4, emoji: '👗', description: '优雅连衣裙，适合各种场合，彰显女性魅力' },
    { id: 24, name: '太阳镜', price: 799, category: 'fashion', rating: 5, emoji: '🕶️', description: '时尚太阳镜，UV防护，潮流必备单品' }
];

// 渲染商品
function renderProducts() {
    const grid1 = document.getElementById('productsGrid');
    const grid2 = document.getElementById('productsGrid2');
    
    grid1.innerHTML = '';
    grid2.innerHTML = '';
    
    products.slice(0, 6).forEach(product => {
        const productEl = createProductElement(product);
        grid1.appendChild(productEl);
    });
    
    products.slice(6).forEach(product => {
        const productEl = createProductElement(product);
        grid2.appendChild(productEl);
    });
}

// 创建商品元素
function createProductElement(product) {
    const div = document.createElement('div');
    div.className = 'product-card';
    div.dataset.productId = product.id;
    div.dataset.category = product.category;
    
    div.innerHTML = `
        <div class="product-image">${product.emoji}</div>
        <div class="product-info">
            <div class="product-title">${product.name}</div>
            <div class="product-price">¥${product.price}</div>
            <div class="product-rating">${'⭐'.repeat(product.rating)}</div>
        </div>
    `;
    
    // 添加点击事件追踪 - 这里追踪商品点击
    div.addEventListener('click', () => {
        showProductDetail(product);
        // 追踪商品点击，按照SDK集成指南中的click追踪方式
        trackClick({
            targetId: `product_${product.id}`,
            category: product.category,
            duration: 1
        });
    });
    
    return div;
}

// 显示商品详情
function showProductDetail(product) {
    const modal = document.getElementById('productModal');
    document.getElementById('modalTitle').textContent = product.name;
    document.getElementById('modalContent').innerHTML = `
        <p><strong>价格:</strong> ¥${product.price}</p>
        <p><strong>类别:</strong> ${product.category}</p>
        <p><strong>评分:</strong> ${'⭐'.repeat(product.rating)}</p>
        <p><strong>描述:</strong> ${product.description}</p>
        <button onclick="addToCart(${product.id})" style="margin-top: 10px; padding: 10px 20px; background: #3498db; color: white; border: none; border-radius: 5px; cursor: pointer;">加入购物车</button>
    `;
    modal.style.display = 'block';
    
    // 开始追踪商品浏览
    startProductViewTracking(product);
}

// 开始追踪商品浏览
function startProductViewTracking(product) {
    // 如果之前有正在追踪的商品，先记录
    if (currentViewedProduct) {
        recordProductView();
    }
    
    // 设置当前浏览的商品
    currentViewedProduct = product;
    viewStartTime = new Date();
    
    // 开始计时（每秒更新一次）
    if (viewTimer) clearInterval(viewTimer);
    viewTimer = setInterval(() => {
        // 检查页面是否可见
        if (!document.hidden) {
            const currentTime = new Date();
            const duration = Math.floor((currentTime - viewStartTime) / 1000); // 秒
            addDebugInfo(`正在浏览商品: ${product.name}, 已浏览 ${duration} 秒`);
        }
    }, 1000);
}

// 记录商品浏览
function recordProductView() {
    if (currentViewedProduct && viewStartTime) {
        const endTime = new Date();
        const duration = Math.floor((endTime - viewStartTime) / 1000); // 秒
        
        // 记录页面浏览，按照SDK集成指南中的view追踪方式
        trackPageView({
            targetId: `product_${currentViewedProduct.id}`,
            category: currentViewedProduct.category,
            duration: Math.max(1, Math.min(duration, 3600)) // 确保duration在1-3600秒之间
        });
        
        // 重置追踪变量
        currentViewedProduct = null;
        viewStartTime = null;
        if (viewTimer) {
            clearInterval(viewTimer);
            viewTimer = null;
        }
    }
}

// 追踪页面浏览 - 按照SDK集成指南
function trackPageView(options) {
    try {
        adTracker.trackPageView(options);
        stats.pageViewCount++;
        updateStats();
        addDebugInfo(`页面浏览记录: ${options.targetId}, 时长: ${options.duration}秒`);
    } catch (error) {
        addDebugInfo(`页面浏览记录失败: ${error.message}`);
    }
}

// 追踪点击 - 按照SDK集成指南
function trackClick(options) {
    try {
        adTracker.trackClick(options);
        stats.clickCount++;
        updateStats();
        addDebugInfo(`点击记录: ${options.targetId}, 类别: ${options.category}`);
    } catch (error) {
        addDebugInfo(`点击记录失败: ${error.message}`);
    }
}

// 追踪搜索 - 按照SDK集成指南
function trackSearch(options) {
    try {
        adTracker.trackSearch(options);
        stats.searchCount++;
        updateStats();
        addDebugInfo(`搜索记录: ${options.keywords}`);
    } catch (error) {
        addDebugInfo(`搜索记录失败: ${error.message}`);
    }
}

// 追踪广告展示 - 按照SDK集成指南
function trackAdImpression(adId, position, bidPrice = 1.00) {
    return adTracker.trackAdImpression(adId, position, bidPrice)
        .then(impressionId => {
            stats.adImpressionCount++;
            updateStats();
            addDebugInfo(`广告展示: 位置=${position}, ID=${adId}, 出价=${bidPrice}, 展示ID=${impressionId}`);
            return impressionId;
        })
        .catch(error => {
            addDebugInfo(`广告展示记录失败: ${error.message}`);
            throw error;
        });
}

// 追踪广告点击 - 按照SDK集成指南
function trackAdClick(impressionId) {
    try {
        adTracker.trackAdClick(impressionId);
        
        stats.adClickCount++;
        updateStats();
        addDebugInfo(`广告点击: 展示ID=${impressionId}`);
    } catch (error) {
        addDebugInfo(`广告点击记录失败: ${error.message}`);
    }
}

// 获取推荐广告 - 按照SDK集成指南
function getRecommendedAds() {
    try {
        adTracker.getRecommendedAds({
            positions: ['top-banner', 'sidebar', 'right-rail-1', 'right-rail-2', 'bottom-banner', 'feed'],
            category: '',
            type: 'native',
            count: 5
        }).then(ads => {
            stats.recommendedAdCount += ads.length;
            updateStats();
            addDebugInfo(`获取推荐广告成功: ${ads.length}个`);
            
            // 渲染推荐广告到各个广告位
            renderRecommendedAds(ads);
        }).catch(error => {
            addDebugInfo(`获取推荐广告失败: ${error.message}`);
        });
    } catch (error) {
        addDebugInfo(`获取推荐广告请求失败: ${error.message}`);
    }
}

// 渲染推荐广告
function renderRecommendedAds(ads) {
    if (ads && ads.length > 0) {
        // 顶部横幅广告
        if (ads[0]) {
            document.getElementById('topBannerContent').innerHTML = `
                <div class="ad-content">
                    ${ads[0].imageUrl ? `<img src="${ads[0].imageUrl}" alt="${ads[0].title || '广告'}" class="ad-image">` : ''}
                    <div class="ad-text">
                        <strong>${ads[0].title || '推荐广告'}</strong><br>
                        ${ads[0].description || '广告描述'}
                    </div>
                </div>
            `;
            // 添加点击事件追踪 - 按照SDK集成指南追踪广告点击
            document.getElementById('topBannerAd').onclick = (event) => {
                if (ads[0].impressionId) {
                    trackAdClick(ads[0].impressionId);
                }
            };
        }
        
        // 侧边栏广告
        if (ads[1]) {
            document.getElementById('sidebarContent').innerHTML = `
                <div class="ad-content">
                    ${ads[1].imageUrl ? `<img src="${ads[1].imageUrl}" alt="${ads[1].title || '广告'}" class="ad-image sidebar">` : ''}
                    <div class="ad-text">
                        <strong>${ads[1].title || '推荐广告'}</strong><br>
                        ${ads[1].description || '广告描述'}
                    </div>
                </div>
            `;
            document.getElementById('sidebarAd').onclick = (event) => {
                if (ads[1].impressionId) {
                    trackAdClick(ads[1].impressionId);
                }
            };
        }
        
        // 信息流广告
        if (ads[2]) {
            document.getElementById('feedContent').innerHTML = `
                <div class="ad-content">
                    ${ads[2].imageUrl ? `<img src="${ads[2].imageUrl}" alt="${ads[2].title || '广告'}" class="ad-image feed">` : ''}
                    <div class="ad-text">
                        <strong>${ads[2].title || '推荐广告'}</strong><br>
                        ${ads[2].description || '广告描述'}
                    </div>
                </div>
            `;
            document.getElementById('feedAd').onclick = (event) => {
                if (ads[2].impressionId) {
                    trackAdClick(ads[2].impressionId);
                }
            };
        }
        
        // 右侧广告1
        if (ads[3]) {
            document.getElementById('rightAd1Content').innerHTML = `
                <div class="ad-content">
                    ${ads[3].imageUrl ? `<img src="${ads[3].imageUrl}" alt="${ads[3].title || '广告'}" class="ad-image right">` : ''}
                    <div class="ad-text">
                        <strong>${ads[3].title || '推荐广告'}</strong><br>
                        ${ads[3].description || '广告描述'}
                    </div>
                </div>
            `;
            document.getElementById('rightAd1').onclick = (event) => {
                if (ads[3].impressionId) {
                    trackAdClick(ads[3].impressionId);
                }
            };
        }
        
        // 右侧广告2
        if (ads[4]) {
            document.getElementById('rightAd2Content').innerHTML = `
                <div class="ad-content">
                    ${ads[4].imageUrl ? `<img src="${ads[4].imageUrl}" alt="${ads[4].title || '广告'}" class="ad-image right">` : ''}
                    <div class="ad-text">
                        <strong>${ads[4].title || '推荐广告'}</strong><br>
                        ${ads[4].description || '广告描述'}
                    </div>
                </div>
            `;
            document.getElementById('rightAd2').onclick = (event) => {
                if (ads[4].impressionId) {
                    trackAdClick(ads[4].impressionId);
                }
            };
        }
        
        // 底部广告
        if (ads[5]) {
            document.getElementById('bottomContent').innerHTML = `
                <div class="ad-content">
                    ${ads[5].imageUrl ? `<img src="${ads[5].imageUrl}" alt="${ads[5].title || '广告'}" class="ad-image bottom">` : ''}
                    <div class="ad-text">
                        <strong>${ads[5].title || '推荐广告'}</strong><br>
                        ${ads[5].description || '广告描述'}
                    </div>
                </div>
            `;
            document.getElementById('bottomAd').onclick = (event) => {
                if (ads[5].impressionId) {
                    trackAdClick(ads[5].impressionId);
                }
            };
        }
        
        // 记录广告展示，确保参数符合SDK集成指南
        ads.forEach((ad, index) => {
            const position = ad.position || `position_${index + 1}`;
            trackAdImpression(ad.id || (index + 1), position, ad.bidPrice || 1.00)
                .then(impressionId => {
                    // 将impressionId存储在ad对象中，以便点击时使用
                    ad.impressionId = impressionId;
                })
                .catch(error => {
                    addDebugInfo(`记录广告展示失败: ${error.message}`);
                });
        });
    }
}

// 搜索功能 - 添加防抖处理
let searchTimeout;
document.getElementById('searchInput').addEventListener('input', (e) => {
    const query = e.target.value.trim();
    
    // 防抖处理，避免频繁搜索
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        if (query.length > 0) {
            // 追踪搜索事件 - 按照SDK集成指南
            trackSearch({
                targetId: 'search_input',
                keywords: query,
                duration: 2
            });
        }
    }, 300); // 300ms防抖
});

// 分类点击 - 按照SDK集成指南追踪分类点击（这也是一种点击行为）
document.querySelectorAll('.categories li').forEach(li => {
    li.addEventListener('click', () => {
        const category = li.dataset.category;
        trackClick({
            targetId: `category_${category}`,
            category: category,
            duration: 1
        });
    });
});

// 弹窗关闭功能
document.querySelector('.close').onclick = function() {
    document.getElementById('productModal').style.display = 'none';
    // 记录当前商品的浏览
    recordProductView();
};

window.onclick = function(event) {
    const modal = document.getElementById('productModal');
    if (event.target == modal) {
        modal.style.display = 'none';
        // 记录当前商品的浏览
        recordProductView();
    }
};

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    // 渲染商品
    renderProducts();
    
    // 获取推荐广告
    getRecommendedAds();
    
    // 页面可见性变化时记录浏览
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            // 页面不可见时，记录当前浏览的商品
            recordProductView();
            addDebugInfo('页面不可见（用户离开）');
        } else {
            addDebugInfo('页面可见（用户返回）');
        }
    });
    
    // 页面卸载前记录当前浏览的商品
    window.addEventListener('beforeunload', () => {
        recordProductView();
    });
    
    addDebugInfo('购物网站测试页面已加载完成');
    addDebugInfo(`用户指纹: ${adTracker.generateFingerprint()}`);
});

// 添加到购物车功能
function addToCart(productId) {
    trackClick({
        targetId: `add_to_cart_${productId}`,
        category: 'cart',
        duration: 1
    });
    
    // 模拟添加成功
    alert('已添加到购物车！');
}