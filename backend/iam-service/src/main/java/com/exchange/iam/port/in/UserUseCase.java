package com.exchange.iam.port.in;

import com.exchange.iam.domain.model.User;

import java.util.List;

public interface UserUseCase {

    User createUser(CreateUserCommand command);

    User getUser(Long id);

    List<User> listUsers();

    User updateUser(UpdateUserCommand command);

    void deleteUser(Long id);
}
