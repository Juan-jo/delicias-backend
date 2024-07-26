package com.delivery.app.elasticsearch.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.delivery.app.elasticsearch.document.ElasticSearchTodoDoc;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@AllArgsConstructor
@Service
public class ElasticSearchTodoService {

    public static String todoDocName = "test_todo";

    private final ElasticsearchClient esClient;

    public void add(String name) throws IOException {

        ElasticSearchTodoDoc todoDoc = new ElasticSearchTodoDoc(
                name
        );


        IndexRequest<ElasticSearchTodoDoc> todoReq = IndexRequest.of((id -> id
                .index(todoDocName)
                .refresh(Refresh.WaitFor)
                .document(todoDoc)));

        esClient.index(todoReq);
    }
}
