package com.polyarniq.calkgp.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatUtils {

    private val format = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    })

    fun formatCurrency(amount: Double): String {
        return "${format.format(amount)} ₽"
    }

    fun formatCurrencyInt(amount: Double): String {
        return "${DecimalFormat("#,##0", DecimalFormatSymbols(Locale("ru", "RU")).apply {
            groupingSeparator = ' '
        }).format(amount)} ₽"
    }
}
