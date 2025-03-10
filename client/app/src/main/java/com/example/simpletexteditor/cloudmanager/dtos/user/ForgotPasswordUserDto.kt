package com.example.simpletexteditor.cloudmanager.dtos.user

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordUserDto(val email: String, val newPassword: String)
