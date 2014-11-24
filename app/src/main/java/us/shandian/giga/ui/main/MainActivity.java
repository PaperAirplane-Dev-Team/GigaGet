package us.shandian.giga.ui.main;

import android.os.Bundle;

import us.shandian.giga.R;
import us.shandian.giga.ui.common.ToolbarActivity;
import us.shandian.giga.ui.fragment.MissionsFragment;

public class MainActivity extends ToolbarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		
		// Fragment test
		MissionsFragment f = new MissionsFragment();
		Bundle b = new Bundle();
		b.putString("loc", "/storage/sdcard0/GigaGet");
		f.setArguments(b);
		getFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.main;
	}
}
