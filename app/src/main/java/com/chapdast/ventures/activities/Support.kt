package com.chapdast.ventures.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.SPref
import com.chapdast.ventures.Configs.SUPP_EMAIL
import com.chapdast.ventures.Configs.SUPP_TELL
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R

import kotlinx.android.synthetic.main.activity_support.*

class Support : ChapActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (netCheck(this)) {
            setContentView(R.layout.activity_support)

            var userId = SPref(HelloApp.context,"userCreds")!!.getString("userId","")
            supp_back.typeface = HelloApp.IRANSANS

            supp_tell.setOnClickListener {
                var call = Intent(Intent.ACTION_DIAL)
                call.data = Uri.parse("tel:$SUPP_TELL")
                startActivity(call)
            }
            supp_email.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPP_EMAIL))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supp_email_subject)+userId)
                startActivity(Intent.createChooser(intent, getString(R.string.email_send_title)))
            }
            supp_back.setOnClickListener {
                finish()
            }
        }
    }

}
