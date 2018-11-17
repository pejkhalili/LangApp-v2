package com.chapdast.ventures.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.Handlers.Ana

import kotlinx.android.synthetic.main.activity_quest_new.*
import kotlinx.android.synthetic.main.new_quest_loader.*
import kotlinx.android.synthetic.main.new_timer.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Quest : ChapActivity(), OnInitListener {
    var locked = false

    var rightAnswers = 0
    var wrongAnswers = 0

    var timeRun = false
    var remainTime: Int = 0
    var tim: CountDownTimer? = null
    var currentQuest: Int? = null
    var ChallengeCount: Int = 20
    var timeForEachChallenge = 20
    var trueAns: String? = null
    var qid = 0
    var temp: Any = ""
    var tts: TextToSpeech? = null
    var speaker: Boolean = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.ENGLISH)
            tts!!.setPitch(1.1f)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS_SRV", "SOUND PROBLEM")
            }
        } else {
            Log.e("TTS_SRV", "INIT FAILED!!")
        }
    }

    fun SpeakOut(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null)
    }

    var back = false
    override fun onBackPressed() {
        if (back) {
            if (tim != null) {
                tim!!.cancel()
            }
            if (tts != null) {
                tts!!.stop()
                tts!!.shutdown()
            }
            super.onBackPressed()
        } else {
            sToast(applicationContext, "برای خروج از آزمون مجددا بازگشت را لمس کنید", true)
            back = true
            HelloApp.HANDLER.postDelayed({
                back = false
            }, 1000)
        }
    }

    override fun onDestroy() {
        if (tim != null) {
            tim!!.cancel()
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_quest_new)
            speaker = SPref(applicationContext, "setting")!!.getBoolean("speaker", true)
            wrongAnswers = SPref(applicationContext, "quest_stat")!!.getInt("WRONG_ANS", 0)
            rightAnswers = SPref(applicationContext, "quest_stat")!!.getInt("RIGHT_ANS", 0)

            timeForEachChallenge = SPref(this, "level")!!.getInt("timeOnChalleng", 20).toInt()
            ChallengeCount = SPref(this, "level")!!.getInt("numChallenge", 20).toInt()

            tts = TextToSpeech(this, this)

            nq_first.typeface = HelloApp.IRANSANS
            nq_sec.typeface = HelloApp.IRANSANS
            nq_third.typeface = HelloApp.IRANSANS
            nq_fourth.typeface = HelloApp.IRANSANS
            nq_unknown.typeface = HelloApp.IRANSANS
            nq_time.typeface = HelloApp.BEBAS_FONT
            nq_time.textSize = 35F
//            val inQuestSubAsked = SPref(applicationContext, "iqs")?.getBoolean("asked", false)
//            val subStat = SPref(applicationContext, "subStat")?.getBoolean("stat", false)
//            if (inQuestSubAsked == true && subStat == false) {//asked user for sub
//                InQuestSub()
//            } else {
                QuestLoader()
//            }
            nq_side.setOnClickListener {
                tim?.cancel()
                finish()
            }
            nq_unknown.setOnClickListener {
                CheckAnswer(0)
            }

            nq_first.setOnClickListener {
                CheckAnswer(1)

            }
            nq_sec.setOnClickListener {
                CheckAnswer(2)

            }

            nq_third.setOnClickListener {
                CheckAnswer(3)

            }
            nq_fourth.setOnClickListener {
                CheckAnswer(4)

            }

        }
    }

    fun QuestLoader() {
        setColor(reset = true)
        var quest = GetQuestion()
        Log.d("RESULT", quest.toString())

        currentQuest = SPref(applicationContext, "runQuest")!!.getInt("currentQuest", 1)

        var resQuest = quest.get("res").toString()

        if (resQuest == "true") {
            if (currentQuest!! <= ChallengeCount) {
                try {
                    nq_counter.text = currentQuest!!.toString() + "/" + ChallengeCount.toString()
                    var questInTitle = quest.get("q").toString().toUpperCase()
                    nq_quest.text = "#" + currentQuest.toString() + " " + questInTitle

                    currentQuest = currentQuest!! + 1

                    temp = SPref(applicationContext, "runQuest")!!.edit().putInt("qid", quest.get("qid")!!.toInt()).commit()
                    temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", currentQuest!!).commit()

                    nq_question.text = quest.get("q").toString().toUpperCase()
                    nq_first.text = quest.get("ans1")
                    nq_sec.text = quest.get("ans2")
                    nq_third.text = quest.get("ans3")
                    nq_fourth.text = quest.get("ans4")
                    nq_trues.text = rightAnswers.toString()//quest.get("trues")
                    nq_wrong.text = wrongAnswers.toString() // quest.get("wrongs")

                    nq_speak.setOnClickListener {
                        SpeakOut(nq_question.text.toString())
                    }
                    HelloApp.HANDLER.postDelayed({
                        if (speaker) {
                            SpeakOut(nq_question.text.toString())
                        }
                    }, 200)


                    trueAns = quest.get(quest.get("ansTrue")!!)
                    if (tim != null) {
                        tim!!.cancel()
                    }
                    tim = Timer(timeForEachChallenge).start()
                    nq_pus.setOnClickListener {
                        TimeControl()
                    }

                } catch (e: Exception) {
                    //                Log.d("Err",e.message)
                }

            } else {
                if (tim != null) {
                    tim!!.cancel()
                }
                var time: Long = (Calendar.getInstance().timeInMillis / 1000).toString().toLong()
                temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", 1).commit()
                temp = SPref(applicationContext, "quest")!!.edit().putString("lastFinish", time.toString()).commit()
                var intent = Intent(this, QuestFinish::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Log.d("Err-RESQUEST", resQuest)
            sToast(this, applicationContext.resources.getString(R.string.noQuestAvalable))
            if (tim != null) {
                tim!!.cancel()
            }
//                temp = SPref(applicationContext,"runQuest")!!.edit().putInt("currentQuest",1).commit()
            temp = SPref(applicationContext, "quest")!!.edit().putString("lastFinish", 0.toString()).commit()
            finish()
        }
    }

    fun CheckAnswer(ans: Int) {
        if (locked) {
            HelloApp.HANDLER.post {
                Runnable {
                    sToast(applicationContext, getString(R.string.setAnswerWait), true)
                }
            }

        } else {
            locked = true
            var userAnswer: String? = null

            when (ans) {
                1 -> userAnswer = nq_first.text.toString()
                2 -> userAnswer = nq_sec.text.toString()
                3 -> userAnswer = nq_third.text.toString()
                4 -> userAnswer = nq_fourth.text.toString()
            }


            val rAns: Int = when (trueAns) {
                nq_first.text.toString() -> 1
                nq_sec.text.toString() -> 2
                nq_third.text.toString() -> 3
                nq_fourth.text.toString() -> 4
                else -> {
                    0
                }
            }
            setColor(rAns)

            if (userAnswer == trueAns && ans != 0) {
                //send true ans to server
//                Toast.makeText(applicationContext, "True Answer", Toast.LENGTH_SHORT).show()

                SetAnswer()
            } else {

                WordShow(nq_question.text.toString(), "", trueAns.toString(), "")
            }

            currentQuest = qid
            qid++
            locked = false
        }


    }

    fun setColor(ans: Int = 0, reset: Boolean = false) {
        if (reset) {
            linearLayout10.background = applicationContext.resources.getDrawable(R.drawable.box_white)
            linearLayout11.background = applicationContext.resources.getDrawable(R.drawable.box_white)
            linearLayout13.background = applicationContext.resources.getDrawable(R.drawable.box_white)
            linearLayout14.background = applicationContext.resources.getDrawable(R.drawable.box_white)

        } else {
            linearLayout10.background = applicationContext.resources.getDrawable(if (ans == 1) R.drawable.box_green else R.drawable.box_red)
            linearLayout11.background = applicationContext.resources.getDrawable(if (ans == 2) R.drawable.box_green else R.drawable.box_red)
            linearLayout13.background = applicationContext.resources.getDrawable(if (ans == 3) R.drawable.box_green else R.drawable.box_red)
            linearLayout14.background = applicationContext.resources.getDrawable(if (ans == 4) R.drawable.box_green else R.drawable.box_red)
        }
    }


    fun TimeControl() {
        if (timeRun) {
            nq_pus.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))

            tim = Timer(remainTime.toInt()).start()

            sToast(applicationContext, applicationContext.resources.getString(R.string.started))
            nq_q_load.visibility = View.VISIBLE
            timeRun = false
        } else {
            nq_pus.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))
            nq_q_load.visibility = View.GONE
            var remTime = nq_time.text.toString()
            var min = remTime.substring(0, 2).toInt()
            var sec = remTime.substring(3, 5).toInt()
            var r: Int = min * 60 + sec
            remainTime = r
            tim?.cancel()
            sToast(applicationContext, applicationContext.resources.getString(R.string.stopped))
            timeRun = true
        }

    }

    @SuppressLint("InflateParams")
    fun WordShow(w: String, sound: String, verb: String, noun: String) {
        val wordToCheck = w.toLowerCase()
        if (tim != null) {
            tim!!.cancel()
        }
        val wordDesc = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.word_desc, null)
        val word = dialogView.findViewById<View>(R.id.wd_word) as TextView
        val speak = dialogView.findViewById<View>(R.id.wd_speak) as ImageView
        val verbDesc = dialogView.findViewById<View>(R.id.wd_verb_dec) as TextView
        val nounDesc = dialogView.findViewById<View>(R.id.wd_noun_dec) as TextView
        val nextBtn = dialogView.findViewById<Button>(R.id.wd_next_question)
        val moreInfo = dialogView.findViewById<Button>(R.id.wd_more_info)
        val moreList = dialogView.findViewById<ListView>(R.id.wd_description)

        wordDesc.setView(dialogView)
        word.typeface = HelloApp.IRANSANS
        verbDesc.typeface = HelloApp.IRANSANS
        nextBtn.typeface = HelloApp.IRANSANS
        moreInfo.typeface = HelloApp.IRANSANS

        moreList.visibility = View.GONE


        moreInfo.setOnClickListener {

            var nwTrans = khttp.get(DESC_TRANS + wordToCheck)

            if (nwTrans.statusCode == 200) {
                var res = nwTrans.jsonArray
//                Log.e("rmean", "res LEN" + res.length())
                var meaningList: ArrayList<WordTransObject?> = ArrayList(res.length())
                for (i in 0 until res.length()) {
                    var row = res.get(i) as JSONObject
                    var mean = WordTransObject(row.getString("type"), row.getString("definition"), row.getString("example"))
//                    Log.e("rmean", mean.toString())
                    meaningList.add(i, mean)
                }

                Log.e("rmean", wordToCheck + "\n" + res.toString() + "\n" + meaningList.toString())
                var adapter = listObjAdapter(applicationContext, meaningList)

                moreList.adapter = adapter
                moreList.deferNotifyDataSetChanged()
                moreList.visibility = View.VISIBLE
            } else {
                Log.d("RESULT", nwTrans.text)
            }
        }

        nextBtn.setOnClickListener {
            wordDesc.dismiss()
        }
        nounDesc.typeface = HelloApp.IRANSANS
        word.text = w
        speak.setOnClickListener {
            SpeakOut(sound)
//                sToast(applicationContext, "Play Pronunciation Of $sound").show()
        }
        verbDesc.text = verb
        nounDesc.text = noun

        speak.setOnClickListener { SpeakOut(w) }


        wordDesc.setOnDismissListener {
            wordDesc.cancel()
            SetAnswer(false)
        }
        wordDesc.show()

    }

    fun Timer(time: Int): CountDownTimer {
        var input = time.toLong() * 1000

        var re = object : CountDownTimer(input, 1000) { // adjust the milli seconds here

            override fun onTick(millisUntilFinished: Long) {
                var m = ((millisUntilFinished / 1000) / 60).toInt()
                var s = ((millisUntilFinished / 1000) - (m * 60)).toInt()
                if (m < 10 && s < 10) {
                    nq_time.text = "0" + m + ":0" + s
                } else if (s < 10) {
                    nq_time.text = "" + m + ":0" + s
                } else if (m < 10) {
                    nq_time.text = "0" + m + ":" + s
                } else {
                    nq_time.text = "" + m + ":" + s
                }
            }

            override fun onFinish() {
                CheckAnswer(0)
                nq_time.text = "00:00"
            }

        }

        return re
    }

    fun GetQuestion(): Map<String, String> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var userid = SPref(applicationContext, "userCreds")!!.getString("userId", null)
        var level = SPref(applicationContext, "level")!!.getInt("level", 0)

        var noQuest = mapOf<String, String>("q" to "elect", "ans1" to "برتر انگاشته", "ans2" to "ابکی", "ans3" to "اب", "ans4" to "آفتاب", "ansTrue" to "ans1")
        var strLevel = SRV_LEVEL_DETECTOR(level)
        Log.d("RESULT", mapOf("m" to "getQuestion", "phone" to userid, "level" to strLevel).toString())
        if (strLevel != 0.toString()) {
            var getQuest = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "getQuestion", "phone" to userid, "level" to strLevel))
            Log.d("RESULT", getQuest.toString())
            if (getQuest.statusCode == 200) {
                var jes = getQuest.jsonObject
                Log.d("RESULT", jes.toString())
                if (jes.getBoolean("result")) {
                    var listing = RanArray()
                    return mapOf<String, String>(
                            "res" to (if (jes.getString("quest_id") != jes.getString("question")) "true" else "false"),
                            "qid" to jes.getString("quest_id"),
                            "q" to jes.getString("question"),
                            listing[0] to jes.getString("trueanswer"),
                            listing[1] to jes.getString("wrong1"),
                            listing[2] to jes.getString("wrong2"),
                            listing[3] to jes.getString("wrong3"),
                            "ansTrue" to listing[0],
                            "trues" to jes.getString("count_true"),
                            "wrongs" to jes.getString("count_wrongs")
                    )
                } else {
                    Log.d("REQ_RES", "ERROR IN SERVER DATA")
                }
            } else {
                Log.d("Server-Error ", getQuest.statusCode.toString())
            }
        }
        return noQuest
    }

    fun SetAnswer(Answer: Boolean = true) {
        var set = true
        if (Answer) {
            rightAnswers++
            SPref(applicationContext, "quest_stat")!!.edit().putInt("RIGHT_ANS", rightAnswers).apply()
        } else {
            wrongAnswers++
            SPref(applicationContext, "quest_stat")!!.edit().putInt("WRONG_ANS", wrongAnswers).apply()
        }

        var con = isNetworkAvailable(this)
        if (con) {
            /*
            if ((rightAnswers + wrongAnswers) > 0 && (rightAnswers + wrongAnswers) % 3 == 0) {

                val userId = SPref(this, "userCreds")?.getString("userId", null)

                var subStat: Boolean = SPref(applicationContext, "subStat")!!.getBoolean("stat", false)
                var userCheck = UserCheck(userId.toString(), this).execute().get()
                if (!userCheck || !subStat) {
                    set=false
                    InQuestSub()
                }

            }
            if (set) {
            */
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var userId = SPref(applicationContext, "userCreds")!!.getString("userId", 0.toString())
            var level = SPref(applicationContext, "level")!!.getInt("level", 0)
            var qid = SPref(applicationContext, "runQuest")!!.getInt("qid", 0)
            var ansStat = if (Answer) 1 else 2
            if (userId != 0.toString() && level != 0 && qid != 0) {

                var setAnswer = khttp.post(SERVER_ADDRESS, data = mapOf<String, String>("m" to "setanswer", "phone" to userId.toString(), "level" to SRV_LEVEL_DETECTOR(level).toString(), "qid" to qid.toString(), "ans" to ansStat.toString()))
                Log.d("REQ_ANSWER", setAnswer.text)

                if (setAnswer.statusCode == 200) {
                    try {
                        var jes = setAnswer.jsonObject
                        if (jes.getBoolean("result")) {
                            QuestLoader()
                        } else {
                            sToast(applicationContext, "SENT $userId $qid")
                            SetAnswer(Answer)
                        }

                    } catch (e: Exception) {
                        Log.d("SetAns", e.message.toString())
                    }

                }
            }
//            }else{
//                TimeControl()
//            }
        } else {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }

    }

    /*
        fun InQuestSub() {

            val iqs = AlertDialog.Builder(this).create()
            val dialogView = layoutInflater.inflate(R.layout.quest_continue, null)
            val label = dialogView.findViewById<TextView>(R.id.qc_lable)
            val qCont = dialogView.findViewById<Button>(R.id.qc_continue)
            val qExit = dialogView.findViewById<Button>(R.id.qc_exit)

            label.typeface = HelloApp.IRANSANS
            qCont.typeface = HelloApp.IRANSANS
            qExit.typeface = HelloApp.IRANSANS

            qCont.setOnClickListener {
                // launch sub action
                var ana = Ana(applicationContext)
                ana.InAppSubRequest(SPref(applicationContext,"userCreds")!!.getString("userId",null))
                var sub = Intent(this, SplashPage::class.java)
                iqs.dismiss()
                startActivity(sub)
                finish()
            }

            qExit.setOnClickListener {
                // limit user so then Quest only Avaliable through subscribtion

                iqs.dismiss()
                finish()
            }

            iqs.setView(dialogView)
            SPref(applicationContext, "iqs")!!.edit().putBoolean("asked", true).commit()
            iqs.show()

        }
    */
    inner class WordTransObject(type: String, meaning: String, eg: String) {
        var T = type
        var D = meaning
        var E = eg
        override fun toString(): String {
            return "WordTransObject(T='$T', D='$D', E='$E')"
        }


    }

    inner class listObjAdapter(private val context: Context, private val dataSource: ArrayList<WordTransObject?>) : BaseAdapter() {
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val listLay = inflater.inflate(R.layout.translation_list_item, null)
            val type = listLay.findViewById<TextView>(R.id.trlist_type)
            val mean = listLay.findViewById<TextView>(R.id.trlist_mean)
            val eg = listLay.findViewById<TextView>(R.id.trlist_eg)
            try {
                var item = dataSource.get(p0)
                mean.text = android.text.Html.fromHtml("<em>Def: </em>" + item!!.D)
                eg.text = android.text.Html.fromHtml("<em>eg.: " + item.E + "</em>")
                type.text = android.text.Html.fromHtml(item.T)
            } catch (e: Exception) {
                Log.e("LIST", e.message)
            }
            return listLay
        }

        override fun getItem(p0: Int): WordTransObject? {
            return dataSource.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return dataSource.size
        }

    }

    class UserCheck(var inp: String = "-1", var context: Context) : AsyncTask<String, Boolean, Boolean>() {
        override fun doInBackground(vararg input: String?): Boolean {

            var checkUser = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "checkUser", "phone" to inp))
            if (checkUser.statusCode == 200) {
                return try {
                    val jes = checkUser.jsonObject
                    Log.d("uchx", jes.toString())
                    if (jes.getBoolean("result") && jes.getString("status").equals("sub")) {
                        SPref(context, "subStat")!!.edit().putBoolean("stat", true).commit()
                        true
                    } else {
                        SPref(context, "subStat")!!.edit().putBoolean("stat", false).commit()
                        false
                    }

                } catch (e: Exception) {
                    Log.d("Err", e.message)
                    SPref(context, "subStat")!!.edit().putBoolean("stat", false).commit()
                    false
                }
            } else {
                val intent = Intent(context, NoConnection::class.java)
                context.startActivity(intent)
            }
            return false
        }

    }

}
