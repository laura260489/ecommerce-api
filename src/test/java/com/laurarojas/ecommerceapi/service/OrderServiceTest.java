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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest createOrderRequest;
    private UserEntity userEntity;
    private ProductEntity productEntity;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId("user-id");
        Map<String, Integer> products = new HashMap<>();
        products.put("product-id", 2);
        createOrderRequest.setProducts(products);

        userEntity = new UserEntity();
        userEntity.setId("user-id");
        userEntity.setFirstName("Laura");
        userEntity.setLastName("Rojas");

        productEntity = new ProductEntity();
        productEntity.setId("product-id");
        productEntity.setTitle("Test Product");
        productEntity.setPrice(new BigDecimal("100.00"));
        productEntity.setStock(10);

        orderEntity = new OrderEntity();
        orderEntity.setId("order-id");
        orderEntity.setUser(userEntity);
        orderEntity.setStatus(Status.ACTIVE);
        orderEntity.setTotalAmount(new BigDecimal("200.00"));
        orderEntity.setCreatedAt(new Date());
        orderEntity.setUpdatedAt(new Date());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setProduct(productEntity);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(new BigDecimal("100.00"));
        orderItem.setOrder(orderEntity);

        Set<OrderItemEntity> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        orderEntity.setOrderItems(orderItems);
    }

    @Test
    void createOrder_WhenValidRequest_ShouldReturnOrderDTO() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.of(productEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDTO result = orderService.createOrder(createOrderRequest);

        assertNotNull(result);
        assertEquals("order-id", result.getOrderId());
        assertEquals("user-id", result.getUserId());
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getProducts());
        assertEquals(1, result.getProducts().size());
        assertEquals(2, result.getProducts().get("product-id"));

        verify(userRepository).findById("user-id");
        verify(productRepository).findById("product-id");
        verify(productRepository).save(argThat(product -> product.getStock() == 8));
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_WhenUserNotFound_ShouldThrowIllegalArgumentException() {
        when(userRepository.findById("user-id")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(createOrderRequest));

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(userRepository).findById("user-id");
        verify(productRepository, never()).findById(anyString());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_WhenProductNotFound_ShouldThrowApiException() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.createOrder(createOrderRequest));

        assertEquals("Producto no encontrado con ID: product-id", exception.getMessage());
        assertEquals(404, exception.statusCode);
        assertEquals("Not Found", exception.error);

        verify(userRepository).findById("user-id");
        verify(productRepository).findById("product-id");
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_WhenInsufficientStock_ShouldThrowApiException() {
        productEntity.setStock(1);
        
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.of(productEntity));

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.createOrder(createOrderRequest));

        assertEquals("Stock insuficiente para producto: Test Product", exception.getMessage());
        assertEquals(404, exception.statusCode);
        assertEquals("Illegal exception", exception.error);

        verify(userRepository).findById("user-id");
        verify(productRepository).findById("product-id");
        verify(productRepository, never()).save(any(ProductEntity.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_ShouldUpdateProductStock() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.of(productEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        orderService.createOrder(createOrderRequest);

        verify(productRepository).save(argThat(product -> 
                product.getId().equals("product-id") && product.getStock() == 8
        ));
    }

    @Test
    void createOrder_ShouldCalculateTotalAmountCorrectly() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.of(productEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity order = invocation.getArgument(0);
            order.setId("saved-order-id");
            return order;
        });

        OrderDTO result = orderService.createOrder(createOrderRequest);

        verify(orderRepository).save(argThat(order -> 
                order.getTotalAmount().equals(new BigDecimal("200.00"))
        ));
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
    }

    @Test
    void createOrder_WithMultipleProducts_ShouldProcessAllProducts() {
        Map<String, Integer> products = new HashMap<>();
        products.put("product-1", 1);
        products.put("product-2", 3);
        createOrderRequest.setProducts(products);

        ProductEntity product1 = new ProductEntity();
        product1.setId("product-1");
        product1.setTitle("Product 1");
        product1.setPrice(new BigDecimal("50.00"));
        product1.setStock(5);

        ProductEntity product2 = new ProductEntity();
        product2.setId("product-2");
        product2.setTitle("Product 2");
        product2.setPrice(new BigDecimal("30.00"));
        product2.setStock(10);

        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product1));
        when(productRepository.findById("product-2")).thenReturn(Optional.of(product2));

        OrderEntity multiProductOrder = new OrderEntity();
        multiProductOrder.setId("multi-order-id");
        multiProductOrder.setUser(userEntity);
        multiProductOrder.setStatus(Status.ACTIVE);
        multiProductOrder.setTotalAmount(new BigDecimal("140.00"));
        multiProductOrder.setCreatedAt(new Date());
        multiProductOrder.setUpdatedAt(new Date());

        OrderItemEntity item1 = new OrderItemEntity();
        item1.setProduct(product1);
        item1.setQuantity(1);
        item1.setOrder(multiProductOrder);

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setProduct(product2);
        item2.setQuantity(3);
        item2.setOrder(multiProductOrder);

        Set<OrderItemEntity> orderItems = new HashSet<>();
        orderItems.add(item1);
        orderItems.add(item2);
        multiProductOrder.setOrderItems(orderItems);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(multiProductOrder);

        OrderDTO result = orderService.createOrder(createOrderRequest);

        assertNotNull(result);
        assertEquals("multi-order-id", result.getOrderId());
        assertEquals(new BigDecimal("140.00"), result.getTotalAmount());
        assertEquals(2, result.getProducts().size());

        verify(productRepository).findById("product-1");
        verify(productRepository).findById("product-2");
        verify(productRepository).save(argThat(p -> p.getId().equals("product-1") && p.getStock() == 4));
        verify(productRepository).save(argThat(p -> p.getId().equals("product-2") && p.getStock() == 7));
    }

    @Test
    void createOrder_ShouldSetOrderStatusToActive() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(userEntity));
        when(productRepository.findById("product-id")).thenReturn(Optional.of(productEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDTO result = orderService.createOrder(createOrderRequest);

        verify(orderRepository).save(argThat(order -> 
                Status.ACTIVE.equals(order.getStatus())
        ));
        assertEquals("ACTIVE", result.getStatus());
    }
}