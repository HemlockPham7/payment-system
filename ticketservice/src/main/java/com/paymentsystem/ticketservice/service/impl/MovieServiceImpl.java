package com.paymentsystem.ticketservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.ticketservice.dto.CreateMovieRequest;
import com.paymentsystem.ticketservice.entity.Movie;
import com.paymentsystem.ticketservice.repository.MovieRepository;
import com.paymentsystem.ticketservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    @Override
    public Movie getMovieById(String id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie Id not found: " + id));
    }

    @Override
    public Movie createMovie(CreateMovieRequest request) {
        Map<String, String> initialSeats = new LinkedHashMap<>();
        for (int row = 1; row <= 5; row++) {
            for (char col = 'A'; col <= 'D'; col++) {
                String seatCode = row + String.valueOf(col);
                initialSeats.put(seatCode, "AVAILABLE");
            }
        }

        String seatsStatusJson;
        try {
            seatsStatusJson = objectMapper.writeValueAsString(initialSeats);
        } catch (Exception e) {
            seatsStatusJson = "{}";
        }

        Movie movie = Movie.builder()
                .id(UUID.randomUUID().toString())
                .title(request.title())
                .description(request.description())
                .seatsStatus(seatsStatusJson)
                .build();
        return movieRepository.save(movie);
    }
}