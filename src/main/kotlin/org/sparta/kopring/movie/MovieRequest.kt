package org.sparta.kopring.movie

data class MovieRequest(
    val name: String,
    val writeYearYn: Boolean,
    val productionYear: Int? = null,
) {
    fun toEntity() = Movie(name = name, productionYear = productionYear)
}