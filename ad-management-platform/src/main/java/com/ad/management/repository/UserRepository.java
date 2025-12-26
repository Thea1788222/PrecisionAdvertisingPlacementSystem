package com.ad.management.repository;

import com.ad.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 符合条件的用户 Optional 对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 符合条件的用户 Optional 对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true 表示用户名已存在，false 表示用户名不存在
     */
    Boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true 表示邮箱已存在，false 表示邮箱不存在
     */
    Boolean existsByEmail(String email);
}