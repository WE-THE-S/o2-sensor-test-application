package kr.thes.o2_test.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kr.thes.o2_test.activity.user.UserStatusActivity
import kr.thes.o2_test.struct.O2Device
import kr.thes.o2_test.utils.getSharedString
import no.nordicsemi.android.support.v18.scanner.*
import org.jetbrains.anko.intentFor
import java.nio.ByteBuffer
import java.util.*


class BLEService : Service(), LocationListener {
    private lateinit var location : Location
    private val notifyId = 123
    private var count = 0
    private val TAG = "BLEService"
    private lateinit var scanner : BluetoothLeScannerCompat
    private var lastTime : Long = 0L

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
            setReportDelay(500)
            setLegacy(false)
            setUseHardwareCallbackTypesIfSupported(true)
            setUseHardwareFilteringIfSupported(true)
            setUseHardwareBatchingIfSupported(true)
        }.build()
        val address = baseContext.getSharedString("address")
        Log.i("Address", address)
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
        @SuppressLint("MissingPermission", "HardwareIds")
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            if(results.size < 1){
                return
            }

            val now = Calendar.getInstance().timeInMillis
            Log.i("Time", "${now - lastTime}")
            val it = results[0]
            val rawBytes = it.scanRecord!!.bytes!!
            val bytes = rawBytes.slice(IntRange(rawBytes.size - 14, rawBytes.size - 1)).reversed().toByteArray()
            val device = O2Device(bytes[0]).apply {
                temp = ByteBuffer.wrap(bytes.slice(IntRange(9, 13)).toByteArray()).float
                o2 = ByteBuffer.wrap(bytes.slice(IntRange(5, 9)).toByteArray()).float
                ppO2 = ByteBuffer.wrap(bytes.slice(IntRange(3, 5)).toByteArray()).short
                barometric = ByteBuffer.wrap(bytes.slice(IntRange(1, 3)).toByteArray()).short
            }

            if(::location.isInitialized) {
                device.location = location
            }
            Log.i("O2", device.toString())
            if(device.warringO2() or device.requestSos()){
                count += 1

                val intent = intentFor<UserStatusActivity>()
                val pendingIntent =
                    PendingIntent.getActivity(baseContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                if (Build.VERSION.SDK_INT >= 26) {
                    val channel = NotificationChannel(
                        packageName, "O2 Device",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                }
                val alarmSound =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                val builder = NotificationCompat.Builder(baseContext, packageName).apply{
                    setContentTitle(count.toString())
                    setContentIntent(pendingIntent)
                    setSmallIcon(android.R.drawable.ic_dialog_alert)
                    setContentText(device.toString())
                    setLights(Color.RED, 500, 500)
                    val pattern = arrayOf<Long>(500, 500,500,500,500,500,500,500,500).toLongArray()
                    setVibrate(pattern)
                    setStyle(NotificationCompat.InboxStyle())
                    setSound(alarmSound)
                    priority = NotificationCompat.PRIORITY_HIGH
                    setCategory(NotificationCompat.CATEGORY_ERROR)
                    setDefaults(Notification.DEFAULT_ALL)
                }
                val notify = builder.build().apply{
                    flags = Notification.FLAG_ONGOING_EVENT
                }
                val tMgr =
                    baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                //val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                //val r = RingtoneManager.getRingtone(applicationContext, notification)
                //r.play()
                if(now - lastTime > 1000 * 60 * 5){
                    //1초 * 60 * 1
                    lastTime = now
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(
                        baseContext.getSharedString("phone"),
                        tMgr.line1Number,
                        if(::location.isInitialized) {
                            "Lat : ${location.latitude},Lng : ${location.longitude}, O2 : ${device.o2}, Help!"
                        }else{
                            "O2 : ${device.o2}, Help!"
                        },
                        null,
                        null
                    )
                    Toast.makeText(applicationContext, "Send Message", Toast.LENGTH_SHORT).show()
                }
                startForeground(notifyId, notify)
            }

            val broadcastIntent = Intent("o2-device")
            broadcastIntent.putExtra("data", device.toString())
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(broadcastIntent)
        }
    }

    private fun getDMS(point : Double): String{
        return "${kotlin.math.floor(point)}°${kotlin.math.floor(point * 60 % 60)}'${point * 3600 % 60}"
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
            location = p0
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
