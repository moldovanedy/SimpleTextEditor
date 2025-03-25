package com.example.simpletexteditor.cloudmanager

import android.content.Context
import android.util.Log
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.R
import com.example.simpletexteditor.cloudmanager.dtos.user.ForgotPasswordUserDto
import com.example.simpletexteditor.cloudmanager.dtos.user.LoginUserDto
import com.example.simpletexteditor.cloudmanager.dtos.user.NewUserDto
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class IdentityManagement {
    companion object {
        suspend fun register(userDto: NewUserDto): String? {
            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/users")) {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(userDto))
                    }

                return if (response.status.value == 201)
                    null
                else
                    response.body()
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }

        suspend fun login(loginDto: LoginUserDto): String? {
            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/users/login")) {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(loginDto))
                    }

                if (response.status.value == 200) {
                    //save token
                    val token: String = Json.decodeFromString<LoginResponse>(response.body()).authToken
                    val prefs = MainActivity.getActivity()?.getPreferences(Context.MODE_PRIVATE)
                        ?: return "An unknown error occurred on your side."

                    with(prefs.edit()) {
                        putString("AUTH_TOKEN", token)
                        apply()
                    }

                    CloudFileManagement.refreshAuthToken()
                    return null
                }

                return response.body()
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }

        suspend fun logout(): String? {
            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/users/logout")) {
                        bearerAuth(CloudFileManagement.authToken.toString())
                        contentType(ContentType.Application.Json)
                    }

                if (response.status.value == 200) {
                    val prefs = MainActivity.getActivity()?.getPreferences(Context.MODE_PRIVATE)
                        ?: return MainActivity.getContext()?.resources?.getText(R.string.logout_failed).toString()

                    with(prefs.edit()) {
                        putString("AUTH_TOKEN", "")
                        apply()
                    }

                    CloudFileManagement.refreshAuthToken()
                    return null
                }

                return response.body()
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }

        suspend fun forgotPassword(forgotPasswordDto: ForgotPasswordUserDto): String? {
            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/users/forgot-password")) {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(forgotPasswordDto))
                    }

                if (response.status.value == 200) {
                    //save token
                    val token: String = Json.decodeFromString<LoginResponse>(response.body()).authToken
                    val prefs = MainActivity.getActivity()?.getPreferences(Context.MODE_PRIVATE)
                        ?: return "An unknown error occurred on your side."

                    with(prefs.edit()) {
                        putString("AUTH_TOKEN", token)
                        apply()
                    }

                    return null
                } else {
                    return response.body()
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }

        @Serializable
        private data class LoginResponse(val authToken: String)
    }
}
