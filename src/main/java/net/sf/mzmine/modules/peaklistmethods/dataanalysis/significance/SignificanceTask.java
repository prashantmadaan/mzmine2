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

package net.sf.mzmine.modules.peaklistmethods.dataanalysis.significance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.datamodel.impl.SimplePeakInformation;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import smile.stat.hypothesis.TTest;

public class SignificanceTask extends AbstractTask {

    private static final String SIGNIFICANCE_KEY = "SIGNIFICANCE";
    private static final String P_VALUE_KEY = "P_Value";
    private static final String T_VALUE_KEY = "T_Value";

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private double finishedPercentage = 0.0;

    private final PeakListRow[] peakListRows;
    private final Group controlGroup;
    private final Group experimentalGroup;

    public SignificanceTask(PeakListRow[] peakListRows, Group controlGroup, Group experimentalGroup) {
        this.peakListRows = peakListRows;
        this.controlGroup = controlGroup;
        this.experimentalGroup = experimentalGroup;
    }

    public String getTaskDescription() {
        return "Calculating significance... ";
    }

    public double getFinishedPercentage() {
        return finishedPercentage;
    }

    public void run() {

        if (!isCanceled()) {
            String errorMsg = null;

            setStatus(TaskStatus.PROCESSING);
            logger.info(String.format(
                    "Started calculating significance\r\nControl group files: %s\r\nExperimental group files: %s",
                    controlGroup.getFiles().stream().map(RawDataFile::getName).collect(Collectors.joining(", ")),
                    experimentalGroup.getFiles().stream().map(RawDataFile::getName).collect(Collectors.joining(", "))));

            try {
                calculateSignificance();

                setStatus(TaskStatus.FINISHED);
                logger.info("Calculating significance is completed");
            } catch (Exception e) {
                errorMsg = "'Unknown Error' during significance calculation: " + e.getMessage();
            } catch (Throwable t) {
                setStatus(TaskStatus.ERROR);
                setErrorMessage(t.getMessage());
                logger.log(Level.SEVERE, "Significance calculation error", t);
            }

            if (errorMsg != null) {
                setErrorMessage(errorMsg);
                setStatus(TaskStatus.ERROR);
            }
        }
    }

    private void calculateSignificance() {

        if (peakListRows.length == 0)
            return;

        finishedPercentage = 0.0;
        final double finishedStep = 1.0 / peakListRows.length;

        for (PeakListRow row : peakListRows) {

            finishedPercentage += finishedStep;

            double controlIntensity = 0.0;
            double experimentalIntensity = 0.0;
            List<Double> ControlGroupIntensities = new ArrayList<Double>();
            List<Double> experimentalGroupIntensities = new ArrayList<Double>();

            for (Feature peak : row.getPeaks()) {
                if (controlGroup.getFiles().contains(peak.getDataFile())) {
                    controlIntensity += peak.getHeight();
                    ControlGroupIntensities.add(peak.getHeight());
                }
                if (experimentalGroup.getFiles().contains(peak.getDataFile())) {
                    experimentalIntensity += peak.getHeight();
                    experimentalGroupIntensities.add(peak.getHeight());
                }

            }

            controlIntensity /= controlGroup.getFiles().size();
            experimentalIntensity /= experimentalGroup.getFiles().size();

            double significance = 0.0;
            if (controlIntensity > 0.0 && experimentalIntensity > 0.0)
                significance = Math.log(experimentalIntensity / controlIntensity);
            else if (experimentalIntensity > 0.0)
                significance = Double.POSITIVE_INFINITY;
            else if (controlIntensity > 0.0)
                significance = Double.NEGATIVE_INFINITY;

            PeakInformation peakInformation = row.getPeakInformation();
            if (peakInformation == null)
                peakInformation = new SimplePeakInformation();

            TTest tTest = null;
            try {
                tTest = TTest.test(convertListToArray(ControlGroupIntensities),
                        convertListToArray(experimentalGroupIntensities), false);
            } catch (IllegalArgumentException e) {
                logger.warning(e.getMessage());
            } catch (StackOverflowError e) {
                logger.warning("Stack Overflow Error");
            }

            if (tTest != null) {
                peakInformation.getAllProperties().put(P_VALUE_KEY, Double.toString(tTest.pvalue));
                peakInformation.getAllProperties().put(T_VALUE_KEY, Double.toString(tTest.t));
            }

            peakInformation.getAllProperties().put(SIGNIFICANCE_KEY, Double.toString(significance));
            row.setPeakInformation(peakInformation);
        }
    }

    public double[] convertListToArray(List<Double> list) {
        double[] result = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    public static Group getGroup(PeakListRow[] rows, String template) {

        Set<RawDataFile> groupFiles = new HashSet<>();
        for (PeakListRow row : rows)
            for (RawDataFile file : row.getRawDataFiles())
                if (file.getName().contains(template))
                    groupFiles.add(file);

        return new Group(groupFiles);
    }
}
