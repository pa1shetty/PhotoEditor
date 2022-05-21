package com.example.photoeditor.utils

import android.annotation.SuppressLint
import java.text.CharacterIterator
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*

object Common {
    @SuppressLint("SimpleDateFormat")
    fun convertDate(timeInMilliseconds: String): String {
        val foo = timeInMilliseconds.toLong()
        val date = Date(foo)
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return (formatter.format(date))
    }

    @SuppressLint("DefaultLocale")
    fun humanReadableByteCountSI(bytes: Long): String {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }
}