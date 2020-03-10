package kr.thes.o2_test

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.IpSecManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kr.thes.o2_test.service.BLEService
import kr.thes.o2_test.utils.getSharedString
import org.jetbrains.anko.intentFor
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 1001
    @SuppressLint("InlinedApi")
    private val USE_PERMISSION = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermission()
    }

    private fun checkPermission(){
        var check = true
        for(it in USE_PERMISSION){
            if(ActivityCompat.checkSelfPermission(baseContext, it) != PackageManager.PERMISSION_GRANTED){
                Log.d("Permission", it)
                requestPermission(it)
                check = false
            }
        }

        if(check){
            val address = baseContext.getSharedString("address")
            if(address.isNullOrBlank()){
                startActivity(intentFor<SettingInformationActivity>())
            }else{
                startActivity(intentFor<MainActivity>())
            }
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
        checkPermission()
    }
}
