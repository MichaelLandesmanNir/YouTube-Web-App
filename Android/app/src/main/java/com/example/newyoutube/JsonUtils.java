package com.example.newyoutube;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    public static List<Video> loadVideoList(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Video>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}

