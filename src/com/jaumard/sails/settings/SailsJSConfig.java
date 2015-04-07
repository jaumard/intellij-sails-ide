package com.jaumard.sails.settings;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jaumard.sails.SailsJSProjectGenerator;
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
    private String npmExecutable = "/usr/local/bin/npm";
    private String defaultPPCSS = SailsJSProjectGenerator.SailsJSProjectSettings.PPCSS_SASS;


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

    public String getNpmExecutable()
    {
        return npmExecutable;
    }

    public void setNpmExecutable(String npmExecutable)
    {
        this.npmExecutable = npmExecutable;
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

    public void setDefaultPPCSS(String defaultPPCSS)
    {
        this.defaultPPCSS = defaultPPCSS;
    }

    public String getDefaultPPCSS()
    {
        return defaultPPCSS;
    }
}
