package com.chapdast.ventures.activities

import android.content.Intent
import android.os.Bundle
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.isNetworkAvailable
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_media_selection.*

class MediaSelection : ChapActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_media_selection)
            if (!isNetworkAvailable(applicationContext)) {
                var noCon = Intent(this, NoConnection::class.java)
                startActivity(noCon)
                finish()
            }
            ms_message.typeface = HelloApp.IRANSANS


            var intent = Intent(applicationContext, MediaLoader::class.java)
            ms_back.setOnClickListener {
                finish()
            }

            ms_pod_key.setOnClickListener {
                intent.putExtra("type", 2)
                startActivity(intent)
            }

            ms_vid_key.setOnClickListener {
                intent.putExtra("type", 1)
                startActivity(intent)
            }

            ms_all.setOnClickListener {
                intent.putExtra("type", 0)
                startActivity(intent)
            }

        }
    }
}
