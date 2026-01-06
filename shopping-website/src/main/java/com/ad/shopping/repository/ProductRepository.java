package com.ad.shopping.repository;

import com.ad.shopping.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    // 模拟数据 - 稍后可以替换为数据库
    private final List<Product> products = new ArrayList<>();

    public ProductRepository() {
        // 初始化一些测试数据
        products.add(new Product(1L, "智能手机", "高性能智能手机，配备最新处理器和优质摄像头", 2999.0, "electronics", "/images/phone.jpg"));
        products.add(new Product(2L, "笔记本电脑", "轻薄便携，性能强劲的商务笔记本电脑", 5999.0, "electronics", "/images/laptop.jpg"));
        products.add(new Product(3L, "无线耳机", "降噪无线蓝牙耳机，音质出色", 399.0, "electronics", "/images/earphone.jpg"));
        products.add(new Product(4L, "智能手表", "健康监测，运动追踪智能手表", 1299.0, "electronics", "/images/watch.jpg"));
        products.add(new Product(5L, "运动鞋", "舒适透气，适合跑步的运动鞋", 299.0, "sports", "/images/shoes.jpg"));
        products.add(new Product(6L, "图书《Vue.js实战》", "前端开发实战指南", 89.0, "education", "/images/book.jpg"));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public List<Product> findByCategory(String category) {
        return products.stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();
    }
}