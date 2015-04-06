package com.jaumard.sails;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.util.containers.ContainerUtil;
import com.jaumard.sails.icons.SailsJSIcons;
import com.jaumard.sails.settings.SailsJSConfig;
import com.jaumard.sails.utils.SailsJSCommandLine;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by jaumard on 04/04/2015.
 */
public class SailsJSProjectGenerator extends WebProjectTemplate<SailsJSProjectGenerator.SailsJSProjectSettings> implements DirectoryProjectGenerator<SailsJSProjectGenerator.SailsJSProjectSettings>
{
    private static final Logger LOG = Logger.getInstance(SailsJSProjectGenerator.class);

    public SailsJSProjectGenerator()
    {

    }

    @Nls
    @NotNull
    @Override
    public String getName()
    {
        return "Sails/Treeline";
    }

    @Override
    public String getDescription()
    {
        return "<html>This project is an application skeleton for a typical <a href=\"http://www.sailsjs.org\">Sails</a> or <a href=\"http://treeline.io\">Treeline</a> server app.<br>" +
                "Don't forget to install dependencies by running<pre>npm install</pre></html>";
    }

    @Override
    public javax.swing.Icon getIcon()
    {
        return SailsJSIcons.SailsJS;
    }

    @Override
    public void generateProject(@NotNull final Project project, @NotNull final VirtualFile baseDir,
                                @NotNull final SailsJSProjectSettings settings, @NotNull Module module
    )
    {
        try
        {

            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable()
            {
                @Override
                public void run()
                {

                    try
                    {
                        ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
                        indicator.setText("Creating...");
                        File tempProject = createTemp();
                        SailsJSCommandLine commandLine = new SailsJSCommandLine(settings.getExecutable(), tempProject.getPath());

                        if (!commandLine.isCorrectExecutable())
                        {
                            showErrorMessage("Incorrect path");
                            return;
                        }
                        settings.setName(project.getName());
                        commandLine.createNewProject(settings.name());

                        File[] array = tempProject.listFiles();
                        if (array != null && array.length != 0)
                        {
                            File from = ContainerUtil.getFirstItem(ContainerUtil.newArrayList(array));
                            assert from != null;
                            FileUtil.copyDir(from, new File(baseDir.getPath()));
                            deleteTemp(tempProject);
                        }
                        else
                        {
                            showErrorMessage("Cannot find files in the directory " + tempProject.getAbsolutePath());
                        }
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }, "Creating Sails project", false, project);

            ApplicationManager.getApplication().runWriteAction(new Runnable()
            {
                @Override
                public void run()
                {
                    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);

                    propertiesComponent.setValue(SailsJSConfig.SAILSJS_WORK_DIRECTORY, project.getBasePath());
                    SailsJSConfig state = SailsJSConfig.getInstance().getState();
                    if (!StringUtil.equals(settings.getExecutable(), state.getExecutablePath()))
                    {
                        SailsJSConfig.getInstance().loadState(new SailsJSConfig());
                    }
                    VfsUtil.markDirty(false, true, project.getBaseDir());
                    createRunConfiguration(project, settings);

                    baseDir.refresh(true, true, new Runnable()
                    {
                        @Override
                        public void run()
                        {

                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            showErrorMessage(e.getMessage());
        }
    }

    private void createRunConfiguration(Project project, SailsJSProjectSettings settings)
    {
        /*RunManager.getInstance(project).createRunConfiguration("Run", new ConfigurationFactory()
        {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project)
            {
                return null;
            }
        });*/
    }

    protected File createTemp() throws IOException
    {
        return FileUtil.createTempDirectory("intellij-sails-generator", null, false);
    }

    protected void deleteTemp(File tempProject)
    {
        if (!FileUtil.delete(tempProject))
        {
            LOG.warn("Cannot delete " + tempProject);
        }
        else
        {
            LOG.info("Successfully deleted " + tempProject);
        }
    }

    @NotNull
    @Override
    public GeneratorPeer<SailsJSProjectSettings> createPeer()
    {
        return new SailsJSProjectPeer();
    }

    static public class SailsJSProjectSettings
    {
        private String name = "example";
        private String executable;

        public SailsJSProjectSettings()
        {
        }

        public void setExecutable(String executable)
        {
            this.executable = executable;
        }

        public String getExecutable()
        {
            return executable;
        }

        public String name()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }

    private static void showErrorMessage(@NotNull String message)
    {
        String fullMessage = "Error creating Sails App. " + message;
        String title = "Create Sails Project";
        Notifications.Bus.notify(
                new Notification("Sails Generator", title, fullMessage, NotificationType.ERROR)
        );
    }

    /*
    @NotNull
    @Override
    protected String getDisplayName()
    {
        return "SailsJS";
    }

    @NotNull
    @Override
    public String getGithubUserName()
    {
        return "balderdashy";
    }

    @NotNull
    @Override
    public String getGithubRepositoryName()
    {
        return "sails";
    }

    @Nullable
    @Override
    public String getDescription()
    {
        return "<html>This project is an application skeleton for a typical <a href=\"https://sails.org\">SailsJS</a> server app.<br>" +
                "Don't forget to install dependencies by running<pre>npm install</pre></html>";
    }

    @Override
    public Icon getIcon()
    {
        return SailsJSIcons.SailsJS;
    }

    @Nullable
    @Override
    public String getPrimaryZipArchiveUrlForDownload(@NotNull GithubTagInfo tag)
    {
        return null;
    }
    */
}
