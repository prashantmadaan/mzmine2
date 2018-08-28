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

import com.google.common.collect.Range;
import dulab.adap.datamodel.BetterComponent;
import dulab.adap.datamodel.BetterPeak;
import dulab.adap.datamodel.Chromatogram;
import dulab.adap.workflow.decomposition.RetTimeClusterer;
import dulab.adap.workflow.peakannotation.PeakAnnotation;
import dulab.adap.workflow.peakannotation.rules.AdductList;
import dulab.adap.workflow.simplespectraldeconvolution.Parameters;
import dulab.adap.workflow.simplespectraldeconvolution.SimpleSpectralDeconvolution;
import net.sf.mzmine.datamodel.*;
import net.sf.mzmine.datamodel.impl.*;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.adap3decompositionV2.ADAP3DecompositionV2Utils;
import net.sf.mzmine.modules.peaklistmethods.qualityparameters.QualityParameters;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aleksandrsmirnov
 */
public class SimpleSpectralDeconvolutionTask extends AbstractTask {

    // Logger.
    private static final Logger LOG = Logger.getLogger(SimpleSpectralDeconvolutionTask.class.getName());

    // Peak lists.
    private final MZmineProject project;
    private final PeakList peakList;
    private final ParameterSet parameters;
    private final SimpleSpectralDeconvolution deconvolution;
    private final PeakAnnotation annotation;


    SimpleSpectralDeconvolutionTask(final MZmineProject project, final PeakList peakList,
                                    final ParameterSet parameterSet) {
        // Initialize.
        this.project = project;
        this.peakList = peakList;
        this.parameters = parameterSet;
        this.deconvolution = new SimpleSpectralDeconvolution();
        this.annotation = new PeakAnnotation();
    }

    @Override
    public String getTaskDescription() {
        return "ADAP Simple Spectral Deconvolution on " + peakList;
    }

    @Override
    public double getFinishedPercentage() {
        return deconvolution.getProcessedPercent();
    }

    @Override
    public void run() {
        if (!isCanceled()) {
            String errorMsg = null;

            setStatus(TaskStatus.PROCESSING);
            LOG.info("Started ADAP Peak Decomposition on " + peakList);

            // Check raw data files.
            if (peakList.getNumberOfRawDataFiles() != 1) {
                setStatus(TaskStatus.ERROR);
                setErrorMessage("Peak Decomposition can only be performed on peak lists with a single raw data file");
            } else {

                try {

                    PeakList newPeakList = getNewPeakList(peakList);

                    if (!isCanceled()) {

                        // Add new peaklist to the project.
                        project.addPeakList(newPeakList);

                        // Add quality parameters to peaks
                        QualityParameters.calculateQualityParameters(newPeakList);

                        // Remove the original peaklist if requested.
                        if (parameters.getParameter(SimpleSpectralDeconvolutionParameters.AUTO_REMOVE).getValue()) {
                            project.removePeakList(peakList);
                        }

                        setStatus(TaskStatus.FINISHED);
                        LOG.info("Finished peak decomposition on " + peakList);
                    }

                } catch (IllegalArgumentException e) {
                    errorMsg = "Incorrect Peak List selected:\n"
                            + e.getMessage();
                } catch (IllegalStateException e) {
                    errorMsg = "Peak decompostion error:\n"
                            + e.getMessage();
                } catch (Exception e) {
                    errorMsg = "'Unknown error' during peak decomposition. \n"
                            + e.getMessage();
                } catch (Throwable t) {

                    setStatus(TaskStatus.ERROR);
                    setErrorMessage(t.getMessage());
                    LOG.log(Level.SEVERE, "Peak decompostion error", t);
                }

                // Report error.
                if (errorMsg != null) {
                    setErrorMessage(errorMsg);
                    setStatus(TaskStatus.ERROR);
                }
            }
        }
    }

