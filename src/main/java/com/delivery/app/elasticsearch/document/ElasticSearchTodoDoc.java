package com.delivery.app.elasticsearch.document;

import lombok.Builder;

@Builder
public record ElasticSearchTodoDoc(
        String name) {

}