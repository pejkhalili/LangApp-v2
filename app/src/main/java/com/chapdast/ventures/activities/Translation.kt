package com.chapdast.ventures.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import com.chapdast.ventures.Adapters.TranslationAdapter
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.Objects.TranslationObject
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_translation.*

class Translation : ChapActivity() {
    lateinit var translationRecyclerView: RecyclerView
    lateinit var translationAdapter: TranslationAdapter
    lateinit var translationList: ArrayList<TranslationObject>
    lateinit var viewManager: LinearLayoutManager
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation)
        ENV.current_context = applicationContext
        ENV.current_activity = this
        if (!isNetworkAvailable(this)) {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }
        trns_en.typeface = HelloApp.IRANSANS
        trns_en_to_fa.typeface = HelloApp.IRANSANS
        trns_fa_to_en.typeface = HelloApp.IRANSANS
        trns_title.typeface = HelloApp.IRANSANS
        trns_input.typeface = HelloApp.IRANSANS

        trns_title.visibility = View.INVISIBLE

        handler.postDelayed({
            var selection = SPref(applicationContext, "translation")!!.getInt("selected", 0)
            when (selection) {
                0 -> {
                    trns_lang_select.check(R.id.trns_en)
                    sToast(this, resources.getString(R.string.english), true)
                }
                1 -> {
                    trns_lang_select.check(R.id.trns_en_to_fa)
                    sToast(this, resources.getString(R.string.en_to_fa), true)
                }
                2 -> {
                    trns_lang_select.check(R.id.trns_fa_to_en)
                    sToast(this, resources.getString(R.string.fa_to_en), true)
                }
            }
        }, 200)

        trns_en.setOnClickListener {
            sToast(this, resources.getString(R.string.english), true)
            SPref(applicationContext, "translation")!!.edit().putInt("selected", 0).apply()
        }

        trns_en_to_fa.setOnClickListener {
            sToast(this, resources.getString(R.string.en_to_fa), true)
            SPref(applicationContext, "translation")!!.edit().putInt("selected", 1).apply()
        }

        trns_fa_to_en.setOnClickListener {
            sToast(this, resources.getString(R.string.fa_to_en), true)
            SPref(applicationContext, "translation")!!.edit().putInt("selected", 2).apply()
        }


        trns_backbtn.setOnClickListener {
            finish()
        }

        viewManager = LinearLayoutManager(applicationContext)
        translationList = ArrayList()
        translationRecyclerView = findViewById(R.id.trns_trans_sec)

        translationAdapter = TranslationAdapter(translationList, applicationContext)

        translationRecyclerView.setHasFixedSize(true)
        translationRecyclerView.layoutManager = viewManager
        translationRecyclerView.adapter = translationAdapter
        translationAdapter.notifyDataSetChanged()


        trns_trans_btn.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->

            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                sToast(applicationContext, "keycode $keyCode", true)
                loadTrans()
                return@OnKeyListener true
            }
            false
        })
        var current = 0
        trns_trans_btn.setOnClickListener {
            loadTrans()
            translationAdapter.notifyDataSetChanged()
            translationRecyclerView.scrollToPosition(translationList.size - 1)


        }


    }

    fun loadTrans() {
        val pg = ProgressDialog(this)
        var input = trns_input.text.toString().toLowerCase().replace(" ", "")
        if (input.isNotEmpty()) {
            pg.setMessage(resources.getString(R.string.pleaseWait))
            pg.show()
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var direction = if (trns_fa_to_en.isChecked) "fa-en" else if (trns_en_to_fa.isChecked) "en-fa" else "en-en"

            if (direction != "en-en") {
                var trans = khttp.post(TRANS_SERVER_ADDRESS, data = mapOf<String, Any>("key" to TRANS_KEY, "text" to input, "lang" to direction, "format" to "plain"))
                if (trans.statusCode == 200) {
                    var tr = trans.jsonObject
                    translationList.add(TranslationObject(input, tr))
                }
            } else {

                var nwTrans = khttp.get(DESC_TRANS + input)
                if (nwTrans.statusCode == 200) {
                    var res = nwTrans.jsonArray
                    translationList.add(
                            TranslationObject("" + input, res)
                    )
                }
            }
            trns_input.setText("")


        } else {
            sToast(applicationContext, applicationContext.resources.getString(R.string.translateInputError))
        }

        if (pg.isShowing) {
            pg.dismiss()
            pg.cancel()
        }

    }


}
