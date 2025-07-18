package com.haiphamcoder.authentication.domain.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

        USER(Set.of(
                        Permission.USER_READ,
                        Permission.USER_UPDATE,
                        Permission.USER_CREATE,
                        Permission.USER_DELETE), "user"),
        ADMIN(Set.of(
                        Permission.ADMIN_READ,
                        Permission.ADMIN_UPDATE,
                        Permission.ADMIN_CREATE,
                        Permission.ADMIN_DELETE,
                        Permission.MANAGER_READ,
                        Permission.MANAGER_UPDATE,
                        Permission.MANAGER_CREATE,
                        Permission.MANAGER_DELETE), "admin"),
        MANAGER(Set.of(
                        Permission.MANAGER_READ,
                        Permission.MANAGER_UPDATE,
                        Permission.MANAGER_CREATE,
                        Permission.MANAGER_DELETE), "manager");

        @Getter
        private final Set<Permission> permissions;

        @Getter
        private final String name;

        public List<SimpleGrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> authorities = permissions
                                .stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionValue()))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
                return authorities;
        }

        public static Role fromName(String name) {
                return Arrays.stream(Role.values())
                                .filter(role -> role.getName().equals(name))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + name));
        }

}
