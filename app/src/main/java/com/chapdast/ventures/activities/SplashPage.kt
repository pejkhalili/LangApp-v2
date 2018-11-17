package com.chapdast.ventures.activities


import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.android.billingclient.util.IabHelper
import com.android.billingclient.util.IabResult
import com.android.billingclient.util.Purchase
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.Handlers.Ana
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_splash_page.*
import kotlinx.android.synthetic.main.splash_screen_five.*
import kotlinx.android.synthetic.main.splash_screen_four.*
import kotlinx.android.synthetic.main.splash_screen_three.*
import net.jhoobin.jhub.CharkhoneSdkApp
import net.jhoobin.jhub.util.AccountUtil
import org.json.JSONException
import org.json.JSONObject


@Suppress("DEPRECATION")
open class SplashPage : ChapActivity(), View.OnClickListener {

    lateinit var codeTimer: CountDownTimer
    val HANDELER = Handler()
    var first_time = false
    lateinit var mHelper: IabHelper
    var payloadJoob = "subscribe"
    lateinit var codeRecReceiver: CodeReceiver
    lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_splash_page)
            CharkhoneSdkApp.initSdk(applicationContext, getSecrets(), true, R.mipmap.icon)
            var ANA = Ana(applicationContext)
            ANA.loginPage()
            pageDots.visibility = View.GONE

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                setupPermissions()
            }

            codeRecReceiver = CodeReceiver()

            intentFilter = IntentFilter()
            intentFilter.addAction(smsAct)
            if (AUTO_READ) applicationContext.registerReceiver(codeRecReceiver, intentFilter)
            if (!AUTO_READ) sp_three_price.visibility = View.INVISIBLE

            mHelper = IabHelper(applicationContext, RSA_KEY)
            mHelper.enableDebugLogging(false, "$PAY_TAG-deb")

            mHelper.startSetup(PayIabListener())


            lb.typeface = HelloApp.IRANSANS

            sp_three_num_box.typeface = HelloApp.IRANSANS
            sp_three_num_box.setSelection(2)
            sp_three_price.typeface = HelloApp.IRANSANS


            sp_four_confirm_box.typeface = HelloApp.IRANSANS
            sp_four_until_text.typeface = HelloApp.IRANSANS
            sp_four_willSend.typeface = HelloApp.IRANSANS
            sp_four_codeTimer.typeface = HelloApp.IRANSANS

            val elm = findViewById<View>(R.id.spf_elm) as Button
            val int = findViewById<View>(R.id.spf_inter) as Button
            val adv = findViewById<View>(R.id.spf_adv) as Button
            val fof = findViewById<View>(R.id.spf_504) as Button
            val oneOone = findViewById<View>(R.id.spf_tofel) as Button

            elm.typeface = HelloApp.IRANSANS
            int.typeface = HelloApp.IRANSANS
            adv.typeface = HelloApp.IRANSANS
            fof.typeface = HelloApp.IRANSANS
            oneOone.typeface = HelloApp.IRANSANS


            sp_three_num_box.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//                    splash3()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(sp_three_num_box.windowToken, 0)
                    return@OnKeyListener true
                }
                false

            })

            sp_four_confirm_box.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//                    SignUpConfirm()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(sp_four_confirm_box.windowToken, 0)
                    return@OnKeyListener true
                }
                false
            })


            sp_three_confirm.setOnClickListener(this)
            sp_four_confirm.setOnClickListener(this)
            sp_four_change_number.setOnClickListener(this)
            sp_four_change_number.typeface = HelloApp.IRANSANS
            sp_four_confirm.typeface = HelloApp.IRANSANS
            sp_three_confirm.typeface = HelloApp.IRANSANS
            sp_three_text.typeface = HelloApp.IRANSANS
            sp_four_text.typeface = HelloApp.IRANSANS


            elm.setOnClickListener { intenter(applicationContext, 1) }
            int.setOnClickListener { intenter(applicationContext, 2) }
            adv.setOnClickListener { intenter(applicationContext, 3) }
            fof.setOnClickListener { intenter(applicationContext, 4) }
            oneOone.setOnClickListener { intenter(applicationContext, 5) }
