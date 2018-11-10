package com.chapdast.ventures.activities

import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_eula_.*
import java.util.*

class EULA_Activity : ChapActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_eula_)
            eula_back.setOnClickListener { finish() }
            eula_back.typeface = HelloApp.IRANSANS
            eula_text.textDirection = TextView.TEXT_DIRECTION_ANY_RTL
//            eula_text.typeface = HelloApp.IRANSANS

            eula_text.setBackgroundColor(0)


            if (intent != null && intent.extras != null) {
                var type = intent.extras.getString("type")
                if (type.equals("eula")) {
                    eula_text.loadUrl("file:///android_asset/html/eula.html")
                } else {
                    eula_text.loadUrl("file:///android_asset/html/about.html")
                }
            }

        }
    }
}
