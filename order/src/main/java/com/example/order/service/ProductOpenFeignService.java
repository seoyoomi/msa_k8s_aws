package com.example.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.order.domain.dto.ProductResponseDTO;


/*
k8s service
@FeignClient(name = "product-service", url="http://product-service")
*/

// 각각의 service찾음
@FeignClient(name = "product-service", url="http://product-service")
public interface ProductOpenFeignService {
    
    // product controller의 getMapping
    // order -> product ctrl로 요청을 보냄
    // http://product-service/product/{파라미터에 있는 pathVariable : productId}
    // 인터페이스의 구현부는 product ctrl에 구현
    @GetMapping("/product/{productId}")
    public ProductResponseDTO getProductId(@PathVariable(name = "productId") Long productId,
                                             @RequestHeader("X-User-Email") String email);
}
