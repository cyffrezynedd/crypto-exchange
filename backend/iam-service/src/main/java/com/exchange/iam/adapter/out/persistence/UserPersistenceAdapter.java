package com.exchange.iam.adapter.out.persistence;

import com.exchange.iam.domain.model.User;
import com.exchange.iam.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        UserJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return repository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
