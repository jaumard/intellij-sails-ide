package com.jaumard.sails.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by jaumard on 04/04/2015.
 */
public class SailsJSIcons
{
    public static javax.swing.Icon close = load("/icons/close.png"); // 16x16;

    private static Icon load(String path)
    {
        return IconLoader.getIcon(path, SailsJSIcons.class);
    }

    public static final Icon SailsJS = load("/icons/SailsJS.png"); // 16x16
}
