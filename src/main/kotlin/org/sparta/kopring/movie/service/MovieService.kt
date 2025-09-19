package org.sparta.kopring.movie.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.sparta.kopring.movie.dto.MovieRequest
import org.sparta.kopring.movie.dto.MovieResponse
import org.sparta.kopring.movie.repository.MovieRepository
import org.sparta.kopring.movie.repository.findByIdOrElseThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovieService(
    val movieRepository: MovieRepository,
) {
    private val log = LoggerFactory.getLogger(MovieService::class.java)

    fun getMovie(movieId: Long): MovieResponse {
        val movie = movieRepository.findByIdOrElseThrow(movieId)
        return MovieResponse.Companion.of(movie)
    }

    @Transactional
    fun saveMovie(movieRequest: MovieRequest) {
        movieRepository.save(movieRequest.toEntity())
    }

    @Transactional
    suspend fun saveMovieAsync(movieRequest: MovieRequest) {
        val list = (1..100).map { movieRequest.copy(name = movieRequest.name + it) }
        log.info("[start] coroutine blocking")
        runBlocking(Dispatchers.IO) {
            val defer =
                list.map {
                    async {
                        saveMovie(it)
                        log.info(Thread.currentThread().name)
                    }
                }
            delay(1000)
            defer.awaitAll()
        }

        log.info("[end] coroutine blocking")

        log.info("[start] coroutine launch")
        CoroutineScope(Dispatchers.IO).launch {
            list.map {
                async {
                    saveMovie(it)
                    log.info(Thread.currentThread().name)
                }
            }
        }
        log.info("[end] coroutine launch")
    }
}
