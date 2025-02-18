package com.example.newyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Switch switchDark;
    private TextView errorTextView;
    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_FILE = "com.example.newyoutube.preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        switchDark = findViewById(R.id.switchDark);
        loginButton = findViewById(R.id.loginButton);
        errorTextView = findViewById(R.id.errorTextView);
        ImageView imageView2 = findViewById(R.id.imageView2);

        imageView2.setOnClickListener(v -> navigateToHomePage());

        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> toggleDarkMode(isChecked));

        loginButton.setOnClickListener(v -> handleLogin());

        sharedPreferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
    }

    private void navigateToHomePage() {
        Intent i = new Intent(this, HomePageActivity.class);
        startActivity(i);
    }

    private void toggleDarkMode(boolean isChecked) {
        LinearLayout layout = findViewById(R.id.loginLayout);
        ImageView imageView2 = findViewById(R.id.imageView2);
        if (isChecked) {
            imageView2.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.darkBackground));
            layout.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.darkBackground));
        } else {
            imageView2.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.lightBackground));
            layout.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.lightBackground));
        }
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!validateForm(username, password)) return;

        OkHttpClient client = new OkHttpClient();

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            errorTextView.setText("An error occurred while creating login data.");
            return;
        }

        RequestBody requestBody = RequestBody.create(
                loginData.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(Strings.BASE_URL + "api/tokens")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(response.body().string());
                        String token = responseObject.getString("token");
                        JSONObject user = responseObject.getJSONObject("user");
                        saveUserData(token, username, user.getString("imageUrl"));
                        navigateToHomePage();
                    } catch (JSONException e) {
                        runOnUiThread(() -> errorTextView.setText("An error occurred while processing the response."));
                    }
                } else {
                    runOnUiThread(() -> errorTextView.setText("Invalid username or password."));
                }
            }
        });
    }

    private boolean validateForm(String username, String password) {
        if (username.isEmpty()) {
            errorTextView.setText("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            errorTextView.setText("Password is required");
            return false;
        }
        return true;
    }

    private void saveUserData(String token, String username, String imageUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putBoolean("isAuthenticated", true);
        editor.putString("name", username);
        editor.putString("image", imageUrl);
        editor.apply();
    }

    public void navigateToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.clearData(this);
    }
}