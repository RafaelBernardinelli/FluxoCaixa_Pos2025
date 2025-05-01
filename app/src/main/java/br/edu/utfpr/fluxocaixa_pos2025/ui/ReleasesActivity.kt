package br.edu.utfpr.fluxocaixa_pos2025.ui

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import br.edu.utfpr.fluxocaixa_pos2025.R
import br.edu.utfpr.fluxocaixa_pos2025.adapter.ListTransactionAdapter
import br.edu.utfpr.fluxocaixa_pos2025.database.DatabaseHandler

class ReleasesActivity : AppCompatActivity() {
    lateinit var db: DatabaseHandler
    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_releases)

        db = DatabaseHandler(this)
        list = findViewById(R.id.listItems)
    }

    override fun onStart() {
        super.onStart()
        val transactions = db.listar()
        val adapter = ListTransactionAdapter( this, transactions )
        list.adapter = adapter
    }
}