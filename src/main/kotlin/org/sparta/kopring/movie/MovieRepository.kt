package org.sparta.kopring.movie

import org.springframework.data.jpa.repository.JpaRepository
import kotlin.jvm.optionals.getOrNull

fun MovieRepository.findByIdOrElseThrow(movieId: Long): Movie = findById(movieId).getOrNull() ?: throw IllegalArgumentException("entity not found.")

interface MovieRepository : JpaRepository<Movie, Long>