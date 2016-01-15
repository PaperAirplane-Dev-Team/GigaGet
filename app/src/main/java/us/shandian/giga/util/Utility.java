package us.shandian.giga.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadMission;
import us.shandian.giga.util.Settings;
import us.shandian.giga.ui.settings.SettingsActivity;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

public class Utility
{
	
	public static enum FileType {
		APP,
		VIDEO,
		EXCEL,
		WORD,
		POWERPOINT,
		MUSIC,
		ARCHIVE,
		UNKNOWN
	}
	
	public static String formatBytes(long bytes) {
		if (bytes < 1024) {
			return String.format("%d B", bytes);
		} else if (bytes < 1024 * 1024) {
			return String.format("%.2f kB", (float) bytes / 1024);
		} else if (bytes < 1024 * 1024 * 1024) {
			return String.format("%.2f MB", (float) bytes / 1024 / 1024);
		} else {
			return String.format("%.2f GB", (float) bytes / 1024 / 1024 / 1024);
		}
	}
	
	public static String formatSpeed(float speed) {
		if (speed < 1024) {
			return String.format("%.2f B/s", speed);
		} else if (speed < 1024 * 1024) {
			return String.format("%.2f kB/s", speed / 1024);
		} else if (speed < 1024 * 1024 * 1024) {
			return String.format("%.2f MB/s", speed / 1024 / 1024);
		} else {
			return String.format("%.2f GB/s", speed / 1024 / 1024 / 1024);
		}
	}
	
	public static void writeToFile(String fileName, String content) {
		try {
			writeToFile(fileName, content.getBytes("UTF-8"));
		} catch (Exception e) {
			
		}
	}
	
