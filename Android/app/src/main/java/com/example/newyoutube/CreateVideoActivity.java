package com.example.newyoutube;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;

public class CreateVideoActivity extends AppCompatActivity {
    private static final String TAG = "CreateVideoActivity";
    private EditText titleEditText;
    private ImageView imageView;
    private Button uploadButton;
    private Button goBackButton;

    private Uri videoUri;
    private Uri imageUri;

    private static final int REQUEST_PERMISSION_SETTING = 123;

    private static final int REQUEST_READ_STORAGE_PERMISSION = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private boolean permissionToReadAccepted = false;
    private boolean permissionToWriteAccepted = false;

    private Button videoInput;
    private Button imageInput;

    private ActivityResultLauncher<Intent> videoCaptureLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video);

        if (!getIntent().hasExtra("username") || getIntent().getStringExtra("username") == null || getIntent().getStringExtra("username").equals("")) {
            Toast.makeText(this, "Please login before uploading a video", Toast.LENGTH_SHORT).show();
            finish();
        }

        titleEditText = findViewById(R.id.titleEditText);
        imageView = findViewById(R.id.thumbnailImageView);
        uploadButton = findViewById(R.id.uploadButton);
        goBackButton = findViewById(R.id.goBackButton);

        goBackButton.setOnClickListener(v -> {
            Log.d(TAG, "Go back button clicked");
            finish();
        });

        uploadButton.setOnClickListener(v -> {
            Log.d(TAG, "Upload button clicked");
            handleUpload();
        });

        videoInput = findViewById(R.id.videoInput);
        imageInput = findViewById(R.id.imageInput);



        videoInput.setOnClickListener(v -> {
            Log.d(TAG, "Video input clicked");
            captureVideo();
        });

        imageInput.setOnClickListener(v -> {
            Log.d(TAG, "Image input clicked");
            selectImage();
        });

        setupActivityResultLaunchers();
    }

    private void setupActivityResultLaunchers() {
        videoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        videoUri = result.getData().getData();
                        Log.d(TAG, "Video captured: " + videoUri.toString());
                    } else {
                        Log.d(TAG, "Video capture failed with result code: " + result.getResultCode());
                        videoUri = null; // Reset videoUri if capture failed
                        Log.d(TAG, "No video captured");
                    }
                });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Log.d(TAG, "Image selected: " + imageUri.toString());
                        imageView.setImageURI(imageUri);
                    } else {
                        Log.d(TAG, "Image selection failed with result code: " + result.getResultCode());
                    }
                });
    }

    private void captureVideo() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissions, REQUEST_READ_STORAGE_PERMISSION);
        } else {
            openGalleryForVideo();
        }
    }

    private void openGalleryForVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        videoCaptureLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryForVideo();
            } else {
                Toast.makeText(this, "Permission denied. Unable to access gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void showReadStoragePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("This app needs access to your storage to capture video. Please grant the required permission.");
        builder.setPositiveButton("Grant", (dialog, which) -> {
            ActivityCompat.requestPermissions(CreateVideoActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(videoFileName, ".mp4", storageDir);
        Log.d(TAG, "Video file created: " + videoFile.getAbsolutePath());
        return videoFile;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleUpload() {
        String title = titleEditText.getText().toString();
        String channel = getIntent().getStringExtra("username");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String comments = "[]"; // Initialize with empty JSON array

        Log.d(TAG, "Handling upload: title=" + title + ", channel=" + channel + ", date=" + date);

        if (videoUri == null) {
            Log.w(TAG, "No video captured");
            Toast.makeText(this, "Please capture a video.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Log.w(TAG, "No image selected");
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        File videoFile = createFileFromUri(videoUri, "video.mp4");
        File imageFile = createFileFromUri(imageUri, "image.jpg");

        if (videoFile == null || imageFile == null) {
            Toast.makeText(this, "Failed to create files from URIs.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestVideo = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("path", videoFile.getName(), requestVideo);

        RequestBody requestImage = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestImage);

        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestBodyChannel = RequestBody.create(MediaType.parse("text/plain"), channel);
        RequestBody requestBodyDate = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody requestBodyComments = RequestBody.create(MediaType.parse("text/plain"), comments);

        OkHttpClient client = new OkHttpClient();

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("path", videoFile.getName(), requestVideo)
                .addFormDataPart("image", imageFile.getName(), requestImage)
                .addFormDataPart("title", title)
                .addFormDataPart("channel", channel)
                .addFormDataPart("date", date)
                .addFormDataPart("comments", comments)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.109.1:5002/api/videos")
                .post(requestBody)
                .build();

        Log.d(TAG, "Making API call to upload video");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e(TAG, "Upload failed: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(CreateVideoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Video uploaded successfully");
                    runOnUiThread(() -> {
                        Toast.makeText(CreateVideoActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CreateVideoActivity.this, HomePageActivity.class);
                        i.putExtra("auth", true);
                        i.putExtra("username", getIntent().getStringExtra("username"));
                        startActivity(i);
                    });
                } else {
                    Log.w(TAG, "Upload failed with response code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(CreateVideoActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private File createFileFromUri(Uri uri, String fileName) {
        File file = new File(getCacheDir(), fileName);
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream == null) return null;
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            Log.d(TAG, "File created from URI: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Failed to create file from URI", e);
            return null;
        }
    }
}
