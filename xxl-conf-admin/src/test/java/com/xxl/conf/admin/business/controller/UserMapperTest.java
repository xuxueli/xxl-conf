package com.xxl.conf.admin.business.controller;

import com.xxl.conf.admin.framework.mapper.UserMapper;
import com.xxl.conf.admin.framework.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserMapperTest {
    private static Logger logger = LoggerFactory.getLogger(UserMapperTest.class);

    @Resource
    private UserMapper userMapper;

    @Test
    public void loadTest() throws Exception {
        User user = userMapper.loadByUserName("admin");
        Assertions.assertNotNull(user);
    }

}
