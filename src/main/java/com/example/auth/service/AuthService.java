package com.example.auth.service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.exception.UserAlreadyExistsException;
import com.example.auth.model.Role;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reactive Business Logic Service for User Registration and Authentication.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Registers a new user reactively in MongoDB.
     */
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByUsername(request.getUsername())
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        return Mono.error(new UserAlreadyExistsException("Error: Username is already taken!"));
                    }
                    return userRepository.existsByEmail(request.getEmail());
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new UserAlreadyExistsException("Error: Email is already in use!"));
                    }

                    Set<Role> roles = new HashSet<>();
                    if (request.getRoles() == null || request.getRoles().isEmpty()) {
                        roles.add(Role.ROLE_USER);
                    } else {
                        request.getRoles().forEach(role -> {
                            if ("admin".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                                roles.add(Role.ROLE_ADMIN);
                            } else {
                                roles.add(Role.ROLE_USER);
                            }
                        });
                    }

                    User user = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .age(request.getAge())
                            .roles(roles)
                            .enabled(true)
                            .build();

                    return userRepository.save(user);
                })
                .map(savedUser -> {
                    List<String> roleNames = savedUser.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList());

                    String token = jwtUtils.generateToken(savedUser.getUsername(), roleNames);

                    return AuthResponse.builder()
                            .token(token)
                            .tokenType("Bearer")
                            .id(savedUser.getId())
                            .username(savedUser.getUsername())
                            .email(savedUser.getEmail())
                            .roles(new HashSet<>(roleNames))
                            .build();
                })
                .retry(3);
    }

    /**
     * Authenticates an existing user reactively.
     */
    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new BadCredentialsException("Invalid username or password"));
                    }

                    List<String> roleNames = user.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList());

                    String token = jwtUtils.generateToken(user.getUsername(), roleNames);

                    AuthResponse response = AuthResponse.builder()
                            .token(token)
                            .tokenType("Bearer")
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .roles(new HashSet<>(roleNames))
                            .build();

                    return Mono.just(response);
                })
                .retry(3);
    }
}
