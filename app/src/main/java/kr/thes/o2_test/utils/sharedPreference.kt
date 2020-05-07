package kr.thes.o2_test.utils

import android.content.Context

val name = "O2"
fun Context.getSharedString(key : String) : String{
    val prefer = getSharedPreferences(name, Context.MODE_PRIVATE)
    return prefer.getString(key, "")!!
}

fun Context.setSharedString(key : String, value : String){
    val prefer = getSharedPreferences(name, Context.MODE_PRIVATE)
    prefer.edit().apply{
        putString(key, value)
        apply()
    }
}

fun Context.rmSharedString(key : String){
    val prefer = getSharedPreferences(name, Context.MODE_PRIVATE)
    prefer.edit().apply{
        remove(key)
        apply()
    }
}


fun Context.clearSharedString(){
    val prefer = getSharedPreferences(name, Context.MODE_PRIVATE)
    prefer.edit().apply{
        clear()
        apply()
    }
}