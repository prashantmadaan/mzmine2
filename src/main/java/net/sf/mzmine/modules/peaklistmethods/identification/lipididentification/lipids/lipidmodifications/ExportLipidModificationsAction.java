/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

/*
 * Code created was by or on behalf of Syngenta and is released under the open source license in use
 * for the pre-existing code or project. Syngenta does not assert ownership or copyright any over
 * pre-existing work.
 */

package net.sf.mzmine.modules.peaklistmethods.identification.lipididentification.lipids.lipidmodifications;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.Ostermiller.util.CSVPrinter;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.parametertypes.LipidModificationChoiceComponent;
import net.sf.mzmine.util.dialogs.LoadSaveFileChooser;

/**
 * An action to handle exporting lipid modifications to a file.
 *
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */

public class ExportLipidModificationsAction extends AbstractAction {

  private static final long serialVersionUID = 1L;

  // Logger.
  private static final Logger LOG =
      Logger.getLogger(ExportLipidModificationsAction.class.getName());

  // Filename extension.
  private static final String FILENAME_EXTENSION = "csv";

  private LoadSaveFileChooser chooser;

  /**
   * Create the action.
   */
  public ExportLipidModificationsAction() {

    super("Export...");
    putValue(SHORT_DESCRIPTION, "Export custom modifications to a CSV file");

    chooser = null;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    // Parent component.
    final LipidModificationChoiceComponent parent =
        (LipidModificationChoiceComponent) SwingUtilities
            .getAncestorOfClass(LipidModificationChoiceComponent.class, (Component) e.getSource());

    if (parent != null) {

      // Create the chooser if necessary.
      if (chooser == null) {

        chooser = new LoadSaveFileChooser("Select lipid modifications File");
        chooser.addChoosableFileFilter(
            new FileNameExtensionFilter("Comma-separated values files", FILENAME_EXTENSION));
      }

      // Choose the file.
      final File file = chooser.getSaveFile(parent, FILENAME_EXTENSION);
      if (file != null) {

        // Export the modifications.
        try {

          exportLipidModificationsToFile(file, (LipidModification[]) parent.getChoices());
        } catch (IOException ex) {
          final Window window =
              (Window) SwingUtilities.getAncestorOfClass(Window.class, (Component) e.getSource());
          final String msg = "There was a problem writing the lipid modifications file.";
          MZmineCore.getDesktop().displayErrorMessage(window, "I/O Error",
              msg + "\n(" + ex.getMessage() + ')');
          LOG.log(Level.SEVERE, msg, ex);
        }
      }
    }
  }

  /**
   * Writes the modifications to a CSV file.
   *
   * @param file the destination file.
   * @param modifications to export.
   * @throws IOException if there are i/o problems.
   */
  private static void exportLipidModificationsToFile(final File file,
      final LipidModification[] modifications) throws IOException {

    final CSVPrinter writer = new CSVPrinter(new FileWriter(file));
    for (final LipidModification modification : modifications) {

      writer.writeln(new String[] {modification.getLipidModificatio(),
          modification.getLipidModificationLabel()});
    }
  }
}
