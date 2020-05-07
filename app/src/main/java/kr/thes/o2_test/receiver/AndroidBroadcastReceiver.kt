package kr.thes.o2_test.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.thes.o2_test.service.BLEService

class AndroidBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let{
            //val service = Intent(it, BLEService::class.java)
            //it.startService(service)
        }
    }
}
