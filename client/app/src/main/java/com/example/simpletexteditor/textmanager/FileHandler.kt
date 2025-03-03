package com.example.simpletexteditor.textmanager

class FileHandler {
    companion object {
        var activeFileIndex: Int = -1
            set(value) {
                if (value < 0 || value >= _openFiles.size) {
                    throw Exception()
                }

                field = value
            }

        private val _openFiles: MutableList<FileDetails> = mutableListOf()

        fun createFile(fileName: String, shouldSaveOnCloud: Boolean) {
            _openFiles.add(
                FileDetails(
                    name = generateNewName(fileName),
                    memoryFile = MemoryFile()
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
