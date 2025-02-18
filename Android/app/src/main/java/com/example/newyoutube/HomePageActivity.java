package com.example.newyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePageActivity extends AppCompatActivity implements HeaderFragment.OnSearchListener {

    private String searchQuery = "";
    private List<Video> videos = new ArrayList<>();
    private List<Video> newVideos = new ArrayList<>();

    LinearLayout layout;
    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        if (getIntent() != null && getIntent().getExtras() != null) {
            newVideos = (List<Video>) getIntent().getSerializableExtra("newVideos");
        }

        layout = findViewById(R.id.allLayout);

        if (getIntent().hasExtra("dark_mode")) {
            int darkModeColor = ContextCompat.getColor(this, R.color.darkMode);
            layout.setBackgroundColor(darkModeColor);
        }
        videoListAdapter = new VideoListAdapter(this, videos, getIntent().getStringExtra("username"));
        fetchData();

        HeaderFragment headerFragment = new HeaderFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.headerContainer, headerFragment).commit();

        SidebarFragment sidebarFragment = new SidebarFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.sidebarContainer, sidebarFragment).commit();

        RecyclerView videoListRecyclerView = findViewById(R.id.videoListRecyclerView);

        videoListRecyclerView.setAdapter(videoListAdapter);
        videoListRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Strings.BASE_URL+"api/videos")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomePageActivity.this, "Failed to fetch videos", Toast.LENGTH_SHORT).show();
                });
                Log.e("HomePageActivity", "Failed to fetch videos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject videoJson = jsonArray.getJSONObject(i);
                            Video video = new Video();
                            video.setId(videoJson.optString("_id", "-1"));  // Assuming id is an integer
                            video.setTitle(videoJson.optString("title", "Unknown Title"));
                            video.setChannel(videoJson.optString("channel", "Unknown Channel"));
                            video.setPath(videoJson.optString("path", ""));
                            video.setImage(videoJson.optString("image", ""));
                            video.setViews(videoJson.optInt("views", 0));
                            video.setLikes(videoJson.optInt("likes", 0));
                            video.setDislikes(videoJson.optInt("dislikes", 0));
                            video.setDetails(videoJson.optString("details", ""));
                            video.setDate(videoJson.isNull("date") ? null : videoJson.getString("date"));

                            JSONArray commentsJsonArray = videoJson.optJSONArray("comments");
                            if (commentsJsonArray != null) {
                                List<Comment> comments = new ArrayList<>();
                                for (int j = 0; j < commentsJsonArray.length(); j++) {
                                    JSONObject commentJson = commentsJsonArray.getJSONObject(j);
                                    Comment comment = new Comment();
                                    comment.setAuthor(commentJson.optString("author", "Unknown Author"));
                                    comment.setText(commentJson.optString("comment", ""));
                                    comments.add(comment);
                                }
                                video.setComments(comments);
                            }

                            videos.add(video);
                        }

                        if (newVideos != null) {
                            videos.addAll(newVideos);
                        }

                        runOnUiThread(() -> videoListAdapter.updateVideos(videos));

                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(HomePageActivity.this, "Failed to parse videos", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("HomePageActivity", "Failed to parse videos", e);
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(HomePageActivity.this, "Failed to fetch videos", Toast.LENGTH_SHORT).show();
                    });
                    Log.e("HomePageActivity", "Failed to fetch videos with response code: " + response.code());
                }
            }
        });

    }

    @Override
    public void onSearch(String query) {
        searchQuery = query;
        videoListAdapter.filterVideos(query);
    }
}
