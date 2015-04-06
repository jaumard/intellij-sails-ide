package com.jaumard.sails.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.SwingHelper;
import com.jaumard.sails.bundle.SailsJSBundle;
import com.jaumard.sails.settings.SailsJSConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaumard on 04/04/2015.
 */
public class SailsJSUtil
{

    @NotNull
    public static TextFieldWithHistoryWithBrowseButton createSailsJSExecutableTextField(@Nullable Project project)
    {
        TextFieldWithHistoryWithBrowseButton field = SwingHelper.createTextFieldWithHistoryWithBrowseButton(
                project, SailsJSBundle.message("sails.conf.executable.name"),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), new NotNullProducer<List<String>>()
                {
                    @NotNull
                    @Override
                    public List<String> produce()
                    {
                        return
                                getDefaultExecutablePaths();
                    }
                });

        String executablePath = SailsJSConfig.getInstance().getExecutablePath();
        setDefaultValue(field, executablePath);

        return field;
    }

    @NotNull
    public static TextFieldWithHistoryWithBrowseButton createSailsJSScriptTextField(@Nullable Project project)
    {
        TextFieldWithHistoryWithBrowseButton field = SwingHelper.createTextFieldWithHistoryWithBrowseButton(
                project, SailsJSBundle.message("sails.conf.options.name"),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), new NotNullProducer<List<String>>()
                {
                    @NotNull
                    @Override
                    public List<String> produce()
                    {
                        return new ArrayList<String>();
                    }
                });

        setDefaultValue(field, "");

        return field;
    }

    @NotNull
    public static List<String> getDefaultExecutablePaths()
    {
        List<String> paths = ContainerUtil.newArrayList();
        ContainerUtil.addIfNotNull(paths, getPath(SailsJSCommandLine.PLATFORM_SAILS));
        ContainerUtil.addIfNotNull(paths, getPath(SailsJSCommandLine.PLATFORM_TREELINE));
        return paths;
    }

    private static void setDefaultValue(@NotNull TextFieldWithHistoryWithBrowseButton field, @Nullable String defaultValue)
    {
        final TextFieldWithHistory textFieldWithHistory = field.getChildComponent();

        if (StringUtil.isNotEmpty(defaultValue))
        {
            setTextFieldWithHistory(textFieldWithHistory, defaultValue);
        }
    }

    public static void setTextFieldWithHistory(TextFieldWithHistory textFieldWithHistory, String value)
    {
        if (null != value)
        {
            textFieldWithHistory.setText(value);
            textFieldWithHistory.addCurrentTextToHistory();
        }
    }

    @Nullable
    private static String getPath(@NotNull String name)
    {
        File path = PathEnvironmentVariableUtil.findInPath(SystemInfo.isWindows ? name + ".cmd" : name);
        return (path != null && path.exists()) ? path.getAbsolutePath() : null;
    }

    public static boolean isSailsProject(Project currentProject)
    {
        boolean isSails = false;
        VirtualFile baseDir = currentProject.getBaseDir();
        VirtualFile[] childs = baseDir.getChildren();
        for (VirtualFile child : childs)
        {
            if (child.getName().equals(".sailsrc"))
            {
                isSails = true;
            }
        }
        return isSails;
    }
}
