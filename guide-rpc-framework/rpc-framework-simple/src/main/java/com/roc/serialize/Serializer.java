package com.roc.serialize;

import com.roc.extension.SPI;

/**
 * @Description 序列化器，所有序列化类都要实现这个接口
 * @Author dongp
 * @Date 2022/8/8 0008 15:45
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
