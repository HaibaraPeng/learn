package org.example.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description 资源角色
 * @Author dongp
 * @Date 2022/10/24 0024 18:24
 */
@Data
public class ResourceRoleDTO {

    /**
     * 资源id
     */
    private Integer id;

    /**
     * 路径
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 角色名
     */
    private List<String> roleList;
}
