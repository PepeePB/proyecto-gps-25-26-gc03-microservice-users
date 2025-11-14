package com.musicfly.backend.views.DTO;

public class ArtistFormDTO {

    private String artistName;
    private String iban;              // IBAN del artista
    private String accountPropietary; // Titular de la cuenta
    private String description;       // Descripción del artista

    // Constructor vacío
    public ArtistFormDTO() {}

    // Constructor con todos los campos
    public ArtistFormDTO(String iban, String accountPropietary, String description, String artistName) {
        this.iban = iban;
        this.accountPropietary = accountPropietary;
        this.description = description;
        this.artistName = artistName;
    }

    // Getters y setters
    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAccountPropietary() {
        return accountPropietary;
    }

    public void setAccountPropietary(String accountPropietary) {
        this.accountPropietary = accountPropietary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
