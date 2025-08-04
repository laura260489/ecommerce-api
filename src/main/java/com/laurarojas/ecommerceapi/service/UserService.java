package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.ListUserDto;
import com.laurarojas.ecommerceapi.dtos.RegisterUserDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.exceptions.UserEmailExistException;
import com.laurarojas.ecommerceapi.repository.RoleRepository;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public void registerUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new UserEmailExistException(String.format("El email %s ya se está registrado", registerUserDTO.getEmail(), registerUserDTO.getEmail()), HttpStatus.UNPROCESSABLE_ENTITY.value());
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
    }

    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    public List<ListUserDto> getListUsers() {
        return userRepository.findByStatus(Status.ACTIVE).stream().map(user ->
                new ListUserDto(
                        user.getId(),
                        String.format("%s %s", user.getFirstName(), user.getLastName()),
                        user.getEmail(),
                        user.getRoles().stream()
                                .map(RoleEntity::getName).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

}
