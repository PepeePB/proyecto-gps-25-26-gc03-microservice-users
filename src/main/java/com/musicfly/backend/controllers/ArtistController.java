package com.musicfly.backend.controllers;

import com.musicfly.backend.exceptions.artist.ArtistNotFoundException;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.services.ArtistService;
import com.musicfly.backend.views.DTO.ArtistFormDTO;
import com.musicfly.backend.views.DTO.ReceivedArtistDTO;
import com.musicfly.backend.views.DTO.SentArtistDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de artistas.
 */
@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // ----------------- CREATE -----------------

    @PostMapping("/create/{userId}")
    public ResponseEntity<Void> createArtist(@RequestBody ReceivedArtistDTO dto, @PathVariable Long userId) {
        try {
            artistService.createArtist(dto, userId);
            return ResponseEntity.status(201).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create-form/{userId}")
    public ResponseEntity<Void> createArtistFromForm(@RequestBody ArtistFormDTO dto, @PathVariable Long userId) {
        try {
            artistService.createArtistFromForm(dto, userId);
            return ResponseEntity.status(201).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ----------------- READ -----------------

    @GetMapping("/{id}")
    public ResponseEntity<SentArtistDTO> getArtistById(@PathVariable Long id) {
        try {
            SentArtistDTO artist = artistService.getArtistById(id);
            return ResponseEntity.ok(artist);
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<SentArtistDTO> getArtistByUsername(@PathVariable String username) {
        try {
            SentArtistDTO artist = artistService.getArtistByUsername(username);
            return ResponseEntity.ok(artist);
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SentArtistDTO> getArtistByEmail(@PathVariable String email) {
        try {
            SentArtistDTO artist = artistService.getArtistByEmail(email);
            return ResponseEntity.ok(artist);
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SentArtistDTO>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<SentArtistDTO>> getTopTrendingArtists() {
        return ResponseEntity.ok(artistService.getTopTrendingArtists());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SentArtistDTO>> getArtistsByName(@RequestParam String name) {
        return ResponseEntity.ok(artistService.getArtistsByName(name));
    }

    // ----------------- UPDATE -----------------

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArtist(@PathVariable Long id, @RequestBody ReceivedArtistDTO dto) {
        try {
            return ResponseEntity.ok(artistService.updateArtist(id, dto));
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<?> updateDescription(@PathVariable Long id, @RequestBody String description) {
        try {
            return ResponseEntity.ok(artistService.updateDescription(id, description));
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/artisticName")
    public ResponseEntity<?> updateArtisticName(@PathVariable Long id, @RequestBody String artisticName) {
        try {
            return ResponseEntity.ok(artistService.updateArtisticName(id, artisticName));
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ----------------- DELETE -----------------

    @DeleteMapping("/username/{username}")
    public ResponseEntity<User> deleteArtistByUsername(@PathVariable String username) {
        try {
            User user = artistService.deleteArtistByUsername(username);
            return ResponseEntity.noContent().build();
        } catch (ArtistNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/revert")
    public ResponseEntity<User> revertArtistToUser(@PathVariable Long id) {
        try {
            User user = artistService.revertArtistToUser(id);
            return ResponseEntity.noContent().build();
        } catch (ArtistNotFoundException | IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
