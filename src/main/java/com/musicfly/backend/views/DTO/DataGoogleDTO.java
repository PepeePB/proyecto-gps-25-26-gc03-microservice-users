package com.musicfly.backend.views.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataGoogleDTO {

    String access_token;
    Integer expires_in;
    String scope;
    String token_type;
    String id_token;

}
