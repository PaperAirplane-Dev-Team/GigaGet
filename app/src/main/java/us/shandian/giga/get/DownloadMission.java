package us.shandian.giga.get;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadMission
{
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
	}
	
	public void start() {
		if (!running) {
			running = true;
			for (int i = 0; i < threadCount; i++) {
				if (threadPositions.size() <= i) {
					threadPositions.add((long) i);
				}
				new Thread(new DownloadRunnable(this, i)).start();
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
