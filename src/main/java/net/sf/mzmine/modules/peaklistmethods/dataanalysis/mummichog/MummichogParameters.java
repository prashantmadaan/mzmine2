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

package net.sf.mzmine.modules.peaklistmethods.dataanalysis.mummichog;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.filenames.DirectoryParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;

public class MummichogParameters extends SimpleParameterSet {

    public static final PeakListsParameter peakLists = new PeakListsParameter(1, 1);

    public static final StringParameter cutoff = new StringParameter("Cutoff",
            "Cutoff Value","0.05");

   // public static final StringParameter network = new StringParameter("Network",
    //        "Metion the model to be used","human_mfn");
    
  //  public static final StringParameter force_primary_ion = new StringParameter("Force Primary ion",
   //         "Force Primary Ion","true");
//    public static final StringParameter modeling = new StringParameter("Modeling",
//            "Modeling","None");
    public static final StringParameter mode = new StringParameter("Mode",
            "Mode","pos_default");
 //   public static final StringParameter output = new StringParameter("Output",
  //          "Output directory","default");
    public static final ComboParameter<String> network = new ComboParameter<String>(
    	    "Netowork",
    	    "Metion the model to be used",
    	    new String[] { "human","worm" }, "human");
    public static final ComboParameter<String> modeling = new ComboParameter<String>(
    	    "Modeling",
    	    "Metion the fitting to be used",
    	    new String[] { "Non-Parametric","Gamma" }, "Non-Parametric");
    
    public static final BooleanParameter force_primary_ion = new BooleanParameter(
    	    "Force Primary ion",
    	    "Force Primary Ion",true);

    public static final DirectoryParameter output = new DirectoryParameter(
    	    "Output",
    	    "Output directory");
    
    public MummichogParameters() {
        super(new Parameter[] {peakLists, cutoff, network, force_primary_ion,modeling,mode,output});
    	//super(new Parameter[] {peakLists, params});
    }
}
