package com.valterc.data.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Valter on 11/11/2014.
 */
public class Cache {

    private final static int MAX_DRAIN = 16;
    private final static int MAX_CACHE_FILES = 64;
    private final static int MAX_CACHE_SIZE = 5 * 1024 * 1024; //5MB

    private static Cache instance;

    public static Cache getInstance(Context context) {
        if (instance == null)
            instance = new Cache(context);
        return instance;
    }

    private DataStorage storage;
    private CacheInfo cacheInfo;

    private Cache(Context context) {
        storage = new DataStorage(context.getCacheDir() + "/dc/");
        if (storage.FileExists("info")){
            try {
                cacheInfo = new CacheInfo(storage.OpenData("info"));
                Log.d("Cache", "Cache loaded!");
                Log.d("Cache", "Cache info ---------------");
                Log.d("Cache", "Data count: " + cacheInfo.GetDataCount());
                Log.d("Cache", "Total size: " + cacheInfo.GetSize() / 1024 + "KB");
                Log.d("Cache", "--------------------------");
            } catch (IOException e) {
                Log.d("Cache", "Error loading cache info, creating new cache!");
                storage.CleanCacheDirectory();
                cacheInfo = new CacheInfo();
            }
        } else {
            Log.d("Cache", "New cache");
            cacheInfo = new CacheInfo();
        }

    }

    private String ComputeHash(String key){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(key.getBytes());
            byte[] bytes = messageDigest.digest();

            StringBuilder hashedKey = new StringBuilder(64);
            for (int i=0; i < bytes.length; i++) {
                hashedKey.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return hashedKey.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean DataExistsHashedKey(String hashedKey){

        if (!cacheInfo.IsDataCached(hashedKey))
            return false;

        CacheDataInfo dataInfo = cacheInfo.GetDataInfo(hashedKey);
        if (dataInfo.Expired()) {
            return false;
        }

        return true;
    }

    private void DeleteInvalidData() {
        ArrayList<CacheDataInfo> dataToDelete = cacheInfo.GetDataToDelete();
        for (CacheDataInfo data : dataToDelete){
            cacheInfo.DeleteData(data.getHashedKey());
            storage.DeleteData(data.getHashedKey());
        }
    }

    private void ForceDeleteData() {
        ArrayList<CacheDataInfo> dataToDelete = cacheInfo.GetLeastImportantData();
        for (int i = 0; i <= MAX_DRAIN && i < dataToDelete.size(); i++) {
            CacheDataInfo data = dataToDelete.get(i);
            cacheInfo.DeleteData(data.getHashedKey());
            storage.DeleteData(data.getHashedKey());

            if (cacheInfo.GetSize() <= MAX_CACHE_SIZE && cacheInfo.GetDataCount() <= MAX_CACHE_FILES)
                break;
        }

        Log.d("Cache", "Cache was too large, items trimmed");

    }

    public boolean DataExists(String key){
        String hashedKey = ComputeHash(key);
        if (!cacheInfo.IsDataCached(hashedKey))
            return false;

        CacheDataInfo dataInfo = cacheInfo.GetDataInfo(hashedKey);
        return !dataInfo.Expired();
    }

    public InputStream GetData(String key) {
        String hashedKey = ComputeHash(key);

        if (!DataExistsHashedKey(hashedKey))
            return null;

        CacheDataInfo dataInfo = cacheInfo.GetDataInfo(hashedKey);
        dataInfo.AccessData();

        InputStream inputStream = storage.OpenData(hashedKey);

        if (inputStream == null) {
            dataInfo.setInvalid(true);
        }

        Log.d("Cache", "Data loaded: KEY=" + hashedKey + " INVALID=" + (dataInfo.isInvalid() || dataInfo.Expired()) + " SIZE=" + dataInfo.getSize() / 1024 + "KB");

        return inputStream;
    }

    public Bitmap GetDataBitmap(String key) {
        InputStream inputStream = GetData(key);
        if (inputStream == null) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public String GetDataString(String key) {
        InputStream inputStream = GetData(key);
        if (inputStream == null) {
            return null;
        }

        String dataString = null;
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            dataString = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dataString;
    }

    public void StoreData(String key, long expiresIn, InputStream inputStream){
        String hashedKey = ComputeHash(key);
        long fileSize = storage.SaveData(hashedKey, inputStream);
        cacheInfo.CacheData(hashedKey, fileSize == -1 ? 0 : fileSize, expiresIn);

        Log.d("Cache", "Data cached: KEY=" + hashedKey + " SIZE=" + fileSize / 1024 + "KB" + " EXPIRES_IN=" + expiresIn / CacheDataInfo.TIME_HOUR + "HOURS");

    }

    public void StoreData(String key, InputStream inputStream){
        StoreData(key, CacheDataInfo.TIME_DAY, inputStream);
    }

    public void StoreDataBitmap(String key, long expiresIn, Bitmap bitmap){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        StoreData(key, expiresIn, inputStream);

        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            //ignore
        }
    }

    public void StoreDataString(String key, long expiresIn, String string){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        ByteArrayInputStream inputStream = null;

        try {
            dataOutputStream.writeUTF(string);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            return;
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }

        StoreData(key, expiresIn, inputStream);

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

    public void Dispose() {

        if (cacheInfo.GetSize() <= MAX_CACHE_SIZE && cacheInfo.GetDataCount() <= MAX_CACHE_FILES){
            DeleteInvalidData();
        } else {
            ForceDeleteData();
        }

        try {
            storage.SaveData("info", cacheInfo.serialize());
/*
            Log.d("Cache", "Cache info ---------------");
            Log.d("Cache", "Data count: " + cacheInfo.GetDataCount());
            Log.d("Cache", "Total size: " + cacheInfo.GetSize() / 1024 + "KB");
            Log.d("Cache", "--------------------------");
*/
            Log.d("Cache", "Cache info stored");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void Clear(){
        storage.CleanCacheDirectory();
        cacheInfo = new CacheInfo();
    }

}
