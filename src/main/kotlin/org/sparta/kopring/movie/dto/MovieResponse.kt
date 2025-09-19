package org.sparta.kopring.movie.dto

import org.sparta.kopring.movie.entity.Movie

class MovieResponse(
    val name: String,
) {
    companion object {
        fun of(movie: Movie): MovieResponse = MovieResponse(movie.name)
    }
}
