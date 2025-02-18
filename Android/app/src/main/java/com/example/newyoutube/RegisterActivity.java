package com.example.newyoutube;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1003;
    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText displayNameEditText;
    private ImageView profileImageView;
    private Button registerButton;
    private TextView errorTextView;
    private Uri profileImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> imageCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        profileImageView = findViewById(R.id.profileImageView);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);
        ImageView imageView3 = findViewById(R.id.imageView3);

        Switch switchDark = findViewById(R.id.switchDark);


        imageView3.setClickable(true);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, HomePageActivity.class));
            }
        });

        switchDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ScrollView layout = findViewById(R.id.registerLayout);
                if (isChecked) {
                    // Apply dark mode
                    imageView3.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.darkBackground));
                    layout.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.darkBackground));
                } else {
                    imageView3.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.lightBackground));
                    layout.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.lightBackground));
                }
            }
        });


        requestPermissions();

        Button selectProfileImageButton = findViewById(R.id.selectProfileImageButton);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);

        selectProfileImageButton.setOnClickListener(v -> selectImage());
        takePhotoButton.setOnClickListener(v -> takePhoto());

        registerButton.setOnClickListener(v -> handleRegister());

        setupActivityResultLaunchers();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API level 30) and above
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                startActivityForResult(intent, REQUEST_PERMISSIONS);
            }
        } else {
            // Android 6.0 to 10 (API level 23 to 29)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "All permissions are required to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupActivityResultLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        profileImageUri = result.getData().getData();
                        profileImageView.setImageURI(profileImageUri);
                        profileImageView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Image selected: " + profileImageUri.toString());
                    } else {
                        Log.d(TAG, "Image selection failed or was canceled");
                    }
                });

        imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        profileImageView.setImageURI(profileImageUri);
                        profileImageView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Photo captured: " + profileImageUri.toString());
                    } else {
                        Log.d(TAG, "Photo capture failed or was canceled");
                    }
                });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSIONS);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e(TAG, "Error creating image file: " + ex.getMessage());
            ex.printStackTrace();
        }
        if (photoFile != null) {
            profileImageUri = FileProvider.getUriForFile(this, "com.example.newyoutube.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
            imageCaptureLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "Image file created: " + imageFile.getAbsolutePath());
        return imageFile;
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String displayName = displayNameEditText.getText().toString();

        if (!validateForm(username, password, confirmPassword, displayName, profileImageUri)) return;

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody;
        if (profileImageUri != null) {
            String filePath = getPathFromUri(profileImageUri);
            if (filePath == null) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show());
                Log.e(TAG, "Failed to get file path from URI: " + profileImageUri.toString());
                return;
            }
            File file = new File(filePath);
            if (!file.canRead()) {
                Log.e(TAG, "File is not readable: " + filePath);
                return;
            }
            RequestBody fileBody = RequestBody.create(file, MediaType.parse(getContentResolver().getType(profileImageUri)));

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", username)
                    .addFormDataPart("password", password)
                    .addFormDataPart("profileImage", file.getName(), fileBody)
                    .build();
            Toast.makeText(this, "got image", Toast.LENGTH_SHORT).show();
        } else {
            requestBody = new FormBody.Builder()
                    .add("name", username)
                    .add("password", password)
                    .build();
        }

        Request request = new Request.Builder()
                .url("http://192.168.109.1:5002/api/users")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    errorTextView.setText("An error occurred: " + e.getMessage());
                });
                Log.e(TAG, "Registration failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBodyStr = response.body().string();  // Read the response body once and store it
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        i.putExtra("username", username);
                        i.putExtra("password", password);
                        startActivity(i);
                    });
                    Log.d(TAG, "Registration successful: " + responseBodyStr);
                } else {
                    runOnUiThread(() -> {
                        errorTextView.setText("Registration failed: " + responseBodyStr);
                        Log.e(TAG, "Registration failed: " + responseBodyStr);
                    });
                }
            }
        });
    }

    private String getPathFromUri(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "URI is null");
            return null;
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Check if the URI authority is FileProvider
            if (isFileProviderUri(uri)) {
                return getFileFromFileProviderUri(uri);
            } else {
                // Handle other content URI types
                return getDataColumn(this, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private boolean isFileProviderUri(Uri uri) {
        return uri.getAuthority() != null && uri.getAuthority().startsWith("com.example.newyoutube.fileprovider");
    }

    private String getFileFromFileProviderUri(Uri uri) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), uri.getLastPathSegment());
        return file.getAbsolutePath();
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying for file path: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private boolean validateForm(String username, String password, String confirmPassword, String displayName, Uri profileImageUri) {
        String error = null;
        if (username.isEmpty()) {
            error = "Username is required";
        } else if (password.isEmpty()) {
            error = "Password is required";
        } else if (!password.equals(confirmPassword)) {
            error = "Passwords do not match";
        } else if (displayName.isEmpty()) {
            error = "Display name is required";
        } else if (profileImageUri == null || profileImageUri.toString().isEmpty()) {
            error = "Profile image is required";
        } else if (!isValidPassword(password)) {
            error = "Password must be at least 8 characters long and combine letters and numbers";
        }

        if (error != null) {
            errorTextView.setText(error);
            errorTextView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Form validation error: " + error);
            return false;
        }
        errorTextView.setVisibility(View.GONE);
        return true;
    }

    public static boolean isValidPassword(String str) {
        // Check if the string has exactly 8 characters
        if (str.length() != 8) {
            return false;
        }

        // Regular expression to check if the string contains both letters and numbers
        boolean hasLetter = str.matches(".*[a-zA-Z].*");
        boolean hasNumber = str.matches(".*[0-9].*");

        return hasLetter && hasNumber;
    }
}
