package us.shandian.giga.util;

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
}
