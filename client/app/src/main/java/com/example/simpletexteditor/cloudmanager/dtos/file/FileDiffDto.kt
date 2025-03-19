package com.example.simpletexteditor.cloudmanager.dtos.file

import kotlinx.serialization.Serializable

@Serializable
data class FileDiffDto(val textChange: String, val isAdded: Boolean, val index: Int)
