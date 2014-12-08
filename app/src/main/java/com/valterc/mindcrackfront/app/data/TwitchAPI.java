package com.valterc.mindcrackfront.app.data;

import com.google.gson.JsonObject;
import com.vcutils.DownloadResponse;
import com.vcutils.Downloader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Valter on 08/12/2014.
 */
public class TwitchAPI {

    private static final String TWITCH_API_STREAM = "https://api.twitch.tv/kraken/streams/";

    public static Boolean IsUserStreaming(String twitchUserId){

        DownloadResponse<String> apiResponse = Downloader.downloadGet(TWITCH_API_STREAM + twitchUserId);

        if (apiResponse.getResult() != DownloadResponse.DownloadResult.Ok || apiResponse.getResponse() == null){
            return false;
        }

        try {
            JSONObject jsonResponse = new JSONObject(apiResponse.getResponse());
            return jsonResponse.has("stream") && jsonResponse.get("stream") != null;
        } catch (JSONException e) {
           //Ignore exceptions
        }

        return false;
    }


}
