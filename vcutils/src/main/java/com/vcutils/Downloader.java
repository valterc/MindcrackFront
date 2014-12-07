package com.vcutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;

import com.vcutils.DownloadResponse.DownloadResult;

public class Downloader {

	private static final int HTTP_CLIENT_TIMEOUT_MILLISECONDS = 20000;

	private static DefaultHttpClient getDefaultHttpClient() {
		return getDefaultHttpClient(HTTP_CLIENT_TIMEOUT_MILLISECONDS);
	}

	private static DefaultHttpClient getDefaultHttpClient(int timeout) {
		HttpParams httpParameters = new BasicHttpParams();

		if (timeout > 0) {
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
			HttpConnectionParams.setSoTimeout(httpParameters, timeout);
		}
		// Set the buffer size to avoid OutOfMemoryError exceptions on certain
		// HTC devices.
		// http://stackoverflow.com/questions/5358014/android-httpclient-oom-on-4g-lte-htc-thunderbolt
		HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);

		return new DefaultHttpClient(httpParameters);
	}

	private static Cookie getCookie(DefaultHttpClient httpClient, String cookieName) {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals(cookieName))
				return c;
		}
		return null;
	}

	public static String httpEntityToString(HttpEntity entity) throws IOException {

		InputStream inputStream = entity.getContent();
		int numberBytesRead = 0;
		StringBuffer out = new StringBuffer();
		byte[] bytes = new byte[4096];

		while (numberBytesRead != -1) {
			out.append(new String(bytes, 0, numberBytesRead));
			numberBytesRead = inputStream.read(bytes);
		}

		inputStream.close();

		return out.toString();
	}
	
	public static String httpInputSteamToString(InputStream inputStream) throws IOException {

		int numberBytesRead = 0;
		StringBuffer out = new StringBuffer();
		byte[] bytes = new byte[4096];

		while (numberBytesRead != -1) {
			out.append(new String(bytes, 0, numberBytesRead));
			numberBytesRead = inputStream.read(bytes);
		}

		inputStream.close();

		return out.toString();
	}
	

	public static DownloadResponse<String> downloadNoRedirectGetLocation(String url, String cookieName, String userAgent, String referer) {
		HttpGet httpget = new HttpGet(url);

		if (userAgent != null)
			httpget.addHeader(HTTP.USER_AGENT, userAgent);
		if (referer != null)
			httpget.addHeader("Referer", referer);

		DefaultHttpClient httpClient = getDefaultHttpClient();

		httpClient.setRedirectHandler(new RedirectHandler() {

			@Override
			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
				return false;
			}

			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
				return null;
			}
		});

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header[] headers = response.getHeaders("Location");

				if (headers != null && headers.length != 0) {
					responseString = headers[headers.length - 1].getValue();
				} else {
					responseString = null;
				}
			} else {
				responseString = httpEntityToString(response.getEntity());
			}

			if (cookieName != null)
				responseCookie = getCookie(httpClient, cookieName);
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		if (response == null || responseString == null) {
			return new DownloadResponse<String>(DownloadResult.ErrorServer);
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadNoRedirectGetLocation(String url, Cookie cookie, String cookieName, String userAgent, String referer) {
		HttpGet httpget = new HttpGet(url);

		if (userAgent != null)
			httpget.addHeader(HTTP.USER_AGENT, userAgent);
		if (referer != null)
			httpget.addHeader("Referer", referer);

		DefaultHttpClient httpClient = getDefaultHttpClient();

		if (cookie != null)
			httpClient.getCookieStore().addCookie(cookie);

		httpClient.setRedirectHandler(new RedirectHandler() {

			@Override
			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
				return false;
			}

			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
				return null;
			}
		});

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header[] headers = response.getHeaders("Location");

				if (headers != null && headers.length != 0) {
					responseString = headers[headers.length - 1].getValue();
				} else {
					responseString = null;
				}
			} else {
				responseString = httpEntityToString(response.getEntity());
			}

			if (cookieName != null)
				responseCookie = getCookie(httpClient, cookieName);
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadGet(String url, String cookieName, String userAgent, String referer) {
		HttpGet httpget = new HttpGet(url);

		if (userAgent != null)
			httpget.addHeader(HTTP.USER_AGENT, userAgent);
		if (referer != null)
			httpget.addHeader("Referer", referer);

		DefaultHttpClient httpClient = getDefaultHttpClient();

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();
			responseString = httpEntityToString(response.getEntity());
			if (cookieName != null)
				responseCookie = getCookie(httpClient, cookieName);
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		if (response == null || responseString == null) {
			return new DownloadResponse<String>(DownloadResult.ErrorServer);
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadGet(String url, Cookie cookie, String userAgent, String referer) {
		HttpGet httpget = new HttpGet(url);

		if (userAgent != null)
			httpget.addHeader(HTTP.USER_AGENT, userAgent);
		if (referer != null)
			httpget.addHeader("Referer", referer);

		DefaultHttpClient httpClient = getDefaultHttpClient();

		if (cookie != null)
			httpClient.getCookieStore().addCookie(cookie);

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();
			responseString = httpEntityToString(response.getEntity());
			if (cookie != null)
				responseCookie = getCookie(httpClient, cookie.getName());
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		if (response == null || responseString == null || response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return new DownloadResponse<String>(DownloadResult.ErrorServer);
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadGet(String url, Cookie cookie) {
		return downloadGet(url, cookie, null, null);
	}

	public static DownloadResponse<String> downloadGet(String url) {
		return downloadGet(url, (String) null, null, null);
	}

	public static DownloadResponse<String> downloadPost(String url, String[] names, String[] values, String cookieName, String userAgent, String referer) {

		if (names != null && values != null)
			if (names.length != values.length)
				throw new RuntimeException("'values' must be the same size as 'names'");

		DefaultHttpClient httpClient = getDefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		if (referer != null)
			httppost.addHeader("Referer", referer);
		if (userAgent != null)
			httppost.addHeader(HTTP.USER_AGENT, userAgent);

		try {
			if (names != null && values != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(names.length);
				for (int i = 0; i < names.length; i++) {
					nameValuePairs.add(new BasicNameValuePair(names[i], values[i]));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			}
		} catch (UnsupportedEncodingException e1) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		}

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httppost);
			statusCode = response.getStatusLine().getStatusCode();
			responseString = httpEntityToString(response.getEntity());
			if (cookieName != null)
				responseCookie = getCookie(httpClient, cookieName);
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadPost(String url, String[] names, String[] values, Cookie cookie, String userAgent, String referer) {

		if (names != null && values != null)
			if (names.length != values.length)
				throw new RuntimeException("'values' must be the same size as 'names'");

		DefaultHttpClient httpClient = getDefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		if (referer != null)
			httppost.addHeader("Referer", referer);
		if (userAgent != null)
			httppost.addHeader(HTTP.USER_AGENT, userAgent);
		if (cookie != null)
			httpClient.getCookieStore().addCookie(cookie);

		try {
			if (names != null && values != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(names.length);
				for (int i = 0; i < names.length; i++) {
					nameValuePairs.add(new BasicNameValuePair(names[i], values[i]));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			}
		} catch (UnsupportedEncodingException e1) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		}

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httppost);
			statusCode = response.getStatusLine().getStatusCode();
			responseString = httpEntityToString(response.getEntity());
			if (cookie != null)
				responseCookie = getCookie(httpClient, cookie.getName());
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}

	public static DownloadResponse<String> downloadPost(String url, String[] names, String[] values, Cookie cookie) {
		return downloadPost(url, names, values, cookie, null, null);
	}

	public static DownloadResponse<String> downloadPost(String url, String[] names, String[] values) {
		return downloadPost(url, names, values, (String) null, null, null);
	}

	public static DownloadResponse<Bitmap> downloadImage(String url, String userAgent, String referer) {

		Bitmap bitmap = null;

		DefaultHttpClient httpClient = getDefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		if (referer != null)
			httpGet.addHeader("Referer", referer);
		if (userAgent != null)
			httpGet.addHeader(HTTP.USER_AGENT, userAgent);

		int statusCode = -1;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			statusCode = response.getStatusLine().getStatusCode();
			InputStream inputStream = response.getEntity().getContent();
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			return new DownloadResponse<Bitmap>(DownloadResult.ErrorInternet);
		} catch (Exception e) {
			WLog.logException("Downloader", "Error downloading image with url: " + url, e);
			e.printStackTrace();
			return new DownloadResponse<Bitmap>(DownloadResult.ErrorUnknown);
		} catch (OutOfMemoryError e) {
			WLog.logException("Downloader", "Out of Memory!", e);
			return new DownloadResponse<Bitmap>(DownloadResult.ErrorUnknown);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		return new DownloadResponse<Bitmap>(DownloadResult.Ok, statusCode, bitmap, null);
	}

    public static DownloadResponse<Bitmap> downloadImage(String url) {

        Bitmap bitmap = null;

        DefaultHttpClient httpClient = getDefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        int statusCode = -1;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            InputStream inputStream = response.getEntity().getContent();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            return new DownloadResponse<Bitmap>(DownloadResult.ErrorInternet);
        } catch (Exception e) {
            WLog.logException("Downloader", "Error downloading image with url: " + url, e);
            e.printStackTrace();
            return new DownloadResponse<Bitmap>(DownloadResult.ErrorUnknown);
        } catch (OutOfMemoryError e) {
            WLog.logException("Downloader", "Out of Memory!", e);
            return new DownloadResponse<Bitmap>(DownloadResult.ErrorUnknown);
        } finally {
            if (httpClient != null)
                httpClient.getConnectionManager().shutdown();
        }

        return new DownloadResponse<Bitmap>(DownloadResult.Ok, statusCode, bitmap, null);
    }
	
	
	public static DownloadResponse<String> downloadGetGZip(String url, String cookieName, String userAgent, String referer) {
		HttpGet httpget = new HttpGet(url);

		if (userAgent != null)
			httpget.addHeader(HTTP.USER_AGENT, userAgent);
		if (referer != null)
			httpget.addHeader("Referer", referer);

		DefaultHttpClient httpClient = getDefaultHttpClient();

		HttpResponse response = null;
		String responseString = null;
		Cookie responseCookie = null;
		int statusCode = -1;
		try {
			response = httpClient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();
			
			
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				InputStream inputStream = AndroidHttpClient.getUngzippedContent(response.getEntity());
				responseString = httpInputSteamToString(inputStream);
			} else {
				responseString = httpEntityToString(response.getEntity());
			}

			
			if (cookieName != null)
				responseCookie = getCookie(httpClient, cookieName);
		} catch (ClientProtocolException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorUnknown);
		} catch (IOException e) {
			return new DownloadResponse<String>(DownloadResult.ErrorInternet);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}

		if (response == null || responseString == null) {
			return new DownloadResponse<String>(DownloadResult.ErrorServer);
		}

		return new DownloadResponse<String>(DownloadResult.Ok, statusCode, responseString, responseCookie);
	}
	
	
}
