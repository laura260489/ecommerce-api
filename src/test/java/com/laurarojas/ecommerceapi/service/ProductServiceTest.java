package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CreateProductRequest;
import com.laurarojas.ecommerceapi.dtos.ProductDTO;
import com.laurarojas.ecommerceapi.dtos.ProductRandom;
import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.entity.ProductEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.exceptions.ApiException;
import com.laurarojas.ecommerceapi.repository.CategoryRepository;
import com.laurarojas.ecommerceapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity productEntity;
    private CategoryEntity categoryEntity;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        categoryEntity = new CategoryEntity();
        categoryEntity.setId("category-id");
        categoryEntity.setName("Electronics");

        productEntity = new ProductEntity();
        productEntity.setId("product-id");
        productEntity.setTitle("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setPrice(new BigDecimal("100.00"));
        productEntity.setDiscountPercentage(10.0);
        productEntity.setStock(50);
        productEntity.setStatus(Status.ACTIVE);
        productEntity.setCategories(Set.of(categoryEntity));

        createRequest = new CreateProductRequest();
        createRequest.setTitle("New Product");
        createRequest.setDescription("New Description");
        createRequest.setPrice(new BigDecimal("200.00"));
        createRequest.setDiscountPercentage(15.0);
        createRequest.setStock(25);
        createRequest.setStatus(Status.ACTIVE);
        createRequest.setCategories(Arrays.asList("Electronics"));
    }

    @Test
    void getFirstPageActiveProductsByTitle_WhenProductsFound_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 6);
        List<ProductEntity> products = Arrays.asList(productEntity);
        Page<ProductEntity> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.findByTitleContainingIgnoreCaseAndStatus("Test", Status.ACTIVE, pageable))
                .thenReturn(productPage);

        Page<ProductDTO> result = productService.getFirstPageActiveProductsByTitle("Test");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        
        ProductDTO dto = result.getContent().get(0);
        assertEquals("product-id", dto.getId());
        assertEquals("Test Product", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(new BigDecimal("100.00"), dto.getPrice());
        assertEquals(10.0, dto.getDiscountPercentage());
        assertEquals(50, dto.getStock());
        assertEquals(Status.ACTIVE, dto.getStatus());

        verify(productRepository).findByTitleContainingIgnoreCaseAndStatus("Test", Status.ACTIVE, pageable);
    }

    @Test
    void getProductsByCategory_WhenProductsFound_ShouldReturnProductList() {
        when(productRepository.findByCategoriesNameAndStatus("Electronics", Status.ACTIVE))
                .thenReturn(Arrays.asList(productEntity));

        List<ProductDTO> result = productService.getProductsByCategory("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
        
        ProductDTO dto = result.get(0);
        assertEquals("product-id", dto.getId());
        assertEquals("Test Product", dto.getTitle());
        assertTrue(dto.getCategories().contains("Electronics"));

        verify(productRepository).findByCategoriesNameAndStatus("Electronics", Status.ACTIVE);
    }

    @Test
    void getProductsByCategory_WhenNoProductsFound_ShouldThrowApiException() {
        when(productRepository.findByCategoriesNameAndStatus("NonExistent", Status.ACTIVE))
                .thenReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class,
                () -> productService.getProductsByCategory("NonExistent"));

        assertEquals("No se encontraron productos para la categoría: NonExistent", exception.getMessage());
        assertEquals(404, exception.statusCode);
        assertEquals("Not Found", exception.error);

        verify(productRepository).findByCategoriesNameAndStatus("NonExistent", Status.ACTIVE);
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProductDTO() {
        when(productRepository.findById("product-id"))
                .thenReturn(Optional.of(productEntity));

        Optional<ProductDTO> result = productService.getProductById("product-id");

        assertTrue(result.isPresent());
        
        ProductDTO dto = result.get();
        assertEquals("product-id", dto.getId());
        assertEquals("Test Product", dto.getTitle());

        verify(productRepository).findById("product-id");
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldReturnEmpty() {
        when(productRepository.findById("non-existent"))
                .thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.getProductById("non-existent");

        assertFalse(result.isPresent());

        verify(productRepository).findById("non-existent");
    }

    @Test
    void getActiveProducts_ShouldReturnAllActiveProducts() {
        ProductEntity product2 = new ProductEntity();
        product2.setId("product-2");
        product2.setTitle("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("150.00"));
        product2.setDiscountPercentage(5.0);
        product2.setStock(30);
        product2.setStatus(Status.ACTIVE);
        product2.setCategories(Set.of(categoryEntity));

        when(productRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(productEntity, product2));

        List<ProductDTO> result = productService.getActiveProducts();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(productRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getRandomProducts_ShouldReturnRandomProductsWithQuantity() {
        ProductEntity product2 = new ProductEntity();
        product2.setId("product-2");
        product2.setTitle("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("150.00"));
        product2.setDiscountPercentage(5.0);
        product2.setStock(30);
        product2.setStatus(Status.ACTIVE);
        product2.setCategories(Set.of(categoryEntity));

        when(productRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(productEntity, product2));

        List<ProductRandom> result = productService.getRandomProducts();

        assertNotNull(result);
        assertTrue(result.size() <= 5);
        
        for (ProductRandom randomProduct : result) {
            assertNotNull(randomProduct.getProduct());
            assertTrue(randomProduct.getQuantity() >= 1 && randomProduct.getQuantity() <= 3);
        }

        verify(productRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void createProduct_WhenValidRequest_ShouldReturnProductDTO() {
        when(categoryRepository.findByNameIgnoreCase("Electronics"))
                .thenReturn(Optional.of(categoryEntity));

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId("new-product-id");
        savedProduct.setTitle("New Product");
        savedProduct.setDescription("New Description");
        savedProduct.setPrice(new BigDecimal("200.00"));
        savedProduct.setDiscountPercentage(15.0);
        savedProduct.setStock(25);
        savedProduct.setStatus(Status.ACTIVE);
        savedProduct.setCategories(Set.of(categoryEntity));

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(createRequest);

        assertNotNull(result);
        assertEquals("new-product-id", result.getId());
        assertEquals("New Product", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(new BigDecimal("200.00"), result.getPrice());
        assertEquals(15.0, result.getDiscountPercentage());
        assertEquals(25, result.getStock());
        assertEquals(Status.ACTIVE, result.getStatus());
        assertTrue(result.getCategories().contains("Electronics"));

        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_WhenCategoryNotFound_ShouldThrowEntityNotFoundException() {
        when(categoryRepository.findByNameIgnoreCase("Electronics"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> productService.createProduct(createRequest));

        assertEquals("Categoría no encontrada: Electronics", exception.getMessage());

        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_WithMultipleCategories_ShouldMapAllCategories() {
        CategoryEntity category2 = new CategoryEntity();
        category2.setId("category-2");
        category2.setName("Books");

        createRequest.setCategories(Arrays.asList("Electronics", "Books"));

        when(categoryRepository.findByNameIgnoreCase("Electronics"))
                .thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.findByNameIgnoreCase("Books"))
                .thenReturn(Optional.of(category2));

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId("multi-category-product");
        savedProduct.setTitle("New Product");
        savedProduct.setDescription("New Description");
        savedProduct.setPrice(new BigDecimal("200.00"));
        savedProduct.setDiscountPercentage(15.0);
        savedProduct.setStock(25);
        savedProduct.setStatus(Status.ACTIVE);
        savedProduct.setCategories(Set.of(categoryEntity, category2));

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(createRequest);

        assertNotNull(result);
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("Electronics"));
        assertTrue(result.getCategories().contains("Books"));

        verify(categoryRepository).findByNameIgnoreCase("Electronics");
        verify(categoryRepository).findByNameIgnoreCase("Books");
    }

    @Test
    void createProduct_ShouldSetAllFieldsCorrectly() {
        when(categoryRepository.findByNameIgnoreCase("Electronics"))
                .thenReturn(Optional.of(categoryEntity));

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId("saved-product-id");
        savedProduct.setTitle(createRequest.getTitle());
        savedProduct.setDescription(createRequest.getDescription());
        savedProduct.setPrice(createRequest.getPrice());
        savedProduct.setDiscountPercentage(createRequest.getDiscountPercentage());
        savedProduct.setStock(createRequest.getStock());
        savedProduct.setStatus(createRequest.getStatus());
        savedProduct.setCategories(Set.of(categoryEntity));

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(createRequest);

        verify(productRepository).save(argThat(product -> 
                "New Product".equals(product.getTitle()) &&
                "New Description".equals(product.getDescription()) &&
                new BigDecimal("200.00").equals(product.getPrice()) &&
                Double.valueOf(15.0).equals(product.getDiscountPercentage()) &&
                Integer.valueOf(25).equals(product.getStock()) &&
                Status.ACTIVE.equals(product.getStatus())
        ));

        assertEquals("saved-product-id", result.getId());
    }
}