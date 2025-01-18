package com.xxl.conf.admin.mapper;

import com.xxl.conf.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.conf.admin.model.entity.Instance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InstanceMapperTest {
    private static Logger logger = LoggerFactory.getLogger(InstanceMapperTest.class);

    @Resource
    private InstanceMapper instanceMapper;

    @Test
    public void loadTest() throws Exception {
        Instance instance = new Instance();
        instance.setEnv("test");
        instance.setAppname("app01");
        instance.setIp("localhost");
        instance.setPort(8080);
        instance.setExtendInfo("sss");
        instance.setRegisterModel(InstanceRegisterModelEnum.AUTO.getValue());
        instance.setRegisterHeartbeat(new Date());

        int ret = instanceMapper.addAutoInstance(instance);
        assertTrue(ret > -1);
    }

}
