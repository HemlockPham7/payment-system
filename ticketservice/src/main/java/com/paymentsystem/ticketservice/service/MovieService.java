package com.paymentsystem.ticketservice.service;

import com.paymentsystem.ticketservice.dto.CreateMovieRequest;
import com.paymentsystem.ticketservice.entity.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
    Movie createMovie(CreateMovieRequest request);
}
