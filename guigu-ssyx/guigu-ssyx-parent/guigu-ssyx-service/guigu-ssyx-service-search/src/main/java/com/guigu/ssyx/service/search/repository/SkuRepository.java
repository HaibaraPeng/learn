package com.guigu.ssyx.service.search.repository;

import com.guigu.ssyx.model.entity.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author Roc
 * @Date 2025/1/4 17:20
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

    //获取爆款商品
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);

    //// 判断keyword是否为空，如果为空 ，根据仓库id + 分类id查询
    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    ///如果keyword不为空根据仓库id + keyword进行查询
    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable pageable);
    //List<SkuEs> findByOrderByHotScoreDesc();
}
