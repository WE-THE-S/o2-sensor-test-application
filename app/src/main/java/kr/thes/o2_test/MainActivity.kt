package kr.thes.o2_test

import android.R.id
import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.bluetooth.le.BluetoothLeScanner
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.thes.o2_test.receiver.BLEScanReceiver
import kr.thes.o2_test.service.BLEService
import no.nordicsemi.android.support.v18.scanner.*
import org.jetbrains.anko.intentFor
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    private lateinit var receiver : BLEScanReceiver
    private lateinit var scanner : BluetoothLeScannerCompat
    private val address =  "30:AE:A4:2C:8E:DA"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!isServiceRunningCheck()){
            startService(intentFor<BLEService>())
        }
    }

    private fun isServiceRunningCheck() : Boolean {
        val manager = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        val list = manager.getRunningServices(Int.MAX_VALUE)
        for(it in list){
            if ("BLEService" == it.service.className) {
                return true
            }
        }
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
