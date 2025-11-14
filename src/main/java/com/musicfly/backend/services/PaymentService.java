package com.musicfly.backend.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.musicfly.backend.models.DAO.Artist;
import com.musicfly.backend.models.DAO.Payment;
import com.musicfly.backend.models.PaymentStatus;
import com.musicfly.backend.repositories.ArtistRepository;
import com.musicfly.backend.repositories.PaymentRepository;
import com.musicfly.backend.views.DTO.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ArtistRepository artistRepository;

    public PaymentDTO createPayment(PaymentDTO dto) {
        // Obtener el artista desde la base de datos usando el artistId
        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artista no encontrado"));

        // Crear el objeto Payment utilizando el artista completo
        Payment payment = Payment.builder()
                .artist(artist)  // Ahora se asigna el objeto Artist completo
                .concept(dto.getConcept())
                .paymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : new Date())  // Si no se proporciona, se asigna la fecha actual
                .amountPaid(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(dto.getStatus() != null ? dto.getStatus() : PaymentStatus.PENDING)  // Si no se proporciona, se asigna PENDING como valor por defecto
                .build();

        // Guardar el pago en la base de datos y devolver el DTO
        return toDTO(paymentRepository.save(payment));
    }


    public List<PaymentDTO> filterPayments(Long artistId, Integer month, Integer year, PaymentStatus status, BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findAll().stream()
                .filter(p -> (artistId == null || p.getArtist().getId().equals(artistId)) &&
                        (month == null || p.getPaymentDate().getMonth() + 1 == month) &&
                        (year == null || p.getPaymentDate().getYear() + 1900 == year) &&
                        (status == null || p.getStatus() == status) &&
                        (minAmount == null || p.getAmountPaid().compareTo(minAmount) >= 0) &&
                        (maxAmount == null || p.getAmountPaid().compareTo(maxAmount) <= 0))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public byte[] generatePaymentReceipt(Long paymentId) throws Exception {
        // Buscar el pago y lanzar EntityNotFoundException si no existe
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Pago con ID " + paymentId + " no encontrado"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Solo se puede generar comprobante para pagos completados.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Comprobante de Pago").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(18));
        document.add(new Paragraph(" "));  // Espacio en blanco

        document.add(new Paragraph("ID del pago: " + payment.getId()));
        document.add(new Paragraph("Artista: " + payment.getArtist().getArtisticName()));
        document.add(new Paragraph("Fecha: " + payment.getPaymentDate()));
        document.add(new Paragraph("Cantidad: €" + payment.getAmountPaid()));
        document.add(new Paragraph("Método de pago: " + payment.getPaymentMethod().getDescription()));
        document.add(new Paragraph("Concepto: " + payment.getConcept()));

        document.close();
        return out.toByteArray();
    }

    private PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .artistId(payment.getArtist().getId())
                .artistName(payment.getArtist().getArtisticName())
                .concept(payment.getConcept())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmountPaid())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .build();
    }

    public PaymentDTO updatePayment(Long paymentId, PaymentDTO paymentDTO) {
        // Buscar el pago en la base de datos
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));

        // Buscar al artista por su ID
        Artist artist = artistRepository.findById(paymentDTO.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artista no encontrado"));

        // Actualizar todos los campos del pago usando el PaymentDTO
        payment.setArtist(artist);  // Asignar el artista encontrado
        payment.setConcept(paymentDTO.getConcept());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setAmountPaid(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setStatus(paymentDTO.getStatus());

        // Guardar el pago con los nuevos datos
        Payment updatedPayment = paymentRepository.save(payment);
        paymentRepository.flush();  // Asegurarse de que los cambios se guarden inmediatamente

        // Retornar el DTO con el pago actualizado
        return toDTO(updatedPayment);
    }

}
