package com.chapdast.ventures.Configs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R
import org.json.JSONObject
import java.util.*


fun sToast(context: Context=HelloApp.context, toastMessage: String, isShort: Boolean = true) {

    var ToastLen = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    var assetManager = context.assets
    var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))

    val toast = Toast(context)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.toaster, null)
//    val img = layout.findViewById<ImageView>(R.id.tst_img)
    val txt = layout.findViewById<TextView>(R.id.tst_text)
    txt.typeface = iransans
    txt.text = toastMessage


//    toast.setGravity(Gravity.TOP, Gravity.CENTER, 110)
    toast.view = layout
//    toast.setMargin(10f, 150f)
    toast.duration = ToastLen
    toast.show()
}

fun SPref(context: Context?, name: String): SharedPreferences? {
    var sh = context?.getSharedPreferences(name, Context.MODE_PRIVATE)
    fun edit(): SharedPreferences.Editor? {
        return sh?.edit()
    }

    return sh
}

fun LevelSelector(context: Context, level: Int): String {
    var text: String
    when (level) {
        2 -> text = context.applicationContext.resources.getString(R.string.intermediate_level)
        3 -> text = context.applicationContext.resources.getString(R.string.advanced_level)
        4 -> text = context.applicationContext.resources.getString(R.string._504_essential_words)
        5 -> text = context.applicationContext.resources.getString(R.string._1001_tofel_vocab)
        else -> {
            text = context.applicationContext.resources.getString(R.string.elementary_level)
            SPref(context, "level")!!.edit().putInt("level", 1).apply()
        }
    }
    return text.toUpperCase()
}

fun CountDowner(v: TextView, time: Int,hide: Boolean = false): CountDownTimer {
    v.visibility = View.GONE
    v.visibility = View.VISIBLE
    var input = time.toLong() * 1000

    var re = object : CountDownTimer(input, 1000) { // adjust the milli seconds here

        override fun onTick(millisUntilFinished: Long) {
            var h = ((millisUntilFinished / 1000) / 3600).toInt()
            var m = (((millisUntilFinished / 1000) - (h * 3600)) / 60).toInt()
            var s = ((millisUntilFinished / 1000) - (h * 3600) - (m * 60)).toInt()
            if (h == 0) {
                if (m == 0) {
                    v.text  = if(s>=10) "00:$s" else "00:0$s"
                } else {
                    if (m >= 10 && s >= 10) {
                        v.text = "$m:$s"
                    } else if (m >= 10) {
                        v.text = "$m:0$s"
                    } else if (s >= 10) {
                        v.text = "0$m:$s"
                    }
                }
            } else {
                if (h >= 10 && m >= 10 && s >= 10) {
                    v.text = "$h:$m:$s"
                } else if (m >= 10 && s >= 10 && h < 10) {
                    v.text = "0$h:$m:$s"
                } else if (h >= 10 && s >= 10 && m < 10) {
                    v.text = "$h:0$m:$s"
                } else if (h >= 10 && m >= 10 && s < 10) {
                    v.text = "$h:$m:0$s"
                } else if (m >= 10 && h < 10 && s < 10) {
                    v.text = "0$h:$m:0$s"
                } else if (s >= 10 && h < 10 && m < 10) {
                    v.text = "0$h:0$m:$s"
                } else if (h >= 10 && m < 10 && s < 10) {
                    v.text = "$h:0$m:0$s"
                }
            }
        }

        override fun onFinish() {
            v.text = "00:00"
            if(hide) v.visibility=View.INVISIBLE
        }

    }

    return re
}

fun CanQuest(context: Context, v: TextView, notif: Boolean = true): Boolean {
    var current_time = (Calendar.getInstance().timeInMillis / 1000).toString().toLong()
//                SPref(this,"quest")!!.edit().putString("lastFinish",current_time.toString()).commit()
    var lastFinish = SPref(context, "quest")!!.getString("lastFinish", "1").toLong()

    var eligiableQuest = lastFinish + QUEST_TIME_LIMIT

    if (eligiableQuest <= current_time) {
        return true
    } else {
        var inTime = eligiableQuest - current_time
//
//        var h = inTime / (60 * 60)
//        var m = (inTime - (h * 60 * 60)) / 60
//        var s = (inTime - (h * 60 * 60) - (m * 60))

        if (notif) sToast(context, context.resources.getString(R.string.finished_quest))
        if (!notif) CountDowner(v, inTime.toInt(), true).start()

    }
    return false
}

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
fun RanArray(): Array<String> {
    var randKey = (1..10).random()
    return when (randKey) {
        1 -> arrayOf("ans1", "ans2", "ans3", "ans4")
        2 -> arrayOf("ans2", "ans3", "ans4", "ans1")
        3 -> arrayOf("ans3", "ans4", "ans1", "ans2")
        4 -> arrayOf("ans4", "ans1", "ans2", "ans3")
        5 -> arrayOf("ans1", "ans3", "ans4", "ans2")
        6 -> arrayOf("ans4", "ans1", "ans3", "ans2")
        7 -> arrayOf("ans3", "ans4", "ans2", "ans1")
        8 -> arrayOf("ans1", "ans2", "ans4", "ans3")
        9 -> arrayOf("ans2", "ans3", "ans1", "ans4")
        10 -> arrayOf("ans3", "ans1", "ans4", "ans2")
        else -> arrayOf("ans1", "ans4", "ans2", "ans3")
    }

}

fun SRV_LEVEL_DETECTOR(int: Int): String? {
    when (int) {
        1 -> return "elementry"
        2 -> return "intermediate"
        3 -> return "advance"
        4 -> return "504"
        5 -> return "tofel"
    }
    return 0.toString()
}

fun SRV_LEVEL_DETECTOR(string: String): Int? {
    when (string) {
        "elementry" -> return 1
        "intermediate" -> return 2
        "advance" -> return 3
        "504" -> return 4
        "tofel" -> return 5
    }
    return 0
}

fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun CheckPhoneNumber(number: String, type: Int): Boolean {
    var Type = type
    var Number = number.substring(1, 4).toInt()
    var irancellStartNum = arrayOf(930, 933, 935, 936, 937, 938, 939, 901, 902, 903, 904, 905, 941)
    var mciStartNum = arrayOf(910, 911, 912, 913, 914, 915, 916, 917, 918, 919, 990, 991)
    when (Type) {
        0 -> {//irancell
            irancellStartNum.forEach { it ->
                if (it == Number) return true
            }

        }
        1 -> {//mci
            mciStartNum.forEach { it ->
                if (it == Number) return true
            }
        }
    }

    return false
}

fun JSONMAKER(str: String): JSONObject {
    /*
    {
        "orderId":"4568971",
        "packageName":"com.chapdast.ventures",
        "productId":"ABAHEL30",
        "purchaseTime":1534161401000,
        "purchaseToken":"4568971",
        "developerPayload":"",
        "autoRenewing":false,
        "msisdn":"fqotQptZ42AcHyDSF4ELfUQdhHrWQy"
    }
     */
    return JSONObject(str.substringAfter("PurchaseInfo(type:subs):"))
}