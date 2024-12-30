package org.example.elasticsearch.util;

import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Author Roc
 * @Date 2024/12/30 16:13
 */
public interface ElasticsearchTask {

    void doSomething(RestHighLevelClient client) throws Exception;
}
