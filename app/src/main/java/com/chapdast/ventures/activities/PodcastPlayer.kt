package com.chapdast.ventures.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import kotlinx.android.synthetic.main.activity_podcast_player.*
import java.util.*

class PodcastPlayer : ChapActivity(), MediaPlayer.OnPreparedListener {

    private lateinit var name: String
    private lateinit var link: String
    private lateinit var thumb: String
    private lateinit var filename: String
    private lateinit var pg: ProgressDialog
    private var pauseTime = 0
    var playing = false
    var mediaPlayer: MediaPlayer? = null
    var timer: CountDownTimer? = null
    var progViewerTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_podcast_player)
            pg = ProgressDialog(this)
            pg.setOnCancelListener {
                if (mediaPlayer != null) mediaPlayer!!.stop()
                finish()
            }
            pg.setMessage(resources.getString(R.string.pleaseWait))
            pg.show()
            ppBackBtn.setOnClickListener {

                //            if(mediaPlayer != null){
                mediaPlayer!!.stop()
                mediaPlayer = null
//            }
//            if(timer != null){
                timer!!.cancel()
                timer = null
//            }
                finish()
            }

            if (intent.extras != null && pg.isShowing) {
                name = intent.extras.getString("NAME")
                link = intent.extras.getString("LINK")
                thumb = intent.extras.getString("THUMB")
                filename = intent.extras.getString("FILENAME")
                Log.d("mk", "$name,$link,$thumb")
                ppMedicLevel.typeface = HelloApp.BEBAS_FONT
                ppMediaName.typeface = HelloApp.BEBAS_FONT

                LoadTumb(ppThumb, applicationContext).execute(MEDIA_SERVER_ADDRESS, thumb, filename)
                ppMediaName.text = name
                var level = SRV_LEVEL_DETECTOR(SPref(applicationContext, "level")!!.getInt("level", 1))
                ppMedicLevel.text = level

                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(MEDIA_SERVER_ADDRESS + link)
                mediaPlayer!!.prepareAsync()
                mediaPlayer!!.setOnPreparedListener(this)


            }

            ppPlayBtn.setOnClickListener {
                if (playing) {
                    mediaPlayer!!.pause()
                    ppPlayBtn.setImageResource(R.mipmap.pod_play)
                    if (timer != null) {
                        timer!!.cancel()
                        timer = null
                    }

                    pauseTime = 0
                    pauseTime = (mediaPlayer!!.duration - mediaPlayer!!.currentPosition) / 1000
                    playing = false

                } else if (!playing) {
                    if (mediaPlayer != null) {
                        ppPlayBtn.setImageResource(R.mipmap.pod_pause)
                        mediaPlayer!!.start()
                        timer = CountDowner(ppElapsedTime, pauseTime)
                        timer!!.start()
                        playing = true
                    }
                }
            }
            ppForward.setOnClickListener {
                if (mediaPlayer != null) {
                    val time = (mediaPlayer!!.duration - mediaPlayer!!.currentPosition - 30000) / 1000
                    if (time > 0) {
                        timer!!.cancel()
                        timer = null
                        mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition + 30000)
                        timer = CountDowner(ppElapsedTime, time).start()
                        ppProgress.progress = mediaPlayer!!.currentPosition
                    }
                }
            }
            ppBackward.setOnClickListener {
                if (mediaPlayer != null) {
                    val time = (mediaPlayer!!.duration - mediaPlayer!!.currentPosition + 30000) / 1000
                    if (time > 0) {
                        timer!!.cancel()
                        timer = null
                        mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition - 30000)
                        timer = CountDowner(ppElapsedTime, time).start()
                        ppProgress.progress = mediaPlayer!!.currentPosition
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        if (pg.isShowing) {
            pg.dismiss()
            pg.cancel()
        }

        if (mediaPlayer != null) {
            mediaPlayer!!.stop()

        }
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (mediaPlayer != null) mediaPlayer!!.stop()

        super.onDestroy()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        pg.dismiss()
//        pg.cancel()
        mediaPlayer!!.start()
        var audioTime = mediaPlayer!!.duration / 1000
        var h = audioTime / (60 * 60)
        var m = (audioTime - (h * 60 * 60)) / 60
        var s = (audioTime - (h * 60 * 60) - (m * 60))
        var time = if (h != 0) {
            if (h < 10) "0$h:" else "$h:"
        } else {
            ""
        }
        time += if (m < 10) "0$m:" else "$m:"
        time += if (s < 10) "0$s" else "$s"

        ppFullTime.text = time

        ppProgress.progress = 0
        ppProgress.max = mediaPlayer!!.duration
        progViewerTimer = object : CountDownTimer(mediaPlayer!!.duration.toLong(), 250) {
            override fun onFinish() {
                ppProgress.progress = 0
            }

            override fun onTick(p0: Long) {
                ppProgress.progress += 250
            }

        }.start()
        timer = CountDowner(ppElapsedTime, audioTime)
        timer!!.start()

        playing = true


    }
}
