package dev.juntralala.oauth.service;

import dev.juntralala.oauth.entity.Role;
import dev.juntralala.oauth.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRole(String name) {
        return roleRepository.findFirstByName(name)
                .orElseThrow(() -> new RuntimeException("The role $role does not exists".replace("$role", name)));
    }

}
