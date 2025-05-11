package com.jizhang.ak.data.remote

import com.jizhang.ak.data.remote.dto.AuthResponse
import com.jizhang.ak.data.remote.dto.LoginRequest
import com.jizhang.ak.data.remote.dto.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object AuthApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // 重要，如果后端返回额外字段
            })
        }
        install(Logging) { // 可选
            level = LogLevel.BODY
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url("https://my-auth-worker.2605063a.workers.dev/") // !!替换为您的 Worker URL!!
        }
    }

    suspend fun registerUser(request: RegisterRequest): AuthResponse {
        return try {
            val response = client.post("api/auth/register") {
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                response.body<AuthResponse>()
            } else {
                // 尝试解析错误响应体，如果后端在错误时也返回 AuthResponse 结构
                try {
                    response.body<AuthResponse>()
                } catch (e: Exception) {
                     AuthResponse(error = "Registration failed. Status: ${response.status.value}. Message: ${response.bodyAsText().take(200)}")
                }
            }
        } catch (e: JsonConvertException) {
            AuthResponse(error = "Registration failed: Invalid data from server. ${e.message?.take(200)}")
        } catch (e: SerializationException) {
            AuthResponse(error = "Registration failed: Could not parse server response. ${e.message?.take(200)}")
        } catch (e: io.ktor.client.plugins.ClientRequestException) {
             AuthResponse(error = "Registration failed: Network request error. Status: ${e.response.status.value}. ${e.message?.take(200)}")
        } catch (e: Exception) {
            AuthResponse(error = "An unexpected error occurred during registration: ${e.message?.take(200)}")
        }
    }

    suspend fun loginUser(request: LoginRequest): AuthResponse {
        return try {
            val response = client.post("api/auth/login") {
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<AuthResponse>()
            } else {
                // 尝试解析错误响应体
                try {
                    response.body<AuthResponse>() // 如果错误也返回 AuthResponse 结构
                } catch (e: Exception) { // 如果错误返回不同结构或纯文本
                    AuthResponse(error = "Login failed. Status: ${response.status.value}. Message: ${response.bodyAsText().take(200)}")
                }
            }
        } catch (e: JsonConvertException) {
            // 这个异常通常是 kotlinx.serialization 在转换时发现类型不匹配或缺少字段
            AuthResponse(error = "Login failed: Invalid data from server. ${e.message?.take(200)}")
        } catch (e: SerializationException) {
            // 更通用的序列化问题
            AuthResponse(error = "Login failed: Could not parse server response. ${e.message?.take(200)}")
        } catch (e: io.ktor.client.plugins.ClientRequestException) {
            // 捕获客户端请求错误，例如 4xx 或 5xx 状态码
            AuthResponse(error = "Login failed: Network request error. Status: ${e.response.status.value}. ${e.message?.take(200)}")
        } catch (e: Exception) {
            // 其他所有未预料到的异常
            AuthResponse(error = "An unexpected error occurred during login: ${e.message?.take(200)}")
        }
    }
}