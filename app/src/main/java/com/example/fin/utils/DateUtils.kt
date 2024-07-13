package com.example.fin.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getDateTime(milliseconds: String): String {
            return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(milliseconds.toLong())).toString()
        }

    }
}