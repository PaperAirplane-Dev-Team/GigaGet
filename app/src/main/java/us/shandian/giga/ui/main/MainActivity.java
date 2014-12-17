package us.shandian.giga.ui.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.service.DownloadManagerService;
import us.shandian.giga.ui.adapter.NavigationAdapter;
import us.shandian.giga.ui.common.FloatingActionButton;
import us.shandian.giga.ui.common.ToolbarActivity;
import us.shandian.giga.ui.fragment.MissionsFragment;
import us.shandian.giga.util.CrashHandler;
import us.shandian.giga.util.Utility;

public class MainActivity extends ToolbarActivity
{
	private MissionsFragment mFragment;
	private DrawerLayout mDrawer;
	private ListView mList;
	private NavigationAdapter mAdapter;
	private ActionBarDrawerToggle mToggle;
	private DownloadManager mManager;
	private DownloadManagerService.DMBinder mBinder;
	
	private String mPendingUrl;
	private SharedPreferences mPrefs;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName p1, IBinder binder) {
			mBinder = (DownloadManagerService.DMBinder) binder;
			mManager = mBinder.getDownloadManager();
		}

		@Override
		public void onServiceDisconnected(ComponentName p1) {
			
		}

		
	};

	@Override
	@TargetApi(21)
	protected void onCreate(Bundle savedInstanceState) {
		CrashHandler.register();
		
		// Service
		Intent i = new Intent();
		i.setClass(this, DownloadManagerService.class);
		startService(i);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		
		mPrefs = getSharedPreferences("threads", Context.MODE_WORLD_READABLE);
		
		// Drawer
		mDrawer = Utility.findViewById(this, R.id.drawer);
		mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, 0, 0);
		mToggle.setDrawerIndicatorEnabled(true);
		mDrawer.setDrawerListener(mToggle);
		
		if (Build.VERSION.SDK_INT >= 21) {
			findViewById(R.id.nav).setElevation(20.0f);
		} else {
			mDrawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
		}
		
		mList = Utility.findViewById(this, R.id.nav_list);
		mAdapter = new NavigationAdapter(this, R.array.drawer_items, R.array.drawer_icons);
		mList.setAdapter(mAdapter);
		
		// FAB
		new FloatingActionButton.Builder(this)
			.withButtonColor(getResources().getColor(R.color.blue))
			.withButtonSize(80)
			.withDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp))
			.withGravity(Gravity.RIGHT | Gravity.BOTTOM)
			.withPaddings(0, 0, 10, 10)
			.create()
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showUrlDialog();
				}
			});
		
		// Fragment
		mFragment = new MissionsFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mFragment).commit();
		
		// Intent
		if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
			mPendingUrl = getIntent().getData().toString();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			mPendingUrl = intent.getData().toString();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (mPendingUrl != null) {
			showUrlDialog();
			mPendingUrl = null;
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.main;
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mToggle.onConfigurationChanged(newConfig);
	}
	
	private void showUrlDialog() {
		// Create the view
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_url, null);
		final EditText text = Utility.findViewById(v, R.id.url);
		final EditText name = Utility.findViewById(v, R.id.file_name);
		final TextView tCount = Utility.findViewById(v, R.id.threads_count);
		final SeekBar threads = Utility.findViewById(v, R.id.threads);
		final Toolbar toolbar = Utility.findViewById(v, R.id.toolbar);
		final Button fetch = Utility.findViewById(v, R.id.fetch_name);

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

		toolbar.setTitle(R.string.add);
		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.inflateMenu(R.menu.dialog_url);

		// Show the dialog
		final AlertDialog dialog = new AlertDialog.Builder(this)
			.setCancelable(true)
			.setView(v)
			.create();

		dialog.show();

		fetch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new NameFetcherTask().execute(text, name);
				}
			});

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (item.getItemId() == R.id.okay) {
						String url = text.getText().toString().trim();
						String fName = name.getText().toString().trim();

						File f = new File(mManager.getLocation() + "/" + fName);

						if (f.exists()) {
							Toast.makeText(MainActivity.this, R.string.msg_exists, Toast.LENGTH_SHORT).show();
						} else if (!checkURL(url)) {
							Toast.makeText(MainActivity.this, R.string.msg_url_malform, Toast.LENGTH_SHORT).show();
						} else {

							while (mBinder == null);

							mBinder.startMission(url, fName, threads.getProgress() + 1);
							mFragment.notifyChange();

							mPrefs.edit().putInt("threads", threads.getProgress() + 1).commit();
							dialog.dismiss();
						}

						return true;
					} else {
						return false;
					}
				}
			});

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
