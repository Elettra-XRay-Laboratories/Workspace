package com.elettra.controller.gui.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.StabilityDurations;
import com.elettra.controller.driver.programs.StabilityParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MeasureListener;

public class TubeStabilityPanel extends MeasureListener implements
		ActionListener {
	static class ActionCommands {
		private static final String SCAN = "SCAN";
		private static final String STOP = "STOP";
		private static final String DELETE_LAST_SCAN = "DELETE_LAST_SCAN";
		private static final String RESET = "RESET";
		private static final String SAVE_LAST_SCAN = "SAVE_LAST_SCAN";
		private static final String SAVE_ALL = "SAVE_ALL";
	}

	private ICommunicationPort port;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8475330198111414161L;
	private JTextField scanProgressiveTextField;
	private JTextField colorTextField;
	private XYSeriesCollection xyDataset;
	private XYSeriesCollection xyDatasetAddInfo1;
	private XYSeriesCollection xyDatasetAddInfo2;
	private boolean isStopScanActivated;

	private short scanIndex = -1;
	private JProgressBar progressBar;
	private boolean isScanActive = false;
	private JComboBox<String> scanTimeComboBox;
	private XYPlot plot;
	private XYPlot plotAI1;
	private XYPlot plotAI2;

	protected static final int STABILILTY_FAKE_AXIS = 999;

	protected JTabbedPane scanGraphTabbedPane;

	public TubeStabilityPanel(ICommunicationPort port)
			throws CommunicationPortException {
		this.port = port;

		ListenerRegister.getInstance().addListener(STABILILTY_FAKE_AXIS, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.2 };
		setLayout(gridBagLayout);

		scanGraphTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanGraphTabbedPane = new GridBagConstraints();
		gbc_scanGraphTabbedPane.insets = new Insets(10, 0, 5, 0);
		gbc_scanGraphTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_scanGraphTabbedPane.gridx = 0;
		gbc_scanGraphTabbedPane.gridy = 0;
		add(scanGraphTabbedPane, gbc_scanGraphTabbedPane);

		JPanel scanGraphPanel = new JPanel();
		scanGraphPanel.setForeground(new Color(0, 0, 0));
		scanGraphTabbedPane
				.addTab("Graph of Scans", null, scanGraphPanel, null);
		GridBagLayout gbl_scanGraphPanel = new GridBagLayout();
		gbl_scanGraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_scanGraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_scanGraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_scanGraphPanel.rowWeights = new double[] { 1.0, 0.0,
				Double.MIN_VALUE };
		scanGraphPanel.setLayout(gbl_scanGraphPanel);

		JPanel scanInnerPanel = new JPanel();
		scanInnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		GridBagConstraints gbc_scanInnerPanel = new GridBagConstraints();
		gbc_scanInnerPanel.gridwidth = 7;
		gbc_scanInnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_scanInnerPanel.fill = GridBagConstraints.BOTH;
		gbc_scanInnerPanel.gridx = 0;
		gbc_scanInnerPanel.gridy = 0;
		scanGraphPanel.add(scanInnerPanel, gbc_scanInnerPanel);

		XYSeries series1 = new XYSeries("Scan 1");
		XYSeries series2 = new XYSeries("Scan 2");
		XYSeries series3 = new XYSeries("Scan 3");

		xyDataset = new XYSeriesCollection();
		xyDataset.addSeries(series1);
		xyDataset.addSeries(series2);
		xyDataset.addSeries(series3);

		JFreeChart scanGraphs = ChartFactory.createXYLineChart(null,
				"Time (seconds)", "Counts", xyDataset,
				PlotOrientation.VERTICAL, false, true, false);

		plot = scanGraphs.getXYPlot();

		plot.getRangeAxis(0).setStandardTickUnits(
				NumberAxis.createIntegerTickUnits());
		plot.getRangeAxis(0).setAutoRange(false);
		plot.getRangeAxis(0).setLowerBound(0);
		plot.getRangeAxis(0).setUpperBound(10);

		plot.getDomainAxis(0).setAutoRange(false);
		plot.getDomainAxis(0).setLowerBound(0);
		plot.getDomainAxis(0).setUpperBound(10);

		plot.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		xylineandshaperenderer.setBaseShapesVisible(true);
		xylineandshaperenderer.setUseFillPaint(true);
		xylineandshaperenderer.setSeriesShape(0, shape);
		xylineandshaperenderer.setSeriesPaint(0, Color.RED);
		xylineandshaperenderer.setSeriesFillPaint(0, Color.RED);
		xylineandshaperenderer.setSeriesShape(1, shape);
		xylineandshaperenderer.setSeriesPaint(1, Color.GREEN);
		xylineandshaperenderer.setSeriesFillPaint(1, Color.GREEN);
		xylineandshaperenderer.setSeriesShape(2, shape);
		xylineandshaperenderer.setSeriesPaint(2, Color.CYAN);
		xylineandshaperenderer.setSeriesFillPaint(2, Color.CYAN);

		scanGraphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		scanInnerPanel.setLayout(new FlowLayout());
		scanInnerPanel.add(new ChartPanel(scanGraphs, 700, 500, 700, 500, 700,
				500, false, false, false, false, false, false));

		JLabel lblPlotColor = new JLabel("Plot Color");
		GridBagConstraints gbc_lblPlotColor = new GridBagConstraints();
		gbc_lblPlotColor.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlotColor.anchor = GridBagConstraints.EAST;
		gbc_lblPlotColor.gridx = 0;
		gbc_lblPlotColor.gridy = 1;
		scanGraphPanel.add(lblPlotColor, gbc_lblPlotColor);

		colorTextField = new JTextField();
		colorTextField.setEditable(false);
		colorTextField.setBackground(Color.BLACK);
		GridBagConstraints gbc_colorTextField = new GridBagConstraints();
		gbc_colorTextField.insets = new Insets(0, 0, 5, 5);
		gbc_colorTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorTextField.gridx = 1;
		gbc_colorTextField.gridy = 1;
		scanGraphPanel.add(colorTextField, gbc_colorTextField);
		colorTextField.setColumns(5);

		scanProgressiveTextField = new JTextField();
		scanProgressiveTextField.setText("0");
		scanProgressiveTextField.setEditable(false);
		GridBagConstraints gbc_scanProgressiveTextField = new GridBagConstraints();
		gbc_scanProgressiveTextField.anchor = GridBagConstraints.WEST;
		gbc_scanProgressiveTextField.insets = new Insets(0, 0, 5, 5);
		gbc_scanProgressiveTextField.gridx = 2;
		gbc_scanProgressiveTextField.gridy = 1;
		scanGraphPanel.add(scanProgressiveTextField,
				gbc_scanProgressiveTextField);
		scanProgressiveTextField.setColumns(1);

		JButton deleteLastScanButton = new JButton("DELETE LAST SCAN");
		deleteLastScanButton.setActionCommand(ActionCommands.DELETE_LAST_SCAN);
		deleteLastScanButton.addActionListener(this);
		GridBagConstraints gbc_deleteLastScanButton = new GridBagConstraints();
		gbc_deleteLastScanButton.insets = new Insets(0, 0, 5, 5);
		gbc_deleteLastScanButton.gridx = 3;
		gbc_deleteLastScanButton.gridy = 1;
		scanGraphPanel.add(deleteLastScanButton, gbc_deleteLastScanButton);

		JButton resetButton = new JButton(ActionCommands.RESET);
		resetButton.setActionCommand(ActionCommands.RESET);
		resetButton.addActionListener(this);
		GridBagConstraints gbc_resetButton = new GridBagConstraints();
		gbc_resetButton.insets = new Insets(0, 0, 5, 15);
		gbc_resetButton.gridx = 4;
		gbc_resetButton.gridy = 1;
		scanGraphPanel.add(resetButton, gbc_resetButton);

		JButton saveLastScanButton = new JButton("SAVE LAST SCAN");
		saveLastScanButton.setActionCommand(ActionCommands.SAVE_LAST_SCAN);
		saveLastScanButton.addActionListener(this);
		GridBagConstraints gbc_saveLastScanButton = new GridBagConstraints();
		gbc_saveLastScanButton.insets = new Insets(0, 0, 5, 5);
		gbc_saveLastScanButton.gridx = 5;
		gbc_saveLastScanButton.gridy = 1;
		scanGraphPanel.add(saveLastScanButton, gbc_saveLastScanButton);

		JButton saveAllButton = new JButton("SAVE ALL");
		saveAllButton.setActionCommand(ActionCommands.SAVE_ALL);
		saveAllButton.addActionListener(this);
		GridBagConstraints gbc_saveAllButton = new GridBagConstraints();
		gbc_saveAllButton.insets = new Insets(0, 0, 5, 5);
		gbc_saveAllButton.gridx = 6;
		gbc_saveAllButton.gridy = 1;
		scanGraphPanel.add(saveAllButton, gbc_saveAllButton);
		scanGraphTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		// ---------------------------------------------------------------------------------------------------
		// Additional Informations
		// ---------------------------------------------------------------------------------------------------

		JPanel addInfo1GraphPanel = new JPanel();
		addInfo1GraphPanel.setForeground(new Color(0, 0, 0));
		if (this.isAdditionalInformation1Visible()) {
			scanGraphTabbedPane.addTab(getAdditionaInfo1TabName(), null,
					addInfo1GraphPanel, null);
			scanGraphTabbedPane.setForegroundAt(1, new Color(0, 102, 0));
		}
		GridBagLayout gbl_addInfo1GraphPanel = new GridBagLayout();
		gbl_addInfo1GraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0,
				0 };
		gbl_addInfo1GraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_addInfo1GraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_addInfo1GraphPanel.rowWeights = new double[] { 1.0, 0.0,
				Double.MIN_VALUE };

		addInfo1GraphPanel.setLayout(gbl_addInfo1GraphPanel);

		JPanel addInfo1InnerPanel = new JPanel();
		addInfo1InnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		GridBagConstraints gbc_addInfo1InnerPanel = new GridBagConstraints();
		gbc_addInfo1InnerPanel.gridwidth = 8;
		gbc_addInfo1InnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_addInfo1InnerPanel.fill = GridBagConstraints.BOTH;
		gbc_addInfo1InnerPanel.gridx = 0;
		gbc_addInfo1InnerPanel.gridy = 0;
		addInfo1GraphPanel.add(addInfo1InnerPanel, gbc_addInfo1InnerPanel);

		XYSeries series11 = new XYSeries("Scan 1 addInf1");
		XYSeries series12 = new XYSeries("Scan 2 addInf1");
		XYSeries series13 = new XYSeries("Scan 3 addInf1");

		xyDatasetAddInfo1 = new XYSeriesCollection();

		xyDatasetAddInfo1.addSeries(series11);
		xyDatasetAddInfo1.addSeries(series12);
		xyDatasetAddInfo1.addSeries(series13);

		JFreeChart addInfo1Graphs = ChartFactory.createXYLineChart(null,
				"Time (seconds)", getAdditionalInfo1Name(), xyDatasetAddInfo1,
				PlotOrientation.VERTICAL, false, true, false);

		plotAI1 = addInfo1Graphs.getXYPlot();

		plotAI1.getRangeAxis(0).setStandardTickUnits(
				NumberAxis.createIntegerTickUnits());
		plotAI1.getRangeAxis(0).setAutoRange(false);
		plotAI1.getRangeAxis(0).setLowerBound(0);
		plotAI1.getRangeAxis(0).setUpperBound(10);

		plotAI1.getDomainAxis(0).setAutoRange(false);
		plotAI1.getDomainAxis(0).setLowerBound(0);
		plotAI1.getDomainAxis(0).setUpperBound(10);

		plotAI1.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape1 = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer1 = (XYLineAndShapeRenderer) plotAI1
				.getRenderer();
		xylineandshaperenderer1.setBaseShapesVisible(true);
		xylineandshaperenderer1.setUseFillPaint(true);
		xylineandshaperenderer1.setSeriesShape(0, shape1);
		xylineandshaperenderer1.setSeriesPaint(0, Color.RED);
		xylineandshaperenderer1.setSeriesFillPaint(0, Color.RED);
		xylineandshaperenderer1.setSeriesShape(1, shape1);
		xylineandshaperenderer1.setSeriesPaint(1, Color.GREEN);
		xylineandshaperenderer1.setSeriesFillPaint(1, Color.GREEN);
		xylineandshaperenderer1.setSeriesShape(2, shape1);
		xylineandshaperenderer1.setSeriesPaint(2, Color.CYAN);
		xylineandshaperenderer1.setSeriesFillPaint(2, Color.CYAN);
		xylineandshaperenderer1.setSeriesShape(3, shape1);
		xylineandshaperenderer1.setSeriesPaint(3, Color.WHITE);
		xylineandshaperenderer1.setSeriesFillPaint(3, Color.WHITE);

		addInfo1Graphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		addInfo1InnerPanel.setLayout(new FlowLayout());
		addInfo1InnerPanel.add(new ChartPanel(addInfo1Graphs, 700, 500, 700,
				500, 700, 500, false, false, false, false, false, false));

		JPanel filler1 = new JPanel();
		GridBagConstraints gbc_lblFiller1 = new GridBagConstraints();
		gbc_lblFiller1.insets = new Insets(0, 0, 0, 5);
		gbc_lblFiller1.anchor = GridBagConstraints.WEST;
		gbc_lblFiller1.fill = GridBagConstraints.BOTH;
		gbc_lblFiller1.gridx = 0;
		gbc_lblFiller1.gridy = 1;
		addInfo1GraphPanel.add(filler1, gbc_lblFiller1);

		// ---------------------------------------------------------------------------------------------------

		JPanel addInfo2GraphPanel = new JPanel();
		addInfo2GraphPanel.setForeground(new Color(0, 0, 0));
		if (this.isAdditionalInformation2Visible()) {
			scanGraphTabbedPane.addTab(getAdditionaInfo2TabName(), null,
					addInfo2GraphPanel, null);
			scanGraphTabbedPane.setForegroundAt(2, new Color(0, 102, 51));
		}

		GridBagLayout gbl_addInfo2GraphPanel = new GridBagLayout();
		gbl_addInfo2GraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0,
				0 };
		gbl_addInfo2GraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_addInfo2GraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_addInfo2GraphPanel.rowWeights = new double[] { 1.0, 0.0,
				Double.MIN_VALUE };
		addInfo2GraphPanel.setLayout(gbl_addInfo2GraphPanel);

		JPanel addInfo2InnerPanel = new JPanel();
		addInfo2InnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		GridBagConstraints gbc_addInfo2InnerPanel = new GridBagConstraints();
		gbc_addInfo2InnerPanel.gridwidth = 8;
		gbc_addInfo2InnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_addInfo2InnerPanel.fill = GridBagConstraints.BOTH;
		gbc_addInfo2InnerPanel.gridx = 0;
		gbc_addInfo2InnerPanel.gridy = 0;
		addInfo2GraphPanel.add(addInfo2InnerPanel, gbc_addInfo2InnerPanel);

		XYSeries series21 = new XYSeries("Scan 1 addInf2");
		XYSeries series22 = new XYSeries("Scan 2 addInf2");
		XYSeries series23 = new XYSeries("Scan 3 addInf2");

		xyDatasetAddInfo2 = new XYSeriesCollection();
		xyDatasetAddInfo2.addSeries(series21);
		xyDatasetAddInfo2.addSeries(series22);
		xyDatasetAddInfo2.addSeries(series23);

		JFreeChart addInfo2Graphs = ChartFactory.createXYLineChart(null,
				"Time (seconds)", getAdditionalInfo2Name(), xyDatasetAddInfo2,
				PlotOrientation.VERTICAL, false, true, false);

		plotAI2 = addInfo2Graphs.getXYPlot();

		plotAI2.getRangeAxis(0).setStandardTickUnits(
				NumberAxis.createIntegerTickUnits());
		plotAI2.getRangeAxis(0).setAutoRange(false);
		plotAI2.getRangeAxis(0).setLowerBound(0);
		plotAI2.getRangeAxis(0).setUpperBound(10);

		plotAI2.getDomainAxis(0).setAutoRange(false);
		plotAI2.getDomainAxis(0).setLowerBound(0);
		plotAI2.getDomainAxis(0).setUpperBound(10);

		plotAI2.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape2 = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer2 = (XYLineAndShapeRenderer) plotAI2
				.getRenderer();
		xylineandshaperenderer2.setBaseShapesVisible(true);
		xylineandshaperenderer2.setUseFillPaint(true);
		xylineandshaperenderer2.setSeriesShape(0, shape2);
		xylineandshaperenderer2.setSeriesPaint(0, Color.RED);
		xylineandshaperenderer2.setSeriesFillPaint(0, Color.RED);
		xylineandshaperenderer2.setSeriesShape(1, shape2);
		xylineandshaperenderer2.setSeriesPaint(1, Color.GREEN);
		xylineandshaperenderer2.setSeriesFillPaint(1, Color.GREEN);
		xylineandshaperenderer2.setSeriesShape(2, shape2);
		xylineandshaperenderer2.setSeriesPaint(2, Color.CYAN);
		xylineandshaperenderer2.setSeriesFillPaint(2, Color.CYAN);
		xylineandshaperenderer2.setSeriesShape(3, shape2);
		xylineandshaperenderer2.setSeriesPaint(3, Color.WHITE);
		xylineandshaperenderer2.setSeriesFillPaint(3, Color.WHITE);

		addInfo2Graphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		addInfo2InnerPanel.setLayout(new FlowLayout());
		addInfo2InnerPanel.add(new ChartPanel(addInfo2Graphs, 700, 500, 700,
				500, 700, 500, false, false, false, false, false, false));

		JPanel filler2 = new JPanel();
		GridBagConstraints gbc_lblFiller2 = new GridBagConstraints();
		gbc_lblFiller2.insets = new Insets(0, 0, 0, 5);
		gbc_lblFiller2.anchor = GridBagConstraints.WEST;
		gbc_lblFiller2.fill = GridBagConstraints.BOTH;
		gbc_lblFiller2.gridx = 0;
		gbc_lblFiller2.gridy = 1;
		addInfo2GraphPanel.add(filler2, gbc_lblFiller2);

		// ---------------------------------------------------------------------------------------------------
		// SCAN MANAGEMENT
		// ---------------------------------------------------------------------------------------------------

		JTabbedPane scanManagementTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanManagementTabbedPane = new GridBagConstraints();
		gbc_scanManagementTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_scanManagementTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_scanManagementTabbedPane.gridx = 0;
		gbc_scanManagementTabbedPane.gridy = 1;
		add(scanManagementTabbedPane, gbc_scanManagementTabbedPane);

		JPanel scanManagementPanel = new JPanel();
		scanManagementTabbedPane.addTab("Scan Management", null,
				scanManagementPanel, null);
		GridBagLayout gbl_scanManagementPanel = new GridBagLayout();
		gbl_scanManagementPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_scanManagementPanel.rowHeights = new int[] { 0, 0 };
		gbl_scanManagementPanel.columnWeights = new double[] { 0.0, 1.0, 0.1,
				1.0 };
		gbl_scanManagementPanel.rowWeights = new double[] { 0.0, 0.0 };
		scanManagementPanel.setLayout(gbl_scanManagementPanel);

		JLabel lblScanTime = new JLabel("Total Scan Duration");
		GridBagConstraints gbc_lblScanTime = new GridBagConstraints();
		gbc_lblScanTime.insets = new Insets(10, 5, 5, 5);
		gbc_lblScanTime.anchor = GridBagConstraints.WEST;
		gbc_lblScanTime.gridx = 0;
		gbc_lblScanTime.gridy = 0;
		scanManagementPanel.add(lblScanTime, gbc_lblScanTime);

		scanTimeComboBox = new JComboBox<String>();
		scanTimeComboBox.setModel(new DefaultComboBoxModel<String>(
				new String[] { StabilityDurations.TEN_MIN,
						StabilityDurations.THIRTY_MIN,
						StabilityDurations.ONE_HOUR,
						StabilityDurations.THREE_HOURS,
						StabilityDurations.FIVE_HOURS,
						StabilityDurations.TEN_HOURS,
						StabilityDurations.TWENTYFOUR_HOURS,
						StabilityDurations.TWO_DAYS,
						StabilityDurations.THREE_DAYS,
						StabilityDurations.TEN_DAYS }));
		GridBagConstraints gbc_scanTimeComboBox = new GridBagConstraints();
		gbc_scanTimeComboBox.anchor = GridBagConstraints.WEST;
		gbc_scanTimeComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_scanTimeComboBox.gridx = 1;
		gbc_scanTimeComboBox.gridy = 0;
		scanManagementPanel.add(scanTimeComboBox, gbc_scanTimeComboBox);

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(0, 5, 5, 5);
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		scanManagementPanel.add(progressBar, gbc_progressBar);
		scanManagementTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		JButton scanButton = new JButton("START SCANNING");
		scanButton.setActionCommand(ActionCommands.SCAN);
		scanButton.addActionListener(this);
		GridBagConstraints gbc_scanButton = new GridBagConstraints();
		gbc_scanButton.anchor = GridBagConstraints.EAST;
		gbc_scanButton.insets = new Insets(0, 0, 5, 5);
		gbc_scanButton.gridx = 2;
		gbc_scanButton.gridy = 1;
		scanManagementPanel.add(scanButton, gbc_scanButton);

		JButton stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);
		stopButton.setForeground(Color.RED);
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.anchor = GridBagConstraints.WEST;
		gbc_stopButton.insets = new Insets(0, 0, 5, 5);
		gbc_stopButton.gridx = 3;
		gbc_stopButton.gridy = 1;
		scanManagementPanel.add(stopButton, gbc_stopButton);

	}

	protected boolean isAdditionalInformation1Visible() {
		return false;
	}

	protected boolean isAdditionalInformation2Visible() {
		return false;
	}

	protected String getAdditionaInfo1TabName() {
		return "Additional Information 1";
	}

	protected String getAdditionaInfo2TabName() {
		return "Additional Information 2";
	}

	protected String getAdditionalInfo1Name() {
		return "Add Info 1";
	}

	protected String getAdditionalInfo2Name() {
		return "Add Info 2";
	}

	protected String getAdditionalInfo1Format() {
		return "%9.3f";
	}

	protected String getAdditionalInfo2Format() {
		return "%9.3f";
	}

	/*
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 * 
	 * 
	 * Interface IDriverListener
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 */

	public synchronized void signalMeasure(int axis, MeasurePoint point,
			Progress progress, ICommunicationPort port)
			throws CommunicationPortException {
		this.xyDataset.getSeries(this.scanIndex).add(point.getX(),
				point.getMeasure());
		this.xyDatasetAddInfo1.getSeries(this.scanIndex).add(point.getX(),
				point.getAdditionalInformation1());
		this.xyDatasetAddInfo2.getSeries(this.scanIndex).add(point.getX(),
				point.getAdditionalInformation2());

		this.progressBar.setValue(progress.getProgress());

		if (point.getMeasure() > plot.getRangeAxis(0).getUpperBound())
			plot.getRangeAxis(0).setUpperBound(point.getMeasure() * 1.1);

		if (point.getAdditionalInformation1() > plotAI1.getRangeAxis(0)
				.getUpperBound())
			plotAI1.getRangeAxis(0).setUpperBound(
					point.getAdditionalInformation1() * 1.1);

		if (point.getAdditionalInformation2() > plotAI2.getRangeAxis(0)
				.getUpperBound())
			plotAI2.getRangeAxis(0).setUpperBound(point.getMeasure() * 1.1);
	}

	public synchronized void signalXAxisRange(int axis, double startPosition,
			double stopPosition) throws CommunicationPortException {
		plot.getDomainAxis(0).setLowerBound(startPosition);
		plot.getDomainAxis(0).setUpperBound(stopPosition);

		plotAI1.getDomainAxis(0).setLowerBound(startPosition);
		plotAI1.getDomainAxis(0).setUpperBound(stopPosition);

		plotAI2.getDomainAxis(0).setLowerBound(startPosition);
		plotAI2.getDomainAxis(0).setUpperBound(stopPosition);

	}

	public synchronized void signalScanStart() {
		this.isScanActive = true;
	}

	public synchronized void signalScanStop() {
		this.progressBar.setValue(100);

		this.isStopScanActivated = false;
		this.isScanActive = false;
	}

	public synchronized boolean isStopScanActivated(int axis) {
		return this.isStopScanActivated;
	}

	/*
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 * 
	 * 
	 * EventManagement
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 */

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals(ActionCommands.STOP)) {
				this.manageEventStop();
			} else {
				if (!this.isScanActive) {
					if (e.getActionCommand().equals(ActionCommands.SCAN)) {
						this.manageEventScan();
					} else if (e.getActionCommand().equals(
							ActionCommands.DELETE_LAST_SCAN)) {
						this.manageEventDeleteLastScan();
					} else if (e.getActionCommand()
							.equals(ActionCommands.RESET)) {
						this.manageEventReset();
					} else if (e.getActionCommand().equals(
							ActionCommands.SAVE_LAST_SCAN)) {
						this.manageEventSaveLastScan();
					} else if (e.getActionCommand().equals(
							ActionCommands.SAVE_ALL)) {
						this.manageEventSaveAll();
					}
				} else {
					GuiUtilities
							.showErrorPopup(
									"It is impossibile to execute the requested action: SCAN ACTIVE",
									this);
				}
			}
		} catch (Exception exception) {
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventScan()
			throws CommunicationPortException {
		if (GuiUtilities.showConfirmPopup(this.getScanMessage(), this)) {
			this.scanIndex++;
			if (this.scanIndex == 3)
				this.scanIndex = 0;

			this.progressBar.setValue(0);
			this.colorTextField.setBackground(this.getColor(this.scanIndex));
			this.scanProgressiveTextField.setText(Integer
					.toString(this.scanIndex + 1));
			this.xyDataset.getSeries(this.scanIndex).clear();
			this.xyDatasetAddInfo1.getSeries(this.scanIndex).clear();
			this.xyDatasetAddInfo2.getSeries(this.scanIndex).clear();

			this.getScanThread().start();
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventStop()
			throws CommunicationPortException {
		if (this.isScanActive) {
			if (GuiUtilities.showConfirmPopup(
					"Confirm interruption of the current scan?", this))
				this.isStopScanActivated = true;
		} else
			GuiUtilities.showErrorPopup("No active scan!", this);

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventDeleteLastScan() {
		if (GuiUtilities.showConfirmPopup(
				"Confirm deletion of the last scan (data will be lost)?", this)) {
			if (this.scanIndex < 0)
				GuiUtilities.showErrorPopup("No Scan to be deleted", this);
			else {
				this.xyDataset.getSeries(this.scanIndex).clear();
				this.xyDatasetAddInfo1.getSeries(this.scanIndex).clear();
				this.xyDatasetAddInfo2.getSeries(this.scanIndex).clear();
				this.scanIndex--;
				this.colorTextField
						.setBackground(this.getColor(this.scanIndex));
				this.scanProgressiveTextField.setText(Integer
						.toString(this.scanIndex + 1));
			}
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventReset() {
		if (GuiUtilities.showConfirmPopup(
				"Confirm deletion of all the scans (data will be lost)?", this)) {
			if (this.scanIndex < 0)
				GuiUtilities.showErrorPopup("No Scan to be deleted", this);
			else {
				this.xyDataset.getSeries(0).clear();
				this.xyDataset.getSeries(1).clear();
				this.xyDataset.getSeries(2).clear();

				this.xyDatasetAddInfo1.getSeries(0).clear();
				this.xyDatasetAddInfo1.getSeries(1).clear();
				this.xyDatasetAddInfo1.getSeries(2).clear();
				this.xyDatasetAddInfo2.getSeries(0).clear();
				this.xyDatasetAddInfo2.getSeries(1).clear();
				this.xyDatasetAddInfo2.getSeries(2).clear();

				this.scanIndex = -1;
				this.colorTextField
						.setBackground(this.getColor(this.scanIndex));
				this.scanProgressiveTextField.setText(Integer
						.toString(this.scanIndex + 1));
			}
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventSaveLastScan() throws IOException {
		String fileName = GuiUtilities.showDatFileChooser(this, "");

		try {
			StringUtilities.checkString(fileName, "");

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try {
				XYSeries series = this.xyDataset.getSeries(this.scanIndex);
				XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1
						.getSeries(this.scanIndex);
				XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2
						.getSeries(this.scanIndex);

				this.writeDataFileHeading(series, writer);
				writer.newLine();

				int nItems = series.getItemCount();

				for (int index = 0; index < nItems; index++) {
					writer.write(GuiUtilities.parseDouble(
							series.getX(index).doubleValue()).trim()
							+ " "
							+ series.getY(index).intValue()
							+ " "
							+ String.format(getAdditionalInfo1Format(),
									seriesAddInfo1.getY(index).doubleValue())
							+ " "
							+ String.format(getAdditionalInfo2Format(),
									seriesAddInfo2.getY(index).doubleValue()));
					writer.newLine();
				}

				writer.flush();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} finally {
				writer.close();
			}
		} catch (IllegalArgumentException e) {

		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventSaveAll() throws IOException {
		String fileName = GuiUtilities.showDatFileChooser(this, "");

		try {
			StringUtilities.checkString(fileName, "");

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try {

				for (int seriesIndex = 0; seriesIndex < 3; seriesIndex++) {
					XYSeries series = this.xyDataset.getSeries(seriesIndex);
					XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1
							.getSeries(seriesIndex);
					XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2
							.getSeries(seriesIndex);

					this.writeDataFileHeading(series, writer);
					writer.newLine();

					int nItems = series.getItemCount();

					for (int index = 0; index < nItems; index++) {
						writer.write(GuiUtilities.parseDouble(
								series.getX(index).doubleValue()).trim()
								+ " "
								+ series.getY(index).intValue()
								+ " "
								+ String.format(getAdditionalInfo1Format(),
										seriesAddInfo1.getY(index)
												.doubleValue())
								+ " "
								+ String.format(getAdditionalInfo2Format(),
										seriesAddInfo2.getY(index)
												.doubleValue()));
						writer.newLine();
					}

					writer.newLine();
				}

				writer.flush();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} finally {
				writer.close();
			}
		} catch (IllegalArgumentException e) {

		}
	}

	/*
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 * 
	 * 
	 * Private Methods
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 */

	private Color getColor(short scanIndex) {
		switch (scanIndex) {
		case 0:
			return Color.RED;
		case 1:
			return Color.GREEN;
		case 2:
			return Color.CYAN;
		default:
			return Color.BLACK;
		}
	}

	// ----------------------------------------------------------------------------

	private String getScanMessage() {
		String message = "Confirm activation of the scan, with the following parameters:\n\n";

		message += "DURATION: " + this.getScanTimeComboBox().getSelectedItem();

		return message;
	}

	// ----------------------------------------------------------------------------

	private void writeDataFileHeading(XYSeries series, BufferedWriter writer)
			throws IOException {
		writer.write("!DURATION: " + this.getScanTimeComboBox().getSelectedItem());
		writer.newLine();
		writer.write("!---------------------------------------------------------------------------------");
		writer.newLine();

	}

	/*
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 * 
	 * 
	 * Inner Classes
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * -- ----------------------------------------------------------
	 */

	protected Thread getScanThread() {
		return new ScanThread(this);
	}

	public JComboBox<String> getScanTimeComboBox()
  {
	  return scanTimeComboBox;
  }

	public ICommunicationPort getPort()
  {
	  return port;
  }


	public class ScanThread extends Thread {
		protected TubeStabilityPanel panel;

		public ScanThread(TubeStabilityPanel panel) {
			this.panel = panel;
		}

		public void run() {
			try {
				StabilityParameters scanParameters = new StabilityParameters(
						STABILILTY_FAKE_AXIS, ListenerRegister.getInstance());
				scanParameters.setScanDuration((String) panel.getScanTimeComboBox()
						.getSelectedItem());
				
				this.addCustomParameters(scanParameters);
				
				ProgramsFacade.executeProgram(getScanProgramName(),
						scanParameters, panel.getPort());
			} catch (Exception exception) {
				GuiUtilities.showErrorPopup(exception.getMessage(), this.panel);
			}
		}

		protected void addCustomParameters(StabilityParameters scanParameters)
		{
		}
		
		protected String getScanProgramName() {
			return ProgramsFacade.Programs.STABILITY;
		}
	}

}
