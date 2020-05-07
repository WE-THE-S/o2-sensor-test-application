package kr.thes.o2_test.adapter

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kr.thes.o2_test.R

class BluetoothDeviceListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val text : TextView = itemView.findViewById(R.id.text)
    init{
        text.setOnClickListener {
            val adr = text.text.split("-").last()
        }
    }

}