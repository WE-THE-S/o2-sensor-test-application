package kr.thes.o2_test.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_user_type.*
import kr.thes.o2_test.R
import kr.thes.o2_test.activity.admin.AdminMenuActivity
import kr.thes.o2_test.activity.user.SetUserInformationActivity
import kr.thes.o2_test.activity.user.UserStatusActivity
import kr.thes.o2_test.utils.getSharedString
import org.jetbrains.anko.intentFor

class SelectUserTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)
        type_admin.setOnClickListener {
            startActivity(intentFor<AdminMenuActivity>())
            this@SelectUserTypeActivity.finish()
        }
        type_user.setOnClickListener {
            if(getSharedString("device_address").isNotBlank()){
                startActivity(intentFor<UserStatusActivity>())
            }else{
                startActivity(intentFor<SetUserInformationActivity>())
            }
            this@SelectUserTypeActivity.finish()
        }
    }
}
