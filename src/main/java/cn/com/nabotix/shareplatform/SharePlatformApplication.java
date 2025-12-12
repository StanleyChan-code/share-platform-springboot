package cn.com.nabotix.shareplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = {"cn.com.nabotix.shareplatform"})
@EnableRedisRepositories(basePackages = {"cn.com.nabotix.shareplatform.user.repository"})
@EnableJpaRepositories(basePackages = {"cn.com.nabotix.shareplatform"})
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableAspectJAutoProxy
public class SharePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharePlatformApplication.class, args);
    }

}