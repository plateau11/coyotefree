package com.example.coyotefree;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class relax extends AppCompatActivity {

    private static final String PREF_GIF_URL = "cached_gif_url";
    private SharedPreferences prefs;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_relax);

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        imageView = findViewById(R.id.imageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchGifUrlWithTryCatch();
    }

    private void fetchGifUrlWithTryCatch() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://clipboard-server-eqcl.onrender.com/getGifUrl")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string().trim();
                    JSONObject json = new JSONObject(body);
                    String gifUrl = json.getString("gif_url");

                    // Save to shared preferences
                    prefs.edit().putString(PREF_GIF_URL, gifUrl).apply();

                    handler.post(() -> loadGif(gifUrl));
                } else {
                    throw new IOException("Server response not successful");
                }

            } catch (Exception e) {
                e.printStackTrace();

                handler.post(() -> {
                    //Toast.makeText(MainActivity.this, "Failed to load from server. Using cached GIF.", Toast.LENGTH_SHORT).show();
                    String cachedUrl = prefs.getString(PREF_GIF_URL, null);
                    if (cachedUrl != null) {
                        try {
                            Glide.with(relax.this)
                                    .asGif()
                                    .load(cachedUrl)
                                    .into(imageView);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(relax.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(relax.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadGif(String url) {
        Glide.with(this)
                .asGif()
                .load(url)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model,
                                                   Target<GifDrawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(imageView);
    }


}
