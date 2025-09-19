package org.sparta.kopring

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.sparta.kopring.movie.dto.MovieRequest
import org.sparta.kopring.movie.entity.Movie
import org.sparta.kopring.movie.repository.MovieRepository
import org.sparta.kopring.movie.service.MovieService
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class MovieServiceTest {
    @Mock
    lateinit var movieRepository: MovieRepository

    @InjectMocks
    lateinit var movieService: MovieService

    @Nested
    inner class GetMovieTest {
        @Test
        fun `영화 조회 - entity 못찾는 에러`() {
            val movieId = 1L
            val emptyMovie = Optional.empty<Movie>()

            given(movieRepository.findById(anyLong())).willReturn(emptyMovie)

            val exception = assertThrows(IllegalArgumentException::class.java) { movieService.getMovie(movieId) }
            assertEquals("entity not found.", exception.message)
        }

        @Test
        fun `영화 조회 - 정상 조회`() {
            val movieId = 1L
            val emptyMovie = Optional.of(Movie(1, "test", 2025))

            given(movieRepository.findById(anyLong())).willReturn(emptyMovie)

            val result = movieService.getMovie(movieId)
            assertNotNull(result)
        }
    }

    @Nested
    inner class PostMovieTest {
        @Test
        fun `영화 등록 - 정상 등록`() {
            val movieRequest = MovieRequest("test", 2025)

            given(movieRepository.save(any())).willReturn(Movie(name = "test", productionYear = 2025))

            movieService.saveMovie(movieRequest)

            verify(movieRepository).save(any())
        }
    }
}
