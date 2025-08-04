package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.ProductReportDTO;
import com.laurarojas.ecommerceapi.dtos.UserReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<?> generateReport(String type) {
        if (type.equalsIgnoreCase("products")) {
            List<Object[]> results = entityManager
                    .createStoredProcedureQuery("obtener_productos_mas_vendidos")
                    .getResultList();

            List<ProductReportDTO> report = new ArrayList<>();
            for (Object[] row : results) {
                String id = (String) row[0];
                String title = (String) row[1];
                String category = (String) row[2];
                Long totalSells = ((Number) row[3]).longValue();

                report.add(new ProductReportDTO(id, title, category, totalSells));
            }
            return report;

        } else if(type.equalsIgnoreCase("users")) {
            List<Object[]> results = entityManager
                    .createStoredProcedureQuery("obtener_clientes_frecuentes")
                    .getResultList();

            List<UserReportDTO> report = new ArrayList<>();
            for (Object[] row : results) {
                String name = (String) row[0];
                String email = (String) row[1];
                Long totalOrders = ((Number) row[2]).longValue();

                report.add(new UserReportDTO(name, email, totalOrders));
            }
            return report;
        }

        throw new IllegalArgumentException("Tipo de reporte no soportado: " + type);
    }

}
