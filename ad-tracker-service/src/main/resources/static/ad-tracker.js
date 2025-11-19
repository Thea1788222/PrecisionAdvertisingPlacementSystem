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
            cookieId: null,
            domain: null
        },
        
        /**
         * 初始化SDK
         * @param {Object} options - 配置选项
         * @param {string} options.trackerServer - 追踪服务地址
         * @param {string} options.website - 网站标识
         * @param {string} options.domain - 可选的Cookie域
         */
        init: function(options) {
            this.config.trackerServer = options.trackerServer || '';
            this.config.website = options.website || '';
            this.config.domain = options.domain || null;
            
            // 获取或生成cookie ID
            this.config.cookieId = this.getCookie('ad_tracker_cid');
            if (!this.config.cookieId) {
                this.config.cookieId = this.generateUUID();
                this.setCookie('ad_tracker_cid', this.config.cookieId, 365);
            }
        },
        
        /**
         * 生成UUID
         */
        generateUUID: function() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                var r = Math.random() * 16 | 0,
                    v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },
        
        /**
         * 获取Cookie值
         */
        getCookie: function(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for(var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) === ' ') c = c.substring(1, c.length);
                if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
            }
            return null;
        },
        
        /**
         * 设置Cookie值
         */
        setCookie: function(name, value, days) {
            var expires = "";
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = "; expires=" + date.toUTCString();
            }
            var cookieString = name + "=" + value + expires + "; path=/";
            if (this.config.domain) {
                cookieString += "; domain=" + this.config.domain;
            }
            document.cookie = cookieString;
        },
        
        /**
         * 生成浏览器指纹
         */
        generateFingerprint: function() {
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
            var strng = canvas.toDataURL();
            
            var hash = 0;
            if (strng.length === 0) return hash;
            for (var i = 0; i < strng.length; i++) {
                var char = strng.charCodeAt(i);
                hash = ((hash<<5)-hash)+char;
                hash = hash & hash;
            }
            return hash;
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
         * 记录页面浏览
         */
        trackPageView: function(options) {
            var data = {
                cookieId: this.config.cookieId,
                userFingerprint: this.generateFingerprint(),
                website: this.config.website,
                actionType: 'page_view',
                targetId: options.targetId || window.location.pathname,
                category: options.category || '',
                keywords: options.keywords || '',
                duration: 0,
                ipAddress: '',
                userAgent: navigator.userAgent
            };
            
            this.sendRequest('/api/track/behavior', data);
        },
        
        /**
         * 记录点击事件
         */
        trackClick: function(options) {
            var data = {
                cookieId: this.config.cookieId,
                userFingerprint: this.generateFingerprint(),
                website: this.config.website,
                actionType: 'click',
                targetId: options.targetId || '',
                category: options.category || '',
                keywords: options.keywords || '',
                duration: 0,
                ipAddress: '',
                userAgent: navigator.userAgent
            };
            
            this.sendRequest('/api/track/behavior', data);
        },
        
        /**
         * 记录广告展示
         */
        trackAdImpression: function(adId, position, bidPrice) {
            var data = {
                adId: adId,
                cookieId: this.config.cookieId,
                website: this.config.website,
                position: position,
                bidPrice: bidPrice
            };
            
            this.sendRequest('/api/track/impression', data);
        },
        
        /**
         * 记录广告点击
         */
        trackAdClick: function(impressionId) {
            var data = {
                impressionId: impressionId
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
                    cookieId: self.config.cookieId,
                    website: self.config.website,
                    positions: options.positions || [],
                    category: options.category || '',
                    count: options.count || 5
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