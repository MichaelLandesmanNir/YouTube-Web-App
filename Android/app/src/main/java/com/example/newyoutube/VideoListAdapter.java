package com.example.newyoutube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private List<Video> videos;
    private ArrayList<Video> filteredVideos;

    private String username;
    private Context context;

    public VideoListAdapter(Context context, List<Video> videos, String username) {
        this.context = context;
        if (videos == null) {
            this.videos = new ArrayList<>();
        } else {
            this.videos = new ArrayList<>(videos);
        }
        this.username = username;
        this.filteredVideos = new ArrayList<>(this.videos);
        Collections.shuffle(videos);
        Collections.shuffle(filteredVideos);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoItemView itemView = new VideoItemView(context);
        return new VideoViewHolder(itemView, filteredVideos, username);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(filteredVideos.get(position));
    }



    @Override
    public int getItemCount() {
        return filteredVideos.size();
    }

    public void updateVideos(List<Video> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        filterVideos("");
    }

    public void filterVideos(String query) {
        filteredVideos.clear();
        if (query.isEmpty()) {
            filteredVideos.addAll(videos);
        } else {
            for (Video video : videos) {
                if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredVideos.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private VideoItemView videoItemView;
        private final ArrayList<Video> vids;
        private String username;

        public VideoViewHolder(@NonNull VideoItemView itemView, ArrayList<Video> vids, String username) {
            super(itemView);
            videoItemView = itemView;
            this.vids = vids;
            this.username = username;
        }

        public void bind(Video video) {
            videoItemView.bind(video, vids, username);
        }
    }
}
