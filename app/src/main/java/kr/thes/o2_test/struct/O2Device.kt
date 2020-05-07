package kr.thes.o2_test.struct

import android.location.Location
import org.json.JSONObject
import kotlin.experimental.and


data class O2Device(val isOk : Byte){
    var temp : Float = 0f
    var o2 : Float = 0f
    var ppO2 : Short = 0
    var barometric : Short = 0
    lateinit var location : Location

    fun sensorOk() : Boolean {
        return (isOk and 0x1) != 0.toByte()
    }

    fun requestSos() : Boolean {
        return (isOk and 0x2) != 0.toByte()
    }

    fun warringO2() : Boolean {
        return (isOk and 0x4) != 0.toByte()
    }

    override fun toString(): String {
        val json = JSONObject().apply{
            put("isOk", isOk)
            put("temp", temp)
            put("o2", o2)
            put("ppO2", ppO2)
            put("barometric", barometric)
            if(::location.isInitialized) {
                put("lat", location.latitude)
                put("lng", location.longitude)
            }
        }
        return json.toString()
    }
}