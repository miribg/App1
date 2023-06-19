package com.example.softw1;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class PreferenceFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {;
        addPreferencesFromResource(R.xml.pref_config);
    }

    public void onResume(){
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    public void onPause(){
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(requireContext());
        switch (s){
            case "idioma": //cambiar idioma
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(preferencias.getString("idioma",null));
                AppCompatDelegate.setApplicationLocales(appLocale);
                break;

        }
    }
}
