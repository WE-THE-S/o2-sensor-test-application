package kr.thes.o2.activity.user

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
import kotlinx.android.synthetic.main.activity_user_status.*
import kr.thes.o2.R
import kr.thes.o2.SplashActivity
import kr.thes.o2.activity.SelectUserTypeActivity
import kr.thes.o2.service.BLEService
import kr.thes.o2.utils.clearSharedString
import org.jetbrains.anko.intentFor
import org.json.JSONObject
import kotlin.system.exitProcess


class UserStatusActivity : AppCompatActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("data")?.let{
                val json = JSONObject(it)
                status_text.text = json.getInt("isOk").toString()
                ppo2_text.text = json.getInt("ppO2").toString()
                barometric_text.text = json.getInt("barometric").toString()
                temp_text.text = json.getDouble("temp").toString()
                o2_text.text = json.getDouble("o2").toString()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "Start")
        setContentView(R.layout.activity_user_status)
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
                startActivity(intentFor<SplashActivity>())
                this@UserStatusActivity.finish()
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
        stopService(intentFor<BLEService>())
    }

}
