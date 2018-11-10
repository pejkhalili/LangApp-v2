package com.chapdast.ventures.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.R
import com.chapdast.ventures.Configs.SERVER_ADDRESS
import com.chapdast.ventures.Configs.SPref
import com.chapdast.ventures.HelloApp
import kotlinx.android.synthetic.main.stat_no_connection.*
import java.util.*

/**
 * Created by pejman on 5/26/18.
 */
class NoConnection : ChapActivity() {

    var showDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.stat_no_connection)

            hub_no_connection.typeface = HelloApp.IRANSANS
            hub_try_again.typeface = HelloApp.IRANSANS
            showDialog = ShowCheckDialog(this)

            hub_try_again.setOnClickListener {
                showDialog!!.dismiss()
                showDialog = ShowCheckDialog(this)

            }

        }
    }

    fun LoadHub() {
        var splash = SPref(this, "userCreds")!!.getBoolean("FirstRun", false)
        var hub = Intent(this, Hub::class.java)
        if (splash) {
            hub = Intent(this, SplashPage::class.java)
        }
        startActivity(hub)
        finish()
    }

    fun ShowCheckDialog(context: Context): AlertDialog {
        val alertDialog = AlertDialog.Builder(context).create()
        val dialogView = layoutInflater.inflate(R.layout.net_check, null)
        val text = dialogView.findViewById<TextView>(R.id.CHX_Text)
        text.typeface = HelloApp.IRANSANS
        alertDialog.setView(dialogView)

        CheckInterNetConnection().execute()
        alertDialog.setOnDismissListener {
            CheckInterNetConnection().cancel(true)
        }
        alertDialog.show()
        return alertDialog

    }

    inner class CheckInterNetConnection : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            var run = true

            while (run) {
                try {
                    var khttp = khttp.get(SERVER_ADDRESS, timeout = 1.0)
                    if (khttp.statusCode == 200) {
                        showDialog!!.dismiss()
                        return "true"
                    }
                } catch (e: Exception) {
                    Log.d("ERR_NET", e.message)
                }
            }
            return "false"
        }

        override fun onPostExecute(result: String?) {
            LoadHub()
            super.onPostExecute(result)
        }
    }

    override fun onDestroy() {
        if (showDialog != null) {
            showDialog!!.dismiss()
        }
        super.onDestroy()
    }

    override fun onPause() {
        if (showDialog != null) {
            showDialog!!.dismiss()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        if (showDialog != null) {
            showDialog!!.dismiss()
        }
        super.onBackPressed()
    }
}