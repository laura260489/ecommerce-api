package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.RoleDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.RoleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private RoleEntity adminRole;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        adminRole = new RoleEntity();
        adminRole.setId("admin-id");
        adminRole.setName("ADMIN");
        adminRole.setStatus(Status.ACTIVE);

        userRole = new RoleEntity();
        userRole.setId("user-id");
        userRole.setName("USER");
        userRole.setStatus(Status.ACTIVE);
    }

    @Test
    void getRoles_WhenActiveRolesExist_ShouldReturnRoleDTOList() {
        when(roleRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(adminRole, userRole));

        List<RoleDTO> result = roleService.getRoles();

        assertNotNull(result);
        assertEquals(2, result.size());

        RoleDTO adminDto = result.get(0);
        assertEquals("admin-id", adminDto.getId());
        assertEquals("ADMIN", adminDto.getName());

        RoleDTO userDto = result.get(1);
        assertEquals("user-id", userDto.getId());
        assertEquals("USER", userDto.getName());

        verify(roleRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getRoles_WhenNoActiveRoles_ShouldReturnEmptyList() {
        when(roleRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Collections.emptyList());

        List<RoleDTO> result = roleService.getRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getRoles_WhenSingleRoleExists_ShouldReturnSingleRoleDTO() {
        when(roleRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(adminRole));

        List<RoleDTO> result = roleService.getRoles();

        assertNotNull(result);
        assertEquals(1, result.size());

        RoleDTO dto = result.get(0);
        assertEquals("admin-id", dto.getId());
        assertEquals("ADMIN", dto.getName());

        verify(roleRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void getRoles_ShouldOnlyReturnActiveRoles() {
        verify(roleRepository, never()).findByStatus(Status.INACTIVE);

        roleService.getRoles();

        verify(roleRepository).findByStatus(Status.ACTIVE);
        verify(roleRepository, never()).findByStatus(Status.INACTIVE);
    }

    @Test
    void getRoles_ShouldMapRoleEntityToRoleDTO() {
        RoleEntity customRole = new RoleEntity();
        customRole.setId("custom-role-id");
        customRole.setName("CUSTOM_ROLE");
        customRole.setStatus(Status.ACTIVE);

        when(roleRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(customRole));

        List<RoleDTO> result = roleService.getRoles();

        assertNotNull(result);
        assertEquals(1, result.size());

        RoleDTO dto = result.get(0);
        assertEquals("custom-role-id", dto.getId());
        assertEquals("CUSTOM_ROLE", dto.getName());

        verify(roleRepository).findByStatus(Status.ACTIVE);
    }
}