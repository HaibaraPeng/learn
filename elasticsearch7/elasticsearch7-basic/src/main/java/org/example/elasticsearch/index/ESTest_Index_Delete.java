package org.example.elasticsearch.index;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.elasticsearch.util.ClientUtils;

import java.io.IOException;

/**
 * @Author Roc
 * @Date 2024/12/30 16:10
 */
public class ESTest_Index_Delete {
    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = ClientUtils.getClient();
        // 删除索引
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("user");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

        boolean acknowledged = delete.isAcknowledged();
        System.out.println("acknowledged = " + acknowledged);


        restHighLevelClient.close();
    }

}
