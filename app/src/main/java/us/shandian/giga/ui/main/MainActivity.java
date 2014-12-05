package us.shandian.giga.ui.main;

import android.content.Intent;
import android.os.Bundle;

import us.shandian.giga.R;
import us.shandian.giga.ui.common.ToolbarActivity;
import us.shandian.giga.ui.fragment.MissionsFragment;

public class MainActivity extends ToolbarActivity
{
	private MissionsFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		
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
}
