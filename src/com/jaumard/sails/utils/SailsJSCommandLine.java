package com.jaumard.sails.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.codec.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SailsJSCommandLine
{
    private static final Logger LOGGER = Logger.getInstance(SailsJSCommandLine.class);

    public static final String GENERATE_API = "api";
    public static final String GENERATE_MODEL = "model";
    public static final String GENERATE_CONTROLLER = "controller";
    public static final String GENERATE_ADAPTER = "adapter";

    public static final String PLATFORM_SAILS = "sails";
    public static final String PLATFORM_TREELINE = "treeline";

    @Nullable
    private final String myWorkDir;

    @NotNull
    private final String myPath;

    @Nullable
    private String version;
    private boolean myIsCorrect = true;

    private Map<String, String> myEnv = ContainerUtil.newHashMap();

    public SailsJSCommandLine(@NotNull String path, @Nullable String dir)
    {
        myWorkDir = dir;
        myPath = path;
        try
        {
            version = getInnerVersion(myPath, "--version").replace("\"", "").trim();
        }
        catch (Exception e)
        {
            version = null;
            LOGGER.debug(e.getMessage(), e);
            myIsCorrect = false;
        }
    }

    public boolean isCorrectExecutable()
    {
        return myIsCorrect;
    }

    public String version()
    {
        return version;
    }

    public void createNewProject(String name) throws Exception
    {
        executeVoidCommand(myPath, "new", name);
    }

    public void generateNew(String what, String name, String[] extras) throws Exception
    {
        String[] command = new String[4 + extras.length];
        command[0] = myPath;
        command[1] = "generate";
        command[2] = what;
        command[3] = name;
        for (int i = 0; i < extras.length; i++)
        {
            String extra = extras[i];
            command[i + 4] = extra;
        }
        executeVoidCommand(command);
    }

    private boolean isSailsJS()
    {
        assert myWorkDir != null;
        Boolean isPhoneGapByName = isSailsJSExecutableByPath(myPath);
        if (isPhoneGapByName != null)
        {
            return isPhoneGapByName;
        }

        String s = executeAndReturnResult(new String[]{myPath});

        return s.contains(PLATFORM_SAILS);
    }

    /**
     * @param path is path a sails executable
     * @return true - sails / false - not sails / null - cannot detect
     */
    @Nullable
    public static Boolean isSailsJSExecutableByPath(@Nullable String path)
    {
        if (StringUtil.isEmpty(path))
        {
            return false;
        }

        File file = new File(path);
        if (!file.exists())
        {
            return false;
        }
        if (file.getName().contains(PLATFORM_TREELINE))
        {
            return false;
        }
        if (file.getName().contains(PLATFORM_SAILS))
        {
            return true;
        }

        return null;
    }

    private boolean isTreeline()
    {
        assert myWorkDir != null;
        File file = new File(myPath);
        if (file.getName().contains(PLATFORM_TREELINE))
        {
            return true;
        }
        if (file.getName().contains(PLATFORM_SAILS))
        {
            return false;
        }

        String s = executeAndReturnResult(new String[]{myPath});

        return s.contains(PLATFORM_TREELINE);
    }

    private void executeVoidCommand(final String... command)
    {
        try
        {
            ProcessOutput output = executeAndGetOut(command);

            if (output.getExitCode() > 0)
            {
                throw new RuntimeException("Command error: " + output.getStderr());
            }
        }
        catch (Exception e)
        {
            LOGGER.debug(e.getMessage(), e);

            throw new RuntimeException("Select correct executable path", e);
        }
    }

    private String getInnerVersion(String... command)
    {
        try
        {
            final ProcessOutput output = executeAndGetOut(command);

            String stderr = output.getStderr();
            if (output.getExitCode() > 0)
            {
                throw new RuntimeException("Command error: " + stderr);
            }

            String stdout = output.getStdout();
            if (StringUtil.isEmpty(stdout) && !StringUtil.isEmpty(stderr))
            {
                return stderr;
            }

            return stdout;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String executeAndReturnResult(String[] command)
    {
        try
        {
            final ProcessOutput output = executeAndGetOut(command);

            if (output.getExitCode() > 0)
            {
                throw new RuntimeException("Command error: " + output.getStderr());
            }

            return output.getStdout();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ProcessOutput executeAndGetOut(String[] command) throws ExecutionException
    {
        final GeneralCommandLine commandLine = new GeneralCommandLine(command);

        commandLine.withWorkDirectory(myWorkDir);
        commandLine.setPassParentEnvironment(true);
        commandLine.withEnvironment(myEnv);

        Process process = commandLine.createProcess();
        OSProcessHandler processHandler = new ColoredProcessHandler(process, commandLine.getCommandLineString(), Charsets.UTF_8);
        final ProcessOutput output = new ProcessOutput();
        processHandler.addProcessListener(new ProcessAdapter()
        {
            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType)
            {
                if (outputType == ProcessOutputTypes.STDERR)
                {
                    output.appendStderr(event.getText());
                }
                else if (outputType != ProcessOutputTypes.SYSTEM)
                {
                    output.appendStdout(event.getText());
                }
            }
        });
        processHandler.startNotify();
        if (processHandler.waitFor(TimeUnit.SECONDS.toMillis(120)))
        {
            output.setExitCode(process.exitValue());
        }
        else
        {
            processHandler.destroyProcess();
            output.setTimeout();
        }
        return output;
    }

}
