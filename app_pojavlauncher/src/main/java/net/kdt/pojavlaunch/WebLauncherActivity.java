package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.kdt.pojavlaunch.lifecycle.ContextExecutor;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.progresskeeper.TaskCountListener;
import net.kdt.pojavlaunch.services.ProgressServiceKeeper;
import net.kdt.pojavlaunch.utils.NotificationUtils;
import net.kdt.pojavlaunch.weblauncher.LaunchInterface;

public class WebLauncherActivity extends AppCompatActivity {
    private WebView mWebView;
    private NotificationManager mNotificationManager;
    private ProgressServiceKeeper mProgressServiceKeeper;

    private final TaskCountListener mDoubleLaunchPreventionListener = taskCount -> {
        // Hide the notification that starts the game if there are tasks executing.
        // Prevents the user from trying to launch the game with tasks ongoing.
        if(taskCount > 0) {
            Tools.runOnUiThread(() ->
                    mNotificationManager.cancel(NotificationUtils.NOTIFICATION_ID_GAME_START)
            );
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotificationManager = ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        ProgressKeeper.addTaskCountListener(mDoubleLaunchPreventionListener);

        mProgressServiceKeeper = new ProgressServiceKeeper(this);
        ProgressKeeper.addTaskCountListener(mProgressServiceKeeper);

        ContextExecutor.setActivity(this);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mWebView = new WebView(this);
        setContentView(mWebView, layoutParams);
        configureWebViewSettings();
        addWebInterfaces();

        if(savedInstanceState != null) mWebView.restoreState(savedInstanceState);
        else mWebView.loadUrl("javascript:LaunchInterface.startGame(\"{\\\"versionId\\\":\\\"1.12.2\\\"}\");");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebViewSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    private void addWebInterfaces() {
        mWebView.addJavascriptInterface(new LaunchInterface(this), "LaunchInterface");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContextExecutor.clearActivity();
        ProgressKeeper.removeTaskCountListener(mDoubleLaunchPreventionListener);
        ProgressKeeper.removeTaskCountListener(mProgressServiceKeeper);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        finish();
    }
}
