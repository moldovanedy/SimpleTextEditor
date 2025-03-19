package com.example.simpletexteditor.textmanager

import java.util.Stack

class MemoryFile {
    var content: StringBuilder = StringBuilder("")
        private set

    /**
     * The history of edits. It's for UNDO.
     */
    private val _history: Stack<TextDiff> = Stack<TextDiff>()

    /**
     * The history of undo-s performed. It's for REDO.
     */
    private val _undoneHistory: Stack<TextDiff> = Stack<TextDiff>()

    constructor()

    constructor(text: String) {
        content.append(text)
    }


    fun pushChange(diff: TextDiff) {
        if (diff.index < 0) {
            throw Exception("Invalid index")
        }

        if (diff.isAdded) {
            content.insert(diff.index, diff.textChange)
        } else {
            if (diff.index + diff.textChange.length > content.length) {
                throw Exception("Invalid delete end index")
            }

            content.delete(diff.index, diff.index + diff.textChange.length)
        }

        _undoneHistory.clear()
        _history.push(diff)
    }

    fun undoChange(): Boolean {
        if (_history.empty()) {
            return false
        }

        val diff: TextDiff = _history.pop()

        if (diff.isAdded) {
            content.delete(diff.index, diff.index + diff.textChange.length)
        } else {
            content.insert(diff.index, diff.textChange)
        }

        _undoneHistory.push(diff)
        return true
    }

    fun redoChange(): Boolean {
        if (_undoneHistory.empty()) {
            return false
        }

        val diff: TextDiff = _undoneHistory.pop()

        if (diff.isAdded) {
            content.insert(diff.index, diff.textChange)
        } else {
            content.delete(diff.index, diff.index + diff.textChange.length)
        }

        _history.push(diff)
        return true
    }


    data class TextDiff(
        val textChange: String = "",
        val isAdded: Boolean = false,
        val index: Int = 0
    )
}

