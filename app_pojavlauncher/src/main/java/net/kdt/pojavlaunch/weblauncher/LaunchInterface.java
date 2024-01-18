package net.kdt.pojavlaunch.weblauncher;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.SettingsActivity;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.lifecycle.ContextAwareDoneListener;
import net.kdt.pojavlaunch.tasks.AsyncMinecraftDownloader;
import net.kdt.pojavlaunch.tasks.MinecraftDownloader;

public class LaunchInterface {
    private final Activity activity;

    public LaunchInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void startGame(String gameLaunchConfig) {
        InterfaceUtils.checkForOngoingProgress();
        TemporaryLaunchSettings launchSettings = Tools.GLOBAL_GSON.fromJson(gameLaunchConfig, TemporaryLaunchSettings.class);
        JMinecraftVersionList.Version version = launchSettings.version;
        if(version == null) version = AsyncMinecraftDownloader.getListedVersion(launchSettings.versionId);
        new MinecraftDownloader().start(
                activity,
                version,
                launchSettings.versionId,
                Tools.getGameDirPath(launchSettings),
                new ContextAwareDoneListener(
                        activity.getBaseContext(),
                        launchSettings.versionId,
                        launchSettings
                )
        );
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void openSettings() {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }
}
