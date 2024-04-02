package com.delivery.app.product.template.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = {"*"})
@RestController
@RequestMapping("/api/product/template")
@RequiredArgsConstructor
@Validated
public class ProductTemplateController {
}
