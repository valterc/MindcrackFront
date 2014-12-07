package com.vcutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.os.Environment;

public class ImageUtils {

	public static void saveBitmap(Context c, Bitmap image, String name, String path) throws Exception {

		File f = null;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			f = new File(android.os.Environment.getExternalStorageDirectory() + path);
			f.mkdir();
		} else {
			f = new File(android.os.Environment.getDataDirectory() + path);
		}

		File file = new File(f, name);
		FileOutputStream os = new FileOutputStream(file, false);
		image.compress(CompressFormat.JPEG, 100, os);
		os.flush();
		os.close();

		// MediaStore.Images.Media.insertImage(c.getContentResolver(),
		// file.getAbsolutePath(), file.getName(), file.getName());
		MediaScannerConnection.scanFile(c, new String[] { file.getAbsolutePath() }, null, null);
		// MediaScannerConnection scanner = new MediaScannerConnection(c, null);
		// scanner.scanFile(filePath, null);

	}
	
	public static String getBitmapSize(Bitmap b){
		
		if (b == null)
			return "0KB";
		
		int size = b.getByteCount();
		
		if (size / (1024 * 1024) < 1)
			return (size / 1024) + "KB";
		else 
			return (size / (1024 * 1024)) + "MB";
	}
	
	public static byte[] bitmapToByteArray(Bitmap b){
		if (b == null)
			return null;
		
		ByteArrayOutputStream outputStream = null;
		byte[] bytes = null;
		
		try {
			outputStream = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			bytes = outputStream.toByteArray();
			
		} catch (Error e) {
			e.printStackTrace();	
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
				}
				outputStream = null;
			}
		}
		
		return bytes;
	}
	
	public static Bitmap byteArrayToBitmap(byte[] bytes){
		if (bytes == null)
			return null;
		ByteArrayInputStream inputStream = null;
		Bitmap bitmap = null;
		
		try {
			inputStream = new ByteArrayInputStream(bytes);
	    	bitmap = BitmapFactory.decodeStream(inputStream);
	    	
		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				inputStream = null;
			}
		}
		
		return bitmap;
	}

}
