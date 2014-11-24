package us.shandian.giga.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.ui.adapter.MissionAdapter;
import us.shandian.giga.util.Utility;

public class MissionsFragment extends Fragment
{
	private DownloadManager mManager;
	
	private RecyclerView mList;
	private MissionAdapter mAdapter;
	private GridLayoutManager mGridManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.missions, container, false);
		
		// Args
		if (mManager == null) {
			String loc = getArguments().getString("loc");
			mManager = new DownloadManager(getActivity(), loc);
		}
		
		// Views
		mList = Utility.findViewById(v, R.id.mission_recycler);
		
		// Init
		mAdapter = new MissionAdapter(getActivity(), mManager);
		mList.setAdapter(mAdapter);
		mGridManager = new GridLayoutManager(getActivity(), 2);
		mList.setLayoutManager(mGridManager);
		
		return v;
	}
}
