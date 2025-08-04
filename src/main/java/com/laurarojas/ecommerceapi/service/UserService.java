package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.RegisterUserDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.exceptions.UserEmailExistException;
import com.laurarojas.ecommerceapi.repository.RoleRepository;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public int registerUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
            throw new UserEmailExistException(String.format("El email %s ya se está registrado", registerUserDTO.getEmail(), registerUserDTO.getEmail()), HttpStatus.UNPROCESSABLE_ENTITY.value());
        };

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(registerUserDTO.getFirstName());
        userEntity.setLastName(registerUserDTO.getLastName());
        userEntity.setEmail(registerUserDTO.getEmail());
        userEntity.setPassword(registerUserDTO.getPassword());
        userEntity.setPhone(registerUserDTO.getPhone());
        userEntity.getRoles().add(getRoleByName("client"));
        userEntity.setStatus("active");
        userRepository.save(userEntity);
        return 200;
    }

    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

}
