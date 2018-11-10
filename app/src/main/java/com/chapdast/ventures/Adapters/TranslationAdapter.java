package com.chapdast.ventures.Adapters;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chapdast.ventures.Objects.TranslationObject;
import com.chapdast.ventures.R;
import java.util.ArrayList;


public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.TranslationViewHolder>{

    private ArrayList<TranslationObject> wordList;
    private Context context;
    private Typeface typeface;
    private Typeface typefaceBold;




    public static class TranslationViewHolder extends RecyclerView.ViewHolder{
        public View v;
        public TextView word;
        public TextView translation;

        public TranslationViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.word = v.findViewById(R.id.history_word);
            this.translation = v.findViewById(R.id.history_translation);
        }
    }

    public TranslationAdapter(ArrayList<TranslationObject> wordList, Context ctx){
        this.context = ctx;
        this.wordList = wordList;
        this.typeface = Typeface.createFromAsset(context.getAssets(),"fonts/iransans.ttf");
        this.typefaceBold = Typeface.createFromAsset(context.getAssets(),"fonts/iranblack.ttf");
    }

    @Override
    public TranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.translation_entry,parent,false);
        Log.d("MK_VT", "ViewType is " + viewType);


        return new TranslationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TranslationViewHolder holder, int position) {
        final TranslationObject trObj = wordList.get(position);


        if( trObj.isRtl() ) {
            holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.translation.setTextSize(18F);
        }else holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        holder.translation.setTypeface(typeface);
        holder.word.setTypeface(typefaceBold);

        holder.word.setText(android.text.Html.fromHtml(trObj.getWord()));
        holder.translation.setText(android.text.Html.fromHtml(trObj.getTranslation()));

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO -- add words to liting
            }
        });

    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }


}
