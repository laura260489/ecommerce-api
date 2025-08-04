package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CreateOrderRequest;
import com.laurarojas.ecommerceapi.dtos.OrderDTO;
import com.laurarojas.ecommerceapi.entity.OrderEntity;
import com.laurarojas.ecommerceapi.entity.OrderItemEntity;
import com.laurarojas.ecommerceapi.entity.ProductEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.exceptions.ApiException;
import com.laurarojas.ecommerceapi.repository.OrderRepository;
import com.laurarojas.ecommerceapi.repository.ProductRepository;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(Status.ACTIVE);

        Set<OrderItemEntity> orderItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var entry : request.getProducts().entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new ApiException(
                            "Producto no encontrado con ID: " + productId,
                            404,
                            "Not Found"
                    ));

            if (product.getStock() < quantity) {
                throw new ApiException(
                        "Stock insuficiente para producto: " + product.getTitle(),
                        404,
                        "Illegal exception"
                );

            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        OrderEntity savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }
    private OrderDTO mapToDTO(OrderEntity order) {
        Map<String, Integer> products = order.getOrderItems().stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                item -> item.getProduct().getId(),
                                OrderItemEntity::getQuantity
                        )
                );

        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                products
        );
    }

}
