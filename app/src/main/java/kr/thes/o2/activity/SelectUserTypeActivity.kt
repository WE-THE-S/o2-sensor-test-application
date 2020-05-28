package kr.thes.o2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_user_type.*
import kr.thes.o2.R
import kr.thes.o2.activity.admin.AdminMenuActivity
import kr.thes.o2.activity.user.SetUserInformationActivity
import kr.thes.o2.activity.user.UserStatusActivity
import kr.thes.o2.utils.getSharedString
import org.jetbrains.anko.intentFor

class SelectUserTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)
        type_admin.setOnClickListener {
            startActivity(intentFor<AdminMenuActivity>())
        }
        type_user.setOnClickListener {
            if(getSharedString("device_address").isNotBlank()){
                startActivity(intentFor<UserStatusActivity>())
            }else{
                startActivity(intentFor<SetUserInformationActivity>())
            }
        }
    }
}
