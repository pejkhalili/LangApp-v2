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

import com.chapdast.ventures.Objects.Media;
import com.chapdast.ventures.activities.PodcastPlayer;
import com.chapdast.ventures.Configs.ENV;
import com.chapdast.ventures.Configs.LoadTumb;
import com.chapdast.ventures.activities.Player;
import com.chapdast.ventures.R;

import java.util.ArrayList;



public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{

    private ArrayList<Media> mediaList;
    private Context context;
    private Typeface typeface;




    public static class MediaViewHolder extends RecyclerView.ViewHolder{
        public View v;
        public ImageView mediaThumb;
        public ImageView mediaSideType;
        public TextView mediaTitle;

        public MediaViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.mediaThumb = v.findViewById(R.id.mlwThumb);
            this.mediaSideType = v.findViewById(R.id.mlwSideType);
            this.mediaTitle = v.findViewById(R.id.mlwTitle);
        }
    }

    public MediaAdapter(ArrayList<Media> mediaList, Context context){
        this.typeface = Typeface.createFromAsset(context.getAssets(),"fonts/bebas.otf");
        this.mediaList = mediaList;
        this.context = context;
    }

    @Override
    public MediaAdapter.MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.media_list_wide,parent,false);
        Log.d("MK_VT", "ViewType is " + viewType);


        return new MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MediaAdapter.MediaViewHolder holder, int position) {
        final Media media = mediaList.get(position);
        final String mediaType = media.getType();

        if( position%2 == 0 ) holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else holder.v.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        holder.mediaTitle.setTypeface(typeface);
        holder.mediaTitle.setText(media.getName());

        LoadTumb loadTumb = new LoadTumb(holder.mediaThumb,context);
        loadTumb.execute(ENV.MEDIA_SERVER_ADDRESS , media.getThumb(),media.GetFileName());

        if(mediaType.equals("video")) {
            holder.mediaSideType.setImageResource(R.drawable.media_list_video);

            holder.mediaThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent playVideo = new Intent(ENV.current_activity, Player.class);
                        playVideo.putExtra("NAME", media.getName());
                        playVideo.putExtra("LINK", media.getLink());
                        playVideo.putExtra("SUB", media.getSub());
                        playVideo.putExtra("THUMB", media.getThumb());
                        ENV.current_activity.startActivity(playVideo);

                }
            });
        }else if (mediaType.equals("podcast")){
            holder.mediaSideType.setImageResource(R.drawable.media_list_podcast);
            holder.mediaThumb.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent playPodcast = new Intent(ENV.current_activity, PodcastPlayer.class);
                    playPodcast.putExtra("NAME", media.getName());
                    playPodcast.putExtra("LINK", media.getLink());
                    playPodcast.putExtra("THUMB", media.getThumb());
                    playPodcast.putExtra("FILENAME", media.GetFileName());
                    ENV.current_activity.startActivity(playPodcast);
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


}
