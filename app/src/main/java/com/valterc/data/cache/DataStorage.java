package com.valterc.data.cache;


import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Valter on 11/11/2014.
 */
public class DataStorage {

    private String baseDirectory;

    public DataStorage(String baseDirectory){
        this.baseDirectory = baseDirectory;
        File f = new File(baseDirectory);
        f.mkdirs();
    }

    public long SaveData(String file, InputStream inputStream){
        BufferedOutputStream outputStream = null;
        long totalSize = 0;

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(baseDirectory + file));
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while (len != -1) {
                totalSize += len;
                outputStream.write(buffer, 0, len);
                len = inputStream.read(buffer);
            }
        } catch (Exception e) {
            Log.d("DataStorage", "Error saving to file '" + file + "', " + e.getMessage()+ "!");
            totalSize = -1;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                //ignore exceptions
            }
        }

        return totalSize;
    }

    public long SaveData(String file, byte[] data){
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(baseDirectory + file));
            outputStream.write(data, 0, data.length);
        } catch (Exception e) {
            Log.d("DataStorage", "Error saving to file '" + file + "', " + e.getMessage()+ "!");
            return -1;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                //ignore exceptions
            }
        }

        return data.length;
    }

    public InputStream OpenData(String file){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(baseDirectory + file);
        } catch (FileNotFoundException e) {
            Log.d("DataStorage", "File '" + file + "' not found!");
        }
        return inputStream;
    }

    public boolean FileExists(String file){
        File f = new File(baseDirectory + file);
        return f.isFile() && f.exists();
    }

    public void CleanCacheDirectory(){
        File directory = new File(baseDirectory);
        DeleteRecursive(directory);
        directory.mkdirs();
    }

    public void DeleteData(String file){
        File f = new File(baseDirectory + file);
        f.delete();
    }

    private void DeleteRecursive(File file){
        if (file.isDirectory()){
            for (String f : file.list())
                DeleteRecursive(new File(baseDirectory + f));
        }

        file.delete();
    }

}
