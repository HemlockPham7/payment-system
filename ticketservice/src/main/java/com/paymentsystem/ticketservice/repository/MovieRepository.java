package com.paymentsystem.ticketservice.repository;

import com.paymentsystem.ticketservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
}
