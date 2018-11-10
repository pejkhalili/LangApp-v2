package com.chapdast.ventures.activities

import android.app.ProgressDialog
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
import com.chapdast.ventures.Adapters.CategoriesAdapter
import com.chapdast.ventures.ChapActivity
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.HelloApp
import com.chapdast.ventures.Objects.CategoriesMedia
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_media_categories.*
import org.json.JSONObject

class MediaCategories : ChapActivity() {

    lateinit var pg: ProgressDialog

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var categories: ArrayList<CategoriesMedia>
    private lateinit var categoriesListView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_media_categories)

            ENV.current_activity = this
            ENV.current_context = applicationContext


            pg = ProgressDialog(this)


            clTopText.typeface = HelloApp.IRANSANS
            clBackBtn.setOnClickListener {
                finish()
            }

//        val backBtn = findViewById<ImageView>(R.id.clBackBtn) as ImageView
//        val title = findViewById<TextView>(R.id.clTopText) as TextView
//        val topImage = findViewById<ImageView>(R.id.clTopImage) as ImageView

            viewManager = LinearLayoutManager(this)

            categories = ArrayList<CategoriesMedia>()
            categoriesAdapter = CategoriesAdapter(categories, applicationContext)
            categoriesListView = findViewById<RecyclerView>(R.id.clMediaList)

//        categoriesListView.hasFixedSize(true)

            categoriesListView.layoutManager = viewManager

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                setupPermissions()
            } else {
                GetCategories().execute()
            }

            categoriesListView.adapter = categoriesAdapter
            categoriesAdapter.notifyDataSetChanged()
        }
    }

    inner class GetCategories : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            //            pg.setCancelable(false)
            pg.setMessage(resources.getString(R.string.pleaseWait))
            pg.show()

        }

        override fun doInBackground(vararg strings: String): String? {
            val loadedLevel = SPref(ENV.current_context, "level")!!.getInt("level", 1)
            val level = Vars.ServerLevelDetector(loadedLevel)
            val userId = SPref(ENV.current_context, "userCreds")!!.getString("userId", 0.toString())

            var getCategories = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "getCat", "phone" to userId, "level" to level))
            Log.d("CATS", ">>>>>>" + getCategories.jsonObject.toString())

            if (getCategories.statusCode == 200) {
                var jes = getCategories.jsonObject
                if (jes.getBoolean("result") && jes.getInt("items_count") > 0) {
                    var getCategoriesItems = jes.getJSONArray("items")
                    var counter = 0
                    while (counter < getCategoriesItems.length()) {
                        var item = getCategoriesItems.get(counter) as JSONObject

                        categories.add(counter,
                                CategoriesMedia(item.getString("thumbnail"), item.getString("name"), item.getInt("id"))
                        )
                        handler.post(Runnable {
                            categoriesAdapter.notifyItemInserted(counter)
                        })
                        counter++


                    }
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {

            categoriesAdapter.notifyDataSetChanged()
            super.onPostExecute(result)
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
            GetCategories().execute()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"),
                CAT_STRG_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAT_STRG_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("SMS_PRR", "Permission has been denied by user")
                    makeRequest()
                } else {
                    Log.i("SMS_PRR", "Permission has been granted by user")
                    GetCategories().execute()
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

    override fun onResume() {
        super.onResume()
        ENV.current_context=applicationContext
        ENV.current_activity = this
    }

}
