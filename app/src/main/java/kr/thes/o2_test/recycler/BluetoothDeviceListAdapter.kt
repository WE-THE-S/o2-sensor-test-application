package kr.thes.o2_test.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.thes.o2_test.R
import kr.thes.o2_test.protocol.IRequestConnectDevice

class BluetoothDeviceListAdapter(private val handler : IRequestConnectDevice) : RecyclerView.Adapter<BluetoothDeviceListHolder>() {
    private val array : MutableList<String> = mutableListOf()
    override fun getItemCount(): Int {
        return array.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceListHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_holder, parent, false)
        return BluetoothDeviceListHolder(view, handler)
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
