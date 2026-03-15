package com.nhj.librarymanage.security.member;

import com.nhj.librarymanage.security.exception.security.DuplicateRoleLevelException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Role {

    ROLE_ADMIN(0, "관리자"),
    ROLE_SYSTEM_ENGINEER(1, "시스템 엔지니어"),
    ROLE_MANAGER(2, "담당자")
    ;

    private final int level;
    private final String description;

    public static String makeRoleHierarchy() {
        List<Role> roles = Arrays.stream(Role.values())
                .sorted(Comparator.comparing(Role::getLevel))
                .toList();

        StringBuilder roleStringBuilder = new StringBuilder();
        int previousLevel = -1;

        for (int i = 0; i < roles.size(); i++) {
            Role currentRole = roles.get(i);

            if (currentRole.getLevel() == previousLevel) {
                throw new DuplicateRoleLevelException("Duplicate Role Level Found");
            }

            roleStringBuilder.append(currentRole.name());
            if (i < roles.size() - 1) {
                roleStringBuilder.append(" > ");
            }

            previousLevel = currentRole.getLevel();
        }

        return roleStringBuilder.toString();
    }

    public static boolean hasRole(String strRole) {
        return Arrays.stream(Role.values()).anyMatch(role -> role.name().equals(strRole));
    }

}