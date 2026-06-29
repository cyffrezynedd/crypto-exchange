package com.exchange.iam.adapter.in.web;

import com.exchange.iam.adapter.in.web.dto.AuthResponse;
import com.exchange.iam.adapter.in.web.dto.LoginRequest;
import com.exchange.iam.adapter.in.web.dto.RefreshRequest;
import com.exchange.iam.port.in.AuthUseCase;
import com.exchange.iam.port.in.LoginCommand;
import com.exchange.iam.port.out.RoleRepositoryPort;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final RoleRepositoryPort roleRepository;

    public AuthController(AuthUseCase authUseCase, RoleRepositoryPort roleRepository) {
        this.authUseCase = authUseCase;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(authUseCase.login(new LoginCommand(request.email(), request.password())));
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return AuthResponse.from(authUseCase.refresh(request.refreshToken()));
    }

    @GetMapping("/users/{id}/roles")
    public List<String> roles(@PathVariable Long id) {
        return roleRepository.findRoleCodesByUserId(id);
    }
}
