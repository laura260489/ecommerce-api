package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CategoryDTO;
import com.laurarojas.ecommerceapi.dtos.CreateCategoryRequest;
import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.exceptions.ApiException;
import com.laurarojas.ecommerceapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity categoryEntity;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        categoryEntity = new CategoryEntity();
        categoryEntity.setId("category-id");
        categoryEntity.setName("Electronics");
        categoryEntity.setDescription("Electronic products");
        categoryEntity.setStatus(Status.ACTIVE);

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Books");
        createRequest.setDescription("Book products");
        createRequest.setStatus(Status.ACTIVE);
    }

    @Test
    void getActiveCategories_WhenCategoriesExist_ShouldReturnCategoryList() {
        CategoryEntity category1 = new CategoryEntity();
        category1.setId("cat1");
        category1.setName("Category 1");
        category1.setDescription("Description 1");
        category1.setStatus(Status.ACTIVE);

        CategoryEntity category2 = new CategoryEntity();
        category2.setId("cat2");
        category2.setName("Category 2");
        category2.setDescription("Description 2");
        category2.setStatus(Status.ACTIVE);

        when(categoryRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(category1, category2));

        List<CategoryDTO> result = categoryService.getActiveCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        
        CategoryDTO dto1 = result.get(0);
        assertEquals("cat1", dto1.getId());
        assertEquals("Category 1", dto1.getName());
        assertEquals("Description 1", dto1.getDescription());
        assertEquals(Status.ACTIVE, dto1.getStatus());

        CategoryDTO dto2 = result.get(1);
        assertEquals("cat2", dto2.getId());
        assertEquals("Category 2", dto2.getName());
        assertEquals("Description 2", dto2.getDescription());
        assertEquals(Status.ACTIVE, dto2.getStatus());

        verify(categoryRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getActiveCategories_WhenNoCategoriesExist_ShouldThrowApiException() {
        when(categoryRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class,
                () -> categoryService.getActiveCategories());

        assertEquals("No existen categorías disponibles", exception.getMessage());
        assertEquals(404, exception.statusCode);
        assertEquals("Not Found", exception.error);

        verify(categoryRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void createCategory_WhenValidRequest_ShouldReturnCategoryDTO() {
        CategoryEntity savedEntity = new CategoryEntity();
        savedEntity.setId("new-category-id");
        savedEntity.setName("Books");
        savedEntity.setDescription("Book products");
        savedEntity.setStatus(Status.ACTIVE);

        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenReturn(savedEntity);

        CategoryDTO result = categoryService.createCategory(createRequest);

        assertNotNull(result);
        assertEquals("new-category-id", result.getId());
        assertEquals("Books", result.getName());
        assertEquals("Book products", result.getDescription());
        assertEquals(Status.ACTIVE, result.getStatus());

        verify(categoryRepository).save(argThat(category -> 
                "Books".equals(category.getName()) &&
                "Book products".equals(category.getDescription()) &&
                Status.ACTIVE.equals(category.getStatus())
        ));
    }

    @Test
    void createCategory_WhenRepositorySaves_ShouldCallSaveWithCorrectData() {
        CategoryEntity savedEntity = new CategoryEntity();
        savedEntity.setId("saved-id");
        savedEntity.setName(createRequest.getName());
        savedEntity.setDescription(createRequest.getDescription());
        savedEntity.setStatus(createRequest.getStatus());

        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenReturn(savedEntity);

        CategoryDTO result = categoryService.createCategory(createRequest);

        verify(categoryRepository).save(any(CategoryEntity.class));
        assertNotNull(result);
        assertEquals("saved-id", result.getId());
    }

    @Test
    void createCategory_WhenInactiveStatus_ShouldCreateInactiveCategory() {
        createRequest.setStatus(Status.INACTIVE);

        CategoryEntity savedEntity = new CategoryEntity();
        savedEntity.setId("inactive-category-id");
        savedEntity.setName("Books");
        savedEntity.setDescription("Book products");
        savedEntity.setStatus(Status.INACTIVE);

        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenReturn(savedEntity);

        CategoryDTO result = categoryService.createCategory(createRequest);

        assertNotNull(result);
        assertEquals(Status.INACTIVE, result.getStatus());

        verify(categoryRepository).save(argThat(category -> 
                Status.INACTIVE.equals(category.getStatus())
        ));
    }
}