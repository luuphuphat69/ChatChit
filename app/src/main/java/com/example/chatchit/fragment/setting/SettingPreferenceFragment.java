package com.example.chatchit.fragment.setting;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.chatchit.R;

public class SettingPreferenceFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences( @Nullable Bundle savedInstanceState, @Nullable String rootKey ) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference backUp = getPreferenceManager().findPreference("backup");
        Preference restore = getPreferenceManager().findPreference("restore");
        backUp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( @NonNull Preference preference ) {
                    return false;
            }
        });
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( @NonNull Preference preference ) {
                return false;
            }
        });
    }
}