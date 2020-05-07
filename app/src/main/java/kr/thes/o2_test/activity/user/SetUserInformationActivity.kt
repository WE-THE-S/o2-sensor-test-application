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

class SetUserInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_information)
        ok_button.setOnClickListener {
            if(user_blood_type.text.toString().isNotBlank() and user_name.text.toString().isNotBlank() and
                user_phone_number.text.toString().isNotBlank() and admin_name.text.toString().isNotBlank() and
                admin_phone_number.text.toString().isNotBlank()){
                val list = arrayListOf(
                    Pair("user_name", user_name.text.toString()),
                    Pair("user_blood_type", user_blood_type.text.toString()),
                    Pair("user_phone_number", user_phone_number.text.toString()),
                    Pair("admin_name", admin_name.text.toString()),
                    Pair("admin_phone_number", admin_phone_number.text.toString()),
                    Pair("user_type", "user")
                )
                list.forEach {
                    baseContext.setSharedString(it.first, it.second)
                }
                startActivity(intentFor<MainActivity>())
                this@SetUserInformationActivity.finish()
            }else{
                toast(getString(R.string.require_fill_all_input))
            }
        }
    }

}