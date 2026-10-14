package com.paymentsystem.ticketservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    //  JSON string (VD: {"A1":"AVAILABLE", "A2":"BOOKED", "B1":"LOCKED", ...})
    @Column(columnDefinition = "TEXT", name = "seats_status")
    private String seatsStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void updatedAt() {
        updatedAt = LocalDateTime.now();
    }
}
