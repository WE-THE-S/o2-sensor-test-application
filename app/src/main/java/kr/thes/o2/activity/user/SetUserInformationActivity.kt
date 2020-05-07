package kr.thes.o2.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_set_user_information.*
import kr.thes.o2.R
import kr.thes.o2.utils.setSharedString
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class SetUserInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_information)
        ok_button.setOnClickListener {
            if(user_blood_type.editText!!.text.toString().isNotBlank() and user_name.editText!!.text.toString().isNotBlank() and
                user_phone_number.editText!!.text.toString().isNotBlank() and admin_name.editText!!.text.toString().isNotBlank() and
                admin_phone_number.editText!!.text.toString().isNotBlank()){
                val list = arrayListOf(
                    Pair("user_name", user_name.editText!!.text.toString()),
                    Pair("user_blood_type", user_blood_type.editText!!.text.toString()),
                    Pair("user_phone_number", user_phone_number.editText!!.text.toString()),
                    Pair("admin_name", admin_name.editText!!.text.toString()),
                    Pair("admin_phone_number", admin_phone_number.editText!!.text.toString()),
                    Pair("user_type", "user")
                )
                list.forEach {
                    baseContext.setSharedString(it.first, it.second)
                }
                startActivity(intentFor<ConnectDeviceActivity>())
                this@SetUserInformationActivity.finish()
            }else{
                toast(getString(R.string.require_fill_all_input))
            }
        }
    }

}