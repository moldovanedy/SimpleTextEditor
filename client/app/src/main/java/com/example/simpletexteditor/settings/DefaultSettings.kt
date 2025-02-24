package com.example.simpletexteditor.settings

class DefaultSettings private constructor() {
    companion object {
        const val UNDO_MEMORY_MIN_SIZE = 1
        const val UNDO_MEMORY_MAX_SIZE = 1024
        const val UNDO_MEMORY_DEFAULT_SIZE = 50
    }
}
