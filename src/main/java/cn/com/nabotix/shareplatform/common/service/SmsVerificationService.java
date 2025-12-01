package cn.com.nabotix.shareplatform.common.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证服务类
 *
 * @author 陈雍文
 */
@Service
public class SmsVerificationService {

    private final StringRedisTemplate redisTemplate;

    // 验证码过期时间（分钟）
    private static final long VERIFICATION_CODE_EXPIRE_MINUTES = 5;
    
    // 短信请求频率限制（分钟）
    private static final long SMS_REQUEST_INTERVAL_MINUTES = 1;
    
    // Redis key前缀
    private static final String VERIFICATION_CODE_PREFIX = "sms_verification_code:";
    private static final String SMS_REQUEST_LIMIT_PREFIX = "sms_request_limit:";

    public SmsVerificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成并发送短信验证码（模拟）
     *
     * @param phoneNumber 手机号码
     * @param businessType 业务类型（如REGISTER, LOGIN, UPDATE_PASSWORD等）
     * @return 验证码
     */
    public String generateAndSendVerificationCode(String phoneNumber, String businessType) {
        // 检查是否在一分钟内重复请求
        String limitKey = SMS_REQUEST_LIMIT_PREFIX + businessType + ":" + phoneNumber;
        String lastRequestTime = redisTemplate.opsForValue().get(limitKey);
        
        if (lastRequestTime != null) {
            // 如果一分钟内已经发送过验证码，则拒绝请求
            long currentTime = System.currentTimeMillis();
            long previousTime = Long.parseLong(lastRequestTime);
            if (currentTime - previousTime < TimeUnit.MINUTES.toMillis(SMS_REQUEST_INTERVAL_MINUTES)) {
                throw new IllegalStateException("请求过于频繁，请稍后再试");
            }
        }
        
        // 生成6位随机数字验证码
        String verificationCode = String.format("%06d", (int) (Math.random() * 1000000));

        // 清除该手机号下所有业务类型的旧验证码，确保一个手机号只有一个有效验证码
        clearAllVerificationCodesForPhone(phoneNumber);

        // 将验证码存储到Redis中，设置过期时间，加上业务类型区分
        String key = VERIFICATION_CODE_PREFIX + businessType + ":" + phoneNumber;
        redisTemplate.opsForValue().set(key, verificationCode, VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 记录本次请求时间，用于频率限制
        redisTemplate.opsForValue().set(limitKey, String.valueOf(System.currentTimeMillis()), 
                SMS_REQUEST_INTERVAL_MINUTES, TimeUnit.MINUTES);

        // 实际应用中，这里应该调用短信服务API发送验证码到用户手机
        // 例如：smsService.sendSms(phoneNumber, "您的验证码是：" + verificationCode);

        // 返回验证码，实际开发中不应返回，仅用于测试或日志记录
        return verificationCode;
    }
    
    /**
     * 生成并发送短信验证码（模拟）- 默认为注册业务类型
     *
     * @param phoneNumber 手机号码
     * @return 验证码
     */
    public String generateAndSendVerificationCode(String phoneNumber) {
        return generateAndSendVerificationCode(phoneNumber, "REGISTER");
    }

    /**
     * 验证手机号和验证码是否匹配
     *
     * @param phoneNumber      手机号码
     * @param verificationCode 用户输入的验证码
     * @param businessType     业务类型（如REGISTER, LOGIN, UPDATE_PASSWORD等）
     * @return 验证结果
     */
    public boolean verifyCode(String phoneNumber, String verificationCode, String businessType) {
        String key = VERIFICATION_CODE_PREFIX + businessType + ":" + phoneNumber;
        String limitKey = SMS_REQUEST_LIMIT_PREFIX + businessType + ":" + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            // 验证码已过期或不存在
            return false;
        }

        // 比较验证码
        if (storedCode.equals(verificationCode)) {
            // 验证成功后删除验证码和请求限制，防止重复使用
            redisTemplate.delete(key);
            redisTemplate.delete(limitKey);
            return true;
        }

        return false;
    }
    
    /**
     * 验证手机号和验证码是否匹配 - 默认为注册业务类型
     *
     * @param phoneNumber      手机号码
     * @param verificationCode 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCode(String phoneNumber, String verificationCode) {
        return verifyCode(phoneNumber, verificationCode, "REGISTER");
    }
    
    /**
     * 清除指定手机号和业务类型的验证码
     *
     * @param phoneNumber 手机号码
     * @param businessType 业务类型
     */
    public void clearVerificationCode(String phoneNumber, String businessType) {
        String key = VERIFICATION_CODE_PREFIX + businessType + ":" + phoneNumber;
        String limitKey = SMS_REQUEST_LIMIT_PREFIX + businessType + ":" + phoneNumber;
        redisTemplate.delete(key);
        redisTemplate.delete(limitKey);
    }
    
    /**
     * 清除指定手机号下所有业务类型的验证码
     *
     * @param phoneNumber 手机号码
     */
    private void clearAllVerificationCodesForPhone(String phoneNumber) {
        // 查找该手机号对应的所有业务类型的验证码key并删除
        Set<String> codeKeys = redisTemplate.keys(VERIFICATION_CODE_PREFIX + "*:" + phoneNumber);
        Set<String> limitKeys = redisTemplate.keys(SMS_REQUEST_LIMIT_PREFIX + "*:" + phoneNumber);
        
        if (!codeKeys.isEmpty()) {
            redisTemplate.delete(codeKeys);
        }
        
        if (!limitKeys.isEmpty()) {
            redisTemplate.delete(limitKeys);
        }
    }
}