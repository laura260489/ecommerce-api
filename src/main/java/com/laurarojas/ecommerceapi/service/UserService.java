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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseMessageDTO registerUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new ApiException(
                    String.format("El email %s ya está registrado", registerUserDTO.getEmail(), registerUserDTO.getEmail()),
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase());
        };

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(registerUserDTO.getFirstName());
        userEntity.setLastName(registerUserDTO.getLastName());
        userEntity.setEmail(registerUserDTO.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        userEntity.setPhone(registerUserDTO.getPhone());
        userEntity.getRoles().add(getRoleByName("client"));
        userEntity.setFrequentUser(false);
        userEntity.setStatus(Status.ACTIVE);
        userRepository.save(userEntity);
        return new ResponseMessageDTO("Usuario creado satisfactoriamente", HttpStatus.CREATED.value());
    }

    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ApiException(String.format("Role not found: %s", name),
                        HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    public List<UserDTO> getListUsers() {
        return userRepository.findByStatus(Status.ACTIVE).stream().map(user ->
                new UserDTO(
                        user.getId(),
                        String.format("%s %s", user.getFirstName(), user.getLastName()),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRoles().stream()
                                .map(RoleEntity::getName).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    public ResponseMessageDTO deleteUserById(String id) {
        UserEntity userEntity = findUser(id);
        userEntity.setStatus(Status.INACTIVE);
        userRepository.save(userEntity);
        return new ResponseMessageDTO(String.format("Usuario %s eliminado", userEntity.getEmail()) , HttpStatus.OK.value());
    }

    public ResponseMessageDTO updateUserById(String id, UserUpdateDTO userUpdateDTO) {
        UserEntity userEntity = findUser(id);

        Set<RoleEntity> roles = userUpdateDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName).orElseThrow(() ->
                        new ApiException("Role not found: " + roleName, HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase())))
                .collect(Collectors.toSet());

        userEntity.setFirstName(userUpdateDTO.getFirstName());
        userEntity.setLastName(userUpdateDTO.getLastName());
        userEntity.setEmail(userUpdateDTO.getEmail());
        userEntity.setPhone(userUpdateDTO.getPhone());
        userEntity.setRoles(roles);
        userRepository.save(userEntity);
        return new ResponseMessageDTO(String.format("Usuario %s actualizado", userEntity.getEmail()) , HttpStatus.OK.value());
    }

    public UserDTO getUser(String id) {
        UserEntity user = findUser(id);

        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFirstName() + " " + user.getLastName());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());

        userDto.setRoles(roleNames);
        return userDto;
    }

    private UserEntity findUser(String id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByIdAndStatus(id, Status.ACTIVE);

        if (optionalUserEntity.isEmpty()) {
            throw new ApiException(
                    "User not found",
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return optionalUserEntity.get();
    }
}
