package us.shandian.giga.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.service.DownloadManagerService;
import us.shandian.giga.ui.adapter.MissionAdapter;
import us.shandian.giga.ui.web.BrowserActivity;
import us.shandian.giga.util.Utility;

public class MissionsFragment extends Fragment
{
	private DownloadManager mManager;
	private DownloadManagerService.DMBinder mBinder;
	
	private RecyclerView mList;
	private MissionAdapter mAdapter;
	private GridLayoutManager mGridManager;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mBinder = (DownloadManagerService.DMBinder) binder;
			mManager = mBinder.getDownloadManager();
			mAdapter = new MissionAdapter(getActivity(), mBinder);
			mList.setAdapter(mAdapter);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// What to do?
		}

		
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.missions, container, false);
		
		// Bind the service
		Intent i = new Intent();
		i.setClass(getActivity(), DownloadManagerService.class);
		getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		
		// Views
		mList = Utility.findViewById(v, R.id.mission_recycler);
		
		// Init
		mGridManager = new GridLayoutManager(getActivity(), 2);
		mList.setLayoutManager(mGridManager);
		
		setHasOptionsMenu(true);
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.frag_mission, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.browser:
				Intent i = new Intent();
				i.setAction(Intent.ACTION_MAIN);
				i.setClass(getActivity(), BrowserActivity.class);
				getActivity().startActivity(i);
				return true;
			case R.id.about:
				showAboutDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void notifyChange() {
		mAdapter.notifyDataSetChanged();
	}
	
	private void showAboutDialog() {
		WebView v = new WebView(getActivity());
		v.loadUrl("file:///android_asset/licenses.html");
		
		new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.about)
			.setNegativeButton(R.string.msg_close, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// Noting to do
				}
			})
			.show();
	}
}
