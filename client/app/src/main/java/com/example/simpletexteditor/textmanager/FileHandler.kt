package com.example.simpletexteditor.textmanager

import android.content.Context
import android.util.Log
import com.example.simpletexteditor.MainActivity
import com.example.simpletexteditor.utils.Event
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
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
            val activity = MainActivity.getActivity() ?: return false

            //for testing only
//            Path(context.filesDir.path.plus("/").plus(FILE_NAME)).deleteIfExists()
//            return true

            if (!Path(context.filesDir.path.plus("/").plus(FILE_NAME)).exists()) {
                try {
                    Path(context.filesDir.path.plus("/").plus(FILE_NAME)).createFile()
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    return false
                }
            }

            var fs: FileInputStream? = null
            var success: Boolean

            try {
                fs = context.openFileInput(FILE_NAME)

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

                activeFileIndex = activity.getPreferences(Context.MODE_PRIVATE).getInt("LAST_OPENED_FILE", 0)
                activeFileChangedEvent.invoke(activeFileIndex)

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
            val context = MainActivity.getContext() ?: return false
            val activity = MainActivity.getActivity() ?: return false

            var fs: FileOutputStream? = null
            var success: Boolean

            try {
                fs = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                val prefs = activity.getPreferences(Context.MODE_PRIVATE)
                with(prefs.edit()) {
                    putInt("LAST_OPENED_FILE", activeFileIndex)
                    apply()
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


        fun createFile(fileName: String, isStoredOnCloud: Boolean = false, givenId: UUID? = null) {
            val context = MainActivity.getContext() ?: return
            val fileDetails =
                FileDetails(
                    id = givenId ?: UUID.randomUUID(),
                    name = generateNewName(fileName),
                    memoryFile = MemoryFile(),
                    isStoredOnCloud = isStoredOnCloud
                )

            _openFiles.add(fileDetails)
            Path(context.filesDir.path.plus("/").plus(fileDetails.id.toString())).createFile()
        }

        fun deleteLocalFile(id: UUID) {
            val context = MainActivity.getContext() ?: return

            _openFiles.removeIf { it.id == id }
            Path(context.filesDir.path.plus("/").plus(id)).deleteIfExists()
        }

        fun modifyFileContents(id: UUID, diff: MemoryFile.TextDiff) {
            val context = MainActivity.getContext() ?: return

            if (!Path(context.filesDir.path.plus("/").plus(id)).exists()) {
                try {
                    Path(context.filesDir.path.plus("/").plus(id)).createFile()
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    return
                }
            }

            var fs: RandomAccessFile? = null

            try {
                fs = RandomAccessFile(File(context.filesDir.path.plus("/").plus(id)), "rw")

                if (diff.index > fs.length()) {
                    //it WILL execute the finally
                    return
                }

                val diffBytes = diff.textChange.toByteArray(Charsets.UTF_8)

                if (diff.isAdded) {
                    fs.seek(diff.index.toLong())

                    //if it's right at the end
                    if (diff.index.toLong() == fs.length()) {
                        fs.write(diffBytes)
                    } else {
                        val remainingBytes = ByteArray(fs.length().toInt() - diff.index)
                        fs.read(remainingBytes, 0, remainingBytes.size)

                        fs.seek(diff.index.toLong())
                        fs.write(diffBytes)
                        fs.write(remainingBytes)
                    }
                } else {
                    val offset = diff.index + diffBytes.size
                    fs.seek(offset.toLong())

                    val remainingBytes = ByteArray(fs.length().toInt() - offset)
                    fs.read(remainingBytes, 0, remainingBytes.size)

                    fs.seek((offset - diffBytes.size).toLong())
                    fs.setLength(fs.length() - diffBytes.size)
                    fs.write(remainingBytes)
                }
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
            } finally {
                fs?.close()
            }
        }

        fun resetFileID(details: FileDetails, newID: UUID) {
            val context = MainActivity.getContext() ?: return
            val oldID = details.id
            val absolutePath = context.filesDir.path.plus("/")

            if (!Path(absolutePath.plus(oldID)).exists()) {
                return
            }

            val success = File(absolutePath.plus(oldID)).renameTo(File(absolutePath.plus(newID)))
            if (!success) {
                return
            }

            details.id = newID
            saveToStorage()
        }

        fun fullyWriteFile(id: UUID, content: String) {
            val context = MainActivity.getContext() ?: return

            if (!Path(context.filesDir.path.plus("/").plus(id)).exists()) {
                try {
                    Path(context.filesDir.path.plus("/").plus(id)).createFile()
                } catch (e: Exception) {
                    Log.e("DBG", e.toString())
                    return
                }
            }

            var fs: FileOutputStream? = null

            try {
                fs = context.openFileOutput(id.toString(), Context.MODE_PRIVATE)
                fs.write(content.toByteArray(Charsets.UTF_8))
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
            } finally {
                fs?.close()
            }
        }

        fun getFileContent(id: UUID): String {
            val context = MainActivity.getContext() ?: return ""

            if (!Path(context.filesDir.path.plus("/").plus(id)).exists()) {
                return ""
            }

            var fs: FileInputStream? = null

            try {
                fs = context.openFileInput(id.toString())

                val bytes = fs.readBytes()
                fs?.close()
                return bytes.toString(Charsets.UTF_8)
            } catch (e: Exception) {
                Log.e("DBG", e.toString())
                fs?.close()
                return ""
            }
        }

        fun getNumberOfFiles(): Int {
            return _openFiles.size
        }

        fun getFileListRef(): MutableList<FileDetails> {
            return _openFiles
        }

        fun getActiveFileData(): FileDetails? {
            if (activeFileIndex < 0 || activeFileIndex >= _openFiles.size) {
                return null
            }

            return _openFiles[activeFileIndex]
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
    }
}
