package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CreateDiscountRequest;
import com.laurarojas.ecommerceapi.dtos.DiscountDTO;
import com.laurarojas.ecommerceapi.dtos.ProductDTO;
import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.entity.DiscountEntity;
import com.laurarojas.ecommerceapi.entity.ProductEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public DiscountDTO createDiscount(CreateDiscountRequest request) {
        DiscountEntity discount = new DiscountEntity();
        discount.setPercentage(request.getPercentage());
        discount.setDescription(request.getDescription());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setStatus(request.getStatus());

        DiscountEntity saved = discountRepository.save(discount);

        return new DiscountDTO(
                saved.getId(),
                saved.getPercentage(),
                saved.getDescription(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getStatus()
        );
    }

    public Optional<DiscountDTO> getActiveDiscountNow() {
        Optional<DiscountEntity> activeDiscounts = discountRepository.findByStatus(Status.ACTIVE);
        LocalDateTime now = LocalDateTime.now();

        return activeDiscounts.stream()
                .filter(d -> !now.isBefore(d.getStartDate()) && !now.isAfter(d.getEndDate()))
                .findFirst()
                .map(d -> new DiscountDTO(
                        d.getId(),
                        d.getPercentage(),
                        d.getDescription(),
                        d.getStartDate(),
                        d.getEndDate(),
                        d.getStatus()
                ));
    }

}
