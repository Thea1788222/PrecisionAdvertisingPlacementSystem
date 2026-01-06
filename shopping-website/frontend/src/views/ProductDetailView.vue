<template>
  <div class="product-detail">
    <div class="product-content">
      <!-- 商品信息 -->
      <div class="product-info" v-if="product">
        <h1>{{ product.name }}</h1>
        <img :src="product.imageUrl || '/src/assets/placeholder.jpg'" :alt="product.name" class="main-image">
        <p class="description">{{ product.description }}</p>
        <p class="price">价格: ¥{{ product.price }}</p>
        <button class="buy-btn">立即购买</button>
      </div>
      <div v-else>
        <p>加载中...</p>
      </div>

      <!-- 广告位预留 -->
      <div class="advertisement">
        <h3>相关广告推荐</h3>
        <p>这里将显示相关商品广告</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const product = ref(null)

// 从后端API获取商品详情
const fetchProductDetail = async (productId) => {
  try {
    const response = await fetch(`http://localhost:8081/api/products/${productId}`)
    if (response.ok) {
      product.value = await response.json()
    } else {
      console.error('获取商品详情失败')
    }
  } catch (error) {
    console.error('请求失败:', error)
  }
}

onMounted(() => {
  const productId = route.params.id
  fetchProductDetail(productId)
})
</script>

<style scoped>
/* 样式保持不变 */
.product-detail {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.product-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.main-image {
  width: 100%;
  max-width: 400px;
  height: 300px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.price {
  color: #e4393c;
  font-size: 1.5em;
  font-weight: bold;
}

.buy-btn {
  background-color: #e4393c;
  color: white;
  padding: 12px 30px;
  border: none;
  border-radius: 4px;
  font-size: 1.1em;
  cursor: pointer;
}

.advertisement {
  border: 2px dashed #ccc;
  padding: 20px;
  height: fit-content;
  background-color: #f9f9f9;
}
</style>
