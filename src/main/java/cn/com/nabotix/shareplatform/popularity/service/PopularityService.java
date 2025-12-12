package cn.com.nabotix.shareplatform.popularity.service;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.repository.DatasetRepository;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.repository.ResearchSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 热度统计服务类
 * 实现数据集和研究学科的热度统计功能
 *
 * @author 陈雍文
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PopularityService {

    private final RedisTemplate<String, String> redisTemplate;
    private final DatasetRepository datasetRepository;
    private final ResearchSubjectRepository researchSubjectRepository;

    private static final String VISIT_RECORD_PREFIX = "visit_record:";
    private static final String HYPERLOGLOG_PREFIX = "hll:visit:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // 热量有效期为7*24小时，每天每个账号能够积累一个热度
    private static final long POPULARITY_EXPIRATION_HOURS = 7 * 24;

    /**
     * 记录数据集访问
     * @param datasetId 数据集ID
     * @param userId 用户ID（如果已登录）
     * @param ipAddress IP地址（用户未登录时使用）
     */
    public void recordDatasetVisit(UUID datasetId, String userId, String ipAddress) {
        recordVisit("dataset", datasetId.toString(), userId, ipAddress);
    }

    /**
     * 记录研究学科访问
     * @param subjectId 研究学科ID
     * @param userId 用户ID（如果已登录）
     * @param ipAddress IP地址（用户未登录时使用）
     */
    public void recordSubjectVisit(UUID subjectId, String userId, String ipAddress) {
        recordVisit("subject", subjectId.toString(), userId, ipAddress);
    }

    /**
     * 通用访问记录方法
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param ipAddress IP地址
     */
    private void recordVisit(String resourceType, String resourceId, String userId, String ipAddress) {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String identifier = (userId != null && !userId.isEmpty()) ? userId : ipAddress;
        
        // 检查是否已经记录过今天的访问
        String visitRecordKey = VISIT_RECORD_PREFIX + resourceType + ":" + resourceId + ":" + identifier + ":" + dateStr;
        Boolean hasVisited = redisTemplate.hasKey(visitRecordKey);

        // 每天每个账号每个资源积累一个热度
        if (hasVisited) {
            // 今天已经访问过，不需要重复记录
            return;
        }

        // 每个热度的过期时间稍长于POPULARITY_EXPIRATION_HOURS
        // 记录访问（防止刷访问量）
        redisTemplate.opsForValue().set(visitRecordKey, "1", POPULARITY_EXPIRATION_HOURS+1, TimeUnit.HOURS);
        
        // 更新HyperLogLog统计
        String hyperloglogDailyKey = HYPERLOGLOG_PREFIX + resourceType + ":" + resourceId + ":" + dateStr;
        String hyperloglogTotalKey = HYPERLOGLOG_PREFIX + resourceType + ":" + resourceId + ":total";
        
        redisTemplate.opsForHyperLogLog().add(hyperloglogDailyKey, identifier);
        redisTemplate.opsForHyperLogLog().add(hyperloglogTotalKey, identifier);
        
        // 标记该资源需要同步到数据库
        String dirtySetKey = "dirty:" + resourceType;
        redisTemplate.opsForSet().add(dirtySetKey, resourceId);
        // 设置过期时间与同步周期一致(30分钟)
        redisTemplate.expire(dirtySetKey, 30, TimeUnit.MINUTES);
        
        log.debug("记录访问: {} {} by {} ({})", resourceType, resourceId, userId, ipAddress);
    }

    /**
     * 定时任务：同步热度数据到数据库
     * 每30分钟执行一次增量同步
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 每30分钟执行一次
    public void syncPopularityToDatabase() {
        log.info("开始增量同步热度数据到数据库");
        
        try {
            syncDirtyDatasets();
            syncDirtySubjects();
            log.info("增量热度数据同步完成");
        } catch (Exception e) {
            log.error("增量热度数据同步失败", e);
        }
    }

    /**
     * 定时任务：全量同步热度数据到数据库
     * 每天凌晨4点45分执行
     */
    @Scheduled(cron = "0 45 4 * * ?")
    public void syncAllPopularityToDatabase() {
        log.info("开始全量同步热度数据到数据库");
        
        try {
            syncDatasetPopularity();
            syncSubjectPopularity();
            // 同步完成后清空待同步集合
            redisTemplate.delete("dirty:dataset");
            redisTemplate.delete("dirty:subject");
            log.info("全量热度数据同步完成");
        } catch (Exception e) {
            log.error("全量热度数据同步失败", e);
        }
    }

    /**
     * 同步数据集热度数据到数据库
     */
    private void syncDatasetPopularity() {
        List<Dataset> datasets = datasetRepository.findAll();
        for (Dataset dataset : datasets) {
            String key = HYPERLOGLOG_PREFIX + "dataset:" + dataset.getId() + ":total";
            Long count = redisTemplate.opsForHyperLogLog().size(key);
            dataset.setSearchCount(count.intValue());
        }
        datasetRepository.saveAll(datasets);
        log.info("同步了{}个数据集的热度数据", datasets.size());
    }

    /**
     * 增量同步数据集热度数据到数据库
     */
    private void syncDirtyDatasets() {
        String dirtySetKey = "dirty:dataset";
        Set<String> dirtyIds = redisTemplate.opsForSet().members(dirtySetKey);

        if (dirtyIds != null && !dirtyIds.isEmpty()) {
            List<Dataset> datasetsToUpdate = new ArrayList<>();
            for (String id : dirtyIds) {
                UUID datasetId = UUID.fromString(id);
                Dataset dataset = datasetRepository.findById(datasetId).orElse(null);
                if (dataset != null) {
                    String key = HYPERLOGLOG_PREFIX + "dataset:" + dataset.getId() + ":total";
                    Long count = redisTemplate.opsForHyperLogLog().size(key);
                    dataset.setSearchCount(count.intValue());
                    datasetsToUpdate.add(dataset);
                }
            }

            if (!datasetsToUpdate.isEmpty()) {
                datasetRepository.saveAll(datasetsToUpdate);
                log.info("增量同步了{}个数据集的热度数据", datasetsToUpdate.size());
            }

            // 清除已同步的标记
            redisTemplate.opsForSet().remove(dirtySetKey, dirtyIds.toArray());
        }
    }

    /**
     * 同步研究学科热度数据到数据库
     */
    private void syncSubjectPopularity() {
        List<ResearchSubject> subjects = researchSubjectRepository.findAll();
        for (ResearchSubject subject : subjects) {
            String key = HYPERLOGLOG_PREFIX + "subject:" + subject.getId() + ":total";
            Long count = redisTemplate.opsForHyperLogLog().size(key);
            subject.setSearchCount(count);
        }
        researchSubjectRepository.saveAll(subjects);
        log.info("同步了{}个研究学科的热度数据", subjects.size());
    }

    /**
     * 增量同步研究学科热度数据到数据库
     */
    private void syncDirtySubjects() {
        String dirtySetKey = "dirty:subject";
        Set<String> dirtyIds = redisTemplate.opsForSet().members(dirtySetKey);

        if (dirtyIds != null && !dirtyIds.isEmpty()) {
            List<ResearchSubject> subjectsToUpdate = new ArrayList<>();
            for (String id : dirtyIds) {
                UUID subjectId = UUID.fromString(id);
                ResearchSubject subject = researchSubjectRepository.findById(subjectId).orElse(null);
                if (subject != null) {
                    String key = HYPERLOGLOG_PREFIX + "subject:" + subject.getId() + ":total";
                    Long count = redisTemplate.opsForHyperLogLog().size(key);
                    subject.setSearchCount(count);
                    subjectsToUpdate.add(subject);
                }
            }

            if (!subjectsToUpdate.isEmpty()) {
                researchSubjectRepository.saveAll(subjectsToUpdate);
                log.info("增量同步了{}个研究学科的热度数据", subjectsToUpdate.size());
            }

            // 清除已同步的标记
            redisTemplate.opsForSet().remove(dirtySetKey, dirtyIds.toArray());
        }
    }

    /**
     * 获取数据集的热度值
     * @param datasetId 数据集ID
     * @return 热度值
     */
    public Long getDatasetPopularity(UUID datasetId) {
        String key = HYPERLOGLOG_PREFIX + "dataset:" + datasetId + ":total";
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    /**
     * 获取研究学科的热度值
     * @param subjectId 研究学科ID
     * @return 热度值
     */
    public Long getSubjectPopularity(UUID subjectId) {
        String key = HYPERLOGLOG_PREFIX + "subject:" + subjectId + ":total";
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}