package com.jaumard.sails;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jaumard on 04/04/2015.
 */
public class SailsJSTemplateFactory extends ProjectTemplatesFactory
{
    @NotNull
    @Override
    public String[] getGroups()
    {
        return new String[]{"Node.js and NPM"};
    }


    @NotNull
    @Override
    public ProjectTemplate[] createTemplates(@Nullable String group, WizardContext context)
    {
        return new ProjectTemplate[]{
                new SailsJSProjectGenerator()
        };
    }
}
