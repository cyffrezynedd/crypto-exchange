package com.exchange.iam.adapter.in.web;

import com.exchange.iam.adapter.in.web.dto.CreateUserRequest;
import com.exchange.iam.adapter.in.web.dto.UpdateUserRequest;
import com.exchange.iam.adapter.in.web.dto.UserResponse;
import com.exchange.common.web.GatewayHeaders;
import com.exchange.common.web.GatewayUserContext;
import com.exchange.iam.port.in.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.from(userUseCase.createUser(request.toCommand()));
    }

    @GetMapping
    public List<UserResponse> list(
            @RequestHeader(GatewayHeaders.USER_ID_HEADER) Long userId,
            @RequestHeader(GatewayHeaders.ROLES_HEADER) String roles) {
        GatewayUserContext.requireAdmin(roles);
        return userUseCase.listUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse get(
            @RequestHeader(GatewayHeaders.USER_ID_HEADER) Long userId,
            @RequestHeader(GatewayHeaders.ROLES_HEADER) String roles,
            @PathVariable Long id) {
        GatewayUserContext.requireSelfOrAdmin(userId, roles, id);
        return UserResponse.from(userUseCase.getUser(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @RequestHeader(GatewayHeaders.USER_ID_HEADER) Long userId,
            @RequestHeader(GatewayHeaders.ROLES_HEADER) String roles,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        GatewayUserContext.requireSelfOrAdmin(userId, roles, id);
        return UserResponse.from(userUseCase.updateUser(request.toCommand(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(GatewayHeaders.USER_ID_HEADER) Long userId,
            @RequestHeader(GatewayHeaders.ROLES_HEADER) String roles,
            @PathVariable Long id) {
        GatewayUserContext.requireAdmin(roles);
        userUseCase.deleteUser(id);
    }
}
