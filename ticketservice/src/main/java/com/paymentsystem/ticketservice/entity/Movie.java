package com.paymentsystem.ticketservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    //  JSON string (VD: {"A1":"AVAILABLE", "A2":"BOOKED", "B1":"LOCKED", ...})
    @Column(columnDefinition = "TEXT", name = "seats_status")
    private String seatsStatus;
}
