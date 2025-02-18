package com.example.newyoutube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class VideoItemView extends LinearLayout {

    private ImageView videoThumbnail;
    private TextView videoTitle;
    private TextView videoChannel;
    private TextView videoViews;
    private TextView videoDate;

    private String username;
    private Uri videoUri;
    private Video video;
    private ArrayList<Video> allVideos;

    public VideoItemView(Context context) {
        super(context);
        init(context);
    }

    public VideoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_video, this, true);
        videoThumbnail = findViewById(R.id.videoThumbnail);
        videoTitle = findViewById(R.id.videoTitle);
        videoChannel = findViewById(R.id.videoChannel);
        videoViews = findViewById(R.id.videoViews);
        videoDate = findViewById(R.id.videoDate);

        setOnClickListener(v -> {
            if (videoUri != null) {
                Intent intent = new Intent(context, MovieScreenActivity.class);
                intent.putExtra("video", video);
                intent.putExtra("username", username);
                intent.putExtra("allVideos", allVideos);

                if (((Activity) context).getIntent().hasExtra("dark_mode")) {
                    intent.putExtra("dark_mode", ((Activity) context).getIntent().getStringExtra("dark_mode"));
                }

                context.startActivity(intent);
            }
        });
    }

    public void bind(Video video, ArrayList<Video> allVideos, String username) {
        this.video = video;
        this.allVideos = allVideos;
        this.username = username;
        videoTitle.setText(video.getTitle());
        videoChannel.setText(video.getChannel());
        videoViews.setText("views: " + (video.getViews()) );


        ZonedDateTime zonedDateTime = ZonedDateTime.parse(video.getDate());

        // Define the desired output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Format the ZonedDateTime object into the desired string format
        String formattedDate = zonedDateTime.format(formatter);
        if(username != "") {
            videoDate.setText(formattedDate);
        }


        String imageUrl = Strings.BASE_URL + video.getPath();
        imageUrl = imageUrl.replace("\\", "/");
        Log.d("VideoItemView", "Image URL: " + imageUrl);


        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imageUrl))
                .frame(3000000) // Load frame at 1 second (1000000 microseconds)
                .into(videoThumbnail);

        // Set the video URI from the video URL
        videoUri = Uri.parse(Strings.BASE_URL + video.getPath().replace("\\", "/"));
        Log.d("VideoItemView", "Video URI: " + videoUri.toString());
    }
}
