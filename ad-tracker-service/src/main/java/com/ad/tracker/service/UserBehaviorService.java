package com.ad.tracker.service;


import com.ad.tracker.model.UserBehavior;

/**
 * @author DaYang
 * @date 2025/12/10
 * @description
 */
public interface UserBehaviorService {

    /**
     * 保存用户行为数据
     *
     * @param userBehavior 用户行为数据
     * @return 保存后的用户行为数据
     */
    public UserBehavior saveUserBehavior(UserBehavior userBehavior);


}
