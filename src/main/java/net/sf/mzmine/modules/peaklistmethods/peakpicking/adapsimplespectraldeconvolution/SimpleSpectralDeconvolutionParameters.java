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
package net.sf.mzmine.modules.peaklistmethods.peakpicking.adapsimplespectraldeconvolution;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.*;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;
import net.sf.mzmine.util.ExitCode;

import java.awt.*;
import java.text.NumberFormat;

/**
 * @author aleksandrsmirnov
 */
public class SimpleSpectralDeconvolutionParameters extends SimpleParameterSet {

    public static final String POLARITY_UNDEFINED = "Undefined";
    public static final String POLARITY_POSITIVE = "Positive";
    public static final String POLARITY_NEGATIVE = "Negative";

    public static final PeakListsParameter PEAK_LISTS =
            new PeakListsParameter("Peaks", 1, Integer.MAX_VALUE);

    public static final DoubleParameter RETENTION_TIME_TOLERANCE = new DoubleParameter(
            "Ret Time Tolerance",
            "If the distance between two peaks exceeds this values, their similarity is not calculated.",
            NumberFormat.getNumberInstance(), 0.05);

    public static final IntegerParameter MIN_NUM_PEAKS = new IntegerParameter(
            "Min number of peaks",
            "If the number of peaks in a spectrum is less than this value, the component is skipped.",
            5);

    public static final DoubleParameter SIMILARITY_TOLERANCE = new DoubleParameter(
            "Similarity tolerance",
            "All peaks of a component must be similar within this tolerance value.",
            NumberFormat.getNumberInstance(), 0.25);

    public static final ComboParameter<String> POLARITY = new ComboParameter<String>(
            "Polarity",
            "Choose the polarity mode for LC/MS data or leave undefine for GC/MS data",
            new String[] {POLARITY_UNDEFINED, POLARITY_POSITIVE, POLARITY_NEGATIVE});

    public static final StringParameter SUFFIX = new StringParameter("Suffix",
            "This string is added to peak list name as suffix", "Spectral Deconvolution");

    public static final BooleanParameter AUTO_REMOVE = new BooleanParameter(
            "Remove original peak lists",
            "If checked, original chromomatogram and peak lists will be removed");

    public SimpleSpectralDeconvolutionParameters() {
        super(new Parameter[]{PEAK_LISTS, RETENTION_TIME_TOLERANCE, MIN_NUM_PEAKS, SIMILARITY_TOLERANCE, POLARITY,
                SUFFIX, AUTO_REMOVE});
    }

    @Override
    public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {
         SimpleSpectralDeconvolutionSetupDialog dialog =
                 new SimpleSpectralDeconvolutionSetupDialog(parent, valueCheckRequired, this);
         dialog.setVisible(true);
         return dialog.getExitCode();
    }
}
