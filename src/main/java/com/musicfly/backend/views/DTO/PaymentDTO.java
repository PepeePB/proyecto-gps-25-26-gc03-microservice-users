package com.musicfly.backend.views.DTO;

import com.musicfly.backend.models.PaymentMethod;
import com.musicfly.backend.models.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long artistId;
    private String artistName;
    private String concept;
    private Date paymentDate;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
}
