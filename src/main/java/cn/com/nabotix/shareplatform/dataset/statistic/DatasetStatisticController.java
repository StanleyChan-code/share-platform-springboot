package cn.com.nabotix.shareplatform.dataset.statistic;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.service.DatasetVersionService;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisRequestDto;
import cn.com.nabotix.shareplatform.dataset.statistic.dto.DataAnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 数据集统计信息控制器
 * 提供数据集统计信息的接口
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/datasets/statistics/dataset-statistics")
@RequiredArgsConstructor
public class DatasetStatisticController {
    
    private final DatasetStatisticService datasetStatisticService;
    private final DatasetVersionService datasetVersionService;

    /**
     * 创建或更新数据集统计信息
     * 支持直接接收字符数据作为统计文件内容
     * @param dto 数据集统计信息DTO
     * @return 保存后的数据集统计信息
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<DatasetStatistic>> createDatasetStatistic(@RequestBody DatasetStatisticDto dto) {
        try {
            UUID datasetVersionId = dto.getDatasetVersionId();
            if (datasetVersionId == null || datasetVersionService.getById(datasetVersionId) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("数据集版本ID无效"));
            }
            DatasetStatistic savedEntity = datasetStatisticService.saveDatasetStatistic(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(savedEntity, "数据集统计信息保存成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("数据集统计信息保存失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取数据集统计信息
     * @param id 数据集统计信息ID
     * @return 数据集统计信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<DatasetStatistic>> getDatasetStatisticById(@PathVariable UUID id) {
        DatasetStatistic entity = datasetStatisticService.getDatasetStatisticById(id);
        if (entity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集统计信息"));
        }
        return ResponseEntity.ok(ApiResponseDto.success(entity, "获取数据集统计信息成功"));
    }
    
    /**
     * 根据数据集版本ID获取数据集统计信息
     * @param datasetVersionId 数据集版本ID
     * @return 数据集统计信息
     */
    @GetMapping("/by-dataset-version/{datasetVersionId}")
    public ResponseEntity<ApiResponseDto<DatasetStatistic>> getDatasetStatisticByDatasetVersionId(@PathVariable UUID datasetVersionId) {
        DatasetStatistic entity = datasetStatisticService.getDatasetStatisticByDatasetVersionId(datasetVersionId);
        if (entity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("未找到指定的数据集版本统计信息"));
        }
        return ResponseEntity.ok(ApiResponseDto.success(entity, "获取数据集版本统计信息成功"));
    }
    
    /**
     * 调用数据分析服务进行数据处理
     * @param requestDto 数据分析请求DTO
     * @return 数据分析结果
     */
    @PostMapping("/analyze")
    @PreAuthorize("hasAnyAuthority('PLATFORM_ADMIN', 'INSTITUTION_SUPERVISOR', 'DATASET_UPLOADER')")
    public ResponseEntity<ApiResponseDto<DataAnalysisResponseDto>> analyzeData(@RequestBody DataAnalysisRequestDto requestDto) {
        try {
            DataAnalysisResponseDto responseDto = datasetStatisticService.analyzeData(requestDto);
            return ResponseEntity.ok(ApiResponseDto.success(responseDto, "数据分析成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("数据分析失败: " + e.getMessage()));
        }
    }

}