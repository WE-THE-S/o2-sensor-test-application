package kr.thes.o2_test.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kr.thes.o2_test.MainActivity
import kr.thes.o2_test.utils.getSharedString
import no.nordicsemi.android.support.v18.scanner.*
import org.jetbrains.anko.intentFor
import org.json.JSONObject
import java.nio.ByteBuffer


data class O2Device(val isOk : Boolean){
    var temp : Float = 0f
    var o2 : Float = 0f
    var ppO2 : Short = 0
    var barometric : Short = 0
    lateinit var location : Location
    override fun toString(): String {
        val json = JSONObject().apply{
            put("isOk", isOk)
            put("temp", temp)
            put("o2", o2)
            put("ppO2", ppO2)
            put("barometric", barometric)
            if(::location.isInitialized) {
                put("location", location.toString())
            }
        }
        return json.toString()
    }
}

class BLEService : Service(), LocationListener {
    private lateinit var location : Location
    private val notifyId = 123
    private var count = 0
    private val TAG = "BLEService"
    private lateinit var scanner : BluetoothLeScannerCompat
    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")
        scanner = BluetoothLeScannerCompat.getScanner()
        val locationManager =
            baseContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, this)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        val setting = ScanSettings.Builder().apply {
            setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            setReportDelay(1)
            setLegacy(true)
            setUseHardwareFilteringIfSupported(true)
            setUseHardwareBatchingIfSupported(true)
        }.build()

        val address = baseContext.getSharedString("address")
        scanner.startScan(
            arrayListOf(
                ScanFilter.Builder().apply {
                    setDeviceAddress(address)
                }.build()
            ),
            setting,
            callback)
        super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT //서비스가 종료될시 재생성
    }


    private val callback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            if(results.size < 1){
                return
            }
            val it = results[0]
            val rawBytes = it.scanRecord!!.bytes!!
            val bytes = rawBytes.slice(IntRange(rawBytes.size - 14, rawBytes.size - 1)).reversed().toByteArray()
            val device = O2Device((bytes[0] != 0.toByte())).apply {
                temp = ByteBuffer.wrap(bytes.slice(IntRange(9, 13)).toByteArray()).float
                o2 = ByteBuffer.wrap(bytes.slice(IntRange(5, 9)).toByteArray()).float
                ppO2 = ByteBuffer.wrap(bytes.slice(IntRange(3, 5)).toByteArray()).short
                barometric = ByteBuffer.wrap(bytes.slice(IntRange(1, 3)).toByteArray()).short
            }

            Log.i("O2", device.toString())
            if(device.o2 < 19.5){
                if(::location.isInitialized){
                    device.location = location
                }
                count += 1
                val intent = intentFor<MainActivity>()
                val pendingIntent =
                    PendingIntent.getActivity(baseContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                if (Build.VERSION.SDK_INT >= 26) {
                    val channel = NotificationChannel(
                        packageName, "O2 Device",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                }
                val builder = NotificationCompat.Builder(baseContext, packageName).apply{
                    setContentTitle(count.toString())
                    setContentIntent(pendingIntent)
                    setSmallIcon(android.R.drawable.ic_dialog_alert)
                    setContentText(device.toString())
                    priority = NotificationCompat.PRIORITY_HIGH
                    setCategory(NotificationCompat.CATEGORY_ALARM)
                }
                val notify = builder.build().apply{
                    defaults = Notification.DEFAULT_SOUND

                    flags = Notification.FLAG_ONLY_ALERT_ONCE
                }
                val notification: Uri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    baseContext.getSharedString("phone"),

                    )

                startForeground(notifyId, notify)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        val locationManager =
            baseContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)

        Log.i(TAG, "onDestroy()")
        if(::scanner.isInitialized){
           scanner.stopScan(callback)
            val intent = intentFor<BLEService>()
            val pendingIntent =
                PendingIntent.getActivity(baseContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(
                    packageName, "O2 Device",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(baseContext, packageName).apply{
                setContentTitle("서비스 재시작")
                setSmallIcon(android.R.drawable.ic_dialog_alert)
                setContentIntent(pendingIntent)
                priority = NotificationCompat.PRIORITY_HIGH
                setCategory(NotificationCompat.CATEGORY_ERROR)
            }
            val notify = builder.build().apply{
                defaults = Notification.DEFAULT_SOUND
                flags = Notification.FLAG_ONLY_ALERT_ONCE
            }
            startForeground(notifyId, notify)
        }
    }

    override fun onLocationChanged(p0: Location?) {
        p0?.run {
            location = this
            Log.i("Location update", location.toString())
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }
}
