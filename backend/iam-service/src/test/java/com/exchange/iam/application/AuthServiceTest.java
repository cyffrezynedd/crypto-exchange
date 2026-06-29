package com.exchange.iam.application;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.security.JwtTokenProvider;
import com.exchange.iam.domain.exception.UserNotFoundException;
import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.in.AuthTokens;
import com.exchange.iam.port.in.LoginCommand;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SECRET = "dev-only-change-me-exchange-jwt-secret-key-32chars";

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, 900, 604800);
        authService = new AuthService(userRepository, roleRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void loginReturnsTokensForValidCredentials() {
        User user = activeUser(1L, "trader", "hash");
        when(userRepository.findByEmail("trader@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hash")).thenReturn(true);
        when(roleRepository.findRoleCodesByUserId(1L)).thenReturn(List.of("USER"));

        AuthTokens tokens = authService.login(new LoginCommand("trader@example.com", "secret123"));

        assertEquals(1L, tokens.userId());
        assertEquals("trader", tokens.username());
        assertNotNull(tokens.accessToken());
        assertNotNull(tokens.refreshToken());
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = activeUser(2L, "bob", "hash");
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        ExchangeException ex = assertThrows(ExchangeException.class,
                () -> authService.login(new LoginCommand("bob@example.com", "wrong")));
        assertEquals(ErrorCode.UNAUTHENTICATED, ex.code());
    }

    @Test
    void loginRejectsDisabledUser() {
        User user = activeUser(3L, "inactive", "hash");
        user.setActive(false);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        ExchangeException ex = assertThrows(ExchangeException.class,
                () -> authService.login(new LoginCommand("inactive@example.com", "secret123")));
        assertEquals(ErrorCode.PERMISSION_DENIED, ex.code());
    }

    @Test
    void refreshIssuesNewTokensFromValidRefreshToken() {
        User user = activeUser(4L, "carol", "hash");
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(roleRepository.findRoleCodesByUserId(4L)).thenReturn(List.of("USER", "ADMIN"));
        String refresh = jwtTokenProvider.issueTokenPair(4L, "carol", List.of("USER")).refreshToken();

        AuthTokens tokens = authService.refresh(refresh);

        assertEquals(4L, tokens.userId());
        assertNotNull(tokens.accessToken());
    }

    @Test
    void refreshRejectsInvalidToken() {
        ExchangeException ex = assertThrows(ExchangeException.class, () -> authService.refresh("not-a-jwt"));
        assertEquals(ErrorCode.TOKEN_INVALID, ex.code());
    }

    @Test
    void refreshFailsWhenUserDeleted() {
        String refresh = jwtTokenProvider.issueTokenPair(404L, "ghost", List.of("USER")).refreshToken();
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.refresh(refresh));
    }

    private static User activeUser(long id, String username, String hash) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPasswordHash(hash);
        user.setActive(true);
        return user;
    }
}
