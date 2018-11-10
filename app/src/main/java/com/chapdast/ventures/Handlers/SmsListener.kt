package com.chapdast.ventures.Handlers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.telephony.SmsMessage
import com.chapdast.ventures.Configs.SMS_LINE_NO
import com.chapdast.ventures.Configs.gotSms
import com.chapdast.ventures.Configs.smsAct
import com.chapdast.ventures.Configs.smsCode

class SmsListener : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Log.e("EACH_SMS",">>>>>IN SMS READER<<<<")
        if(intent.action.equals("mk.mk.mk")){
            Log.d("SMS",intent.action)
        }else if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.action)) {
            Log.e("EACH_SMS",">>>>>IN SMS READER IN<<<<")
            val bundle = intent.extras
            val pdus = bundle.get("pdus") as Array<Any>
            val messages = arrayOfNulls<SmsMessage>(pdus.size)
            for (i in pdus.indices) {
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)

            }
            for (i in 0 until messages.size) {
                if (messages[i] != null) {
                    Log.d("EACH_SMS", "....>>>" + messages[i]?.originatingAddress.toString())
                    if (!gotSms && messages[i]?.originatingAddress!!.startsWith(SMS_LINE_NO) ) {
                        val messageBody = messages[i]?.messageBody
                        var code = messageBody?.substringAfter(":")
                        var cleanCode = code?.subSequence(0, 4)
                        smsCode = cleanCode.toString().toInt()
                        gotSms = true
                        var intent = Intent(smsAct)
                        intent.putExtra("code",cleanCode.toString())
                        ctx.sendBroadcast(intent)

                        Log.i("EACH_SMS-cod", "COD IS $smsCode")
                    }
                }

            }
        }
    }
}