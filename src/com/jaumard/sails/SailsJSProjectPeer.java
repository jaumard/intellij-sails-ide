package com.jaumard.sails;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.WebProjectGenerator;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.FormBuilder;
import com.jaumard.sails.bundle.SailsJSBundle;
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
    private ComboBox ppCSS;
    private final List<WebProjectGenerator.SettingsStateListener> myStateListeners = ContainerUtil.createLockFreeCopyOnWriteList();
    private TextFieldWithHistoryWithBrowseButton myExecutablePathField;
    private TextFieldWithHistoryWithBrowseButton myNPMExecutablePathField;

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
                .addLabeledComponent(SailsJSBundle.message("sails.conf.name") + " :", myExecutablePathField)
                .addLabeledComponent(SailsJSBundle.message("sails.conf.ppCSS") + " :", ppCSS)
                .getPanel();

        return panel;
    }

    @Override
    public void buildUI(@NotNull SettingsStep settingsStep)
    {
        setFields();
        settingsStep.addSettingsField(SailsJSBundle.message("sails.conf.executable.name") + " :", myExecutablePathField);
        settingsStep.addSettingsField(SailsJSBundle.message("sails.conf.npm.executable.name"), myNPMExecutablePathField);
        settingsStep.addSettingsField(SailsJSBundle.message("sails.conf.ppCSS") + " :", ppCSS);
    }

    @NotNull
    @Override
    public SailsJSProjectGenerator.SailsJSProjectSettings getSettings()
    {
        SailsJSProjectGenerator.SailsJSProjectSettings settings = new SailsJSProjectGenerator.SailsJSProjectSettings();
        SailsJSConfig.getInstance().setExecutablePath(myExecutablePathField.getText());
        SailsJSConfig.getInstance().setDefaultPPCSS((String) ppCSS.getSelectedItem());
        settings.setExecutable(myExecutablePathField.getText());
        settings.setNpmExecutable(myNPMExecutablePathField.getText());
        settings.setPpCSS((String) ppCSS.getSelectedItem());

        return settings;
    }

    private void setFields()
    {
        if (ppCSS == null)
        {
            ppCSS = new ComboBox(new String[]{SailsJSProjectGenerator.SailsJSProjectSettings.PPCSS_SASS, SailsJSProjectGenerator.SailsJSProjectSettings.PPCSS_LESS});
            ppCSS.setSelectedItem(SailsJSConfig.getInstance().getDefaultPPCSS());
        }
        if (myNPMExecutablePathField == null)
        {
            myNPMExecutablePathField = SailsJSUtil.createNPMExecutableTextField(null);
        }
        if (myExecutablePathField == null)
        {
            myExecutablePathField = SailsJSUtil.createSailsJSExecutableTextField(null);
        }
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
        return error ? new ValidationInfo("Incorrect Sails/Treeline executable") : null;
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
