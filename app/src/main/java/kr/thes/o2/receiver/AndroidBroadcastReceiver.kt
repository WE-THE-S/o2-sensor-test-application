package kr.thes.o2.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AndroidBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let{
            //val service = Intent(it, BLEService::class.java)
            //it.startService(service)
        }
    }
}
