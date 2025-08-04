package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.LoginDTO;
import com.laurarojas.ecommerceapi.dtos.ResponseTokenDTO;
import com.laurarojas.ecommerceapi.entity.RoleEntity;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.exceptions.UnauthorizedException;
import com.laurarojas.ecommerceapi.repository.UserRepository;
import com.laurarojas.ecommerceapi.util.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtProvider  jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public ResponseTokenDTO validateLogin(LoginDTO loginDTO) throws UnauthorizedException {
        Optional<UserEntity> user = userRepository.findUserWithRolesByEmail(loginDTO.getEmail());

        if(!user.isPresent()) {
            throw new UnauthorizedException("Usuario no registrado", HttpStatus.UNAUTHORIZED.value());
        }

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            throw new UnauthorizedException("Correo y/o contraseña incorrecta", HttpStatus.UNAUTHORIZED.value());
        }
        UserEntity userEntity = user.get();
        List<String> roleNames = userEntity.getRoles().stream()
                .map(RoleEntity::getName).collect(Collectors.toList());
        String token = jwtProvider.generateToken(userEntity.getId().toString(), userEntity.getEmail(),
                userEntity.getFirstName(),userEntity.getLastName(), roleNames);
        return new ResponseTokenDTO(token);
    }

}
