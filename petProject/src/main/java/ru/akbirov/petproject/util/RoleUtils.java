package ru.akbirov.petproject.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RoleUtils {
    
    private static final String ROLE_PREFIX = "ROLE_";
    
    /**
     * Убирает префикс "ROLE_" из роли, если он присутствует
     */
    public static String removeRolePrefix(String role) {
        if (role != null && role.startsWith(ROLE_PREFIX)) {
            return role.substring(ROLE_PREFIX.length());
        }
        return role;
    }
    
    /**
     * Убирает префикс "ROLE_" из всех ролей в массиве
     */
    public static String[] removeRolePrefix(String[] roles) {
        if (roles == null) {
            return null;
        }
        return Arrays.stream(roles)
                .map(RoleUtils::removeRolePrefix)
                .toArray(String[]::new);
    }
}

