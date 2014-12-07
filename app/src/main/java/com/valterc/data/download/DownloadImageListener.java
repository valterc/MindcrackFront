package com.valterc.data.download;

import android.graphics.Bitmap;

/**
 * Created by Valter on 12/11/2014.
 */
public interface DownloadImageListener {
    public void OnDownloadComplete(String imageUrl, boolean error, Bitmap bitmap);
}
