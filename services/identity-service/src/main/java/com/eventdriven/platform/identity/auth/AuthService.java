package com.eventdriven.platform.identity.auth;

import com.eventdriven.platform.identity.auth.dto.AuthResponse;
import com.eventdriven.platform.identity.auth.dto.LoginRequest;
import com.eventdriven.platform.identity.auth.dto.RegisterRequest;
import com.eventdriven.platform.identity.auth.dto.UserProfileResponse;
import com.eventdriven.platform.identity.domain.RoleEntity;
import com.eventdriven.platform.identity.domain.RoleName;
import com.eventdriven.platform.identity.domain.RoleRepository;
import com.eventdriven.platform.identity.domain.UserEntity;
import com.eventdriven.platform.identity.domain.UserRepository;
import com.eventdriven.platform.identity.domain.UserStatus;
import com.eventdriven.platform.identity.support.InvalidCredentialsException;
import com.eventdriven.platform.identity.support.ResourceConflictException;
import com.eventdriven.platform.identity.support.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("A user with that email already exists");
        }

        RoleEntity customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("CUSTOMER role is not configured"));

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new LinkedHashSet<>());
        user.getRoles().add(customerRole);

        UserEntity savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("User account is not active");
        }

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse currentUser(JwtAuthenticatedUser authenticatedUser) {
        UserEntity user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
        return toUserProfile(user);
    }

    private AuthResponse buildAuthResponse(UserEntity user) {
        return new AuthResponse(
                jwtTokenService.generateToken(user),
                "Bearer",
                jwtTokenService.getAccessTokenTtlSeconds(),
                toUserProfile(user)
        );
    }

    private UserProfileResponse toUserProfile(UserEntity user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus().name(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .sorted(Comparator.naturalOrder())
                        .toList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
