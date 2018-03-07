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

import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.modules.peaklistmethods.io.mspexport.MSPExportParameters;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.R.REngineType;
import net.sf.mzmine.util.R.RSessionWrapper;
import net.sf.mzmine.util.R.RSessionWrapperException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Du-Lab Team <dulab.binf@gmail.com>
 */


public class XCMSExportTask extends AbstractTask {
    private static final String NAME_PATTERN = "{}";
    private static final int MS_LEVEL = 1;
    private static final double SIGNAL_TO_NOISE = 10.0;
    private static final double SECONDS_PER_MINUTE = 60.0;

    private final PeakList[] peakLists;
    private final File fileName;

    private final REngineType rEngineType;
    private boolean userCanceled = false;

    XCMSExportTask(ParameterSet parameters) {
        this.peakLists = parameters.getParameter(XCMSExportParameters.PEAK_LISTS)
                .getValue().getMatchingPeakLists();

        this.fileName = parameters.getParameter(XCMSExportParameters.FILENAME).getValue();

        this.rEngineType = parameters.getParameter(XCMSExportParameters.RENGINE_TYPE).getValue();
    }

    public double getFinishedPercentage() {
        return 0.0;
    }

    public String getTaskDescription() {
        return "Exporting peak list(s) "
                + Arrays.toString(peakLists) + " to .RData file(s)";
    }

    public void run() {
        setStatus(TaskStatus.PROCESSING);

        // Shall export several files?
        boolean substitute = fileName.getPath().contains(NAME_PATTERN);

        // Process peak lists
        for (PeakList peakList : peakLists) {

            // Filename
            File curFile = fileName;
            if (substitute) {
                // Cleanup from illegal filename characters
                String cleanPlName = peakList.getName().replaceAll(
                        "[^a-zA-Z0-9.-]", "_");
                // Substitute
                String newFilename = fileName.getPath().replaceAll(
                        Pattern.quote(NAME_PATTERN), cleanPlName);
                curFile = new File(newFilename);
            }

            exportPeakList(peakList, curFile);

            // Cancel?
            if (isCanceled()) return;

            // If peak list substitution pattern wasn't found, 
            // treat one peak list only
            if (!substitute)
                break;
        }

        if (getStatus() == TaskStatus.PROCESSING)
            setStatus(TaskStatus.FINISHED);
    }

