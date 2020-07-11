package br.com.mmetzner.vendor.utils

import java.util.*

object CustomDate {
    var currentDate: Calendar = Calendar.getInstance()

    fun getCurrentDateFormated() : String {
        return formatDateToString(currentDate)
    }

    fun formatDateToString(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        return "$day/${month + 1}/$year"
    }
}