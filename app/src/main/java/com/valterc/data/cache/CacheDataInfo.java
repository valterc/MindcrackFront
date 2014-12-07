package com.valterc.data.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Valter on 11/11/2014.
 */
public class CacheDataInfo {

    public static  final long TIME_MINUTE = 60000L;
    public static final long TIME_HOUR = 3600000L;
    public static final long TIME_DAY = 3600000L * 24;

    private String hashedKey;
    private long timestamp;
    private long lastAccessTimestamp;
    private long timeToLive;
    private int accessCount;
    private long size;
    private boolean invalid;

    public CacheDataInfo(String hashedKey, long size, long timeToLive) {
        this.hashedKey = hashedKey;
        this.size = size;
        this.timeToLive = timeToLive;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.lastAccessTimestamp = Calendar.getInstance().getTimeInMillis();
        this.accessCount = 0;
    }

    public CacheDataInfo(byte[] data, int offset, int length) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data, offset, length);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        setHashedKey(dataInputStream.readUTF());
        setTimestamp(dataInputStream.readLong());
        setLastAccessTimestamp(dataInputStream.readLong());
        setTimeToLive(dataInputStream.readLong());
        setAccessCount(dataInputStream.readInt());
        setSize(dataInputStream.readLong());

        dataInputStream.close();
    }

    public CacheDataInfo(DataInputStream dataInputStream) throws IOException {

        setHashedKey(dataInputStream.readUTF());
        setTimestamp(dataInputStream.readLong());
        setLastAccessTimestamp(dataInputStream.readLong());
        setTimeToLive(dataInputStream.readLong());
        setAccessCount(dataInputStream.readInt());
        setSize(dataInputStream.readLong());

    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        byte[] data = null;

        try {

            dataOutputStream.writeUTF(getHashedKey());
            dataOutputStream.writeLong(getTimestamp());
            dataOutputStream.writeLong(getLastAccessTimestamp());
            dataOutputStream.writeLong(getTimeToLive());
            dataOutputStream.writeInt(getAccessCount());
            dataOutputStream.writeLong(getSize());

            data = outputStream.toByteArray();
        }  finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    public boolean Expired(){
        if (Calendar.getInstance().getTimeInMillis() > timestamp + timeToLive) {
            return true;
        }

        return false;
    }

    public void AccessData(){
        setAccessCount(getAccessCount() + 1);
        setLastAccessTimestamp(Calendar.getInstance().getTimeInMillis());
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    public void setLastAccessTimestamp(long lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

}