    private void exportPeakList(PeakList peakList, File curFile) {

        RawDataFile rawDataFile = peakList.getRawDataFile(0);

        try {

            RSessionWrapper rSession = new RSessionWrapper(rEngineType,
                    "XCMS Export",
                    new String[]{"xcms"},
                    new String[]{"3.0.0"});

            rSession.open();

            // Create empty peaks matrix.
            rSession.eval("columnHeadings <- c('mz','mzmin','mzmax','rt','rtmin','rtmax','into','intb','maxo','sn')");
            rSession.eval("peaks <- matrix(nrow=0, ncol=length(columnHeadings))");
            rSession.eval("colnames(peaks) <- columnHeadings");

            Map<Scan, Set<DataPoint>> dataPoints = new HashMap<>(rawDataFile.getNumOfScans(MS_LEVEL));
            int numDataPoints = 0;

            for (final Feature peak : peakList.getPeaks(rawDataFile)) {

                double minRetTime = Double.MAX_VALUE;
                double maxRetTime = -Double.MAX_VALUE;
                double minIntensity = Double.MAX_VALUE;
                double maxIntensity = 0.0;

                for (int scanNumber : peak.getScanNumbers()) {

                    final Scan scan = rawDataFile.getScan(scanNumber);
                    if (scan.getMSLevel() != MS_LEVEL) continue;

                    DataPoint dataPoint = peak.getDataPoint(scanNumber);
                    if (dataPoint == null) continue;

                    // Add data point to dataPoints
                    dataPoints.computeIfAbsent(scan, s -> new HashSet<>())
                            .add(dataPoint);

                    // Update retention time and intensity range
                    minRetTime = Math.min(minRetTime, scan.getRetentionTime());
                    maxRetTime = Math.max(maxRetTime, scan.getRetentionTime());
                    minIntensity = Math.min(minIntensity, dataPoint.getIntensity());
                    maxIntensity = Math.max(maxIntensity, dataPoint.getIntensity());

                    ++numDataPoints;
                }

                // Set peak values
                final double mz = peak.getMZ();
                final double area = peak.getArea();
                final double maxo = (minIntensity < maxIntensity) ? maxIntensity : peak.getHeight();
                final double rtMin = (minRetTime < maxRetTime) ?
                        minRetTime : peak.getRawDataPointsRTRange().lowerEndpoint();
                final double rtMax = (minRetTime < maxRetTime) ?
                        maxRetTime : peak.getRawDataPointsRTRange().upperEndpoint();

                rSession.eval("peaks <- rbind(peaks, c(" + mz + ", " // mz
                                + mz + ", " // mzmin: use the same as mz.
                                + mz + ", " // mzmax: use the same as mz.
                                + peak.getRT() + ", " // rt
                                + rtMin + ", " // rtmin
                                + rtMax + ", " // rtmax
                                + area + ", " // into: peak area.
                                + area + ", " // intb: doesn't affect result, use area.
                                + maxo + ", " // maxo
                                + SIGNAL_TO_NOISE + "))",
                        false);
            }

            // Create R vectors
            final int[] scanIndices = new int[dataPoints.size()];
            final double[] scanTimes = new double[dataPoints.size()];
            final double[] masses = new double[numDataPoints];
            final double[] intensities = new double[numDataPoints];

            int scanIndex = 0;
            int pointIndex = 0;
            for (Scan scan : dataPoints.keySet()) {

                scanTimes[scanIndex] = scan.getRetentionTime();
                scanIndices[scanIndex] = pointIndex + 1;

                for (DataPoint dataPoint : dataPoints.get(scan)) {
                    masses[pointIndex] = dataPoint.getMZ();
                    intensities[pointIndex] = dataPoint.getIntensity();
                    ++pointIndex;
                }
            }

            // Set vectors
            rSession.assign("scantime", scanTimes);
            rSession.assign("scanindex", scanIndices);
            rSession.assign("mass", masses);
            rSession.assign("intensity", intensities);

            // Construct xcmsRaw object
            rSession.eval("xcmsRaw <- new(\"xcmsRaw\")");
            rSession.eval("xcmsRaw@tic <- intensity");
            rSession.eval("xcmsRaw@scantime <- scantime * " + SECONDS_PER_MINUTE);
            rSession.eval("xcmsRaw@scanindex <- as.integer(scanindex)");
            rSession.eval("xcmsRaw@env$mz <- mass");
            rSession.eval("xcmsRaw@env$intensity <- intensity");

            // Create the xcmsSet object.
            rSession.eval("xcmsSet <- new(\"xcmsSet\")");
            rSession.eval("xcmsSet@peaks <- peaks");
            rSession.eval("xcmsSet@filepaths  <- ''");
            rSession.assign("sampleName", peakList.getName());
            rSession.eval("sampnames(xcmsSet) <- sampleName");

            rSession.assign("filename", curFile.getAbsolutePath());
            rSession.eval("save(xcmsSet, xcmsRaw, file=filename)");

            rSession.runOnlyOnline();
            rSession.clearCode();
            rSession.close(userCanceled);
        }
        catch (RSessionWrapperException e) {
            if (!userCanceled) {
                setErrorMessage("R computing error: " + e.getMessage());
                setStatus(TaskStatus.ERROR);
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            if (!userCanceled) {
                setErrorMessage("Unknown error: " + e.getMessage());
                setStatus(TaskStatus.ERROR);
                e.printStackTrace();
            }
        }
    }
}
