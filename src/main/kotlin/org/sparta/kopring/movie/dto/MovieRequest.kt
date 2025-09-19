package org.sparta.kopring.movie.dto

import org.sparta.kopring.movie.entity.Movie

data class MovieRequest(
    val name: String,
    val productionYear: Int? = null,
) {
    fun toEntity() = Movie(name = name, productionYear = productionYear)
}
