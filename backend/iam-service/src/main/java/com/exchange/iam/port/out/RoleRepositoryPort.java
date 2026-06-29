package com.exchange.iam.port.out;

import java.util.List;

public interface RoleRepositoryPort {

    List<String> findRoleCodesByUserId(Long userId);

    void assignRole(Long userId, String roleCode);
}
