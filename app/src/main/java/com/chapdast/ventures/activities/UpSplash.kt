package com.chapdast.ventures.activities


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import co.ronash.pushe.Pushe
import com.chapdast.ventures.ChapActivity
//import com.chapdast.ventures.Configs.FIREBASE_CLI
import com.chapdast.ventures.Configs.SERVER_ADDRESS
import com.chapdast.ventures.Configs.SPref
import com.chapdast.ventures.Handlers.Ana
import com.chapdast.ventures.R
//import com.google.firebase.analytics.FirebaseAnalytics



/**
 * Created by pejman on 6/1/18.
 */
class UpSplash : ChapActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ChapActivity.netCheck(this)) {

            setContentView(R.layout.activity_up_splash)

//            FIREBASE_CLI = FirebaseAnalytics.getInstance(applicationContext)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var ana = Ana(applicationContext)
            ana.install()

            Pushe.initialize(applicationContext, false)

            val userId = SPref(this, "userCreds")?.getString("userId", null)
            Log.d("USTT", "UserId: " + userId)

            var userCheck = UserCheck(userId.toString(), this).execute()

            Log.d("USTT", userCheck.get().toString())

            if (userId != null && userCheck.get().toString() == "0" ) {
                var hub = Intent(this, Hub::class.java)
                startActivity(hub)
                finish()
            } else {
                var intent = Intent(this, Welcome::class.java)
                startActivity(intent)
                finish()
            }

        }
    }


    class UserCheck(var inp: String = "-1", var context: Context) : AsyncTask<String, String, String>() {

        fun LaunchFirstRun(context: Context, run: Boolean = false): String {
            if (run) {
                SPref(context, "userCreds")!!.edit().putBoolean("FirstRun", true).apply()
                return "1"
            } else {
                SPref(context, "userCreds")!!.edit().putBoolean("FirstRun", false).apply()
                return "0"
            }
        }

        override fun doInBackground(vararg input: String?): String {

            var checkUser = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "checkUser", "phone" to inp))
            if (checkUser.statusCode == 200) {
                try {
                    val jes = checkUser.jsonObject
                    Log.d("uchx", jes.toString())
                    if (jes.getBoolean("result")) {
                        val name = if (jes.getString("status").equals("sub")) {
                            if (!jes.getString("name").equals("-")) {
                                jes.getString("name")
                            } else {
                                context.resources.getString(R.string.unSet)
                            }
                        } else {
                            context.resources.getString(R.string.unSet)
                        }
                        SPref(context, "userProfileCreds")!!.edit().putString("userName", name).apply()

                        return LaunchFirstRun(context, !jes.getString("status").equals("sub"))
                    } else {
                        return LaunchFirstRun(context, true)
                    }

                } catch (e: Exception) {
                    Log.d("Err", e.message)
                }
            } else {

                val intent = Intent(context, NoConnection::class.java)
                context.startActivity(intent)

            }
            return 1.toString()
        }

    }

}