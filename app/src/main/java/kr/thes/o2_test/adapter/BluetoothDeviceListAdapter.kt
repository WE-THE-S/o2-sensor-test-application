package kr.thes.o2_test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.thes.o2_test.R

class BluetoothDeviceListAdapter(val address : TextView) : RecyclerView.Adapter<BluetoothDeviceListHolder>() {
    private val array : MutableList<String> = mutableListOf()
    override fun getItemCount(): Int {
        return array.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceListHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_holder, parent, false)
        return BluetoothDeviceListHolder(view, address)
    }


    override fun onBindViewHolder(holder: BluetoothDeviceListHolder, position: Int) {
        val str = array[position]
        holder.text.text = str
    }

    fun add(str : String){
        if(array.indexOf(str) < 0){
            array.add(str)
            notifyDataSetChanged()
        }
    }
}
