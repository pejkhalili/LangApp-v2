package com.chapdast.ventures.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.StrictMode
import android.speech.tts.TextToSpeech
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.review_loader.*
import kotlinx.android.synthetic.main.vw_timer.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Review : ChapActivity(), TextToSpeech.OnInitListener {
    var locked = false
    var timeRun = false
    var remainTime: Int = 0
    var tim: CountDownTimer? = null
    private var currentQuest = 0
    private var ChallengeCount: Int = 20
    private var timeForEachChallenge = 20
    private var trueAns: String? = null
    var temp: Any = ""
    var tts: TextToSpeech? = null
    private var LoadedReview: Array<MutableMap<String, String>?>? = null
    var allWrong = 0
    var allRight = 0
    var speaker: Boolean = true
    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_review)
            speaker = SPref(applicationContext, "setting")!!.getBoolean("speaker", true)

            val net = isNetworkAvailable(this)
            if (!net) {
                val noCon = Intent(this, NoConnection::class.java)
                startActivity(noCon)
                finish()
            } else {
                GetQuestion()
                timeForEachChallenge = SPref(this, "level")!!.getInt("timeOnChalleng", 20)
                ChallengeCount = SPref(this, "level")!!.getInt("numChallenge", 20)
                tts = TextToSpeech(applicationContext, this)

                vw_ans_st.typeface = HelloApp.IRANSANS
                vw_ans_nd.typeface = HelloApp.IRANSANS
                vw_ans_rd.typeface = HelloApp.IRANSANS
                vw_ans_th.typeface = HelloApp.IRANSANS
                vw_ans_un.typeface = HelloApp.IRANSANS
                vw_timer_time.typeface = HelloApp.BEBAS_FONT
                vw_timer_time.textSize = 35F

                if (LoadedReview != null) {
                    QuestLoader()
                } else {
                    End()
                }
                rv_side_menu.setOnClickListener {
                    tim?.cancel()
                    finish()
                }
                vw_unknown.setOnClickListener { CheckAnswer(0) }
                vw_first_answer.setOnClickListener { CheckAnswer(1) }
                vw_second_answer.setOnClickListener { CheckAnswer(2) }
                vw_third_answer.setOnClickListener { CheckAnswer(3) }
                vw_fourth_answer.setOnClickListener { CheckAnswer(4) }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            tts!!.setPitch(1.3f)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS_SRV", "SOUND PROBLEM")
            }
        } else {
            Log.e("TTS_SRV", "INIT FAILED!!")
        }
    }

    @Suppress("DEPRECATION")
    fun SpeakOut(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null)

    }

    override fun onBackPressed() {
        if (tim != null) {
            tim!!.cancel()
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onBackPressed()
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

    @SuppressLint("SetTextI18n")
    fun QuestLoader() {
        if (currentQuest <= LoadedReview!!.lastIndex) {
            val Load = Helper(currentQuest)
            val quest = Load

            val resQuest = quest?.get("res").toString()

            if (quest != null && resQuest == "true") {

                try {
                    vw_count.text = (currentQuest + 1).toString() + "/" + (LoadedReview!!.lastIndex + 1).toString()

//                    currentQuest = currentQuest!! + 1

                    temp = SPref(applicationContext, "runReview")!!.edit().putInt("qid", quest.get("qid")!!.toInt()).commit()


//                    sToast(applicationContext, quest.get("res").toString())
                    val questInTitle = quest.get("q").toString().toUpperCase()
                    rv_quest_num.text = "#" + currentQuest.toString() + " " + questInTitle




                    vw_word.text = quest.get("q").toString().toUpperCase()
                    vw_ans_st.text = quest.get("ans1")
                    vw_ans_nd.text = quest.get("ans2")
                    vw_ans_rd.text = quest.get("ans3")
                    vw_ans_th.text = quest.get("ans4")
                    vw_timer_right_times.text = quest.get("trues")
                    vw_timer_wrong_times.text = quest.get("wrongs")
                    vw_all_timer_right_times.text = allRight.toString()
                    vw_all_timer_wrong_times.text = allWrong.toString()
                    vw_speak.setOnClickListener {
                        val word = vw_word.text.toString()
                        SpeakOut(word)
                    }
                    handler.postDelayed({
                        if (speaker) SpeakOut(vw_word.text.toString())
                    }, 200)


                    trueAns = quest.get(quest.get("ansTrue")!!)
                    if (tim != null) {
                        tim!!.cancel()
                    }
                    tim = Timer(timeForEachChallenge).start()
                    vw_timer_pause_btn.setOnClickListener {
                        TimeControl()
                    }

                } catch (e: Exception) {
                    //                Log.d("Err",e.message)
                }

            } else {
                Log.d("Err-RESQUEST", resQuest)
                if (tim != null) {
                    tim!!.cancel()
                }
                temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", 1).commit()
                finish()
            }
            currentQuest++
        } else {
            if (tim != null) {
                tim!!.cancel()
            }
//            var time: Long = (Calendar.getInstance().timeInMillis / 1000).toString().toLong()
            val intent = Intent(this, QuestFinish::class.java)
            intent.putExtra("hide_RWS", true)
            startActivity(intent)
            finish()
        }
    }

    fun CheckAnswer(ans: Int) {
        if (locked) {
            sToast(applicationContext, getString(R.string.setAnswerWait), true)
        } else {
            locked = true
            var userAnswer: String? = null

            when (ans) {
                1 -> userAnswer = vw_ans_st.text.toString()
                2 -> userAnswer = vw_ans_nd.text.toString()
                3 -> userAnswer = vw_ans_rd.text.toString()
                4 -> userAnswer = vw_ans_th.text.toString()
            }

            if (userAnswer == trueAns && ans != 0) {
                //send true ans to server
//                Toast.makeText(applicationContext, "True Answer", Toast.LENGTH_SHORT).show()
                SetAnswer()
            } else {

                WordShow(vw_word.text.toString(), "", trueAns.toString(), "")
            }
            locked = false
        }

    }

    @Suppress("DEPRECATION")
    fun TimeControl() {
        if (timeRun) {
            vw_timer_pause_btn.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))

            tim = Timer(remainTime).start()

            Toast.makeText(applicationContext, "Started", Toast.LENGTH_SHORT).show()
            rv_questLoader.visibility = View.VISIBLE
            timeRun = false
        } else {
            vw_timer_pause_btn.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))
            rv_questLoader.visibility = View.GONE
            val remTime = vw_timer_time.text.toString()
            val min = remTime.substring(0, 2).toInt()
            val sec = remTime.substring(3, 5).toInt()
            val r: Int = min * 60 + sec
            remainTime = r
            tim?.cancel()
            Toast.makeText(applicationContext, "Paused", Toast.LENGTH_SHORT).show()
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
        wordDesc.setView(dialogView)
        val word = dialogView.findViewById<View>(R.id.wd_word) as TextView
        word.typeface = HelloApp.IRANSANS
        val speak = dialogView.findViewById<View>(R.id.wd_speak) as ImageView
        val verbDesc = dialogView.findViewById<View>(R.id.wd_verb_dec) as TextView
        verbDesc.typeface = HelloApp.IRANSANS
        val nounDesc = dialogView.findViewById<View>(R.id.wd_noun_dec) as TextView
        val nextBtn = dialogView.findViewById<Button>(R.id.wd_next_question)
        val moreInfo = dialogView.findViewById<Button>(R.id.wd_more_info)
        val moreList = dialogView.findViewById<ListView>(R.id.wd_description)
        nextBtn.typeface = HelloApp.IRANSANS
        moreInfo.typeface = HelloApp.IRANSANS

        moreList.visibility = View.GONE


        moreInfo.setOnClickListener {

            val nwTrans = khttp.get(DESC_TRANS + wordToCheck)

            if (nwTrans.statusCode == 200) {
                val res = nwTrans.jsonArray
                val meaningList: ArrayList<WordTransObject?> = ArrayList(res.length())
                for (i in 0 until res.length() ) {
                    val row = res.get(i) as JSONObject
                    val mean = WordTransObject(row.getString("type"),row.getString("definition"),row.getString("example"))
                    meaningList.add(i,mean)
                }
                Log.d("RESULT", res.toString())
                val adapter = listObjAdapter(applicationContext, meaningList)

                moreList.adapter = adapter
                moreList.deferNotifyDataSetChanged()
                moreList.visibility = View.VISIBLE
            }
        }
        nextBtn.setOnClickListener {
            wordDesc.dismiss()
        }
        nounDesc.typeface = HelloApp.IRANSANS
        word.text = w
        speak.setOnClickListener {
            Toast.makeText(applicationContext, "Play Pronunciation Of $sound", Toast.LENGTH_SHORT).show()
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
        val input = time.toLong() * 1000

        val re = object : CountDownTimer(input, 1000) { // adjust the milli seconds here

            override fun onTick(millisUntilFinished: Long) {
                val m = ((millisUntilFinished / 1000) / 60).toInt()
                val s = ((millisUntilFinished / 1000) - (m * 60)).toInt()
                if (m < 10 && s < 10) {
                    vw_timer_time.text = "0" + m + ":0" + s
                } else if (s < 10) {
                    vw_timer_time.text = "" + m + ":0" + s
                } else if (m < 10) {
                    vw_timer_time.text = "0" + m + ":" + s
                } else {
                    vw_timer_time.text = "" + m + ":" + s
                }
            }

            override fun onFinish() {
                CheckAnswer(0)
                vw_timer_time.text = "00:00"
            }

        }

        return re
    }

    fun GetQuestion() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val userid = SPref(applicationContext, "userCreds")!!.getString("userId", null)
        val level = SPref(applicationContext, "level")!!.getInt("level", 0)
        val countQuestion = SPref(applicationContext, "level")!!.getInt("numChallenge", 20)

