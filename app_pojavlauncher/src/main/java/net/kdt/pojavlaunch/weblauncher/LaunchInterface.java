package net.kdt.pojavlaunch.weblauncher;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.lifecycle.ContextAwareDoneListener;
import net.kdt.pojavlaunch.tasks.AsyncMinecraftDownloader;

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
        JMinecraftVersionList.Version listedVersion = AsyncMinecraftDownloader.getListedVersion(launchSettings.versionId);
        new AsyncMinecraftDownloader().start(
                activity,
                listedVersion,
                launchSettings.versionId,
                new ContextAwareDoneListener(
                        activity.getBaseContext(),
                        launchSettings.versionId,
                        launchSettings
                )
        );
    }
}
