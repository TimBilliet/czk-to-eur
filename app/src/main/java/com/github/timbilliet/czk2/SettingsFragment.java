package com.github.timbilliet.czk2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private ListPreference rateType;
    private Preference rateAmount;
    private MainActivity mainActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

//        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.isHomefrag = false;
        mainActivity.invalidateOptionsMenu();
//        rateType = findPreference("rate_type");
//        rateAmount = findPreference("rate_amount");
//
//        rateAmount.setVisible(rateType.getValue().equals("own_rate"));
//
//        rateType.setOnPreferenceChangeListener((preference, newValue) -> {
//            System.out.println("newval:" + newValue);
//            rateAmount.setVisible(newValue.equals("own_rate"));
//            return true;
//        });
//
//        rateAmount.setOnPreferenceChangeListener((preference, newValue) -> {
//            mainActivity.invalidateOptionsMenu();
//            return true;
//        });
//        MenuItem item = mainActivity.menu.findItem(R.id.action_settings);
//        item.setVisible(false);
    }

}