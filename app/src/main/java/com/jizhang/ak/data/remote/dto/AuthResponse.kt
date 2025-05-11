package com.jizhang.ak.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String? = null,
    val message: String? = null,
    val error: String? = null,
    val userId: String? = null
)