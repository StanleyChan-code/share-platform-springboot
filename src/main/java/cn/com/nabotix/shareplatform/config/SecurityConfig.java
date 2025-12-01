package cn.com.nabotix.shareplatform.config;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder();
    }
    
    public static class PasswordEncoder {
        
        public String encode(String rawPassword) {
            return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        }
        
        public boolean matches(String rawPassword, String encodedPassword) {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        }
    }
}