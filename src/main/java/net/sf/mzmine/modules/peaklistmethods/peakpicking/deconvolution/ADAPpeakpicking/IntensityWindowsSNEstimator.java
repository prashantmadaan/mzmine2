/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.ADAPpeakpicking;

import javax.annotation.Nonnull;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.baseline.BaselinePeakDetectorParameters;
import net.sf.mzmine.parameters.ParameterSet;

/**
 *
 * @author owenmyers
 */
public class IntensityWindowsSNEstimator implements SNEstimatorChoice{
    @Override
    public @Nonnull
    String getName() {
        return "Intensity window SN";
    }
    
    public String getSNCode(){
        return "Intensity Window Estimator";
    }
    @Override
    public @Nonnull
    Class<? extends ParameterSet> getParameterSetClass() {
        return IntensityWindowsSNParameters.class;
    }
    
        @Override
    public boolean getRequiresR() {
        return false;
    }

    @Override
    public String[] getRequiredRPackages() {
        return null;
    }

    @Override
    public String[] getRequiredRPackagesVersions() {
        return null;
    }
}
