package com.polyarniq.calkgp.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatUtils {

    private val symbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    private val formatInt = DecimalFormat("#,##0", symbols)

    fun formatCurrencyInt(amount: Double): String {
        return "${formatInt.format(amount)} ₽"
    }
}
