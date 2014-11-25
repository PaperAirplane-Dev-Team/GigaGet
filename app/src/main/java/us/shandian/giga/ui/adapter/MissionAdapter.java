package us.shandian.giga.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.get.DownloadMission;
import us.shandian.giga.ui.common.ProgressDrawable;
import us.shandian.giga.util.Utility;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder>
{
	private static final int[] BACKGROUNDS = new int[]{
		R.color.blue,
		R.color.red,
		R.color.green,
		R.color.orange,
		R.color.gray,
		R.color.purple
	};
	
	private static final int[] FOREGROUNDS = new int[]{
		R.color.blue_dark,
		R.color.red_dark,
		R.color.green_dark,
		R.color.orange_dark,
		R.color.gray_dark,
		R.color.purple_dark
	};
	
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
		DownloadMission ms = mManager.getMission(pos);
		h.mission = ms;
		h.letter.setText(ms.name.substring(0, 1));
		h.name.setText(ms.name);
		h.size.setText(Utility.formatBytes(ms.length));
		
		int first = ms.name.charAt(0);
		h.progress = new ProgressDrawable(mContext, BACKGROUNDS[first % BACKGROUNDS.length], FOREGROUNDS[first % FOREGROUNDS.length]);
		h.bkg.setBackgroundDrawable(h.progress);
		
		updateProgress(h);
	}

	@Override
	public int getItemCount() {
		return mManager.getCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private void updateProgress(ViewHolder h) {
		float progress = (float) h.mission.done / h.mission.length;
		h.status.setText(String.format("%.2f%%", progress * 100));
		h.progress.setProgress(progress);
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		public DownloadMission mission;
		
		public TextView status;
		public TextView letter;
		public TextView name;
		public TextView size;
		public View bkg;
		public ProgressDrawable progress;
		
		public ViewHolder(View v) {
			super(v);
			
			status = Utility.findViewById(v, R.id.item_status);
			letter = Utility.findViewById(v, R.id.item_letter);
			name = Utility.findViewById(v, R.id.item_name);
			size = Utility.findViewById(v, R.id.item_size);
			bkg = Utility.findViewById(v, R.id.item_bkg);
		}
	}
}
