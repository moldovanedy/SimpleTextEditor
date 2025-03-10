package com.example.simpletexteditor.cloudmanager

import io.ktor.client.HttpClient

class BaseClient {
    companion object {
        val client = HttpClient()

        const val domain = "http://10.0.2.2:5251"
    }
}