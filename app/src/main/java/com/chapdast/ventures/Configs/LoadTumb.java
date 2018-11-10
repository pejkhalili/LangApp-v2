package com.chapdast.ventures.Configs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadTumb extends AsyncTask<String,String,Bitmap> {
    final ImageView img;
    final Context context;

    public LoadTumb(ImageView img,Context context){
        this.context = context;
        this.img=img;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        Bitmap mIcon = null;
        String fileName = strings[2].trim().replace(" ",".");
        File mediaDir = context.getExternalFilesDir("/media/");
        if(!mediaDir.exists()){
            Log.e("FILE", "MEDIA DIR CREATE " + mediaDir.mkdir() );
        }

        File thumbPath = new File (mediaDir.getPath(),fileName);
        if(thumbPath.exists()){
            Log.e("Load From storage: ",fileName + ">>> OK");
            return BitmapFactory.decodeFile(thumbPath.getPath());
        }else {
            try {
                String url = strings[0] + strings[1];
                thumbPath.createNewFile();
                FileOutputStream fos = new FileOutputStream(thumbPath);

                URL Url = new URL(url);
                HttpURLConnection c = (HttpURLConnection) Url.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer,0,len);
                }
                mIcon = BitmapFactory.decodeFile(thumbPath.getPath());
                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                Log.e("ERR_IMG", e.getMessage());
            } catch (IOException e) {
                Log.e("ERR_IMG", e.getMessage());

                e.printStackTrace();
            }
        }
        return mIcon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(500);
        aa.setStartOffset(1L);
        aa.setFillAfter(true);


        img.setImageBitmap(bitmap);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.startAnimation(aa);
        super.onPostExecute(bitmap);
    }
}