//            val userID = SPref(applicationContext, "userCreds")?.getString("userId", null)
//            val exp = SPref(applicationContext, "subStat")?.getBoolean("stat", false)
//
//            if (exp != true && userID != null) {
//                SignUp()
//            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onClick(item: View?) {

        if (item != null) {
            when (item.id) {

                R.id.sp_three_confirm -> {
                    //check Phone Number
//                    var id = SPref(applicationContext, "userCreds")?.getString("userId", null)
//                    if (id != null) {
//                        SPref(applicationContext, "userCreds")?.edit()?.clear()?.commit()
//                        SPref(applicationContext, "userCreds")?.edit()?.putBoolean("expired", true)?.commit()
//                    }
                    SignUp()

                }

                R.id.sp_four_confirm -> {
                    //check Entered RespCode With server
                    SignUpConfirm()
                }

                R.id.sp_four_change_number -> {
                    codeTimer.cancel()
                    SPref(applicationContext, "userCreds")!!.edit()!!.putBoolean("expired", true)!!.commit()
                    setConfirmNumberKey()
                    sp3.visibility = View.VISIBLE
                    sp4.visibility = View.GONE
                }

            }
        }
    }

    inner class CodeReceiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {

            if (p1!!.action == smsAct) {

                var tempUserId = SPref(applicationContext, "Temp")!!.getString("userId", 0.toString())
                sp_four_confirm_box.setText(p1.getStringExtra("code").toString())
                var ANA = Ana(applicationContext)
                ANA.recievedCode(tempUserId)
                if (AUTO_CONFIRM) SignUpConfirm()
            }
        }


    }

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

    inner class PayIabListener : IabHelper.OnIabSetupFinishedListener {
        override fun onIabSetupFinished(result: IabResult?) {
            Log.d("$PAY_TAG-iab", result!!.message)
            if (!result.isSuccess) return
            if (mHelper == null) return
//             Let's get an inventory of stuff we own.
            Log.d(PAY_TAG, "Setup successful. Querying inventory.")
            try {

//                mHelper.queryInventoryAsync(mGotInventoryListener(mHelper))

            } catch (e: IabHelper.IabAsyncInProgressException) {
                Log.e(PAY_TAG, "Error querying inventory. Another async operation inprogress. ${e.message}")
            }
        }

    }

    inner class PayDown : IabHelper.OnIabPurchaseFinishedListener {

        override fun onIabPurchaseFinished(result: IabResult?, purchase: Purchase?) {
            Log.d(PAY_TAG, "Purchase finished: " + result + ",\npurchase: " + purchase.toString())
            SPref(applicationContext, "purchase")!!.edit().putString("purchase", purchase.toString()).apply()
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                Log.d(PAY_TAG, "mHelper is Null")
                return
            }

            if (result!!.isFailure) {
                Log.i(PAY_TAG, "Error purchasing: " + result)
                return
            }

            var ANA = Ana(applicationContext)
            var tempUserId = SPref(applicationContext, "Temp")!!.getString("userId", 0.toString())
            ANA.sub(tempUserId!!)

            var userId = SPref(applicationContext, "userCreds")!!.getString("insertedPhone", "null")
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)
            if (userId.equals("null")) Log.d("Err1", userId.toString())
            var purchaseToken = SPref(applicationContext, "purchase")!!.getString("purchase", null)
            if (purchaseToken != null) {
                var puJson: JSONObject = JSONMAKER(purchaseToken)
                var resp = khttp.post(SERVER_ADDRESS, data = mapOf(
                        "m" to "register",
                        "phone" to userId,
                        "purchaseToken" to puJson.getString("purchaseToken"),
                        "purchaseTime" to puJson.getString("purchaseTime"),
                        "orderId" to puJson.getString("orderId"),
                        "msisdn" to puJson.getString("msisdn")))
                if (resp.statusCode == 200) {

                    try {
                        var jes = resp.jsonObject
                        if (jes.getBoolean("result")) {
                            //send to responseble service provider
                            SPref(applicationContext, "userCreds")!!.edit().putString("userId", userId).apply() // set userid to old userId field
                            SPref(applicationContext,"subStat")!!.edit().putBoolean("stat",true).apply() // sub stat to true
                            Log.i("SET_ID", "USER SET $userId")
                            sp3.visibility = View.GONE
                            sp5.visibility = View.VISIBLE
                            pageDots.visibility = View.INVISIBLE
                            //send Request To register
                            //Load next Page
                            intenter(applicationContext, 1, false)
                            Log.d("$PAY_TAG-OnFine", "PURCHASE COMP!")
                        } else if (!jes.getBoolean("result")) {
                            Log.d("Err1", jes.toString())
                        }
                    } catch (e: Exception) {
                        Log.d("Err1", e.message)
                    }
                } else {
                    Log.d("Err1", resp.statusCode.toString())
                }

                Log.d(PAY_TAG, "Purchase successful.")
            }
        }

    }

    fun launchPay() {

        try {
            mHelper.launchSubscriptionPurchaseFlow(
                    this,
                    SUK_KEY,
                    9009,
                    PayDown(),
                    payloadJoob)
        } catch (e: Exception) {
            Log.e(PAY_TAG, "Error-->> " + e.message)
        }
    }

    fun getSecrets(): Array<out String>? {
        return resources.getStringArray(R.array.secrets)
    }


    //joobin

    fun setConfirmNumberKey(wait: Boolean = true) {
        if (wait == true) {
            sp_three_confirm.isEnabled = true
            sp_three_confirm.text = applicationContext.getString(R.string.sendNumber)
        } else {
            sp_three_confirm.isEnabled = false
            sp_three_confirm.text = getString(R.string.pleaseWaitLoginPage)
        }
    }

    @SuppressLint("ApplySharedPref")
    fun SignUp() {
//        val expired = SPref(applicationContext, "userCreds")?.getBoolean("expired", false)
//        val trialUser = SPref(applicationContext, "userCreds")?.getString("userId", null)
//        lateinit var phoneNum: String
/*
        if (trialUser == null && expired != true) {
            phoneNum = sp_three_num_box.text.toString()
            if (phoneNum.length > 10) {
                SPref(applicationContext, "userCreds")?.edit()?.putString("userId", phoneNum)?.apply()
                intenter(applicationContext, 0, true)
            } else {
                var ANA = Ana(applicationContext)
                ANA.wrongNumber(phoneNum)
                sToast(this, applicationContext.resources.getString(R.string.wrongNum))
                setConfirmNumberKey()
            }
        } else {
            if(expired == true){
                phoneNum = sp_three_num_box.text.toString()
            }else{
                phoneNum = "" + trialUser
            }
            */
        val phoneNum = sp_three_num_box.text.toString();
            if (phoneNum.length > 10) {
                SPref(applicationContext, "userCreds")!!.edit().putString("insertedPhone", phoneNum).apply()
                var getUserStatus = CheckUserStatus().execute(phoneNum)
                var jes = getUserStatus.get()
                if (jes.getBoolean("result")) {
                    if (jes.getString("status") == "sub") {
                        var ANA = Ana(applicationContext)
                        ANA.reLog(phoneNum)
                    }
// Irancell Peyment
                    if (CheckPhoneNumber(phoneNum, 0)) {
                        var checkVpnConnection = CheckVpnConnection().execute()
                        var vpn = checkVpnConnection.get()
                        if (vpn) {
                            HelloApp.HANDLER.postDelayed({
                                SPref(applicationContext, "Temp")!!.edit().putString("userId", phoneNum).commit()
                                var ANA = Ana(applicationContext)
                                ANA.requestCode(phoneNum)
                                AccountUtil.removeAccount()
                                var fillNumber = Intent()
                                fillNumber.putExtra("msisdn", phoneNum)
                                fillNumber.putExtra("editAble", false)
                                fillNumber.putExtra("autoRenewing", true)
                                mHelper.setFillInIntent(fillNumber)
                                launchPay()
                            }, 10)
                        }
                        // Vpn On
                        else {
                            sToast(applicationContext, getString(R.string.turnOffVpn), false)
                        }

                    }
// MCI PEYMENT
                    else if (CheckPhoneNumber(phoneNum, 1)) {
                        codeTimer = CountDowner(sp_four_codeTimer, TIME_FOR_NEW_CODE, false)
                        var sendMessage = SendSubRequest().execute(phoneNum)

                        var jes = sendMessage.get()

                        Log.i("ERR_SMS", "RSP:" + jes.toString())
                        if (jes.getBoolean("result") && jes.getBoolean("status")) {
                            var ANA = Ana(applicationContext)
                            ANA.requestCode(phoneNum)
                            SPref(applicationContext, "Temp")!!.edit().putString("userId", phoneNum).commit()
                            SPref(applicationContext, "userCreds")!!.edit().putString("activeCode", jes.getString("tid")).apply()
                        } else {
                            Log.d("ERR_SMS", "3cant Send")
                            sToast(applicationContext, getString(R.string.pleaseTryAgain), true)
                            var ANA = Ana(applicationContext)
                            ANA.mciFail(phoneNum)
                        }
                    }
// Not Supported Number
                    else {
                        var ANA = Ana(applicationContext)
                        ANA.NotSupported(phoneNum)
                        sToast(applicationContext, resources.getString(R.string.unSupportedNumber), false)
                        SPref(applicationContext, "userCreds")?.edit()?.clear()?.commit()
                        setConfirmNumberKey()
                    }
                }
            }else {
                var ANA = Ana(applicationContext)
                ANA.wrongNumber(phoneNum)
                sToast(this, applicationContext.resources.getString(R.string.wrongNum))
                setConfirmNumberKey()
            }
//        }

    }

    fun SignUpConfirm() {
        var UserId = SPref(applicationContext, "userCreds")!!.getString("insertedPhone", 0.toString())
        var PurchaseToken = SPref(this, "userCreds")!!.getString("activeCode", 0.toString()).replace("SUCCESS.", "")
        var verfiyText = sp_four_confirm_box.text.toString()

        if (verfiyText.length == 4) {
            var ANA = Ana(applicationContext)
            var tempUserId = SPref(applicationContext, "Temp")!!.getString("userId", 0.toString())
            ANA.sub(tempUserId)

            var resp = AcvtivateSubScribtion().execute(*arrayOf(UserId, PurchaseToken, verfiyText))
            var jes = resp.get()

            if (jes.getBoolean("result")) {
                SPref(applicationContext, "userCreds")!!.edit().putString("userId", UserId).apply() // set userid to old userId field
                intenter(applicationContext, 0, false)
                SPref(applicationContext,"userCreds")?.edit()?.putBoolean("expired",false)?.commit()
                //send to responseble service provider
                sp4.visibility = View.GONE
                sp5.visibility = View.VISIBLE
                SPref(applicationContext,"subStat")!!.edit()!!.putBoolean("stat",true)!!.apply()
                //send Request To register
                //Load next Page
            } else if (!jes.getBoolean("result")) {
                Log.d("Err1", jes.toString())
                sToast(applicationContext, getString(R.string.pleaseTryAgain), true)
                sp4.visibility = View.GONE
                sp3.visibility = View.VISIBLE
            }

        } else {
            sToast(applicationContext, getString(R.string.code_not_match))
        }
    }

    fun intenter(context: Context, lvl: Int, loadHub: Boolean = true) {
        val userId = SPref(this, "userCreds")!!.getString("userId", null)
        if (userId != null) {
            SPref(this, "level")!!.edit().putInt("level", lvl).apply()
            var strLevel: String? = null
            when (lvl) {
                1 -> strLevel = "elementry"
                2 -> strLevel = "intermediate"
                3 -> strLevel = "advance"
                4 -> strLevel = "504"
                5 -> strLevel = "tofel"
                else -> strLevel = "elementry"
            }
            val setLevel = SetUserLevel().execute(*arrayOf(userId, strLevel))
            val res: JSONObject = setLevel.get()
            if (res.getBoolean("result") && loadHub) {

                val intent = Intent(context, Hub::class.java)
                finish()
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupPermissions() {
        val permission = applicationContext.checkSelfPermission("android.permission.RECEIVE_SMS")

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf("android.permission.RECEIVE_SMS", "android.permission.READ_PHONE_STATE"),
                SMS_REC_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SMS_REC_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("SMS_PRR", "Permission has been denied by user")
                } else {
                    Log.i("SMS_PRR", "Permission has been granted by user")
                }
            }
        }
    }

    override fun onBackPressed() {
        if (first_time) {
            finish()
            finishAffinity()
        } else {
            sToast(applicationContext, resources.getString(R.string.exit), true)
            first_time = true
            HANDELER.postDelayed({
                first_time = false
            }, 2000)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("$PAY_TAG-Disp", "Destroying helper.")
        if (mHelper != null) {
            mHelper.disposeWhenFinished()
//            mHelper = null;
        }

//        applicationContext.unregisterReceiver(codeRecReceiver)


    }


    // API Requests
    inner class CheckVpnConnection : AsyncTask<String, Boolean, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {

            val request = khttp.get(VPN_CHECK_ADDRESS)
            if (request.statusCode == 200) {
                try {
                    val res = request.jsonObject
                    if (res["result"] == true) {
                        return res.getBoolean("vpn_status")
                    }
                } catch (e: JSONException) {
                    Log.e("VPN_CHECK", e.message)
                }
            }
            return false
        }


    }

    inner class SendSubRequest : AsyncTask<String, String, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            HelloApp.HANDLER.post {
                sp3.visibility = View.GONE
                sp_four_confirm_box.setText("")
                sp4.visibility = View.VISIBLE

                sp_four_requestNowCode.typeface = HelloApp.IRANSANS

                codeTimer.start()

                sp_four_requestNowCode.setOnClickListener {
                    //timer reset
                    codeTimer.cancel()

                    Log.e("SSR", "SUBREQUEST " + sp_three_num_box.text.toString())
                    codeTimer = CountDowner(sp_four_codeTimer, TIME_FOR_NEW_CODE, false)
                    SendSubRequest().execute(SPref(applicationContext,"userCreds")!!.getString("userId",""))

                    sp_four_timerHolder.visibility = View.VISIBLE

                    sp_four_requestNowCode.visibility = View.GONE

                    codeTimer.start()
                    //ask for new code

                }
                HANDELER.postDelayed({
                    sp_four_timerHolder.visibility = View.INVISIBLE

                    sp_four_requestNowCode.visibility = View.VISIBLE

                }, (TIME_FOR_NEW_CODE * 1000).toLong())

            }


        }

        override fun doInBackground(vararg params: String?): JSONObject {
            val phoneNum = params[0]

            var req = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "sendsms", "phone" to phoneNum))
            if (req.statusCode == 200) {
                return req.jsonObject
            } else {
                Log.e("SSR", "SendSubRequest " + req.statusCode.toString())
            }

            return mapOf<String, Boolean>("result" to false, "status" to false) as JSONObject
        }
    }

    inner class CheckUserStatus : AsyncTask<String, String, JSONObject>() {
        override fun doInBackground(vararg params: String?): JSONObject {
            val phoneNum = params[0]
            var req = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "checkUser", "phone" to phoneNum))
            try {
                if (req.statusCode == 200) {
                    return req.jsonObject
                } else {
                    Log.e("CUS", "CheckUserStatus " + req.statusCode.toString())
                }
            } catch (e: Exception) {
                Log.i("CUS", "++++++" + req.toString())
            }
            return mapOf<String, Boolean>("result" to false) as JSONObject
        }
    }

    inner class AcvtivateSubScribtion : AsyncTask<String, String, JSONObject>() {
        override fun doInBackground(vararg params: String?): JSONObject {
            val phoneNum = params[0]
            val PurchaseToken = params[1]
            val verfiyText = params[2]
            var req = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "register", "phone" to phoneNum, "tid" to PurchaseToken, "pin" to verfiyText))
            if (req.statusCode == 200) {
                return req.jsonObject
            } else {
                HelloApp.HANDLER.post {
                    sToast(applicationContext, getString(R.string.cantConnectToServer), true)
                }

                Log.e("ASS", "AcvtivateSubScribtion " + req.statusCode.toString())
            }

            return mapOf<String, Boolean>("result" to false) as JSONObject
        }
    }

    inner class SetUserLevel : AsyncTask<String, String, JSONObject>() {
        override fun doInBackground(vararg params: String?): JSONObject {
            val phoneNum = params[0]
            val level = params[1]
            var req = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "setLevel", "phone" to phoneNum, "level" to level))
            try {
                if (req.statusCode == 200) {
                    return req.jsonObject
                } else {
                    Log.e("SUL", "SetUserLevel  " + req.statusCode.toString())
                }
            } catch (e: Exception) {

            }
            return mapOf<String, Boolean>("result" to false) as JSONObject
        }
    }

}

