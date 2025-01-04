package org.example.elasticsearch.util;

import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Author Roc
 * @Date 2024/12/30 16:13
 */
public class ConnectElasticsearch {

    public static void connect(ElasticsearchTask task) {
        RestHighLevelClient client = ClientUtils.getClient();
        try {
            task.doSomething(client);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
