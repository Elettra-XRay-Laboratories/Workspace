package com.elettra.lab.powder.diffractometer.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.controller.gui.fit.IFitResultListener;
import com.elettra.controller.gui.fit.MovementMatrix;
import com.elettra.controller.gui.fit.PlottableFitResult;
import com.elettra.lab.powder.diffractometer.Axis;
import com.elettra.lab.powder.diffractometer.panels.PsiUtilities.SampleHolder;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ResidualStressPanel extends MovementListener implements ActionListener, IFitResultListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2615009339614648005L;

	static class SampleHolderTxt
	{
		private static final String PHI_MOTOR_TXT = "Phi Motor";
		private static final String STATIC_TXT    = "Static";
		private static final String SPINNER_TXT   = "Spinner";
	}

	static class ActionCommands
	{
		private static final String CLEAR_PLOTS  = "CLEAR_PLOTS";
		private static final String EXPORT_DATA  = "EXPORT_DATA";
		private static final String PREVIOUS_PSI = "PREVIOUS_PSI";
		private static final String NEXT_PSI     = "NEXT_PSI";
		private static final String MOVE_PSI     = "MOVE_PSI";

	}

	private ICommunicationPort port;
	private boolean            isScanActive;

	private Vector             measureSetupsVector;
	private Vector             measureVector;

	private AxisConfiguration  axisZConfiguration;
	private AxisConfiguration  axisAlphaConfiguration;
	private AxisConfiguration  axisBetaConfiguration;
	private JComboBox          zSignComboBox;
	private JTextField         zMeasureUnit;
	private JTextField         zPosition;
	private JTextField         currentPsiPosition;
	private JTextField         axisZPosition;
	private JTextField         axisAlphaPosition;
	private JTextField         axisBetaPosition;
	private JTextField         axisZPositionOut;
	private JTextField         axisAlphaPositionOut;
	private JTextField         axisBetaPositionOut;
	private JTextField         zMeasureUnitOut;
	private JTextField         alphaMeasureUnitOut;
	private JTextField         betaMeasureUnitOut;
	private JTextField         psiMovingLabel;
	private JButton            movePsiButton;
	private XYSeriesCollection xyFWHMDataset;
	private XYPlot             FWHMplot;
	private XYSeriesCollection xyPositionDataset;
	private XYPlot             positionPlot;
	private JCheckBox[]        measureSetups;
	private JCheckBox          chckbxLockMeasureSetup;
	private JTextField         positionOkLabel;
	private JTextField         sin2PsiTextField;
	private JButton            previousPsiButton;
	private JButton            nextPsiButton;

	private int                currentMeasureIndex;
	private JComboBox          sampleHolderComboBox;

	/**
	 * Create the panel.
	 */
	public ResidualStressPanel(ICommunicationPort port) throws IOException
	{
		this.setBounds(0, 0, 670, 780);

		this.isScanActive = false;
		this.port = port;

		this.measureSetupsVector = new Vector<MeasurePoint>(13);
		this.measureSetupsVector.add(0, new MeasurePoint(-50.8, 0.6, 0));
		this.measureSetupsVector.add(1, new MeasurePoint(-45.0, 0.5, 1));
		this.measureSetupsVector.add(2, new MeasurePoint(-39.2, 0.4, 2));
		this.measureSetupsVector.add(3, new MeasurePoint(-33.2, 0.3, 3));
		this.measureSetupsVector.add(4, new MeasurePoint(-26.6, 0.2, 4));
		this.measureSetupsVector.add(5, new MeasurePoint(-18.4, 0.1, 5));
		this.measureSetupsVector.add(6, new MeasurePoint(0.0, 0.0, 6));
		this.measureSetupsVector.add(7, new MeasurePoint(18.4, 0.1, 7));
		this.measureSetupsVector.add(8, new MeasurePoint(26.6, 0.2, 8));
		this.measureSetupsVector.add(9, new MeasurePoint(33.2, 0.3, 9));
		this.measureSetupsVector.add(10, new MeasurePoint(39.2, 0.4, 10));
		this.measureSetupsVector.add(11, new MeasurePoint(45.0, 0.5, 11));
		this.measureSetupsVector.add(12, new MeasurePoint(50.6, 0.6, 12));

		this.measureVector = new Vector<MeasurePoint>(13);

		this.currentMeasureIndex = -1;

		ListenerRegister.getInstance().addListener(Axis.ALPHA, this);
		ListenerRegister.getInstance().addListener(Axis.BETA, this);
		ListenerRegister.getInstance().addListener(Axis.Z, this);

		ListenerRegister.getInstance().addFitResultListener(Axis.OMEGA2THETA, this);

		axisZConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.Z);
		axisAlphaConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.ALPHA);
		axisBetaConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.BETA);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 440, 340 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
		setLayout(gridBagLayout);

		JTabbedPane plotsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_plotsTabbedPane = new GridBagConstraints();
		gbc_plotsTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_plotsTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_plotsTabbedPane.gridx = 0;
		gbc_plotsTabbedPane.gridy = 0;
		add(plotsTabbedPane, gbc_plotsTabbedPane);

		JPanel plotsPanel = new JPanel();
		plotsTabbedPane.addTab("Plots", null, plotsPanel, null);
		plotsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_plotsPanel = new GridBagLayout();
		gbl_plotsPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_plotsPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_plotsPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
		gbl_plotsPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		plotsPanel.setLayout(gbl_plotsPanel);

		XYSeries seriesFWHM_positive = new XYSeries("FWHM - positive PSI");
		XYSeries seriesFWHM_negative = new XYSeries("FWHM - negative PSI");
		XYSeries seriesPosition_positive = new XYSeries("Position of the Peak - positive PSI");
		XYSeries seriesPosition_negative = new XYSeries("Position of the Peak - negative PSI");
		seriesFWHM_positive.setDescription(seriesFWHM_positive.getKey().toString());
		seriesFWHM_negative.setDescription(seriesFWHM_negative.getKey().toString());
		seriesPosition_positive.setDescription(seriesPosition_positive.getKey().toString());
		seriesPosition_negative.setDescription(seriesPosition_negative.getKey().toString());

		xyFWHMDataset = new XYSeriesCollection();
		xyFWHMDataset.addSeries(seriesFWHM_positive);
		xyFWHMDataset.addSeries(seriesFWHM_negative);

		xyPositionDataset = new XYSeriesCollection();
		xyPositionDataset.addSeries(seriesPosition_positive);
		xyPositionDataset.addSeries(seriesPosition_negative);

		this.initializePlots();

		JFreeChart FWHMGraph = ChartFactory.createXYLineChart(null, "Sin^2(Psi)", "FWHM (deg)", xyFWHMDataset, PlotOrientation.VERTICAL, false, true, false);
		JFreeChart positionGraph = ChartFactory.createXYLineChart(null, "Sin^2(Psi)", "Position (deg)", xyPositionDataset, PlotOrientation.VERTICAL, false, true, false);

		FWHMplot = FWHMGraph.getXYPlot();
		positionPlot = positionGraph.getXYPlot();

		FWHMplot.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		FWHMplot.getRangeAxis(0).setAutoRange(false);
		FWHMplot.getRangeAxis(0).setLowerBound(0);
		FWHMplot.getRangeAxis(0).setUpperBound(10);
		FWHMplot.getDomainAxis(0).setAutoRange(false);
		FWHMplot.getDomainAxis(0).setLowerBound(-0.1);
		FWHMplot.getDomainAxis(0).setUpperBound(0.7);
		FWHMplot.getDomainAxis(0).setFixedDimension(0.8);
		FWHMplot.getDomainAxis(0).setRangeWithMargins(-0.1, 0.7);
		FWHMplot.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xyFWHMlineandshaperenderer = (XYLineAndShapeRenderer) FWHMplot.getRenderer();
		xyFWHMlineandshaperenderer.setBaseShapesVisible(true);
		xyFWHMlineandshaperenderer.setUseFillPaint(true);
		xyFWHMlineandshaperenderer.setSeriesShape(0, shape);
		xyFWHMlineandshaperenderer.setSeriesPaint(0, Color.CYAN);
		xyFWHMlineandshaperenderer.setSeriesFillPaint(0, Color.CYAN);
		xyFWHMlineandshaperenderer.setSeriesShape(1, shape);
		xyFWHMlineandshaperenderer.setSeriesPaint(1, Color.RED);
		xyFWHMlineandshaperenderer.setSeriesFillPaint(1, Color.RED);

		FWHMGraph.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		positionPlot.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		positionPlot.getRangeAxis(0).setAutoRange(false);
		positionPlot.getRangeAxis(0).setLowerBound(0);
		positionPlot.getRangeAxis(0).setUpperBound(10);
		positionPlot.getDomainAxis(0).setAutoRange(false);
		positionPlot.getDomainAxis(0).setLowerBound(-0.1);
		positionPlot.getDomainAxis(0).setUpperBound(0.7);
		positionPlot.getDomainAxis(0).setFixedDimension(0.8);
		positionPlot.getDomainAxis(0).setRangeWithMargins(-0.1, 0.7);
		positionPlot.setBackgroundPaint(Color.BLACK);
		XYLineAndShapeRenderer xyPositionlineandshaperenderer = (XYLineAndShapeRenderer) positionPlot.getRenderer();
		xyPositionlineandshaperenderer.setBaseShapesVisible(true);
		xyPositionlineandshaperenderer.setUseFillPaint(true);
		xyPositionlineandshaperenderer.setSeriesShape(0, shape);
		xyPositionlineandshaperenderer.setSeriesPaint(0, Color.CYAN);
		xyPositionlineandshaperenderer.setSeriesFillPaint(0, Color.CYAN);
		xyPositionlineandshaperenderer.setSeriesShape(1, shape);
		xyPositionlineandshaperenderer.setSeriesPaint(1, Color.RED);
		xyPositionlineandshaperenderer.setSeriesFillPaint(1, Color.RED);

		positionGraph.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		JLabel lblFwhm = new JLabel("FWHM");
		lblFwhm.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_lblFwhm = new GridBagConstraints();
		gbc_lblFwhm.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblFwhm.insets = new Insets(10, 5, 5, 5);
		gbc_lblFwhm.gridx = 0;
		gbc_lblFwhm.gridy = 0;
		plotsPanel.add(lblFwhm, gbc_lblFwhm);

		JLabel lblPeakPosition = new JLabel("Peak Position");
		lblPeakPosition.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_lblPeakPosition = new GridBagConstraints();
		gbc_lblPeakPosition.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblPeakPosition.insets = new Insets(10, 5, 5, 5);
		gbc_lblPeakPosition.gridx = 2;
		gbc_lblPeakPosition.gridy = 0;
		plotsPanel.add(lblPeakPosition, gbc_lblPeakPosition);

		GridBagConstraints gbc_FWHMPlot = new GridBagConstraints();
		gbc_FWHMPlot.fill = GridBagConstraints.BOTH;
		gbc_FWHMPlot.insets = new Insets(0, 5, 5, 5);
		gbc_FWHMPlot.gridx = 0;
		gbc_FWHMPlot.gridy = 1;
		gbc_FWHMPlot.gridwidth = 2;
		JPanel scanInnerPanel1 = new JPanel();
		scanInnerPanel1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		scanInnerPanel1.add(new ChartPanel(FWHMGraph, 300, 300, 300, 300, 300, 300, false, false, false, false, false, false));
		plotsPanel.add(scanInnerPanel1, gbc_FWHMPlot);

		GridBagConstraints gbc_PositionPlot = new GridBagConstraints();
		gbc_PositionPlot.fill = GridBagConstraints.BOTH;
		gbc_PositionPlot.insets = new Insets(0, 5, 5, 5);
		gbc_PositionPlot.gridx = 2;
		gbc_PositionPlot.gridy = 1;
		gbc_PositionPlot.gridwidth = 2;
		JPanel scanInnerPanel2 = new JPanel();
		scanInnerPanel2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		scanInnerPanel2.add(new ChartPanel(positionGraph, 300, 300, 300, 300, 300, 300, false, false, false, false, false, false));
		plotsPanel.add(scanInnerPanel2, gbc_PositionPlot);

		JButton clearPlotsButton = new JButton("CLEAR PLOTS");
		clearPlotsButton.addActionListener(this);
		clearPlotsButton.setActionCommand(ActionCommands.CLEAR_PLOTS);
		GridBagConstraints gbc_clearPlotsButton = new GridBagConstraints();
		gbc_clearPlotsButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_clearPlotsButton.insets = new Insets(10, 5, 5, 5);
		gbc_clearPlotsButton.gridx = 0;
		gbc_clearPlotsButton.gridy = 2;
		plotsPanel.add(clearPlotsButton, gbc_clearPlotsButton);

		JButton exportDataButton = new JButton("EXPORT DATA");
		exportDataButton.addActionListener(this);
		exportDataButton.setActionCommand(ActionCommands.EXPORT_DATA);
		GridBagConstraints gbc_exportDataButton = new GridBagConstraints();
		gbc_exportDataButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_exportDataButton.insets = new Insets(10, 0, 5, 5);
		gbc_exportDataButton.gridx = 1;
		gbc_exportDataButton.gridy = 2;
		plotsPanel.add(exportDataButton, gbc_exportDataButton);

		JTabbedPane psiPositioningTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_psiPositioningTabbedPane = new GridBagConstraints();
		gbc_psiPositioningTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_psiPositioningTabbedPane.gridx = 0;
		gbc_psiPositioningTabbedPane.gridy = 1;
		add(psiPositioningTabbedPane, gbc_psiPositioningTabbedPane);

		JPanel psiPositioningPanel = new JPanel();
		psiPositioningTabbedPane.addTab("Psi Positioning", null, psiPositioningPanel, null);
		psiPositioningTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_psiPositioningPanel = new GridBagLayout();
		gbl_psiPositioningPanel.columnWidths = new int[] { 0, 76, 0, 0, 0, 0, 0, 0 };
		gbl_psiPositioningPanel.rowHeights = new int[] { 110, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_psiPositioningPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_psiPositioningPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		psiPositioningPanel.setLayout(gbl_psiPositioningPanel);

		JPanel measureSetupPanel = new JPanel();
		GridBagConstraints gbc_measureSetupPanel = new GridBagConstraints();
		gbc_measureSetupPanel.gridwidth = 7;
		gbc_measureSetupPanel.insets = new Insets(0, 0, 5, 0);
		gbc_measureSetupPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_measureSetupPanel.gridx = 0;
		gbc_measureSetupPanel.gridy = 0;
		psiPositioningPanel.add(measureSetupPanel, gbc_measureSetupPanel);
		GridBagLayout gbl_measureSetupPanel = new GridBagLayout();
		gbl_measureSetupPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_measureSetupPanel.rowHeights = new int[] { 0, 0, 34, 0 };
		gbl_measureSetupPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_measureSetupPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		measureSetupPanel.setLayout(gbl_measureSetupPanel);

		JLabel lblMeasureSetup = new JLabel("Measure Setup");
		GridBagConstraints gbc_lblMeasureSetup = new GridBagConstraints();
		gbc_lblMeasureSetup.anchor = GridBagConstraints.WEST;
		gbc_lblMeasureSetup.insets = new Insets(2, 5, 5, 5);
		gbc_lblMeasureSetup.gridx = 0;
		gbc_lblMeasureSetup.gridy = 0;
		measureSetupPanel.add(lblMeasureSetup, gbc_lblMeasureSetup);

		JPanel innerPanel1 = new JPanel();
		GridBagConstraints gbc_innerPanel1 = new GridBagConstraints();
		gbc_innerPanel1.gridheight = 3;
		gbc_innerPanel1.fill = GridBagConstraints.BOTH;
		gbc_innerPanel1.gridx = 1;
		gbc_innerPanel1.gridy = 0;
		measureSetupPanel.add(innerPanel1, gbc_innerPanel1);
		GridBagLayout gbl_innerPanel1 = new GridBagLayout();
		gbl_innerPanel1.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_innerPanel1.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_innerPanel1.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_innerPanel1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		innerPanel1.setLayout(gbl_innerPanel1);

		this.measureSetups = new JCheckBox[13];

		for (int index = 0; index < 13; index++)
			this.measureSetups[index] = new JCheckBox("");

		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 0;
		innerPanel1.add(this.measureSetups[0], gbc_chckbxNewCheckBox);

		GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_1.gridx = 1;
		gbc_chckbxNewCheckBox_1.gridy = 0;
		innerPanel1.add(this.measureSetups[1], gbc_chckbxNewCheckBox_1);

		GridBagConstraints gbc_chckbxLockMeasureSetup = new GridBagConstraints();
		gbc_chckbxLockMeasureSetup.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLockMeasureSetup.gridx = 2;
		gbc_chckbxLockMeasureSetup.gridy = 0;
		innerPanel1.add(this.measureSetups[2], gbc_chckbxLockMeasureSetup);

		GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
		gbc_checkBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_1.gridx = 3;
		gbc_checkBox_1.gridy = 0;
		innerPanel1.add(this.measureSetups[3], gbc_checkBox_1);

		GridBagConstraints gbc_checkBox_2 = new GridBagConstraints();
		gbc_checkBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_2.gridx = 4;
		gbc_checkBox_2.gridy = 0;
		innerPanel1.add(this.measureSetups[4], gbc_checkBox_2);

		GridBagConstraints gbc_checkBox_3 = new GridBagConstraints();
		gbc_checkBox_3.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_3.gridx = 5;
		gbc_checkBox_3.gridy = 0;
		innerPanel1.add(this.measureSetups[5], gbc_checkBox_3);

		GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_2.gridx = 6;
		gbc_chckbxNewCheckBox_2.gridy = 0;
		innerPanel1.add(this.measureSetups[6], gbc_chckbxNewCheckBox_2);

		GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_3.gridx = 7;
		gbc_chckbxNewCheckBox_3.gridy = 0;
		innerPanel1.add(this.measureSetups[7], gbc_chckbxNewCheckBox_3);

		GridBagConstraints gbc_checkBox_4 = new GridBagConstraints();
		gbc_checkBox_4.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_4.gridx = 8;
		gbc_checkBox_4.gridy = 0;
		innerPanel1.add(this.measureSetups[8], gbc_checkBox_4);

		GridBagConstraints gbc_checkBox_5 = new GridBagConstraints();
		gbc_checkBox_5.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_5.gridx = 9;
		gbc_checkBox_5.gridy = 0;
		innerPanel1.add(this.measureSetups[9], gbc_checkBox_5);

		GridBagConstraints gbc_checkBox_6 = new GridBagConstraints();
		gbc_checkBox_6.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_6.gridx = 10;
		gbc_checkBox_6.gridy = 0;
		innerPanel1.add(this.measureSetups[10], gbc_checkBox_6);

		GridBagConstraints gbc_checkBox_7 = new GridBagConstraints();
		gbc_checkBox_7.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox_7.gridx = 11;
		gbc_checkBox_7.gridy = 0;
		innerPanel1.add(this.measureSetups[11], gbc_checkBox_7);

		GridBagConstraints gbc_checkBox_8 = new GridBagConstraints();
		gbc_checkBox_8.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox_8.gridx = 12;
		gbc_checkBox_8.gridy = 0;
		innerPanel1.add(this.measureSetups[12], gbc_checkBox_8);

		JLabel label_13 = new JLabel("-50.8");
		label_13.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_13 = new GridBagConstraints();
		gbc_label_13.insets = new Insets(0, 0, 5, 5);
		gbc_label_13.gridx = 0;
		gbc_label_13.gridy = 1;
		innerPanel1.add(label_13, gbc_label_13);

		JLabel label_12 = new JLabel("-45.0");
		label_12.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_12 = new GridBagConstraints();
		gbc_label_12.insets = new Insets(0, 0, 5, 5);
		gbc_label_12.gridx = 1;
		gbc_label_12.gridy = 1;
		innerPanel1.add(label_12, gbc_label_12);

		JLabel label_11 = new JLabel("-39.2");
		label_11.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_11 = new GridBagConstraints();
		gbc_label_11.insets = new Insets(0, 0, 5, 5);
		gbc_label_11.gridx = 2;
		gbc_label_11.gridy = 1;
		innerPanel1.add(label_11, gbc_label_11);

		JLabel label_10 = new JLabel("-33.2");
		label_10.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.insets = new Insets(0, 0, 5, 5);
		gbc_label_10.gridx = 3;
		gbc_label_10.gridy = 1;
		innerPanel1.add(label_10, gbc_label_10);

		JLabel label_9 = new JLabel("-26.6");
		label_9.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.insets = new Insets(0, 0, 5, 5);
		gbc_label_9.gridx = 4;
		gbc_label_9.gridy = 1;
		innerPanel1.add(label_9, gbc_label_9);

		JLabel label_8 = new JLabel("-18.4");
		label_8.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		gbc_label_8.gridx = 5;
		gbc_label_8.gridy = 1;
		innerPanel1.add(label_8, gbc_label_8);

		JLabel label_7 = new JLabel("0.0");
		label_7.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.insets = new Insets(0, 0, 5, 5);
		gbc_label_7.gridx = 6;
		gbc_label_7.gridy = 1;
		innerPanel1.add(label_7, gbc_label_7);

		JLabel label_6 = new JLabel("18.4");
		label_6.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.insets = new Insets(0, 0, 5, 5);
		gbc_label_6.gridx = 7;
		gbc_label_6.gridy = 1;
		innerPanel1.add(label_6, gbc_label_6);

		JLabel label_5 = new JLabel("26.6");
		label_5.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		gbc_label_5.gridx = 8;
		gbc_label_5.gridy = 1;
		innerPanel1.add(label_5, gbc_label_5);

		JLabel label_4 = new JLabel("33.2");
		label_4.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.gridx = 9;
		gbc_label_4.gridy = 1;
		innerPanel1.add(label_4, gbc_label_4);

		JLabel label_3 = new JLabel("39.2");
		label_3.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 10;
		gbc_label_3.gridy = 1;
		innerPanel1.add(label_3, gbc_label_3);

		JLabel label_2 = new JLabel("45.0");
		label_2.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 11;
		gbc_label_2.gridy = 1;
		innerPanel1.add(label_2, gbc_label_2);

		JLabel label_1 = new JLabel("50.8");
		label_1.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 0);
		gbc_label_1.gridx = 12;
		gbc_label_1.gridy = 1;
		innerPanel1.add(label_1, gbc_label_1);

		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(0, 0, 0));
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.BOTH;
		gbc_separator.gridwidth = 13;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		innerPanel1.add(separator, gbc_separator);

		JLabel label_14 = new JLabel("0.6");
		label_14.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_14 = new GridBagConstraints();
		gbc_label_14.insets = new Insets(0, 0, 0, 5);
		gbc_label_14.gridx = 0;
		gbc_label_14.gridy = 3;
		innerPanel1.add(label_14, gbc_label_14);

		JLabel label_15 = new JLabel("0.5");
		label_15.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_15 = new GridBagConstraints();
		gbc_label_15.insets = new Insets(0, 0, 0, 5);
		gbc_label_15.gridx = 1;
		gbc_label_15.gridy = 3;
		innerPanel1.add(label_15, gbc_label_15);

		JLabel label_16 = new JLabel("0.4");
		label_16.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_16 = new GridBagConstraints();
		gbc_label_16.insets = new Insets(0, 0, 0, 5);
		gbc_label_16.gridx = 2;
		gbc_label_16.gridy = 3;
		innerPanel1.add(label_16, gbc_label_16);

		JLabel label_17 = new JLabel("0.3");
		label_17.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_17 = new GridBagConstraints();
		gbc_label_17.insets = new Insets(0, 0, 0, 5);
		gbc_label_17.gridx = 3;
		gbc_label_17.gridy = 3;
		innerPanel1.add(label_17, gbc_label_17);

		JLabel label_18 = new JLabel("0.2");
		label_18.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_18 = new GridBagConstraints();
		gbc_label_18.insets = new Insets(0, 0, 0, 5);
		gbc_label_18.gridx = 4;
		gbc_label_18.gridy = 3;
		innerPanel1.add(label_18, gbc_label_18);

		JLabel label_19 = new JLabel("0.1");
		label_19.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_19 = new GridBagConstraints();
		gbc_label_19.insets = new Insets(0, 0, 0, 5);
		gbc_label_19.gridx = 5;
		gbc_label_19.gridy = 3;
		innerPanel1.add(label_19, gbc_label_19);

		JLabel label_20 = new JLabel("0.0");
		label_20.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_20 = new GridBagConstraints();
		gbc_label_20.insets = new Insets(0, 0, 0, 5);
		gbc_label_20.gridx = 6;
		gbc_label_20.gridy = 3;
		innerPanel1.add(label_20, gbc_label_20);

		JLabel label_21 = new JLabel("0.1");
		label_21.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_21 = new GridBagConstraints();
		gbc_label_21.insets = new Insets(0, 0, 0, 5);
		gbc_label_21.gridx = 7;
		gbc_label_21.gridy = 3;
		innerPanel1.add(label_21, gbc_label_21);

		JLabel label_22 = new JLabel("0.2");
		label_22.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_22 = new GridBagConstraints();
		gbc_label_22.insets = new Insets(0, 0, 0, 5);
		gbc_label_22.gridx = 8;
		gbc_label_22.gridy = 3;
		innerPanel1.add(label_22, gbc_label_22);

		JLabel label_25 = new JLabel("0.3");
		label_25.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_25 = new GridBagConstraints();
		gbc_label_25.insets = new Insets(0, 0, 0, 5);
		gbc_label_25.gridx = 9;
		gbc_label_25.gridy = 3;
		innerPanel1.add(label_25, gbc_label_25);

		JLabel label_24 = new JLabel("0.4");
		label_24.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_24 = new GridBagConstraints();
		gbc_label_24.insets = new Insets(0, 0, 0, 5);
		gbc_label_24.gridx = 10;
		gbc_label_24.gridy = 3;
		innerPanel1.add(label_24, gbc_label_24);

		JLabel label_23 = new JLabel("0.5");
		label_23.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label_23 = new GridBagConstraints();
		gbc_label_23.insets = new Insets(0, 0, 0, 5);
		gbc_label_23.gridx = 11;
		gbc_label_23.gridy = 3;
		innerPanel1.add(label_23, gbc_label_23);

		JLabel label = new JLabel("0.6");
		label.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 12;
		gbc_label.gridy = 3;
		innerPanel1.add(label, gbc_label);

		JLabel lblPsi = new JLabel("Psi");
		lblPsi.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_lblPsi = new GridBagConstraints();
		gbc_lblPsi.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblPsi.insets = new Insets(5, 0, 5, 5);
		gbc_lblPsi.gridx = 0;
		gbc_lblPsi.gridy = 1;
		measureSetupPanel.add(lblPsi, gbc_lblPsi);

		JLabel lblSinpsi = new JLabel("Sin^2(Psi)");
		lblSinpsi.setForeground(new Color(0, 102, 51));
		GridBagConstraints gbc_lblSinpsi = new GridBagConstraints();
		gbc_lblSinpsi.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblSinpsi.insets = new Insets(6, 0, 0, 5);
		gbc_lblSinpsi.gridx = 0;
		gbc_lblSinpsi.gridy = 2;
		measureSetupPanel.add(lblSinpsi, gbc_lblSinpsi);

		chckbxLockMeasureSetup = new JCheckBox("Lock Measure Setup and Start Measuring");
		chckbxLockMeasureSetup.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				currentMeasureIndex = -1;
				sin2PsiTextField.setText("N.A.");
				currentPsiPosition.setText("N.A.");

				axisAlphaPositionOut.setText("");
				axisBetaPositionOut.setText("");
				axisZPositionOut.setText("");

				nextPsiButton.setEnabled(false);
				previousPsiButton.setEnabled(false);
				movePsiButton.setEnabled(false);

				if (chckbxLockMeasureSetup.isSelected())
				{
					measureVector = new Vector<MeasurePoint>(13);

					for (int index = 0; index < 13; index++)
					{
						measureSetups[index].setEnabled(false);

						if (measureSetups[index].isSelected())
							measureVector.addElement(measureSetupsVector.get(index));
					}

					if (measureVector.size() == 0)
					{
						GuiUtilities.showErrorPopup("Select at least one measure before locking the setup!", (JPanel) chckbxLockMeasureSetup.getParent());

						for (int index = 0; index < 13; index++)
							measureSetups[index].setEnabled(true);

						chckbxLockMeasureSetup.setSelected(false);
					}
					else
						nextPsiButton.setEnabled(true);
				}
				else
					for (int index = 0; index < 13; index++)
						measureSetups[index].setEnabled(true);
			}
		});
		GridBagConstraints gbc_chckbxLockMeasureSetup2 = new GridBagConstraints();
		gbc_chckbxLockMeasureSetup2.gridwidth = 3;
		gbc_chckbxLockMeasureSetup2.insets = new Insets(0, 5, 5, 5);
		gbc_chckbxLockMeasureSetup2.gridx = 0;
		gbc_chckbxLockMeasureSetup2.gridy = 1;
		psiPositioningPanel.add(chckbxLockMeasureSetup, gbc_chckbxLockMeasureSetup2);

		JLabel lblSampleZPos = new JLabel("Z at Psi=0");
		GridBagConstraints gbc_lblSampleZPos = new GridBagConstraints();
		gbc_lblSampleZPos.anchor = GridBagConstraints.WEST;
		gbc_lblSampleZPos.fill = GridBagConstraints.VERTICAL;
		gbc_lblSampleZPos.insets = new Insets(0, 5, 5, 5);
		gbc_lblSampleZPos.gridx = 0;
		gbc_lblSampleZPos.gridy = 2;
		psiPositioningPanel.add(lblSampleZPos, gbc_lblSampleZPos);

		String zPositionString = References.getInstance().get(References.SAMPLE_Z_AT_PSI_0);

		ControllerPosition referenceZposition = (zPositionString == null ? new ControllerPosition(0.0) : DriverUtilities.parseAxisPositionResponse(Axis.Z, zPositionString));

		zSignComboBox = new JComboBox();
		zSignComboBox.setModel(new DefaultComboBoxModel(new String[] { "+", "-" }));
		if (referenceZposition.getSign().equals(DriverUtilities.getPlus()))
			zSignComboBox.setSelectedIndex(0);
		else
			zSignComboBox.setSelectedIndex(1);
		zSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_zSignComboBox = new GridBagConstraints();
		gbc_zSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_zSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_zSignComboBox.gridx = 1;
		gbc_zSignComboBox.gridy = 2;
		psiPositioningPanel.add(zSignComboBox, gbc_zSignComboBox);

		zPosition = new JTextField();
		zPosition.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					zPosition.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zPosition.getText()))));
				}
				catch (Throwable t)
				{
					zPosition.setText("<not a number>");
				}
			}
		});
		zPosition.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					zPosition.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zPosition.getText()))));
				}
				catch (Throwable t)
				{
					zPosition.setText("<not a number>");
				}
			}
		});
		zPosition.setText(DriverUtilities.formatControllerPosition(referenceZposition.getAbsolutePosition()));
		zPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_zPosition = new GridBagConstraints();
		gbc_zPosition.insets = new Insets(0, 0, 5, 5);
		gbc_zPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_zPosition.gridx = 2;
		gbc_zPosition.gridy = 2;
		psiPositioningPanel.add(zPosition, gbc_zPosition);
		zPosition.setColumns(8);

		zMeasureUnit = new JTextField();
		zMeasureUnit.setEditable(false);
		zMeasureUnit.setText(axisZConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_zMeasureUnit = new GridBagConstraints();
		gbc_zMeasureUnit.anchor = GridBagConstraints.WEST;
		gbc_zMeasureUnit.insets = new Insets(0, 5, 5, 5);
		gbc_zMeasureUnit.gridx = 3;
		gbc_zMeasureUnit.gridy = 2;
		psiPositioningPanel.add(zMeasureUnit, gbc_zMeasureUnit);
		zMeasureUnit.setColumns(3);

		JLabel lblSampleHolder = new JLabel("Sample Holder");
		GridBagConstraints gbc_lblSampleHolder = new GridBagConstraints();
		gbc_lblSampleHolder.gridwidth = 2;
		gbc_lblSampleHolder.anchor = GridBagConstraints.WEST;
		gbc_lblSampleHolder.insets = new Insets(0, 5, 5, 5);
		gbc_lblSampleHolder.gridx = 0;
		gbc_lblSampleHolder.gridy = 3;
		psiPositioningPanel.add(lblSampleHolder, gbc_lblSampleHolder);

		sampleHolderComboBox = new JComboBox();
		sampleHolderComboBox.setModel(new DefaultComboBoxModel(new String[] { SampleHolderTxt.SPINNER_TXT, SampleHolderTxt.STATIC_TXT, SampleHolderTxt.PHI_MOTOR_TXT }));
		sampleHolderComboBox.setSelectedIndex(0);
		sampleHolderComboBox.setMaximumRowCount(3);
		GridBagConstraints gbc_sampleHolderComboBox = new GridBagConstraints();
		gbc_sampleHolderComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_sampleHolderComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sampleHolderComboBox.gridx = 2;
		gbc_sampleHolderComboBox.gridy = 3;
		psiPositioningPanel.add(sampleHolderComboBox, gbc_sampleHolderComboBox);

		previousPsiButton = new JButton("<<< PREVIOUS PSI");
		previousPsiButton.setActionCommand(ActionCommands.PREVIOUS_PSI);
		previousPsiButton.addActionListener(this);
		previousPsiButton.setEnabled(false);
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.fill = GridBagConstraints.HORIZONTAL;
		gbc_button.insets = new Insets(0, 5, 5, 5);
		gbc_button.gridx = 4;
		gbc_button.gridy = 3;
		psiPositioningPanel.add(previousPsiButton, gbc_button);

		nextPsiButton = new JButton("NEXT PSI >>>");
		nextPsiButton.setActionCommand(ActionCommands.NEXT_PSI);
		nextPsiButton.addActionListener(this);
		nextPsiButton.setEnabled(false);
		GridBagConstraints gbc_btnNextPsi = new GridBagConstraints();
		gbc_btnNextPsi.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNextPsi.insets = new Insets(0, 0, 5, 5);
		gbc_btnNextPsi.gridx = 5;
		gbc_btnNextPsi.gridy = 3;
		psiPositioningPanel.add(nextPsiButton, gbc_btnNextPsi);

		JLabel lblPositionOk = new JLabel("Position OK");
		GridBagConstraints gbc_lblPositionOk = new GridBagConstraints();
		gbc_lblPositionOk.insets = new Insets(0, 0, 5, 0);
		gbc_lblPositionOk.gridx = 6;
		gbc_lblPositionOk.gridy = 3;
		psiPositioningPanel.add(lblPositionOk, gbc_lblPositionOk);

		sin2PsiTextField = new JTextField();
		sin2PsiTextField.setText("N.A.");
		sin2PsiTextField.setEditable(false);
		sin2PsiTextField.setColumns(3);
		GridBagConstraints gbc_sin2PsiTextField = new GridBagConstraints();
		gbc_sin2PsiTextField.anchor = GridBagConstraints.WEST;
		gbc_sin2PsiTextField.insets = new Insets(0, 5, 5, 5);
		gbc_sin2PsiTextField.gridx = 3;
		gbc_sin2PsiTextField.gridy = 4;
		psiPositioningPanel.add(sin2PsiTextField, gbc_sin2PsiTextField);

		JLabel lblPosition = new JLabel("Current Psi");
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.anchor = GridBagConstraints.EAST;
		gbc_lblPosition.insets = new Insets(0, 5, 5, 5);
		gbc_lblPosition.gridx = 0;
		gbc_lblPosition.gridy = 4;
		psiPositioningPanel.add(lblPosition, gbc_lblPosition);

		currentPsiPosition = new JTextField();
		currentPsiPosition.setEditable(false);
		currentPsiPosition.setText("N.A.");
		currentPsiPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		currentPsiPosition.setColumns(8);
		GridBagConstraints gbc_currentPsiPosition = new GridBagConstraints();
		gbc_currentPsiPosition.anchor = GridBagConstraints.EAST;
		gbc_currentPsiPosition.insets = new Insets(0, 0, 5, 5);
		gbc_currentPsiPosition.gridx = 1;
		gbc_currentPsiPosition.gridy = 4;
		psiPositioningPanel.add(currentPsiPosition, gbc_currentPsiPosition);

		JLabel lblSinpsi_1 = new JLabel("Sin^2(Psi)");
		GridBagConstraints gbc_lblSinpsi_1 = new GridBagConstraints();
		gbc_lblSinpsi_1.anchor = GridBagConstraints.EAST;
		gbc_lblSinpsi_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblSinpsi_1.gridx = 2;
		gbc_lblSinpsi_1.gridy = 4;
		psiPositioningPanel.add(lblSinpsi_1, gbc_lblSinpsi_1);

		movePsiButton = new JButton("MOVE PSI");
		movePsiButton.setActionCommand(ActionCommands.MOVE_PSI);
		movePsiButton.addActionListener(this);
		movePsiButton.setEnabled(false);
		GridBagConstraints gbc_btnMovePsi = new GridBagConstraints();
		gbc_btnMovePsi.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnMovePsi.insets = new Insets(0, 0, 5, 5);
		gbc_btnMovePsi.gridx = 5;
		gbc_btnMovePsi.gridy = 4;
		psiPositioningPanel.add(movePsiButton, gbc_btnMovePsi);

		positionOkLabel = new JTextField("");
		positionOkLabel.setEditable(false);
		positionOkLabel.setColumns(2);
		positionOkLabel.setForeground(new Color(0, 102, 51));
		positionOkLabel.setBackground(new Color(0, 102, 51));
		GridBagConstraints gbc_positionOkLabel = new GridBagConstraints();
		gbc_positionOkLabel.insets = new Insets(0, 0, 10, 0);
		gbc_positionOkLabel.gridx = 6;
		gbc_positionOkLabel.gridy = 4;
		psiPositioningPanel.add(positionOkLabel, gbc_positionOkLabel);

		JLabel axis1LabelOut = new JLabel(axisZConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis1LabelOut = new GridBagConstraints();
		gbc_axis1LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis1LabelOut.insets = new Insets(5, 5, 5, 5);
		gbc_axis1LabelOut.gridx = 0;
		gbc_axis1LabelOut.gridy = 5;
		gbc_axis1LabelOut.gridwidth = 2;
		psiPositioningPanel.add(axis1LabelOut, gbc_axis1LabelOut);

		axisZPositionOut = new JTextField();
		axisZPositionOut.setEditable(false);
		GridBagConstraints gbc_axis1PositionOut = new GridBagConstraints();
		gbc_axis1PositionOut.anchor = GridBagConstraints.WEST;
		gbc_axis1PositionOut.insets = new Insets(5, 0, 5, 5);
		gbc_axis1PositionOut.gridx = 2;
		gbc_axis1PositionOut.gridy = 5;
		psiPositioningPanel.add(axisZPositionOut, gbc_axis1PositionOut);
		axisZPositionOut.setColumns(10);
		axisZPositionOut.setText("");

		zMeasureUnitOut = new JTextField();
		zMeasureUnitOut.setEditable(false);
		zMeasureUnitOut.setText(axisZConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_zMeasureUnitOut = new GridBagConstraints();
		gbc_zMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_zMeasureUnitOut.insets = new Insets(5, 5, 5, 5);
		gbc_zMeasureUnitOut.gridx = 3;
		gbc_zMeasureUnitOut.gridy = 5;
		psiPositioningPanel.add(zMeasureUnitOut, gbc_zMeasureUnitOut);
		zMeasureUnitOut.setColumns(3);

		JLabel axis2LabelOut = new JLabel(axisAlphaConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis2LabelOut = new GridBagConstraints();
		gbc_axis2LabelOut.insets = new Insets(0, 5, 5, 5);
		gbc_axis2LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis2LabelOut.gridx = 0;
		gbc_axis2LabelOut.gridy = 6;
		gbc_axis2LabelOut.gridwidth = 2;
		psiPositioningPanel.add(axis2LabelOut, gbc_axis2LabelOut);

		axisAlphaPositionOut = new JTextField();
		axisAlphaPositionOut.setEditable(false);
		GridBagConstraints gbc_axis2PositionOut = new GridBagConstraints();
		gbc_axis2PositionOut.anchor = GridBagConstraints.WEST;
		gbc_axis2PositionOut.insets = new Insets(0, 0, 5, 5);
		gbc_axis2PositionOut.gridx = 2;
		gbc_axis2PositionOut.gridy = 6;
		psiPositioningPanel.add(axisAlphaPositionOut, gbc_axis2PositionOut);
		axisAlphaPositionOut.setColumns(10);
		axisAlphaPositionOut.setText("");

		alphaMeasureUnitOut = new JTextField();
		alphaMeasureUnitOut.setEditable(false);
		alphaMeasureUnitOut.setText(axisAlphaConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_alphaMeasureUnitOut = new GridBagConstraints();
		gbc_alphaMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_alphaMeasureUnitOut.insets = new Insets(0, 5, 5, 5);
		gbc_alphaMeasureUnitOut.gridx = 3;
		gbc_alphaMeasureUnitOut.gridy = 6;
		psiPositioningPanel.add(alphaMeasureUnitOut, gbc_alphaMeasureUnitOut);
		alphaMeasureUnitOut.setColumns(3);

		JLabel axis3LabelOut = new JLabel(axisBetaConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis3LabelOut = new GridBagConstraints();
		gbc_axis3LabelOut.insets = new Insets(0, 5, 10, 5);
		gbc_axis3LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis3LabelOut.gridx = 0;
		gbc_axis3LabelOut.gridy = 7;
		gbc_axis3LabelOut.gridwidth = 2;
		psiPositioningPanel.add(axis3LabelOut, gbc_axis3LabelOut);

		axisBetaPositionOut = new JTextField();
		axisBetaPositionOut.setEditable(false);
		GridBagConstraints gbc_axis3PositionOut = new GridBagConstraints();
		gbc_axis3PositionOut.anchor = GridBagConstraints.WEST;
		gbc_axis3PositionOut.insets = new Insets(0, 0, 10, 5);
		gbc_axis3PositionOut.gridx = 2;
		gbc_axis3PositionOut.gridy = 7;
		psiPositioningPanel.add(axisBetaPositionOut, gbc_axis3PositionOut);
		axisBetaPositionOut.setColumns(10);
		axisBetaPositionOut.setText("");

		betaMeasureUnitOut = new JTextField();
		betaMeasureUnitOut.setEditable(false);
		betaMeasureUnitOut.setText(axisBetaConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_betaMeasureUnitOut = new GridBagConstraints();
		gbc_betaMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_betaMeasureUnitOut.insets = new Insets(0, 5, 10, 5);
		gbc_betaMeasureUnitOut.gridx = 3;
		gbc_betaMeasureUnitOut.gridy = 7;
		psiPositioningPanel.add(betaMeasureUnitOut, gbc_betaMeasureUnitOut);
		betaMeasureUnitOut.setColumns(3);

		JLabel lblNewLabel = new JLabel("Psi Moving");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 6;
		gbc_lblNewLabel.gridy = 6;
		psiPositioningPanel.add(lblNewLabel, gbc_lblNewLabel);

		psiMovingLabel = new JTextField("");
		psiMovingLabel.setEditable(false);
		psiMovingLabel.setColumns(2);
		psiMovingLabel.setForeground(new Color(0, 102, 51));
		psiMovingLabel.setBackground(new Color(0, 102, 51));
		GridBagConstraints gbc_psiMovingLabel = new GridBagConstraints();
		gbc_psiMovingLabel.insets = new Insets(0, 0, 10, 0);
		gbc_psiMovingLabel.gridx = 6;
		gbc_psiMovingLabel.gridy = 7;
		psiPositioningPanel.add(psiMovingLabel, gbc_psiMovingLabel);

		JLabel axis1Label = new JLabel(axisZConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis1Label = new GridBagConstraints();
		gbc_axis1Label.anchor = GridBagConstraints.EAST;
		gbc_axis1Label.insets = new Insets(5, 5, 5, 5);
		gbc_axis1Label.gridx = 4;
		gbc_axis1Label.gridy = 5;
		psiPositioningPanel.add(axis1Label, gbc_axis1Label);

		axisZPosition = new JTextField();
		axisZPosition.setEditable(false);
		GridBagConstraints gbc_axis1Position = new GridBagConstraints();
		gbc_axis1Position.anchor = GridBagConstraints.WEST;
		gbc_axis1Position.insets = new Insets(5, 0, 5, 5);
		gbc_axis1Position.gridx = 5;
		gbc_axis1Position.gridy = 5;
		psiPositioningPanel.add(axisZPosition, gbc_axis1Position);
		axisZPosition.setColumns(10);
		axisZPosition.setText(this.readAxisPosition(Axis.Z));

		JLabel axis2Label = new JLabel(axisAlphaConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis2Label = new GridBagConstraints();
		gbc_axis2Label.insets = new Insets(0, 5, 5, 5);
		gbc_axis2Label.anchor = GridBagConstraints.EAST;
		gbc_axis2Label.gridx = 4;
		gbc_axis2Label.gridy = 6;
		psiPositioningPanel.add(axis2Label, gbc_axis2Label);

		axisAlphaPosition = new JTextField();
		axisAlphaPosition.setEditable(false);
		GridBagConstraints gbc_axis2Position = new GridBagConstraints();
		gbc_axis2Position.anchor = GridBagConstraints.WEST;
		gbc_axis2Position.insets = new Insets(0, 0, 5, 5);
		gbc_axis2Position.gridx = 5;
		gbc_axis2Position.gridy = 6;
		psiPositioningPanel.add(axisAlphaPosition, gbc_axis2Position);
		axisAlphaPosition.setColumns(10);
		axisAlphaPosition.setText(this.readAxisPosition(Axis.ALPHA));

		JLabel axis3Label = new JLabel(axisBetaConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis3Label = new GridBagConstraints();
		gbc_axis3Label.insets = new Insets(0, 5, 10, 5);
		gbc_axis3Label.anchor = GridBagConstraints.EAST;
		gbc_axis3Label.gridx = 4;
		gbc_axis3Label.gridy = 7;
		psiPositioningPanel.add(axis3Label, gbc_axis3Label);

		axisBetaPosition = new JTextField();
		axisBetaPosition.setEditable(false);
		GridBagConstraints gbc_axis3Position = new GridBagConstraints();
		gbc_axis3Position.anchor = GridBagConstraints.WEST;
		gbc_axis3Position.insets = new Insets(0, 0, 10, 5);
		gbc_axis3Position.gridx = 5;
		gbc_axis3Position.gridy = 7;
		psiPositioningPanel.add(axisBetaPosition, gbc_axis3Position);
		axisBetaPosition.setColumns(10);
		axisBetaPosition.setText(this.readAxisPosition(Axis.BETA));

	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.MOVE_PSI))
					this.manageEventMovePsi();
				else if (e.getActionCommand().equals(ActionCommands.NEXT_PSI))
					this.manageEventNextPsi();
				else if (e.getActionCommand().equals(ActionCommands.PREVIOUS_PSI))
					this.manageEventPreviousPsi();
				else if (e.getActionCommand().equals(ActionCommands.CLEAR_PLOTS))
					this.manageEventClearPlots();
				else if (e.getActionCommand().equals(ActionCommands.EXPORT_DATA))
					this.manageEventExportData();
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}
		else
		{
			GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
		}
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * IFitListener
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	public void signalFitResult(int axis, PlottableFitResult fitResult)
	{
		if (this.currentMeasureIndex == -1)
			throw new RuntimeException("Select a collection of Measure Point, then act on the selection/movement buttons before doing the scan and sending the data");

		MeasurePoint currentMeasurePoint = (MeasurePoint) this.measureVector.get(this.currentMeasureIndex);

		if (currentMeasurePoint.getPsi() == 0.0)
		{
			this.xyFWHMDataset.getSeries(0).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakFWHM());
			this.xyFWHMDataset.getSeries(1).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakFWHM());
			this.xyPositionDataset.getSeries(0).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakPosition());
			this.xyPositionDataset.getSeries(1).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakPosition());
		}
		else if (currentMeasurePoint.getPsi() > 0.0)
		{
			this.xyFWHMDataset.getSeries(0).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakFWHM());
			this.xyPositionDataset.getSeries(0).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakPosition());
		}
		else
		{
			this.xyFWHMDataset.getSeries(1).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakFWHM());
			this.xyPositionDataset.getSeries(1).addOrUpdate(currentMeasurePoint.getSin2psi(), fitResult.getPeakPosition());
		}
	}

	public void signalInitialization(int axis, MovementMatrix matrix)
	{
		throw new RuntimeException("Method signalInitialization should not be called here");
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * IDriverListener
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		new PsiMoveListener(this).start();
	}

	public synchronized void signalScanStart()
	{
		this.isScanActive = true;
	}

	public synchronized void signalScanStop()
	{
		this.isScanActive = false;
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * EventManagement
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	private void manageEventNextPsi()
	{
		this.currentMeasureIndex++;

		this.doButtonsSetup();

		this.currentPsiPosition.setText(GuiUtilities.parseDouble(((MeasurePoint) this.measureVector.get(this.currentMeasureIndex)).getPsi()));
		this.sin2PsiTextField.setText(Double.toString(((MeasurePoint) this.measureVector.get(this.currentMeasureIndex)).getSin2psi()));

		this.calculateAxisPositionFromPsi();

		this.movePsiButton.setEnabled(true);
	}

	private void manageEventPreviousPsi()
	{
		this.currentMeasureIndex--;

		this.doButtonsSetup();

		this.currentPsiPosition.setText(GuiUtilities.parseDouble(((MeasurePoint) this.measureVector.get(this.currentMeasureIndex)).getPsi()));
		this.sin2PsiTextField.setText(Double.toString(((MeasurePoint) this.measureVector.get(this.currentMeasureIndex)).getSin2psi()));

		this.calculateAxisPositionFromPsi();

		this.movePsiButton.setEnabled(true);
	}

	private void manageEventMovePsi() throws CommunicationPortException
	{
		try
		{
			IDriverListener nullListener = GuiUtilities.getNullListener();

			ControllerPosition controllerPosition1 = DriverUtilities.parseAxisPositionResponse(Axis.Z, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.Z, nullListener), port));
			ControllerPosition controllerPosition2 = DriverUtilities.parseAxisPositionResponse(Axis.ALPHA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.ALPHA, nullListener), port));
			ControllerPosition controllerPosition3 = DriverUtilities.parseAxisPositionResponse(Axis.BETA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.BETA, nullListener), port));

			ControllerPosition controllerPositionOut1 = DriverUtilities.numberToController(Double.parseDouble(this.axisZPositionOut.getText()));
			ControllerPosition controllerPositionOut2 = DriverUtilities.numberToController(Double.parseDouble(this.axisAlphaPositionOut.getText()));
			ControllerPosition controllerPositionOut3 = DriverUtilities.numberToController(Double.parseDouble(this.axisBetaPositionOut.getText()));

			new StartPsiMoveProgram(this, new PsiMoveParameters(controllerPosition1, controllerPosition2, controllerPosition3, controllerPositionOut1, controllerPositionOut2, controllerPositionOut3), this.port).start();
		}
		catch (NumberFormatException exception)
		{
			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	private void doButtonsSetup()
	{
		this.nextPsiButton.setEnabled(true);
		this.previousPsiButton.setEnabled(true);

		if (this.currentMeasureIndex == this.measureVector.size() - 1)
			this.nextPsiButton.setEnabled(false);

		if (this.currentMeasureIndex == 0)
			this.previousPsiButton.setEnabled(false);
	}

	private synchronized void manageEventExportData() throws IOException
	{

		String fileName = GuiUtilities.showDatFileChooser(this, "ResidualStress_" + GuiUtilities.getNowString() + ".dat");

		try
		{
			StringUtilities.checkString(fileName, "");

			if (!fileName.toLowerCase().endsWith(".dat"))
				fileName += ".dat";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try
			{
				for (int plotIndex = 0; plotIndex < 2; plotIndex++)
				{
					XYSeries series = this.xyFWHMDataset.getSeries(plotIndex);

					this.writeDataFileHeading(series, writer, "FWHM");

					int nItems = series.getItemCount();

					for (int index = 0; index < nItems; index++)
					{
						writer.write(GuiUtilities.parseDouble(series.getX(index).doubleValue()).trim() + " " + series.getY(index).intValue());
						writer.newLine();
					}

					writer.newLine();
					writer.flush();
				}

				for (int plotIndex = 0; plotIndex < 2; plotIndex++)
				{
					XYSeries series = this.xyPositionDataset.getSeries(plotIndex);

					this.writeDataFileHeading(series, writer, "Position");

					int nItems = series.getItemCount();

					for (int index = 0; index < nItems; index++)
					{
						writer.write(GuiUtilities.parseDouble(series.getX(index).doubleValue()).trim() + " " + series.getY(index).intValue());
						writer.newLine();
					}

					writer.newLine();
					writer.flush();
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				writer.close();
			}
		}
		catch (IllegalArgumentException e)
		{

		}
	}

	private void manageEventClearPlots()
	{
		this.initializePlots();
	}

	private void initializePlots()
	{
		for (int plotIndex = 0; plotIndex < 2; plotIndex++)
		{
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.0, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.1, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.2, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.3, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.4, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.5, 0.0);
			this.xyFWHMDataset.getSeries(plotIndex).addOrUpdate(0.6, 0.0);

			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.0, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.1, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.2, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.3, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.4, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.5, 0.0);
			this.xyPositionDataset.getSeries(plotIndex).addOrUpdate(0.6, 0.0);
		}
	}

	private void calculateAxisPositionFromPsi()
	{
		boolean isError = false;
		ControllerPosition sampleZPosition = null;
		ControllerPosition psiPosition = null;

		try
		{
			sampleZPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.zSignComboBox.getSelectedItem()), Double.parseDouble(this.zPosition.getText()));
		}
		catch (NumberFormatException exception)
		{
			isError = true;
			this.zPosition.setText("<not a number>");

			GuiUtilities.showErrorPopup("Z Position should be a number (double)", this);
		}

		try
		{
			if (!isError)
				psiPosition = new ControllerPosition(Double.parseDouble(this.currentPsiPosition.getText()));
		}
		catch (NumberFormatException exception)
		{
			isError = true;
			currentPsiPosition.setText("<not a number>");

			GuiUtilities.showErrorPopup("Psi Position should be a number (double)", this);
		}

		if (!isError)
		{
			String sampleHolderTxt = (String) this.sampleHolderComboBox.getSelectedItem();
			SampleHolder sampleHolder = null;

			if (SampleHolderTxt.SPINNER_TXT.equals(sampleHolderTxt))
				sampleHolder = SampleHolder.SPINNER;
			else if (SampleHolderTxt.STATIC_TXT.equals(sampleHolderTxt))
				sampleHolder = SampleHolder.STATIC;
			else if (SampleHolderTxt.PHI_MOTOR_TXT.equals(sampleHolderTxt))
				sampleHolder = SampleHolder.PHI_MOTOR;

			PsiMoveParameters out = PsiUtilities.calculatePsi(sampleHolder, psiPosition, sampleZPosition);

			this.axisZPositionOut.setText(out.getAxisZPositionOut().getSign() + DriverUtilities.formatControllerPosition(out.getAxisZPositionOut().getAbsolutePosition()));
			this.axisAlphaPositionOut.setText(out.getAxisAlphaPositionOut().getSign() + DriverUtilities.formatControllerPosition(out.getAxisAlphaPositionOut().getAbsolutePosition()));
			this.axisBetaPositionOut.setText(out.getAxisBetaPositionOut().getSign() + DriverUtilities.formatControllerPosition(out.getAxisBetaPositionOut().getAbsolutePosition()));
		}
	}

	protected void setAxisPositionField(int axis, String text)
	{
		switch (axis)
		{
			case Axis.Z:
				this.axisZPosition.setText(text);
				break;
			case Axis.ALPHA:
				this.axisAlphaPosition.setText(text);
				break;
			case Axis.BETA:
				this.axisBetaPosition.setText(text);
				break;
			default:
				break;
		}
	}

	private String readAxisPosition(int axis) throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis, position);
	}

	// ----------------------------------------------------------------------------

	private void writeDataFileHeading(XYSeries series, BufferedWriter writer, String yName) throws IOException
	{
		String comment = FileIni.getInstance().getProperty("FileCommentString");

		writer.write(comment + "---------------------------------------------------------------------------------");
		writer.newLine();
		writer.write(comment + "DATA : " + series.getDescription());
		writer.newLine();
		writer.write(comment);
		writer.newLine();
		writer.write(comment + "Sin^2(Psi) " + yName);
		writer.newLine();
		writer.write(comment + "---------------------------------------------------------------------------------");
		writer.newLine();
	}

	protected void turnOnPsiMoving()
	{
		this.psiMovingLabel.setForeground(new Color(0, 204, 51));
		this.psiMovingLabel.setBackground(new Color(0, 204, 51));
	}

	protected void turnOffPsiMoving()
	{
		this.psiMovingLabel.setForeground(new Color(51, 102, 51));
		this.psiMovingLabel.setBackground(new Color(51, 102, 51));
	}

	protected void turnOnPositionOk()
	{
		this.positionOkLabel.setForeground(new Color(0, 204, 51));
		this.positionOkLabel.setBackground(new Color(0, 204, 51));
	}

	protected void turnOffPositionOk()
	{
		this.positionOkLabel.setForeground(new Color(51, 102, 51));
		this.positionOkLabel.setBackground(new Color(51, 102, 51));
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * Inner Classes
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	class StartPsiMoveProgram extends Thread
	{
		private ResidualStressPanel panel;
		private PsiMoveParameters   moveParameters;
		private ICommunicationPort  port;

		public StartPsiMoveProgram(ResidualStressPanel panel, PsiMoveParameters moveParameters, ICommunicationPort port)
		{
			super();

			this.moveParameters = moveParameters;
			this.port = port;
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				this.panel.nextPsiButton.setEnabled(false);
				this.panel.previousPsiButton.setEnabled(false);
				this.panel.movePsiButton.setEnabled(false);
				this.panel.turnOnPsiMoving();
				this.panel.turnOffPositionOk();

				PsiUtilities.movePsi(this.moveParameters, this.port);
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}

	class PsiMoveListener extends Thread
	{
		/**
		 * 
		 */
		private ResidualStressPanel panel;

		public PsiMoveListener(ResidualStressPanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition1 = DriverUtilities.parseAxisPositionResponse(Axis.Z, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.Z, nullListener), port));
				ControllerPosition controllerPosition2 = DriverUtilities.parseAxisPositionResponse(Axis.ALPHA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.ALPHA, nullListener), port));
				ControllerPosition controllerPosition3 = DriverUtilities.parseAxisPositionResponse(Axis.BETA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.BETA, nullListener), port));

				double previousPosition1 = controllerPosition1.getSignedPosition();
				double currentPosition1 = previousPosition1;
				double previousPosition2 = controllerPosition2.getSignedPosition();
				double currentPosition2 = previousPosition2;
				double previousPosition3 = controllerPosition3.getSignedPosition();
				double currentPosition3 = previousPosition3;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(Axis.Z, DriverUtilities.formatControllerPositionTextField(Axis.Z, controllerPosition1));
					this.panel.setAxisPositionField(Axis.ALPHA, DriverUtilities.formatControllerPositionTextField(Axis.ALPHA, controllerPosition2));
					this.panel.setAxisPositionField(Axis.BETA, DriverUtilities.formatControllerPositionTextField(Axis.BETA, controllerPosition3));

					ObjectUtilities.pause(200);

					controllerPosition1 = DriverUtilities.parseAxisPositionResponse(Axis.Z, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.Z, nullListener), port));

					previousPosition1 = currentPosition1;
					currentPosition1 = controllerPosition1.getSignedPosition();

					controllerPosition2 = DriverUtilities.parseAxisPositionResponse(Axis.ALPHA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.ALPHA, nullListener), port));

					previousPosition2 = currentPosition2;
					currentPosition2 = controllerPosition2.getSignedPosition();

					controllerPosition3 = DriverUtilities.parseAxisPositionResponse(Axis.BETA, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.BETA, nullListener), port));

					previousPosition3 = currentPosition3;
					currentPosition3 = controllerPosition3.getSignedPosition();

					if (currentPosition1 == previousPosition1 && currentPosition2 == previousPosition2 && currentPosition3 == previousPosition3)
						doCycle = ++trial < 5;

				} while (doCycle);

				this.panel.turnOffPsiMoving();
				this.panel.turnOnPositionOk();
				this.panel.doButtonsSetup();
				this.panel.movePsiButton.setEnabled(false);
			}
			catch (CommunicationPortException exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}

	class MeasurePoint
	{
		private double psi;
		private double sin2psi;
		private int    measureIndex;

		public MeasurePoint(double psi, double sin2psi, int measureIndex)
		{
			super();
			this.psi = psi;
			this.sin2psi = sin2psi;
			this.measureIndex = measureIndex;
		}

		public double getPsi()
		{
			return this.psi;
		}

		public double getSin2psi()
		{
			return this.sin2psi;
		}

		public int getMeasureIndex()
		{
			return this.measureIndex;
		}

	}
}
