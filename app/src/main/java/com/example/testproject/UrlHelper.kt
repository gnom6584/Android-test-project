package com.example.testproject

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.system.exitProcess

private class UrlRunnableReader(val url: String) : Runnable {

    var output = String()
    var fail = false

    override fun run() {
        try {
            val connection = URL(url).openConnection()

            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val inputStream = BufferedReader(
                InputStreamReader(
                    connection.getInputStream()
                )
            )
            output = inputStream.readLines().joinToString { it }
            inputStream.close()
        }
        catch(ex: Exception) {
            fail = true
        }
    }
}

fun getUrlRawData(url: String) : String?{
    val temp = UrlRunnableReader(url)
    Thread(temp).apply {
        start()
        join()
    }
    return if(!temp.fail)
        temp.output
    else
        null
}