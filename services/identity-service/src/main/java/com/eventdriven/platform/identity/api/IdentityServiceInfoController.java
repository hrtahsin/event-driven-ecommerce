package com.eventdriven.platform.identity.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/service-info")
public class IdentityServiceInfoController {

    @GetMapping
    public IdentityServiceInfoResponse describe() {
        return new IdentityServiceInfoResponse(
                "identity-service",
                "Authentication, users, and roles",
                List.of("users", "roles", "user_roles"),
                List.of("POST /auth/register", "POST /auth/login", "GET /users/me"),
                List.of(),
                List.of()
        );
    }
}
