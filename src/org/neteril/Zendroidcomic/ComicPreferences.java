package org.neteril.Zendroidcomic;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ComicPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
