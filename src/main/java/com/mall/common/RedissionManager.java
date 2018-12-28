package com.mall.common;

import com.mall.util.PropertiesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissionManager {
    private Config config = new Config();
    @Getter
    private Redisson redisson=null;

    private static String ip = PropertiesUtil.getPropery("redis.ip");
    private static String[] ports = PropertiesUtil.getPropery("redis.port").split(",");
    private static Integer port1 = Integer.valueOf(ports[0]);
    private static Integer port2 = Integer.valueOf(ports[1]);

    @PostConstruct
    private void init() {
        try {
            config.useSingleServer().setAddress(ip + ":" + port1);
            redisson = (Redisson) Redisson.create(config);
            log.info("初始化redission结束");
        } catch (Exception e) {
            log.error("初始化redission失败",e);
        }
    }
}
