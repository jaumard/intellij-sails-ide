package com.jaumard.sails.settings;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jaumard on 04/04/2015.
 */
@State(
        name = "SailsJSConfig",
        storages = {
                @Storage(
                        file = StoragePathMacros.APP_CONFIG + "/sails.xml"
                )}
)
public class SailsJSConfig implements PersistentStateComponent<SailsJSConfig>
{
    public String SAILSJS_PATH = "/usr/local/bin/sails";
    public static String SAILSJS_WORK_DIRECTORY = "sails.js.settings.workdir";
    private String executableOptions;


    public static SailsJSConfig getInstance()
    {
        return ServiceManager.getService(SailsJSConfig.class);
    }

    @Nullable
    @Override
    public SailsJSConfig getState()
    {
        return this;
    }

    @Override
    public void loadState(SailsJSConfig state)
    {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getExecutablePath()
    {

        return SAILSJS_PATH;
    }

    public void setExecutablePath(String path)
    {
        SAILSJS_PATH = path;
    }

    public void setExecutableOptions(String executableOptions)
    {
        this.executableOptions = executableOptions;
    }

    public String getExecutableOptions()
    {
        return executableOptions;
    }
}
