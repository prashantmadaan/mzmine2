/*
 * Copyright 2006-2008 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peakpicking.threestep.peakconstruction.savitzkygolay;

import java.text.NumberFormat;

import net.sf.mzmine.data.Parameter;
import net.sf.mzmine.data.ParameterType;
import net.sf.mzmine.data.impl.SimpleParameter;
import net.sf.mzmine.data.impl.SimpleParameterSet;
import net.sf.mzmine.main.MZmineCore;

public class SavitzkyGolayPeakDetectorParameters extends SimpleParameterSet {

	public static final String peakModelNames[] = { "Triangle", "Gaussian", "EMG" };

	public static final String peakModelClasses[] = {
		"net.sf.mzmine.modules.peakpicking.threestep.peakconstruction.peakfillingmodels.impl.TrianglePeakFillingModel",
		"net.sf.mzmine.modules.peakpicking.threestep.peakconstruction.peakfillingmodels.impl.GaussianPeakFillingModel",
		"net.sf.mzmine.modules.peakpicking.threestep.peakconstruction.peakfillingmodels.impl.EMGPeakFillingModel" };
	
	public static final NumberFormat percentFormat = NumberFormat
			.getPercentInstance();

	public static final Parameter minimumPeakHeight = new SimpleParameter(
			ParameterType.DOUBLE, "Min peak height",
			"Minimum acceptable peak height", "absolute", new Double(10000.0),
			new Double(0.0), null, MZmineCore.getIntensityFormat());

	public static final Parameter minimumPeakDuration = new SimpleParameter(
			ParameterType.DOUBLE, "Min peak duration",
			"Minimum acceptable peak duration", null, new Double(10.0),
			new Double(0.0), null, MZmineCore.getRTFormat());

	public static final Parameter derivativeThresholdLevel = new SimpleParameter(
			ParameterType.DOUBLE, "Derivative threshold level",
			"Minimum acceptable intensity in the 2nd derivative for peak recognition", null, new Double(0.80),
			new Double(0.0), null, percentFormat);
	
	public static final Parameter fillingPeaks = new SimpleParameter(
			ParameterType.BOOLEAN, "Filling peak shape",
			"Activates the method to fill the peak shape using the selected peak model function", null, null,
			null, null, null);
	
	public static final Parameter peakModel = new SimpleParameter(
			ParameterType.STRING,
			"Peak Model function",
			"Lateral peaks under the curve of this peak model are not considered as a possible peak",
			null, peakModelNames);

	public static final Parameter excessLevel = new SimpleParameter(
			ParameterType.DOUBLE,
			"Level of excess",
			"Increasing this parameter in positive domain the width of the filling peak becomes smaller, " +
			"and increasing this parameter in negative domain the width is bigger.",
			"absolute", new Double(0.1), new Double(-0.9), new Double(0.9), null);

	public static final Parameter resolution = new SimpleParameter(
			ParameterType.INTEGER,
			"Mass Resolution",
			"Mass resolution is the dimensionless ratio of the mass of the peak divided by its width."
					+ " Peak width is taken as the full width at half maximum intensity, (fwhm).",
			null, new Integer(60000), new Integer(0), null, NumberFormat
					.getIntegerInstance());
	
	public SavitzkyGolayPeakDetectorParameters() {
		super(new Parameter[] { minimumPeakHeight, minimumPeakDuration, derivativeThresholdLevel,
				fillingPeaks, peakModel, excessLevel, resolution });
	}

}
