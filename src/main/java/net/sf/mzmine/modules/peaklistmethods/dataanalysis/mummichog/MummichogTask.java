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

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import driver.ExecuteMummiChog;
import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.datamodel.impl.SimplePeakIdentity;
import net.sf.mzmine.datamodel.impl.SimplePeakInformation;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import pojo.Compound;

public class MummichogTask extends AbstractTask {

    private static final String SIGNIFICANCE_KEY = "SIGNIFICANCE";
    private static final String P_VALUE_KEY = "P_Value";
    private static final String T_VALUE_KEY = "T_Value";

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private double finishedPercentage = 0.0;

    private final PeakListRow[] peakListRows;
  //  private final  String params;
    private final  String cutoff;
    private final String network;
    private final String force_primary_ion;
    private final String modeling;
    private final String mode;
    private final File output;


//    public MummichogTask(PeakListRow[] peakListRows, String cutoff,String network,String force_primary_ion,String modeling,String mode,String output) {
//        this.peakListRows = peakListRows;
//        this.controlGroup = controlGroup;
//        this.experimentalGroup = experimentalGroup;
//    }
    
    

    public String getTaskDescription() {
        return "Calculating Mummichog... ";
    }
    
    public MummichogTask(PeakListRow[] peakListRows, String cutoff, String network, String force_primary_ion, String modeling, String mode, File output) {
	super();
	this.logger = logger;
	this.peakListRows = peakListRows;
	this.cutoff = cutoff;
	this.network = network;
	this.force_primary_ion = force_primary_ion;
	this.modeling = modeling;
	this.mode = mode;
	this.output = output;
	//this.params=params;
}

	public double getFinishedPercentage() {
        return finishedPercentage;
    }

    @SuppressWarnings({ "unlikely-arg-type", "unlikely-arg-type" })
	public void run() {
    	setStatus(TaskStatus.PROCESSING);
    	// Make a String from peaks data to be passed as input file 
    	// Make a String array input parameters to be passed as args
    	//TODO Put a check for if the information map contains the Significance
    	//TODO  Check how to add error messages so that we can throw exceptions and display them on UI
    	//MummichogIntegrationUtilities miu = new MummichogIntegrationUtilities();
    	String errorMsg = null;
    	try {
    	String input=prepareData();
    	ExecuteMummiChog emc = new ExecuteMummiChog();
    	String [] arguments=new String[12];
    	
    	// This condition needs to be checked as cant provide blank value to ui fields as of now
//    	if(this.params.equalsIgnoreCase("NA")) {
//    		arguments=new String[0];
//    	}else {
//    		arguments=this.params.split(" ");
//    	}
    	
    	arguments[0]="--cutoff";
    	arguments[1]=this.cutoff;
    	arguments[2]="--network";
    	arguments[3]=this.network;
    	arguments[4]="--force_primary_ion";
    	arguments[5]=this.force_primary_ion;
    	arguments[6]="--modeling";
    	arguments[7]=this.modeling;
    	arguments[8]="--mode";
    	arguments[9]=this.mode;
    	arguments[10]="--output";
    	arguments[11]=this.output.getAbsolutePath();
    	
    	@SuppressWarnings("unused")
		Map<String, List<Compound>> mummiOutput=emc.runMummiChog(input, arguments);
    	
    	
    	// Code to update the allign peak list
    	for (String mzr: mummiOutput.keySet()) {
    		
    		for( PeakListRow pr: peakListRows) {
    			if((String.valueOf(pr.getAverageMZ()) +";" + String.valueOf(pr.getAverageRT())).equalsIgnoreCase(mzr)){
    				for(Compound compund: mummiOutput.get(mzr)){
    					PeakIdentity pId = new SimplePeakIdentity(compund.getName(),compund.getFormula(),"","","");
    					pr.addPeakIdentity(pId, true);
    				}
    			}
        	}
    	}
    	
    	
    	
    	 setStatus(TaskStatus.FINISHED);
         logger.info("Calculating Mummichog is completed");
    	}   catch (Exception e) {
            errorMsg = "'Unknown Error' during Mummichog calculation: " + e.getMessage();
        } catch (Throwable t) {
            setStatus(TaskStatus.ERROR);
            setErrorMessage(t.getMessage());
            logger.log(Level.SEVERE, "Mummichog calculation error", t);
        }

        if (errorMsg != null) {
            setErrorMessage(errorMsg);
            setStatus(TaskStatus.ERROR);
        }
    }
    
    String prepareData() throws Exception {
    	StringBuilder result = new StringBuilder();
    	result.append("m/z"+"\t"+"retention_time" +"\t"+"p-value"+"\t"+	"t-score"+"\t"+	"custom_id");
    	result.append("\n");
    	for(PeakListRow pr: this.peakListRows) {
    		if(pr.getPeakInformation().getAllProperties().containsKey("SIGNIFICANCE")) {
    		result.append(pr.getAverageMZ()).append("\t").append(pr.getAverageRT()).append("\t").append(pr.getPeakInformation().getAllProperties().get(P_VALUE_KEY)).append("\t").append(pr.getPeakInformation().getAllProperties().get(T_VALUE_KEY)).append("\t").append("randomText");
    		result.append("\n");
    		}else {
    			throw new Exception("P Value property not present in one or many records during Mummichog calculation");
    		}
    	}
    	return result.toString();
    }
}
