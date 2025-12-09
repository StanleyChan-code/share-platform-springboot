package cn.com.nabotix.shareplatform.filemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 文件清理配置类
 * 配置定时任务用于清理临时文件
 *
 * @author 陈雍文
 */
@Configuration
@EnableScheduling
public class FileCleanupConfiguration {

    private final FileManagementService fileManagementService;

    @Autowired
    public FileCleanupConfiguration(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    /**
     * 定时清理临时文件（每天凌晨4点执行）
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanUpTemporaryFiles() {
        fileManagementService.cleanUpTemporaryFiles();
    }
}