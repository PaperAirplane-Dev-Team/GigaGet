package us.shandian.giga.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static us.shandian.giga.BuildConfig.DEBUG;

public class FlvxzApi
{
	private static final String TAG = FlvxzApi.class.getSimpleName();
	private static final String REQUEST = "http://api.flvxz.com/token/%s/url/%s/jsonp/purejson";
	
	private static FlvxzApi sInstance;
	
	private String mToken;
	
	public static FlvxzApi getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new FlvxzApi(context);
		}
		
		return sInstance;
	}
	
	private FlvxzApi(Context context) {
		try {
			mToken = ApiUtils.readInputStream(context.getAssets().open("flvxz")).replace("\n", "");
		} catch (Exception e) {
			mToken = null;
		}
		
		if (DEBUG) {
			Log.d(TAG, mToken);
		}
	}
	
	public ArrayList<String> detectVideos(String url) {
		ArrayList<String> ret = new ArrayList<String>();
		
		if (mToken != null) {
			String encoded = ApiUtils.base64url(url);
			String result = ApiUtils.httpGet(String.format(REQUEST, mToken, encoded));
			
			if (DEBUG) {
				Log.d(TAG, encoded);
				if (result != null) {
					Log.d(TAG, result);
				}
			}
			
			try {
				JSONArray json = new JSONArray(result);
				
				for (int i = 0; i < json.length(); i++) {
					JSONObject obj = json.getJSONObject(i);
					JSONArray array = obj.getJSONArray("files");
					
					for (int j = 0; j < array.length(); j++) {
						String u = array.getJSONObject(j).optString("furl");
						ret.add(u.replace("\\/", "/"));
					}
				}
			} catch (JSONException e) {
				if (DEBUG) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
		}
		
		return ret;
	}
	
}