//        var noQuest =arrayOf(mapOf<String,String>("q" to "elect","ans1" to "برتر انگاشته" ,"ans2" to "ابکی" ,"ans3" to "اب" ,"ans4" to "آفتاب","ansTrue" to "ans1"))
        val strLevel = SRV_LEVEL_DETECTOR(level)
        if (strLevel != 0.toString()) {

            val reviews = khttp.post(SERVER_ADDRESS, data = mapOf<String, Any>("m" to "review", "phone" to userid, "level" to SRV_LEVEL_DETECTOR(level).toString(), "count" to countQuestion))

            if (reviews.statusCode == 200) {
                val result = reviews.jsonObject
                Log.d("mk", result.toString())
                Log.d("RESU", result.toString())
                if (result.getBoolean("result") && result.getInt("count_items") > 0) {
                    try {

                        val reviewables = result.getJSONArray("items")

                        val count = reviewables.length()
                        Log.d("JSON", "3-->" + count)
                        val LoadedReviews = arrayOfNulls<MutableMap<String, String>>(count)
                        var i = 0
                        while (i < count) {
                            val jes = reviewables.getJSONObject(i)


                            Log.d("JSON", "4--->" + jes.getString("question").toString())
                            val listing = RanArray()

                            LoadedReviews[i] = mutableMapOf<String, String>(
                                    "res" to (if (jes.getString("quest_id") != jes.getString("question")) "true" else "false"),
                                    "qid" to jes.getString("quest_id"),
                                    "q" to jes.getString("question"),
                                    listing[0] to jes.getString("trueanswer"),
                                    listing[1] to jes.getString("wrong1"),
                                    listing[2] to jes.getString("wrong2"),
                                    listing[3] to jes.getString("wrong3"),
                                    "ansTrue" to listing[0],
                                    "trues" to jes.getString("count_true"),
                                    "wrongs" to jes.getString("count_wrong")
                            )
                            i++
                        }
                        LoadedReview = LoadedReviews
                    } catch (E: JSONException) {

                    }
                }

            } else {
                Log.d("Server-Error ", reviews.statusCode.toString())
            }
        }
    }

    fun Helper(Index: Int): MutableMap<String, String>? {
        return LoadedReview?.get(Index)
    }

    fun SetAnswer(Answer: Boolean = true) {

        val con = isNetworkAvailable(this)
        if (con) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val userId = SPref(applicationContext, "userCreds")!!.getString("userId", 0.toString())
            val level = SPref(applicationContext, "level")!!.getInt("level", 0)
            val qid = SPref(applicationContext, "runReview")!!.getInt("qid", 0)
            val ansStat = if (Answer) 1 else 2
            if (userId != 0.toString() && level != 0 && qid != 0) {

                val setAnswer = khttp.post(SERVER_ADDRESS, data = mapOf<String, String>("m" to "setanswer", "phone" to userId, "level" to SRV_LEVEL_DETECTOR(level).toString(), "qid" to qid.toString(), "ans" to ansStat.toString()))
                Log.d("REQ_ANSWER", setAnswer.text)

                if (setAnswer.statusCode == 200) {
                    try {
                        val jes = setAnswer.jsonObject
                        if (jes.getBoolean("result")) {
                            if (Answer) allRight++ else allWrong++
                            QuestLoader()
                        } else {
//                            sToast(applicationContext, "SENT $userId $qid")
                            SetAnswer(Answer)
                        }

                    } catch (e: Exception) {
                        Log.d("SetAns", e.message.toString())
                    }

                }
            }
        } else {
            val noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }

    }

    fun End() {
//        sToast(applicationContext,applicationContext.resources.getString(R.string.no_reviews))
        sToast(applicationContext, applicationContext.resources.getString(R.string.noReviewAvalable))
        finish()

    }

    inner class WordTransObject(type: String, meaning: String, eg: String) {
        var T = type
        var D = meaning
        var E = eg
    }

    inner class listObjAdapter(context: Context, private val dataSource: ArrayList<WordTransObject?>) : BaseAdapter() {
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @Suppress("DEPRECATION")
        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val listLay = inflater.inflate(R.layout.translation_list_item, null)
            val type = listLay.findViewById<TextView>(R.id.trlist_type)
            val mean = listLay.findViewById<TextView>(R.id.trlist_mean)
            val eg = listLay.findViewById<TextView>(R.id.trlist_eg)
            try {
                val item = dataSource.get(p0)
                mean.text = android.text.Html.fromHtml("<em>Def: </em>" + item!!.D)
                eg.text = android.text.Html.fromHtml("<em>eg.: " + item.E+"</em>")
                type.text = android.text.Html.fromHtml( item.T)
            }catch(e:Exception){
                Log.e("LIST",e.message)
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
}
