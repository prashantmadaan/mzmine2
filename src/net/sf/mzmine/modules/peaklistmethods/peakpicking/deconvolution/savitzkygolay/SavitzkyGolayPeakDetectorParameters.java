/*
 * Copyright 2006-2011 The MZmine 2 Development Team
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

package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.savitzkygolay;

import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakResolver;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakResolverSetupDialog;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.PercentParameter;
import net.sf.mzmine.util.dialogs.ExitCode;

public class SavitzkyGolayPeakDetectorParameters extends SimpleParameterSet {

	private PeakResolver peakResolver;

	public static final DoubleParameter minimumPeakHeight = new DoubleParameter(
			"Min peak height",
			"Minimum acceptable peak height (absolute intensity)");

	public static final DoubleParameter minimumPeakDuration = new DoubleParameter(
			"Min peak duration", "Minimum acceptable peak duration (min)");

	public static final PercentParameter derivativeThresholdLevel = new PercentParameter(
			"Derivative threshold level",
			"Minimum acceptable intensity in the 2nd derivative for peak recognition");

	public ExitCode showSetupDialog() {
		PeakResolverSetupDialog dialog = new PeakResolverSetupDialog(
				peakResolver);
		dialog.setVisible(true);
		return dialog.getExitCode();
	}

	public SavitzkyGolayPeakDetectorParameters(PeakResolver peakResolver) {
		super(new Parameter[] { minimumPeakHeight, minimumPeakDuration,
				derivativeThresholdLevel });
		this.peakResolver = peakResolver;

	}

}
