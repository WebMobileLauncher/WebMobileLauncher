package net.kdt.pojavlaunch.progresskeeper;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TaskPriorityManager implements TaskCountListener {
    public final List<ProgressStateKeeper> keeperList = new ArrayList<>();
    public final Listener outputListener;
    private boolean isOutputRunning;
    private boolean acceptsUpdates;
    public TaskPriorityManager(Listener outputListener) {
        this.outputListener = outputListener;
        acceptsUpdates = false;
        keeperList.add(new ProgressStateKeeper("unpack_runtime"));
        keeperList.add(new ProgressStateKeeper("download_verlist"));
        keeperList.add(new ProgressStateKeeper("authenticate_microsoft"));
        keeperList.add(new ProgressStateKeeper("extract_components"));
        keeperList.add(new ProgressStateKeeper("extract_single_files"));
        keeperList.add(new ProgressStateKeeper("download_minecraft"));
        acceptsUpdates = true;
        checkPriorities();
    }

    private synchronized void checkPriorities() {
        if(!acceptsUpdates) return;
        for(ProgressStateKeeper keeper : keeperList) {
            Log.i("TaskPriorityManager", keeper.label + " " +keeper.isRunning());
            if (keeper.isRunning()) {
                Log.i("TaskPritrityManager", "Attaching: "+keeper.label);
                outputListener.onProgressStarted();
                outputListener.setCancelButton(keeper.label.equals("download_minecraft"));
                keeper.attachListener(outputListener);
                return;
            }
        }
        outputListener.onProgressEnded();
    }

    public synchronized void detach()  {
        for(ProgressStateKeeper keeper : keeperList) {
            ProgressKeeper.removeListener(keeper.label, keeper);
        }
        keeperList.clear();
    }

    @Override
    public void onUpdateTaskCount(int count) {
        if(count < 1 && isOutputRunning) {
            isOutputRunning = false;
            outputListener.onProgressEnded();
        } else if(!isOutputRunning) {
            isOutputRunning = true;
            outputListener.onProgressStarted();
        }
    }

    public interface Listener extends ProgressListener {
        void setCancelButton(boolean enabled);
    }

    private class ProgressStateKeeper implements ProgressListener {
        private final String label;
        private boolean isProgressRunning;
        private int lastProgress, lastResId;
        private Object[] lastVa;
        private ProgressListener attachmentListener;

        public ProgressStateKeeper(String event) {
            this.label = event;
            ProgressKeeper.addListener(event, this);
        }

        public synchronized boolean isRunning() {
            return isProgressRunning;
        }

        public synchronized void attachListener(ProgressListener attachmentListener) {
            attachmentListener.onProgressUpdated(lastProgress, lastResId, lastVa);
            this.attachmentListener = attachmentListener;
        }

        @Override
        public void onProgressStarted() {
            Log.i("TaskPriorityManager","progressStarted "+ label);
            isProgressRunning = true;
            checkPriorities();
        }

        @Override
        public void onProgressUpdated(int progress, int resid, Object... va) {
            lastProgress = progress;
            lastResId = resid;
            lastVa = va;
            if(attachmentListener != null) attachmentListener.onProgressUpdated(progress, resid, va);
        }

        @Override
        public void onProgressEnded() {
            isProgressRunning = false;
            Log.i("TaskPriorityManager","progressEnded "+ label);
            checkPriorities();
        }
    }

}
