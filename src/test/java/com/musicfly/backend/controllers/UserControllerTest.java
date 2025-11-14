package com.musicfly.backend.controllers;

import com.musicfly.backend.services.UserService;
import com.musicfly.backend.views.DTO.UserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------- GET USER BY ID -----------
    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        Long userId = 1L;
        UserProfileDTO user = new UserProfileDTO();
        user.setId(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<UserProfileDTO> response = userController.getUserById(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(user);
        verify(userService).getUserById(userId);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<UserProfileDTO> response = userController.getUserById(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
        verify(userService).getUserById(userId);
    }

    // ----------- UPDATE USER -----------
    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserExists() {
        Long userId = 1L;
        UserProfileDTO updatedUser = new UserProfileDTO();
        updatedUser.setId(userId);

        when(userService.updateUser(userId, updatedUser)).thenReturn(updatedUser);

        ResponseEntity<UserProfileDTO> response = userController.updateUser(userId, updatedUser);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedUser);
        verify(userService).updateUser(userId, updatedUser);
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() {
        Long userId = 1L;
        UserProfileDTO updatedUser = new UserProfileDTO();

        when(userService.updateUser(userId, updatedUser)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserProfileDTO> response = userController.updateUser(userId, updatedUser);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
        verify(userService).updateUser(userId, updatedUser);
    }

    // ----------- DELETE USER -----------
    @Test
    void deleteUser_shouldReturnNoContent_whenUserExists() {
        Long userId = 1L;

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() {
        Long userId = 1L;
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(userService).deleteUser(userId);
    }
}
