package cn.com.nabotix.shareplatform.popularity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统计模块配置类
 * 启用定时任务调度功能和AOP切面功能
 *
 * @author 陈雍文
 */
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class PopularityConfig {
}