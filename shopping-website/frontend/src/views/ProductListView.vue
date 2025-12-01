<template>
  <div class="product-list">
    <h1>商品列表</h1>

    <!-- 分类筛选（预留功能） -->
    <div class="category-filter">
      <button class="filter-btn active">全部</button>
      <button class="filter-btn">电子产品</button>
      <button class="filter-btn">运动户外</button>
      <button class="filter-btn">图书教育</button>
    </div>

    <div class="products-container">
      <!-- 使用 ProductCard 组件 -->
      <div class="products-grid">
        <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
        />
      </div>

      <!-- 广告位预留 -->
      <div class="advertisement">
        <h3>📢 广告位</h3>
        <p>这里将显示个性化推荐广告</p>
        <small>基于您的浏览行为智能推荐</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ProductCard from '../components/ProductCard.vue'  // 导入组件

const products = ref([])

// 从后端API获取商品数据
const fetchProducts = async () => {
  try {
    const response = await fetch('http://localhost:8081/api/products')
    if (response.ok) {
      products.value = await response.json()
    } else {
      console.error('获取商品数据失败')
    }
  } catch (error) {
    console.error('请求失败:', error)
    // 如果API调用失败，使用模拟数据作为后备
    products.value = [
      { id: 1, name: '智能手机', price: 2999, imageUrl: '/images/phone.jpg' },
      { id: 2, name: '笔记本电脑', price: 5999, imageUrl: '/images/laptop.jpg' },
      { id: 3, name: '无线耳机', price: 399, imageUrl: '/images/earphone.jpg' },
      { id: 4, name: '智能手表', price: 1299, imageUrl: '/images/watch.jpg' },
    ]
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.category-filter {
  display: flex;
  gap: 10px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-btn.active {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.filter-btn:hover {
  background-color: #f8f9fa;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.advertisement {
  border: 2px dashed #3498db;
  padding: 30px;
  text-align: center;
  margin: 40px 0;
  background-color: #f8fafc;
  border-radius: 12px;
}

.advertisement h3 {
  color: #3498db;
  margin-bottom: 10px;
}

.advertisement p {
  color: #666;
  margin-bottom: 5px;
}

.advertisement small {
  color: #999;
}
</style>
