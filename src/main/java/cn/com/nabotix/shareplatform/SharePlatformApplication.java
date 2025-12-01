package cn.com.nabotix.shareplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(scanBasePackages = {"cn.com.nabotix.shareplatform"})
@EnableRedisRepositories(basePackages = {"cn.com.nabotix.shareplatform.user.repository", "cn.com.nabotix.shareplatform.dataset.repository"})
@EnableJpaRepositories(basePackages = {"cn.com.nabotix.shareplatform.user.repository", "cn.com.nabotix.shareplatform.dataset.repository"})
public class SharePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharePlatformApplication.class, args);
    }

}