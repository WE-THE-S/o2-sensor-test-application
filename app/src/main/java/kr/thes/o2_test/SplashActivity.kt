package kr.thes.o2_test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kr.thes.o2_test.service.BLEService
import org.jetbrains.anko.intentFor
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 1001
    private val RSS_JOB_ID = 1000
    private val USE_PERMISSION = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermission()
    }

    private fun checkPermission(){
        var check = true
        USE_PERMISSION.forEach {
            if(ActivityCompat.checkSelfPermission(baseContext, it) != PackageManager.PERMISSION_GRANTED){
                requestPermission(it)
                check = false
            }
        }
        if(check){
            startActivity(intentFor<MainActivity>())
            SplashActivity@this.finish()
        }
    }

    private fun requestPermission(name : String){
        ActivityCompat.requestPermissions(this, arrayOf(name), REQUEST_PERMISSION_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_PERMISSION_CODE -> {
                checkPermission()
            }
        }
    }
}
