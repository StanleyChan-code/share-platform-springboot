package cn.com.nabotix.shareplatform.dataset.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cn.com.nabotix.shareplatform.filemanagement.service.FileManagementService;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisRequestDto;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisResponseDto;

/**
 * 数据集统计信息服务类
 * 提供数据集统计信息的业务逻辑处理
 *
 * @author 陈雍文
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetStatisticService {
    
    private final DatasetStatisticRepository datasetStatisticRepository;
    private final FileManagementService fileManagementService;
    
    /**
     * 保存数据集统计信息
     * @param dto 数据集统计信息DTO
     * @return 保存后的数据集统计信息实体
     */
    public DatasetStatistic saveDatasetStatistic(DatasetStatisticDto dto) {
        DatasetStatistic entity = new DatasetStatistic();
        
        // 如果提供了ID，则使用提供的ID，否则由数据库生成
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        
        entity.setDatasetVersionId(dto.getDatasetVersionId());
        entity.setVersion(dto.getVersion());
        List<DatasetStatistic.VariableInfo> variables = new ArrayList<>();
        dto.getVariables().forEach(variableInfoDto -> variables.add(new DatasetStatistic.VariableInfo(
                variableInfoDto.getType(),
                variableInfoDto.getVariables(),
                variableInfoDto.getFileIndex()
        )));
        entity.setVariables(variables);
        
        // 处理统计文件数据
        List<byte[]> statisticalFiles = new ArrayList<>();
        dto.getStatisticalFiles().forEach(fileStr -> {
            if (fileStr != null && !fileStr.isEmpty()) {
                // 将字符串压缩为字节数组
                try {
                    statisticalFiles.add(compressString(fileStr));
                } catch (Exception e) {
                    log.warn("压缩统计文件数据失败，使用未压缩数据: {}", e.getMessage());
                    statisticalFiles.add(fileStr.getBytes());
                }
            } else {
                statisticalFiles.add(new byte[0]); // 添加空字节数组
            }
        });
        entity.setStatisticalFiles(statisticalFiles);
        
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : Instant.now());
        
        DatasetStatistic savedEntity = datasetStatisticRepository.save(entity);
        log.info("保存数据集统计信息成功，ID: {}", savedEntity.getId());
        
        return savedEntity;
    }
    
    /**
     * 根据ID获取数据集统计信息
     * @param id 数据集统计信息ID
     * @return 数据集统计信息实体
     */
    public DatasetStatistic getDatasetStatisticById(UUID id) {
        return datasetStatisticRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据数据集版本ID获取数据集统计信息
     * @param datasetVersionId 数据集版本ID
     * @return 数据集统计信息实体
     */
    public DatasetStatistic getDatasetStatisticByDatasetVersionId(UUID datasetVersionId) {
        return datasetStatisticRepository.findByDatasetVersionId(datasetVersionId).orElse(null);
    }
    
    /**
     * 将字符串压缩为字节数组
     * @param data 要压缩的字符串
     * @return 压缩后的字节数组
     * @throws IOException IO异常
     */
    private byte[] compressString(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data.getBytes());
        }
        return bos.toByteArray();
    }
    
    /**
     * 将压缩的字节数组解压为字符串
     * @param compressedData 压缩的字节数组
     * @return 解压后的字符串
     * @throws IOException IO异常
     */
    private String decompressString(byte[] compressedData) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
        try (GZIPInputStream gis = new GZIPInputStream(bis)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toString();
        }
    }
    
    /**
     * 调用数据分析服务进行数据处理
     * @param requestDto 数据分析请求DTO
     * @return 数据分析结果
     */
    public DataAnalysisResponseDto analyzeData(DataAnalysisRequestDto requestDto) {
        try {
            // 获取文件记录
            var dataFileRecord = fileManagementService.getFileRecordById(requestDto.getDataFileId());
            var dictionaryFileRecord = fileManagementService.getFileRecordById(requestDto.getDictionaryFileId());
            
            if (dataFileRecord == null || dictionaryFileRecord == null) {
                throw new RuntimeException("文件记录不存在");
            }
            
            // 构造文件绝对路径
            Path dataFilePath = fileManagementService.getCompleteFilePath(
                dataFileRecord.getFilePath(), 
                dataFileRecord.getId(), 
                dataFileRecord.getFileName()
            );
            
            Path dictionaryFilePath = fileManagementService.getCompleteFilePath(
                dictionaryFileRecord.getFilePath(), 
                dictionaryFileRecord.getId(), 
                dictionaryFileRecord.getFileName()
            );
            
            // 构造请求数据
            Map<String, String> requestData = new HashMap<>();
            requestData.put("dataFile", dataFilePath.toString());
            requestData.put("dictionaryFile", dictionaryFilePath.toString());
            
            // 调用数据分析服务
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);
            ResponseEntity<DataAnalysisResponseDto> response = restTemplate.postForEntity(
                "http://localhost:10021/analyze", 
                requestEntity, 
                DataAnalysisResponseDto.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("调用数据分析服务失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据分析服务调用失败: " + e.getMessage());
        }
    }
}