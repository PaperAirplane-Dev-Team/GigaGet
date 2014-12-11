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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	private SharedPreferences mPrefs;
	
	private String mPendingUrl = null;
	
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
		getActivity().startService(i);
		getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		
		// Views
		mList = Utility.findViewById(v, R.id.mission_recycler);
		
		// Init
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
			case R.id.about:
				showAboutDialog();
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
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
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
						} else if (!checkURL(url)) {
							Toast.makeText(getActivity(), R.string.msg_url_malform, Toast.LENGTH_SHORT).show();
						} else {
							
							while (mBinder == null);
							
							mBinder.startMission(url, fName, threads.getProgress() + 1);
							mAdapter.notifyDataSetChanged();
						
							mPrefs.edit().putInt("threads", threads.getProgress() + 1).commit();
						}
					}
				})
				.setNeutralButton(R.string.msg_fetch_filename, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
					}
				})
				.create();
				
				final View.OnClickListener l = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new NameFetcherTask().execute(text, name);
					}
				};
				
				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(l);
					}
				});
				
				dialog.show();
	}
	
	private boolean checkURL(String url) {
		try {
			URL u = new URL(url);
			u.openConnection();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	private class NameFetcherTask extends AsyncTask<View, Void, Object[]> {

		@Override
		protected Object[] doInBackground(View[] params) {
			try {
				URL url = new URL(((EditText) params[0]).getText().toString());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				String header = conn.getHeaderField("Content-Disposition");
				
				if (header != null && header.indexOf("=") != -1) {
					return new Object[]{params[1], header.split("=")[1].replace("\"", "")};
				}
			} catch (Exception e) {
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object[] result)	{
			super.onPostExecute(result);
			
			if (result != null) {
				((EditText) result[0]).setText(result[1].toString());
			}
		}
	}
}
