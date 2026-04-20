package com.eventdriven.platform.identity.auth;

import com.eventdriven.platform.identity.auth.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user profile")
    public UserProfileResponse me(@AuthenticationPrincipal JwtAuthenticatedUser currentUser) {
        return authService.currentUser(currentUser);
    }
}
