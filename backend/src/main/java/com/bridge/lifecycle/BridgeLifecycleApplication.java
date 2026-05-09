package com.bridge.lifecycle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 桥梁设备全生命周期管理系统启动类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@SpringBootApplication
@MapperScan("com.bridge.lifecycle.mapper")
public class BridgeLifecycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BridgeLifecycleApplication.class, args);
    }
}
