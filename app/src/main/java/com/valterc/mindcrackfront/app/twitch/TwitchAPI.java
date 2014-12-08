package com.valterc.mindcrackfront.app.twitch;

import com.google.gson.JsonObject;
import com.vcutils.DownloadResponse;
import com.vcutils.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Valter on 08/12/2014.
 */
public class TwitchAPI {

    private static final String TWITCH_API_STREAM = "https://api.twitch.tv/kraken/streams/";
    private static final String TWITCH_API_STREAM_CHANNELS = "https://api.twitch.tv/kraken/streams?channel=";

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


    /**
     * Checks the streaming status of several twitch users
     * @param twitchUserIds users to query streaming status
     * @return NULL on error, array containing the users streaming
     */
    public static String[] GetUsersStreaming(String[] twitchUserIds){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(TWITCH_API_STREAM_CHANNELS);

        for (int i = 0; i < twitchUserIds.length; i++) {
            stringBuilder.append(twitchUserIds[i]);
            stringBuilder.append(','); //trailing comma is OK with the API
        }

        DownloadResponse<String> apiResponse = Downloader.downloadGet(stringBuilder.toString());

        if (apiResponse.getResult() != DownloadResponse.DownloadResult.Ok || apiResponse.getResponse() == null){
            return null;
        }

        try {
            JSONObject jsonResponse = new JSONObject(apiResponse.getResponse());

            JSONArray streamsArray = jsonResponse.getJSONArray("streams");

            String[] usersStreaming = new String[streamsArray.length()];

            for (int i = 0; i < streamsArray.length(); i++) {
                usersStreaming[i] = streamsArray.getJSONObject(i).getJSONObject("channel").getString("name");
            }

            return usersStreaming;
        } catch (JSONException e) {
            e.printStackTrace();
            //Ignore exceptions
        }

        return null;
    }

}
