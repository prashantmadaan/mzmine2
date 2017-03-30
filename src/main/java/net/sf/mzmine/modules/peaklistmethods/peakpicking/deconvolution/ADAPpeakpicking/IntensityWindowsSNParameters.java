/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.ADAPpeakpicking;

import net.sf.mzmine.parameters.impl.SimpleParameterSet;


import java.awt.Window;
import java.text.NumberFormat;

import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakResolverSetupDialog;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import net.sf.mzmine.util.ExitCode;

import com.google.common.collect.Range;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakResolver;
import net.sf.mzmine.parameters.parametertypes.ModuleComboParameter;

/**
 *
 * @author owenmyers
 */
public class IntensityWindowsSNParameters extends SimpleParameterSet{


//    public static final DoubleRangeParameter PEAK_DURATION = new DoubleRangeParameter(
//	    "Peak duration range", "Range of acceptable peak lengths",
//	    MZmineCore.getConfiguration().getRTFormat(),
//	    Range.closed(0.0, 10.0));

//    public static final ComboParameter<PeakIntegrationMethod> INTEGRATION_METHOD = new ComboParameter<PeakIntegrationMethod>(
//	    "Peak integration method",
//	    "Method used to determine RT extents of detected peaks",
//	    PeakIntegrationMethod.values(),
//	    PeakIntegrationMethod.UseSmoothedData);

    public IntensityWindowsSNParameters() {

	//super(new Parameter[] { SN_THRESHOLD,SHARP_THRESHOLD, MIN_FEAT_HEIGHT, PEAK_DURATION, });
        super(new Parameter[] { });
    }

    @Override
    public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {

	final SNSetUpDialog dialog = new SNSetUpDialog(
		parent, valueCheckRequired, this);
	dialog.setVisible(true);
	return dialog.getExitCode();
    }
    
}
