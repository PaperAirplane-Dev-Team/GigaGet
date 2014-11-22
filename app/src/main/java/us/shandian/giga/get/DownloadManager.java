package us.shandian.giga.get;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadManager
{
	public static final int BLOCK_SIZE = 512 * 1024;
	
	private ArrayList<DownloadMission> mMissions = new ArrayList<DownloadMission>();
	
	public int startMission(String url, String name, String location) {
		DownloadMission mission = new DownloadMission();
		mission.url = url;
		mission.name = name;
		mission.location = location;
		mMissions.add(mission);
		new Initializer(mission).start();
		return mMissions.size() - 1;
	}
	
	private class Initializer extends Thread {
		private DownloadMission mission;
		
		public Initializer(DownloadMission mission) {
			this.mission = mission;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(mission.url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				mission.length = conn.getContentLength();
				mission.blocks = mission.length / BLOCK_SIZE;
				
				if (mission.blocks * BLOCK_SIZE < mission.length) {
					mission.blocks++;
				}
				

				new File(mission.location).mkdirs();
				new File(mission.location + "/" + mission.name).createNewFile();
				RandomAccessFile af = new RandomAccessFile(mission.location + "/" + mission.name, "rw");
				af.setLength(mission.length);
				af.close();
				
				mission.start();
			} catch (Exception e) {
				// TODO Notify
				throw new RuntimeException(e);
			}
		}
	}
}
