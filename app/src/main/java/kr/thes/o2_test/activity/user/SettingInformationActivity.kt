package kr.thes.o2_test.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_set_user_information.*
import kr.thes.o2_test.R
import kr.thes.o2_test.adapter.BluetoothDeviceListAdapter
import kr.thes.o2_test.utils.setSharedString
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class SettingInformationActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_set_user_information)
        adapter = BluetoothDeviceListAdapter(address)
        ok_button.setOnClickListener {
            if(phone_number.text.toString().isNotBlank() and name.text.toString().isNotBlank()
                and address.text.toString().isNotBlank()){
                val list = arrayListOf(
                    Pair("address", address.text.toString()),
                    Pair("phone", phone_number.text.toString()),
                    Pair("name", name.text.toString())
                )
                list.forEach {
                    baseContext.setSharedString(it.first, it.second)
                }
                startActivity(intentFor<MainActivity>())
                SettingInformationActivity@this.finish()
            }else{
                toast("안됌")
            }
        }
        device_list.layoutManager = LinearLayoutManager(SettingInformationActivity@this)
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
}