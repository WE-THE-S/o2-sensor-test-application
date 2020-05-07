package kr.thes.o2_test.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.thes.o2_test.R
import kr.thes.o2_test.protocol.IRequestConnectDevice

class BluetoothDeviceListHolder(itemView: View, handler : IRequestConnectDevice) : RecyclerView.ViewHolder(itemView) {
    val text : TextView = itemView.findViewById(R.id.text)
    init{
        text.setOnClickListener {
            val adr = text.text.split("-").last()
            handler.onRequestConnect(adr)
        }
    }

}