/*
 * Copyright 2006-2018 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.parameters.parametertypes;


import java.awt.Color;
import java.util.Arrays;
// import org.apache.commons.lang3.ArrayUtils;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.ArrayUtils;
// import com.google.common.collect.Range;
import net.sf.mzmine.util.components.GridBagPanel;

public class ListDoubleComponent extends GridBagPanel {
  private JTextField inputField;
  private JLabel textField;

  public ListDoubleComponent() {
    inputField = new JTextField();
    inputField.setColumns(16);
    inputField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        update();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        update();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        update();
      }
    });

    textField = new JLabel();
    // textField.setColumns(8);

    add(inputField, 0, 0);
    add(textField, 0, 1);
  }


  public List<Double> getValue() {
    try {
      String values = textField.getText().replaceAll("\\s", "");
      String[] strValues = values.split(",");
      double[] doubleValues = new double[strValues.length];
      for (int i = 0; i < strValues.length; i++) {
        try {
          doubleValues[i] = Double.parseDouble(strValues[i]);
        } catch (NumberFormatException nfe) {
          // The string does not contain a parsable integer.
        }
      }
      Double[] doubleArray = ArrayUtils.toObject(doubleValues);
      List<Double> ranges = Arrays.asList(doubleArray);
      return ranges;
    } catch (Exception e) {
      return null;
    }
  }

  public void setValue(List<Double> ranges) {
    String[] strValues = new String[ranges.size()];

    for (int i = 0; i < ranges.size(); i++) {
      strValues[i] = Double.toString(ranges.get(i));
    }
    String text = String.join(",", strValues);

    textField.setForeground(Color.black);
    textField.setText(text);
    inputField.setText(text);
  }

  @Override
  public void setToolTipText(String toolTip) {
    textField.setToolTipText(toolTip);
    inputField.setToolTipText(toolTip);
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    textField.setEnabled(enabled);
    inputField.setEnabled(enabled);
  }

  private void update() {
    try {
      String values = inputField.getText().replaceAll("\\s", "");
      String[] strValues = values.split(",");

      String text = String.join(",", strValues);


      textField.setForeground(Color.black);
      textField.setText(text);
    } catch (IllegalArgumentException e) {
      textField.setForeground(Color.red);
      textField.setText(e.getMessage());
    }
  }
}


