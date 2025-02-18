package com.example.newyoutube;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.newyoutube.api.ApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

public class VideoPlayerFragment extends Fragment {
    private static final String PREFERENCES_FILE = "com.example.newyoutube.preferences";

    private Video video;
    private String username;
    private TextView videoTitle;
    private String url;
    private TextView videoChannel;
    private TextView videoViews;
    private ImageView btnDownload;
    private VideoView videoView;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private Button addCommentButton;
    private ImageView likeButton;
    private ImageView dislikeButton;
    private ImageView btnShare;
    private ImageView editVideo;
    private ImageView deleteVideo;
    private TextView likeCount;
    private int initialLikes;
    private List<Comment> commentList;
    private ImageView imageView;
    private CommentAdapter commentAdapter;
    private Map<String, Integer> map;
    private boolean userLiked = false;
    private int likeClickCount = 0;
    private SharedPreferences sharedPreferences;

    private RecyclerView recommendationsRecyclerView;
    private VideoListAdapter recommendationsAdapter;
    private List<Video> recommendationsList;

    public static VideoPlayerFragment newInstance(Video video, String username) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable("video", video);
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        btnDownload = view.findViewById(R.id.downloadButton);
        videoView = view.findViewById(R.id.videoView);
        videoTitle = view.findViewById(R.id.videoTitle);
        videoChannel = view.findViewById(R.id.videoChannel);
        videoViews = view.findViewById(R.id.videoViews);
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentInput = view.findViewById(R.id.commentInput);
        addCommentButton = view.findViewById(R.id.addCommentButton);
        likeButton = view.findViewById(R.id.likeButton);
        dislikeButton = view.findViewById(R.id.dislikeButton);
        likeCount = view.findViewById(R.id.likeCount);
        btnShare = view.findViewById(R.id.shareButton);
        editVideo = view.findViewById(R.id.editButton);
        deleteVideo = view.findViewById(R.id.deleteButton);
        imageView = view.findViewById(R.id.imageView);
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);

        ScrollView layout = view.findViewById(R.id.allLayout);
        if (((Activity) getContext()).getIntent().hasExtra("dark_mode")) {
            int darkModeColor = ContextCompat.getColor(getContext(), R.color.darkMode);
            layout.setBackgroundColor(darkModeColor);
        }

        btnShare.setOnClickListener(v -> shareVideoUri());
        deleteVideo.setOnClickListener(v -> deleteVideo());
        editVideo.setOnClickListener(v -> showChangeTitleDialog());
        btnDownload.setOnClickListener(v -> downloadVideo());

        map = new HashMap<>();
        initializeVideoMap();

        if (getArguments() != null) {
            video = (Video) getArguments().getSerializable("video");
            username = getArguments().getString("username");
            setVideo(video);
        }

        initializeComments();
        initialLikes = video.getLikes();
        url = Strings.BASE_URL + video.getPath();
        url = url.replace("\\", "/");

        addCommentButton.setOnClickListener(v -> addComment());
        likeButton.setOnClickListener(v -> likeVideo());
        dislikeButton.setOnClickListener(v -> dislikeVideo());

        loadLikesAndDislikes();


        return view;
    }




    private void initializeVideoMap() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                int resId = field.getInt(null);
                map.put(name, resId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadVideo() {
        // Create the video file in the Downloads directory
        File videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "video.mp4");


        String finalUrl = url;
        Log.d("log_url", finalUrl);
        new Thread(() -> {
            try (InputStream inputStream = new URL(finalUrl).openStream();
                 OutputStream outputStream = new FileOutputStream(videoFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                // Show success message on the UI thread
                ((Activity)getContext()).runOnUiThread(() -> Toast.makeText(getContext(), "Video downloaded to " + videoFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();

                // Show error message on the UI thread
                ((Activity)getContext()).runOnUiThread(() -> Toast.makeText(getContext(), "Failed to download video", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void shareVideoUri() {
        Uri videoUri = Uri.parse(url);

        // Create the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share Video via"));
    }

    private void showChangeTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Video Title");
        final EditText input = new EditText(getContext());
        input.setHint("Enter new title");



        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTitle = input.getText().toString();
            video.setTitle(newTitle);
            setVideo(video);

            JsonObject json = new JsonObject();
            json.addProperty("newName", newTitle);

            new Thread(() -> {
                try {
                    Response response = ApiClient.put("/videos/" + video.getId(), json, "");
                    if (!response.isSuccessful()) {
                        Log.e("editVideo", "Failed to edit video: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e("editVideo", "Failed to edit video: " + e.getMessage());
                }
            }).start();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void deleteVideo() {
        new Thread(() -> {
            try {
                Response response = ApiClient.delete("/videos/" + video.getId());
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        Intent i = new Intent(getContext(), HomePageActivity.class);
                        i.putExtra("deleted_video", video);
                        i.putExtra("username", username);
                        startActivity(i);
                    });
                } else {
                    Log.e("deleteVideo", "Failed to delete video: " + response.message());
                }
            } catch (IOException e) {
                Log.e("deleteVideo", "Failed to delete video: " + e.getMessage());
            }
        }).start();
    }

    public void setVideo(Video video) {
        this.video = video;
        videoTitle.setText(video.getTitle());
        videoChannel.setText(video.getChannel());

        videoChannel.setOnClickListener(v -> {
            openUserVideos(video.getChannel());
        });

        String token = sharedPreferences.getString("token", "");

        Uri videoUri = Uri.parse(Strings.BASE_URL + video.getPath().replace("\\", "/"));
        Log.d("Video_URI", videoUri.toString());
        videoView.setVideoURI(videoUri);
        String URL = "api/videos/" + video.getId() + "/addView";
        Log.d("addView", URL);
        new Thread(() -> {
            try {
                Response response = ApiClient.put(URL, new JsonObject(), token);
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject videoObject = jsonObject.getJSONObject("updatedVideo");
                    int updatedViews = videoObject.getInt("views");

                    JSONArray recommendationsArray = jsonObject.getJSONArray("recommendations");
                    List<String> videoList = new ArrayList<>();
                    for (int i = 0; i < recommendationsArray.length(); i++) {
                        String recommendationId = recommendationsArray.getString(i);
                       videoList.add(recommendationId);
                    }
                    if (videoList.size() > 0) {
                        Intent intent = new Intent("com.example.newyoutube.VIDEO_LIST_UPDATED");
                        intent.putExtra("videoList", (Serializable) videoList); // Pass a list of video IDs
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    }



                    getActivity().runOnUiThread(() -> {
                        videoViews.setText(updatedViews + " views");
                    });
                } else {
                    Log.e("addView", "Failed adding view " + response.message());
                }
            } catch (IOException | JSONException e) {
                Log.e("addView", "Failed to add view: " + e.getMessage());
            }
        }).start();

        deleteVideo.setVisibility(video.getChannel() != null && video.getChannel().equals(username) ? View.VISIBLE : View.INVISIBLE);
        editVideo.setVisibility(video.getChannel() != null && video.getChannel().equals(username) ? View.VISIBLE : View.INVISIBLE);

        Log.d("name_log", username + "");

        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setOnPreparedListener(mp -> videoView.start());

        videoView.setOnErrorListener((mp, what, extra) -> {
            String errorMessage;
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errorMessage = "Unknown media error.";
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    errorMessage = "Media server died.";
                    break;
                default:
                    errorMessage = "Media player error: " + what + " Extra code: " + extra;
                    break;
            }
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            return true;
        });
    }


    private void initializeComments() {
        commentList = video.getComments();
        commentAdapter = new CommentAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentAdapter);
    }
    private void openUserVideos(String channel) {
        // Use Executors for background threading
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Make the API call
                    Response resp = ApiClient.get("/allVideos", "");

                    if (resp.isSuccessful() && resp.body() != null) {
                        // Parse the response body
                        Gson gson = new Gson();
                        Type videoListType = new TypeToken<List<Video>>() {}.getType();
                        List<Video> videos = gson.fromJson(resp.body().string(), videoListType);

                        // Filter the list of videos based on the channel
                        List<Video> filtered = new ArrayList<>();
                        for (Video video : videos) {
                            if (video != null && video.getChannel().equals(channel)) {
                                filtered.add(video);
                            }
                        }

                        // Update UI on the main thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("User Videos");

                                VideoListAdapter adapter = new VideoListAdapter(getContext(), filtered, "");

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.dialog_list, null);
                                builder.setView(dialogView);

                                RecyclerView listView = dialogView.findViewById(R.id.dialog_list_view);
                                listView.setAdapter(adapter);
                                listView.setLayoutManager(new LinearLayoutManager(getContext())); // Set a layout manager

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    } else {
                        // Handle the error response
                        Log.e("API Error", "Failed to fetch videos");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void addComment() {
        String author = username;
        String text = commentInput.getText().toString().trim();

        if (author == null || author.isEmpty()) {
            Toast.makeText(getContext(), "You must log in to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!text.isEmpty()) {
            Comment newComment = new Comment(author, text);
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);
            commentInput.setText("");
            commentsRecyclerView.smoothScrollToPosition(commentList.size() - 1);

            JsonObject json = new JsonObject();
            json.addProperty("author", author);
            json.addProperty("comment", text);

            new Thread(() -> {
                try {
                    Response response = ApiClient.post("/videos/" + video.getId() + "/comments", json);
                    if (!response.isSuccessful()) {
                        Log.e("addComment", "Failed to add comment: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e("addComment", "Failed to add comment: " + e.getMessage());
                }
            }).start();
        } else {
            Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLikesAndDislikes() {
        int likes = video.getLikes();
        likeCount.setText(String.valueOf(likes));
    }

    private void likeVideo() {
        likeClickCount++;
        int likes = video.getLikes();

        if (likeClickCount % 2 == 1) {
            likes++;
            userLiked = true;
        } else if (likes > initialLikes) {
            likes--;
            userLiked = false;
        }

        video.setLikes(likes);
        likeCount.setText(String.valueOf(likes));

        String endpoint = userLiked ? "/videos/" + video.getId() + "/like" : "/videos/" + video.getId() + "/unlike";

        new Thread(() -> {
            try {
                Response response = ApiClient.post(endpoint, new JsonObject());
                if (!response.isSuccessful()) {
                    Log.e("likeVideo", "Failed to like/unlike video: " + response.message());
                }
            } catch (IOException e) {
                Log.e("likeVideo", "Failed to like/unlike video: " + e.getMessage());
            }
        }).start();
    }

    private void dislikeVideo() {
        int likes = video.getLikes();

        if (userLiked && likes > initialLikes) {
            likes--;
            userLiked = false;
            String endpoint = "/videos/" + video.getId() + "/undislike";

            new Thread(() -> {
                try {
                    Response response = ApiClient.delete(endpoint);
                    if (!response.isSuccessful()) {
                        Log.e("dislikeVideo", "Failed to undislike video: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e("dislikeVideo", "Failed to undislike video: " + e.getMessage());
                }
            }).start();
        }

        video.setLikes(likes);
        likeCount.setText(String.valueOf(likes));
    }
}
