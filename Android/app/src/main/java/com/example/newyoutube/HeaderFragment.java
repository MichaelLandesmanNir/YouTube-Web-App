package com.example.newyoutube;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

public class HeaderFragment extends Fragment {

    private OnSearchListener onSearchListener;
    private EditText searchEditText;
    private String name;
    private String url;
    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_FILE = "com.example.newyoutube.preferences";

    public interface OnSearchListener {
        void onSearch(String query);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchListener) {
            onSearchListener = (OnSearchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSearchListener");
        }
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        ImageButton createVideoButton = view.findViewById(R.id.createVideoButton);
        ImageButton loginButton = view.findViewById(R.id.loginButton);
        ImageView showUserDetailsButton = view.findViewById(R.id.imageViewProfile);

        name = sharedPreferences.getString("name", "");
        String imagePath = sharedPreferences.getString("image", "");
        if (!imagePath.isEmpty()) {
            url = "http://192.168.109.1:5002/" + imagePath.replace("\\", "/");
        }

        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .transform(new CircleCrop())
                    .into(showUserDetailsButton);
        }

        LinearLayout layout = view.findViewById(R.id.allLayout);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            int darkModeColor = ContextCompat.getColor(getContext(), R.color.darkMode);
            layout.setBackgroundColor(darkModeColor);
        }

        showUserDetailsButton.setOnClickListener(v -> showDialog(getContext(), name));

        createVideoButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), CreateVideoActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onSearchListener != null) {
                    onSearchListener.onSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    public void showDialog(Context context, String name) {
        if (name == null || name.isEmpty()) {
            Toast.makeText(context, "You must login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_user_details, null);
        builder.setView(dialogView);
        builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView tvUsername = dialogView.findViewById(R.id.tvUsername);
        ImageView ivImage = dialogView.findViewById(R.id.ivImage);

        tvUsername.setText(name);

        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .into(ivImage);
        }

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}