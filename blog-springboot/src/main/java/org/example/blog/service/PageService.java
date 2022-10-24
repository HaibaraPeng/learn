package org.example.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.blog.entity.Page;
import org.example.blog.vo.PageVO;

import java.util.List;

/**
 * @Description 页面服务
 * @Author dongp
 * @Date 2022/10/24 0024 17:10
 */
public interface PageService extends IService<Page> {

    /**
     * 获取页面列表
     *
     * @return {@link List <PageVO>} 页面列表
     */
    List<PageVO> listPages();
}
