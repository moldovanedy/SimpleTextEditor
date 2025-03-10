package com.example.simpletexteditor.cloudmanager.dtos.file

import kotlinx.serialization.Serializable

@Serializable
data class FileDetailsDto(
    val id: String,
    val name: String,
    val size: Int,
    val dateCreated: Long,
    val dateModified: Long
)
