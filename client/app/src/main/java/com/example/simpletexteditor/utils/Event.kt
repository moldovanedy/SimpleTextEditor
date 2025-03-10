package com.example.simpletexteditor.utils

class Event<T> {
    private val observers = mutableSetOf<(T) -> Unit>()

    operator fun plusAssign(observer: (T) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    fun invoke(value: T) {
        for (observer in observers)
            observer(value)
    }
}