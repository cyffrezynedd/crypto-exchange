package com.exchange.iam.port.in;

public interface AuthUseCase {

    AuthTokens login(LoginCommand command);

    AuthTokens refresh(String refreshToken);
}
