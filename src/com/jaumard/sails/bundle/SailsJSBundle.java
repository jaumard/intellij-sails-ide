package com.jaumard.sails.bundle;


import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class SailsJSBundle extends AbstractBundle
{

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params)
    {
        return ourInstance.getMessage(key, params);
    }

    @NonNls
    public static final String BUNDLE = "com.jaumard.sails.bundle.SailsJSBundle";
    private static final SailsJSBundle ourInstance = new SailsJSBundle();

    private SailsJSBundle()
    {
        super(BUNDLE);
    }
}
