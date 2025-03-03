package com.example.simpletexteditor.textmanager

import java.util.UUID

data class FileDetails(
    val name: String,
    val memoryFile: MemoryFile,
    var id: UUID = UUID.randomUUID(),
    var areChangesSavedLocally: Boolean = true,
    var isSyncedWithServer: Boolean = false
)
