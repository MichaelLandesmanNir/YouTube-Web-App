// SideBarMovieScreen.java (Fragment in Activity B)
package com.example.newyoutube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SideBarMovieScreen extends Fragment {

    private List<Video> allVideos;
    private VideoListAdapter adapter;
    private String username;

    // BroadcastReceiver to handle video list updates
    private BroadcastReceiver videoListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.newyoutube.VIDEO_LIST_UPDATED".equals(intent.getAction())) {
                List<String> updatedVideoIds = (List<String>) intent.getSerializableExtra("videoList");
                if (updatedVideoIds != null) {
                    for (String id : updatedVideoIds) {
                        new FetchVideoTask().execute(id);
                    }
                }
            }
        }
    };

    // AsyncTask to fetch video data from server
    private class FetchVideoTask extends AsyncTask<String, Void, Video> {
        @Override
        protected Video doInBackground(String... params) {
            String videoId = params[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Strings.BASE_URL + "api/videos/" + videoId)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject videoObject = jsonObject.getJSONObject("video");

                    // Parse the video object from the JSON
                    Video video = parseVideoFromJson(videoObject);
                    return video;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Video video) {
            if (video != null) {
                allVideos.add(video);
                adapter.updateVideos(allVideos);
            }
        }
    }

    // Parse a Video object from a JSONObject
    private Video parseVideoFromJson(JSONObject videoObject) throws JSONException {
        String id = videoObject.getString("_id");
        String title = videoObject.getString("title");
        String channel = videoObject.getString("channel");
        String path = videoObject.getString("path");
        String image = videoObject.getString("image");
        int views = videoObject.getInt("views");
        int likes = videoObject.getInt("likes");
        int dislikes = videoObject.getInt("dislikes");
        String date = videoObject.getString("date");

        JSONArray commentsArray = videoObject.getJSONArray("comments");
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < commentsArray.length(); i++) {
            JSONObject commentObject = commentsArray.getJSONObject(i);
            String author = commentObject.getString("author");
            String commentText = commentObject.getString("comment");
            Comment comment = new Comment(author, commentText);
            comments.add(comment);
        }

        return new Video(id, title, channel, path, image, views, likes, dislikes, comments, "", date);
    }

    public static SideBarMovieScreen newInstance(List<Video> allVideos, Video video, String username) {
        SideBarMovieScreen fragment = new SideBarMovieScreen();
        Bundle args = new Bundle();
        args.putSerializable("allVideos", (Serializable) allVideos);
        args.putSerializable("video", (Serializable) video);
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_bar_movie_screen, container, false);

        if (getArguments() != null) {
            allVideos = (List<Video>) getArguments().getSerializable("allVideos");
            username = getArguments().getString("username");
        }

        RecyclerView allListVideos = view.findViewById(R.id.allVideos);
        allListVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoListAdapter(getContext(), allVideos, username);
        allListVideos.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(videoListReceiver,
                new IntentFilter("com.example.newyoutube.VIDEO_LIST_UPDATED"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(videoListReceiver);
    }
}
