package net.kdt.pojavlaunch.weblauncher;

import net.kdt.pojavlaunch.JMinecraftVersionList;

import java.io.Serializable;

public class TemporaryLaunchSettings implements Serializable {
    public JMinecraftVersionList.Version version;
    public String gameDir;
    public String runtimeId;
    public String javaArgs;
    public String controlFile;
    public String renderer;
    public String versionId = "1.7.10";
    public String mcUsername = "Steve";
    public String mcSession = "0";
    public String mcAccessToken = "0";
    public String mcUuid = "0";
    public String mcXuid = "0";
}
