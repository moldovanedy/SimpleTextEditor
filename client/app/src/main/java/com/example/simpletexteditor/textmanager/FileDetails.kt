package com.example.simpletexteditor.textmanager

import kotlinx.serialization.Serializable
import java.util.UUID

data class FileDetails(
    var name: String,
    val memoryFile: MemoryFile,
    var id: UUID = UUID.randomUUID(),
    var areChangesSavedLocally: Boolean = true,
    var isStoredOnCloud: Boolean = false
) {
    fun exportToSerializable(): SavedFileDetails {
        return SavedFileDetails(
            name = this.name,
            id = this.id.toString(),
            isStoredOnCloud = this.isStoredOnCloud
        )
    }

    companion object {
        fun importFromSerializable(serializedData: SavedFileDetails): FileDetails {
            val uuid = UUID.fromString(serializedData.id)
            val memoryFile = MemoryFile(FileHandler.getFileContent(uuid))

            return FileDetails(
                name = serializedData.name,
                id = uuid,
                memoryFile = memoryFile,
                isStoredOnCloud = serializedData.isStoredOnCloud
            )
        }
    }
}

@Serializable
data class SavedFileDetails(
    val name: String,
    var id: String,
    @Deprecated("This is not used. It's here to avoid compatibility break")
    var areChangesSavedLocally: Boolean = true,
    @Deprecated("This is not used. It's here to avoid compatibility break")
    var isSyncedWithServer: Boolean = true,
    var isStoredOnCloud: Boolean
)
