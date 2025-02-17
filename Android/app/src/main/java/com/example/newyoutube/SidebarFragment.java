package com.example.newyoutube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SideBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SidebarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_bar, container, false);

        LinearLayout layout = view.findViewById(R.id.allLayout);

        if (getActivity().getIntent().hasExtra("dark_mode")) {

            int darkModeColor = ContextCompat.getColor(getContext(), R.color.darkMode);
            layout.setBackgroundColor(darkModeColor);
        }

        view.findViewById(R.id.homeButton).setOnClickListener(v -> {
            // Handle Home button click
        });

        view.findViewById(R.id.trendingButton).setOnClickListener(v -> {
            // Handle Trending button click
        });

        view.findViewById(R.id.subscriptionsButton).setOnClickListener(v -> {
            // Handle Subscriptions button click
        });

        view.findViewById(R.id.libraryButton).setOnClickListener(v -> {
            // Handle Library button click
        });

        return view;
    }
}
