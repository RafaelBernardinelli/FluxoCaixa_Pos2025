package br.edu.utfpr.fluxocaixa_pos2025.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import br.edu.utfpr.fluxocaixa_pos2025.R
import br.edu.utfpr.fluxocaixa_pos2025.database.DatabaseHandler
import br.edu.utfpr.fluxocaixa_pos2025.entity.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListTransactionAdapter(var context : Context, var cursor : Cursor) : BaseAdapter() {
    override fun getCount(): Int {
       return cursor.count
    }

    override fun getItem(pos: Int): Any {
        cursor.moveToPosition( pos )
        val transaction = Transaction(
            cursor.getInt( DatabaseHandler.ID ).toInt(),
            cursor.getString( DatabaseHandler.TYPE ).toString(),
            cursor.getString( DatabaseHandler.DETAIL ).toString(),
            cursor.getDouble( DatabaseHandler.VALUE ).toDouble(),
            cursor.getLong( DatabaseHandler.DATE ).toLong(),
        )
        return transaction
    }

    override fun getItemId(pos: Int): Long {
        cursor.moveToPosition( pos )
        return cursor.getInt( DatabaseHandler.ID ).toLong()
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater =
            context.getSystemService( Context.LAYOUT_INFLATER_SERVICE )
                    as LayoutInflater

        val listElement = inflater.inflate( R.layout.element_list, null )

        val tvType = listElement.findViewById<TextView>( R.id.tvType )
        val tvDetailType = listElement.findViewById<TextView>( R.id.tvDetailType )
        val tvCost = listElement.findViewById<TextView>(R.id.tvCost)
        val tvDate = listElement.findViewById<TextView>(R.id.tvDate)

        cursor.moveToPosition( pos )

        tvType.text = cursor.getString( DatabaseHandler.TYPE )
        tvDetailType.text = cursor.getString( DatabaseHandler.DETAIL )
        val value = cursor.getDouble(DatabaseHandler.VALUE)
        val valueFormatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)

        tvCost.text = valueFormatted
        val timestamp = cursor.getLong(DatabaseHandler.DATE)
        val dateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        tvDate.text = dateFormatted

        tvCost.setTextColor(
            if (tvType.text == "Débito") Color.RED else Color.parseColor("#388E3C") // verde escuro
        )


        return listElement
    }
}