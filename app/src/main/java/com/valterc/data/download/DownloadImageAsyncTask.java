package com.valterc.data.download;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.valterc.data.cache.CacheDataInfo;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.utils.ImageUtils;
import com.vcutils.DownloadResponse;
import com.vcutils.Downloader;

/**
 * Created by Valter on 12/11/2014.
 */
public class DownloadImageAsyncTask extends AsyncTask<DownloadImageRequest, Void, DownloadImageResult> {

    private static final int MAX_TRIES = 3;

    private int tryCount;

    @Override
    protected DownloadImageResult doInBackground(DownloadImageRequest... requests) {
        DownloadImageRequest request = requests[0];

        //Try to load from cache
        Bitmap bitmap = MindcrackFrontApplication.getCache().GetDataBitmap(request.getImageUrl());
        if (bitmap != null){
            DownloadImageResult result = new DownloadImageResult();
            result.setImage(bitmap);
            result.setImageUrl(request.getImageUrl());
            result.setError(false);
            result.setListener(request.getListener());
            return result;
        }

        DownloadResponse<Bitmap> response = null;

        do {
            response = Downloader.downloadImage(request.getImageUrl());
        } while (++tryCount < MAX_TRIES && response.getResult() != DownloadResponse.DownloadResult.Ok);

        DownloadImageResult result = new DownloadImageResult();
        result.setImage(response.getResponse());
        result.setImageUrl(request.getImageUrl());
        result.setError(response.getResult() != DownloadResponse.DownloadResult.Ok);
        result.setListener(request.getListener());

        //Store data on cache
        if (!result.getError()){
            MindcrackFrontApplication.getCache().StoreDataBitmap(request.getImageUrl(), CacheDataInfo.TIME_DAY, result.getImage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(DownloadImageResult downloadImageResult) {
        super.onPostExecute(downloadImageResult);
        if (downloadImageResult.getListener() != null){
            downloadImageResult.getListener().OnDownloadComplete(downloadImageResult.getImageUrl(), downloadImageResult.getError(), downloadImageResult.getImage());
        }

        downloadImageResult.setListener(null);
    }

}
