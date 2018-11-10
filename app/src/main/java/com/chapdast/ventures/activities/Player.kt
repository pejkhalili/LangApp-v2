package com.chapdast.ventures.activities

import android.content.Intent
import android.media.MediaFormat
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.MEDIA_SERVER_ADDRESS
import com.chapdast.ventures.R
import com.chapdast.ventures.Configs.isNetworkAvailable
import com.chapdast.ventures.Configs.sToast
import kotlinx.android.synthetic.main.activity_player.*
import java.io.InputStream

class Player : ChapActivity() {

    var name="Name"
    var link="link"
    var thumb="thumb"
    var level="UNSET"
    var sub = "sub"
    var play = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        //check Internet Connection

        if (ChapActivity.netCheck(this)) {
            if (intent.extras!=null) {

                name = intent.extras.getString("NAME")
                link = intent.extras.getString("LINK")
                thumb = intent.extras.getString("THUMB")
//                level = intent.extras.getString("LEVEL")
                //srt
                sub = intent.extras.getString("SUB")
                Log.d("mk", "$name,$link,$thumb,$level")

            }


            var mc:android.widget.MediaController
            plr_video.setVideoPath(MEDIA_SERVER_ADDRESS + link)
            plr_video.setOnCompletionListener { finish() }



            mc = android.widget.MediaController(this)
            mc.setAnchorView(plr_video)

            plr_video.setMediaController(mc)
            //sub
            if(sub!="no") {
                plr_video.addSubtitleSource(
                        GetSubTitle().execute(sub).get(),
                        MediaFormat.createSubtitleFormat("Text", "fa-IR")
                )
            }else{
//                sToast(applicationContext, "NO_SUB")
            }

            plr_video.setOnPreparedListener { mp->

                mp.isLooping=true
                Log.d("mk","Duration"+plr_video.duration)
                plr_progress.visibility = View.GONE



            }
            plr_video.requestFocus()
            plr_video.start()
            Log.d("mk","AfterStart")


        }
    }

    inner class GetSubTitle:AsyncTask<String,Any,InputStream>(){
        override fun doInBackground(vararg p0: String?): InputStream {
            var url = p0[0]
            return java.net.URL(url).openStream()
        }

    }

}


