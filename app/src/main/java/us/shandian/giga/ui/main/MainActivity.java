package us.shandian.giga.ui.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import us.shandian.giga.R;
import us.shandian.giga.ui.adapter.NavigationAdapter;
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

	@Override
	@TargetApi(21)
	protected void onCreate(Bundle savedInstanceState) {
		CrashHandler.register();
		
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		
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
		
		// Fragment
		mFragment = new MissionsFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame, mFragment).commit();
		
		// Intent
		if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
			mFragment.setPendingUrl(getIntent().getData().toString());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			mFragment.setPendingUrl(intent.getData().toString());
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

}
