package com.example.simpletexteditor.textmanager

import java.util.Stack

class MemoryFile {
    var endOfLine: EndOfLineType = EndOfLineType.LF
        private set
    var isUtf8Encoding: Boolean = true
        private set
    var hasBom: Boolean = false
        private set

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


    fun pushChange(diff: TextDiff) {
        if (diff.specialOperation != null) {
            when (diff.specialOperation.opType) {
                SpecialOperationType.EolChange -> endOfLine = diff.specialOperation.oldEol ?: EndOfLineType.LF
                SpecialOperationType.EncodingChange -> isUtf8Encoding = diff.specialOperation.wasOldEncodingUtf8 ?: true
                SpecialOperationType.BomChange -> hasBom = diff.specialOperation.wasBomEnabled ?: false
            }

            if (diff.specialOperation.fullSnapshot != null) {
                content = StringBuilder(diff.specialOperation.fullSnapshot)
            }

            _undoneHistory.clear()
            _history.push(diff)
            return
        }

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
        if (diff.specialOperation != null) {
            when (diff.specialOperation.opType) {
                SpecialOperationType.EolChange -> endOfLine = diff.specialOperation.oldEol ?: EndOfLineType.LF
                SpecialOperationType.EncodingChange -> isUtf8Encoding = diff.specialOperation.wasOldEncodingUtf8 ?: true
                SpecialOperationType.BomChange -> hasBom = diff.specialOperation.wasBomEnabled ?: false
            }

            if (diff.specialOperation.fullSnapshot != null) {
                content = StringBuilder(diff.specialOperation.fullSnapshot)
            }

            _undoneHistory.push(diff)
            return true
        }

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
        if (diff.specialOperation != null) {
            when (diff.specialOperation.opType) {
                SpecialOperationType.EolChange -> endOfLine = diff.specialOperation.oldEol ?: EndOfLineType.LF
                SpecialOperationType.EncodingChange -> isUtf8Encoding = diff.specialOperation.wasOldEncodingUtf8 ?: true
                SpecialOperationType.BomChange -> hasBom = diff.specialOperation.wasBomEnabled ?: false
            }

            if (diff.specialOperation.fullSnapshot != null) {
                content = StringBuilder(diff.specialOperation.fullSnapshot)
            }

            _history.push(diff)
            return true
        }

        if (diff.isAdded) {
            content.insert(diff.index, diff.textChange)
        } else {
            content.delete(diff.index, diff.index + diff.textChange.length)
        }

        _history.push(diff)
        return true
    }

    fun changeEolAndNormalize(newEolType: EndOfLineType) {
        val newContent: StringBuilder = StringBuilder("")
        newContent.ensureCapacity(content.length)

        val newEol: String = when (newEolType) {
            EndOfLineType.LF -> "\n"
            EndOfLineType.CR -> "\r"
            EndOfLineType.CRLF -> "\r\n"
        }

        var wasPossiblyCrlf = false

        for (c in content) {
            if (c == '\n' || c == '\r') {
                //handle CRLF
                if (wasPossiblyCrlf) {
                    wasPossiblyCrlf = false

                    //if the previous was CR and this is LF, don't add another line
                    if (c == '\n') {
                        continue
                    }
                }

                if (c == '\r') {
                    wasPossiblyCrlf = true
                }

                newContent.append(newEol)
                continue
            }

            newContent.append(c)
        }

        pushChange(
            TextDiff(
                specialOperation =
                SpecialOperationDiff(
                    opType = SpecialOperationType.EolChange,
                    oldEol = endOfLine,
                    fullSnapshot = content.toString()
                )
            )
        )

        endOfLine = newEolType
        content = newContent
    }

    /**
     * Returns whether changing the encoding to ASCII will result in loss of data or not. O(n) operation.
     */
    fun isEncodingLossy(): Boolean {
        for (c in content) {
            if (c.code >= 128) {
                return true
            }
        }

        return false
    }

    fun switchEncoding(shouldSetToUtf8: Boolean) {
        if (shouldSetToUtf8 == isUtf8Encoding) {
            return
        }

        if (!isUtf8Encoding && shouldSetToUtf8) {
            isUtf8Encoding = true

            pushChange(
                TextDiff(
                    specialOperation =
                    SpecialOperationDiff(
                        opType = SpecialOperationType.EncodingChange,
                        wasOldEncodingUtf8 = false
                    )
                )
            )
            return
        }

        var isLossy = false
        val newContent: StringBuilder = StringBuilder("")
        newContent.ensureCapacity(content.length)

        for (c in content) {
            if (c.code >= 128) {
                newContent.append("?")
                isLossy = true
            } else {
                newContent.append(c)
            }
        }


        pushChange(
            TextDiff(
                specialOperation = SpecialOperationDiff(
                    opType = SpecialOperationType.EncodingChange,
                    wasOldEncodingUtf8 = true,
                    fullSnapshot = if (isLossy) null else content.toString()
                )
            )
        )

        isUtf8Encoding = false
        content = newContent
    }

    fun toggleBom(isBomEnabled: Boolean) {
        if (hasBom == isBomEnabled) {
            return
        }

        pushChange(
            TextDiff(
                specialOperation = SpecialOperationDiff(
                    opType = SpecialOperationType.EncodingChange,
                    wasBomEnabled = hasBom,
                )
            )
        )

        hasBom = isBomEnabled
    }


    data class TextDiff(
        val specialOperation: SpecialOperationDiff? = null,
        val textChange: String = "",
        val isAdded: Boolean = false,
        val index: Int = 0
    )

    data class SpecialOperationDiff(
        val opType: SpecialOperationType,
        val oldEol: EndOfLineType? = null,
        val wasOldEncodingUtf8: Boolean? = null,
        val wasBomEnabled: Boolean? = null,
        val fullSnapshot: String? = null
    )

    enum class EndOfLineType {
        LF,
        CR,
        CRLF
    }

    enum class SpecialOperationType {
        EolChange,
        EncodingChange,
        BomChange
    }
}

