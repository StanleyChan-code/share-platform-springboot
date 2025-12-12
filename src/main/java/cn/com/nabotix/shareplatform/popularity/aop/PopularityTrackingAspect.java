package cn.com.nabotix.shareplatform.popularity.aop;

import cn.com.nabotix.shareplatform.security.AuthorityUtil;
import cn.com.nabotix.shareplatform.popularity.service.PopularityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * 热度追踪切面
 * 用于自动记录数据集和研究学科的访问热度
 *
 * @author 陈雍文
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PopularityTrackingAspect {

    private final PopularityService popularityService;

    /**
     * 定义切入点：数据集详情查询方法
     */
    @Pointcut("execution(* cn.com.nabotix.shareplatform.dataset.controller.DatasetController.getPublicDatasetById(..))")
    public void datasetDetailQuery() {
    }

    /**
     * 定义切入点：研究学科详情查询方法
     */
    @Pointcut("execution(* cn.com.nabotix.shareplatform.researchsubject.service.ResearchSubjectService.getResearchSubjectById(..))")
    public void subjectDetailQuery() {
    }

    /**
     * 在数据集详情查询方法成功返回后记录热度
     *
     * @param joinPoint 连接点
     * @param result    返回结果
     */
    @AfterReturning(pointcut = "datasetDetailQuery()", returning = "result")
    public void recordDatasetPopularity(JoinPoint joinPoint, Object result) {
        try {
            // 检查返回结果是否为空
            if (result == null) {
                return;
            }

            // 获取数据集ID参数
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof UUID datasetId) {

                // 获取当前用户ID和IP地址
                String userId = getCurrentUserId();
                String ipAddress = getClientIpAddress();

                // 记录数据集访问热度
                popularityService.recordDatasetVisit(datasetId, userId, ipAddress);
            }
        } catch (Exception e) {
            log.warn("记录数据集热度时发生异常", e);
        }
    }

    /**
     * 在研究学科详情查询方法成功返回后记录热度
     *
     * @param joinPoint 连接点
     * @param result    返回结果
     */
    @AfterReturning(pointcut = "subjectDetailQuery()", returning = "result")
    public void recordSubjectPopularity(JoinPoint joinPoint, Object result) {
        try {
            // 检查返回结果是否为空
            if (result == null) {
                return;
            }

            // 获取研究学科ID参数
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof UUID subjectId) {

                // 获取当前用户ID和IP地址
                String userId = getCurrentUserId();
                String ipAddress = getClientIpAddress();

                // 记录研究学科访问热度
                popularityService.recordSubjectVisit(subjectId, userId, ipAddress);
            }
        } catch (Exception e) {
            log.warn("记录研究学科热度时发生异常", e);
        }
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，如果未登录则返回null
     */
    private String getCurrentUserId() {
        try {
            return AuthorityUtil.getCurrentUserId() != null ? AuthorityUtil.getCurrentUserId().toString() : null;
        } catch (Exception e) {
            log.debug("获取当前用户ID时发生异常", e);
            return null;
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @return 客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getHeader("X-Real-IP");
                }
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                }
                return ipAddress;
            }
        } catch (Exception e) {
            log.debug("获取客户端IP地址时发生异常", e);
        }
        return "unknown";
    }
}