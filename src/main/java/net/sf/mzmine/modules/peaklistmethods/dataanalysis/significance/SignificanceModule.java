/*
 * Copyright (C) 2018 Du-Lab Team <dulab.binf@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sf.mzmine.modules.peaklistmethods.dataanalysis.significance;

import java.util.*;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineRunnableModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

public class SignificanceModule implements MZmineRunnableModule {

    private static final String MODULE_NAME = "Significance calculation";
    private static final String MODULE_DESCRIPTION = "Calculates significance of aligned spectra based on their intensity.";

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
    public ExitCode runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {

        String group1template = parameters.getParameter(SignificanceParameters.group1template).getValue();
        String group2template = parameters.getParameter(SignificanceParameters.group2template).getValue();

        PeakList[] peakLists = parameters
                .getParameter(SignificanceParameters.peakLists).getValue()
                .getMatchingPeakLists();

        for (PeakList peakList : peakLists) {

            Group group1 = SignificanceTask.getGroup(peakList.getRows(), group1template);
            Group group2 = SignificanceTask.getGroup(peakList.getRows(), group2template);

            tasks.add(new SignificanceTask(peakList.getRows(), group1, group2));
        }

        return ExitCode.OK;

    }

    public @Nonnull MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.DATAANALYSIS;
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return SignificanceParameters.class;
    }
}
