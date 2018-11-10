package com.chapdast.ventures.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.Handlers.Ana

import kotlinx.android.synthetic.main.activity_profile.*
import net.jhoobin.jhub.util.AccountUtil
import org.json.JSONObject
import java.util.*

class Profile : ChapActivity() {

    var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        if (ChapActivity.netCheck(this)) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profile)

            uInfoName.typeface = HelloApp.IRANSANS_BLACK
            uInfoUsername.typeface = HelloApp.IRANSANS_BLACK
            uInfoUnsub.typeface = HelloApp.IRANSANS
            uInfoNameLbl.typeface = HelloApp.IRANSANS
            uInfoUsernameLbl.typeface = HelloApp.IRANSANS

            userId = SPref(applicationContext, "userCreds")?.getString("userId", 0.toString())
            var userName = SPref(this, "userProfileCreds")!!.getString("userName", resources.getString(R.string.unSet))

            uInfoName.setOnClickListener {
                setName()
            }


            uInfoName.text = userName
            uInfoUsername.text = userId
            uInfoUnsub.setOnClickListener {
                UnSub()
            }
            uInfoBackBtn.setOnClickListener {
                finish()
            }
        }

    }

    fun UnSub() {
        val wordDesc = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.unsub_alert, null, false)
        val userId = SPref(this, "userCreds")!!.getString("userId", 0.toString())

        var text = dialogView.findViewById<View>(R.id.unSub_txt) as TextView

        val yesBtn = dialogView.findViewById<View>(R.id.unSUb_yes) as TextView
        val noBtn = dialogView.findViewById<View>(R.id.unSub_no) as TextView
        text.typeface = HelloApp.IRANSANS
        yesBtn.typeface = HelloApp.IRANSANS
        noBtn.typeface = HelloApp.IRANSANS
        var message = text.text.toString()+"\n"
        if(CheckPhoneNumber(userId,0)){
            message += applicationContext.resources.getString(R.string.unsubIrancel)
        }else if(CheckPhoneNumber(userId,1)){
            message += applicationContext.resources.getString(R.string.unsubMci)
        }

        text.text = message
        yesBtn.setOnClickListener {

            var data: MutableMap<String, String> = mutableMapOf("m" to "unSub", "phone" to userId)

            if (CheckPhoneNumber(userId, 0)) {
                var purchaseToken = SPref(applicationContext, "purchase")!!.getString("purchase", 0.toString())
                var puJson: JSONObject = JSONMAKER(purchaseToken)
                Log.e("UNSUB", puJson.toString())
                data.put("purchaseToken", puJson.getString("purchaseToken"))
                data.put("purchaseTime", puJson.getString("purchaseTime"))
                data.put("orderId", puJson.getString("orderId"))
                data.put("msisdn", puJson.getString("msisdn"))
            }


            var unsub = khttp.post(SERVER_ADDRESS, data = data)
            Log.d("unSub", unsub.text.toString())
            if (unsub.statusCode == 200) {
//                if (CheckPhoneNumber(userId, 1)) {
//                    var smsHandler = MciSmsHandler()
//                    var mci_unsub = smsHandler.memberUnsub(userId)
//                    Log.d("sms-v2-unsub", "unsub $mci_unsub")
//                }

                var jes = unsub.jsonObject
                if (jes.getBoolean("result")) {
                    var ana = Ana(this)
                    var uns = ana.unSub()
                    Log.d("RESULT", uns.toString())
                    if (uns) {
                        SPref(applicationContext, "userCreds")!!.edit().clear().apply()
                        SPref(applicationContext, "level")!!.edit().clear().apply()
                        SPref(applicationContext, "purchase")!!.edit().clear().apply()
                        SPref(applicationContext, "userProfileCreds")!!.edit().clear().apply()
                        SPref(applicationContext, "ana")!!.edit().clear().apply()
                        var unsubAction = Intent(Intent.ACTION_MAIN)
                        unsubAction.addCategory(Intent.CATEGORY_HOME)
                        startActivity(unsubAction)
                        sToast(applicationContext, applicationContext.resources.getString(R.string.unSubSucces), true)
                        AccountUtil.removeAccount()
                        wordDesc.dismiss()
                        var splashPage = Intent(applicationContext, Welcome::class.java)
                        startActivity(splashPage)
                        if(CheckPhoneNumber(userId,0)){
                            sToast(applicationContext,applicationContext.resources.getString(R.string.unsubIrancel),true)
                        }else if(CheckPhoneNumber(userId,1)){
                            sToast(applicationContext,applicationContext.resources.getString(R.string.unsubMci),true)
                        }
                        finish()
                    } else {
                        sToast(applicationContext, "UN SUCCESS")
                    }
                } else {
                    sToast(applicationContext, getString(R.string.cantConnectToServer), true)
                }


            }
        }


        noBtn.setOnClickListener {
            wordDesc.dismiss()
        }


        wordDesc.setView(dialogView)
        wordDesc.show()

    }

    fun setName() {
        val setNameDialog = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.set_user_name, null, false)

        var message = dialogView.findViewById<View>(R.id.setName_name_textView) as TextView

        var confirmBtn = dialogView.findViewById<View>(R.id.setName_confirm) as TextView
        var inputName = dialogView.findViewById<View>(R.id.setName_name) as EditText
        message.typeface = HelloApp.IRANSANS
        confirmBtn.typeface = HelloApp.IRANSANS
        inputName.typeface = HelloApp.IRANSANS

        confirmBtn.setOnClickListener {
            if (!inputName.text.toString().isNullOrBlank()) {
                SPref(applicationContext, "userProfileCreds")!!.edit().putString("userName", inputName.text.toString()).apply()
                uInfoName.text = inputName.text.toString()
                if (!userId.equals(0.toString())) {
                    SetNameToServer(applicationContext, userId!!, inputName.text.toString()).execute()
                }
                setNameDialog.dismiss()
            }
        }
        setNameDialog.setView(dialogView)
        setNameDialog.show()

    }

    inner class SetNameToServer(context: Context, userId: String, inpName: String) : AsyncTask<String, String, String>() {
        val name = inpName
        val userId = userId
        val con = context
        override fun doInBackground(vararg params: String?): String {
            var data = mapOf<String, String>("m" to "SetName", "phone" to userId, "name" to name)
            var setname = khttp.post(SERVER_ADDRESS, data = data)
            if (setname.statusCode == 200) {
                Log.d("SETNAME", "Name Changed")
            }
            return "0"
        }

    }

}
