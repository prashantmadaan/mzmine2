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

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.filenames.FileNameParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;
import net.sf.mzmine.util.R.REngineType;

/**
 *
 * @author Du-Lab Team <dulab.binf@gmail.com>
 */


public class XCMSExportParameters extends SimpleParameterSet
{
    public static final PeakListsParameter PEAK_LISTS = new PeakListsParameter();
    
    public static final FileNameParameter FILENAME = new FileNameParameter(
	    "Filename",
	    "Name of the output .RData file. " +
	    "Use pattern \"{}\" in the file name to substitute with peak list name. " +
	    "(i.e. \"blah{}blah.RData\" would become \"blahSourcePeakListNameblah.RData\"). " +
	    "If the file already exists, it will be overwritten.",
	    "RData");

    public static final ComboParameter<REngineType> RENGINE_TYPE = new ComboParameter<REngineType>(
            "R engine",
            "The R engine to be used for communicating with R.",
            REngineType.values(), REngineType.RCALLER);

    public XCMSExportParameters() {
	super(new Parameter[] {PEAK_LISTS, FILENAME, RENGINE_TYPE});
    }
}
