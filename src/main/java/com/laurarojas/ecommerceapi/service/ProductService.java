package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CreateProductRequest;
import com.laurarojas.ecommerceapi.dtos.ProductDTO;
import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.entity.ProductEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.CategoryRepository;
import com.laurarojas.ecommerceapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductDTO> getFirstPageActiveProductsByTitle(String title) {
        Pageable pageable = PageRequest.of(0, 6);
        return productRepository
                .findByTitleContainingIgnoreCaseAndStatus(title, Status.ACTIVE, pageable)
                .map(this::mapToDTO);
    }

    public List<ProductDTO> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoriesNameAndStatus(categoryName, Status.ACTIVE)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public Optional<ProductDTO> getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::mapToDTO);
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        Set<CategoryEntity> categories = request.getCategories().stream()
                .map(name -> categoryRepository.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada: " + name)))
                .collect(Collectors.toSet());

        ProductEntity product = new ProductEntity();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());
        product.setCategories(categories);

        ProductEntity saved = productRepository.save(product);

        return new ProductDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getDiscountPercentage(),
                saved.getStock(),
                saved.getStatus(),
                saved.getCategories().stream().map(CategoryEntity::getName).collect(Collectors.toSet())
        );
    }

    private ProductDTO mapToDTO(ProductEntity product) {
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getStock(),
                product.getStatus(),
                product.getCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.toSet())
        );
    }



}
