package com.exchange.iam.application;

import com.exchange.iam.domain.exception.DuplicateUserException;
import com.exchange.iam.domain.exception.UserNotFoundException;
import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.in.CreateUserCommand;
import com.exchange.iam.port.in.UpdateUserCommand;
import com.exchange.iam.port.in.UserUseCase;
import com.exchange.iam.port.out.RoleRepositoryPort;
import com.exchange.iam.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService implements UserUseCase {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateUserException("Email already exists: " + command.email());
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new DuplicateUserException("Username already exists: " + command.username());
        }

        User user = new User();
        user.setEmail(command.email());
        user.setPasswordHash(passwordEncoder.encode(command.password()));
        user.setUsername(command.username());
        user.setKycStatus(command.kycStatus());
        user.setActive(command.active());
        User saved = userRepository.save(user);
        roleRepository.assignRole(saved.getId(), DEFAULT_ROLE);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.id())
                .orElseThrow(() -> new UserNotFoundException(command.id()));

        if (userRepository.existsByEmailAndIdNot(command.email(), command.id())) {
            throw new DuplicateUserException("Email already exists: " + command.email());
        }
        if (userRepository.existsByUsernameAndIdNot(command.username(), command.id())) {
            throw new DuplicateUserException("Username already exists: " + command.username());
        }

        user.setEmail(command.email());
        if (command.password() != null && !command.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(command.password()));
        }
        user.setUsername(command.username());
        user.setKycStatus(command.kycStatus());
        user.setActive(command.active());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
