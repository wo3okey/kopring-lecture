package org.sparta.kopring.movie

class MovieResponse(
    val name: String
) {
    companion object {
        fun of(movie: Movie): MovieResponse = MovieResponse(movie.name)
    }
}