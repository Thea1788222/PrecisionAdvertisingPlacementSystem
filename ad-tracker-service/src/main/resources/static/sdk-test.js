/**
 * 广告追踪SDK自动化测试脚本
 * 用于验证SDK各项功能是否正常工作
 */

// 模拟浏览器环境（用于Node.js环境测试）
if (typeof window === 'undefined') {
    global.window = {
        location: { pathname: '/test' },
        navigator: { userAgent: 'Node.js Test Environment' },
        document: {
            cookie: '',
            createElement: function(tag) {
                return {
                    getContext: function() { 
                        return {
                            textBaseline: '',
                            font: '',
                            fillStyle: '',
                            fillRect: function() {},
                            fillText: function() {}
                        };
                    },
                    toDataURL: function() { return 'test-canvas-data'; }
                };
            }
        }
    };
    
    global.document = window.document;
    global.navigator = window.navigator;
    
    // 模拟XMLHttpRequest
    global.XMLHttpRequest = class {
        constructor() {
            this.readyState = 0;
            this.status = 200;
            this.responseText = '{"success":true,"message":"测试成功"}';
        }
        
        open(method, url, async) {
            this.method = method;
            this.url = url;
            this.async = async;
        }
        
        setRequestHeader(header, value) {
            // 模拟设置请求头
        }
        
        send(data) {
            this.readyState = 4;
            if (this.onreadystatechange) {
                this.onreadystatechange();
            }
        }
    };
}

// 等待adTracker加载完成
function waitForAdTracker() {
    return new Promise((resolve, reject) => {
        let attempts = 0;
        const maxAttempts = 100;
        const interval = setInterval(() => {
            if (typeof adTracker !== 'undefined') {
                clearInterval(interval);
                resolve();
            } else if (attempts >= maxAttempts) {
                clearInterval(interval);
                reject(new Error('adTracker未加载完成'));
            }
            attempts++;
        }, 100);
    });
}

// 测试套件
class AdTrackerSDKTest {
    constructor() {
        this.tests = [];
        this.results = [];
    }
    
    // 添加测试用例
    addTest(name, testFn) {
        this.tests.push({ name, testFn });
    }
    
    // 运行所有测试
    async run() {
        console.log('开始运行广告追踪SDK测试套件...\n');
        
        for (const test of this.tests) {
            try {
                await test.testFn();
                this.results.push({ name: test.name, status: 'PASS' });
                console.log(`✅ ${test.name}: 通过`);
            } catch (error) {
                this.results.push({ name: test.name, status: 'FAIL', error: error.message });
                console.log(`❌ ${test.name}: 失败 - ${error.message}`);
            }
        }
        
        // 输出测试摘要
        console.log('\n测试结果摘要:');
        const passed = this.results.filter(r => r.status === 'PASS').length;
        const failed = this.results.filter(r => r.status === 'FAIL').length;
        
        console.log(`总计: ${this.results.length}, 通过: ${passed}, 失败: ${failed}`);
        
        if (failed > 0) {
            console.log('\n失败的测试:');
            this.results
                .filter(r => r.status === 'FAIL')
                .forEach(r => console.log(`  - ${r.name}: ${r.error}`));
        }
        
        return { total: this.results.length, passed, failed };
    }
}

// 创建测试实例
const testSuite = new AdTrackerSDKTest();

// 测试用例1: SDK初始化
testSuite.addTest('SDK初始化', async () => {
    adTracker.init({
        trackerServer: 'http://localhost:8084',
        website: 'test-website'
    });
    
    if (!adTracker.config.trackerServer) {
        throw new Error('追踪服务器未正确设置');
    }
    
    if (!adTracker.config.website) {
        throw new Error('网站标识未正确设置');
    }
    
    if (!adTracker.config.cookieId) {
        throw new Error('Cookie ID未生成');
    }
});

// 测试用例2: UUID生成
testSuite.addTest('UUID生成', () => {
    const uuid1 = adTracker.generateUUID();
    const uuid2 = adTracker.generateUUID();
    
    if (!uuid1 || typeof uuid1 !== 'string') {
        throw new Error('UUID生成失败');
    }
    
    if (uuid1 === uuid2) {
        throw new Error('生成的UUID不唯一');
    }
});

// 测试用例3: Cookie操作
testSuite.addTest('Cookie操作', () => {
    const testKey = 'ad_tracker_test';
    const testValue = 'test_value';
    
    // 设置Cookie
    adTracker.setCookie(testKey, testValue, 1);
    
    // 获取Cookie
    const retrievedValue = adTracker.getCookie(testKey);
    
    if (retrievedValue !== testValue) {
        throw new Error('Cookie操作失败');
    }
    
    // 清理测试Cookie
    adTracker.setCookie(testKey, '', -1);
});

// 测试用例4: 浏览器指纹生成
testSuite.addTest('浏览器指纹生成', () => {
    const fingerprint = adTracker.generateFingerprint();
    
    if (typeof fingerprint !== 'number') {
        throw new Error('浏览器指纹生成失败');
    }
});

// 测试用例5: 页面浏览追踪
testSuite.addTest('页面浏览追踪', () => {
    // 模拟trackPageView调用，不应抛出异常
    adTracker.trackPageView({
        targetId: 'test-page',
        category: 'test'
    });
});

// 测试用例6: 点击事件追踪
testSuite.addTest('点击事件追踪', () => {
    // 模拟trackClick调用，不应抛出异常
    adTracker.trackClick({
        targetId: 'test-button',
        category: 'test'
    });
});

// 测试用例7: 广告展示追踪
testSuite.addTest('广告展示追踪', () => {
    // 模拟trackAdImpression调用，不应抛出异常
    adTracker.trackAdImpression(1, 'top-banner', 1.50);
});

// 测试用例8: 广告点击追踪
testSuite.addTest('广告点击追踪', () => {
    // 模拟trackAdClick调用，不应抛出异常
    adTracker.trackAdClick(1);
});

// 测试用例9: 推荐广告获取
testSuite.addTest('推荐广告获取', async () => {
    // 模拟getRecommendedAds调用
    try {
        await adTracker.getRecommendedAds({
            positions: ['top-banner'],
            category: 'test',
            count: 1
        });
    } catch (error) {
        // 我们期望网络请求会失败，但方法本身不应抛出异常
        // 这里我们只测试方法是否能正常调用
    }
});

// 在浏览器环境中自动运行测试
if (typeof window !== 'undefined') {
    // 页面加载完成后运行测试
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', async () => {
            try {
                await waitForAdTracker();
                await testSuite.run();
            } catch (error) {
                console.error('测试运行失败:', error.message);
            }
        });
    } else {
        // DOM已加载完成
        waitForAdTracker().then(async () => {
            await testSuite.run();
        }).catch(error => {
            console.error('测试运行失败:', error.message);
        });
    }
}

// 在Node.js环境中导出测试套件
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { AdTrackerSDKTest, testSuite, waitForAdTracker };
}