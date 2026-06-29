package com.exchange.iam.port.in;

public record LoginCommand(String email, String password) {
}
