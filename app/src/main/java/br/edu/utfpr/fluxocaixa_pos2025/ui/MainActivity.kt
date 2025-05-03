package br.edu.utfpr.fluxocaixa_pos2025.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.TooltipCompat
import br.edu.utfpr.fluxocaixa_pos2025.R
import br.edu.utfpr.fluxocaixa_pos2025.database.DatabaseHandler
import br.edu.utfpr.fluxocaixa_pos2025.entity.Transaction
import br.edu.utfpr.fluxocaixa_pos2025.utils.MoneyTextWatcher
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerType: AppCompatSpinner
    private lateinit var spinnerDetail: AppCompatSpinner
    private lateinit var etData: EditText
    private lateinit var etValor: EditText
    private lateinit var database: DatabaseHandler
    private lateinit var btnSave: Button
    private lateinit var btnReleases: Button
    private lateinit var btnBalance: Button

    private val optionsSpinnerType = listOf("Crédito", "Débito")
    private val optionsSpinnerDetailCredit = listOf("Salário", "Extras")
    private val optionsSpinnerDetailDebit = listOf("Alimentação", "Transporte", "Saúde", "Moradia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.activity_main)

        initViews()
        setupTypeSpinner()
        setupDetailSpinner(optionsSpinnerType.first())
        setupEditTextDate()
        setButtonsListeners()
        setupTooltips()
    }

    private fun initViews() {
        spinnerType = findViewById(R.id.spinnerType)
        spinnerType.setDropDownVerticalOffset(145)

        spinnerDetail = findViewById(R.id.spinnerDetail)
        spinnerDetail.setDropDownVerticalOffset(145)
        etData = findViewById(R.id.etData)
        etValor = findViewById(R.id.etValor)
        etValor.addTextChangedListener(MoneyTextWatcher(etValor))
        database = DatabaseHandler(this)
        btnSave = findViewById(R.id.btnSave)
        btnReleases = findViewById(R.id.btnReleases)
        btnBalance = findViewById(R.id.btnBalance)
    }

    private fun setupTypeSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            optionsSpinnerType
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position) as String
                setupDetailSpinner(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDetailSpinner(selectedType: String) {
        val options = if (selectedType == "Crédito") {
            optionsSpinnerDetailCredit
        } else {
            optionsSpinnerDetailDebit
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            options
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDetail.adapter = adapter

        spinnerDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {}

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupEditTextDate() {
        etData.inputType = InputType.TYPE_NULL
        etData.keyListener = null

        etData.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(etData.windowToken, 0)
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                etData.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun setButtonsListeners() {
        btnSave.setOnClickListener { btnSaveOnClick() }
        btnReleases.setOnClickListener { btnReleasesOnClick() }
        btnBalance.setOnClickListener { btnBalanceOnClick() }
    }

    private fun btnSaveOnClick() {
        if (etValor.text.isBlank() || etData.text.isBlank()) {
            Toast.makeText(this, "Preencha valor e data", Toast.LENGTH_SHORT).show()
            return
        }

        val tipo = spinnerType.selectedItem as String
        val detalhe = spinnerDetail.selectedItem as String

        val rawValor = etValor.text.toString()
        val valor = rawValor
            .replace("R$", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0

        val dataParts = etData.text.toString().split("/")
        if (dataParts.size != 3) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, dataParts[0].toInt())
            set(Calendar.MONTH, dataParts[1].toInt() - 1)
            set(Calendar.YEAR, dataParts[2].toInt())
        }
        val timestamp = cal.timeInMillis

        val transaction = Transaction(
            _id = 0,
            type = tipo,
            detail = detalhe,
            value = valor,
            date = timestamp
        )
        database.incluir(transaction)
        clearFields()
        Toast.makeText(this, "Registro incluído!", Toast.LENGTH_SHORT).show()
    }

    private fun btnReleasesOnClick() {
        val intent = Intent(this, ReleasesActivity::class.java)
        startActivity(intent)
    }

    private fun btnBalanceOnClick() {
        val transacoes = database.listaTransacoes()

        var totalCreditos = 0.0
        var totalDebitos = 0.0

        for (t in transacoes) {
            if (t.type.equals("Crédito", ignoreCase = true)) {
                totalCreditos += t.value
            } else if (t.type.equals("Débito", ignoreCase = true)) {
                totalDebitos += t.value
            }
        }

        val saldoFinal = totalCreditos - totalDebitos
        val formatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(saldoFinal)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(formatado)
            .setTitle("Seu saldo atual é: ")

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun setupTooltips() {
        spinnerType.setOnLongClickListener {
            Toast.makeText(this, "Toque para escolher o tipo de transação", Toast.LENGTH_SHORT).show()
            true
        }

        spinnerDetail.setOnLongClickListener {
            Toast.makeText(this, "Toque para escolher qual o lançamento", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun clearFields() {
        etValor.setText("0")
        etData.setText("")
        spinnerType.requestFocus()
    }
}
