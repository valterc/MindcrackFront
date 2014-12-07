package com.vcutils;

import org.apache.http.cookie.Cookie;

public class DownloadResponse<T> {

	public enum DownloadResult {
		Ok, ErrorUnknown, ErrorInternet, ErrorServer, Cached;
	}

	public DownloadResponse(DownloadResult result) {
		super();
		this.result = result;
		this.statusCode = -1;
	}

	public DownloadResponse(DownloadResult result, int statusCode, T response, Cookie cookie) {
		super();
		this.response = response;
		this.result = result;
		this.cookie = cookie;
		this.setStatusCode(statusCode);
	}

	private T response;
	private DownloadResult result;
	private Cookie cookie;
	private int statusCode;
	
	public T getResponse() {
		return response;
	}

	public DownloadResult getResult() {
		return result;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}