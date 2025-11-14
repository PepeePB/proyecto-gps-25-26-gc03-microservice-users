package com.musicfly.backend.services;

import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.views.DTO.UserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void getUserById_userExists_returnsDTO() {
        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .personalLink("http://personal.link")
                .birthday(Date.from(Instant.now()))
                .bio("Bio test")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserProfileDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void getUserById_userNotFound_returnsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<UserProfileDTO> result = userService.getUserById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_existingUser_updatesFields() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileDTO updatedDTO = new UserProfileDTO(
                "New Name", "new@example.com", "http://link.com", Date.from(Instant.now()), "New Bio"
        );

        UserProfileDTO result = userService.updateUser(1L, updatedDTO);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New Bio", result.getBio());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserProfileDTO dto = new UserProfileDTO("Name", "email@example.com", null, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, dto);
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void deleteUser_existingUser_deletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("Usuario no encontrado.", exception.getMessage());
    }
}
