package com.exchange.iam.application;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.security.JwtTokenProvider;
import com.exchange.iam.domain.exception.UserNotFoundException;
import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.in.AuthTokens;
import com.exchange.iam.port.in.AuthUseCase;
import com.exchange.iam.port.in.LoginCommand;
import com.exchange.iam.port.out.RoleRepositoryPort;
import com.exchange.iam.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthTokens login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new ExchangeException(ErrorCode.UNAUTHENTICATED, "Invalid email or password"));

        if (!user.isActive()) {
            throw new ExchangeException(ErrorCode.PERMISSION_DENIED, "User account is disabled");
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new ExchangeException(ErrorCode.UNAUTHENTICATED, "Invalid email or password");
        }

        List<String> roles = roleRepository.findRoleCodesByUserId(user.getId());
        JwtTokenProvider.TokenPair pair = jwtTokenProvider.issueTokenPair(user.getId(), user.getUsername(), roles);
        return AuthTokens.from(pair, user.getId(), user.getUsername());
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        JwtTokenProvider.ParsedToken parsed;
        try {
            parsed = jwtTokenProvider.parseRefreshToken(refreshToken);
        } catch (RuntimeException ex) {
            throw new ExchangeException(ErrorCode.TOKEN_INVALID, "Invalid refresh token");
        }

        User user = userRepository.findById(parsed.userId())
                .orElseThrow(() -> new UserNotFoundException(parsed.userId()));

        if (!user.isActive()) {
            throw new ExchangeException(ErrorCode.PERMISSION_DENIED, "User account is disabled");
        }

        List<String> roles = roleRepository.findRoleCodesByUserId(user.getId());
        JwtTokenProvider.TokenPair pair = jwtTokenProvider.issueTokenPair(user.getId(), user.getUsername(), roles);
        return AuthTokens.from(pair, user.getId(), user.getUsername());
    }
}
