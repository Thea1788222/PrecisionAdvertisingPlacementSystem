<template>
  <div class="product-card" @click="handleClick">
    <!-- 商品图片 -->
    <div class="product-image-container">
      <img 
        :src="product.imageUrl || '/src/assets/placeholder.jpg'" 
        :alt="product.name" 
        class="product-image"
      >
    </div>
    
    <!-- 商品信息 -->
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-footer">
        <span class="product-price">¥{{ product.price }}</span>
        <span class="product-category">{{ getCategoryName(product.category) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

// 分类名称映射
const getCategoryName = (category) => {
  const categoryMap = {
    'electronics': '电子产品',
    'sports': '运动户外',
    'education': '图书教育',
    'fashion': '服装鞋帽',
    'food': '食品饮料',
    'home': '家居生活'
  }
  return categoryMap[category] || category
}

// 点击商品跳转到详情页
const handleClick = () => {
  router.push(`/product/${props.product.id}`)
}
</script>

<style scoped>
.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.product-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 1.1em;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  line-height: 1.4;
}

.product-description {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 1.2em;
  font-weight: bold;
  color: #e4393c;
}

.product-category {
  font-size: 0.8em;
  color: #999;
  background-color: #f5f5f5;
  padding: 4px 8px;
  border-radius: 12px;
}
</style>