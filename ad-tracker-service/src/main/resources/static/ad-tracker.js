/**
 * 广告追踪SDK
 * 提供用户行为追踪、广告展示与点击追踪、推荐广告获取等功能
 */

(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
    typeof define === 'function' && define.amd ? define(factory) :
    (global.adTracker = factory());
}(this, (function () { 'use strict';

    var adTracker = {
        config: {
            trackerServer: '',
            website: '',
            domain: null
        },

        /**
         * 初始化SDK
         * @param {Object} options - 配置选项
         * @param {string} options.trackerServer - 追踪服务地址
         * @param {string} options.website - 网站类别，shop、video、news
         */
        init: function(options) {
            this.config.trackerServer = options.trackerServer || '';
            this.config.website = options.website || '';
        },

        /**
         * 生成浏览器指纹
         */
        generateFingerprint: function() {
            // 收集多种浏览器和设备特征
            var features = [];
            
            // 基础特征
            features.push(navigator.userAgent);
            features.push(navigator.language);
            features.push(navigator.platform);
            features.push(screen.colorDepth);
            features.push(screen.width + "x" + screen.height);
            features.push(screen.availWidth + "x" + screen.availHeight);
            features.push(new Date().getTimezoneOffset());
            features.push(!!window.sessionStorage);
            features.push(!!window.localStorage);
            features.push(typeof indexedDB !== "undefined");
            
            // Canvas指纹
            var canvas = document.createElement('canvas');
            var ctx = canvas.getContext('2d');
            var txt = 'https://github.com/lingmaoffice';
            ctx.textBaseline = "top";
            ctx.font = "14px 'Arial'";
            ctx.textBaseline = "alphabetic";
            ctx.fillStyle = "#f60";
            ctx.fillRect(125,1,62,20);
            ctx.fillStyle = "#069";
            ctx.fillText(txt, 2, 15);
            ctx.fillStyle = "rgba(102, 204, 0, 0.7)";
            ctx.fillText(txt, 4, 17);
            features.push(canvas.toDataURL());
            
            // WebGL指纹
            try {
                var gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
                if (gl) {
                    features.push(gl.getParameter(gl.RENDERER));
                    features.push(gl.getParameter(gl.VENDOR));
                    var exts = gl.getSupportedExtensions();
                    features.push(exts ? exts.join(';') : '');
                }
            } catch(e) {
                features.push("webgl_not_supported");
            }
            
            // AudioContext指纹
            try {
                var audioCtx = new (window.AudioContext || window.webkitAudioContext)();
                var oscillator = audioCtx.createOscillator();
                var analyser = audioCtx.createAnalyser();
                var gain = audioCtx.createGain();
                var scriptProcessor = audioCtx.createScriptProcessor(4096, 1, 1);
                
                oscillator.type = "triangle";
                oscillator.connect(analyser);
                analyser.connect(scriptProcessor);
                scriptProcessor.connect(audioCtx.destination);
                oscillator.start(0);
                
                var that = this;
                setTimeout(function() {
                    oscillator.stop();
                    audioCtx.close();
                }, 100);
                
                features.push("audio_context_available");
            } catch(e) {
                features.push("audio_context_not_supported");
            }
            
            // 硬件特征
            features.push(navigator.hardwareConcurrency || "unknown");
            features.push(navigator.deviceMemory || "unknown");
            features.push('ontouchstart' in window);
            
            // 浏览器插件信息
            try {
                var plugins = [];
                for (var i = 0; i < navigator.plugins.length; i++) {
                    plugins.push(navigator.plugins[i].name + "::" + navigator.plugins[i].filename + "::" + navigator.plugins[i].description);
                }
                features.push(plugins.join(";"));
            } catch(e) {
                features.push("plugins_unavailable");
            }
            
            // MIME类型
            try {
                var mimeTypes = [];
                for (var i = 0; i < navigator.mimeTypes.length; i++) {
                    mimeTypes.push(navigator.mimeTypes[i].type + "::" + navigator.mimeTypes[i].description);
                }
                features.push(mimeTypes.join(";"));
            } catch(e) {
                features.push("mime_types_unavailable");
            }
            
            // 字体列表
            var fonts = ["serif", "sans-serif", "monospace", "Arial", "Arial Black", "Arial Narrow", "Courier", "Courier New", "Georgia", "Helvetica", "Impact", "Lucida Console", "Lucida Grande", "Palatino", "Tahoma", "Times", "Times New Roman", "Verdana"];
            var fontFeatures = [];
            for (var i = 0; i < fonts.length; i++) {
                ctx.font = "14px " + fonts[i];
                var w = ctx.measureText(txt).width;
                fontFeatures.push(fonts[i] + ":" + w);
            }
            features.push(fontFeatures.join(","));
            
            // WebRTC支持检测
            features.push(!!window.RTCPeerConnection);
            
            // 生成最终指纹
            var fingerprintStr = features.join(";");
            
            // 使用两次MurmurHash3算法生成128位指纹
            var hash1 = this.murmurHash3(fingerprintStr, 0);
            var hash2 = this.murmurHash3(fingerprintStr, hash1);
            
            // 返回128位十六进制字符串 (32个字符)
            var fullHash = ("00000000" + hash1.toString(16)).slice(-8) + ("00000000" + hash2.toString(16)).slice(-8);
            return fullHash;
        },
        
        /**
         * MurmurHash3算法实现
         */
        murmurHash3: function(key, seed) {
            var h = seed ^ key.length;
            var k;
            var i = 0;
            var len = key.length;
            
            // 处理4字节块
            while (len >= 4) {
                k = 
                    ((key.charCodeAt(i) & 0xff)) |
                    ((key.charCodeAt(++i) & 0xff) << 8) |
                    ((key.charCodeAt(++i) & 0xff) << 16) |
                    ((key.charCodeAt(++i) & 0xff) << 24);
                
                k = (((k & 0xffff) * 0xcc9e2d51) + ((((k >>> 16) * 0xcc9e2d51) & 0xffff) << 16));
                k = (k << 15) | (k >>> 17);
                k = (((k & 0xffff) * 0x1b873593) + ((((k >>> 16) * 0x1b873593) & 0xffff) << 16));
                
                h ^= k;
                h = (h << 13) | (h >>> 19);
                h = (((h & 0xffff) * 5) + 0xe6546b64) + ((((h >>> 16) * 5) & 0xffff) << 16);
                
                len -= 4;
                ++i;
            }
            
            // 处理剩余字节
            k = 0;
            switch (len) {
                case 3: k ^= (key.charCodeAt(i + 2) & 0xff) << 16;
                case 2: k ^= (key.charCodeAt(i + 1) & 0xff) << 8;
                case 1: k ^= (key.charCodeAt(i) & 0xff);
                        k = (((k & 0xffff) * 0xcc9e2d51) + ((((k >>> 16) * 0xcc9e2d51) & 0xffff) << 16));
                        k = (k << 15) | (k >>> 17);
                        k = (((k & 0xffff) * 0x1b873593) + ((((k >>> 16) * 0x1b873593) & 0xffff) << 16));
                        h ^= k;
            }
            
            // 最终化处理
            h ^= key.length;
            h ^= h >>> 16;
            h = (((h & 0xffff) * 0x85ebca6b) + ((((h >>> 16) * 0x85ebca6b) & 0xffff) << 16));
            h ^= h >>> 13;
            h = ((((h & 0xffff) * 0xc2b2ae35) + ((((h >>> 16) * 0xc2b2ae35) & 0xffff) << 16))) & 0xffffffff;
            h ^= h >>> 16;
            
            return h >>> 0;
        },

        /**
         * 发送HTTP请求
         */
        sendRequest: function(url, data, callback) {
            var xhr = new XMLHttpRequest();
            xhr.open('POST', this.config.trackerServer + url, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        var response = JSON.parse(xhr.responseText);
                        if (callback) callback(null, response);
                    } else {
                        if (callback) callback(new Error('Request failed'), null);
                    }
                }
            };
            xhr.send(JSON.stringify(data));
        },

        /**
         * 通用行为追踪函数
         */
        trackBehavior: function(type, options) {
            var data = {
                userFingerprint: this.generateFingerprint(),    // 用户指纹
                website: this.config.website,        // 网站类型，shop、video、news
                actionType: type,                    // 行为类型 view、click、search
                targetId: options.targetId || '',    // 目标ID，商品id、视频id、广告id
                category: options.category || '',    // 类别，electronics, fashion, food, sports, home
                keywords: options.keywords || '',    // 搜索关键词
                duration: options.duration || (type === 'click' ? 1 : 10),    // 持续时长，默认点击1秒，浏览10秒
                ipAddress: '',
                userAgent: navigator.userAgent
            };

            this.sendRequest('/api/track/behavior', data);
        },

        /**
         * 记录页面浏览
         */
        trackPageView: function(options) {
            return this.trackBehavior('view', options);
        },

        /**
         * 记录点击事件
         */
        trackClick: function(options) {
            return this.trackBehavior('click', options);
        },

        /**
         * 记录搜索事件
         */
        trackSearch: function(options) {
            return this.trackBehavior('search', options);
        },

        /**
         * 记录广告展示
         */
        trackAdImpression: function(adId, position, bidPrice) {
            var self = this;
            return new Promise(function(resolve, reject) {
                var data = {
                    adId: adId,
                    userFingerprint: self.generateFingerprint(),    // 用户指纹
                    website: self.config.website,        // 网站类型，shop、video、news
                    position: position,  
                    bidPrice: bidPrice                   
                };

                self.sendRequest('/api/track/impression', data, function(err, response) {
                    if (err) {
                        reject(err);
                    } else {
                        // 返回impressionId，如果服务器响应中包含的话
                        resolve(response.impressionId || response.id || data.adId);
                    }
                });
            });
        },

        /**
         * 记录广告点击
         */
        trackAdClick: function(impressionId) {
            var data = {
                impressionId: impressionId,
                userFingerprint: this.generateFingerprint()
            };

            this.sendRequest('/api/track/click', data);
        },

        /**
         * 获取推荐广告
         */
        getRecommendedAds: function(options) {
            var self = this;
            return new Promise(function(resolve, reject) {
                var data = {
                    userFingerprint: self.generateFingerprint(),    // 用户指纹
                    website: self.config.website,        // 网站类型，shop、video、news
                    positions: options.positions || [],
                    category: options.category || '',    // 类别，electronics, fashion, food, sports, home
                    count: options.count || 1            //  广告数量
                };

                self.sendRequest('/api/ad/recommend', data, function(err, response) {
                    if (err) {
                        reject(err);
                    } else {
                        resolve(response.ads);
                    }
                });
            });
        }
    };

    return adTracker;
})));