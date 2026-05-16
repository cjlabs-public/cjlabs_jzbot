package com.cjlabs.jzbot;

import com.cjlabs.boot.runner.ApplicationContextRunnerWrapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 使用 markerInterface 过滤（高级用法）
// 如果你想保持扫描整个包，可以指定 Mapper 的标记接口：
@MapperScan(
        basePackages = "com.cjlabs.jzbot",
        markerInterface = com.baomidou.mybatisplus.core.mapper.BaseMapper.class
        // annotationClass = Mapper.class  // 只扫描带 @Mapper 注解的接口
)
@SpringBootApplication
public class JzbotApplication {

    public static void main(String[] args) {
        ApplicationContextRunnerWrapper.run(JzbotApplication.class, args);
    }

}