	private static void writeToFile(String fileName, byte[] content) {
		File f = new File(fileName);
		
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
				
			}
		}
		
		try {
			FileOutputStream opt = new FileOutputStream(f, false);
			opt.write(content, 0, content.length);
			opt.close();
		} catch (Exception e) {
			
		}
	}
	
	public static String readFromFile(String file) {
		try {
			File f = new File(file);
			
			if (!f.exists() || !f.canRead()) {
				return null;
			}
			
			BufferedInputStream ipt = new BufferedInputStream(new FileInputStream(f));
			
			byte[] buf = new byte[512];
			StringBuilder sb = new StringBuilder();
			
			while (ipt.available() > 0) {
				int len = ipt.read(buf, 0, 512);
				sb.append(new String(buf, 0, len, "UTF-8"));
			}
			
			ipt.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <T> T findViewById(View v, int id) {
		return (T) v.findViewById(id);
	}
	
	public static <T> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}
	
	public static String getFileExt(String url) {
		if (url.indexOf("?")>-1) {
			url = url.substring(0,url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf(".") );
			if (ext.indexOf("%")>-1) {
				ext = ext.substring(0,ext.indexOf("%"));
			}
			if (ext.indexOf("/")>-1) {
				ext = ext.substring(0,ext.indexOf("/"));
			}
			return ext.toLowerCase();

		}
	}
	
	public static String getErrorString(Context context, int code) {
		switch (code) {
			case DownloadMission.ERROR_SERVER_UNSUPPORTED:
				return context.getString(R.string.msg_server_unsupported);
			default:
				return "";
		}
	}
	
	public static FileType getFileType(String file) {
		if (file.endsWith(".apk")) {
			return FileType.APP;
		} else if (file.endsWith(".mp3") || file.endsWith(".wav") || file.endsWith(".flac")) {
			return FileType.MUSIC;
		} else if (file.endsWith(".mp4") || file.endsWith(".mpeg") || file.endsWith(".rm") || file.endsWith(".rmvb")
					|| file.endsWith(".flv") || file.endsWith(".webp")) {
			return FileType.VIDEO;
		} else if (file.endsWith(".doc") || file.endsWith(".docx")) {
			return FileType.WORD;
		} else if (file.endsWith(".xls") || file.endsWith(".xlsx")) {
			return FileType.EXCEL;
		} else if (file.endsWith(".ppt") || file.endsWith(".pptx")) {
			return FileType.POWERPOINT;
		} else if (file.endsWith(".zip") || file.endsWith(".rar") || file.endsWith(".7z") || file.endsWith(".gz")
					|| file.endsWith("tar") || file.endsWith(".bz")) {
			return FileType.ARCHIVE;
		} else {
			return FileType.UNKNOWN;
		}
	}
	
	public static int getBackgroundForFileType(FileType type) {
		switch (type) {
			case APP:
				return R.color.orange;
			case MUSIC:
				return R.color.cyan;
			case ARCHIVE:
				return R.color.blue;
			case VIDEO:
				return R.color.green;
			case WORD:
			case EXCEL:
			case POWERPOINT:
				return R.color.brown;
			case UNKNOWN:
			default:
				return R.color.bluegray;
		}
	}
	
	public static int getForegroundForFileType(FileType type) {
		switch (type) {
			case APP:
				return R.color.orange_dark;
			case MUSIC:
				return R.color.cyan_dark;
			case ARCHIVE:
				return R.color.blue_dark;
			case VIDEO:
				return R.color.green_dark;
			case WORD:
			case EXCEL:
			case POWERPOINT:
				return R.color.brown_dark;
			case UNKNOWN:
			default:
				return R.color.bluegray_dark;
		}
	}
	
	public static int getThemeForFileType(FileType type) {
		switch (type) {
			case APP:
				return R.style.Theme_App_Orange;
			case MUSIC:
				return R.style.Theme_App_Cyan;
			case ARCHIVE:
				return R.style.Theme_App_Blue;
			case VIDEO:
				return R.style.Theme_App_Green;
			case WORD:
			case EXCEL:
			case POWERPOINT:
				return R.style.Theme_App_Brown;
			case UNKNOWN:
			default:
				return R.style.Theme_App_BlueGray;
		}
	}
	
	public static int getIconForFileType(FileType type) {
		switch (type) {
			case APP:
				return R.drawable.apps;
			case MUSIC:
				return R.drawable.music;
			case ARCHIVE:
				return R.drawable.archive;
			case VIDEO:
				return R.drawable.video;
			case WORD:
				return R.drawable.word;
			case EXCEL:
				return R.drawable.excel;
			case POWERPOINT:
				return R.drawable.powerpoint;
			case UNKNOWN:
			default:
				return R.drawable.unknown;
		}
	}
	
	private static boolean isDirectoryAvailble(String path) {
		File dir = new File(path);
		return dir.exists() && dir.isDirectory();
	}
	
	private static boolean isDownloadDirectoryAvailble(Context context) {
		return isDirectoryAvailble(Settings.getInstance(context).getString(Settings.DOWNLOAD_DIRECTORY, Settings.DEFAULT_PATH));
	}
	
	public static void changeDownloadDirectory(Context context, String path) {
		Settings.getInstance(context).putString(Settings.DOWNLOAD_DIRECTORY, path);
	}
	
	public static void showDirectoryChooser(Activity activity) {
		Intent i = new Intent(activity, FilePickerActivity.class);
		i.setAction(Intent.ACTION_GET_CONTENT);
		i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
		i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
		i.putExtra(FilePickerActivity.EXTRA_MODE, AbstractFilePickerFragment.MODE_DIR);
		activity.startActivityForResult(i, 233);
	}
	
	public static void processDirectoryChange(int requestCode, int resultCode,
                                    Intent data, Activity activity){
		if (requestCode != 233) return; //Oh devils!
		if (resultCode !=  Activity.RESULT_OK) {
				checkAndReshow(activity);
				return;
		}
		Settings.getInstance(activity).putString(Settings.DOWNLOAD_DIRECTORY, data.getData().toString().substring(7));
		if (activity instanceof SettingsActivity){
			activity.recreate(); //FIXME Just a workaround because OnSharedPreferenceChangeListener is NOT WORKING.
		}
		Toast.makeText(activity.getApplicationContext(),
				R.string.need_restart, Toast.LENGTH_LONG).show();
		//remove "file://" from the path
	}
	
	private static void checkAndReshow(Activity activity){
		if (!isDownloadDirectoryAvailble(activity)){
			Toast.makeText(activity.getApplicationContext(),
				R.string.no_available_dir, Toast.LENGTH_LONG).show();
			showDirectoryChooser(activity);
		}
	}
	
	public static void copyToClipboard(Context context, String str) {
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setPrimaryClip(ClipData.newPlainText("text", str));
		Toast.makeText(context, R.string.msg_copied, Toast.LENGTH_SHORT).show();
	}
	
	public static String checksum(String path, String algorithm) {
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		FileInputStream i = null;
		
		try {
			i = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		byte[] buf = new byte[1024];
		int len = 0;
		
		try {
			while ((len = i.read(buf)) != -1) {
				md.update(buf, 0, len);
			}
		} catch (IOException e) {
			
		}
		
		byte[] digest = md.digest();
		
		// HEX
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
		
	}
}
