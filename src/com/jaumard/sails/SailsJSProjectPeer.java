package com.jaumard.sails;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.WebProjectGenerator;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.jaumard.sails.settings.SailsJSConfig;
import com.jaumard.sails.utils.SailsJSCommandLine;
import com.jaumard.sails.utils.SailsJSUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jaumard on 04/04/2015.
 */
public class SailsJSProjectPeer implements WebProjectGenerator.GeneratorPeer<SailsJSProjectGenerator.SailsJSProjectSettings>
{

    private final List<WebProjectGenerator.SettingsStateListener> myStateListeners = ContainerUtil.createLockFreeCopyOnWriteList();
    private TextFieldWithHistoryWithBrowseButton myExecutablePathField;

    private final ConcurrentMap<String, Boolean> myValidateCache = ContainerUtil.newConcurrentMap();

    SailsJSProjectPeer()
    {
    }

    @NotNull
    @Override
    public JComponent getComponent()
    {
        setFields();
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Sails/Treeline :", myExecutablePathField)
                .getPanel();

        panel.setPreferredSize(JBUI.size(600, 40));
        return panel;
    }

    @Override
    public void buildUI(@NotNull SettingsStep settingsStep)
    {
        setFields();
        settingsStep.addSettingsField("Sails/Treeline :", myExecutablePathField);
    }

    @NotNull
    @Override
    public SailsJSProjectGenerator.SailsJSProjectSettings getSettings()
    {
        SailsJSProjectGenerator.SailsJSProjectSettings settings = new SailsJSProjectGenerator.SailsJSProjectSettings();
        SailsJSConfig.getInstance().setExecutablePath(myExecutablePathField.getText());
        settings.setExecutable(myExecutablePathField.getText());

        return settings;
    }

    private void setFields()
    {
        myExecutablePathField = SailsJSUtil.createSailsJSExecutableTextField(null);
    }

    @Nullable
    @Override
    public ValidationInfo validate()
    {
        String path = myExecutablePathField.getText();

        boolean error;

        if (myValidateCache.containsKey(path))
        {
            error = myValidateCache.get(path);
        }
        else
        {
            try
            {
                if (StringUtil.isEmpty(path))
                {
                    return new ValidationInfo("Please select path to executable");
                }

                new SailsJSCommandLine(path, null).version();
                error = false;
            }
            catch (Exception e)
            {
                error = true;
            }
            myValidateCache.put(path, error);
        }
        return error ? new ValidationInfo("Incorrect Sails executable") : null;
    }

    @Override
    public boolean isBackgroundJobRunning()
    {
        return false;
    }

    @Override
    public void addSettingsStateListener(@NotNull WebProjectGenerator.SettingsStateListener listener)
    {
        myStateListeners.add(listener);
    }
}
