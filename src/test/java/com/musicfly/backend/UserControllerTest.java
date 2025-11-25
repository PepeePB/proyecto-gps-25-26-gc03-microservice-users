package com.musicfly.backend;

import com.musicfly.backend.controllers.UserController;
import com.musicfly.backend.services.UserService;
import com.musicfly.backend.views.DTO.UserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "app.open-doors=true")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_UserExists() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<UserProfileDTO> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserProfileDTO> response = userController.getUserById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testUpdateUser_Success() {
        UserProfileDTO input = new UserProfileDTO();
        input.setUsername("updatedUser");

        UserProfileDTO updated = new UserProfileDTO();
        updated.setId(1L);
        updated.setUsername("updatedUser");

        when(userService.updateUser(1L, input)).thenReturn(updated);

        ResponseEntity<UserProfileDTO> response = userController.updateUser(1L, input);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("updatedUser", response.getBody().getUsername());
        verify(userService, times(1)).updateUser(1L, input);
    }

    @Test
    void testUpdateUser_NotFound() {
        UserProfileDTO input = new UserProfileDTO();
        input.setUsername("updatedUser");

        when(userService.updateUser(1L, input)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserProfileDTO> response = userController.updateUser(1L, input);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService, times(1)).updateUser(1L, input);
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1L);
    }
}

