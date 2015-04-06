package com.jaumard.sails.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.jaumard.sails.utils.SailsJSCommandLine;
import com.jaumard.sails.utils.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by jaumard on 06/04/2015.
 */
public class GenerateContentPopup extends JPanel
{
    private ComboBox itemType;
    private JTextField nameField;
    private JHintTextField extrasField;
    private GeneratePopupListener listener;

    public GenerateContentPopup()
    {
        super();
        setLayout(new BorderLayout());
        init();
    }

    public GenerateContentPopup(LayoutManager layout)
    {
        super(layout);
        init();
    }

    private void init()
    {

        add(getForm(), BorderLayout.CENTER);
        add(getButtons(), BorderLayout.SOUTH);
    }

    public void setCurrentItem(String item)
    {
        itemType.setSelectedItem(StringUtil.capitalize(item));
    }

    private JPanel getForm()
    {
        SpringLayout layout = new SpringLayout();
        JPanel form = new JPanel(layout);
        JLabel l = new JLabel("Item :", JLabel.TRAILING);

        String[] strings = {
                StringUtil.capitalize(SailsJSCommandLine.GENERATE_API),
                StringUtil.capitalize(SailsJSCommandLine.GENERATE_MODEL),
                StringUtil.capitalize(SailsJSCommandLine.GENERATE_CONTROLLER),
                StringUtil.capitalize(SailsJSCommandLine.GENERATE_ADAPTER)
        };
        itemType = new ComboBox(strings, 10);
        itemType.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if (e.getStateChange() == ItemEvent.SELECTED)
                {
                    String item = (String) itemType.getSelectedItem();
                    if (item.toLowerCase().equals(SailsJSCommandLine.GENERATE_API) || item.toLowerCase().equals(SailsJSCommandLine.GENERATE_ADAPTER))
                    {
                        extrasField.setEnabled(false);
                        extrasField.setText("");
                        extrasField.setHint("");
                    }
                    else
                    {
                        extrasField.setEnabled(true);
                        extrasField.setText("");
                        if (item.toLowerCase().equals(SailsJSCommandLine.GENERATE_MODEL))
                        {
                            extrasField.setHint("attribute1:type1 attribute2:type2...");
                        }
                        else if (item.toLowerCase().equals(SailsJSCommandLine.GENERATE_CONTROLLER))
                        {
                            extrasField.setHint("action1 action2...");
                        }
                    }
                }
            }
        });
        l.setLabelFor(itemType);
        form.add(l);
        form.add(itemType);

        JLabel l2 = new JLabel("Name :", JLabel.TRAILING);
        nameField = new JTextField(10);
        l.setLabelFor(nameField);
        form.add(l2);
        form.add(nameField);

        JLabel l3 = new JLabel("Extra :", JLabel.TRAILING);
        extrasField = new JHintTextField(10);
        extrasField.setEnabled(false);
        l.setLabelFor(extrasField);
        form.add(l3);
        form.add(extrasField);

        SpringUtilities.makeCompactGrid(form,
                3, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);

        return form;
    }

    private JPanel getButtons()
    {
        JPanel buttons = new JPanel();

        JButton cancel = new JButton("Cancel");
        JButton validate = new JButton("Generate");

        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (listener != null)
                {
                    listener.onCancelClick();
                }
            }
        });
        validate.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (listener == null)
                {
                    throw new RuntimeException("GenerateContentPopup need a listener");
                }
                else
                {
                    if (getName() == null || getName().isEmpty())
                    {

                    }
                    else
                    {
                        listener.onValidateClick();
                    }
                }

            }
        });

        buttons.add(cancel);
        buttons.add(validate);
        return buttons;
    }

    public String getName()
    {
        return nameField.getText();
    }

    public String getItemType()
    {
        return ((String) itemType.getSelectedItem()).toLowerCase();
    }

    public String getExtras()
    {
        return extrasField.getText();
    }

    public GeneratePopupListener getListener()
    {
        return listener;
    }

    public void setListener(GeneratePopupListener listener)
    {
        this.listener = listener;
    }

    public interface GeneratePopupListener
    {
        void onCancelClick();

        void onValidateClick();
    }
}
