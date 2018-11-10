package com.chapdast.ventures.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.TextView
import com.chapdast.ventures.Adapters.MediaAdapter
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.Objects.Media
import com.chapdast.ventures.Objects.Podcast
import com.chapdast.ventures.Objects.Video
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_media_loader.*
import org.json.JSONObject


class MediaLoader : ChapActivity() {

    //    private var type = 0
    private var cid = 0.toString()
    private lateinit var mediaList: ArrayList<Media>
    private lateinit var mediaListVideos: ArrayList<Media>
    private lateinit var mediaListPodcast: ArrayList<Media>
    private lateinit var mediaListView: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var videosAdapter: MediaAdapter
    private lateinit var podcastAdapter: MediaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var pg: ProgressDialog
    val handler = Handler()

    override fun onResume() {
        super.onResume()
        ENV.current_context = applicationContext
        ENV.current_activity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_media_loader)
            mlFilter.visibility = View.GONE
            ENV.current_activity = this
            ENV.current_context = applicationContext
            if (!isNetworkAvailable(applicationContext)) {
                var noCon = Intent(this, NoConnection::class.java)
                startActivity(noCon)
                finish()
            }

            pg = ProgressDialog(this)

            mlBackBtn.setOnClickListener {
                finish()
            }
            mlTopText.typeface = HelloApp.IRANSANS

            val backBtn = findViewById<ImageView>(R.id.mlBackBtn) as ImageView
            val title = findViewById<TextView>(R.id.mlTopText) as TextView
            val topImage = findViewById<ImageView>(R.id.mlTopImage) as ImageView

            viewManager = LinearLayoutManager(this)
            mediaList = ArrayList<Media>()
            mediaListVideos = ArrayList<Media>()
            mediaListPodcast = ArrayList<Media>()

            mediaAdapter = MediaAdapter(mediaList, this)
            videosAdapter = MediaAdapter(mediaListVideos, this)
            podcastAdapter = MediaAdapter(mediaListPodcast, this)

            if (intent.extras != null) {
//            type = intent.extras.getInt("type")
                cid = intent.extras.getInt("id").toString()
            }
//        Log.d("Media","type: $type")
//        mlFilter.setSelection(type)

            mediaListView = findViewById<RecyclerView>(R.id.mlMediaList)
            mediaListView.setHasFixedSize(true)
            mediaListView.layoutManager = viewManager

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                setupPermissions()
            } else {
                GetMedia().execute()
            }
            mediaListView.adapter = mediaAdapter
            mlFilter.onItemSelectedListener = ItemSelected()
            mediaAdapter.notifyDataSetChanged()
        }
    }

    inner class ItemSelected : OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            Log.d("ITEM", "IN LISTENER")
            when (p2) {
                0 -> {

                    mediaListView.swapAdapter(mediaAdapter, false)
                    Log.d("ITEM", "IN LISTENER>>>>ALl")
                    return
                }
                1 -> {
                    mediaListView.swapAdapter(videosAdapter, false)
                    Log.d("ITEM", "IN LISTENER>>>>Videos")
                    return
                }
                2 -> {
                    mediaListView.swapAdapter(podcastAdapter, false)
                    Log.d("ITEM", "IN LISTENER>>>>Podcasts")
                    return
                }
            }

        }

    }

    inner class GetMedia : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg strings: String): String? {
//            val loadedLevel = SPref(applicationContext, "level")!!.getInt("level", 1)
//            val level = Vars.ServerLevelDetector(loadedLevel)
            val userId = SPref(applicationContext, "userCreds")!!.getString("userId", 0.toString())

            var getAllMedia = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "getMediaByCategory", "phone" to userId, "catid" to cid))
            Log.d("Media", ">>>>>>" + getAllMedia.jsonObject.toString())

            if (getAllMedia.statusCode == 200) {
                var jes = getAllMedia.jsonObject
                if (jes.getBoolean("result") && jes.getInt("items_count") > 0) {
                    var getMediaItems = jes.getJSONArray("items")

                    var counter = 0
                    var vCount = 0
                    var pCount = 0
                    while (counter < getMediaItems.length()) {
                        var item = getMediaItems.get(counter) as JSONObject
                        if (item.getString("type").equals("video")) { // if media is Video
                            mediaList.add(counter,
                                    Video(item.getString("thumbnail"), item.getString("stream_link"), item.getString("name"), item.getString("level"), "no", item.getInt("mid"))
                            )
                            mediaListVideos.add(vCount,
                                    Video(item.getString("thumbnail"), item.getString("stream_link"), item.getString("name"), item.getString("level"), "no", item.getInt("mid"))
                            )
                            vCount += 1
                        } else if (item.getString("type").equals("podcast")) {
                            mediaList.add(counter,
                                    Podcast(item.getString("thumbnail"), item.getString("stream_link"), item.getString("name"), item.getString("level"), "no", item.getInt("mid"))
                            )
                            mediaListPodcast.add(pCount,
                                    Podcast(item.getString("thumbnail"), item.getString("stream_link"), item.getString("name"), item.getString("level"), "no", item.getInt("mid"))
                            )
                            pCount += 1
                        }

                        handler.post(Runnable {
                            mediaAdapter.notifyItemInserted(counter)
                        })
                        counter++


                    }
                }
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            //            pg.setCancelable(false)
            pg.setMessage(resources.getString(R.string.pleaseWait))
            pg.show()


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            when(type){
//                1->{
//                    Log.d("Media","videosLoaded")
//                    mediaListView.swapAdapter(videosAdapter,true)
//
//                }
//                2->{
//                    Log.d("Media","podcastAdapter")
//                    mediaListView.swapAdapter(podcastAdapter, true)
//
//                }
//            }

            mediaAdapter.notifyDataSetChanged()
            if (pg != null && pg.isShowing) {
                pg.dismiss()
                pg.cancel()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupPermissions() {
        val permission = applicationContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")
        val permission2 = applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE")
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            GetMedia().execute()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"),
                STRG_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            STRG_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("SMS_PRR", "Permission has been denied by user")
                    makeRequest()
                } else {
                    Log.i("SMS_PRR", "Permission has been granted by user")
                    GetMedia().execute()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (pg.isShowing) {
            pg.dismiss()
            pg.cancel()

        }

    }

}
