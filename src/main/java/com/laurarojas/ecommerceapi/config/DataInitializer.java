package com.laurarojas.ecommerceapi.config;

import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> defaultRoles = List.of("admin", "client");

        for (String roleName : defaultRoles) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        RoleEntity role = new RoleEntity();
                        role.setName(roleName);
                        role.setStatus(Status.ACTIVE);
                        return roleRepository.save(role);
                    });
        }
    }
}
