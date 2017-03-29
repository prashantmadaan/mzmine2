/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.ADAPpeakpicking;

import net.sf.mzmine.modules.MZmineModule;

/**
 *
 * @author owenmyers
 */
public interface SNEstimatorChoice extends MZmineModule{
    /**
     * Gets if resolver requires R, if applicable
     */
    public boolean getRequiresR();
    
    /**
     * Gets R required packages for the resolver's method, if applicable
     */
    public String[] getRequiredRPackages();
    
    /**
     * Gets R required packages versions for the resolver's method, if
     * applicable
     */
    public String[] getRequiredRPackagesVersions();
    
    public String getSNCode();
    
}
