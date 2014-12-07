package com.valterc.data.download;

/**
 * Created by Valter on 12/11/2014.
 */
public class DownloadImageRequest {

    private String imageUrl;
    private DownloadImageListener listener;

    public DownloadImageRequest(String imageUrl, DownloadImageListener listener) {
        this.imageUrl = imageUrl;
        this.listener = listener;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DownloadImageListener getListener() {
        return listener;
    }

    public void setListener(DownloadImageListener listener) {
        this.listener = listener;
    }

}
