/**
 * 新闻API服务
 */
const API_BASE_URL = 'http://localhost:8083/api';

export const newsApi = {
  /**
   * 获取最新20条新闻
   */
  async getLatestNews() {
    const response = await fetch(`${API_BASE_URL}/news/latest`);
    return await response.json();
  },

  /**
   * 根据ID获取新闻详情
   */
  async getNewsById(id) {
    const response = await fetch(`${API_BASE_URL}/news/${id}`);
    if (response.ok) {
      return await response.json();
    }
    return null;
  },

  /**
   * 根据类别获取新闻（分页）
   */
  async getNewsByCategory(categoryId, page = 0, size = 20) {
    const response = await fetch(
      `${API_BASE_URL}/news/category/${categoryId}?page=${page}&size=${size}`
    );
    return await response.json();
  },

  /**
   * 搜索新闻（分页）
   */
  async searchNews(keyword, page = 0, size = 20) {
    const response = await fetch(
      `${API_BASE_URL}/news/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`
    );
    return await response.json();
  },

  /**
   * 获取所有新闻（管理页面，分页）
   */
  async getAllNews(page = 0, size = 20) {
    const response = await fetch(
      `${API_BASE_URL}/news/admin/all?page=${page}&size=${size}`
    );
    return await response.json();
  },

  /**
   * 创建新闻（管理页面）
   */
  async createNews(news) {
    const response = await fetch(`${API_BASE_URL}/news/admin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(news),
    });
    return await response.json();
  },

  /**
   * 更新新闻（管理页面）
   */
  async updateNews(id, news) {
    const response = await fetch(`${API_BASE_URL}/news/admin/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(news),
    });
    return await response.json();
  },

  /**
   * 删除新闻（管理页面）
   */
  async deleteNews(id) {
    const response = await fetch(`${API_BASE_URL}/news/admin/${id}`, {
      method: 'DELETE',
    });
    return response.ok;
  },
};

export const categoryApi = {
  /**
   * 获取所有类别
   */
  async getAllCategories() {
    const response = await fetch(`${API_BASE_URL}/categories`);
    return await response.json();
  },

  /**
   * 创建类别（管理页面）
   */
  async createCategory(category) {
    const response = await fetch(`${API_BASE_URL}/categories/admin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(category),
    });
    return await response.json();
  },

  /**
   * 更新类别（管理页面）
   */
  async updateCategory(id, category) {
    const response = await fetch(`${API_BASE_URL}/categories/admin/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(category),
    });
    return await response.json();
  },

  /**
   * 删除类别（管理页面）
   */
  async deleteCategory(id) {
    const response = await fetch(`${API_BASE_URL}/categories/admin/${id}`, {
      method: 'DELETE',
    });
    return response.ok;
  },
};
