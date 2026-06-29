package com.exchange.iam.adapter.out.persistence;

import com.exchange.iam.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleRepository;
    private final UserRoleJpaRepository userRoleRepository;

    public RolePersistenceAdapter(RoleJpaRepository roleRepository, UserRoleJpaRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        return userRoleRepository.findRoleCodesByUserId(userId);
    }

    @Override
    public void assignRole(Long userId, String roleCode) {
        RoleJpaEntity role = roleRepository.findByCode(roleCode);
        if (role == null) {
            throw new IllegalStateException("Role not found: " + roleCode);
        }
        if (!userRoleRepository.existsById_UserIdAndId_RoleId(userId, role.getId())) {
            userRoleRepository.save(UserRoleJpaEntity.of(userId, role.getId()));
        }
    }
}
