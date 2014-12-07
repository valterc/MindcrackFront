package com.valterc.data.download;

import android.graphics.Bitmap;

/**
 * Created by Valter on 12/11/2014.
 */
public class DownloadImageResult {

    private Bitmap image;
    private String imageUrl;
    private Boolean error;
    private DownloadImageListener listener;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public DownloadImageListener getListener() {
        return listener;
    }

    public void setListener(DownloadImageListener listener) {
        this.listener = listener;
    }
}
