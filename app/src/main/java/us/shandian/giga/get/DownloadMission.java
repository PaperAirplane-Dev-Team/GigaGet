package us.shandian.giga.get;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import us.shandian.giga.util.Utility;

public class DownloadMission
{
	
	public static interface MissionListener {
		public void onProgressUpdate(long done, long total);
		public void onFinish();
	}
	
	public static final int ERROR_SERVER_UNSUPPORTED = 206;
	
	public String name = "";
	public String url = "";
	public String location = "";
	public long blocks = 0;
	public long length = 0;
	public long done = 0;
	public int threadCount = 3;
	public int finishCount = 0;
	public ArrayList<Long> threadPositions = new ArrayList<Long>();
	public HashMap<Long, Boolean> blockState = new HashMap<Long, Boolean>();
	public boolean running = false;
	public boolean finished = false;
	public int errCode = -1;
	
	private transient ArrayList<MissionListener> mListeners = new ArrayList<MissionListener>();
	private transient boolean mWritingToFile = false;
	
	public boolean isBlockPreserved(long block) {
		return blockState.containsKey(block) ? blockState.get(block) : false;
	}
	
	public void preserveBlock(long block) {
		synchronized (blockState) {
			blockState.put(block, true);
		}
	}
	
	public void setPosition(int id, long position) {
		threadPositions.set(id, position);
	}
	
	public long getPosition(int id) {
		return threadPositions.get(id);
	}
	
	public synchronized void notifyProgress(long deltaLen) {
		done += deltaLen;
		
		if (done > length) {
			done = length;
		}
		
		writeThisToFile();
		
		for (MissionListener listener : mListeners) {
			if (listener != null) {
				listener.onProgressUpdate(done, length);
			}
		}
	}
	
	public synchronized void notifyFinished() {
		finishCount++;
		
		if (finishCount == threadCount) {
			onFinish();
		}
	}
	
	private void onFinish() {
		running = false;
		finished = true;
		
		deleteThisFromFile();
		
		for (MissionListener listener : mListeners) {
			listener.onFinish();
		}
	}
	
	public synchronized void addListener(MissionListener listener) {
		mListeners.add(listener);
	}
	
	public synchronized void removeListener(MissionListener listener) {
		mListeners.remove(listener);
	}
	
	public void start(Context context) {
		if (!running && !finished) {
			running = true;
			
			Handler handler = new Handler(context.getMainLooper());
			for (int i = 0; i < threadCount; i++) {
				if (threadPositions.size() <= i) {
					threadPositions.add((long) i);
				}
				new Thread(new DownloadRunnable(this, handler, i)).start();
			}
		}
	}
	
	public void pause() {
		if (running) {
			running = false;
			
			// TODO: Notify & Write state to info file
			// if (err)
		}
	}
	
	private void writeThisToFile() {
		if (!mWritingToFile) {
			mWritingToFile = true;
			new Thread() {
				@Override
				public void run() {
					doWriteThisToFile();
					mWritingToFile = false;
				}
			}.start();
		}
	}
	
	private void doWriteThisToFile() {
		synchronized (blockState) {
			Utility.writeToFile(location + "/" + name + ".giga", new Gson().toJson(this));
		}
	}
	
	private void deleteThisFromFile() {
		new File(location + "/" + name + ".giga").delete();
	}
}
