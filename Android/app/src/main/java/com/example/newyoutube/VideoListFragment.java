package com.example.newyoutube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoListFragment extends Fragment {
    private List<Video> videos;
    private VideoListAdapter adapter;

    private String username;

    public static VideoListFragment newInstance(List<Video> videos, String username) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putSerializable("videos", (Serializable) videos);
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            videos = (List<Video>) getArguments().getSerializable("videos");
            username = getArguments().getString("username");

            Collections.shuffle(videos);
        }

        adapter = new VideoListAdapter(getContext(), videos, username);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
