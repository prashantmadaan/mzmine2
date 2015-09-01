/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.sf.mzmine.parameters.parametertypes;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringComponent extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final JTextField textField;

    public StringComponent(int inputsize) {
    	textField = new JTextField(inputsize);
    	textField.setBorder(
                BorderFactory.createCompoundBorder(textField.getBorder(),
                        BorderFactory.createEmptyBorder(0, 2, 0, 0)));
    	add(textField);
    }

    public void setText(String text) {
    	textField.setText(text);
    }

    public String getText() {
	return textField.getText();
    }

    @Override
    public void setToolTipText(String toolTip) {
    	textField.setToolTipText(toolTip);
    }
}
