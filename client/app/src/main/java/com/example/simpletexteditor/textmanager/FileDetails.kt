package com.example.simpletexteditor.textmanager

import kotlinx.serialization.Serializable
import java.util.UUID

data class FileDetails(
    val name: String,
    val memoryFile: MemoryFile,
    var id: UUID = UUID.randomUUID(),
    var areChangesSavedLocally: Boolean = true,
    var isSyncedWithServer: Boolean = false,
    var isStoredOnCloud: Boolean = false
) {
    fun exportToSerializable(): SavedFileDetails {
        return SavedFileDetails(
            name = this.name,
            id = this.id.toString(),
            areChangesSavedLocally = this.areChangesSavedLocally,
            isSyncedWithServer = this.isSyncedWithServer,
            isStoredOnCloud = this.isStoredOnCloud
        )
    }

    companion object {
        fun importFromSerializable(serializedData: SavedFileDetails): FileDetails {
            return FileDetails(
                name = serializedData.name,
                id = UUID.fromString(serializedData.id),
                //TODO: get the data from the files
                memoryFile = MemoryFile(),
                areChangesSavedLocally = serializedData.areChangesSavedLocally,
                isSyncedWithServer = serializedData.isSyncedWithServer,
                isStoredOnCloud = serializedData.isStoredOnCloud
            )
        }
    }
}

@Serializable
data class SavedFileDetails(
    val name: String,
    var id: String,
    var areChangesSavedLocally: Boolean,
    var isSyncedWithServer: Boolean,
    var isStoredOnCloud: Boolean
)
