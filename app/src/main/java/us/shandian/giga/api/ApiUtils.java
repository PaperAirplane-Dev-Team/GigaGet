package us.shandian.giga.api;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

import static us.shandian.giga.BuildConfig.DEBUG;

public class ApiUtils
{
	private static final String TAG = ApiUtils.class.getSimpleName();
	
	public static String httpGet(String url) {
		
		if (DEBUG) {
			Log.d(TAG, url);
		}
		
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			
			if (DEBUG) {
				Log.d(TAG, "response " + conn.getResponseCode());
			}
			
			return readInputStream(conn.getInputStream());
		} catch (Exception e) {
			
			if (DEBUG) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			
			return null;
		}
	}
	
	public static String base64url(String url) {
		return Base64.encodeToString(url.replace("://", ":##").getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
	}
	
	public static String readInputStream(InputStream i) throws IOException {
		BufferedInputStream s = new BufferedInputStream(i);
		byte[] buf = new byte[512];
		int len = -1;
		StringBuilder sb = new StringBuilder();

		while ((len = s.read(buf, 0, buf.length)) != -1) {
			sb.append(new String(buf, 0, len));
		}

		return sb.toString();
	}
}
