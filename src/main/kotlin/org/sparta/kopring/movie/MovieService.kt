package org.sparta.kopring.movie

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovieService(
    val movieRepository: MovieRepository
) {
    private val log = LoggerFactory.getLogger(MovieService::class.java)

    fun getMovie(movieId: Long): MovieResponse {
        val movie = movieRepository.findByIdOrElseThrow(movieId)
        return MovieResponse.of(movie)
    }

    @Transactional
    fun saveMovie(movieRequest: MovieRequest) {
        if (movieRequest.writeYearYn) {
            checkNotNull(movieRequest.productionYear) { "write movie product year." }
        }
        movieRepository.save(movieRequest.toEntity())
    }

    @Transactional
    suspend fun saveMovieBulkAsync(movieRequest: MovieRequest) {
        val list = (1..100).map { movieRequest.copy(name = movieRequest.name + it) }
        log.info("시작")
        runBlocking(Dispatchers.IO) {
            val defer = list.map {
                async {
                    saveMovie(it)
                    log.info(Thread.currentThread().name)
                }
            }
            delay(1000)
            defer.awaitAll()
        }

        log.info("끝")

        log.info("시작2")
        CoroutineScope(Dispatchers.IO).launch {
            list.map {
                async {
                    saveMovie(it)
                    log.info(Thread.currentThread().name)
                }
            }
        }

        log.info("끝2")
    }
}