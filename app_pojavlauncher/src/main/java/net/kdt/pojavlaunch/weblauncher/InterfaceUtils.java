package net.kdt.pojavlaunch.weblauncher;

import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;

public class InterfaceUtils {
    public static void checkForOngoingProgress() {
        if(ProgressKeeper.hasOngoingTasks())
            throw new IllegalStateException("Cannot execute method while tasks are ongoing");
    }
}
