@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.chapdast.ventures.Handlers

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.chapdast.ventures.Configs.ANA_SERVER
//import com.chapdast.ventures.Configs.FIREBASE_CLI
import com.chapdast.ventures.Configs.SPref
//import com.google.firebase.analytics.FirebaseAnalytics

//import ir.mono.monolyticsdk.Monolyitcs


@Suppress("DEPRECATION")
@SuppressLint("Registered")
/**
 * Created by pejman on 5/27/18.
 */
class Ana(context: Context) : Activity() {
    val con = context

    var pm = con.packageManager.getPackageInfo(con.applicationInfo.packageName.toString(), 0)
    var ver: String = "${pm.versionName}/${pm.versionCode}"
    var tm = con.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        con.checkSelfPermission("android.permission.READ_PHONE_STATE")
    } else {
        PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission", "HardwareIds")
    var imei = if (perm == PackageManager.PERMISSION_GRANTED) tm.deviceId else "not_allowed"


    fun sub(phone:String) {
        var userId: String = phone
        val SUB = SPref(con, "ana")!!.getBoolean("sub", false)
        if (!SUB) {
            SendApiRequest(mapOf<String, String>("event" to "sub", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
            SPref(con, "ana")!!.edit().putBoolean("sub", true).apply()
        }
    }

    fun unSub(): Boolean {
        var userId: String = SPref(con.applicationContext, "userCreds")!!.getString("userId", "first_run")
        return SendApiRequest(mapOf<String, String>("event" to "unsub", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun splash(pos:Int) {
        var userId: String = SPref(con.applicationContext, "userCreds")!!.getString("userId", "first_run")
        val SPLASH = SPref(con, "ana")!!.getBoolean("splash$pos", false)
        if (!SPLASH) {
            SendApiRequest(mapOf<String, String>("event" to "splash$pos", "imei" to imei.toString(), "ver" to ver))
            SPref(con, "ana")!!.edit().putBoolean("splash$pos", true).apply()
        }
    }

    fun install() {
        var userId: String = "first_run"
        val INSTALLED = SPref(con, "ana")!!.getBoolean("install", false)
        if (!INSTALLED) {
            SendApiRequest(mapOf<String, String>("event" to "install", "imei" to imei.toString(), "ver" to ver))
            SPref(con, "ana")!!.edit().putBoolean("install", true).apply()
        }
    }

    fun reLog(phone: String) {
        var userId = phone
            SendApiRequest(mapOf<String, String>("event" to "relogin", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
            SPref(con, "ana")!!.edit().putBoolean("relog", true).apply()
    }

    fun recievedCode(phone: String) {
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "recievedCode", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun requestCode(phone: String) {
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "requestCode", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }
    fun InAppSubRequest(phone: String){
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "inAppReq", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun wrongNumber(phone: String) {
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "wrongNumber", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun mciFail(phone: String) {
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "mciFail", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun NotSupported(phone: String) {
        var userId = phone
        SendApiRequest(mapOf<String, String>("event" to "NotSupported", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }
    fun loginPage() {
        SendApiRequest(mapOf<String, String>("event" to "LoginPage", "imei" to imei.toString(), "ver" to ver,"phone" to "first_run"))
    }


    fun SendApiRequest(data: Map<String, String>): Boolean {
        var userId: String = SPref(con.applicationContext, "userCreds")!!.getString("userId", "first_run")
        var sendApiRequest = SendApiRequestAsync(data,userId)
        sendApiRequest.execute()
        return sendApiRequest.get()
    }



    inner class SendApiRequestAsync(data:Map<String,String> ,userId:String):AsyncTask<String,Int,Boolean>(){
        var data= data
        var userId=userId
        override fun doInBackground(vararg params: String?): Boolean {
            var event: String? = data.get("event")

//            if (FIREBASE_CLI != null) {
//                var bundle: Bundle = Bundle()
//                bundle.putString("userId", userId)
//                bundle.putString("version", ver)
//                bundle.putString("deviceId", imei)
//                bundle.putString("event", event)
//                FIREBASE_CLI!!.logEvent(event!!, bundle)
//                Log.i("FRIEBASE","$event on FB Called!")
//            }

            var sendReq = khttp.post(ANA_SERVER, data = data)
            if (sendReq.statusCode == 200) {

                try {
                    var jObj = sendReq.jsonObject
                    Log.e("ANA", "$event >> $imei >> $ver >> $userId >> Res: $jObj >> Data: $data")
                    return true
                } catch (e: Exception) {
                    Log.e("ANA", "$event >> $imei >> $ver >> $userId >> Res: ${e.message} >> Data: $data")
                }
            }
            return false
        }

    }

}