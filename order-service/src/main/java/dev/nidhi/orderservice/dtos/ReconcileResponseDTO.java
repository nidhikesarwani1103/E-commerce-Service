package dev.nidhi.orderservice.dtos;

public record ReconcileResponseDTO(
        Long paymentId,
        String status
) {}
