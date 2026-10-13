package com.paymentsystem.ticketservice.dto;

import lombok.Builder;

@Builder
public record CreateMovieRequest(
        String title,
        String description
) {
}
