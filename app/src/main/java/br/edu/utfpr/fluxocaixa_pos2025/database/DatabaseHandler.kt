package br.edu.utfpr.fluxocaixa_pos2025.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.edu.utfpr.fluxocaixa_pos2025.entity.Transaction

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME ( " +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, " +  // Crédito ou Débito
                    "detail TEXT, " +// Salário, Alimentação...
                    "value REAL, " + // valor numérico
                    "date INTEGER " + // timestamp em ms
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insere nova transação
    fun incluir(tx: Transaction): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("type", tx.type)
            put("detail", tx.detail)
            put("value", tx.value)
            put("date", tx.date)
        }
        return db.insert(TABLE_NAME, null, cv)
    }

    fun limparTransacoes() {
        val db = this.writableDatabase
        db.delete("transactions", null, null)
        db.close()
    }

    fun listaTransacoes(): List<Transaction> {
        val lista = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "date DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val transacao = Transaction(
                    _id = cursor.getInt(ID),
                    type = cursor.getString(TYPE),
                    detail = cursor.getString(DETAIL),
                    value = cursor.getDouble(VALUE),
                    date = cursor.getLong(DATE)
                )
                lista.add(transacao)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }

    // Retorna todos os registros
    fun listar(): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "date DESC" // ordena por data mais recente primeiro
        )
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "fluxocaixa.db"
        private const val TABLE_NAME = "transactions"
        public const val ID = 0
        public const val TYPE = 1
        public const val DETAIL = 2
        public const val VALUE = 3
        public const val DATE = 4
    }
}