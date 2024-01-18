package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.kdt.pojavlaunch.lifecycle.ContextExecutor;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.progresskeeper.TaskCountListener;
import net.kdt.pojavlaunch.progresskeeper.TaskPriorityManager;
import net.kdt.pojavlaunch.services.ProgressServiceKeeper;
import net.kdt.pojavlaunch.tasks.AsyncVersionList;
import net.kdt.pojavlaunch.tasks.MinecraftDownloader;
import net.kdt.pojavlaunch.utils.NotificationUtils;
import net.kdt.pojavlaunch.weblauncher.LaunchInterface;

public class WebLauncherActivity extends BaseActivity implements TaskPriorityManager.Listener {
    private WebView mWebView;
    private View mProgressView;
    private View mCancelButton;
    private TextView mProgressLabel;
    private ProgressBar mProgressBar;
    private NotificationManager mNotificationManager;
    private ProgressServiceKeeper mProgressServiceKeeper;
    private TaskPriorityManager mTaskPriorityManager;

    static {
        new AsyncVersionList().getVersionList(false);
    }

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

        setContentView(R.layout.activity_ww);

        mProgressView = findViewById(R.id.main_progressLayout);
        mProgressLabel = findViewById(R.id.main_progressText);
        mProgressBar = findViewById(R.id.main_progressBar);
        mCancelButton = findViewById(R.id.main_cancelButton);
        mCancelButton.setOnClickListener((v)-> MinecraftDownloader.cancelDownload());
        mTaskPriorityManager = new TaskPriorityManager(this);
        mWebView = findViewById(R.id.main_webView);
        configureWebViewSettings();
        addWebInterfaces();

        if(savedInstanceState != null) mWebView.restoreState(savedInstanceState);
        else {
            //mWebView.loadUrl("javascript:LaunchInterface.startGame(\"{\\\"versionId\\\":\\\"1.12.2\\\"}\");");
            mWebView.loadUrl("javascript:LaunchInterface.openSettings();");
        }
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
        mTaskPriorityManager.detach();
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

    @Override
    public void onProgressStarted() {
        runOnUiThread(()->mProgressView.setVisibility(View.VISIBLE));
    }

    @Override
    public void onProgressUpdated(int progress, int resid, Object... va) {
        runOnUiThread(()->{
            String text = "";
            if(resid != 0 && resid != 0xffffffff) text = getString(resid, va);
            mProgressBar.setProgress(progress);
            mProgressLabel.setText(text);
        });
    }

    @Override
    public void onProgressEnded() {
        runOnUiThread(()->mProgressView.setVisibility(View.GONE));
    }

    @Override
    public void setCancelButton(boolean enabled) {
        runOnUiThread(()->mCancelButton.setVisibility(enabled ? View.VISIBLE : View.GONE));
    }
}
