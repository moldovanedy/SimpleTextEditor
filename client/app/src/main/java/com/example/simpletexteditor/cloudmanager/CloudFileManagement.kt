package com.example.simpletexteditor.cloudmanager

import android.content.Context
import android.util.Log
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDetailsDto
import com.example.simpletexteditor.cloudmanager.dtos.file.FileDiffDto
import com.example.simpletexteditor.cloudmanager.dtos.file.FileFullUpdateDto
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlin.jvm.internal.Ref.ObjectRef

class CloudFileManagement {
    companion object {
        internal var authToken: String? = null

        fun refreshAuthToken() {
            authToken = MainActivity
                .getActivity()
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getString("AUTH_TOKEN", "")
                ?: ""
        }

        suspend fun createFile(fileName: String, serverId: ObjectRef<String>): String? {
            if (authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/files")) {
                        contentType(ContentType.Application.Json)
                        bearerAuth(authToken.toString())
                        setBody("\"".plus(fileName).plus("\""))
                    }

                if (response.status.value == 201) {
                    var rawID: String = response.body()
                    rawID = rawID.substring(1, rawID.length - 1)
                    serverId.element = rawID
                    return null
                } else {
                    return response.body()
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return "An unknown error occurred on your side."
            }
        }

        suspend fun getFileContents(fileId: String): Pair<Boolean, String> {
            if (authToken == null) {
                return Pair(true, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(authToken.toString())
                    }

                return Pair(response.status.value == 200, response.body())
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                return Pair(false, "An unknown error occurred on your side.")
            }
        }

        suspend fun getFileDetails(fileId: String): Pair<FileDetailsDto?, String?> {
            if (authToken == null) {
                return Pair(null, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/details/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(authToken.toString())
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
            if (authToken == null) {
                return Pair(null, "You are not logged in.")
            }

            try {
                val response =
                    BaseClient.client.get(BaseClient.domain.plus("/files/details")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(authToken.toString())
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

        suspend fun updateFileByDiff(fileId: String, diff: FileDiffDto): String? {
            if (authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/files/${fileId}")) {
                        contentType(ContentType.Application.Json)
                        bearerAuth(authToken.toString())
                        setBody(Json.encodeToString(diff))
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

        suspend fun fullyUpdateFile(fileId: String, content: String): String? {
            if (authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.post(BaseClient.domain.plus("/files/${fileId}/full-update")) {
                        contentType(ContentType.Application.Json)
                        bearerAuth(authToken.toString())
                        setBody(Json.encodeToString(FileFullUpdateDto(content)))
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

        suspend fun deleteFile(fileId: String): String? {
            if (authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.delete(BaseClient.domain.plus("/files/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(authToken.toString())
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

        suspend fun renameFile(fileId: String, newName: String): String? {
            if (authToken == null) {
                return "You are not logged in."
            }

            try {
                val response =
                    BaseClient.client.put(BaseClient.domain.plus("/files/details/${fileId}")) {
                        contentType(ContentType.Text.Plain)
                        bearerAuth(authToken.toString())
                        setBody(newName)
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