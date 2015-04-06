package com.jaumard.sails.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jaumard.sails.icons.SailsJSIcons;
import com.jaumard.sails.settings.SailsJSConfig;
import com.jaumard.sails.ui.GenerateContentPopup;
import com.jaumard.sails.utils.SailsJSCommandLine;
import com.jaumard.sails.utils.SailsJSUtil;

import java.awt.*;

/**
 * Created by jaumard on 04/04/2015.
 */
public class Generate extends AnAction implements GenerateContentPopup.GeneratePopupListener
{
    Project currentProject;
    JBPopup popup;
    GenerateContentPopup generatePopup;

    public void actionPerformed(AnActionEvent e)
    {
        if (popup != null)
        {
            popup.cancel();
            popup.dispose();
        }

        if (SailsJSUtil.isSailsProject(currentProject))
        {
            currentProject = DataKeys.PROJECT.getData(e.getDataContext());
            generatePopup = new GenerateContentPopup();
            generatePopup.setListener(this);
            ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(generatePopup, null).setTitle("Generate new item").setMovable(true)
                    .setCancelOnClickOutside(false).setRequestFocus(true).setResizable(true).setMayBeParent(true)
                    .setCancelButton(new IconButton("Cancel", SailsJSIcons.close)).setModalContext(true);
            popup = builder.createPopup();

            popup.setSize(new Dimension(300, 150));
            popup.setRequestFocus(true);
            popup.addListener(new JBPopupListener()
            {
                @Override
                public void beforeShown(LightweightWindowEvent lightweightWindowEvent)
                {
                    generatePopup.setFocus();
                }

                @Override
                public void onClosed(LightweightWindowEvent lightweightWindowEvent)
                {

                }
            });
            popup.showCenteredInCurrentWindow(currentProject);

            final VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);

            if (file != null && currentProject != null)
            {
                if (file.getName().equals(SailsJSCommandLine.GENERATE_API))
                {
                    generatePopup.setCurrentItem(SailsJSCommandLine.GENERATE_API);
                }
                else if (file.getName().equals(SailsJSCommandLine.GENERATE_MODEL + "s"))
                {
                    generatePopup.setCurrentItem(SailsJSCommandLine.GENERATE_MODEL);
                }
                else if (file.getName().equals(SailsJSCommandLine.GENERATE_CONTROLLER + "s"))
                {
                    generatePopup.setCurrentItem(SailsJSCommandLine.GENERATE_CONTROLLER);
                }
                else if (file.getName().equals(SailsJSCommandLine.GENERATE_ADAPTER + "s"))
                {
                    generatePopup.setCurrentItem(SailsJSCommandLine.GENERATE_ADAPTER);
                }
            }
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
        currentProject = DataKeys.PROJECT.getData(e.getDataContext());
        Presentation presentation = e.getPresentation();
        presentation.setText("Sails item");
        if (SailsJSUtil.isSailsProject(currentProject))
        {
            VirtualFile file = e.getData(PlatformDataKeys.VIRTUAL_FILE);
            if (file != null && currentProject != null && file.equals(currentProject.getBaseDir()))
            {
                presentation.setVisible(true);
            }
            else if (file != null && file.getName().equals(SailsJSCommandLine.GENERATE_API))
            {
                presentation.setText(SailsJSCommandLine.GENERATE_API.toUpperCase());
                presentation.setVisible(true);
            }
            else if (file != null && file.getName().equals(SailsJSCommandLine.GENERATE_MODEL + "s"))
            {
                presentation.setText(StringUtil.capitalize(SailsJSCommandLine.GENERATE_MODEL));
                presentation.setVisible(true);
            }
            else if (file != null && file.getName().equals(SailsJSCommandLine.GENERATE_CONTROLLER + "s"))
            {
                presentation.setText(StringUtil.capitalize(SailsJSCommandLine.GENERATE_CONTROLLER));
                presentation.setVisible(true);
            }
            else if (file != null && file.getName().equals(SailsJSCommandLine.GENERATE_ADAPTER + "s"))
            {
                presentation.setText(StringUtil.capitalize(SailsJSCommandLine.GENERATE_ADAPTER));
                presentation.setVisible(true);
            }
            else
            {
                presentation.setVisible(false);
            }
        }
        else
        {
            presentation.setVisible(false);
        }
        super.update(e);
    }


    @Override
    public void onError(String error)
    {
        Messages.showMessageDialog(currentProject, error, "Error", Messages.getErrorIcon());

    }

    @Override
    public void onCancelClick()
    {
        popup.cancel();
    }

    @Override
    public void onValidateClick()
    {
        final SailsJSCommandLine commandLine = new SailsJSCommandLine(SailsJSConfig.getInstance().getExecutablePath(), currentProject.getBasePath());
        final String finalGenerate = generatePopup.getItemType();
        final String name = generatePopup.getName();
        final String extras = generatePopup.getExtras();
        if (ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
                    indicator.setText("Creating...");
                    String[] extrasArray;
                    if (extras.isEmpty())
                    {
                        extrasArray = null;
                    }
                    else
                    {
                        extrasArray = extras.split(" ");
                    }
                    commandLine.generateNew(finalGenerate, name, extrasArray);
                    currentProject.getBaseDir().refresh(true, true);
                }
                catch (Exception e1)
                {
                    ProgressManager.getInstance().getProgressIndicator().cancel();
                    Messages.showMessageDialog(currentProject, "Sorry, an error has occurred : <br/>" + e1.getCause(), "Error", Messages.getErrorIcon());
                    e1.printStackTrace();
                }
            }
        }, "Generating " + finalGenerate + " " + name, true, currentProject))
        {
            closePopup();
        }
    }

    private void closePopup()
    {
        popup.cancel();
        popup.dispose();
    }
}
