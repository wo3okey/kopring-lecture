package org.sparta.kopring.movie.controller

import org.sparta.kopring.common.dto.Response
import org.sparta.kopring.movie.dto.MovieRequest
import org.sparta.kopring.movie.dto.MovieResponse
import org.sparta.kopring.movie.service.MovieService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MovieController(
    val movieService: MovieService,
) {
    @GetMapping("/api/v1/movies/{movieId}")
    fun getMovie(
        @PathVariable movieId: Long,
    ): Response<MovieResponse> = Response.Companion.of(movieService.getMovie(movieId))

    @PostMapping("/api/v1/movies")
    fun saveMovie(
        @RequestBody movieRequest: MovieRequest,
    ) {
        movieService.saveMovie(movieRequest)
    }

    @PostMapping("/api/v1/movies/bulk")
    suspend fun saveMovieBulkAsync(
        @RequestBody movieRequest: MovieRequest,
    ) {
        movieService.saveMovieAsync(movieRequest)
    }
}
