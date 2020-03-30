package kr.thes.o2_test

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import kr.thes.o2_test.service.BLEService
import kr.thes.o2_test.utils.clearSharedString
import org.jetbrains.anko.intentFor
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            text.text = intent?.getStringExtra("data")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "Start")
        setContentView(R.layout.activity_main)
        if(!isServiceRunning(BLEService::javaClass.javaClass)){
            startService(intentFor<BLEService>())
        }
    }
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            Log.i("Service", service.service.className)
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.remove_data -> {
                baseContext.clearSharedString()
                val packageManager: PackageManager = applicationContext.packageManager
                val intent =
                    packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                val componentName = intent!!.component.toString()
                val mainIntent: Intent = IntentCompat.makeMainSelectorActivity(componentName, IntentCompat.CATEGORY_LEANBACK_LAUNCHER)
                applicationContext.startActivity(mainIntent)
                exitProcess(0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(baseContext).registerReceiver(receiver, IntentFilter("o2-device"))
    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(receiver)
    }

}
