package com.example.order.ctrl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.domain.dto.OrderRequestDTO;
import com.example.order.domain.dto.OrderResponseDTO;
import com.example.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    
    // 주문에 대한 정보 DTO에 담겨 파라미터로 들어옴
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrderRequestDTO request,
                                    @RequestHeader("X-User-Email") String email) {
        System.out.println(">>>> order ctrl path : /create"); 
        System.out.println(">>>> params : "+ request); 
        
        // service에 전달
        OrderResponseDTO response =  orderService.orderFeignKafkaCreate(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
