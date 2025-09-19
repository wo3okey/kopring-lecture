package org.sparta.kopring.common.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
interface Response<T> {
    val data: T

    companion object {
        fun <T> of(data: T): Response<T> = DefaultResponse(data)

        fun <T> empty(): Response<T?> = DefaultResponse(null)
    }
}

data class DefaultResponse<T>(
    override val data: T,
) : Response<T>
