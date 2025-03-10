package com.example.simpletexteditor.cloudmanager.dtos.user

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserDto(val email: String, val password: String)
