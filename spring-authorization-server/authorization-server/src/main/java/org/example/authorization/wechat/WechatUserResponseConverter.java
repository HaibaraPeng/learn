package org.example.authorization.wechat;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/2/13 14:19
 */
public class WechatUserResponseConverter extends MappingJackson2HttpMessageConverter {

    public WechatUserResponseConverter() {
        List<MediaType> mediaTypes = new ArrayList<>(super.getSupportedMediaTypes());
        // 微信获取用户信息时响应的类型为“text/plain”，这里特殊处理一下
        mediaTypes.add(MediaType.TEXT_PLAIN);
        super.setSupportedMediaTypes(mediaTypes);
    }
}
