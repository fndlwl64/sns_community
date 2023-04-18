package com.springboot.sns_community.fixture;

import com.springboot.sns_community.model.entity.UserEntity;

public class UserEntityFixture {
    public static UserEntity get(String userName, String password) {
        UserEntity result = new UserEntity();
        result.setId(1);
        result.setUserName(userName);
        result.setPassword(password);
        return result;
    }
}
