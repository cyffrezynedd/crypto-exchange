package com.exchange.iam.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    RoleJpaEntity findByCode(String code);
}

interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UserRoleJpaEntity.UserRoleId> {

    @Query(value = """
            SELECT r.code FROM iam.user_roles ur
            JOIN iam.roles r ON r.id = ur.role_id
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    boolean existsById_UserIdAndId_RoleId(Long userId, Long roleId);
}
