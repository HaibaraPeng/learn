package org.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.blog.dto.UniqueViewDTO;
import org.example.blog.entity.UniqueView;

import java.util.List;

/**
 * @Description 用户量统计
 * @Author roc
 * @Date 2022/10/24 下午10:34
 */
public interface UniqueViewService extends IService<UniqueView> {

    /**
     * 获取7天用户量统计
     *
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueViews();

}
