package com.example.laborator08;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagesActivity extends AppCompatActivity {
    private ListView lvImages;
    private List<ProductImageInfo> imageInfoList;
    private ProductImageAdapter adapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        lvImages = findViewById(R.id.lv_images);
        initData();

        adapter = new ProductImageAdapter(this, imageInfoList);
        lvImages.setAdapter(adapter);

        downloadImages();

        lvImages.setOnItemClickListener((parent, view, position, id) -> {
            ProductImageInfo selected = imageInfoList.get(position);
            Intent intent = new Intent(ImagesActivity.this, WebViewActivity.class);
            intent.putExtra("url", selected.getDetailUrl());
            startActivity(intent);
        });
    }

    private void initData() {
        imageInfoList = new ArrayList<>();
        imageInfoList.add(new ProductImageInfo("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200", "Smartphone Modern", "https://en.wikipedia.org/wiki/Smartphone"));
        imageInfoList.add(new ProductImageInfo("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=200", "Laptop performant", "https://en.wikipedia.org/wiki/Laptop"));
        imageInfoList.add(new ProductImageInfo("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200", "Ceas inteligent", "https://en.wikipedia.org/wiki/Watch"));
        imageInfoList.add(new ProductImageInfo("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200", "Căști audio", "https://en.wikipedia.org/wiki/Headphones"));
        imageInfoList.add(new ProductImageInfo("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=200", "Cameră foto", "https://en.wikipedia.org/wiki/Camera"));
    }

    private void downloadImages() {
        for (ProductImageInfo info : imageInfoList) {
            executorService.execute(() -> {
                Bitmap bitmap = downloadBitmap(info.getImageUrl());
                if (bitmap != null) {
                    info.setImage(bitmap);
                    handler.post(() -> adapter.notifyDataSetChanged());
                }
            });
        }
    }

    private Bitmap downloadBitmap(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
