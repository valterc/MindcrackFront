package com.vcutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.AndroidRuntimeException;

public class IntentUtils {

	private IntentUtils() {
	}

	public static void actionView(Context c, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		c.startActivity(intent);
	}

	public static void actionView(Context c, String s) {
		IntentUtils.actionView(c, Uri.parse(s));
	}

	public static void actionChooser(Context c, Uri uri) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			c.startActivity(Intent.createChooser(intent, null));
		} catch (AndroidRuntimeException e) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				c.startActivity(Intent.createChooser(intent, null));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void actionChooser(Context c, String s) {
		IntentUtils.actionChooser(c, Uri.parse(s));
	}
	
	public static void shareText(Context c, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		c.startActivity(Intent.createChooser(intent, "Share to..."));
	}

	public static void shareLink(Context c, String text, String subject, String dialogTitle) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/*");
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		c.startActivity(Intent.createChooser(intent, dialogTitle));
	}
	
	public static void shareImage(Context c, String text, Uri imageUri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.putExtra(Intent.EXTRA_STREAM, imageUri);

		intent = Intent.createChooser(intent, "Share to...");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}

	public static void shareImage(Context c, String text, Bitmap image) throws IOException {

		File f = null;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			f = new File(android.os.Environment.getExternalStorageDirectory() + "/temp");
			f.mkdir();
		} else {
			f = new File(android.os.Environment.getDownloadCacheDirectory() + "/temp");
		}

		File noMediaFile = new File(f, ".nomedia");
		if (!noMediaFile.exists()) {
			FileWriter fw = new FileWriter(noMediaFile, false);
			fw.write("This file prevents the images in this folder to show up on the Gallery");
			fw.close();
		}

		File file = new File(f, "infinitegag_image.jpg");
		FileOutputStream os = new FileOutputStream(file, false);
		image.compress(CompressFormat.JPEG, 100, os);
		os.flush();
		os.close();

		Uri uri = Uri.fromFile(file);
		shareImage(c, text, uri);
	}
	
	public static void sendEmail(Context c, String email, String subject, String message){	
		Intent send = new Intent(Intent.ACTION_SENDTO);
		String uriText;

		uriText = "mailto:" + email + 
		          "?subject=" + subject + 
		          "&body=" + message;
		uriText = uriText.replace(" ", "%20");
		Uri uri = Uri.parse(uriText);

		send.setData(uri);
		c.startActivity(Intent.createChooser(send, "Send mail..."));

	}
	
}
