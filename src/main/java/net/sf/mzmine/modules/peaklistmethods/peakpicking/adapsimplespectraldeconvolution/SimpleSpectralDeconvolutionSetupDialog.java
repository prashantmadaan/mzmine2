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
import dulab.adap.datamodel.Peak;
import dulab.adap.workflow.simplespectraldeconvolution.Parameters;
import dulab.adap.workflow.simplespectraldeconvolution.SimpleSpectralDeconvolution;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.adap3decompositionV2.ADAP3DecompositionV2Utils;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.adap3decompositionV2.EICPlot;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialog;
import net.sf.mzmine.parameters.parametertypes.ranges.RTRangeComponent;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsSelection;
import net.sf.mzmine.util.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleSpectralDeconvolutionSetupDialog extends ParameterSetupDialog {

    /* Minimum dimensions of plots */
    private static final Dimension MIN_DIMENSIONS = new Dimension(400, 300);

    /* Font for the preview combo elements */
    private static final Font COMBO_FONT = new Font("SansSerif", Font.PLAIN, 10);

    private ParameterSet parameters;

    /*
     * Interface elements
     */
    private JPanel pnlPreview;
    private JPanel pnlParameters;
    private JPanel pnlPlot;
    private JCheckBox chkPreview;
    private JComboBox<PeakList> cboPeakLists;
    private JButton btnRefresh;
    private EICPlot plot;
    private RTRangeComponent retTimeRangeComponent;

    public SimpleSpectralDeconvolutionSetupDialog(Window parent, boolean valueCheckRequired, ParameterSet parameters) {
        super(parent, valueCheckRequired, parameters);
        this.parameters = parameters;
    }

    /* Creates the interface elements */
    @Override
    protected void addDialogComponents() {
        super.addDialogComponents();

        // Preview CheckBox
        chkPreview = new JCheckBox("Show preview");
        chkPreview.addActionListener(this);
        chkPreview.setHorizontalAlignment(SwingConstants.CENTER);
        chkPreview.setEnabled(true);

        // Preview panel with parameters
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JSeparator(), BorderLayout.NORTH);
        panel.add(chkPreview, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        // Panel with parameters
        pnlParameters = new JPanel(new BorderLayout());
        cboPeakLists = new JComboBox<>();
        cboPeakLists.setFont(COMBO_FONT);
        for (PeakList peakList : MZmineCore.getDesktop().getSelectedPeakLists())
            cboPeakLists.addItem(peakList);
        cboPeakLists.addActionListener(this);
//        pnlParameters.add(cboPeakLists, BorderLayout.NORTH);
        retTimeRangeComponent = new RTRangeComponent();
        retTimeRangeComponent.setEnabled(true);
//        pnlParameters.add(retTimeRangeComponent, BorderLayout.CENTER);
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(this);
//        pnlParameters.add(btnRefresh, BorderLayout.SOUTH);
        pnlParameters.add(
                GUIUtils.makeTablePanel(3, 3, 2, new JComponent[]{
                        new JLabel("Peak lists:"), cboPeakLists, new JPanel(),
                        new JPanel(), new JLabel("Reduce the retention time range to speed up the preview"), new JPanel(),
                        new JLabel("Ret time range:"), retTimeRangeComponent, btnRefresh}),
                BorderLayout.CENTER);

        pnlPreview = new JPanel(new BorderLayout());
        pnlPreview.add(panel, BorderLayout.NORTH);

        // Panel with plot
        plot = new EICPlot();
        plot.setMinimumSize(MIN_DIMENSIONS);
        plot.setPreferredSize(MIN_DIMENSIONS);

        pnlPlot = new JPanel(new BorderLayout());
        pnlPlot.setBackground(Color.white);
        pnlPlot.add(plot, BorderLayout.CENTER);
        GUIUtils.addMarginAndBorder(pnlPlot, 10);


        super.mainPanel.add(pnlPreview, 0, super.getNumberOfParameters() + 2,
                2, 1, 0, 0, GridBagConstraints.HORIZONTAL);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        final Object source = e.getSource();

        if (source.equals(chkPreview)) {
            if (chkPreview.isSelected()) {
                mainPanel.add(pnlPlot, 3, 0, 1, 200, 10, 10, GridBagConstraints.BOTH);
                pnlPreview.add(pnlParameters, BorderLayout.CENTER);
                refresh();
            } else {
                mainPanel.remove(pnlPlot);
                pnlPreview.remove(pnlParameters);
            }
            updateMinimumSize();
            pack();
            setLocationRelativeTo(MZmineCore.getDesktop().getMainWindow());
        } else if (source.equals(btnRefresh)) {
            refresh();
        } else if (source.equals(cboPeakLists)) {
            refresh();
        }
    }

    @Override
    public void parametersChanged() {
        super.updateParameterSetFromComponents();

        if (!chkPreview.isSelected()) return;

        refresh();
    }

    private void refresh() {
        Range<Double> retTimeRange = retTimeRangeComponent.getValue();
        if (retTimeRange == null) return;

        PeakList peakList = cboPeakLists.getItemAt(cboPeakLists.getSelectedIndex());
        if (peakList == null) return;

        List<BetterPeak> peaks = new ADAP3DecompositionV2Utils().getPeaks(peakList)
                .stream()
                .filter(p -> retTimeRange.contains(p.getRetTime()))
                .collect(Collectors.toList());

        Double retTimeToleranceParameter = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.RETENTION_TIME_TOLERANCE).getValue();
        Integer minNumPeakParameter = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.MIN_NUM_PEAKS).getValue();
        Double similarityToleranceParameter = parameters.getParameter(
                SimpleSpectralDeconvolutionParameters.SIMILARITY_TOLERANCE).getValue();

        if (retTimeToleranceParameter == null || minNumPeakParameter == null || similarityToleranceParameter == null)
            return;

        Parameters params = new Parameters();
        params.retTimeTolerance = retTimeToleranceParameter;
        params.minNumPeaks = minNumPeakParameter;
        params.peakSimilarityThreshold = similarityToleranceParameter;

        List<BetterComponent> components = new SimpleSpectralDeconvolution().run(peaks, params);
        plot.updateData(peaks, components);
    }
}
