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

import java.io.*;
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
        final List<String> defaultExecutablePaths = getDefaultExecutablePaths();
        TextFieldWithHistoryWithBrowseButton field = SwingHelper.createTextFieldWithHistoryWithBrowseButton(
                project, SailsJSBundle.message("sails.conf.executable.name"),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), new NotNullProducer<List<String>>()
                {
                    @NotNull
                    @Override
                    public List<String> produce()
                    {
                        return
                                defaultExecutablePaths;
                    }
                });

        String executablePath = SailsJSConfig.getInstance().getExecutablePath();
        if (executablePath == null && defaultExecutablePaths.size() > 0)
        {
            setDefaultValue(field, defaultExecutablePaths.get(0));
        }
        else
        {
            setDefaultValue(field, executablePath);
        }

        return field;
    }

    @NotNull
    public static TextFieldWithHistoryWithBrowseButton createNPMExecutableTextField(@Nullable Project project)
    {
        final List<String> defaultNpmExecutablePaths = getDefaultNpmExecutablePaths();
        TextFieldWithHistoryWithBrowseButton field = SwingHelper.createTextFieldWithHistoryWithBrowseButton(
                project, SailsJSBundle.message("sails.conf.npm.executable.name"),
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), new NotNullProducer<List<String>>()
                {
                    @NotNull
                    @Override
                    public List<String> produce()
                    {
                        return
                                defaultNpmExecutablePaths;
                    }
                });

        String executablePath = SailsJSConfig.getInstance().getNpmExecutable();
        if (executablePath == null && defaultNpmExecutablePaths.size() > 0)
        {
            setDefaultValue(field, defaultNpmExecutablePaths.get(0));
        }
        else
        {
            setDefaultValue(field, executablePath);
        }

        return field;
    }

    private static List<String> getDefaultNpmExecutablePaths()
    {
        List<String> paths = ContainerUtil.newArrayList();
        ContainerUtil.addIfNotNull(paths, getPath(SailsJSCommandLine.NPM));
        return paths;
    }

    public static void copyFileFromAssets(InputStream inputStream, String pathToWrite)
    {
        OutputStream outputStream = null;

        try
        {
            File newFile = new File(pathToWrite);
            if (!newFile.exists())
            {
                newFile.createNewFile();
            }
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File(pathToWrite));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1)
            {
                outputStream.write(bytes, 0, read);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (outputStream != null)
            {
                try
                {
                    // outputStream.flush();
                    outputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (String aChildren : children)
            {
                if (!deleteDir(new File(dir, aChildren)))
                {
                    return false;
                }
            }

        }
        // The directory is now empty or this is a file so delete it
        return dir.delete();
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
        if (null == value)
        {
            if (textFieldWithHistory.getHistory().size() > 0)
            {
                textFieldWithHistory.setSelectedItem(textFieldWithHistory.getHistory().get(0));
            }
        }
        else
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
