package org.example.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.blog.dto.UniqueViewDTO;
import org.example.blog.entity.UniqueView;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Description 访问量
 * @Author roc
 * @Date 2022/10/24 下午10:39
 */
@Repository
public interface UniqueViewDao extends BaseMapper<UniqueView> {

    /**
     * 获取7天用户量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueViews(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
