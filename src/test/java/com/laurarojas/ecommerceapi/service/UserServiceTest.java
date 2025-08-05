package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.RegisterUserDTO;
import com.laurarojas.ecommerceapi.dtos.ResponseMessageDTO;
import com.laurarojas.ecommerceapi.dtos.UserDTO;
import com.laurarojas.ecommerceapi.dtos.UserUpdateDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.exceptions.ApiException;
import com.laurarojas.ecommerceapi.repository.RoleRepository;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterUserDTO registerUserDTO;
    private UserEntity userEntity;
    private RoleEntity clientRole;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setFirstName("Laura");
        registerUserDTO.setLastName("Rojas");
        registerUserDTO.setEmail("laura@email.com");
        registerUserDTO.setPassword("password123");
        registerUserDTO.setPhone("123456789");

        clientRole = new RoleEntity();
        clientRole.setId("client-role-id");
        clientRole.setName("client");

        userEntity = new UserEntity();
        userEntity.setId("user-id");
        userEntity.setFirstName("Laura");
        userEntity.setLastName("Rojas");
        userEntity.setEmail("laura@email.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setPhone("123456789");
        userEntity.setFrequentUser(false);
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setRoles(new HashSet<>(Arrays.asList(clientRole)));

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Martha");
        userUpdateDTO.setLastName("Gonzales");
        userUpdateDTO.setEmail("martha@email.com");
        userUpdateDTO.setPhone("987654321");
        userUpdateDTO.setRoles(Arrays.asList("admin"));
    }

    @Test
    void registerUser_WhenValidUser_ShouldCreateUserSuccessfully() {
        when(userRepository.findByEmail("laura@email.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("client")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        ResponseMessageDTO result = userService.registerUser(registerUserDTO);

        assertNotNull(result);
        assertEquals("Usuario creado satisfactoriamente", result.getMessage());
        assertEquals(201, result.getStatusCode());

        verify(userRepository).findByEmail("laura@email.com");
        verify(roleRepository).findByName("client");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user -> 
                "Laura".equals(user.getFirstName()) &&
                "Rojas".equals(user.getLastName()) &&
                "laura@email.com".equals(user.getEmail()) &&
                "encodedPassword".equals(user.getPassword()) &&
                "123456789".equals(user.getPhone()) &&
                !user.isFrequentUser() &&
                Status.ACTIVE.equals(user.getStatus())
        ));
    }

    @Test
    void registerUser_WhenEmailAlreadyExists_ShouldThrowApiException() {
        when(userRepository.findByEmail("laura@email.com")).thenReturn(Optional.of(userEntity));

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.registerUser(registerUserDTO));

        assertTrue(exception.getMessage().contains("El email laura@email.com ya está registrado"));
        assertEquals(409, exception.statusCode);

        verify(userRepository).findByEmail("laura@email.com");
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getRoleByName_WhenRoleExists_ShouldReturnRole() {
        when(roleRepository.findByName("client")).thenReturn(Optional.of(clientRole));

        RoleEntity result = userService.getRoleByName("client");

        assertNotNull(result);
        assertEquals("client-role-id", result.getId());
        assertEquals("client", result.getName());

        verify(roleRepository).findByName("client");
    }

    @Test
    void getRoleByName_WhenRoleNotExists_ShouldThrowApiException() {
        when(roleRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.getRoleByName("nonexistent"));

        assertEquals("Role not found: nonexistent", exception.getMessage());
        assertEquals(404, exception.statusCode);

        verify(roleRepository).findByName("nonexistent");
    }

    @Test
    void getListUsers_ShouldReturnUserDTOList() {
        UserEntity user2 = new UserEntity();
        user2.setId("user-2");
        user2.setFirstName("Martha");
        user2.setLastName("Gonzales");
        user2.setEmail("martha@email.com");
        user2.setPhone("987654321");
        user2.setRoles(new HashSet<>(Arrays.asList(clientRole)));

        when(userRepository.findByStatus(Status.ACTIVE))
                .thenReturn(Arrays.asList(userEntity, user2));

        List<UserDTO> result = userService.getListUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        UserDTO dto1 = result.get(0);
        assertEquals("user-id", dto1.getId());
        assertEquals("Laura Rojas", dto1.getFullName());
        assertEquals("Laura", dto1.getFirstName());
        assertEquals("Rojas", dto1.getLastName());
        assertEquals("laura@email.com", dto1.getEmail());
        assertEquals("123456789", dto1.getPhone());
        assertEquals(Arrays.asList("client"), dto1.getRoles());

        verify(userRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldSetStatusInactive() {
        when(userRepository.findByIdAndStatus("user-id", Status.ACTIVE))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        ResponseMessageDTO result = userService.deleteUserById("user-id");

        assertNotNull(result);
        assertEquals("Usuario laura@email.com eliminado", result.getMessage());
        assertEquals(200, result.getStatusCode());

        verify(userRepository).findByIdAndStatus("user-id", Status.ACTIVE);
        verify(userRepository).save(argThat(user -> 
                Status.INACTIVE.equals(user.getStatus())
        ));
    }

    @Test
    void deleteUserById_WhenUserNotFound_ShouldThrowApiException() {
        when(userRepository.findByIdAndStatus("non-existent", Status.ACTIVE))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.deleteUserById("non-existent"));

        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.statusCode);

        verify(userRepository).findByIdAndStatus("non-existent", Status.ACTIVE);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateUserById_WhenValidUpdate_ShouldReturnUpdatedUser() {
        RoleEntity adminRole = new RoleEntity();
        adminRole.setId("admin-role-id");
        adminRole.setName("admin");

        when(userRepository.findByIdAndStatus("user-id", Status.ACTIVE))
                .thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserUpdateDTO result = userService.updateUserById("user-id", userUpdateDTO);

        assertNotNull(result);
        assertEquals("user-id", result.getId());
        assertEquals("Martha", result.getFirstName());
        assertEquals("Gonzales", result.getLastName());
        assertEquals("martha@email.com", result.getEmail());
        assertEquals("987654321", result.getPhone());
        assertEquals(Arrays.asList("admin"), result.getRoles());
        assertTrue(result.getMessage().contains("Usuario martha@email.com actualizado"));
        assertEquals(200, result.getStatusCode());

        verify(userRepository).findByIdAndStatus("user-id", Status.ACTIVE);
        verify(roleRepository).findByName("admin");
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUserById_WhenRoleNotFound_ShouldThrowApiException() {
        when(userRepository.findByIdAndStatus("user-id", Status.ACTIVE))
                .thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName("admin")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.updateUserById("user-id", userUpdateDTO));

        assertEquals("Role not found: admin", exception.getMessage());
        assertEquals(404, exception.statusCode);

        verify(userRepository).findByIdAndStatus("user-id", Status.ACTIVE);
        verify(roleRepository).findByName("admin");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUserDTO() {
        when(userRepository.findByIdAndStatus("user-id", Status.ACTIVE))
                .thenReturn(Optional.of(userEntity));

        UserDTO result = userService.getUser("user-id");

        assertNotNull(result);
        assertEquals("user-id", result.getId());
        assertEquals("Laura Rojas", result.getFullName());
        assertEquals("Laura", result.getFirstName());
        assertEquals("Rojas", result.getLastName());
        assertEquals("laura@email.com", result.getEmail());
        assertEquals("123456789", result.getPhone());
        assertEquals(Arrays.asList("client"), result.getRoles());

        verify(userRepository).findByIdAndStatus("user-id", Status.ACTIVE);
    }

    @Test
    void getUser_WhenUserNotFound_ShouldThrowApiException() {
        when(userRepository.findByIdAndStatus("non-existent", Status.ACTIVE))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.getUser("non-existent"));

        assertEquals("User not found", exception.getMessage());
        assertEquals(404, exception.statusCode);

        verify(userRepository).findByIdAndStatus("non-existent", Status.ACTIVE);
    }

    @Test
    void convertUserEntityToDTO_ShouldMapAllFields() {
        userEntity.setFrequentUser(true);

        UserUpdateDTO result = userService.convertUserEntityToDTO(userEntity);

        assertNotNull(result);
        assertEquals("user-id", result.getId());
        assertEquals("Laura", result.getFirstName());
        assertEquals("Rojas", result.getLastName());
        assertEquals("laura@email.com", result.getEmail());
        assertEquals("123456789", result.getPhone());
        assertTrue(result.isFrecuent());
        assertEquals(Arrays.asList("client"), result.getRoles());
        assertEquals("Usuario laura@email.com actualizado", result.getMessage());
        assertEquals(200, result.getStatusCode());
    }

    @Test
    void registerUser_ShouldSetDefaultClientRole() {
        when(userRepository.findByEmail("laura@email.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("client")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        userService.registerUser(registerUserDTO);

        verify(roleRepository).findByName("client");
        verify(userRepository).save(argThat(user -> 
                user.getRoles().contains(clientRole) &&
                !user.isFrequentUser() &&
                Status.ACTIVE.equals(user.getStatus())
        ));
    }
}