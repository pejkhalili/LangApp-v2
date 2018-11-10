package com.chapdast.ventures.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.billingclient.util.IabHelper
import com.android.billingclient.util.IabResult
import com.android.billingclient.util.Purchase
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_hub.*
import kotlinx.android.synthetic.main.app_bar_activity_home.*
import net.jhoobin.jhub.CharkhoneSdkApp
import org.json.JSONObject


class Hub : ChapActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    lateinit var mHelper: IabHelper
    var payloadJoob = ""
    var HANDLER = Handler()
    //joobin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 9009) {
            Log.d("$PAY_TAG-onActRes", "onActivityResult(" + requestCode + "," + resultCode + "," + data)
            if (mHelper == null) return
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
            } else {
                Log.d("$PAY_TAG-OnActRes", "onActivityResult handled by IABUtil.")
            }
        }
    }

    inner class PayDown : IabHelper.OnIabPurchaseFinishedListener {
        override fun onIabPurchaseFinished(result: IabResult?, purchase: Purchase?) {
            Log.d("$PAY_TAG", "Purchase finished: " + result + ",\npurchase: " + purchase.toString())
            SPref(applicationContext, "purchase")!!.edit().putString("purchase", purchase.toString()).apply()
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                Log.d("$PAY_TAG", "mHelper is Null")
                return
            }

            if (result!!.isFailure) {
                Log.i("$PAY_TAG", "Error purchasing: " + result)
                return
            }
/*
            var ana = Ana(applicationContext)
            ana.sub()

            var userId = SPref(applicationContext, "userCreds")!!.getString("userId", null)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)
            if(userId !=null) Log.d("Err1", userId.toString())

            var resp = khttp.post(SERVER_ADDRESS,data = mapOf("m" to "register","phone" to userId))
            if(resp.statusCode==200) {

                try {
                    var jes = resp.jsonObject
                    if (jes.getBoolean("result")) {
                        //send to responseble service provider

                        sp3.visibility = View.GONE
                        sp5.visibility = View.VISIBLE
                        pageDots.visibility = View.INVISIBLE
                        //send Request To register
                        //Load next Page
                        intenter(applicationContext,1,false)
                        Log.d("$PAY_TAG-OnFine","PURCHASE COMP!")
                    } else if (!jes.getBoolean("result")) {
                        Log.d("Err1", jes.toString())
                    }
                } catch (e: Exception) {
                    Log.d("Err1", e.message)
                }
            }else{
                Log.d("Err1",resp.statusCode.toString())
            }
*/
            Log.d("$PAY_TAG", "Purchase successful.")
        }

    }

    fun launchPay() {

        try {
            mHelper.launchSubscriptionPurchaseFlow(
                    this,
                    SUK_KEY,
                    9010,
                    PayDown(),
                    payloadJoob)
        } catch (e: Exception) {
            Log.e("$PAY_TAG", "Error-->> " + e.message)
        }
    }

    inner class PayIabListener : IabHelper.OnIabSetupFinishedListener {
        override fun onIabSetupFinished(result: IabResult?) {
            Log.d("$PAY_TAG-iab", result!!.message)
            if (!result.isSuccess) return
            if (mHelper == null) return
//            Let's get an inventory of stuff we own.
            Log.d("$PAY_TAG", "Setup successful. Querying inventory.")
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener(mHelper))

            } catch (e: IabHelper.IabAsyncInProgressException) {
                Log.e("$PAY_TAG", "Error querying inventory. Another async operation inprogress. ${e.message}")
            }

        }

        fun mGotInventoryListener(helper: IabHelper): IabHelper.QueryInventoryFinishedListener {
            return IabHelper.QueryInventoryFinishedListener { result, inventory ->
                Log.d("$PAY_TAG", "Query inventory finished.")
                // Have we been disposed of in the meantime? If so, quit.
                if (helper == null) return@QueryInventoryFinishedListener
                // Is it a failure?
                if (result.isFailure) {
                    Log.e("$PAY_TAG", "Failed to query inventory: $result")
                    return@QueryInventoryFinishedListener
                }
                Log.d("$PAY_TAG", "Query inventory was successful.")
//                var skuDetails = inventory.getSkuDetails(SUK_KEY)
//                var purchase = inventory.getPurchase(SUK_KEY)
                var skus = inventory.allOwnedSkus
                var hasPurchased = inventory.hasPurchase(SUK_KEY)
                Log.i(PAY_TAG, skus.toString() + hasPurchased.toString())
                if (!skus.contains(SUK_KEY) || hasPurchased != true) {
                    launchPay()
                }


//                Log.d("$PAY_TAG-inv", inventory.allPurchases.toString())
//                Log.d("$PAY_TAG-inv2", inventory.allOwnedSkus.toString())
                Log.d("$PAY_TAG", "Initial inventory query finished; enabling main UI.")
            }
        }

    }

    private fun getSecrets(): Array<String> {
        return resources.getStringArray(R.array.secrets)
    }
    //joobin


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    var first_time: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_home)


            CharkhoneSdkApp.initSdk(applicationContext, getSecrets(), true, R.mipmap.icon)
            mHelper = IabHelper(applicationContext, RSA_KEY)
            mHelper.startSetup(PayIabListener())

            hub_api.setOnClickListener {
                sToast(applicationContext, getString(R.string.timeRemainToNextQuest), true)
            }


            setSupportActionBar(toolbar)
            supportActionBar!!.hide()
            val toggle = ActionBarDrawerToggle(
                    this, drawer_layout, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()

            nav_view.setNavigationItemSelectedListener(this)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val userId = SPref(this, "userCreds")?.getString("userId", null)
            Log.d("MK-User", "UserID -->>>" + userId.toString())
            var level = SPref(this, "level")?.getInt("level", 0)

            UpdateNavDrawer(userId)
            CanQuest(this, hub_api, false)

            hub_side_menu.setOnClickListener {
                drawer_layout.openDrawer(Gravity.START)
                Log.d("toggle", 1.toString())
            }
            hub_current_level.typeface = HelloApp.IRANSANS
            hub_current_level.text = LevelSelector(applicationContext, level!!)
            hub_start.setOnClickListener(this)
            hub_level_select.setOnClickListener(this)
            hub_level_setting.setOnClickListener(this)
            hub_online_dic.setOnClickListener(this)
            hub_status.setOnClickListener(this)
            hub_videos.setOnClickListener(this)
            hub_wrongs.setOnClickListener(this)
            HUB_Level_Setting.typeface = HelloApp.IRANSANS_BLACK
            HUB_Media.typeface = HelloApp.IRANSANS_BLACK
            HUB_OnlineDic.typeface = HelloApp.IRANSANS_BLACK
            HUB_lvlSet.typeface = HelloApp.IRANSANS_BLACK
            HUB_userStat.typeface = HelloApp.IRANSANS_BLACK
            HUB_wrongAns.typeface = HelloApp.IRANSANS_BLACK

        }
    }

    fun UpdateNavDrawer(userId: String?) {
        val nv = findViewById<NavigationView>(R.id.nav_view)
        val hv = nv.getHeaderView(0)
//        var mnu = nv.menu
        val utv = hv.findViewById<TextView>(R.id.drw_userId)
//        var logo = hv.findViewById<ImageView>(R.id.drw_logo)
        val about = hv.findViewById<TextView>(R.id.drHead_about)
        val eula = hv.findViewById<TextView>(R.id.drHead_eula)
        val support = hv.findViewById<TextView>(R.id.drHead_support)

        utv.typeface = HelloApp.IRANSANS_BLACK
        about.typeface = HelloApp.IRANSANS
        eula.typeface = HelloApp.IRANSANS
        utv.typeface = HelloApp.IRANSANS
        support.typeface = HelloApp.IRANSANS

        about.setOnClickListener {
            var terms = Intent(applicationContext, EULA_Activity::class.java)
            terms.putExtra("type", "terms")
            startActivity(terms)
        }
        eula.setOnClickListener {
            var eula = Intent(applicationContext, EULA_Activity::class.java)
            eula.putExtra("type", "eula")
            startActivity(eula)
        }
        utv.setOnClickListener {
            var intent = Intent(applicationContext, Profile::class.java)
            startActivity(intent)
        }

        support.setOnClickListener {
            var intent = Intent(applicationContext, Support::class.java)
            startActivity(intent)
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.hub_start -> {
                if (CanQuest(this, hub_api)) {
                    var loadQuest = Intent(this, Quest::class.java)
                    startActivity(loadQuest)
                }
            }

            R.id.hub_level_select -> {
                LevelSetting(this)
            }

            R.id.hub_level_setting -> {
                QuestSettings(this)
            }

            R.id.hub_online_dic -> {
                var intent = Intent(this, Translation::class.java)
                startActivity(intent)
            }

            R.id.hub_status -> {
                UserStatus()
            }

            R.id.hub_videos -> {
                var intent = Intent(this, MediaCategories::class.java)
                startActivity(intent)
            }

            R.id.hub_wrongs -> {
                var intent = Intent(this, Review::class.java)
                startActivity(intent)
            }

        }
    }

    fun LevelSetting(context: Context) {


        var currentLevel = SPref(context, "level")!!.getInt("level", 1)


        val wordDesc = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.select_level, null)
        var level: Int = 0

        val elm = dialogView.findViewById<View>(R.id.sl_elm) as RadioButton
        val int = dialogView.findViewById<View>(R.id.sl_inter) as RadioButton
        val adv = dialogView.findViewById<View>(R.id.sl_adv) as RadioButton
        val fof = dialogView.findViewById<View>(R.id.sl_504) as RadioButton
        val oneOone = dialogView.findViewById<View>(R.id.sl_tofel) as RadioButton
        val okBtn = dialogView.findViewById<View>(R.id.sl_change_btn) as Button
        val title = dialogView.findViewById<TextView>(R.id.sl_title)

        title.typeface = HelloApp.IRANSANS
        elm.typeface = HelloApp.IRANSANS
        int.typeface = HelloApp.IRANSANS
        adv.typeface = HelloApp.IRANSANS
        fof.typeface = HelloApp.IRANSANS
        oneOone.typeface = HelloApp.IRANSANS
        okBtn.typeface = HelloApp.IRANSANS

        fun Update(lvl: Int) {

            var strLevel = SRV_LEVEL_DETECTOR(lvl)
            var userId = SPref(applicationContext, "userCreds")!!.getString("userId", "0")
            var setLevel = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "setLevel", "phone" to userId, "level" to strLevel))

            if (userId != 0.toString() && setLevel.statusCode == 200) {
                try {
                    var res: JSONObject = setLevel.jsonObject
                    if (res.getBoolean("result")) {
                        SPref(this, "level")?.edit()?.putInt("level", lvl)?.apply()
                        SPref(applicationContext, "runQuest$lvl")!!.edit().clear().apply()
                        hub_current_level.text = LevelSelector(applicationContext, lvl)
                    } else {
//                        sToast(applicationContext, "ERROR:Result" + res.getBoolean("result"))
                        Log.d("SL_RES", res.toString())
                    }
                } catch (e: Exception) {
                    Log.d("setLevel", e.message)
                }

            } else {
//                sToast(applicationContext,"Stat:"+setLevel.statusCode)
                Log.d("SRV_ERR", setLevel.statusCode.toString())
            }
        }

        when (currentLevel) {
            1 -> elm.isChecked = true
            2 -> int.isChecked = true
            3 -> adv.isChecked = true
            4 -> fof.isChecked = true
            5 -> oneOone.isChecked = true
        }

        elm.setOnClickListener {
            level = 1

        }

        int.setOnClickListener {
            level = 2
        }

        adv.setOnClickListener {
            level = 3
        }

        fof.setOnClickListener {
            level = 4
        }

        oneOone.setOnClickListener {
            level = 5
        }

        var sectime = false
        okBtn.setOnClickListener {
            if (level == 0 && sectime) {
                wordDesc.cancel()
            }
            if (level != 0) {
                Update(level)
                wordDesc.cancel()
            } else {
                sToast(applicationContext, resources.getString(R.string.select_your_level))
                sectime = true
            }


        }
        wordDesc.setView(dialogView)
        wordDesc.show()

    }

    fun UserStatus() {


        val UserStatDialog = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.user_status, null)
        val lay_elm = dialogView.findViewById<LinearLayout>(R.id.us_lay_elm)
        val lay_inter = dialogView.findViewById<LinearLayout>(R.id.us_lay_inter)
        val lay_adv = dialogView.findViewById<LinearLayout>(R.id.us_lay_adv)
        val lay_504 = dialogView.findViewById<LinearLayout>(R.id.us_lay_504)
        val lay_tofel = dialogView.findViewById<LinearLayout>(R.id.us_lay_tofel)
        val lay_no_stat = dialogView.findViewById<LinearLayout>(R.id.us_no_stat)

        val noStatBtn = dialogView.findViewById<Button>(R.id.us_no_stat_btn)

        val elm = dialogView.findViewById<TextView>(R.id.us_elm)
        val inter = dialogView.findViewById<TextView>(R.id.us_inter)
        val adv = dialogView.findViewById<TextView>(R.id.us_adv)
        val fof = dialogView.findViewById<TextView>(R.id.us_504)
        val tofel = dialogView.findViewById<TextView>(R.id.us_tofel)
        val noStat = dialogView.findViewById<TextView>(R.id.us_no_stat_text)

        var userStatus = khttp.post(SERVER_ADDRESS, data = mapOf<String, String>("m" to "statChecker", "phone" to SPref(this, "userCreds")!!.getString("userId", 0.toString())))
        if (userStatus.statusCode == 200) {
            var jes = userStatus.jsonObject
            Log.d("USTAT", jes.toString())
            if (jes.getBoolean("result")) {
                var levels = jes.getJSONObject("level")
                Log.d("USTAT", levels.toString())
                var lv_elm = levels.getJSONObject("elementry")
                var lv_int = levels.getJSONObject("intermediate")
                var lv_adv = levels.getJSONObject("advance")
                var lv_504 = levels.getJSONObject("504")
                var lv_tof = levels.getJSONObject("tofel")

                var elm_tru: Float = lv_elm.getInt("trueans").toFloat()
                var elm_fls: Float = lv_elm.getInt("wrongans").toFloat()

                var int_tru: Float = lv_int.getInt("trueans").toFloat()
                var int_fls: Float = lv_int.getInt("wrongans").toFloat()

                var adv_tru: Float = lv_adv.getInt("trueans").toFloat()
                var adv_fls: Float = lv_adv.getInt("wrongans").toFloat()

                var fof_tru: Float = lv_504.getInt("trueans").toFloat()
                var fof_fls: Float = lv_504.getInt("wrongans").toFloat()

                var tof_tru: Float = lv_tof.getInt("trueans").toFloat()
                var tof_fls: Float = lv_tof.getInt("wrongans").toFloat()


                if (elm_tru != 0f && elm_fls != 0f) {
                    elm.text = Math.floor((elm_tru * 100 / (elm_tru + elm_fls)).toDouble()).toString() + "%"
                } else {
                    lay_elm.visibility = View.GONE
                }

                if (int_tru != 0f && int_fls != 0f) {
                    inter.text = Math.floor((int_tru * 100 / (int_tru + int_fls)).toDouble()).toString() + "%"
                } else {
                    lay_inter.visibility = View.GONE
                }

                if (adv_tru != 0f && adv_fls != 0f) {
                    adv.text = Math.floor((adv_tru * 100 / (adv_tru + adv_fls)).toDouble()).toString() + "%"
                } else {
                    lay_adv.visibility = View.GONE
                }

                if (fof_tru != 0f && fof_fls != 0f) {
                    fof.text = Math.floor((fof_tru * 100 / (fof_tru + fof_fls)).toDouble()).toString() + "%"
                } else {
                    lay_504.visibility = View.GONE
                }

                if (tof_tru != 0f && tof_fls != 0f) {
                    tofel.text = Math.floor((tof_tru * 100 / (tof_tru + tof_fls)).toDouble()).toString() + "%"
                } else {
                    lay_tofel.visibility = View.GONE
                }

                if (tof_tru == 0f && tof_fls == 0f && fof_tru == 0f && fof_fls == 0f && adv_tru == 0f && adv_fls == 0f && int_tru == 0f && int_fls == 0f && elm_tru == 0f && elm_fls == 0f) {
                    noStat.typeface = HelloApp.IRANSANS
                    lay_no_stat.visibility = View.VISIBLE
                    noStatBtn.setOnClickListener {
                        UserStatDialog.dismiss()
                    }
                }

            }
        }


        UserStatDialog.setView(dialogView)
        UserStatDialog.show()

    }

    fun QuestSettings(context: Context) {


        var NumChalleng = SPref(context, "level")!!.getInt("numChallenge", 20)
        var timeOnChalleng = SPref(context, "level")!!.getInt("timeOnChalleng", 20)


        val wordDesc = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.level_setting, null)

        val confirmBtn = dialogView.findViewById<Button>(R.id.ls_okBtn)
        confirmBtn.typeface = HelloApp.IRANSANS
        val numChellenge = dialogView.findViewById<View>(R.id.ls_count_challenge) as EditText
        val timeChallenge = dialogView.findViewById<View>(R.id.ls_time_for_challenge) as EditText
        val tvQuest = dialogView.findViewById<TextView>(R.id.ls_tv_quest_count)
        val tvTimeQuest = dialogView.findViewById<TextView>(R.id.ls_tv_time_limit)
        val speakLbl = dialogView.findViewById<TextView>(R.id.ls_speakTitle)
        val speakSwh = dialogView.findViewById<SwitchCompat>(R.id.ls_speakSwh) as SwitchCompat


        speakLbl.typeface = HelloApp.IRANSANS

        speakSwh.isChecked = SPref(applicationContext, "setting")!!.getBoolean("speaker", true)

        speakSwh.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPref(applicationContext, "setting")!!.edit().putBoolean("speaker", true).apply()
            } else {
                SPref(applicationContext, "setting")!!.edit().putBoolean("speaker", false).apply()
            }
        }

        tvQuest.typeface = HelloApp.IRANSANS
        tvTimeQuest.typeface = HelloApp.IRANSANS

        numChellenge.typeface = HelloApp.IRANSANS
        timeChallenge.typeface = HelloApp.IRANSANS

        numChellenge.setText(NumChalleng.toString())
        timeChallenge.setText(timeOnChalleng.toString())


        confirmBtn.setOnClickListener {
            var numChallenges = numChellenge.text.toString().toInt()
            var timeOnChallenge = timeChallenge.text.toString().toInt()
            var toast = false
            if (numChallenges >= 40) {
                numChallenges = 40
                toast = true
            }
            if (timeOnChalleng >= 60) {
                timeOnChalleng = 60
                toast = true
            }
            if (toast) sToast(applicationContext, applicationContext.resources.getString(R.string.maxNums))

            SPref(context, "level")!!.edit().putInt("numChallenge", numChallenges.toInt()).apply()
            SPref(context, "level")!!.edit().putInt("timeOnChalleng", timeOnChallenge).apply()
            wordDesc.cancel()
        }
        wordDesc.setView(dialogView)
        wordDesc.show()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (first_time) {
//            onDestroy()
                finish()
                finishAffinity()
            } else {
                sToast(applicationContext, applicationContext.resources.getString(R.string.exit), true)
                first_time = true
                HANDLER.postDelayed(Runnable {
                    first_time = false
                }, 2000)
            }
//            super.onBackPressed()

        }
    }
}
