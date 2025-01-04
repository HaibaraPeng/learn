package org.example.elasticsearch.index;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author Roc
 * @Date 2024/12/30 16:08
 */
public class ESTest_Index_Search {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.190.42", 9200, "http"))
        );
        // 查询索引
        GetIndexRequest getIndexRequest = new GetIndexRequest("user");

        GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);

        Map<String, List<AliasMetadata>> aliases = getIndexResponse.getAliases();
        System.out.println("aliases = " + aliases);
        Map<String, MappingMetadata> mappings = getIndexResponse.getMappings();
        System.out.println("mappings = " + mappings);
        Map<String, Settings> settings = getIndexResponse.getSettings();
        System.out.println("settings = " + settings);


        restHighLevelClient.close();
    }
}
