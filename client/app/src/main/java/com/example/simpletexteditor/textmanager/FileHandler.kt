package com.example.simpletexteditor.textmanager

import android.content.Context
import android.util.Log
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.utils.Event
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists

class FileHandler {
    companion object {
        private const val FILE_NAME = "opened_files.json"

        var activeFileIndex: Int = -1
            set(value) {
                if (value < 0 || value >= _openFiles.size) {
                    throw Exception("Active file set to invalid index")
                }

                field = value
                activeFileChangedEvent.invoke(value)
            }

        val activeFileChangedEvent: Event<Int> = Event()

        private val _openFiles: MutableList<FileDetails> = mutableListOf()


        fun loadFromStorage(): Boolean {
            val context = MainActivity.getContext() ?: return false

            //Path(context.filesDir.path.plus("/data.txt")).deleteIfExists()
            if (Path(context.filesDir.path.plus(FILE_NAME)).exists()) {
                try {
                    Path(context.filesDir.path.plus(FILE_NAME)).createFile()
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    return false
                }
            }

            var fs: FileInputStream? = null
            var success: Boolean

            try {
                fs = MainActivity.getContext()?.openFileInput(FILE_NAME)
                if (fs == null) {
                    return false
                }

                val bytes = fs.readBytes()
                val str = bytes.toString(Charsets.UTF_8)
                if (str == "") {
                    return true
                }

                val savedList = Json.decodeFromString<List<SavedFileDetails>>(str)
                _openFiles.clear()
                for (item in savedList) {
                    _openFiles.add(FileDetails.importFromSerializable(item))
                }

                activeFileIndex = 0
                activeFileChangedEvent.invoke(0)

                success = true
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                success = false
            } finally {
                fs?.close()
            }

            return success
        }

        fun saveToStorage(): Boolean {
            var fs: FileOutputStream? = null
            var success: Boolean

            try {
                fs = MainActivity.getContext()?.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                if (fs == null) {
                    return false
                }

                val dataArray = mutableListOf<SavedFileDetails>()
                for (fileDetails in _openFiles) {
                    dataArray.add(fileDetails.exportToSerializable())
                }

                fs.write(Json.encodeToString(dataArray).toByteArray(Charsets.UTF_8))
                success = true
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                success = false
            } finally {
                fs?.close()
            }

            return success
        }


        fun createFile(fileName: String, isStoredOnCloud: Boolean = false) {
            _openFiles.add(
                FileDetails(
                    name = generateNewName(fileName),
                    memoryFile = MemoryFile(),
                    isStoredOnCloud = isStoredOnCloud
                )
            )
        }

        fun generateNewName(givenName: String = "New file"): String {
            //add a " (n)" (n >= 2) to the name if the name is duplicate
            var newFileNameIndex = 1
            for (file in _openFiles) {
                if (file.name == givenName) {
                    newFileNameIndex++
                }
            }

            return if (newFileNameIndex != 1)
                givenName.plus(" ($newFileNameIndex)")
            else
                givenName
        }

        fun closeFileAt(index: Int) {
            _openFiles.removeAt(index)
        }

        fun closeFile(id: UUID) {
            _openFiles.removeIf { it.id == id }
        }

        fun getNumberOfOpenedFiles(): Int {
            return _openFiles.size
        }

        fun getOpenedFilesRef(): MutableList<FileDetails> {
            return _openFiles
        }

        fun getActiveFileData(): FileDetails? {
            if (activeFileIndex < 0 || activeFileIndex >= _openFiles.size) {
                return null
            }

            return _openFiles[activeFileIndex]
        }
    }
}