    private PeakList getNewPeakList(@Nonnull PeakList peakList) {
        RawDataFile dataFile = peakList.getRawDataFile(0);

        // Create new peak list.
        final PeakList resolvedPeakList = new SimplePeakList(peakList + " "
                + parameters.getParameter(SimpleSpectralDeconvolutionParameters.SUFFIX).getValue(), dataFile);

        // Load previous applied methods.
        for (final PeakList.PeakListAppliedMethod method : peakList.getAppliedMethods()) {
            resolvedPeakList.addDescriptionOfAppliedTask(method);
        }

        // Add task description to peak list.
        resolvedPeakList.addDescriptionOfAppliedTask(new SimplePeakListAppliedMethod(
                "Simple Spectral Deconvolution by ADAP", parameters));

        // Collect peak information
        List<BetterPeak> peaks = new ADAP3DecompositionV2Utils().getPeaks(peakList);

        // Find components (a.k.a. clusters of peaks with fragmentation spectra)
        List<BetterComponent> components = getComponents(peaks);

        // Create PeakListRow for each components
        List<PeakListRow> newPeakListRows = new ArrayList<>();

        int rowID = 0;

        for (final BetterComponent component : components) {
            if (component.spectrum.length == 0) continue;

            // Create a reference peal
            Feature refPeak = getFeature(dataFile, component);

            // Add spectrum
            List<DataPoint> dataPoints = new ArrayList<>();
            for (int i = 0; i < component.spectrum.length; ++i) {
                double mz = component.spectrum.getMZ(i);
                double intensity = component.spectrum.getIntensity(i);
                if (intensity > 1e-3 * component.getIntensity())
                    dataPoints.add(new SimpleDataPoint(mz, intensity));
            }

            if (dataPoints.size() < 5) continue;

            refPeak.setIsotopePattern(new SimpleIsotopePattern(
                    dataPoints.toArray(new DataPoint[dataPoints.size()]),
                    IsotopePattern.IsotopePatternStatus.PREDICTED,
                    "Spectrum"));

            PeakListRow row = new SimplePeakListRow(++rowID);

            row.addPeak(dataFile, refPeak);

            // Set row properties
            row.setAverageMZ(refPeak.getMZ());
            row.setAverageRT(refPeak.getRT());

            // resolvedPeakList.addRow(row);
            newPeakListRows.add(row);
        }

        // ------------------------------------
        // Sort new peak rows by retention time
        // ------------------------------------

        newPeakListRows.sort(Comparator.comparingDouble(PeakListRow::getAverageRT));

        for (PeakListRow row : newPeakListRows)
            resolvedPeakList.addRow(row);

        return resolvedPeakList;
    }


    /**
     * Performs ADAP Peak Decomposition
     *
     * @param peaks  list of {@link BetterPeak} representing chromatograms
     * @param ranges arrays of {@link RetTimeClusterer.Item} containing ranges of detected peaks
     * @return Collection of dulab.adap.Component objects
     */

    private List<BetterComponent> getComponents(List<BetterPeak> peaks) {
        // -----------------------------
        // ADAP Decomposition Parameters
        // -----------------------------

        Parameters deconvolutionParams = new Parameters();

        deconvolutionParams.retTimeTolerance = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.RETENTION_TIME_TOLERANCE).getValue();
        deconvolutionParams.minNumPeaks = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.MIN_NUM_PEAKS).getValue();
        deconvolutionParams.peakSimilarityThreshold = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.SIMILARITY_TOLERANCE).getValue();

        List<BetterComponent> components = deconvolution.run(peaks, deconvolutionParams);

        String polarity = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.POLARITY).getValue();

        if (polarity == null || polarity.equals(SimpleSpectralDeconvolutionParameters.POLARITY_UNDEFINED))
            return components;

        AdductList adductList = null;
        switch (polarity) {
            case SimpleSpectralDeconvolutionParameters.POLARITY_POSITIVE:
                adductList = AdductList.GEN_LIST_POS;
                break;
            case SimpleSpectralDeconvolutionParameters.POLARITY_NEGATIVE:
                adductList = AdductList.GEN_LIST_NEG;
                break;
        }

        if (adductList != null) {

            PeakAnnotation.Parameters annotationParams =
                    new PeakAnnotation.Parameters(0.01, 0.01, adductList);

            components.forEach(c -> c.precursor = annotation
                    .run(c.spectrum, annotationParams)
                    .orElseThrow(() -> new IllegalStateException("Cannot find precursor.")));
        }

        return components;
    }

    @Nonnull
    private Feature getFeature(@Nonnull RawDataFile file, @Nonnull BetterComponent peak) {
        Chromatogram chromatogram = peak.chromatogram;
        double mzValue = peak.precursor != null ? peak.precursor : peak.mzValue;

        // Retrieve scan numbers
        int representativeScan = 0;
        int[] scanNumbers = new int[chromatogram.length];
        int count = 0;
        for (int num : file.getScanNumbers()) {
            double retTime = file.getScan(num).getRetentionTime();
            if (chromatogram.contains(retTime))
                scanNumbers[count++] = num;
            if (retTime == peak.getRetTime())
                representativeScan = num;
        }

        // Calculate peak area
        double area = 0.0;
        for (int i = 1; i < chromatogram.length; ++i) {
            double base = chromatogram.xs[i] - chromatogram.xs[i - 1];
            double height = 0.5 * (chromatogram.ys[i] + chromatogram.ys[i - 1]);
            area += base * height;
        }

        // Create array of DataPoints
        DataPoint[] dataPoints = new DataPoint[chromatogram.length];
        count = 0;
        for (double intensity : chromatogram.ys)
            dataPoints[count++] = new SimpleDataPoint(peak.getMZ(), intensity);

        return new SimpleFeature(file, mzValue, peak.getRetTime(), peak.getIntensity(),
                area, scanNumbers, dataPoints,
                Feature.FeatureStatus.MANUAL, representativeScan, representativeScan,
                Range.closed(peak.getFirstRetTime(), peak.getLastRetTime()),
                Range.closed(mzValue - 0.01, mzValue + 0.01),
                Range.closed(0.0, peak.getIntensity()));
    }

    @Override
    public void cancel() {
        deconvolution.cancel();
        super.cancel();
    }
}
