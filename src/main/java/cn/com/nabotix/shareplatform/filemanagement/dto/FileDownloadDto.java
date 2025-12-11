package cn.com.nabotix.shareplatform.filemanagement.dto;

import lombok.Data;
import org.springframework.core.io.Resource;

/**
 * 文件下载数据传输对象
 * 用于封装文件下载相关的信息
 *
 * @author 陈雍文
 */
@Data
public class FileDownloadDto {
    private Resource file;
    private String fileName;
    private Long fileSize;
    private String fileType;
}
