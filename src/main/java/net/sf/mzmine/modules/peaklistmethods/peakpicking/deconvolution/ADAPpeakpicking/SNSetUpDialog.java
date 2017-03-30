/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.ADAPpeakpicking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.ChromatogramTICDataSet;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakPreviewComboRenderer;
import net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution.PeakResolver;
import net.sf.mzmine.modules.visualization.tic.PeakDataSet;
import net.sf.mzmine.modules.visualization.tic.TICPlot;
import net.sf.mzmine.modules.visualization.tic.TICToolBar;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialog;
import net.sf.mzmine.util.GUIUtils;
import net.sf.mzmine.util.R.RSessionWrapper;
import net.sf.mzmine.util.R.RSessionWrapperException;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author owenmyers
 */
public class SNSetUpDialog extends ParameterSetupDialog{
        /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
        // Logger.
    private static final Logger LOG = Logger
            .getLogger(SNSetUpDialog.class.getName());
    
        // Combo-box font.
    private static final Font COMBO_FONT = new Font("SansSerif", Font.PLAIN,
            10);
    
    private final ParameterSet parameters;
    
    public SNSetUpDialog(Window parent, boolean valueCheckRequired,
            final ParameterSet SNParameters
           ) {

        super(parent, valueCheckRequired, SNParameters);

        parameters = SNParameters;
    }
}


