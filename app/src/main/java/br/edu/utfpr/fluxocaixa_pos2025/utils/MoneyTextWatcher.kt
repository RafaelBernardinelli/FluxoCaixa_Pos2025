package br.edu.utfpr.fluxocaixa_pos2025.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class MoneyTextWatcher(private val editText: EditText) : TextWatcher {

    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != current) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString()
                .replace("[R$,.\\s]".toRegex(), "")

            val parsed = cleanString.toDoubleOrNull() ?: 0.0
            val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)

            current = formatted
            editText.setText(formatted)
            editText.setSelection(formatted.length)

            editText.addTextChangedListener(this)
        }
    }
}