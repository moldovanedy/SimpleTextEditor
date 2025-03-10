package com.example.simpletexteditor.cloudmanager

import android.content.Context
import android.util.Log
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDetailsDto
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class FileManagement {
    companion object {
        private var _authToken: String? = null

        fun refreshAuthToken() {
            _authToken = MainActivity
                .getActivity()
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getString("AUTH_TOKEN", "")
                ?: ""
        }

        suspend fun createFile(fileName: String): String? {
            if (_authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/files")) {
                        contentType(ContentType.Application.Json)
                        bearerAuth(_authToken.toString())
                        setBody("\"".plus(fileName).plus("\""))
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

        suspend fun getFileContents(fileId: String): Pair<Boolean, String> {
            if (_authToken == null) {
                return Pair(true, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(_authToken.toString())
                    }

                return Pair(response.status.value == 200, response.body())
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return Pair(false, "An unknown error occurred on your side.")
            }
        }

        suspend fun getFileDetails(fileId: String): Pair<FileDetailsDto?, String?> {
            if (_authToken == null) {
                return Pair(null, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/details/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(_authToken.toString())
                    }

                return if (response.status.value == 200) {
                    Pair(Json.decodeFromString<FileDetailsDto>(response.body()), null)
                } else {
                    Pair(null, response.body())
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return Pair(null, "An unknown error occurred on your side.")
            }
        }

        suspend fun getAllFilesDetails(): Pair<List<FileDetailsDto>?, String?> {
            if (_authToken == null) {
                return Pair(null, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/details")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(_authToken.toString())
                    }

                return if (response.status.value == 200) {
                    Pair(Json.decodeFromString<List<FileDetailsDto>>(response.body()), null)
                } else {
                    Pair(null, response.body())
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return Pair(null, "An unknown error occurred on your side.")
            }
        }

        suspend fun deleteFile(fileId: String): String? {
            if (_authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.delete(BaseClient.domain.plus("/files/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(_authToken.toString())
                    }

                return if (response.status.value == 200) {
                    null
                } else {
                    response.body()
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }
    }
}