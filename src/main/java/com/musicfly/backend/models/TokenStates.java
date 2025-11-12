package com.musicfly.backend.models;

public enum TokenStates {
    RENEWED("token.renew"),
    CREATED("token.created"),
    DELETED("token.deleted"),
    ALREADY_EXISTS("token.already.exists"),
    NOT_VERIFIED("token.not_verified");

    private String value;

    private TokenStates(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
