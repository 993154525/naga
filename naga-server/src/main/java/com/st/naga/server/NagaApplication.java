package com.st.naga.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Scheduled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ShaoTian
 * @date 2020/11/11 15:15
 */
@SpringBootApplication(scanBasePackages = "com.st.naga.*")
@EntityScan(basePackages = "com.st.naga.entity")
@EnableJpaRepositories(basePackages = "com.st.naga.repository")
@Slf4j
@EnableScheduling
public class NagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NagaApplication.class, args);
    }

}
