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

package net.sf.mzmine.modules.peaklistmethods.io.xcmsexport;

import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.modules.peaklistmethods.io.mspexport.MSPExportParameters;
import net.sf.mzmine.modules.peaklistmethods.io.mspexport.MSPExportTask;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Du-Lab Team <dulab.binf@gmail.com>
 */


public class XCMSExportModule implements MZmineProcessingModule {
    private static final String MODULE_NAME = "Export to .RData file";
    private static final String MODULE_DESCRIPTION = "This method creates xcmsSet and xRaw data objects and saves them into .RData file.";

    @Override
    public @Nonnull
    String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull
    String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    @Nonnull
    public ExitCode runModule(@Nonnull MZmineProject project,
                              @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {

        XCMSExportTask task = new XCMSExportTask(parameters);
        tasks.add(task);
        return ExitCode.OK;
    }

    @Override
    public @Nonnull
    MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.PEAKLISTEXPORT;
    }

    @Override
    public @Nonnull
    Class<? extends ParameterSet> getParameterSetClass() {
        return XCMSExportParameters.class;
    }

}
