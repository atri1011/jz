package com.jizhang.ak.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jizhang.ak.data.local.AuthPreferences
import com.jizhang.ak.data.remote.AuthApiService
import com.jizhang.ak.data.remote.dto.LoginRequest
import com.jizhang.ak.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null) // 新增，用于存储用户ID
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false) // 新增，用于表示加载状态
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkInitialAuthState()
    }

    private fun checkInitialAuthState() {
        viewModelScope.launch {
            val token = AuthPreferences.getAuthToken(getApplication())
            val storedUserId = AuthPreferences.getUserId(getApplication())
            if (!token.isNullOrEmpty()) {
                _authToken.value = token
                _userId.value = storedUserId
                _isLoggedIn.value = true
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val response = AuthApiService.registerUser(RegisterRequest(email, password))
                if (!response.token.isNullOrEmpty()) { // 主要检查 token
                    AuthPreferences.saveAuthToken(getApplication(), response.token)
                    _authToken.value = response.token
                    _isLoggedIn.value = true
                    if (response.userId != null) { // userId 是可选的
                        AuthPreferences.saveUserId(getApplication(), response.userId)
                        _userId.value = response.userId
                    } else {
                        AuthPreferences.saveUserId(getApplication(), null) // 确保清除旧的 userId
                        _userId.value = null
                    }
                } else {
                    _authError.value = response.error ?: response.message ?: "Registration failed (token missing)"
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _authError.value = e.message ?: "An unexpected error occurred"
                _isLoggedIn.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            try {
                val response = AuthApiService.loginUser(LoginRequest(email, password))
                if (!response.token.isNullOrEmpty()) { // 主要检查 token
                    AuthPreferences.saveAuthToken(getApplication(), response.token)
                    _authToken.value = response.token
                    _isLoggedIn.value = true
                    if (response.userId != null) { // userId 是可选的
                        AuthPreferences.saveUserId(getApplication(), response.userId)
                        _userId.value = response.userId
                    } else {
                        AuthPreferences.saveUserId(getApplication(), null) // 确保清除旧的 userId
                        _userId.value = null
                    }
                } else {
                    _authError.value = response.error ?: response.message ?: "Login failed (token missing)"
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _authError.value = e.message ?: "An unexpected error occurred"
                _isLoggedIn.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            AuthPreferences.clear(getApplication())
            _authToken.value = null
            _userId.value = null
            _isLoggedIn.value = false
            _authError.value = null
        }
    }

    fun clearError() { // 新增，用于清除错误信息，以便UI可以重置
        _authError.value = null
    }
}