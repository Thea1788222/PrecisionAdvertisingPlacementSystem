package com.ad.management.service;

import com.ad.management.model.User;
import com.ad.management.model.RegisterRequest;
import java.util.Optional;

public interface UserService {
    /**
     * 注册用户
     *
     * @param registerRequest 注册请求对象
     * @return 注册的用户对象
     */
    User register(RegisterRequest registerRequest);

     /**
      * 根据用户名查找用户
      *
      * @param username 用户名
      * @return 匹配的用户对象
      */
    Optional<User> findByUsername(String username);

    /**
     * 验证用户名和密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 匹配的用户对象
     */
    User authenticate(String username, String password);
}