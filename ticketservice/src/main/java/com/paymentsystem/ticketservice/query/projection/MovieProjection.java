package com.paymentsystem.ticketservice.query.projection;

import com.paymentsystem.ticketservice.entity.Movie;
import com.paymentsystem.ticketservice.model.MovieResponseModel;
import com.paymentsystem.ticketservice.query.queries.GetMovieDetailQuery;
import com.paymentsystem.ticketservice.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieProjection {

    private final MovieRepository movieRepository;

    @QueryHandler
    public MovieResponseModel handle(GetMovieDetailQuery query) throws Exception {
        Movie movie = movieRepository.findById(query.getId())
                .orElseThrow(() -> new Exception("Movie not found with MovieId: " + query.getId()));
        return MovieResponseModel.builder()
                .id(movie.getId())
                .seatsStatus(movie.getSeatsStatus())
                .build();
    }
}
