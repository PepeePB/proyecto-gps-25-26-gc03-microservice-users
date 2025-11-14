package com.musicfly.backend.repositories;

import com.musicfly.backend.models.DAO.Payment;
import com.musicfly.backend.models.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE " +
            "(:artistId IS NULL OR p.artist.id = :artistId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:minAmount IS NULL OR p.amountPaid >= :minAmount) AND " +
            "(:maxAmount IS NULL OR p.amountPaid <= :maxAmount) AND " +
            "(:month IS NULL OR FUNCTION('MONTH', p.paymentDate) = :month) AND " +
            "(:year IS NULL OR FUNCTION('YEAR', p.paymentDate) = :year)")
    List<Payment> findFilteredPayments(
            @Param("artistId") Long artistId,
            @Param("status") PaymentStatus status,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}
