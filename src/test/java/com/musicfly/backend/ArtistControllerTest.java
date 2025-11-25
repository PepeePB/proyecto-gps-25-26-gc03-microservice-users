package com.musicfly.backend;

import com.musicfly.backend.controllers.ArtistController;
import com.musicfly.backend.exceptions.artist.ArtistNotFoundException;
import com.musicfly.backend.models.DAO.Artist;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.services.ArtistService;
import com.musicfly.backend.views.DTO.ArtistFormDTO;
import com.musicfly.backend.views.DTO.ReceivedArtistDTO;
import com.musicfly.backend.views.DTO.SentArtistDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "app.open-doors=true")
class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------- CREATE -----------------
    @Test
    void testCreateArtist_success() {
        ReceivedArtistDTO dto = new ReceivedArtistDTO();
        doNothing().when(artistService).createArtist(dto, 1L);

        ResponseEntity<Void> response = artistController.createArtist(dto, 1L);
        assertEquals(201, response.getStatusCodeValue());
        verify(artistService, times(1)).createArtist(dto, 1L);
    }

    @Test
    void testCreateArtistFromForm_success() {
        ArtistFormDTO dto = new ArtistFormDTO();
        doNothing().when(artistService).createArtistFromForm(dto, 2L);

        ResponseEntity<Void> response = artistController.createArtistFromForm(dto, 2L);
        assertEquals(201, response.getStatusCodeValue());
        verify(artistService, times(1)).createArtistFromForm(dto, 2L);
    }

    // ----------------- READ -----------------
    @Test
    void testGetArtistById_success() {
        SentArtistDTO sent = mock(SentArtistDTO.class);
        when(artistService.getArtistById(1L)).thenReturn(sent);

        ResponseEntity<SentArtistDTO> response = artistController.getArtistById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sent, response.getBody());
    }

    @Test
    void testGetArtistById_notFound() {
        when(artistService.getArtistById(1L)).thenThrow(new ArtistNotFoundException(1L));

        ResponseEntity<SentArtistDTO> response = artistController.getArtistById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAllArtists_success() {
        List<SentArtistDTO> list = List.of(mock(SentArtistDTO.class));
        when(artistService.getAllArtists()).thenReturn(list);

        ResponseEntity<List<SentArtistDTO>> response = artistController.getAllArtists();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
    }

    @Test
    void testGetTopTrendingArtists_success() {
        List<SentArtistDTO> list = List.of(mock(SentArtistDTO.class));
        when(artistService.getTopTrendingArtists()).thenReturn(list);

        ResponseEntity<List<SentArtistDTO>> response = artistController.getTopTrendingArtists();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
    }

    // ----------------- UPDATE -----------------
    @Test
    void testUpdateArtist_success() {
        ReceivedArtistDTO dto = new ReceivedArtistDTO();
        Artist updated = mock(Artist.class);
        when(artistService.updateArtist(1L, dto)).thenReturn(updated);

        ResponseEntity<?> response = artistController.updateArtist(1L, dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateArtist_notFound() {
        ReceivedArtistDTO dto = new ReceivedArtistDTO();
        when(artistService.updateArtist(1L, dto)).thenThrow(new ArtistNotFoundException(1L));

        ResponseEntity<?> response = artistController.updateArtist(1L, dto);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateDescription_success() {
        Artist sent = mock(Artist.class);
        when(artistService.updateDescription(1L, "New description")).thenReturn(sent);

        ResponseEntity<?> response = artistController.updateDescription(1L, "New description");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sent, response.getBody());
    }

    // ----------------- DELETE -----------------
    @Test
    void testDeleteArtistByUsername_success() {
        User user = mock(User.class);
        when(artistService.deleteArtistByUsername("artist1")).thenReturn(user);

        ResponseEntity<User> response = artistController.deleteArtistByUsername("artist1");
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteArtistByUsername_notFound() {
        when(artistService.deleteArtistByUsername("artist1")).thenThrow(new ArtistNotFoundException("artist1"));

        ResponseEntity<User> response = artistController.deleteArtistByUsername("artist1");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testRevertArtistToUser_success() {
        User user = mock(User.class);
        when(artistService.revertArtistToUser(1L)).thenReturn(user);

        ResponseEntity<User> response = artistController.revertArtistToUser(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testRevertArtistToUser_notFound() {
        when(artistService.revertArtistToUser(1L)).thenThrow(new ArtistNotFoundException(1L));

        ResponseEntity<User> response = artistController.revertArtistToUser(1L);
        assertEquals(404, response.getStatusCodeValue());
    }
}
