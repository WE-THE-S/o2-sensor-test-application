package kr.thes.o2.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_set_user_device.*
import kr.thes.o2.R
import kr.thes.o2.recycler.BluetoothDeviceListAdapter
import kr.thes.o2.protocol.IRequestConnectDevice
import kr.thes.o2.utils.setSharedString
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import org.jetbrains.anko.intentFor

class ConnectDeviceActivity : AppCompatActivity(),
    IRequestConnectDevice {
    private lateinit var scanner : BluetoothLeScannerCompat
    private lateinit var adapter : BluetoothDeviceListAdapter
    private val callback = object : no.nordicsemi.android.support.v18.scanner.ScanCallback() {
        override fun onBatchScanResults(results: MutableList<no.nordicsemi.android.support.v18.scanner.ScanResult>) {
            super.onBatchScanResults(results)
            if (results.size < 1) {
                return
            }
            results.forEach {
                if(it.device.address.isNotEmpty()){
                    if(::adapter.isInitialized) {
                        Log.i("BLE Scan", it.device.address)
                        adapter.add("${it.device.name}-${it.device.address}")
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_device)
        adapter = BluetoothDeviceListAdapter(this@ConnectDeviceActivity)
        device_list.layoutManager = LinearLayoutManager(this@ConnectDeviceActivity)
        device_list.adapter = adapter

        scanner = BluetoothLeScannerCompat.getScanner()
        val setting = ScanSettings.Builder().apply {
            setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            setReportDelay(1)
            setLegacy(true)
            setUseHardwareFilteringIfSupported(true)
            setUseHardwareBatchingIfSupported(true)
        }.build()
        scanner.startScan(
            arrayListOf(
                ScanFilter.Builder().build()
            ),
            setting,
            callback)
    }

    override fun onPause() {
        super.onPause()
        if(::scanner.isInitialized) {
            scanner.stopScan(callback)
        }
    }

    override fun onRequestConnect(address: String) {
        baseContext.setSharedString("device_address", address)
        startActivity(intentFor<UserStatusActivity>())
        this@ConnectDeviceActivity.finish()
    }
}