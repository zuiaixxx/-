package com.platform.agriculture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.platform.agriculture.mapper")
public class CurriculumLearningPlatformBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurriculumLearningPlatformBackendApplication.class, args);
    }

}
