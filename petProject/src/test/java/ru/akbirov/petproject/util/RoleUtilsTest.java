package ru.akbirov.petproject.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleUtilsTest {

    @Test
    void testRemoveRolePrefix_WithPrefix() {
        // Given
        String role = "ROLE_ADMIN";

        // When
        String result = RoleUtils.removeRolePrefix(role);

        // Then
        assertEquals("ADMIN", result);
    }

    @Test
    void testRemoveRolePrefix_WithoutPrefix() {
        // Given
        String role = "ADMIN";

        // When
        String result = RoleUtils.removeRolePrefix(role);

        // Then
        assertEquals("ADMIN", result);
    }

    @Test
    void testRemoveRolePrefix_Null() {
        // When
        String result = RoleUtils.removeRolePrefix(null);

        // Then
        assertNull(result);
    }

    @Test
    void testRemoveRolePrefix_EmptyString() {
        // Given
        String role = "";

        // When
        String result = RoleUtils.removeRolePrefix(role);

        // Then
        assertEquals("", result);
    }

    @Test
    void testRemoveRolePrefix_Array_WithPrefix() {
        // Given
        String[] roles = {"ROLE_ADMIN", "ROLE_USER"};

        // When
        String[] result = RoleUtils.removeRolePrefix((String[]) roles);

        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("ADMIN", result[0]);
        assertEquals("USER", result[1]);
    }

    @Test
    void testRemoveRolePrefix_Array_WithoutPrefix() {
        // Given
        String[] roles = {"ADMIN", "USER"};

        // When
        String[] result = RoleUtils.removeRolePrefix(roles);

        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("ADMIN", result[0]);
        assertEquals("USER", result[1]);
    }

    @Test
    void testRemoveRolePrefix_Array_Mixed() {
        // Given
        String[] roles = {"ROLE_ADMIN", "USER"};

        // When
        String[] result = RoleUtils.removeRolePrefix(roles);

        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("ADMIN", result[0]);
        assertEquals("USER", result[1]);
    }

    @Test
    void testRemoveRolePrefix_Array_Null() {
        // When
        String[] result = RoleUtils.removeRolePrefix((String[]) null);

        // Then
        assertNull(result);
    }

    @Test
    void testRemoveRolePrefix_Array_Empty() {
        // Given
        String[] roles = {};

        // When
        String[] result = RoleUtils.removeRolePrefix(roles);

        // Then
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}

