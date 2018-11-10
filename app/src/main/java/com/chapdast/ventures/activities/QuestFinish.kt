package com.chapdast.ventures.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.R
import com.chapdast.ventures.Configs.SPref
import com.chapdast.ventures.HelloApp

import kotlinx.android.synthetic.main.activity_quest_finish.*
import java.util.*

class QuestFinish : ChapActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_quest_finish)

            //add date and time to keep user out of quest
            fq_text.typeface = HelloApp.IRANSANS_BLACK
            fq_main_menu.typeface = HelloApp.IRANSANS
            fq_reviews.typeface = HelloApp.IRANSANS
            SPref(applicationContext, "quest_stat")!!.edit().clear().apply()
            if (intent.extras != null) {
                if (intent.extras.getBoolean("hide_RWS")) fq_reviews.visibility = View.INVISIBLE
            }
            fq_main_menu.setOnClickListener {
                var intent = Intent(this, Hub::class.java)
                startActivity(intent)
                finish()
            }
            fq_reviews.setOnClickListener {
                var intent = Intent(this, Review::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

}
