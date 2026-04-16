package com.example.laborator08;

import android.graphics.Bitmap;

public class ProductImageInfo {
    private String imageUrl;
    private String description;
    private String detailUrl;
    private Bitmap image;

    public ProductImageInfo(String imageUrl, String description, String detailUrl) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.detailUrl = detailUrl;
    }

    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public String getDetailUrl() { return detailUrl; }
    public Bitmap getImage() { return image; }
    public void setImage(Bitmap image) { this.image = image; }
}
