package com.delivery.app.elasticsearch.controller;

import com.delivery.app.configs.validation.common.OnCreate;
import com.delivery.app.elasticsearch.document.ElasticSearchTodoDoc;
import com.delivery.app.elasticsearch.services.ElasticSearchTodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Validated
public class ElasticSearchTodoController {

    private final ElasticSearchTodoService todoService;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<?> create(
            @Valid @RequestBody ElasticSearchTodoDoc todoDoc
    ) throws IOException {

        todoService.add(todoDoc.name());

        return  ResponseEntity.ok().build();
    }
}
