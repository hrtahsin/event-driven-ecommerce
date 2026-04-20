package com.eventdriven.platform.identity.config;

import com.eventdriven.platform.identity.domain.RoleEntity;
import com.eventdriven.platform.identity.domain.RoleName;
import com.eventdriven.platform.identity.domain.RoleRepository;
import com.eventdriven.platform.identity.domain.UserEntity;
import com.eventdriven.platform.identity.domain.UserRepository;
import com.eventdriven.platform.identity.domain.UserStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

@Component
public class ReferenceDataSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties bootstrapAdminProperties;

    public ReferenceDataSeeder(RoleRepository roleRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               BootstrapAdminProperties bootstrapAdminProperties) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminProperties = bootstrapAdminProperties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RoleEntity customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new RoleEntity(null, RoleName.CUSTOMER)));
        RoleEntity adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new RoleEntity(null, RoleName.ADMIN)));

        if (!bootstrapAdminProperties.isEnabled()) {
            return;
        }

        String adminEmail = normalizeEmail(bootstrapAdminProperties.getEmail());
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        UserEntity adminUser = new UserEntity();
        adminUser.setEmail(adminEmail);
        adminUser.setPasswordHash(passwordEncoder.encode(bootstrapAdminProperties.getPassword()));
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setRoles(new LinkedHashSet<>());
        adminUser.getRoles().add(customerRole);
        adminUser.getRoles().add(adminRole);
        userRepository.save(adminUser);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
