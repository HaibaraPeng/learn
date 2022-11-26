package com.roc.shop.common.serializer.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description ImgJsonSerializer
 * @Author roc
 * @Date 2022/11/26 下午3:00
 */
@Component
public class ImgJsonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StrUtil.isBlank(value)) {
            jsonGenerator.writeString(StrUtil.EMPTY);
            return;
        }
        String[] imgs = value.split(StrUtil.COMMA);
        StringBuilder sb = new StringBuilder();
        for (String img : imgs) {
            sb.append("www.roc.com/").append(img).append(StrUtil.COMMA);
        }
        sb.deleteCharAt(sb.length() - 1);
        jsonGenerator.writeString(sb.toString());
    }
}
