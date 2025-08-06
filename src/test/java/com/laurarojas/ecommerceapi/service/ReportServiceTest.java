package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.ProductReportDTO;
import com.laurarojas.ecommerceapi.dtos.UserReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private ReportService reportService;



    @Test
    void generateReport_WhenTypeIsProducts_ShouldReturnProductReportList() {
        Object[] row1 = {"product-1", "Product 1", "Electronics", 150L};
        Object[] row2 = {"product-2", "Product 2", "Books", 100L};
        
        when(entityManager.createStoredProcedureQuery("obtener_productos_mas_vendidos"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList())
                .thenReturn(Arrays.asList(row1, row2));

        List<?> result = reportService.generateReport("products");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof ProductReportDTO);
        assertTrue(result.get(1) instanceof ProductReportDTO);

        ProductReportDTO dto1 = (ProductReportDTO) result.get(0);
        assertEquals("product-1", dto1.getId());
        assertEquals("Product 1", dto1.getTitle());
        assertEquals("Electronics", dto1.getCategory());
        assertEquals(150L, dto1.getTotalSell());

        ProductReportDTO dto2 = (ProductReportDTO) result.get(1);
        assertEquals("product-2", dto2.getId());
        assertEquals("Product 2", dto2.getTitle());
        assertEquals("Books", dto2.getCategory());
        assertEquals(100L, dto2.getTotalSell());

        verify(entityManager).createStoredProcedureQuery("obtener_productos_mas_vendidos");
        verify(storedProcedureQuery).getResultList();
    }

    @Test
    void generateReport_WhenTypeIsUsers_ShouldReturnUserReportList() {
        Object[] row1 = {"Laura Rojas", "laura@email.com", 25L};
        Object[] row2 = {"Martha Gonzales", "martha@email.com", 15L};
        
        when(entityManager.createStoredProcedureQuery("obtener_clientes_frecuentes"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList())
                .thenReturn(Arrays.asList(row1, row2));

        List<?> result = reportService.generateReport("users");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof UserReportDTO);
        assertTrue(result.get(1) instanceof UserReportDTO);

        UserReportDTO dto1 = (UserReportDTO) result.get(0);
        assertEquals("Laura Rojas", dto1.getName());
        assertEquals("laura@email.com", dto1.getEmail());
        assertEquals(25L, dto1.getTotalOrders());

        UserReportDTO dto2 = (UserReportDTO) result.get(1);
        assertEquals("Martha Gonzales", dto2.getName());
        assertEquals("martha@email.com", dto2.getEmail());
        assertEquals(15L, dto2.getTotalOrders());

        verify(entityManager).createStoredProcedureQuery("obtener_clientes_frecuentes");
        verify(storedProcedureQuery).getResultList();
    }


    @Test
    void generateReport_WhenTypeIsUnsupported_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.generateReport("invalid"));

        assertEquals("Tipo de reporte no soportado: invalid", exception.getMessage());

        verify(entityManager, never()).createStoredProcedureQuery(anyString());
    }

    @Test
    void generateReport_WhenEmptyResults_ShouldReturnEmptyList() {
        when(entityManager.createStoredProcedureQuery("obtener_productos_mas_vendidos"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList())
                .thenReturn(Arrays.asList());

        List<?> result = reportService.generateReport("products");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager).createStoredProcedureQuery("obtener_productos_mas_vendidos");
        verify(storedProcedureQuery).getResultList();
    }

}