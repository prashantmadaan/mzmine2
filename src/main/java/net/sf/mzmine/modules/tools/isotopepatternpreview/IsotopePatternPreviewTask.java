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

package net.sf.mzmine.modules.tools.isotopepatternpreview;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import net.sf.mzmine.datamodel.PolarityType;
import net.sf.mzmine.datamodel.impl.ExtendedIsotopePattern;
import net.sf.mzmine.modules.peaklistmethods.isotopes.isotopeprediction.IsotopePatternCalculator;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class IsotopePatternPreviewTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());
  
  private String message;
  
  private boolean parametersChanged;
  private double minIntensity, mergeWidth;
  private int charge;
  private PolarityType polarity;
  private String formula;
  private boolean displayResult;
  
  ExtendedIsotopePattern pattern;
  IsotopePatternPreviewDialog dialog;
  
  public IsotopePatternPreviewTask() {
    message = "Wating for parameters";
    parametersChanged = false;
    formula = "";
    minIntensity = 0.d;
    mergeWidth = 0.d;
    charge = 0;
    pattern = null;
  }
  
  public void initialise(String formula, double minIntensity, double mergeWidth, int charge, PolarityType polarity) {
    message = "Wating for parameters";
    parametersChanged = false;
    this.minIntensity = minIntensity;
    this.mergeWidth = mergeWidth;
    this.charge = charge;
    this.formula = formula;
    this.polarity = polarity;
    parametersChanged = true;
    pattern = null;
    displayResult = true;
  }
  
  public IsotopePatternPreviewTask(String formula, double minIntensity, double mergeWidth, int charge, PolarityType polarity, IsotopePatternPreviewDialog dialog) {
    message = "Wating for parameters";
    parametersChanged = false;
    this.minIntensity = minIntensity;
    this.mergeWidth = mergeWidth;
    this.charge = charge;
    this.formula = formula;
    this.polarity = polarity;
    this.dialog = dialog;
    setStatus(TaskStatus.WAITING);
    parametersChanged = true;
    pattern = null;
    displayResult = true;
  }
  
  public void run() {
    setStatus(TaskStatus.PROCESSING);
    dialog.setStatus("Calculating...");
    pattern = (ExtendedIsotopePattern) IsotopePatternCalculator.calculateIsotopePattern(formula, minIntensity, mergeWidth, charge, polarity, true);
    if(pattern == null) {
      logger.warning("Isotope pattern could not be calculated.");
      return;
    }
    logger.finest("Pattern " + pattern.getDescription() + " calculated.");
        
    if(displayResult) {
      dialog.setStatus("Waiting.");
      updateWindow();
      startNextThread();
    }
    setStatus(TaskStatus.FINISHED);
  }
  
  public void updateWindow() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        logger.finest("Updating window");
        dialog.updateChart(pattern);
        dialog.updateTable(pattern);
      }
    });
  }
  
  public void startNextThread() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        dialog.startNextThread();
      }
    });
  }
  
  public void setDisplayResult(boolean val) {
    this.displayResult = val;
  }
  
  @Override
  public String getTaskDescription() {
    return message;
  }

  @Override
  public double getFinishedPercentage() {
    return 0;
  }
}
