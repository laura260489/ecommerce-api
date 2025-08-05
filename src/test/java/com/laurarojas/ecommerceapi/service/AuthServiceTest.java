package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.LoginDTO;
import com.laurarojas.ecommerceapi.dtos.ResponseTokenDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.exceptions.UnauthorizedException;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import com.laurarojas.ecommerceapi.util.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private LoginDTO loginDTO;
    private UserEntity userEntity;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@email.com");
        loginDTO.setPassword("password123");

        roleEntity = new RoleEntity();
        roleEntity.setId("role-id");
        roleEntity.setName("USER");

        userEntity = new UserEntity();
        userEntity.setId("user-id");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setFirstName("Laura");
        userEntity.setLastName("Rojas");
        userEntity.setFrequentUser(false);
        userEntity.setRoles(new HashSet<>(Arrays.asList(roleEntity)));
    }

    @Test
    void validateLoginWhenUserExistsShouldReturnToken() {
        when(userRepository.findUserWithRolesByEmail("test@email.com"))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtProvider.generateToken(anyString(), anyString(), anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn("jwt-token");

        ResponseTokenDTO result = authService.validateLogin(loginDTO);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals(200, result.getStatusCode());
        assertEquals("Inicio de sesión válido", result.getMessage());

        verify(userRepository).findUserWithRolesByEmail("test@email.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtProvider).generateToken("user-id", "test@email.com", "Laura", "Rojas", Arrays.asList("USER"), false);
    }

    @Test
    void validateLogin_WhenUserNotExists_ShouldThrowUnauthorizedException() {
        when(userRepository.findUserWithRolesByEmail("test@email.com"))
                .thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.validateLogin(loginDTO));

        assertEquals("Usuario no registrado", exception.getMessage());
        assertEquals(401, exception.statusCode);

        verify(userRepository).findUserWithRolesByEmail("test@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtProvider, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyList(), anyBoolean());
    }

    @Test
    void validateLogin_WhenPasswordIncorrect_ShouldThrowUnauthorizedException() {
        when(userRepository.findUserWithRolesByEmail("test@email.com"))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.validateLogin(loginDTO));

        assertEquals("Correo y/o contraseña incorrecta", exception.getMessage());
        assertEquals(401, exception.statusCode);

        verify(userRepository).findUserWithRolesByEmail("test@email.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtProvider, never()).generateToken(anyString(), anyString(), anyString(), anyString(), anyList(), anyBoolean());
    }

    @Test
    void validateLogin_WhenUserHasMultipleRoles_ShouldIncludeAllRoles() {
        RoleEntity adminRole = new RoleEntity();
        adminRole.setId("admin-role-id");
        adminRole.setName("ADMIN");
        
        userEntity.getRoles().add(adminRole);

        when(userRepository.findUserWithRolesByEmail("test@email.com"))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtProvider.generateToken(anyString(), anyString(), anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn("jwt-token");

        ResponseTokenDTO result = authService.validateLogin(loginDTO);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());

        verify(jwtProvider).generateToken(eq("user-id"), eq("test@email.com"), eq("Laura"), eq("Rojas"), 
                argThat(roles -> roles.size() == 2 && roles.contains("USER") && roles.contains("ADMIN")), eq(false));
    }

    @Test
    void validateLogin_WhenUserIsFrequent_ShouldSetFrequentUserFlag() {
        userEntity.setFrequentUser(true);

        when(userRepository.findUserWithRolesByEmail("test@email.com"))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtProvider.generateToken(anyString(), anyString(), anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn("jwt-token");

        ResponseTokenDTO result = authService.validateLogin(loginDTO);

        assertNotNull(result);
        verify(jwtProvider).generateToken("user-id", "test@email.com", "Laura", "Rojas", Arrays.asList("USER"), true);
    }
}