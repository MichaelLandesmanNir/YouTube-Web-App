package com.example.newyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MovieScreenActivity extends AppCompatActivity {
    private Video selectedVideo;
    private List<Video> allVideos;
    private VideoPlayerFragment videoPlayerFragment;
    private SideBarMovieScreen sidebarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_screen);

        ImageButton goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> startActivity(new Intent(this, HomePageActivity.class)));

        selectedVideo = (Video) getIntent().getSerializableExtra("video");
        allVideos = (List<Video>) getIntent().getSerializableExtra("allVideos");

        List<Video> recommendedVideos = new ArrayList<>();





        LinearLayout layout = findViewById(R.id.allLayout);

        if (getIntent().hasExtra("dark_mode")) {

            int darkModeColor = ContextCompat.getColor(this, R.color.darkMode);
            layout.setBackgroundColor(darkModeColor);
        }

        videoPlayerFragment = VideoPlayerFragment.newInstance(selectedVideo, getIntent().getStringExtra("username"));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.videoDetailContainer, videoPlayerFragment)
                .commit();

        sidebarFragment = SideBarMovieScreen.newInstance(allVideos, selectedVideo, getIntent().getStringExtra("username"));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sidebarContainer, sidebarFragment)
                .commit();
    }

    public void setSelectedVideo(Video video) {
        selectedVideo = video;
        videoPlayerFragment.setVideo(video);
    }
}
