package us.shandian.giga.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void showUrlDialog() {
		// Create the view
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_url, null);
		final EditText text = Utility.findViewById(v, R.id.url);
		final EditText name = Utility.findViewById(v, R.id.file_name);
		
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
						
						mManager.startMission(url, name.getText().toString().trim());
						mAdapter.notifyDataSetChanged();
						
						// TODO Check for illegal url or file name
					}
				})
				.create()
				.show();
	}
}
