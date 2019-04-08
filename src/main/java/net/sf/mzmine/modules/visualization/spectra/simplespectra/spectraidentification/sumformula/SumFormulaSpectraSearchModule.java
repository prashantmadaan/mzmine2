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

package net.sf.mzmine.modules.visualization.spectra.simplespectra.spectraidentification.sumformula;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.modules.visualization.spectra.simplespectra.SpectraPlot;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

/**
 * Module for sum formula prediction in spectra.
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class SumFormulaSpectraSearchModule implements MZmineProcessingModule {

  private static final String MODULE_NAME = "Sum formula prediction";
  private static final String MODULE_DESCRIPTION =
      "This module annotates signals in selected mass spectra";

  @Override
  public @Nonnull String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nonnull String getDescription() {
    return MODULE_DESCRIPTION;
  }

  @Override
  @Nonnull
  public ExitCode runModule(@Nonnull MZmineProject project, @Nonnull ParameterSet parameters,
      @Nonnull Collection<Task> tasks) {

    return ExitCode.OK;
  }

  public static void showSpectraIdentificationDialog(final Scan scan,
      final SpectraPlot spectraPlot) {

    final ParameterSet parameters = new SpectraIdentificationSumFormulaParameters();

    // Run task.
    if (parameters.showSetupDialog(MZmineCore.getDesktop().getMainWindow(), true) == ExitCode.OK) {

      MZmineCore.getTaskController().addTask(new SpectraIdentificationSumFormulaTask(
          parameters.cloneParameterSet(), scan, spectraPlot));
    }
  }

  @Override
  public @Nonnull MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.IDENTIFICATION;
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return SpectraIdentificationSumFormulaParameters.class;
  }
}
