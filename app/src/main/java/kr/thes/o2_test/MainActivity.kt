package kr.thes.o2_test

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import kr.thes.o2_test.service.BLEService
import kr.thes.o2_test.utils.clearSharedString
import org.jetbrains.anko.intentFor
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private val address =  "30:AE:A4:2C:8E:DA"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "Start")
        setContentView(R.layout.activity_main)
        if(!isServiceRunningCheck()){
            startService(intentFor<BLEService>())
        }
    }

    private fun isServiceRunningCheck() : Boolean {
        val manager = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        val list = manager.getRunningServices(Int.MAX_VALUE)
        for(it in list){
            if (BLEService::javaClass.javaClass.name == it.service.className) {
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

    override fun onDestroy() {
        super.onDestroy()
    }
}
