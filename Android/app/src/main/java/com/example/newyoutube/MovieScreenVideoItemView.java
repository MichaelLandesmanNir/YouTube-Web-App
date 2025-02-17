package com.example.newyoutube;

import android.content.Context;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MovieScreenVideoItemView extends LinearLayout {
    private Video video;
    private ImageView videoThumbnail;
    private TextView videoTitle;
    private TextView videoChannel;
    private TextView videoViews;

    public MovieScreenVideoItemView(Context context) {
        super(context);
        init(context);
    }

    public MovieScreenVideoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MovieScreenVideoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_item, this, true);
        videoThumbnail = findViewById(R.id.videoThumbnail);
        videoTitle = findViewById(R.id.videoTitle);
        videoChannel = findViewById(R.id.videoChannel);
        videoViews = findViewById(R.id.videoViews);

        setOnClickListener(v -> {
            if (context instanceof MovieScreenActivity) {
                ((MovieScreenActivity) context).setSelectedVideo(video);
            }
        });
    }

    public void bind(Video video) {
        this.video = video;
        videoTitle.setText(video.getTitle());
        videoChannel.setText(video.getChannel());
        videoViews.setText(String.valueOf(video.getViews()) + " views");

        // Load thumbnail
        // You can use Glide or Picasso to load the image
        // Glide.with(getContext()).load(video.getImage()).into(videoThumbnail);
    }
}
