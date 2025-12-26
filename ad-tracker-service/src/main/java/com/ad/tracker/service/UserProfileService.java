package com.ad.tracker.service;


import com.ad.tracker.model.UserProfile;

/**
 * @author DaYang
 * @date 2025/12/10
 * @description
 */
public interface UserProfileService {

    /**
     * 根据用户指纹获取用户画像
     *
     * @param fingerprint 用户指纹
     * @return 用户画像
     */
    public UserProfile getUserProfileByFingerprint(String fingerprint);

    /**
     * 保存用户画像
     *
     * @param userProfile 用户画像
     * @return 保存后的用户画像
     */
    public UserProfile saveUserProfile(UserProfile userProfile);
}