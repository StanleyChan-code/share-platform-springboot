package cn.com.nabotix.shareplatform.popularity.controller;

import cn.com.nabotix.shareplatform.common.dto.ApiResponseDto;
import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.service.ManageDatasetService;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.researchsubject.service.ResearchSubjectService;
import cn.com.nabotix.shareplatform.popularity.service.PopularityService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 统计信息控制器
 * 提供数据集和研究学科的热度统计接口
 *
 * @author 陈雍文
 */
@RestController
@RequestMapping("/api/popularity")
@RequiredArgsConstructor
public class PopularityController {

    private final PopularityService popularityService;
    private final ResearchSubjectService researchSubjectService;
    private final ManageDatasetService manageDatasetService;

    /**
     * 获取热门数据集列表（按热度排序）
     *
     * @param size 每页大小
     * @return 热门数据集列表
     */
    @GetMapping("/datasets/popular")
    public ResponseEntity<ApiResponseDto<Page<Dataset>>> getPopularDatasets(
            @RequestParam(defaultValue = "10")
            @Max(value = 20)
            @Min(value = 1)
            int size
    ) {
        // 创建排序对象，按searchCount降序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "searchCount");
        Pageable pageable = PageRequest.of(0, size, sort);

        Page<Dataset> popularDatasets = manageDatasetService.getAllDatasets(pageable);

        return ResponseEntity.ok(ApiResponseDto.success(popularDatasets, "获取热门数据集列表成功"));
    }

    /**
     * 获取热门研究学科列表（按热度排序）
     *
     * @return 热门研究学科列表
     */
    @GetMapping("/subjects/popular")
    public ResponseEntity<ApiResponseDto<Page<ResearchSubject>>> getPopularSubjects() {
        // 创建排序对象，按searchCount降序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "searchCount");
        Pageable pageable = PageRequest.of(0, 1000, sort);

        Page<ResearchSubject> popularSubjects = researchSubjectService.getAllSubjects(pageable);

        return ResponseEntity.ok(ApiResponseDto.success(popularSubjects, "获取热门研究学科列表成功"));
    }

    /**
     * 获取特定数据集的热度值
     *
     * @param datasetId 数据集ID
     * @return 热度值
     */
    @GetMapping("/datasets/popularity")
    public ResponseEntity<ApiResponseDto<Long>> getDatasetPopularity(@RequestParam UUID datasetId) {
        Long popularity = popularityService.getDatasetPopularity(datasetId);
        return ResponseEntity.ok(ApiResponseDto.success(popularity, "获取数据集热度成功"));
    }

    /**
     * 获取特定研究学科的热度值
     *
     * @param subjectId 研究学科ID
     * @return 热度值
     */
    @GetMapping("/subjects/popularity")
    public ResponseEntity<ApiResponseDto<Long>> getSubjectPopularity(@RequestParam UUID subjectId) {
        Long popularity = popularityService.getSubjectPopularity(subjectId);
        return ResponseEntity.ok(ApiResponseDto.success(popularity, "获取研究学科热度成功"));
    }
}