package com.chapdast.ventures.Objects;

import android.util.Log;

import com.chapdast.ventures.Configs.ENV;
import com.chapdast.ventures.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TranslationObject {
    private String word = "";
    private String translation = "\n";
    private boolean rtl = false;

    public boolean isRtl() {
        return rtl;
    }

    public TranslationObject(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public TranslationObject(String word, JSONArray translation) {
        this.word = word.toUpperCase();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < translation.length(); i++) {
            try {
                JSONObject mean = translation.getJSONObject(i);
                sb.append(mean.getString("type").equals("null") ? "" : "<span style= 'color:#0000FF'><small><strong>" + mean.getString("type").toUpperCase() + "</strong></small></span><br>");
                sb.append(mean.getString("definition").equals("null") ? "" : "<span style= 'color:#FF535353'><strong>" + mean.getString("definition") + "</strong></span> <br>");
                sb.append(mean.getString("example").equals("null") ? "" : "<span style= 'color:#131313'><em><strong>Example: </strong>" + mean.getString("example") + "</em></span><br><br>");

            } catch (JSONException e) {
                Log.e("ERR-Mean", e.getMessage());
            }
        }
        this.translation = sb.length() > 8 ? sb.toString().substring(0, sb.length() - 8) : ENV.current_context.getResources().getString(R.string.noTranslationFound_en);

    }

    public TranslationObject(String word, JSONObject translation) {
        try {
            this.word = word.toUpperCase();
            this.rtl = translation.getString("lang").equals("en-fa");
            String text = translation.getString("text");

            if (this.word.equals(text.substring(2, text.length() - 2).toUpperCase()) ) {
                if (this.rtl) {
                    this.translation = ENV.current_context.getResources().getString(R.string.noTranslationFound_fa);
                } else {
                    this.translation = ENV.current_context.getResources().getString(R.string.noTranslationFound_en);
                }
            } else {
                this.translation = text.substring(2, text.length() - 2).toUpperCase();
            }


        } catch (JSONException e) {
            Log.d("ERR-Mean", e.getMessage());
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
