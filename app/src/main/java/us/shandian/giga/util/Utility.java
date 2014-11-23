package us.shandian.giga.util;

import java.io.File;
import java.io.FileOutputStream;

public class Utility
{
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
	
	public static void writeToFile(String fileName, byte[] content) {
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
}
