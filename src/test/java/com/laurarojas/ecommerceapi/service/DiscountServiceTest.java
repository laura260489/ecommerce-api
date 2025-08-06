package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CreateDiscountRequest;
import com.laurarojas.ecommerceapi.dtos.DiscountDTO;
import com.laurarojas.ecommerceapi.entity.DiscountEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private CreateDiscountRequest createRequest;
    private DiscountEntity discountEntity;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        createRequest = new CreateDiscountRequest();
        createRequest.setPercentage(10.0);
        createRequest.setDescription("Test discount");
        createRequest.setStartDate(now.minusDays(1));
        createRequest.setEndDate(now.plusDays(30));
        createRequest.setStatus(Status.ACTIVE);

        discountEntity = new DiscountEntity();
        discountEntity.setId("discount-id");
        discountEntity.setPercentage(10.0);
        discountEntity.setDescription("Test discount");
        discountEntity.setStartDate(now.minusDays(1));
        discountEntity.setEndDate(now.plusDays(30));
        discountEntity.setStatus(Status.ACTIVE);
    }

    @Test
    void createDiscount_WhenValidRequest_ShouldReturnDiscountDTO() {
        when(discountRepository.save(any(DiscountEntity.class)))
                .thenReturn(discountEntity);

        DiscountDTO result = discountService.createDiscount(createRequest);

        assertNotNull(result);
        assertEquals("discount-id", result.getId());
        assertEquals(10.0, result.getPercentage());
        assertEquals("Test discount", result.getDescription());
        assertEquals(discountEntity.getStartDate(), result.getStartDate());
        assertEquals(discountEntity.getEndDate(), result.getEndDate());
        assertEquals(Status.ACTIVE, result.getStatus());

        verify(discountRepository).save(argThat(discount -> 
                discount.getPercentage().equals(10.0) &&
                "Test discount".equals(discount.getDescription()) &&
                Status.ACTIVE.equals(discount.getStatus())
        ));
    }

    @Test
    void createDiscount_ShouldMapAllFieldsCorrectly() {
        DiscountEntity savedEntity = new DiscountEntity();
        savedEntity.setId("new-discount-id");
        savedEntity.setPercentage(createRequest.getPercentage());
        savedEntity.setDescription(createRequest.getDescription());
        savedEntity.setStartDate(createRequest.getStartDate());
        savedEntity.setEndDate(createRequest.getEndDate());
        savedEntity.setStatus(createRequest.getStatus());

        when(discountRepository.save(any(DiscountEntity.class)))
                .thenReturn(savedEntity);

        DiscountDTO result = discountService.createDiscount(createRequest);

        verify(discountRepository).save(any(DiscountEntity.class));
        assertNotNull(result);
        assertEquals("new-discount-id", result.getId());
        assertEquals(createRequest.getPercentage(), result.getPercentage());
        assertEquals(createRequest.getDescription(), result.getDescription());
        assertEquals(createRequest.getStartDate(), result.getStartDate());
        assertEquals(createRequest.getEndDate(), result.getEndDate());
        assertEquals(createRequest.getStatus(), result.getStatus());
    }

    @Test
    void getActiveDiscountNow_WhenCurrentDiscountsExist_ShouldReturnCurrentDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        
        DiscountEntity currentDiscount = new DiscountEntity();
        currentDiscount.setId("current-discount");
        currentDiscount.setPercentage(15.0);
        currentDiscount.setDescription("Current discount");
        currentDiscount.setStartDate(now.minusDays(5));
        currentDiscount.setEndDate(now.plusDays(5));
        currentDiscount.setStatus(Status.ACTIVE);

        DiscountEntity expiredDiscount = new DiscountEntity();
        expiredDiscount.setId("expired-discount");
        expiredDiscount.setPercentage(20.0);
        expiredDiscount.setDescription("Expired discount");
        expiredDiscount.setStartDate(now.minusDays(10));
        expiredDiscount.setEndDate(now.minusDays(1));
        expiredDiscount.setStatus(Status.ACTIVE);

        DiscountEntity futureDiscount = new DiscountEntity();
        futureDiscount.setId("future-discount");
        futureDiscount.setPercentage(25.0);
        futureDiscount.setDescription("Future discount");
        futureDiscount.setStartDate(now.plusDays(1));
        futureDiscount.setEndDate(now.plusDays(10));
        futureDiscount.setStatus(Status.ACTIVE);

        when(discountRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(currentDiscount, expiredDiscount, futureDiscount));

        List<DiscountDTO> result = discountService.getActiveDiscountNow();

        assertNotNull(result);
        assertEquals(1, result.size());
        
        DiscountDTO currentDto = result.get(0);
        assertEquals("current-discount", currentDto.getId());
        assertEquals(15.0, currentDto.getPercentage());
        assertEquals("Current discount", currentDto.getDescription());

        verify(discountRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getActiveDiscountNow_WhenNoCurrentDiscounts_ShouldReturnEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        
        DiscountEntity expiredDiscount = new DiscountEntity();
        expiredDiscount.setId("expired-discount");
        expiredDiscount.setPercentage(20.0);
        expiredDiscount.setDescription("Expired discount");
        expiredDiscount.setStartDate(now.minusDays(10));
        expiredDiscount.setEndDate(now.minusDays(1));
        expiredDiscount.setStatus(Status.ACTIVE);

        when(discountRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(expiredDiscount));

        List<DiscountDTO> result = discountService.getActiveDiscountNow();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(discountRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getActiveDiscountNow_WhenNoActiveDiscounts_ShouldReturnEmptyList() {
        when(discountRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Collections.emptyList());

        List<DiscountDTO> result = discountService.getActiveDiscountNow();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(discountRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getActiveDiscountNow_WhenDiscountStartsToday_ShouldIncludeDiscount() {
        LocalDateTime now = LocalDateTime.now();
        
        DiscountEntity todayDiscount = new DiscountEntity();
        todayDiscount.setId("today-discount");
        todayDiscount.setPercentage(30.0);
        todayDiscount.setDescription("Today discount");
        todayDiscount.setStartDate(now);
        todayDiscount.setEndDate(now.plusDays(5));
        todayDiscount.setStatus(Status.ACTIVE);

        when(discountRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(todayDiscount));

        List<DiscountDTO> result = discountService.getActiveDiscountNow();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("today-discount", result.get(0).getId());

        verify(discountRepository).findByStatus(Status.ACTIVE);
    }

}