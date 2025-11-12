package com.musicfly.backend.models;

public enum UserOptionsUUID {
    CONFIRM_ACCOUNT("CF#"),
    BLACK_LIST("BL#"),
    RESET_PASSWORD("RP#"),
    VALID_TOKEN("VT#");

    private String value;

    private UserOptionsUUID(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
