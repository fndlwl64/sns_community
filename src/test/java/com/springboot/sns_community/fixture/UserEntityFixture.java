package com.springboot.sns_community.fixture;

import com.springboot.sns_community.model.entity.UserEntity;

public class UserEntityFixture {
    public static UserEntity get(String userName, String password,Integer postId) {
        UserEntity result = new UserEntity();
        result.setId(postId);
        result.setUserName(userName);
        result.setPassword(password);
        return result;
    }
}
