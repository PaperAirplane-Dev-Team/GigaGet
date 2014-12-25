package us.shandian.giga.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import us.shandian.giga.R;
import us.shandian.giga.ui.common.ToolbarActivity;

public class SettingsActivity extends ToolbarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		
		getFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.settings;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;	
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

}
