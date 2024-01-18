package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceControlFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceVideoFragment;
import net.kdt.pojavlaunch.utils.FragmentUtils;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private View mainSettingsView;
    private View controlSettingsView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        mainSettingsView = findViewById(R.id.settings_button1);
        controlSettingsView = findViewById(R.id.settings_button2);
        mainSettingsView.setOnClickListener(this);
        controlSettingsView.setOnClickListener(this);
        mainSettingsView.setEnabled(false);
        controlSettingsView.setEnabled(true);
        findViewById(R.id.settings_resetToDefaults).setOnClickListener(this::onClickResetToDefault);
        findViewById(R.id.settings_openGameDir).setOnClickListener(this::onClickOpenGameDir);
        FragmentUtils.replaceFragment(
                getSupportFragmentManager(),
                new LauncherPreferenceVideoFragment(),
                null,
                false,
                true
        );
    }

    @Override
    public void onClick(View v) {
        if(v == mainSettingsView) {
            mainSettingsView.setEnabled(false);
            controlSettingsView.setEnabled(true);
            FragmentUtils.replaceFragment(
                    getSupportFragmentManager(),
                    new LauncherPreferenceVideoFragment(),
                    null,
                    false,
                    true
            );
        }
        if(v == controlSettingsView) {
            controlSettingsView.setEnabled(false);
            mainSettingsView.setEnabled(true);
            FragmentUtils.replaceFragment(
                    getSupportFragmentManager(),
                    new LauncherPreferenceControlFragment(),
                    null,
                    false,
                    false
            );
        }
    }
    @SuppressLint("ApplySharedPref")
    public void onClickResetToDefault(View v) {
        LauncherPreferences.DEFAULT_PREF.edit().clear().commit();
        MainActivity.fullyExit();
    }
    public void onClickOpenGameDir(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                DocumentsContract.buildDocumentUri(
                        getString(net.kdt.pojavlaunch.R.string.storageProviderAuthorities),
                        Tools.DIR_GAME_HOME),
                DocumentsContract.Document.MIME_TYPE_DIR
        );
        startActivity(intent);
    }
}
