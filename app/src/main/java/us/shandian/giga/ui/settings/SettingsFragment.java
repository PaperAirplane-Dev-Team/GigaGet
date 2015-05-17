package us.shandian.giga.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.webkit.WebView;

import us.shandian.giga.R;
import us.shandian.giga.util.Utility;
import us.shandian.giga.util.Settings;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	private static final String ABOUT = "about";
	private static final String DOWNLOAD_DIR = "download_directory";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		onSharedPreferenceChanged(null, DOWNLOAD_DIR);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		switch (preference.getKey()) {
			case ABOUT:
				showAboutDialog();
				return true;
			case DOWNLOAD_DIR:
				Utility.showDirectoryChooser(getActivity());
				return true;
			default:
				return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals(DOWNLOAD_DIR))
        {
			changeSummary(findPreference(key), getDownloadDir());
        }
    }
	
	private String getDownloadDir(){
		return Settings.getInstance(getActivity()).getString(Settings.DOWNLOAD_DIRECTORY, Settings.DEFAULT_PATH);
	}
	
	private void changeSummary(Preference pref, String summary){
		pref.setSummary(summary);
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
