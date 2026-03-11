package com.example.order.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.dao.OrderRepository;
import com.example.order.domain.dto.OrderRequestDTO;
import com.example.order.domain.dto.OrderResponseDTO;
import com.example.order.domain.dto.ProductResponseDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductOpenFeignService productOpenFeignService;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    // 직접 접근 못 하니 내부통신 msa 내부에서 통신하기 위한 것 openfeign

    /*
    1. Client(path: /order/create) -> 주문요청
    2. orderFeignKafkaCreate() 실행
    3. Feign 상품 조회 (동기방식)
    4. 재고 여부 확인
    5. Kafka 토픽(KafkaTemplate.send()를 발행)
    6. kafka broker에 메시지 저장
    7. product-service에서 수신(kafkaListener)하여 stockConsumer() 호출
    8. 재고감소구현 (비동기)
    */
   @CircuitBreaker(name = "productService", fallbackMethod = "fallbackProductService")
    public OrderResponseDTO orderFeignKafkaCreate(OrderRequestDTO request, String email){
        System.out.println(">>> order service orderFeignKafkaCreate"); 
        System.out.println(">>> 재고 유뮤 판단을 위해서 product-service feign 통신");
        productOpenFeignService.getProductId(request.getProductId(), email);
        ProductResponseDTO response = productOpenFeignService.getProductId(request.getProductId(), email);
        System.out.println(">>> feign 통신결과 수량 확인 : " + response.getStockQty());
        if(response.getStockQty() < request.getQty()){
            throw new RuntimeException("재고부족");
        } else {
            System.out.println(">>> order service Kafka 토픽 발행");
            kafkaTemplate.send("update-stock-topic", request);
        }

        // 주문 저장
        return OrderResponseDTO.fromEntity(orderRepository.save(request.toEntity(email)));
    }
    public OrderResponseDTO fallbackProductService( OrderRequestDTO request, 
                                                    String email, 
                                                    Throwable t){
        throw new RuntimeException("서비스 지연으로 에러 발생. 다시 시도해주세요");                                               
    }
}
