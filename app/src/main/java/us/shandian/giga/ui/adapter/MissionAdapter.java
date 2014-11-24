package us.shandian.giga.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.get.DownloadMission;
import us.shandian.giga.util.Utility;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder>
{
	private Context mContext;
	private LayoutInflater mInflater;
	private DownloadManager mManager;
	
	public MissionAdapter(Context context, DownloadManager manager) {
		mContext = context;
		mManager = manager;
		
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public MissionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(mInflater.inflate(R.layout.mission_item, parent, false));
	}

	@Override
	public void onBindViewHolder(MissionAdapter.ViewHolder h, int pos) {
		// TODO: Implement this method
	}

	@Override
	public int getItemCount() {
		return mManager.getCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View v) {
			super(v);
		}
	}
}
