package com.chapdast.ventures.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chapdast.ventures.Configs.ENV;
import com.chapdast.ventures.Configs.LoadTumb;
import com.chapdast.ventures.Objects.CategoriesMedia;
import com.chapdast.ventures.R;
import com.chapdast.ventures.activities.MediaLoader;

import java.util.ArrayList;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CatViewHolder>{

    private ArrayList<CategoriesMedia> mediaList;
    private Context context;
    private Typeface typeface;




    public static class CatViewHolder extends RecyclerView.ViewHolder{
        public View v;
        public ImageView mediaThumb;
        public TextView mediaTitle;



        public CatViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.mediaThumb = v.findViewById(R.id.clwThumb);
            this.mediaTitle = v.findViewById(R.id.clwTitle);

        }
    }

    public CategoriesAdapter(ArrayList<CategoriesMedia> mediaList,Context ctx){
        this.context = ctx;
        this.mediaList = mediaList;
        this.typeface = Typeface.createFromAsset(context.getAssets(),"fonts/bebas.otf");
    }

    @Override
    public CategoriesAdapter.CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.categories_list_wide,parent,false);
        Log.d("MK_VT", "ViewType is " + viewType);


        return new CatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CatViewHolder holder, int position) {
        final CategoriesMedia category = mediaList.get(position);


        if( position%2 == 0 ) holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        holder.mediaTitle.setTypeface(typeface);
        holder.mediaTitle.setText(category.getName());

        LoadTumb loadTumb = new LoadTumb(holder.mediaThumb,context);
        loadTumb.execute(ENV.MEDIA_SERVER_ADDRESS , category.getThumb(),category.GetFileName());
        holder.mediaThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediaLoader = new Intent(ENV.current_activity, MediaLoader.class);
                mediaLoader.putExtra("id", category.getId());
                Log.e("CAT", "CAT: " + category.GetFileName() + "\t" + category.getName() + "\t" + category.getThumb() + "\t" + category.getId());
                ENV.current_activity.startActivity(mediaLoader);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


}
