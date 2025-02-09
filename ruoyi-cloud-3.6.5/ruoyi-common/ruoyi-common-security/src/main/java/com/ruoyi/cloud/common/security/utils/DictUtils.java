package com.ruoyi.cloud.common.security.utils;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.cloud.common.core.constant.CacheConstants;
import com.ruoyi.cloud.api.system.domain.SysDictData;
import com.ruoyi.cloud.common.core.utils.SpringUtils;
import com.ruoyi.cloud.common.core.utils.StringUtils;
import com.ruoyi.cloud.common.redis.service.RedisService;

import java.util.Collection;
import java.util.List;

/**
 * @Author Roc
 * @Date 2025/02/09 16:46
 */
public class DictUtils {
    /**
     * 设置字典缓存
     *
     * @param key       参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) {
        SpringUtils.getBean(RedisService.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        JSONArray arrayCache = SpringUtils.getBean(RedisService.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache)) {
            return arrayCache.toList(SysDictData.class);
        }
        return null;
    }

//    /**
//     * 删除指定字典缓存
//     *
//     * @param key 字典键
//     */
//    public static void removeDictCache(String key)
//    {
//        SpringUtils.getBean(RedisService.class).deleteObject(getCacheKey(key));
//    }
//
//    /**
//     * 清空字典缓存
//     */
//    public static void clearDictCache()
//    {
//        Collection<String> keys = SpringUtils.getBean(RedisService.class).keys(CacheConstants.SYS_DICT_KEY + "*");
//        SpringUtils.getBean(RedisService.class).deleteObject(keys);
//    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}
