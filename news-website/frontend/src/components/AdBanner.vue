<template>
  <div class="ad-banner" v-if="ad">
    <a
      :href="ad.linkUrl"
      target="_blank"
      @click="handleAdClick"
      class="ad-link"
    >
      <img
        v-if="ad.imageUrl"
        :src="ad.imageUrl"
        :alt="ad.title"
        class="ad-image"
      />
      <div v-else class="ad-placeholder">
        {{ ad.title }}
      </div>
    </a>
    <div class="ad-label">广告</div>
  </div>
  <!-- 移除加载提示，避免显示乱码 -->
</template>

<script>
import { getAdTracker } from '../utils/adTracker'

export default {
  name: 'AdBanner',
  props: {
    position: {
      type: String,
      required: true,  // 'top-banner', 'sidebar'
      default: 'top-banner'
    },
    category: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      ad: null,
      impressionId: null,
      loading: false
    }
  },
  async mounted() {
    await this.loadAd();
  },
  methods: {
    async loadAd() {
      const tracker = getAdTracker();
      if (!tracker) {
        console.error('广告追踪SDK未初始化');
        return;
      }

      try {
        // 获取推荐广告
        const ads = await tracker.getRecommendedAds({
          positions: [this.position],
          category: this.category,
          type: 'banner',  // 横幅广告
          count: 1
        });

        if (ads && ads.length > 0) {
          this.ad = ads[0];

          // 记录广告展示
          if (this.ad) {
            tracker.trackAdImpression(
              this.ad.id,
              this.position,
              this.ad.bidPrice || 1.0
            ).then(impressionId => {
              this.impressionId = impressionId;
              console.log('广告展示已记录，ID:', impressionId);
            }).catch(error => {
              console.error('记录广告展示失败:', error);
            });
          }
        } else {
          console.log('未找到合适的广告');
        }
      } catch (error) {
        console.error('加载广告失败:', error);
      }
    },
    handleAdClick() {
      const tracker = getAdTracker();
      if (tracker && this.impressionId) {
        tracker.trackAdClick(this.impressionId);
        console.log('广告点击已记录:', this.impressionId);
      }
    }
  }
}
</script>

<style scoped>
.ad-banner {
  position: relative;
  margin: 20px 0;
  text-align: center;
}

.ad-link {
  display: block;
  text-decoration: none;
}

.ad-image {
  display: block;
  max-width: 100%;
  height: auto;
  width: auto;
}

.ad-placeholder {
  padding: 20px;
  background: #f0f0f0;
  border: 1px dashed #ccc;
  color: #666;
}

.ad-label {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 2px 6px;
  font-size: 12px;
  border-radius: 2px;
}

.ad-loading {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}
</style>