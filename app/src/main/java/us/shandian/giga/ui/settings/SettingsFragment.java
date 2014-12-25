package us.shandian.giga.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.webkit.WebView;

import us.shandian.giga.R;

public class SettingsFragment extends PreferenceFragment
{
	private static final String ABOUT = "about";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		switch (preference.getKey()) {
			case ABOUT:
				showAboutDialog();
				return true;
			default:
				return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
	
	private void showAboutDialog() {
		WebView v = new WebView(getActivity());
		v.loadUrl("file:///android_asset/licenses.html");

		new AlertDialog.Builder(getActivity())
			.setView(v)
			.setNegativeButton(R.string.msg_close, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// Noting to do
				}
			})
			.show();
	}
}
