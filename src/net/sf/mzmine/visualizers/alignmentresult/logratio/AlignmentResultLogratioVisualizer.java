package net.sf.mzmine.visualizers.alignmentresult.logratio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.mzmine.data.AlignmentResult;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.userinterface.Desktop;
import net.sf.mzmine.userinterface.Desktop.MZmineMenu;
import net.sf.mzmine.visualizers.alignmentresult.AlignmentResultVisualizer;
import net.sf.mzmine.visualizers.alignmentresult.table.AlignmentResultTableVisualizerWindow;

public class AlignmentResultLogratioVisualizer implements
		AlignmentResultVisualizer, ActionListener, ListSelectionListener {

	private Desktop desktop;
	private JMenuItem myMenuItem;

	private Logger logger = Logger.getLogger(this.getClass().getName());	
	
	public void initModule(MZmineCore core) {

        this.desktop = core.getDesktop();

        myMenuItem = desktop.addMenuItem(MZmineMenu.VISUALIZATION, "Alignment result logratio plot", this, null, KeyEvent.VK_L, false, false);
        desktop.addSelectionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		
        AlignmentResult[] alignmentResults = desktop.getSelectedAlignmentResults();

        for (AlignmentResult alignmentResult : alignmentResults) {

			logger.finest("Showing a new alignment result logratio plot");

            AlignmentResultTableVisualizerWindow alignmentResultView = new AlignmentResultTableVisualizerWindow(alignmentResult);
            desktop.addInternalFrame(alignmentResultView);
        }
        
	}

	public void valueChanged(ListSelectionEvent e) {

		AlignmentResult[] alignmentResults = desktop.getSelectedAlignmentResults();
		if (alignmentResults.length>0) myMenuItem.setEnabled(true);

	}

}
