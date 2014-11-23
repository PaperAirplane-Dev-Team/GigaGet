package us.shandian.giga.get;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadMission
{
	
	public static interface MissionListener {
		public void onProgressUpdate(long done, long total);
	}
	
	public String name = "";
	public String url = "";
	public String location = "";
	public long blocks = 0;
	public long length = 0;
	public long done = 0;
	public int threadCount = 3;
	public ArrayList<Long> threadPositions = new ArrayList<Long>();
	public HashMap<Long, Boolean> blockState = new HashMap<Long, Boolean>();
	public boolean running = false;
	public boolean err = false;
	
	private transient ArrayList<MissionListener> mListeners = new ArrayList<MissionListener>();
	
	public boolean isBlockPreserved(long block) {
		return blockState.containsKey(block) ? blockState.get(block) : false;
	}
	
	public void preserveBlock(long block) {
		blockState.put(block, true);
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
		
		for (MissionListener listener : mListeners) {
			if (listener != null) {
				listener.onProgressUpdate(done, length);
			}
		}
	}
	
	public synchronized void addListener(MissionListener listener) {
		mListeners.add(listener);
	}
	
	public synchronized void removeListener(MissionListener listener) {
		mListeners.remove(listener);
	}
	
	public void start(Context context) {
		if (!running) {
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
}
