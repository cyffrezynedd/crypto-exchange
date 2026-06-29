package com.exchange.iam.application;

import com.exchange.iam.domain.exception.DuplicateUserException;
import com.exchange.iam.domain.exception.UserNotFoundException;
import com.exchange.iam.domain.model.KycStatus;
import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.in.CreateUserCommand;
import com.exchange.iam.port.in.UpdateUserCommand;
import com.exchange.iam.port.out.RoleRepositoryPort;
import com.exchange.iam.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void createUserPersistsUserAndAssignsDefaultRole() {
        CreateUserCommand command = new CreateUserCommand(
                "alice@example.com", "password123", "alice", KycStatus.PENDING, true);
        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(userRepository.existsByUsername(command.username())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(7L);
            return user;
        });

        User created = userService.createUser(command);

        assertEquals(7L, created.getId());
        assertEquals("hashed", created.getPasswordHash());
        verify(roleRepository).assignRole(7L, "USER");
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.createUser(
                new CreateUserCommand("dup@example.com", "password123", "user1", null, null)));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserRejectsDuplicateUsername() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.createUser(
                new CreateUserCommand("new@example.com", "password123", "taken", null, null)));
    }

    @Test
    void getUserThrowsWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void updateUserChangesFieldsAndHashesPasswordWhenProvided() {
        User existing = new User();
        existing.setId(5L);
        existing.setEmail("old@example.com");
        existing.setUsername("old");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 5L)).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("newname", 5L)).thenReturn(false);
        when(passwordEncoder.encode("newpass123")).thenReturn("new-hash");
        when(userRepository.save(existing)).thenReturn(existing);

        UpdateUserCommand command = new UpdateUserCommand(
                5L, "new@example.com", "newpass123", "newname", KycStatus.VERIFIED, false);
        User updated = userService.updateUser(command);

        assertEquals("new@example.com", updated.getEmail());
        assertEquals("new-hash", updated.getPasswordHash());
        assertEquals(KycStatus.VERIFIED, updated.getKycStatus());
        assertEquals(false, updated.isActive());
    }

    @Test
    void deleteUserRemovesExistingUser() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));

        userService.deleteUser(3L);

        verify(userRepository).deleteById(3L);
    }

    @Test
    void listUsersReturnsRepositoryResult() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertEquals(1, userService.listUsers().size());
    }
}
