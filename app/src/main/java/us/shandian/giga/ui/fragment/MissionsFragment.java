package us.shandian.giga.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.ui.adapter.MissionAdapter;
import us.shandian.giga.ui.web.BrowserActivity;
import us.shandian.giga.util.Utility;

public class MissionsFragment extends Fragment
{
	private DownloadManager mManager;
	
	private RecyclerView mList;
	private MissionAdapter mAdapter;
	private GridLayoutManager mGridManager;
	
	private SharedPreferences mPrefs;
	
	private String mPendingUrl = null;

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
		
		mPrefs = getActivity().getSharedPreferences("threads", Context.MODE_WORLD_READABLE);
		
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
			case R.id.add:
				showUrlDialog();
				return true;
			case R.id.browser:
				Intent i = new Intent();
				i.setAction(Intent.ACTION_MAIN);
				i.setClass(getActivity(), BrowserActivity.class);
				getActivity().startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (mPendingUrl != null) {
			showUrlDialog();
			mPendingUrl = null;
		}
	}
	
	public void setPendingUrl(String url) {
		mPendingUrl = url;
	}
	
	private void showUrlDialog() {
		// Create the view
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_url, null);
		final EditText text = Utility.findViewById(v, R.id.url);
		final EditText name = Utility.findViewById(v, R.id.file_name);
		final TextView tCount = Utility.findViewById(v, R.id.threads_count);
		final SeekBar threads = Utility.findViewById(v, R.id.threads);
		
		threads.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
					tCount.setText(String.valueOf(progress + 1));
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1) {
					
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1) {
					
				}
				
		});
		
		int def = mPrefs.getInt("threads", 4);
		threads.setProgress(def - 1);
		tCount.setText(String.valueOf(def));
		
		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
				
			}

			@Override
			public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
				
				String url = text.getText().toString().trim();

				if (!url.equals("")) {
					int index = url.lastIndexOf("/");

					if (index > 0) {
						int end = url.lastIndexOf("?");

						if (end == -1) {
							end = url.length();
						}

						name.setText(url.substring(index + 1, end));
					}
				}
			}

			@Override
			public void afterTextChanged(Editable txt) {
				
			}	
		});
		
		if (mPendingUrl != null) {
			text.setText(mPendingUrl);
		}
		
		// Show the dialog
		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setView(v)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						String url = text.getText().toString().trim();
						String fName = name.getText().toString().trim();
						
						File f = new File(mManager.getLocation() + "/" + fName);
						
						if (f.exists()) {
							Toast.makeText(getActivity(), R.string.msg_exists, Toast.LENGTH_SHORT).show();
						} else {				
							mManager.startMission(url, fName, threads.getProgress() + 1);
							mAdapter.notifyDataSetChanged();
						
							mPrefs.edit().putInt("threads", threads.getProgress() + 1).commit();
						}
					}
				})
				.create()
				.show();
	}
}
