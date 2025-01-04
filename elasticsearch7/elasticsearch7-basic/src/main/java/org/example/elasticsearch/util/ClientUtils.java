package org.example.elasticsearch.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Author Roc
 * @Date 2024/12/30 16:10
 */
public class ClientUtils {

    public static RestHighLevelClient getClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.190.42", 9200, "http"))
        );
    }
}
