package com.valterc.data.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Valter on 11/11/2014.
 */
public class CacheInfo {

    private Hashtable<String, CacheDataInfo> dataInfo;
    private AtomicLong size;

    public CacheInfo() {
        dataInfo = new Hashtable<>();
        size = new AtomicLong();
    }

    public CacheInfo(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        size = new AtomicLong(dataInputStream.readLong());
        int dataCount = dataInputStream.readInt();

        this.dataInfo = new Hashtable<>(dataCount);

        for (int i = 0; i < dataCount; i++) {
            int dataSize = dataInputStream.readInt();
            CacheDataInfo dataInfo = new CacheDataInfo(dataInputStream);
            this.dataInfo.put(dataInfo.getHashedKey(), dataInfo);
        }

        dataInputStream.close();
    }

    public CacheInfo(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        size = new AtomicLong(dataInputStream.readLong());
        int dataCount = dataInputStream.readInt();

        this.dataInfo = new Hashtable<>(dataCount);

        for (int i = 0; i < dataCount; i++) {
            int dataSize = dataInputStream.readInt();
            CacheDataInfo dataInfo = new CacheDataInfo(dataInputStream);
            this.dataInfo.put(dataInfo.getHashedKey(), dataInfo);
        }
        inputStream.close();
    }

    public byte[] serialize() throws IOException {
        byte[] data = null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        try {
            dataOutputStream.writeLong(size.get());
            dataOutputStream.writeInt(dataInfo.size());


            for (Map.Entry<String, CacheDataInfo> dataInfo : this.dataInfo.entrySet()) {
                byte[] serializedDataInfo = dataInfo.getValue().serialize();

                if (serializedDataInfo == null)
                    continue;

                dataOutputStream.writeInt(serializedDataInfo.length);
                dataOutputStream.write(serializedDataInfo);
            }

            data = outputStream.toByteArray();

        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    public boolean IsDataCached(String key){
        return dataInfo.containsKey(key);
    }

    public CacheDataInfo GetDataInfo(String key){
        if (dataInfo.containsKey(key)) {
            return dataInfo.get(key);
        }
        return null;
    }

    public int GetDataCount(){
        return  dataInfo.size();
    }

    public long GetSize() {
        return size.get();
    }

    public void DeleteData(String key){
        if (!this.dataInfo.containsKey(key)){
            return;
        }

        CacheDataInfo cacheDataInfo = this.dataInfo.remove(key);
        size.addAndGet(-cacheDataInfo.getSize());
    }

    public void CacheData(String key, long size, long expiresIn){
        DeleteData(key);
        CacheDataInfo cacheDataInfo = new CacheDataInfo(key, size, expiresIn);
        this.size.addAndGet(size);
        dataInfo.put(key, cacheDataInfo);
    }

    public ArrayList<CacheDataInfo> GetLeastImportantData(){

        ArrayList<CacheDataInfo> sortedDataInfo = new ArrayList<CacheDataInfo>(dataInfo.values());

        try {
            Collections.sort(sortedDataInfo, new CacheDataInfoComparator());
        } catch (Exception e) {
            //ignore
        }

        return sortedDataInfo;
    }

    public ArrayList<CacheDataInfo> GetDataToDelete(){

        ArrayList<CacheDataInfo> dataInfoToDelete = new ArrayList<CacheDataInfo>();

        for (CacheDataInfo data : dataInfo.values()){
            if (data.isInvalid() || data.Expired()) {
                dataInfoToDelete.add(data);
            }
        }

        return dataInfoToDelete;
    }


    private class CacheDataInfoComparator implements Comparator<CacheDataInfo> {

        @Override
        public int compare(CacheDataInfo lhs /*-1*/, CacheDataInfo rhs /*1*/) {

            //Check for invalid files
            {
                if (lhs.isInvalid() && rhs.isInvalid())
                    return 0;

                if (lhs.isInvalid())
                    return -1;

                if (rhs.isInvalid())
                    return 1;
            }

            //Order by expiration
            {
                if (lhs.Expired() && rhs.Expired())
                    return 0;

                if (lhs.Expired())
                    return -1;

                if (rhs.Expired())
                    return 1;
            }

            //Order by size difference
            {
                if (lhs.getSize() < rhs.getSize() / 2)
                    return -1;

                if (lhs.getSize() / 2 > rhs.getSize())
                    return 1;
            }

            //Order by time difference
            {
                if (lhs.getTimestamp() + CacheDataInfo.TIME_MINUTE < rhs.getLastAccessTimestamp())
                    return -1;

                if (lhs.getTimestamp() - CacheDataInfo.TIME_MINUTE > rhs.getLastAccessTimestamp())
                    return 1;
            }

            //Order by access count
            {
                if (lhs.getAccessCount() < rhs.getAccessCount())
                    return -1;

                if (lhs.getAccessCount() > rhs.getAccessCount())
                    return 1;
            }

            //Order by last access time
            {
                if (lhs.getLastAccessTimestamp() < rhs.getLastAccessTimestamp())
                    return -1;

                if (lhs.getLastAccessTimestamp() > rhs.getLastAccessTimestamp())
                    return 1;
            }

            //Order by cache time
            {
                if (lhs.getTimestamp() < rhs.getTimestamp())
                    return -1;

                if (lhs.getTimestamp() > rhs.getTimestamp())
                    return 1;
            }

            //Consider that both object are "equal" in importance
            return 0;
        }

    }

}
