package com.elettra.controller.gui.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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

import com.elettra.common.fit.FitUtilities;
import com.elettra.common.fit.GaussianFitResult;
import com.elettra.common.fit.LinearFitResult;
import com.elettra.common.fit.MiddlePointResult;
import com.elettra.common.fit.PseudoVoigtFitParameters;
import com.elettra.common.fit.PseudoVoigtFitResult;
import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.driver.programs.DoubleMoveParameters;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ScanParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.InputFieldsException;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MeasureListener;
import com.elettra.controller.gui.fit.PlottableFitResult;

public class ScanPanel extends MeasureListener implements ActionListener
{
	static class ActionCommands
	{
		private static final String EXECUTE_LINEAR_FIT          = "EXECUTE_LINEAR_FIT";
		private static final String SCAN                        = "SCAN";
		private static final String STOP                        = "STOP";
		private static final String VALUE                       = "VALUE";
		private static final String STEPS                       = "STEPS";
		private static final String DELETE_LAST_SCAN            = "DELETE_LAST_SCAN";
		private static final String RESET                       = "RESET";
		private static final String SAVE_LAST_SCAN              = "SAVE_LAST_SCAN";
		private static final String SAVE_ALL                    = "SAVE_ALL";
		private static final String LOAD                        = "LOAD";
		private static final String GAUSSIAN_FIT_PEAK           = "GAUSSIAN_FIT_PEAK";
		private static final String GAUSSIAN_MOVE_AXIS          = "GAUSSIAN_MOVE_AXIS";
		private static final String GAUSSIAN_SEND_DATA          = "GAUSSIAN_SEND_DATA";
		private static final String PSEUDOVOIGT_FIT_PEAK        = "PSEUDOVOIGT_FIT_PEAK";
		private static final String PSEUDOVOIGT_MOVE_AXIS       = "PSEUDOVOIGT_MOVE_AXIS";
		private static final String PSEUDOVOIGT_INIT_PARAMETERS = "PSEUDOVOIGT_INIT_PARAMETERS";
		private static final String PSEUDOVOIGT_SEND_DATA       = "PSEUDOVOIGT_SEND_DATA";
		private static final String MIDDLE_POINT_FIND           = "MIDDLE_POINT_FIND";
		private static final String MIDDLE_POINT_MOVE_AXIS      = "MIDDLE_POINT_MOVE_AXIS";
	}

	private int                axis;
	private ICommunicationPort port;

	/**
	 * 
	 */
	private static final long  serialVersionUID = -8475330198111414161L;
	private JTextField         scanProgressiveTextField;
	private JTextField         colorTextField;
	private JTextField         stopTextField;
	private JTextField         startTextField;
	private JTextField         stepValueTextField;
	private JTextField         stepsTextField;
	private JTextField         gaussianFitFwhmTextField;
	private JTextField         gaussianFitPeakHeightTextField;
	private JTextField         gaussianFitPeakPositionTextField;
	private JTextField         gaussianFitFromTextField;
	private JTextField         gaussianFitToTextField;
	private XYSeriesCollection xyDataset;
	private XYSeriesCollection xyDatasetAddInfo1;
	private boolean            isStopScanActivated;

	private short              scanIndex        = -1;
	private JProgressBar       progressBar;
	private boolean            isScanActive     = false;
	private JComboBox<String>  kindOfMovementComboBox;
	private JComboBox<String>  startSignComboBox;
	private JComboBox<String>  stopSignComboBox;
	private JComboBox<String>  scanTimeComboBox;
	private JComboBox<String>  gaussianFitFromSignComboBox;
	private JComboBox<String>  gaussianFitToSignComboBox;
	private XYPlot             plot;
	private XYPlot             plotAI1;
	private XYPlot             plotAI2;
	private JTextField         referenceAxisTextField;
	private JTextField         axisName;
	private JTextField         measureUnitTextField;
	private AxisConfiguration  axisConfiguration;
	private JButton            gaussianFitSendDataButton;
	private JButton            gaussianFitMoveAxisButton;
	private JButton            gaussianFitFitPeakButton;
	private JTextField         pseudoVoigtFitChi2OutTextField;
	private JTextField         pseudoVoigtFitCenterOutTextField;
	private JTextField         pseudoVoigtFitAmplitudeOutTextField;
	private JTextField         pseudoVoigtFitOffsetOutTextField;
	private JTextField         pseudoVoigtFitFwhmOutTextField;
	private JTextField         pseudoVoigtFitShapeTextField;
	private JTextField         pseudoVoigtFitOffsetTextField;
	private JTextField         pseudoVoigtFitAmplitudeTextField;
	private JTextField         pseudoVoigtFitFwhmTextField;
	private JTextField         pseudoVoigtFitFromTextField;
	private JTextField         pseudoVoigtFitCenterTextField;
	private JTextField         pseudoVoigtFitToTextField;
	private JComboBox<String>  pseudoVoigtFitToSignComboBox;
	private JComboBox<String>  pseudoVoigtFitFromSignComboBox;
	private JButton            pseudoVoigtFitSendDataButton;
	private JButton            pseudoVoigtFitMoveAxisButton;
	private JButton            pseudoVoigtFitFitPeakButton;
	private JButton            pseudoVoigtFitInitParametersButton;
	private JTextField         pseudoVoigtFitRmsOutTextField;
	private JTextField         gaussianFitChi2TextField;
	private JTextField         gaussianFitRmsTextField;
	private JTextField         gaussianFitOffsetTextField;
	private JTextField         linearFitInterceptTextField;
	private JTextField         linearFitFromTextField;
	private JTextField         linearFitSlopeTextField;
	private JTextField         linearFitToTextField;
	private JButton            linearFitExecuteButton;
	private JComboBox<String>  linearFitToSignComboBox;
	private JComboBox<String>  linearFitFromSignComboBox;
	private JTextField         linearFitSlopeErrorTextField;
	private JTextField         linearFitInterceptErrorTextField;
	private JTextField         linearFitRSquareTextField;
	private JButton            middlePointMoveAxisButton;
	private JTextField         middlePointPositionTextField;
	private JTextField         middlePointSignalHeigtTextField;
	private JTextField         middlePointDownFromTextField;
	private JTextField         middlePointDownToTextField;
	private JComboBox<String>  middlePointDownFromSignComboBox;
	private JComboBox<String>  middlePointDownToSignComboBox;
	private JTextField         middlePointPlateauFromTextField;
	private JTextField         middlePointPlateauToTextField;
	private JComboBox<String>  middlePointPlateauFromSignComboBox;
	private JComboBox<String>  middlePointPlateauToSignComboBox;
	private JButton            middlePointFindMiddlePointButton;

	private int                sendDataAxis1;
	private int                sendDataAxis2;
	private boolean            sendDataEnabled;
	private JButton            loadButton;
	private XYSeriesCollection xyDatasetAddInfo2;

	/**
	 * Create the panel.
	 * 
	 */
	public ScanPanel(int axis, ICommunicationPort port, boolean sendDataEnabled) throws CommunicationPortException
	{
		this(axis, port, sendDataEnabled, -1, -1);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public ScanPanel(int axis, ICommunicationPort port, boolean sendDataEnabled, int sendDataAxis1, int sendDataAxis2) throws CommunicationPortException
	{
		this.axis = axis;
		this.port = port;
		this.sendDataAxis1 = sendDataAxis1;
		this.sendDataAxis2 = sendDataAxis2;
		this.sendDataEnabled = sendDataEnabled;

		ListenerRegister.getInstance().addListener(this.axis, this);

		axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 2, 58, 15, 25, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JPanel panelTop = new JPanel();
		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		gbc_panelTop.anchor = GridBagConstraints.EAST;
		gbc_panelTop.fill = GridBagConstraints.VERTICAL;
		gbc_panelTop.insets = new Insets(0, 0, 5, 0);
		gbc_panelTop.gridx = 0;
		gbc_panelTop.gridy = 0;
		add(panelTop, gbc_panelTop);
		GridBagLayout gbl_panelTop = new GridBagLayout();
		gbl_panelTop.columnWidths = new int[] { 150, 90, 0, 0, 0, 0 };
		gbl_panelTop.rowHeights = new int[] { 0 };
		gbl_panelTop.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panelTop.rowWeights = new double[] { 0.0 };
		panelTop.setLayout(gbl_panelTop);

		JLabel lblAxis = new JLabel(axisConfiguration.isMultiple() ? "Multiple Axis" : "Axis");
		GridBagConstraints gbc_lblAxis = new GridBagConstraints();
		gbc_lblAxis.insets = new Insets(0, 0, 0, 5);
		gbc_lblAxis.anchor = GridBagConstraints.EAST;
		gbc_lblAxis.gridx = 0;
		gbc_lblAxis.gridy = 0;
		panelTop.add(lblAxis, gbc_lblAxis);

		axisName = new JTextField();
		axisName.setHorizontalAlignment(SwingConstants.CENTER);
		axisName.setFont(new Font("Tahoma", Font.BOLD, 11));
		axisName.setForeground(new Color(255, 255, 255));
		axisName.setBackground(new Color(51, 102, 0));
		GridBagConstraints gbc_axisName = new GridBagConstraints();
		gbc_axisName.insets = new Insets(0, 0, 0, 5);
		gbc_axisName.anchor = GridBagConstraints.WEST;
		gbc_axisName.gridx = 1;
		gbc_axisName.gridy = 0;
		panelTop.add(axisName, gbc_axisName);
		axisName.setColumns(10);
		axisName.setText(axisConfiguration.getName());

		JLabel lblNewLabel = new JLabel("U.M.");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		panelTop.add(lblNewLabel, gbc_lblNewLabel);

		measureUnitTextField = new JTextField();
		measureUnitTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		measureUnitTextField.setForeground(new Color(255, 255, 255));
		measureUnitTextField.setBackground(new Color(51, 102, 0));
		measureUnitTextField.setEditable(false);
		GridBagConstraints gbc_measureUnitTextField = new GridBagConstraints();
		gbc_measureUnitTextField.anchor = GridBagConstraints.EAST;
		gbc_measureUnitTextField.gridx = 3;
		gbc_measureUnitTextField.gridy = 0;
		panelTop.add(measureUnitTextField, gbc_measureUnitTextField);
		measureUnitTextField.setColumns(3);
		measureUnitTextField.setText(axisConfiguration.getMeasureUnit().toString());

		if (axisConfiguration.isMultiple())
		{
			JLabel lblRefAxis = new JLabel("Reference Axis");
			GridBagConstraints gbc_lblRefAxis = new GridBagConstraints();
			gbc_lblRefAxis.insets = new Insets(0, 5, 0, 5);
			gbc_lblRefAxis.anchor = GridBagConstraints.EAST;
			gbc_lblRefAxis.gridx = 4;
			gbc_lblRefAxis.gridy = 0;
			panelTop.add(lblRefAxis, gbc_lblRefAxis);

			AxisConfiguration axis1Configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axisConfiguration.getMultipleAxis().getAxis1());
			AxisConfiguration axis2Configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axisConfiguration.getMultipleAxis().getAxis2());

			referenceAxisTextField = new JTextField();
			referenceAxisTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
			referenceAxisTextField.setForeground(new Color(255, 255, 255));
			referenceAxisTextField.setBackground(new Color(51, 102, 0));
			referenceAxisTextField.setEditable(false);

			switch (axisConfiguration.getMultipleAxis().getDefaultReferenceAxis())
			{
				case 1:
					referenceAxisTextField.setText(axis1Configuration.getName());
					break;
				case 2:
					referenceAxisTextField.setText(axis2Configuration.getName());
					break;
			}

			GridBagConstraints gbc_referenceAxisComboBox = new GridBagConstraints();
			gbc_referenceAxisComboBox.insets = new Insets(0, 0, 0, 5);
			gbc_referenceAxisComboBox.fill = GridBagConstraints.NONE;
			gbc_referenceAxisComboBox.anchor = GridBagConstraints.EAST;
			gbc_referenceAxisComboBox.gridx = 5;
			gbc_referenceAxisComboBox.gridy = 0;
			panelTop.add(referenceAxisTextField, gbc_referenceAxisComboBox);
		}

		JTabbedPane scanGraphTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanGraphTabbedPane = new GridBagConstraints();
		gbc_scanGraphTabbedPane.insets = new Insets(-20, 0, 5, 0);
		gbc_scanGraphTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_scanGraphTabbedPane.gridx = 0;
		gbc_scanGraphTabbedPane.gridy = 1;
		add(scanGraphTabbedPane, gbc_scanGraphTabbedPane);

		JPanel scanGraphPanel = new JPanel();
		scanGraphPanel.setForeground(new Color(0, 0, 0));
		scanGraphTabbedPane.addTab("Scan Graphs", null, scanGraphPanel, null);
		GridBagLayout gbl_scanGraphPanel = new GridBagLayout();
		gbl_scanGraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_scanGraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_scanGraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_scanGraphPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		scanGraphPanel.setLayout(gbl_scanGraphPanel);

		JPanel scanInnerPanel = new JPanel();
		scanInnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_scanInnerPanel = new GridBagConstraints();
		gbc_scanInnerPanel.gridwidth = 8;
		gbc_scanInnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_scanInnerPanel.fill = GridBagConstraints.BOTH;
		gbc_scanInnerPanel.gridx = 0;
		gbc_scanInnerPanel.gridy = 0;
		scanGraphPanel.add(scanInnerPanel, gbc_scanInnerPanel);

		XYSeries series1 = new XYSeries("Scan 1");
		XYSeries series2 = new XYSeries("Scan 2");
		XYSeries series3 = new XYSeries("Scan 3");
		XYSeries fit = new XYSeries("Fit");
		XYSeries middlePoint = new XYSeries("MP");

		xyDataset = new XYSeriesCollection();
		xyDataset.addSeries(series1);
		xyDataset.addSeries(series2);
		xyDataset.addSeries(series3);
		xyDataset.addSeries(fit);
		xyDataset.addSeries(middlePoint);

		JFreeChart scanGraphs = ChartFactory.createXYLineChart(null, "Position (" + axisConfiguration.getMeasureUnit().toString() + ")", this.getOrdinateLabel(), xyDataset, PlotOrientation.VERTICAL, false, true, false);

		plot = scanGraphs.getXYPlot();

		if (this.isOrdinateInteger())
			plot.getRangeAxis(0).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		else
			plot.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());

		plot.getRangeAxis(0).setAutoRange(false);
		plot.getRangeAxis(0).setLowerBound(0);
		plot.getRangeAxis(0).setUpperBound(10);

		plot.getDomainAxis(0).setAutoRange(false);
		plot.getDomainAxis(0).setLowerBound(-10);
		plot.getDomainAxis(0).setUpperBound(10);

		plot.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) plot.getRenderer();
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
		xylineandshaperenderer.setSeriesShape(3, shape);
		xylineandshaperenderer.setSeriesPaint(3, Color.WHITE);
		xylineandshaperenderer.setSeriesFillPaint(3, Color.WHITE);
		xylineandshaperenderer.setSeriesShape(4, new Ellipse2D.Double(-8D, -8D, 8D, 8D));
		xylineandshaperenderer.setSeriesPaint(4, Color.WHITE);
		xylineandshaperenderer.setSeriesFillPaint(4, Color.WHITE);

		scanGraphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		scanInnerPanel.setLayout(new FlowLayout());
		scanInnerPanel.add(new ChartPanel(scanGraphs, 700, 500, 700, 500, 700, 500, false, false, false, false, false, false));

		JLabel lblPlotColor = new JLabel("Plot Color");
		GridBagConstraints gbc_lblPlotColor = new GridBagConstraints();
		gbc_lblPlotColor.insets = new Insets(0, 0, 0, 5);
		gbc_lblPlotColor.anchor = GridBagConstraints.EAST;
		gbc_lblPlotColor.gridx = 0;
		gbc_lblPlotColor.gridy = 1;
		scanGraphPanel.add(lblPlotColor, gbc_lblPlotColor);

		colorTextField = new JTextField();
		colorTextField.setEditable(false);
		colorTextField.setBackground(Color.BLACK);
		GridBagConstraints gbc_colorTextField = new GridBagConstraints();
		gbc_colorTextField.insets = new Insets(0, 0, 0, 5);
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
		gbc_scanProgressiveTextField.insets = new Insets(0, 0, 0, 5);
		gbc_scanProgressiveTextField.gridx = 2;
		gbc_scanProgressiveTextField.gridy = 1;
		scanGraphPanel.add(scanProgressiveTextField, gbc_scanProgressiveTextField);
		scanProgressiveTextField.setColumns(1);

		JButton deleteLastScanButton = new JButton("DELETE LAST SCAN");
		deleteLastScanButton.setActionCommand(ActionCommands.DELETE_LAST_SCAN);
		deleteLastScanButton.addActionListener(this);
		GridBagConstraints gbc_deleteLastScanButton = new GridBagConstraints();
		gbc_deleteLastScanButton.insets = new Insets(0, 0, 0, 5);
		gbc_deleteLastScanButton.gridx = 3;
		gbc_deleteLastScanButton.gridy = 1;
		scanGraphPanel.add(deleteLastScanButton, gbc_deleteLastScanButton);

		JButton resetButton = new JButton(ActionCommands.RESET);
		resetButton.setActionCommand(ActionCommands.RESET);
		resetButton.addActionListener(this);
		GridBagConstraints gbc_resetButton = new GridBagConstraints();
		gbc_resetButton.insets = new Insets(0, 0, 0, 15);
		gbc_resetButton.gridx = 4;
		gbc_resetButton.gridy = 1;
		scanGraphPanel.add(resetButton, gbc_resetButton);

		JButton saveLastScanButton = new JButton("SAVE LAST SCAN");
		saveLastScanButton.setActionCommand(ActionCommands.SAVE_LAST_SCAN);
		saveLastScanButton.addActionListener(this);

		loadButton = new JButton("LOAD SCAN");
		loadButton.setActionCommand(ActionCommands.LOAD);
		loadButton.addActionListener(this);

		GridBagConstraints gbc_loadButton = new GridBagConstraints();
		gbc_loadButton.insets = new Insets(0, 0, 0, 5);
		gbc_loadButton.gridx = 5;
		gbc_loadButton.gridy = 1;
		scanGraphPanel.add(loadButton, gbc_loadButton);
		GridBagConstraints gbc_saveLastScanButton = new GridBagConstraints();
		gbc_saveLastScanButton.insets = new Insets(0, 0, 0, 5);
		gbc_saveLastScanButton.gridx = 6;
		gbc_saveLastScanButton.gridy = 1;
		scanGraphPanel.add(saveLastScanButton, gbc_saveLastScanButton);

		JButton saveAllButton = new JButton("SAVE ALL");
		saveAllButton.setActionCommand(ActionCommands.SAVE_ALL);
		saveAllButton.addActionListener(this);
		GridBagConstraints gbc_saveAllButton = new GridBagConstraints();
		gbc_saveAllButton.insets = new Insets(0, 0, 0, 5);
		gbc_saveAllButton.gridx = 7;
		gbc_saveAllButton.gridy = 1;
		scanGraphPanel.add(saveAllButton, gbc_saveAllButton);
		scanGraphTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		// ---------------------------------------------------------------------------------------------------
		// Additional Informations
		// ---------------------------------------------------------------------------------------------------

		JPanel addInfo1GraphPanel = new JPanel();

		addInfo1GraphPanel.setForeground(new Color(0, 0, 0));

		if (this.isAdditionalInformation1Visible())
		{
			scanGraphTabbedPane.addTab(getAdditionaInfo1TabName(), null, addInfo1GraphPanel, null);
			scanGraphTabbedPane.setForegroundAt(1, new Color(0, 102, 0));
		}

		GridBagLayout gbl_addInfo1GraphPanel = new GridBagLayout();
		gbl_addInfo1GraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_addInfo1GraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_addInfo1GraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_addInfo1GraphPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		addInfo1GraphPanel.setLayout(gbl_addInfo1GraphPanel);

		JPanel addInfo1InnerPanel = new JPanel();
		addInfo1InnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
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

		JFreeChart addInfo1Graphs = ChartFactory.createXYLineChart(null, "Position (" + axisConfiguration.getMeasureUnit().toString() + ")", getAdditionalInfo1Name(), xyDatasetAddInfo1, PlotOrientation.VERTICAL, false, true, false);

		plotAI1 = addInfo1Graphs.getXYPlot();

		plotAI1.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		plotAI1.getRangeAxis(0).setAutoRange(false);
		plotAI1.getRangeAxis(0).setLowerBound(0);
		plotAI1.getRangeAxis(0).setUpperBound(10);

		plotAI1.getDomainAxis(0).setAutoRange(false);
		plotAI1.getDomainAxis(0).setLowerBound(-10);
		plotAI1.getDomainAxis(0).setUpperBound(10);

		plotAI1.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape1 = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer1 = (XYLineAndShapeRenderer) plotAI1.getRenderer();
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

		addInfo1Graphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		addInfo1InnerPanel.setLayout(new FlowLayout());
		addInfo1InnerPanel.add(new ChartPanel(addInfo1Graphs, 700, 500, 700, 500, 700, 500, false, false, false, false, false, false));

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

		if (this.isAdditionalInformation2Visible())
		{
			scanGraphTabbedPane.addTab(getAdditionaInfo2TabName(), null, addInfo2GraphPanel, null);
			scanGraphTabbedPane.setForegroundAt(2, new Color(0, 102, 51));
			scanGraphTabbedPane.setForegroundAt(1, new Color(0, 102, 0));
		}

		GridBagLayout gbl_addInfo2GraphPanel = new GridBagLayout();
		gbl_addInfo2GraphPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_addInfo2GraphPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_addInfo2GraphPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_addInfo2GraphPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		addInfo2GraphPanel.setLayout(gbl_addInfo2GraphPanel);

		JPanel addInfo2InnerPanel = new JPanel();
		addInfo2InnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
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

		JFreeChart addInfo2Graphs = ChartFactory.createXYLineChart(null, "Position (" + axisConfiguration.getMeasureUnit().toString() + ")", getAdditionalInfo2Name(), xyDatasetAddInfo2, PlotOrientation.VERTICAL, false, true, false);

		plotAI2 = addInfo2Graphs.getXYPlot();

		plotAI2.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		plotAI2.getRangeAxis(0).setAutoRange(false);
		plotAI2.getRangeAxis(0).setLowerBound(0);
		plotAI2.getRangeAxis(0).setUpperBound(10);

		plotAI2.getDomainAxis(0).setAutoRange(false);
		plotAI2.getDomainAxis(0).setLowerBound(-10);
		plotAI2.getDomainAxis(0).setUpperBound(10);

		plotAI2.setBackgroundPaint(Color.BLACK);
		Ellipse2D.Double shape2 = new Ellipse2D.Double(-2D, -2D, 4D, 4D);
		XYLineAndShapeRenderer xylineandshaperenderer2 = (XYLineAndShapeRenderer) plotAI2.getRenderer();
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

		addInfo2Graphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		addInfo2InnerPanel.setLayout(new FlowLayout());
		addInfo2InnerPanel.add(new ChartPanel(addInfo2Graphs, 700, 500, 700, 500, 700, 500, false, false, false, false, false, false));

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
		gbc_scanManagementTabbedPane.gridy = 2;
		add(scanManagementTabbedPane, gbc_scanManagementTabbedPane);

		JPanel scanManagementPanel = new JPanel();
		scanManagementTabbedPane.addTab("Scan Management", null, scanManagementPanel, null);
		GridBagLayout gbl_scanManagementPanel = new GridBagLayout();
		gbl_scanManagementPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_scanManagementPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_scanManagementPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_scanManagementPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		scanManagementPanel.setLayout(gbl_scanManagementPanel);

		JLabel lblAbsrel = new JLabel("Abs/Rel");
		GridBagConstraints gbc_lblAbsrel = new GridBagConstraints();
		gbc_lblAbsrel.anchor = GridBagConstraints.EAST;
		gbc_lblAbsrel.insets = new Insets(10, 5, 5, 5);
		gbc_lblAbsrel.gridx = 0;
		gbc_lblAbsrel.gridy = 0;
		scanManagementPanel.add(lblAbsrel, gbc_lblAbsrel);

		kindOfMovementComboBox = new JComboBox<String>();
		kindOfMovementComboBox.setMaximumRowCount(2);
		kindOfMovementComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "A", "R" }));
		GridBagConstraints gbc_kindOfMovementComboBox = new GridBagConstraints();
		gbc_kindOfMovementComboBox.anchor = GridBagConstraints.WEST;
		gbc_kindOfMovementComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_kindOfMovementComboBox.gridx = 1;
		gbc_kindOfMovementComboBox.gridy = 0;
		scanManagementPanel.add(kindOfMovementComboBox, gbc_kindOfMovementComboBox);

		JLabel lblStart = new JLabel("Start");
		GridBagConstraints gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.insets = new Insets(10, 0, 5, 5);
		gbc_lblStart.anchor = GridBagConstraints.EAST;
		gbc_lblStart.gridx = 2;
		gbc_lblStart.gridy = 0;
		scanManagementPanel.add(lblStart, gbc_lblStart);

		startSignComboBox = new JComboBox<String>();
		startSignComboBox.setMaximumRowCount(2);
		startSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		GridBagConstraints gbc_startSignComboBox = new GridBagConstraints();
		gbc_startSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_startSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_startSignComboBox.gridx = 3;
		gbc_startSignComboBox.gridy = 0;
		scanManagementPanel.add(startSignComboBox, gbc_startSignComboBox);

		startTextField = new JTextField();
		startTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					startTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(startTextField.getText()))));
				}
				catch (Throwable t)
				{
					startTextField.setText("<not a number>");
				}
			}
		});
		startTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					startTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(startTextField.getText()))));
				}
				catch (Throwable t)
				{
					startTextField.setText("<not a number>");
				}
			}
		});

		startTextField.setText(DriverUtilities.formatControllerPosition(0));
		startTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_startTextField = new GridBagConstraints();
		gbc_startTextField.anchor = GridBagConstraints.WEST;
		gbc_startTextField.insets = new Insets(10, 0, 5, 5);
		gbc_startTextField.gridx = 4;
		gbc_startTextField.gridy = 0;
		scanManagementPanel.add(startTextField, gbc_startTextField);
		startTextField.setColumns(10);

		JLabel lblStop = new JLabel("Stop");
		GridBagConstraints gbc_lblStop = new GridBagConstraints();
		gbc_lblStop.insets = new Insets(10, 0, 5, 5);
		gbc_lblStop.anchor = GridBagConstraints.EAST;
		gbc_lblStop.gridx = 5;
		gbc_lblStop.gridy = 0;
		scanManagementPanel.add(lblStop, gbc_lblStop);

		stopSignComboBox = new JComboBox<String>();
		stopSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		stopSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_stopSignComboBox = new GridBagConstraints();
		gbc_stopSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_stopSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_stopSignComboBox.gridx = 6;
		gbc_stopSignComboBox.gridy = 0;
		scanManagementPanel.add(stopSignComboBox, gbc_stopSignComboBox);

		stopTextField = new JTextField();
		stopTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		stopTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					stopTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(stopTextField.getText()))));
				}
				catch (Throwable t)
				{
					stopTextField.setText("<not a number>");
				}
			}
		});
		stopTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					stopTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(stopTextField.getText()))));
				}
				catch (Throwable t)
				{
					stopTextField.setText("<not a number>");
				}
			}
		});
		stopTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_stopTextField = new GridBagConstraints();
		gbc_stopTextField.anchor = GridBagConstraints.WEST;
		gbc_stopTextField.insets = new Insets(10, 0, 5, 5);
		gbc_stopTextField.gridx = 7;
		gbc_stopTextField.gridy = 0;
		scanManagementPanel.add(stopTextField, gbc_stopTextField);
		stopTextField.setColumns(10);

		JLabel lblScanTime = new JLabel("Scan Time");
		GridBagConstraints gbc_lblScanTime = new GridBagConstraints();
		gbc_lblScanTime.insets = new Insets(10, 0, 5, 5);
		gbc_lblScanTime.anchor = GridBagConstraints.EAST;
		gbc_lblScanTime.gridx = 8;
		gbc_lblScanTime.gridy = 0;
		scanManagementPanel.add(lblScanTime, gbc_lblScanTime);

		scanTimeComboBox = new JComboBox<String>();
		scanTimeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "0.1 s", "0.2 s", "0.5 s", "1 s", "2 s", "5 s", "10 s", "20 s", "30 s", "40 s", "50 s", "60 s", "100 s", "200 s", "500 s" }));
		GridBagConstraints gbc_scanTimeComboBox = new GridBagConstraints();
		gbc_scanTimeComboBox.anchor = GridBagConstraints.WEST;
		gbc_scanTimeComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_scanTimeComboBox.gridx = 9;
		gbc_scanTimeComboBox.gridy = 0;
		scanManagementPanel.add(scanTimeComboBox, gbc_scanTimeComboBox);

		JLabel lblSteps = new JLabel("Steps");
		GridBagConstraints gbc_lblSteps = new GridBagConstraints();
		gbc_lblSteps.insets = new Insets(0, 0, 5, 5);
		gbc_lblSteps.anchor = GridBagConstraints.EAST;
		gbc_lblSteps.gridx = 0;
		gbc_lblSteps.gridy = 1;
		scanManagementPanel.add(lblSteps, gbc_lblSteps);

		stepsTextField = new JTextField();
		stepsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		stepsTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					stepsTextField.setText(Integer.toString(Math.abs(Integer.parseInt(stepsTextField.getText()))));
				}
				catch (Throwable t)
				{
					stepsTextField.setText("<not a number>");
				}
			}
		});
		stepsTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					stepsTextField.setText(Integer.toString(Math.abs(Integer.parseInt(stepsTextField.getText()))));
				}
				catch (Throwable t)
				{
					stepsTextField.setText("<not a number>");
				}
			}
		});
		stepsTextField.setText("0");
		GridBagConstraints gbc_stepsTextField = new GridBagConstraints();
		gbc_stepsTextField.insets = new Insets(0, 0, 5, 5);
		gbc_stepsTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_stepsTextField.gridx = 1;
		gbc_stepsTextField.gridy = 1;
		scanManagementPanel.add(stepsTextField, gbc_stepsTextField);
		stepsTextField.setColumns(4);

		JButton valueButton = new JButton(ActionCommands.VALUE);
		valueButton.setActionCommand(ActionCommands.VALUE);
		valueButton.addActionListener(this);
		GridBagConstraints gbc_valueButton = new GridBagConstraints();
		gbc_valueButton.insets = new Insets(0, 0, 5, 5);
		gbc_valueButton.gridx = 2;
		gbc_valueButton.gridy = 1;
		scanManagementPanel.add(valueButton, gbc_valueButton);

		JLabel lblStepValue = new JLabel("Step Value");
		GridBagConstraints gbc_lblStepValue = new GridBagConstraints();
		gbc_lblStepValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblStepValue.anchor = GridBagConstraints.EAST;
		gbc_lblStepValue.gridx = 3;
		gbc_lblStepValue.gridy = 1;
		scanManagementPanel.add(lblStepValue, gbc_lblStepValue);

		stepValueTextField = new JTextField();
		stepValueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		stepValueTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					stepValueTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(stepValueTextField.getText()))));
				}
				catch (Throwable t)
				{
					stepValueTextField.setText("<not a number>");
				}
			}
		});
		stopTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					stepValueTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(stepValueTextField.getText()))));
				}
				catch (Throwable t)
				{
					stepValueTextField.setText("<not a number>");
				}
			}
		});
		stepValueTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_stepValueTextField = new GridBagConstraints();
		gbc_stepValueTextField.anchor = GridBagConstraints.WEST;
		gbc_stepValueTextField.insets = new Insets(0, 0, 5, 5);
		gbc_stepValueTextField.gridx = 4;
		gbc_stepValueTextField.gridy = 1;
		scanManagementPanel.add(stepValueTextField, gbc_stepValueTextField);
		stepValueTextField.setColumns(10);

		JButton stepsButton = new JButton(ActionCommands.STEPS);
		stepsButton.setActionCommand(ActionCommands.STEPS);
		stepsButton.addActionListener(this);
		GridBagConstraints gbc_stepsButton = new GridBagConstraints();
		gbc_stepsButton.anchor = GridBagConstraints.WEST;
		gbc_stepsButton.insets = new Insets(0, 0, 5, 5);
		gbc_stepsButton.gridx = 5;
		gbc_stepsButton.gridy = 1;
		scanManagementPanel.add(stepsButton, gbc_stepsButton);

		JButton scanButton = new JButton(ActionCommands.SCAN);
		scanButton.setActionCommand(ActionCommands.SCAN);
		scanButton.addActionListener(this);
		GridBagConstraints gbc_scanButton = new GridBagConstraints();
		gbc_scanButton.anchor = GridBagConstraints.EAST;
		gbc_scanButton.insets = new Insets(0, 0, 5, 5);
		gbc_scanButton.gridx = 6;
		gbc_scanButton.gridy = 1;
		scanManagementPanel.add(scanButton, gbc_scanButton);

		JButton stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);
		stopButton.setForeground(Color.RED);
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.anchor = GridBagConstraints.WEST;
		gbc_stopButton.insets = new Insets(0, 0, 5, 5);
		gbc_stopButton.gridx = 7;
		gbc_stopButton.gridy = 1;
		scanManagementPanel.add(stopButton, gbc_stopButton);

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.anchor = GridBagConstraints.WEST;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.gridx = 8;
		gbc_progressBar.gridy = 1;
		scanManagementPanel.add(progressBar, gbc_progressBar);
		scanManagementTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		JTabbedPane fitTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_fitTabbedPane = new GridBagConstraints();
		gbc_fitTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_fitTabbedPane.gridx = 0;
		gbc_fitTabbedPane.gridy = 3;
		add(fitTabbedPane, gbc_fitTabbedPane);

		// ---------------------------------------------------------------------------------------------------
		// GAUSSIAN FIT TAB
		// ---------------------------------------------------------------------------------------------------

		JPanel gaussianFitPanel = new JPanel();
		fitTabbedPane.addTab("Gaussian Fit", null, gaussianFitPanel, null);
		GridBagLayout gbl_gaussianFitPanel = new GridBagLayout();
		gbl_gaussianFitPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_gaussianFitPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_gaussianFitPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_gaussianFitPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gaussianFitPanel.setLayout(gbl_gaussianFitPanel);

		JLabel lblFrom = new JLabel("From");
		GridBagConstraints gbc_lblFrom = new GridBagConstraints();
		gbc_lblFrom.insets = new Insets(10, 5, 5, 5);
		gbc_lblFrom.anchor = GridBagConstraints.EAST;
		gbc_lblFrom.gridx = 0;
		gbc_lblFrom.gridy = 0;
		gaussianFitPanel.add(lblFrom, gbc_lblFrom);

		gaussianFitFromSignComboBox = new JComboBox<String>();
		gaussianFitFromSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		gaussianFitFromSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_gaussianFitFromSignComboBox = new GridBagConstraints();
		gbc_gaussianFitFromSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_gaussianFitFromSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_gaussianFitFromSignComboBox.gridx = 1;
		gbc_gaussianFitFromSignComboBox.gridy = 0;
		gaussianFitPanel.add(gaussianFitFromSignComboBox, gbc_gaussianFitFromSignComboBox);

		gaussianFitFromTextField = new JTextField();
		gaussianFitFromTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitFromTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					gaussianFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(gaussianFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					gaussianFitFromTextField.setText("<not a number>");
				}
			}
		});
		gaussianFitFromTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					gaussianFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(gaussianFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					gaussianFitFromTextField.setText("<not a number>");
				}
			}
		});
		gaussianFitFromTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_gaussianFitFromTextField = new GridBagConstraints();
		gbc_gaussianFitFromTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitFromTextField.insets = new Insets(10, 0, 5, 5);
		gbc_gaussianFitFromTextField.gridx = 2;
		gbc_gaussianFitFromTextField.gridy = 0;
		gaussianFitPanel.add(gaussianFitFromTextField, gbc_gaussianFitFromTextField);
		gaussianFitFromTextField.setColumns(10);

		JLabel lblPeakPosition = new JLabel("Peak Position");
		GridBagConstraints gbc_lblPeakPosition = new GridBagConstraints();
		gbc_lblPeakPosition.insets = new Insets(10, 0, 5, 5);
		gbc_lblPeakPosition.anchor = GridBagConstraints.EAST;
		gbc_lblPeakPosition.gridx = 3;
		gbc_lblPeakPosition.gridy = 0;
		gaussianFitPanel.add(lblPeakPosition, gbc_lblPeakPosition);

		gaussianFitPeakPositionTextField = new JTextField();
		gaussianFitPeakPositionTextField.setEditable(false);
		gaussianFitPeakPositionTextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitPeakPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_gaussianFitPeakPositionTextField = new GridBagConstraints();
		gbc_gaussianFitPeakPositionTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitPeakPositionTextField.insets = new Insets(10, 0, 5, 5);
		gbc_gaussianFitPeakPositionTextField.gridx = 4;
		gbc_gaussianFitPeakPositionTextField.gridy = 0;
		gaussianFitPanel.add(gaussianFitPeakPositionTextField, gbc_gaussianFitPeakPositionTextField);
		gaussianFitPeakPositionTextField.setColumns(10);

		JLabel lblOffset_1 = new JLabel("Shift");
		GridBagConstraints gbc_lblOffset_1 = new GridBagConstraints();
		gbc_lblOffset_1.insets = new Insets(10, 0, 5, 5);
		gbc_lblOffset_1.anchor = GridBagConstraints.EAST;
		gbc_lblOffset_1.gridx = 5;
		gbc_lblOffset_1.gridy = 0;
		gaussianFitPanel.add(lblOffset_1, gbc_lblOffset_1);

		gaussianFitOffsetTextField = new JTextField();
		gaussianFitOffsetTextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitOffsetTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitOffsetTextField.setEditable(false);
		gaussianFitOffsetTextField.setColumns(10);
		GridBagConstraints gbc_gaussianFitOffsetTextField = new GridBagConstraints();
		gbc_gaussianFitOffsetTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitOffsetTextField.insets = new Insets(10, 0, 5, 200);
		gbc_gaussianFitOffsetTextField.gridx = 6;
		gbc_gaussianFitOffsetTextField.gridy = 0;
		gaussianFitPanel.add(gaussianFitOffsetTextField, gbc_gaussianFitOffsetTextField);

		JLabel lblTo = new JLabel("To");
		GridBagConstraints gbc_lblTo = new GridBagConstraints();
		gbc_lblTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTo.anchor = GridBagConstraints.EAST;
		gbc_lblTo.gridx = 0;
		gbc_lblTo.gridy = 1;
		gaussianFitPanel.add(lblTo, gbc_lblTo);

		gaussianFitToSignComboBox = new JComboBox<String>();
		gaussianFitToSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		gaussianFitToSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_gaussianFitToSignComboBox = new GridBagConstraints();
		gbc_gaussianFitToSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_gaussianFitToSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_gaussianFitToSignComboBox.gridx = 1;
		gbc_gaussianFitToSignComboBox.gridy = 1;
		gaussianFitPanel.add(gaussianFitToSignComboBox, gbc_gaussianFitToSignComboBox);

		gaussianFitToTextField = new JTextField();
		gaussianFitToTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitToTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					gaussianFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(gaussianFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					gaussianFitToTextField.setText("<not a number>");
				}
			}
		});
		gaussianFitToTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					gaussianFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(gaussianFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					gaussianFitToTextField.setText("<not a number>");
				}
			}
		});
		gaussianFitToTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_gaussianFitToTextField = new GridBagConstraints();
		gbc_gaussianFitToTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitToTextField.insets = new Insets(0, 0, 5, 5);
		gbc_gaussianFitToTextField.gridx = 2;
		gbc_gaussianFitToTextField.gridy = 1;
		gaussianFitPanel.add(gaussianFitToTextField, gbc_gaussianFitToTextField);
		gaussianFitToTextField.setColumns(10);

		JLabel lblPeakHeight = new JLabel("Peak Height");
		GridBagConstraints gbc_lblPeakHeight = new GridBagConstraints();
		gbc_lblPeakHeight.insets = new Insets(0, 0, 5, 5);
		gbc_lblPeakHeight.anchor = GridBagConstraints.EAST;
		gbc_lblPeakHeight.gridx = 3;
		gbc_lblPeakHeight.gridy = 1;
		gaussianFitPanel.add(lblPeakHeight, gbc_lblPeakHeight);

		gaussianFitPeakHeightTextField = new JTextField();
		gaussianFitPeakHeightTextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitPeakHeightTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitPeakHeightTextField.setEditable(false);
		GridBagConstraints gbc_gaussianFitPeakHeightTextField = new GridBagConstraints();
		gbc_gaussianFitPeakHeightTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitPeakHeightTextField.insets = new Insets(0, 0, 5, 5);
		gbc_gaussianFitPeakHeightTextField.gridx = 4;
		gbc_gaussianFitPeakHeightTextField.gridy = 1;
		gaussianFitPanel.add(gaussianFitPeakHeightTextField, gbc_gaussianFitPeakHeightTextField);
		gaussianFitPeakHeightTextField.setColumns(10);

		JLabel lblChi = new JLabel("Chi2");
		GridBagConstraints gbc_lblChi = new GridBagConstraints();
		gbc_lblChi.insets = new Insets(0, 0, 5, 5);
		gbc_lblChi.anchor = GridBagConstraints.EAST;
		gbc_lblChi.gridx = 5;
		gbc_lblChi.gridy = 1;
		gaussianFitPanel.add(lblChi, gbc_lblChi);

		gaussianFitChi2TextField = new JTextField();
		gaussianFitChi2TextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitChi2TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitChi2TextField.setEditable(false);
		gaussianFitChi2TextField.setColumns(10);
		GridBagConstraints gbc_gaussianFitChi2TextField = new GridBagConstraints();
		gbc_gaussianFitChi2TextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitChi2TextField.insets = new Insets(0, 0, 5, 0);
		gbc_gaussianFitChi2TextField.gridx = 6;
		gbc_gaussianFitChi2TextField.gridy = 1;
		gaussianFitPanel.add(gaussianFitChi2TextField, gbc_gaussianFitChi2TextField);

		JLabel lblFwhm = new JLabel("FWHM");
		GridBagConstraints gbc_lblFwhm = new GridBagConstraints();
		gbc_lblFwhm.insets = new Insets(0, 0, 5, 5);
		gbc_lblFwhm.anchor = GridBagConstraints.EAST;
		gbc_lblFwhm.gridx = 3;
		gbc_lblFwhm.gridy = 2;
		gaussianFitPanel.add(lblFwhm, gbc_lblFwhm);

		gaussianFitFwhmTextField = new JTextField();
		gaussianFitFwhmTextField.setEditable(false);
		gaussianFitFwhmTextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitFwhmTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_gaussianFitFwhmTextField = new GridBagConstraints();
		gbc_gaussianFitFwhmTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitFwhmTextField.insets = new Insets(0, 0, 5, 5);
		gbc_gaussianFitFwhmTextField.gridx = 4;
		gbc_gaussianFitFwhmTextField.gridy = 2;
		gaussianFitPanel.add(gaussianFitFwhmTextField, gbc_gaussianFitFwhmTextField);
		gaussianFitFwhmTextField.setColumns(10);

		gaussianFitMoveAxisButton = new JButton("MOVE AXIS ON PEAK");
		gaussianFitMoveAxisButton.setActionCommand(ActionCommands.GAUSSIAN_MOVE_AXIS);
		gaussianFitMoveAxisButton.addActionListener(this);

		gaussianFitFitPeakButton = new JButton("FIT PEAK");
		gaussianFitFitPeakButton.setActionCommand(ActionCommands.GAUSSIAN_FIT_PEAK);
		gaussianFitFitPeakButton.addActionListener(this);

		JLabel lblRms_1 = new JLabel("RMS");
		GridBagConstraints gbc_lblRms_1 = new GridBagConstraints();
		gbc_lblRms_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRms_1.anchor = GridBagConstraints.EAST;
		gbc_lblRms_1.gridx = 5;
		gbc_lblRms_1.gridy = 2;
		gaussianFitPanel.add(lblRms_1, gbc_lblRms_1);

		gaussianFitRmsTextField = new JTextField();
		gaussianFitRmsTextField.setText(DriverUtilities.formatControllerPosition(0));
		gaussianFitRmsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gaussianFitRmsTextField.setEditable(false);
		gaussianFitRmsTextField.setColumns(10);
		GridBagConstraints gbc_gaussianFitRmsTextField = new GridBagConstraints();
		gbc_gaussianFitRmsTextField.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitRmsTextField.insets = new Insets(0, 0, 5, 0);
		gbc_gaussianFitRmsTextField.gridx = 6;
		gbc_gaussianFitRmsTextField.gridy = 2;
		gaussianFitPanel.add(gaussianFitRmsTextField, gbc_gaussianFitRmsTextField);
		GridBagConstraints gbc_gaussianFitFitPeakButton = new GridBagConstraints();
		gbc_gaussianFitFitPeakButton.anchor = GridBagConstraints.EAST;
		gbc_gaussianFitFitPeakButton.insets = new Insets(0, 0, 5, 10);
		gbc_gaussianFitFitPeakButton.gridx = 3;
		gbc_gaussianFitFitPeakButton.gridy = 3;
		gaussianFitPanel.add(gaussianFitFitPeakButton, gbc_gaussianFitFitPeakButton);
		GridBagConstraints gbc_gaussianFitMoveAxisButton = new GridBagConstraints();
		gbc_gaussianFitMoveAxisButton.gridwidth = 2;
		gbc_gaussianFitMoveAxisButton.anchor = GridBagConstraints.EAST;
		gbc_gaussianFitMoveAxisButton.insets = new Insets(0, 0, 5, 5);
		gbc_gaussianFitMoveAxisButton.gridx = 4;
		gbc_gaussianFitMoveAxisButton.gridy = 3;
		gaussianFitPanel.add(gaussianFitMoveAxisButton, gbc_gaussianFitMoveAxisButton);

		gaussianFitSendDataButton = new JButton("SEND DATA TO PLOTS");
		gaussianFitSendDataButton.setActionCommand(ActionCommands.GAUSSIAN_SEND_DATA);
		gaussianFitSendDataButton.addActionListener(this);
		gaussianFitSendDataButton.setEnabled(sendDataEnabled);
		GridBagConstraints gbc_gaussianFitSendDataButton = new GridBagConstraints();
		gbc_gaussianFitSendDataButton.insets = new Insets(0, 0, 5, 0);
		gbc_gaussianFitSendDataButton.anchor = GridBagConstraints.WEST;
		gbc_gaussianFitSendDataButton.gridx = 6;
		gbc_gaussianFitSendDataButton.gridy = 3;
		gaussianFitPanel.add(gaussianFitSendDataButton, gbc_gaussianFitSendDataButton);
		fitTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		// ---------------------------------------------------------------------------------------------------
		// PSEUDO VOIGT FIT TAB
		// ---------------------------------------------------------------------------------------------------

		JPanel pseudovoigtFitPanel = new JPanel();
		fitTabbedPane.addTab("Pseudo-Voigt Fit", null, pseudovoigtFitPanel, null);
		GridBagLayout gbl_pseudovoigtFitPanel = new GridBagLayout();
		gbl_pseudovoigtFitPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_pseudovoigtFitPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_pseudovoigtFitPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_pseudovoigtFitPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pseudovoigtFitPanel.setLayout(gbl_pseudovoigtFitPanel);

		JLabel label_4 = new JLabel("From");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(10, 5, 5, 5);
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 0;
		pseudovoigtFitPanel.add(label_4, gbc_label_4);

		pseudoVoigtFitFromSignComboBox = new JComboBox<String>();
		pseudoVoigtFitFromSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		pseudoVoigtFitFromSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_pseudoVoigtFitFromSignComboBox = new GridBagConstraints();
		gbc_pseudoVoigtFitFromSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_pseudoVoigtFitFromSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_pseudoVoigtFitFromSignComboBox.gridx = 1;
		gbc_pseudoVoigtFitFromSignComboBox.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitFromSignComboBox, gbc_pseudoVoigtFitFromSignComboBox);

		pseudoVoigtFitFromTextField = new JTextField();
		pseudoVoigtFitFromTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitFromTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitFromTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitFromTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitFromTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitFromTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitFromTextField.setColumns(10);
		GridBagConstraints gbc_pseudoVoigtFitFromTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitFromTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitFromTextField.insets = new Insets(10, 0, 5, 5);
		gbc_pseudoVoigtFitFromTextField.gridx = 2;
		gbc_pseudoVoigtFitFromTextField.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitFromTextField, gbc_pseudoVoigtFitFromTextField);

		JLabel label = new JLabel("Center");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(10, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 3;
		gbc_label.gridy = 0;
		pseudovoigtFitPanel.add(label, gbc_label);

		pseudoVoigtFitCenterTextField = new JTextField();
		pseudoVoigtFitCenterTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitCenterTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitCenterTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitCenterTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitCenterTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitCenterTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitCenterTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitCenterTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitCenterTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitCenterTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitCenterTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitCenterTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitCenterTextField.insets = new Insets(10, 0, 5, 5);
		gbc_pseudoVoigtFitCenterTextField.gridx = 4;
		gbc_pseudoVoigtFitCenterTextField.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitCenterTextField, gbc_pseudoVoigtFitCenterTextField);
		pseudoVoigtFitCenterTextField.setColumns(10);

		JLabel label_2 = new JLabel("Shape");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(10, 0, 5, 5);
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.gridx = 5;
		gbc_label_2.gridy = 0;
		pseudovoigtFitPanel.add(label_2, gbc_label_2);

		pseudoVoigtFitShapeTextField = new JTextField();
		pseudoVoigtFitShapeTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitShapeTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitShapeTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitShapeTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitShapeTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitShapeTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitShapeTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitShapeTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitShapeTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitShapeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitShapeTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitShapeTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitShapeTextField.insets = new Insets(10, 0, 5, 5);
		gbc_pseudoVoigtFitShapeTextField.gridx = 6;
		gbc_pseudoVoigtFitShapeTextField.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitShapeTextField, gbc_pseudoVoigtFitShapeTextField);
		pseudoVoigtFitShapeTextField.setColumns(10);

		JLabel lblCenter = new JLabel("Center");
		GridBagConstraints gbc_lblCenter = new GridBagConstraints();
		gbc_lblCenter.insets = new Insets(10, 0, 5, 5);
		gbc_lblCenter.anchor = GridBagConstraints.EAST;
		gbc_lblCenter.gridx = 7;
		gbc_lblCenter.gridy = 0;
		pseudovoigtFitPanel.add(lblCenter, gbc_lblCenter);

		pseudoVoigtFitCenterOutTextField = new JTextField();
		pseudoVoigtFitCenterOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitCenterOutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitCenterOutTextField.setEditable(false);
		GridBagConstraints gbc_pseudoVoigtFitCenterOutTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitCenterOutTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitCenterOutTextField.insets = new Insets(10, 0, 5, 5);
		gbc_pseudoVoigtFitCenterOutTextField.gridx = 8;
		gbc_pseudoVoigtFitCenterOutTextField.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitCenterOutTextField, gbc_pseudoVoigtFitCenterOutTextField);
		pseudoVoigtFitCenterOutTextField.setColumns(10);

		JLabel lblOffset = new JLabel("Offset");
		lblOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblOffset = new GridBagConstraints();
		gbc_lblOffset.anchor = GridBagConstraints.EAST;
		gbc_lblOffset.insets = new Insets(10, 0, 5, 5);
		gbc_lblOffset.gridx = 9;
		gbc_lblOffset.gridy = 0;
		pseudovoigtFitPanel.add(lblOffset, gbc_lblOffset);

		pseudoVoigtFitOffsetOutTextField = new JTextField();
		pseudoVoigtFitOffsetOutTextField.setEditable(false);
		pseudoVoigtFitOffsetOutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitOffsetOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitOffsetOutTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitOffsetOutTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitOffsetOutTextField.insets = new Insets(10, 0, 5, 0);
		gbc_pseudoVoigtFitOffsetOutTextField.gridx = 10;
		gbc_pseudoVoigtFitOffsetOutTextField.gridy = 0;
		pseudovoigtFitPanel.add(pseudoVoigtFitOffsetOutTextField, gbc_pseudoVoigtFitOffsetOutTextField);
		pseudoVoigtFitOffsetOutTextField.setColumns(10);

		JLabel label_5 = new JLabel("To");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.EAST;
		gbc_label_5.insets = new Insets(0, 5, 5, 5);
		gbc_label_5.gridx = 0;
		gbc_label_5.gridy = 1;
		pseudovoigtFitPanel.add(label_5, gbc_label_5);

		pseudoVoigtFitToSignComboBox = new JComboBox<String>();
		pseudoVoigtFitToSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		pseudoVoigtFitToSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_pseudoVoigtFitToSignComboBox = new GridBagConstraints();
		gbc_pseudoVoigtFitToSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_pseudoVoigtFitToSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitToSignComboBox.gridx = 1;
		gbc_pseudoVoigtFitToSignComboBox.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitToSignComboBox, gbc_pseudoVoigtFitToSignComboBox);

		pseudoVoigtFitToTextField = new JTextField();
		pseudoVoigtFitToTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitToTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitToTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitToTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitToTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitToTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitToTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitToTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitToTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitToTextField.gridx = 2;
		gbc_pseudoVoigtFitToTextField.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitToTextField, gbc_pseudoVoigtFitToTextField);
		pseudoVoigtFitToTextField.setColumns(10);

		JLabel label_1 = new JLabel("Amplitude");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 1;
		pseudovoigtFitPanel.add(label_1, gbc_label_1);

		pseudoVoigtFitAmplitudeTextField = new JTextField();
		pseudoVoigtFitAmplitudeTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitAmplitudeTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitAmplitudeTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitAmplitudeTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitAmplitudeTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitAmplitudeTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitAmplitudeTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitAmplitudeTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitAmplitudeTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitAmplitudeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitAmplitudeTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitAmplitudeTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitAmplitudeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitAmplitudeTextField.gridx = 4;
		gbc_pseudoVoigtFitAmplitudeTextField.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitAmplitudeTextField, gbc_pseudoVoigtFitAmplitudeTextField);
		pseudoVoigtFitAmplitudeTextField.setColumns(10);

		JLabel label_3 = new JLabel("Offset");
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.gridx = 5;
		gbc_label_3.gridy = 1;
		pseudovoigtFitPanel.add(label_3, gbc_label_3);

		pseudoVoigtFitOffsetTextField = new JTextField();
		pseudoVoigtFitOffsetTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitOffsetTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitOffsetTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitOffsetTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitOffsetTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitOffsetTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitOffsetTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitOffsetTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitOffsetTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitOffsetTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_pseudoVoigtFitOffsetTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitOffsetTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitOffsetTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitOffsetTextField.gridx = 6;
		gbc_pseudoVoigtFitOffsetTextField.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitOffsetTextField, gbc_pseudoVoigtFitOffsetTextField);
		pseudoVoigtFitOffsetTextField.setColumns(10);

		JLabel lblAmplitude = new JLabel("Amplitude");
		GridBagConstraints gbc_lblAmplitude = new GridBagConstraints();
		gbc_lblAmplitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblAmplitude.anchor = GridBagConstraints.EAST;
		gbc_lblAmplitude.gridx = 7;
		gbc_lblAmplitude.gridy = 1;
		pseudovoigtFitPanel.add(lblAmplitude, gbc_lblAmplitude);

		pseudoVoigtFitAmplitudeOutTextField = new JTextField();
		pseudoVoigtFitAmplitudeOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitAmplitudeOutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitAmplitudeOutTextField.setEditable(false);
		GridBagConstraints gbc_pseudoVoigtFitAmplitudeOutTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitAmplitudeOutTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitAmplitudeOutTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitAmplitudeOutTextField.gridx = 8;
		gbc_pseudoVoigtFitAmplitudeOutTextField.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitAmplitudeOutTextField, gbc_pseudoVoigtFitAmplitudeOutTextField);
		pseudoVoigtFitAmplitudeOutTextField.setColumns(10);

		JLabel lblShape = new JLabel("Chi2");
		lblShape.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblShape = new GridBagConstraints();
		gbc_lblShape.insets = new Insets(0, 0, 5, 5);
		gbc_lblShape.anchor = GridBagConstraints.EAST;
		gbc_lblShape.gridx = 9;
		gbc_lblShape.gridy = 1;
		pseudovoigtFitPanel.add(lblShape, gbc_lblShape);

		pseudoVoigtFitChi2OutTextField = new JTextField();
		pseudoVoigtFitChi2OutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitChi2OutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitChi2OutTextField.setEditable(false);
		GridBagConstraints gbc_pseudoVoigtFitChi2OutTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitChi2OutTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitChi2OutTextField.insets = new Insets(0, 0, 5, 0);
		gbc_pseudoVoigtFitChi2OutTextField.gridx = 10;
		gbc_pseudoVoigtFitChi2OutTextField.gridy = 1;
		pseudovoigtFitPanel.add(pseudoVoigtFitChi2OutTextField, gbc_pseudoVoigtFitChi2OutTextField);
		pseudoVoigtFitChi2OutTextField.setColumns(10);

		JLabel lblSigma = new JLabel("FWHM");
		GridBagConstraints gbc_lblSigma = new GridBagConstraints();
		gbc_lblSigma.insets = new Insets(0, 0, 5, 5);
		gbc_lblSigma.anchor = GridBagConstraints.EAST;
		gbc_lblSigma.gridx = 3;
		gbc_lblSigma.gridy = 2;
		pseudovoigtFitPanel.add(lblSigma, gbc_lblSigma);

		pseudoVoigtFitFwhmTextField = new JTextField();
		pseudoVoigtFitFwhmTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					pseudoVoigtFitFwhmTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitFwhmTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitFwhmTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitFwhmTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					pseudoVoigtFitFwhmTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(pseudoVoigtFitFwhmTextField.getText()))));
				}
				catch (Throwable t)
				{
					pseudoVoigtFitFwhmTextField.setText("<not a number>");
				}
			}
		});
		pseudoVoigtFitFwhmTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitFwhmTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_pseudoVoigtFitSigmaTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitSigmaTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitSigmaTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitSigmaTextField.gridx = 4;
		gbc_pseudoVoigtFitSigmaTextField.gridy = 2;
		pseudovoigtFitPanel.add(pseudoVoigtFitFwhmTextField, gbc_pseudoVoigtFitSigmaTextField);
		pseudoVoigtFitFwhmTextField.setColumns(10);

		JLabel lblFwhm_1 = new JLabel("FWHM");
		GridBagConstraints gbc_lblFwhm_1 = new GridBagConstraints();
		gbc_lblFwhm_1.anchor = GridBagConstraints.EAST;
		gbc_lblFwhm_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblFwhm_1.gridx = 7;
		gbc_lblFwhm_1.gridy = 2;
		pseudovoigtFitPanel.add(lblFwhm_1, gbc_lblFwhm_1);

		pseudoVoigtFitFwhmOutTextField = new JTextField();
		pseudoVoigtFitFwhmOutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitFwhmOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitFwhmOutTextField.setEditable(false);
		GridBagConstraints gbc_pseudoVoigtFitFWHMTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitFWHMTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitFWHMTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitFWHMTextField.gridx = 8;
		gbc_pseudoVoigtFitFWHMTextField.gridy = 2;
		pseudovoigtFitPanel.add(pseudoVoigtFitFwhmOutTextField, gbc_pseudoVoigtFitFWHMTextField);
		pseudoVoigtFitFwhmOutTextField.setColumns(10);

		pseudoVoigtFitInitParametersButton = new JButton("INIT PARAMETERS");
		pseudoVoigtFitInitParametersButton.setActionCommand(ActionCommands.PSEUDOVOIGT_INIT_PARAMETERS);
		pseudoVoigtFitInitParametersButton.addActionListener(this);

		JLabel lblRms = new JLabel("RMS");
		GridBagConstraints gbc_lblRms = new GridBagConstraints();
		gbc_lblRms.insets = new Insets(0, 0, 5, 5);
		gbc_lblRms.anchor = GridBagConstraints.EAST;
		gbc_lblRms.gridx = 9;
		gbc_lblRms.gridy = 2;
		pseudovoigtFitPanel.add(lblRms, gbc_lblRms);

		pseudoVoigtFitRmsOutTextField = new JTextField();
		pseudoVoigtFitRmsOutTextField.setText(DriverUtilities.formatControllerPosition(0));
		pseudoVoigtFitRmsOutTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		pseudoVoigtFitRmsOutTextField.setEditable(false);
		pseudoVoigtFitRmsOutTextField.setColumns(10);
		GridBagConstraints gbc_pseudoVoigtFitRmsOutTextField = new GridBagConstraints();
		gbc_pseudoVoigtFitRmsOutTextField.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitRmsOutTextField.insets = new Insets(0, 0, 5, 0);
		gbc_pseudoVoigtFitRmsOutTextField.gridx = 10;
		gbc_pseudoVoigtFitRmsOutTextField.gridy = 2;
		pseudovoigtFitPanel.add(pseudoVoigtFitRmsOutTextField, gbc_pseudoVoigtFitRmsOutTextField);
		GridBagConstraints gbc_pseudoVoigtFitInitParametersButton = new GridBagConstraints();
		gbc_pseudoVoigtFitInitParametersButton.anchor = GridBagConstraints.EAST;
		gbc_pseudoVoigtFitInitParametersButton.gridwidth = 2;
		gbc_pseudoVoigtFitInitParametersButton.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitInitParametersButton.gridx = 3;
		gbc_pseudoVoigtFitInitParametersButton.gridy = 3;
		pseudovoigtFitPanel.add(pseudoVoigtFitInitParametersButton, gbc_pseudoVoigtFitInitParametersButton);

		pseudoVoigtFitFitPeakButton = new JButton("FIT PEAK");
		pseudoVoigtFitFitPeakButton.setActionCommand(ActionCommands.PSEUDOVOIGT_FIT_PEAK);
		pseudoVoigtFitFitPeakButton.addActionListener(this);
		GridBagConstraints gbc_pseudoVoigtFitFitPeakButton = new GridBagConstraints();
		gbc_pseudoVoigtFitFitPeakButton.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitFitPeakButton.gridwidth = 2;
		gbc_pseudoVoigtFitFitPeakButton.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitFitPeakButton.gridx = 5;
		gbc_pseudoVoigtFitFitPeakButton.gridy = 3;
		pseudovoigtFitPanel.add(pseudoVoigtFitFitPeakButton, gbc_pseudoVoigtFitFitPeakButton);

		pseudoVoigtFitMoveAxisButton = new JButton("MOVE AXIS ON PEAK");
		pseudoVoigtFitMoveAxisButton.setActionCommand(ActionCommands.PSEUDOVOIGT_MOVE_AXIS);
		pseudoVoigtFitMoveAxisButton.addActionListener(this);
		GridBagConstraints gbc_pseudoVoigtFitMoveAxisButton = new GridBagConstraints();
		gbc_pseudoVoigtFitMoveAxisButton.anchor = GridBagConstraints.EAST;
		gbc_pseudoVoigtFitMoveAxisButton.gridwidth = 2;
		gbc_pseudoVoigtFitMoveAxisButton.insets = new Insets(0, 0, 5, 5);
		gbc_pseudoVoigtFitMoveAxisButton.gridx = 7;
		gbc_pseudoVoigtFitMoveAxisButton.gridy = 3;
		pseudovoigtFitPanel.add(pseudoVoigtFitMoveAxisButton, gbc_pseudoVoigtFitMoveAxisButton);

		pseudoVoigtFitSendDataButton = new JButton("SEND DATA TO PLOTS");
		pseudoVoigtFitSendDataButton.setActionCommand(ActionCommands.PSEUDOVOIGT_SEND_DATA);
		pseudoVoigtFitSendDataButton.addActionListener(this);
		pseudoVoigtFitSendDataButton.setEnabled(sendDataEnabled);
		GridBagConstraints gbc_pseudoVoigtFitSendDataButton = new GridBagConstraints();
		gbc_pseudoVoigtFitSendDataButton.insets = new Insets(0, 0, 5, 0);
		gbc_pseudoVoigtFitSendDataButton.anchor = GridBagConstraints.WEST;
		gbc_pseudoVoigtFitSendDataButton.gridwidth = 2;
		gbc_pseudoVoigtFitSendDataButton.gridx = 9;
		gbc_pseudoVoigtFitSendDataButton.gridy = 3;
		pseudovoigtFitPanel.add(pseudoVoigtFitSendDataButton, gbc_pseudoVoigtFitSendDataButton);
		fitTabbedPane.setForegroundAt(1, new Color(0, 102, 51));

		// ---------------------------------------------------------------------------------------------------
		// LINEAR FIT TAB
		// ---------------------------------------------------------------------------------------------------

		JPanel linearFitPanel = new JPanel();
		fitTabbedPane.addTab("Linear Fit", null, linearFitPanel, null);
		fitTabbedPane.setForegroundAt(2, new Color(0, 102, 51));
		GridBagLayout gbl_linearFitPanel = new GridBagLayout();
		gbl_linearFitPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_linearFitPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_linearFitPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_linearFitPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		linearFitPanel.setLayout(gbl_linearFitPanel);

		JLabel lblFrom_1 = new JLabel("From");
		GridBagConstraints gbc_lblFrom_1 = new GridBagConstraints();
		gbc_lblFrom_1.insets = new Insets(10, 5, 5, 5);
		gbc_lblFrom_1.anchor = GridBagConstraints.EAST;
		gbc_lblFrom_1.gridx = 0;
		gbc_lblFrom_1.gridy = 0;
		linearFitPanel.add(lblFrom_1, gbc_lblFrom_1);

		linearFitFromSignComboBox = new JComboBox<String>();
		linearFitFromSignComboBox.setMaximumRowCount(2);
		linearFitFromSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		GridBagConstraints gbc_linearFitFromSignComboBox = new GridBagConstraints();
		gbc_linearFitFromSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_linearFitFromSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_linearFitFromSignComboBox.gridx = 1;
		gbc_linearFitFromSignComboBox.gridy = 0;
		linearFitPanel.add(linearFitFromSignComboBox, gbc_linearFitFromSignComboBox);

		linearFitFromTextField = new JTextField();
		linearFitFromTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					linearFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(linearFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					linearFitFromTextField.setText("<not a number>");
				}
			}
		});
		linearFitFromTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					linearFitFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(linearFitFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					linearFitFromTextField.setText("<not a number>");
				}
			}
		});
		linearFitFromTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitFromTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_linearFitFromTextField = new GridBagConstraints();
		gbc_linearFitFromTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitFromTextField.insets = new Insets(10, 0, 5, 5);
		gbc_linearFitFromTextField.gridx = 2;
		gbc_linearFitFromTextField.gridy = 0;
		linearFitPanel.add(linearFitFromTextField, gbc_linearFitFromTextField);
		linearFitFromTextField.setColumns(10);

		JLabel lblIntercept = new JLabel("Intercept");
		GridBagConstraints gbc_lblIntercept = new GridBagConstraints();
		gbc_lblIntercept.insets = new Insets(10, 0, 5, 5);
		gbc_lblIntercept.anchor = GridBagConstraints.EAST;
		gbc_lblIntercept.gridx = 3;
		gbc_lblIntercept.gridy = 0;
		linearFitPanel.add(lblIntercept, gbc_lblIntercept);

		linearFitInterceptTextField = new JTextField();
		linearFitInterceptTextField.setEditable(false);
		linearFitInterceptTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitInterceptTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_linearFitInterceptTextField = new GridBagConstraints();
		gbc_linearFitInterceptTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitInterceptTextField.insets = new Insets(10, 0, 5, 5);
		gbc_linearFitInterceptTextField.gridx = 4;
		gbc_linearFitInterceptTextField.gridy = 0;
		linearFitPanel.add(linearFitInterceptTextField, gbc_linearFitInterceptTextField);
		linearFitInterceptTextField.setColumns(10);

		JLabel label_7 = new JLabel("\u00B1");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.anchor = GridBagConstraints.EAST;
		gbc_label_7.insets = new Insets(10, 0, 5, 5);
		gbc_label_7.gridx = 5;
		gbc_label_7.gridy = 0;
		linearFitPanel.add(label_7, gbc_label_7);

		linearFitInterceptErrorTextField = new JTextField();
		linearFitInterceptErrorTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitInterceptErrorTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		linearFitInterceptErrorTextField.setEditable(false);
		GridBagConstraints gbc_linearFitInterceptErrorTextField = new GridBagConstraints();
		gbc_linearFitInterceptErrorTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitInterceptErrorTextField.insets = new Insets(10, 0, 5, 250);
		gbc_linearFitInterceptErrorTextField.gridx = 6;
		gbc_linearFitInterceptErrorTextField.gridy = 0;
		linearFitPanel.add(linearFitInterceptErrorTextField, gbc_linearFitInterceptErrorTextField);
		linearFitInterceptErrorTextField.setColumns(10);

		JLabel lblTo_1 = new JLabel("To");
		GridBagConstraints gbc_lblTo_1 = new GridBagConstraints();
		gbc_lblTo_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblTo_1.anchor = GridBagConstraints.EAST;
		gbc_lblTo_1.gridx = 0;
		gbc_lblTo_1.gridy = 1;
		linearFitPanel.add(lblTo_1, gbc_lblTo_1);

		linearFitToSignComboBox = new JComboBox<String>();
		linearFitToSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		linearFitToSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_linearFitToSignComboBox = new GridBagConstraints();
		gbc_linearFitToSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_linearFitToSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_linearFitToSignComboBox.gridx = 1;
		gbc_linearFitToSignComboBox.gridy = 1;
		linearFitPanel.add(linearFitToSignComboBox, gbc_linearFitToSignComboBox);

		linearFitToTextField = new JTextField();
		linearFitToTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					linearFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(linearFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					linearFitToTextField.setText("<not a number>");
				}
			}
		});
		linearFitToTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					linearFitToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(linearFitToTextField.getText()))));
				}
				catch (Throwable t)
				{
					linearFitToTextField.setText("<not a number>");
				}
			}
		});
		linearFitToTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitToTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_linearFitToTextField = new GridBagConstraints();
		gbc_linearFitToTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitToTextField.insets = new Insets(0, 0, 5, 5);
		gbc_linearFitToTextField.gridx = 2;
		gbc_linearFitToTextField.gridy = 1;
		linearFitPanel.add(linearFitToTextField, gbc_linearFitToTextField);
		linearFitToTextField.setColumns(10);

		JLabel lblPendency = new JLabel("Slope");
		GridBagConstraints gbc_lblPendency = new GridBagConstraints();
		gbc_lblPendency.anchor = GridBagConstraints.EAST;
		gbc_lblPendency.insets = new Insets(0, 0, 5, 5);
		gbc_lblPendency.gridx = 3;
		gbc_lblPendency.gridy = 1;
		linearFitPanel.add(lblPendency, gbc_lblPendency);

		linearFitSlopeTextField = new JTextField();
		linearFitSlopeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		linearFitSlopeTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitSlopeTextField.setEditable(false);
		GridBagConstraints gbc_linearFitSlopeTextField = new GridBagConstraints();
		gbc_linearFitSlopeTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitSlopeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_linearFitSlopeTextField.gridx = 4;
		gbc_linearFitSlopeTextField.gridy = 1;
		linearFitPanel.add(linearFitSlopeTextField, gbc_linearFitSlopeTextField);
		linearFitSlopeTextField.setColumns(10);

		JLabel label_6 = new JLabel("\u00B1");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.insets = new Insets(0, 0, 5, 5);
		gbc_label_6.gridx = 5;
		gbc_label_6.gridy = 1;
		linearFitPanel.add(label_6, gbc_label_6);

		linearFitSlopeErrorTextField = new JTextField();
		linearFitSlopeErrorTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		linearFitSlopeErrorTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitSlopeErrorTextField.setEditable(false);
		GridBagConstraints gbc_linearFitSlopeErrorTextField = new GridBagConstraints();
		gbc_linearFitSlopeErrorTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitSlopeErrorTextField.insets = new Insets(0, 0, 5, 250);
		gbc_linearFitSlopeErrorTextField.gridx = 6;
		gbc_linearFitSlopeErrorTextField.gridy = 1;
		linearFitPanel.add(linearFitSlopeErrorTextField, gbc_linearFitSlopeErrorTextField);
		linearFitSlopeErrorTextField.setColumns(10);

		JLabel lblR = new JLabel("R^2");
		GridBagConstraints gbc_lblR = new GridBagConstraints();
		gbc_lblR.insets = new Insets(0, 0, 5, 5);
		gbc_lblR.anchor = GridBagConstraints.EAST;
		gbc_lblR.gridx = 3;
		gbc_lblR.gridy = 2;
		linearFitPanel.add(lblR, gbc_lblR);

		linearFitRSquareTextField = new JTextField();
		linearFitRSquareTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		linearFitRSquareTextField.setText(DriverUtilities.formatControllerPosition(0));
		linearFitRSquareTextField.setEditable(false);
		GridBagConstraints gbc_linearFitRSquareTextField = new GridBagConstraints();
		gbc_linearFitRSquareTextField.anchor = GridBagConstraints.WEST;
		gbc_linearFitRSquareTextField.insets = new Insets(0, 0, 5, 5);
		gbc_linearFitRSquareTextField.gridx = 4;
		gbc_linearFitRSquareTextField.gridy = 2;
		linearFitPanel.add(linearFitRSquareTextField, gbc_linearFitRSquareTextField);
		linearFitRSquareTextField.setColumns(10);

		linearFitExecuteButton = new JButton("EXECUTE LINEAR FIT");
		linearFitExecuteButton.setActionCommand(ActionCommands.EXECUTE_LINEAR_FIT);
		linearFitExecuteButton.addActionListener(this);
		GridBagConstraints gbc_linearFitExecuteButton = new GridBagConstraints();
		gbc_linearFitExecuteButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_linearFitExecuteButton.insets = new Insets(0, 0, 5, 250);
		gbc_linearFitExecuteButton.gridwidth = 3;
		gbc_linearFitExecuteButton.gridx = 4;
		gbc_linearFitExecuteButton.gridy = 3;
		linearFitPanel.add(linearFitExecuteButton, gbc_linearFitExecuteButton);

		// ---------------------------------------------------------------------------------------------------
		// MIDDLE POINT TAB
		// ---------------------------------------------------------------------------------------------------

		JPanel middlePointPanel = new JPanel();
		fitTabbedPane.addTab("Middle Point", null, middlePointPanel, null);
		GridBagLayout gbl_middlePointPanel = new GridBagLayout();
		gbl_middlePointPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_middlePointPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_middlePointPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_middlePointPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		middlePointPanel.setLayout(gbl_middlePointPanel);

		JLabel lblPlateau = new JLabel("Plateau");
		lblPlateau.setForeground(new Color(0, 102, 0));
		GridBagConstraints gbc_lblPlateau = new GridBagConstraints();
		gbc_lblPlateau.insets = new Insets(10, 5, 5, 10);
		gbc_lblPlateau.gridx = 0;
		gbc_lblPlateau.gridy = 0;
		middlePointPanel.add(lblPlateau, gbc_lblPlateau);

		JLabel lblFrom_3 = new JLabel("From");
		GridBagConstraints gbc_lblFrom_3 = new GridBagConstraints();
		gbc_lblFrom_3.anchor = GridBagConstraints.EAST;
		gbc_lblFrom_3.insets = new Insets(10, 0, 5, 5);
		gbc_lblFrom_3.gridx = 1;
		gbc_lblFrom_3.gridy = 0;
		middlePointPanel.add(lblFrom_3, gbc_lblFrom_3);

		middlePointPlateauFromSignComboBox = new JComboBox<String>();
		middlePointPlateauFromSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		middlePointPlateauFromSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_middlePointPlateauFromSignComboBox = new GridBagConstraints();
		gbc_middlePointPlateauFromSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_middlePointPlateauFromSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_middlePointPlateauFromSignComboBox.gridx = 2;
		gbc_middlePointPlateauFromSignComboBox.gridy = 0;
		middlePointPanel.add(middlePointPlateauFromSignComboBox, gbc_middlePointPlateauFromSignComboBox);

		middlePointPlateauFromTextField = new JTextField();
		middlePointPlateauFromTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					middlePointPlateauFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointPlateauFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointPlateauFromTextField.setText("<not a number>");
				}
			}
		});
		middlePointPlateauFromTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					middlePointPlateauFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointPlateauFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointPlateauFromTextField.setText("<not a number>");
				}
			}
		});
		middlePointPlateauFromTextField.setText(DriverUtilities.formatControllerPosition(0));
		middlePointPlateauFromTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_middlePointPlateauFromTextField = new GridBagConstraints();
		gbc_middlePointPlateauFromTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointPlateauFromTextField.insets = new Insets(10, 0, 5, 5);
		gbc_middlePointPlateauFromTextField.gridx = 3;
		gbc_middlePointPlateauFromTextField.gridy = 0;
		middlePointPanel.add(middlePointPlateauFromTextField, gbc_middlePointPlateauFromTextField);
		middlePointPlateauFromTextField.setColumns(10);

		JLabel lblDown = new JLabel("Down");
		lblDown.setForeground(new Color(0, 102, 0));
		GridBagConstraints gbc_lblDown = new GridBagConstraints();
		gbc_lblDown.insets = new Insets(10, 0, 5, 10);
		gbc_lblDown.gridx = 4;
		gbc_lblDown.gridy = 0;
		middlePointPanel.add(lblDown, gbc_lblDown);

		JLabel lblFrom_2 = new JLabel("From");
		GridBagConstraints gbc_lblFrom_2 = new GridBagConstraints();
		gbc_lblFrom_2.insets = new Insets(10, 0, 5, 5);
		gbc_lblFrom_2.anchor = GridBagConstraints.EAST;
		gbc_lblFrom_2.gridx = 5;
		gbc_lblFrom_2.gridy = 0;
		middlePointPanel.add(lblFrom_2, gbc_lblFrom_2);

		middlePointDownFromSignComboBox = new JComboBox<String>();
		middlePointDownFromSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		middlePointDownFromSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_middlePointDownFromSignComboBox = new GridBagConstraints();
		gbc_middlePointDownFromSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_middlePointDownFromSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_middlePointDownFromSignComboBox.gridx = 6;
		gbc_middlePointDownFromSignComboBox.gridy = 0;
		middlePointPanel.add(middlePointDownFromSignComboBox, gbc_middlePointDownFromSignComboBox);

		middlePointDownFromTextField = new JTextField();
		middlePointDownFromTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					middlePointDownFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointDownFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointDownFromTextField.setText("<not a number>");
				}
			}
		});
		middlePointDownFromTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					middlePointDownFromTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointDownFromTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointDownFromTextField.setText("<not a number>");
				}
			}
		});
		middlePointDownFromTextField.setText(DriverUtilities.formatControllerPosition(0));
		middlePointDownFromTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_middlePointDownFromTextField = new GridBagConstraints();
		gbc_middlePointDownFromTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointDownFromTextField.insets = new Insets(10, 0, 5, 5);
		gbc_middlePointDownFromTextField.gridx = 7;
		gbc_middlePointDownFromTextField.gridy = 0;
		middlePointPanel.add(middlePointDownFromTextField, gbc_middlePointDownFromTextField);
		middlePointDownFromTextField.setColumns(10);

		JLabel lblMiddlePointSignal = new JLabel("Middle Point Signal Height");
		GridBagConstraints gbc_lblMiddlePointSignal = new GridBagConstraints();
		gbc_lblMiddlePointSignal.insets = new Insets(10, 0, 5, 5);
		gbc_lblMiddlePointSignal.anchor = GridBagConstraints.EAST;
		gbc_lblMiddlePointSignal.gridx = 8;
		gbc_lblMiddlePointSignal.gridy = 0;
		middlePointPanel.add(lblMiddlePointSignal, gbc_lblMiddlePointSignal);

		middlePointSignalHeigtTextField = new JTextField();
		middlePointSignalHeigtTextField.setText("0");
		middlePointSignalHeigtTextField.setEditable(false);
		middlePointSignalHeigtTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_middlePointSignalHeigtTextField = new GridBagConstraints();
		gbc_middlePointSignalHeigtTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointSignalHeigtTextField.insets = new Insets(10, 0, 5, 100);
		gbc_middlePointSignalHeigtTextField.gridx = 9;
		gbc_middlePointSignalHeigtTextField.gridy = 0;
		middlePointPanel.add(middlePointSignalHeigtTextField, gbc_middlePointSignalHeigtTextField);
		middlePointSignalHeigtTextField.setColumns(10);

		JLabel lblTo_3 = new JLabel("To");
		GridBagConstraints gbc_lblTo_3 = new GridBagConstraints();
		gbc_lblTo_3.anchor = GridBagConstraints.EAST;
		gbc_lblTo_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblTo_3.gridx = 1;
		gbc_lblTo_3.gridy = 1;
		middlePointPanel.add(lblTo_3, gbc_lblTo_3);

		middlePointPlateauToSignComboBox = new JComboBox<String>();
		middlePointPlateauToSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		middlePointPlateauToSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_middlePointPlateauToSignComboBox = new GridBagConstraints();
		gbc_middlePointPlateauToSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_middlePointPlateauToSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_middlePointPlateauToSignComboBox.gridx = 2;
		gbc_middlePointPlateauToSignComboBox.gridy = 1;
		middlePointPanel.add(middlePointPlateauToSignComboBox, gbc_middlePointPlateauToSignComboBox);

		middlePointPlateauToTextField = new JTextField();
		middlePointPlateauToTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					middlePointPlateauToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointPlateauToTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointPlateauToTextField.setText("<not a number>");
				}
			}
		});
		middlePointPlateauToTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					middlePointPlateauToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointPlateauToTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointPlateauToTextField.setText("<not a number>");
				}
			}
		});
		middlePointPlateauToTextField.setText(DriverUtilities.formatControllerPosition(0));
		middlePointPlateauToTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_middlePointPlateauToTextField = new GridBagConstraints();
		gbc_middlePointPlateauToTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointPlateauToTextField.insets = new Insets(0, 0, 5, 5);
		gbc_middlePointPlateauToTextField.gridx = 3;
		gbc_middlePointPlateauToTextField.gridy = 1;
		middlePointPanel.add(middlePointPlateauToTextField, gbc_middlePointPlateauToTextField);
		middlePointPlateauToTextField.setColumns(10);

		JLabel lblTo_2 = new JLabel("To");
		GridBagConstraints gbc_lblTo_2 = new GridBagConstraints();
		gbc_lblTo_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblTo_2.anchor = GridBagConstraints.EAST;
		gbc_lblTo_2.gridx = 5;
		gbc_lblTo_2.gridy = 1;
		middlePointPanel.add(lblTo_2, gbc_lblTo_2);

		middlePointDownToSignComboBox = new JComboBox<String>();
		middlePointDownToSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		middlePointDownToSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_middlePointDownToSignComboBox = new GridBagConstraints();
		gbc_middlePointDownToSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_middlePointDownToSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_middlePointDownToSignComboBox.gridx = 6;
		gbc_middlePointDownToSignComboBox.gridy = 1;
		middlePointPanel.add(middlePointDownToSignComboBox, gbc_middlePointDownToSignComboBox);

		middlePointDownToTextField = new JTextField();
		middlePointDownToTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					middlePointDownToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointDownToTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointDownToTextField.setText("<not a number>");
				}
			}
		});
		middlePointDownToTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					middlePointDownToTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(middlePointDownToTextField.getText()))));
				}
				catch (Throwable t)
				{
					middlePointDownToTextField.setText("<not a number>");
				}
			}
		});
		middlePointDownToTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		middlePointDownToTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_middlePointDownToTextField = new GridBagConstraints();
		gbc_middlePointDownToTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointDownToTextField.insets = new Insets(0, 0, 5, 5);
		gbc_middlePointDownToTextField.gridx = 7;
		gbc_middlePointDownToTextField.gridy = 1;
		middlePointPanel.add(middlePointDownToTextField, gbc_middlePointDownToTextField);
		middlePointDownToTextField.setColumns(10);

		JLabel lblMiddlePointPosition = new JLabel("Middle Point Position");
		GridBagConstraints gbc_lblMiddlePointPosition = new GridBagConstraints();
		gbc_lblMiddlePointPosition.insets = new Insets(0, 0, 5, 5);
		gbc_lblMiddlePointPosition.anchor = GridBagConstraints.EAST;
		gbc_lblMiddlePointPosition.gridx = 8;
		gbc_lblMiddlePointPosition.gridy = 1;
		middlePointPanel.add(lblMiddlePointPosition, gbc_lblMiddlePointPosition);

		middlePointPositionTextField = new JTextField();
		middlePointPositionTextField.setText(DriverUtilities.formatControllerPosition(0));
		middlePointPositionTextField.setEditable(false);
		middlePointPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_middlePointPositionTextField = new GridBagConstraints();
		gbc_middlePointPositionTextField.anchor = GridBagConstraints.WEST;
		gbc_middlePointPositionTextField.insets = new Insets(0, 0, 5, 0);
		gbc_middlePointPositionTextField.gridx = 9;
		gbc_middlePointPositionTextField.gridy = 1;
		middlePointPanel.add(middlePointPositionTextField, gbc_middlePointPositionTextField);
		middlePointPositionTextField.setColumns(10);

		middlePointFindMiddlePointButton = new JButton("FIND MIDDLE POINT");
		middlePointFindMiddlePointButton.setActionCommand(ActionCommands.MIDDLE_POINT_FIND);
		middlePointFindMiddlePointButton.addActionListener(this);
		GridBagConstraints gbc_middlePointFindMiddlePointButton = new GridBagConstraints();
		gbc_middlePointFindMiddlePointButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_middlePointFindMiddlePointButton.gridwidth = 3;
		gbc_middlePointFindMiddlePointButton.insets = new Insets(25, 0, 0, 5);
		gbc_middlePointFindMiddlePointButton.gridx = 5;
		gbc_middlePointFindMiddlePointButton.gridy = 2;
		middlePointPanel.add(middlePointFindMiddlePointButton, gbc_middlePointFindMiddlePointButton);

		middlePointMoveAxisButton = new JButton("MOVE AXIS ON MIDDLE POINT");
		middlePointMoveAxisButton.setActionCommand(ActionCommands.MIDDLE_POINT_MOVE_AXIS);
		middlePointMoveAxisButton.addActionListener(this);
		GridBagConstraints gbc_middlePointMoveAxisButton = new GridBagConstraints();
		gbc_middlePointMoveAxisButton.insets = new Insets(25, 0, 0, 100);
		gbc_middlePointMoveAxisButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_middlePointMoveAxisButton.gridwidth = 2;
		gbc_middlePointMoveAxisButton.gridx = 8;
		gbc_middlePointMoveAxisButton.gridy = 2;
		middlePointPanel.add(middlePointMoveAxisButton, gbc_middlePointMoveAxisButton);
		fitTabbedPane.setForegroundAt(3, new Color(0, 102, 51));
	}

	protected String getOrdinateLabel()
	{
		return "Counts";
	}

	protected boolean isOrdinateInteger()
	{
		return true;
	}

	protected boolean isAdditionalInformation1Visible()
	{
		return true;
	}

	protected boolean isAdditionalInformation2Visible()
	{
		return true;
	}

	protected String getAdditionaInfo1TabName()
	{
		return "Additional Information 1";
	}

	protected String getAdditionaInfo2TabName()
	{
		return "Additional Information 2";
	}

	protected String getAdditionalInfo1Name()
	{
		return "Add Info 1";
	}

	protected String getAdditionalInfo2Name()
	{
		return "Add Info 2";
	}

	protected String getAdditionalInfo1Format()
	{
		return "%9.3f";
	}

	protected String getAdditionalInfo2Format()
	{
		return "%9.3f";
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * public
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	public synchronized void resetPanel()
	{
		this.xyDataset.getSeries(0).clear();
		this.xyDataset.getSeries(1).clear();
		this.xyDataset.getSeries(2).clear();
		this.xyDataset.getSeries(3).clear();
		this.xyDataset.getSeries(4).clear();

		this.xyDatasetAddInfo1.getSeries(0).clear();
		this.xyDatasetAddInfo1.getSeries(1).clear();
		this.xyDatasetAddInfo1.getSeries(2).clear();
		this.xyDatasetAddInfo2.getSeries(0).clear();
		this.xyDatasetAddInfo2.getSeries(1).clear();
		this.xyDatasetAddInfo2.getSeries(2).clear();

		plot.getRangeAxis(0).setLowerBound(0);
		plot.getRangeAxis(0).setUpperBound(10);
		plot.getDomainAxis(0).setLowerBound(-10);
		plot.getDomainAxis(0).setUpperBound(10);

		plotAI1.getRangeAxis(0).setLowerBound(0);
		plotAI1.getRangeAxis(0).setUpperBound(10);
		plotAI1.getDomainAxis(0).setLowerBound(-10);
		plotAI1.getDomainAxis(0).setUpperBound(10);

		plotAI2.getRangeAxis(0).setLowerBound(0);
		plotAI2.getRangeAxis(0).setUpperBound(10);
		plotAI2.getDomainAxis(0).setLowerBound(-10);
		plotAI2.getDomainAxis(0).setUpperBound(10);

		this.scanIndex = -1;
		this.colorTextField.setBackground(this.getColor(this.scanIndex));
		this.scanProgressiveTextField.setText(Integer.toString(this.scanIndex + 1));

		this.loadButton.setEnabled(true);
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * Interface IDriverListener
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	public synchronized void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
		if (this.scanIndex >= 0)
		{
			this.xyDataset.getSeries(this.scanIndex).add(point.getX(), point.getMeasure());

			this.xyDatasetAddInfo1.getSeries(this.scanIndex).add(point.getX(), point.getAdditionalInformation1());
			this.xyDatasetAddInfo2.getSeries(this.scanIndex).add(point.getX(), point.getAdditionalInformation2());

			this.progressBar.setValue(progress.getProgress());

			if (point.getMeasure() > plot.getRangeAxis(0).getUpperBound())
				plot.getRangeAxis(0).setUpperBound(point.getMeasure() * 1.1);

			if (point.getAdditionalInformation1() > plotAI1.getRangeAxis(0).getUpperBound())
				plotAI1.getRangeAxis(0).setUpperBound(point.getAdditionalInformation1() * 1.1);

			if (point.getAdditionalInformation2() > plotAI2.getRangeAxis(0).getUpperBound())
				plotAI2.getRangeAxis(0).setUpperBound(point.getMeasure() * 1.1);
		}
	}

	public synchronized void signalXAxisRange(int axis, double startPosition, double stopPosition) throws CommunicationPortException
	{
		plot.getDomainAxis(0).setLowerBound(startPosition);
		plot.getDomainAxis(0).setUpperBound(stopPosition);

		plotAI1.getDomainAxis(0).setLowerBound(startPosition);
		plotAI1.getDomainAxis(0).setUpperBound(stopPosition);

		plotAI2.getDomainAxis(0).setLowerBound(startPosition);
		plotAI2.getDomainAxis(0).setUpperBound(stopPosition);

		this.populateFitFields(startPosition, stopPosition);
	}

	public synchronized void signalScanStart()
	{
		this.isScanActive = true;

		this.disableFitItems();
	}

	public synchronized void signalScanStop()
	{
		this.progressBar.setValue(100);

		this.isStopScanActivated = false;
		this.isScanActive = false;

		this.enableFitItems();
	}

	public synchronized boolean isStopScanActivated(int axis)
	{
		return this.isStopScanActivated;
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

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals(ActionCommands.STOP))
			{
				this.manageEventStop();
			}
			else
			{
				if (!this.isScanActive)
				{
					if (e.getActionCommand().equals(ActionCommands.SCAN))
					{
						this.manageEventScan();
					}
					else if (e.getActionCommand().equals(ActionCommands.VALUE))
					{
						this.manageEventValue();
					}
					else if (e.getActionCommand().equals(ActionCommands.STEPS))
					{
						this.manageEventSteps();
					}
					else if (e.getActionCommand().equals(ActionCommands.DELETE_LAST_SCAN))
					{
						this.manageEventDeleteLastScan();
					}
					else if (e.getActionCommand().equals(ActionCommands.RESET))
					{
						this.manageEventReset();
					}
					else if (e.getActionCommand().equals(ActionCommands.SAVE_LAST_SCAN))
					{
						this.manageEventSaveLastScan();
					}
					else if (e.getActionCommand().equals(ActionCommands.SAVE_ALL))
					{
						this.manageEventSaveAll();
					}
					else if (e.getActionCommand().equals(ActionCommands.LOAD))
					{
						this.manageEventLoad();
					}
					else if (e.getActionCommand().equals(ActionCommands.GAUSSIAN_FIT_PEAK))
					{
						this.manageEventGaussianFitFitPeak();
					}
					else if (e.getActionCommand().equals(ActionCommands.GAUSSIAN_MOVE_AXIS))
					{
						this.manageEventGaussianFitMoveAxisOnPeak();
					}
					else if (e.getActionCommand().equals(ActionCommands.GAUSSIAN_SEND_DATA))
					{
						this.manageEventGaussianFitSendData();
					}
					else if (e.getActionCommand().equals(ActionCommands.PSEUDOVOIGT_FIT_PEAK))
					{
						this.manageEventPseudoVoigtFitFitPeak();
					}
					else if (e.getActionCommand().equals(ActionCommands.PSEUDOVOIGT_MOVE_AXIS))
					{
						this.manageEventPseudoVoigtFitMoveAxisOnPeak();
					}
					else if (e.getActionCommand().equals(ActionCommands.PSEUDOVOIGT_INIT_PARAMETERS))
					{
						this.manageEventPseudoVoigtFitInitParameters();
					}
					else if (e.getActionCommand().equals(ActionCommands.PSEUDOVOIGT_SEND_DATA))
					{
						this.manageEventPseudoVoigtFitSendData();
					}
					else if (e.getActionCommand().equals(ActionCommands.EXECUTE_LINEAR_FIT))
					{
						this.manageEventLinearFitExecute();
					}
					else if (e.getActionCommand().equals(ActionCommands.MIDDLE_POINT_FIND))
					{
						this.manageEventMiddlePointFindMiddlePoint();
					}
					else if (e.getActionCommand().equals(ActionCommands.MIDDLE_POINT_MOVE_AXIS))
					{
						this.manageEventMiddlePointMoveAxisOnMiddlePoint();
					}
				}
				else
				{
					GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
				}
			}
		}
		catch (Exception exception)
		{
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventLoad() throws IOException
	{
		String fileName = GuiUtilities.showDatFileChooser(this, "");

		try
		{
			StringUtilities.checkString(fileName, "");

			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			this.scanIndex = 0;

			this.progressBar.setValue(0);
			this.colorTextField.setBackground(this.getColor(this.scanIndex));
			this.scanProgressiveTextField.setText(Integer.toString(this.scanIndex + 1));

			XYSeries series = this.xyDataset.getSeries(this.scanIndex);
			XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1.getSeries(this.scanIndex);
			XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2.getSeries(this.scanIndex);

			series.clear();
			seriesAddInfo1.clear();
			seriesAddInfo2.clear();

			String row = reader.readLine();

			double countsMax = 0;
			double addInfo1Max = 0.0;
			double addInfo2Max = 0.0;

			MeasurePoint measurePointFirst = null;
			MeasurePoint measurePointLast = null;

			while (row != null)
			{
				row = row.trim();

				if (!(row.isEmpty() || row.startsWith("!")))
				{
					MeasurePoint measurePoint = new MeasurePoint(row);

					measurePointFirst = (measurePointFirst == null) ? measurePoint : measurePointFirst;
					measurePointLast = measurePoint;
					countsMax = (measurePoint.getMeasure() > countsMax) ? measurePoint.getMeasure() : countsMax;
					addInfo1Max = (measurePoint.getAdditionalInformation1() > addInfo1Max) ? measurePoint.getAdditionalInformation1() : addInfo1Max;
					addInfo2Max = (measurePoint.getAdditionalInformation2() > addInfo2Max) ? measurePoint.getAdditionalInformation2() : addInfo2Max;

					series.add(measurePoint.getX(), measurePoint.getMeasure());
					seriesAddInfo1.add(measurePoint.getX(), measurePoint.getAdditionalInformation1());
					seriesAddInfo2.add(measurePoint.getX(), measurePoint.getAdditionalInformation2());
				}

				row = reader.readLine();
			}

			reader.close();

			plot.getRangeAxis(0).setUpperBound(countsMax * 1.1);
			plot.getDomainAxis(0).setLowerBound(measurePointFirst.getX());
			plot.getDomainAxis(0).setUpperBound(measurePointLast.getX());

			plotAI1.getRangeAxis(0).setUpperBound(addInfo1Max * 1.1);
			plotAI1.getDomainAxis(0).setLowerBound(measurePointFirst.getX());
			plotAI1.getDomainAxis(0).setUpperBound(measurePointLast.getX());

			plotAI2.getRangeAxis(0).setUpperBound(addInfo2Max * 1.1);
			plotAI2.getDomainAxis(0).setLowerBound(measurePointFirst.getX());
			plotAI2.getDomainAxis(0).setUpperBound(measurePointLast.getX());

		}
		catch (IllegalArgumentException e)
		{
			GuiUtilities.showErrorPopup("Choose a file for loading data", this);
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventScan() throws CommunicationPortException
	{
		this.scanDoChecksOnFields();
		this.calculateStepValue();

		if (GuiUtilities.showConfirmPopup(this.getScanMessage(), this))
		{
			this.disableFitItems();

			this.scanIndex++;
			if (this.scanIndex == 3)
				this.scanIndex = 0;

			this.loadButton.setEnabled(false);
			this.progressBar.setValue(0);
			this.colorTextField.setBackground(this.getColor(this.scanIndex));
			this.scanProgressiveTextField.setText(Integer.toString(this.scanIndex + 1));
			this.xyDataset.getSeries(this.scanIndex).clear();
			this.xyDatasetAddInfo1.getSeries(this.scanIndex).clear();
			this.xyDatasetAddInfo2.getSeries(this.scanIndex).clear();

			this.getScanThread().start();
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventStop() throws CommunicationPortException
	{
		if (this.isScanActive)
		{
			if (GuiUtilities.showConfirmPopup("Confirm interruption of the current scan?", this))
				this.isStopScanActivated = true;
		}
		else
			GuiUtilities.showErrorPopup("No active scan!", this);

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventValue()
	{
		this.calculateStepValue();
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventSteps()
	{
		this.calculateSteps();
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventDeleteLastScan()
	{
		if (GuiUtilities.showConfirmPopup("Confirm deletion of the last scan (data will be lost)?", this))
		{
			if (this.scanIndex < 0)
				GuiUtilities.showErrorPopup("No Scan to be deleted", this);
			else
			{
				this.xyDataset.getSeries(this.scanIndex).clear();
				this.xyDatasetAddInfo1.getSeries(this.scanIndex).clear();
				this.xyDatasetAddInfo2.getSeries(this.scanIndex).clear();
				this.xyDataset.getSeries(3).clear();
				this.xyDataset.getSeries(4).clear();
				this.scanIndex--;
				this.colorTextField.setBackground(this.getColor(this.scanIndex));
				this.scanProgressiveTextField.setText(Integer.toString(this.scanIndex + 1));

				if (this.scanIndex < 0)
					this.loadButton.setEnabled(true);
			}
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventReset()
	{
		if (GuiUtilities.showConfirmPopup("Confirm deletion of all the scans (data will be lost)?", this))
		{
			if (this.scanIndex < 0)
				GuiUtilities.showErrorPopup("No Scan to be deleted", this);
			else
				this.resetPanel();
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventSaveLastScan() throws IOException
	{
		String fileName = GuiUtilities.showDatFileChooser(this, "Scan_" + this.axisConfiguration.getName() + "_" + Integer.toString(this.scanIndex + 1) + ".dat");

		try
		{
			StringUtilities.checkString(fileName, "");

			if (!fileName.toLowerCase().endsWith(".dat"))
				fileName += ".dat";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try
			{
				XYSeries series = this.xyDataset.getSeries(this.scanIndex);
				XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1.getSeries(this.scanIndex);
				XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2.getSeries(this.scanIndex);

				this.writeDataFileHeading(series, writer);
				writer.newLine();

				int nItems = series.getItemCount();

				for (int index = 0; index < nItems; index++)
				{
					writer.write(GuiUtilities.parseDouble(series.getX(index).doubleValue()).trim() + " " + (this.isOrdinateInteger() ? series.getY(index).intValue() : series.getY(index).doubleValue()) + " " + String.format(getAdditionalInfo1Format(), seriesAddInfo1.getY(index).doubleValue()) + " " + String.format(getAdditionalInfo2Format(),
					    seriesAddInfo2.getY(index).doubleValue()));
					writer.newLine();
				}

				writer.flush();
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

	// ----------------------------------------------------------------------------

	private synchronized void manageEventSaveAll() throws IOException
	{
		String fileName = GuiUtilities.showDatFileChooser(this, "Scan_" + this.axisConfiguration.getName() + "_All.dat");

		try
		{
			StringUtilities.checkString(fileName, "");

			if (!fileName.toLowerCase().endsWith(".dat"))
				fileName += ".dat";

			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try
			{

				for (int seriesIndex = 0; seriesIndex < 3; seriesIndex++)
				{
					XYSeries series = this.xyDataset.getSeries(seriesIndex);
					XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1.getSeries(seriesIndex);
					XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2.getSeries(seriesIndex);

					this.writeDataFileHeading(series, writer);
					writer.newLine();

					int nItems = series.getItemCount();

					for (int index = 0; index < nItems; index++)
					{
						writer.write(GuiUtilities.parseDouble(series.getX(index).doubleValue()).trim() + " " + (this.isOrdinateInteger() ? series.getY(index).intValue() : series.getY(index).doubleValue()) + " " + String.format(getAdditionalInfo1Format(), seriesAddInfo1.getY(index).doubleValue()) + " " + String.format(getAdditionalInfo2Format(),
						    seriesAddInfo2.getY(index).doubleValue()));
						writer.newLine();
					}

					writer.newLine();
				}

				writer.flush();
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

	// ----------------------------------------------------------------------------

	private synchronized void manageEventGaussianFitFitPeak() throws Exception
	{
		if (this.scanIndex < 0)
			GuiUtilities.showErrorPopup("No data to be fitted", this);
		else
		{
			this.gaussianFitDoChecksOnFields();

			XYSeries series = this.xyDataset.getSeries(this.scanIndex);
			XYSeries fit = this.xyDataset.getSeries(3);
			XYSeries mp = this.xyDataset.getSeries(4);
			mp.clear();
			fit.clear();

			ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.gaussianFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.gaussianFitFromTextField.getText()));
			ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.gaussianFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.gaussianFitToTextField.getText()));

			GaussianFitResult result = FitUtilities.executeGaussianFit(DriverUtilities.controllerToNumber(fromPosition), DriverUtilities.controllerToNumber(toPosition), FitUtilities.Optimizers.LEVENBERG_MARQUARDT, series);

			for (int index = 0; index < series.getItemCount(); index++)
				fit.add(series.getX(index).doubleValue(), FitUtilities.getFittedGaussianFunctionValue(series.getX(index).doubleValue(), result));

			this.gaussianFitPeakPositionTextField.setText(GuiUtilities.parseDouble(result.getPosition()));
			this.gaussianFitPeakHeightTextField.setText(GuiUtilities.parseDouble(result.getShift() + result.getHeight()));
			this.gaussianFitOffsetTextField.setText(GuiUtilities.parseDouble(result.getShift()));
			this.gaussianFitFwhmTextField.setText(GuiUtilities.parseDouble(result.getFwhm()));
			this.gaussianFitChi2TextField.setText(GuiUtilities.parseDouble(result.getChi2()));
			this.gaussianFitRmsTextField.setText(GuiUtilities.parseDouble(result.getRms()));

		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventGaussianFitMoveAxisOnPeak() throws CommunicationPortException
	{
		if (!this.xyDataset.getSeries(3).isEmpty())
		{
			String peakPosition = this.gaussianFitPeakPositionTextField.getText();

			if (GuiUtilities.showConfirmPopup("Confirm movement of the axis on fitted peak position: " + peakPosition + " " + this.measureUnitTextField.getText(), this))
				this.moveAxisOnThePeak(peakPosition);
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventGaussianFitSendData()
	{
		if (!this.xyDataset.getSeries(3).isEmpty())
		{
			String peakPosition = this.gaussianFitPeakPositionTextField.getText();
			String peakIntensity = this.gaussianFitPeakHeightTextField.getText();
			String peakFWHM = this.gaussianFitFwhmTextField.getText();

			if (GuiUtilities.showConfirmPopup("Confirm publishing of the following data?\n\nPeak position: " + peakPosition + " " + this.measureUnitTextField.getText() + "\nPeak Intensity: " + peakIntensity + "\nPeak FWHM: " + peakFWHM, this))
			{
				double firstCoordinate = ListenerRegister.getInstance().getMotorCoordinate(sendDataAxis1);
				double secondCoordinate = ListenerRegister.getInstance().getMotorCoordinate(sendDataAxis2);

				PlottableFitResult fitResult = new PlottableFitResult(firstCoordinate, secondCoordinate, 10 + Double.parseDouble(peakPosition), Double.parseDouble(peakIntensity), Double.parseDouble(peakFWHM));

				ListenerRegister.getInstance().signalFitResult(this.axis, fitResult);
			}
		}

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventPseudoVoigtFitFitPeak() throws Exception
	{
		if (this.scanIndex < 0)
			GuiUtilities.showErrorPopup("No data to be fitted", this);
		else
		{
			this.pseudoVoigtFitDoChecksOnFields();

			XYSeries series = this.xyDataset.getSeries(this.scanIndex);
			XYSeries fit = this.xyDataset.getSeries(3);
			XYSeries mp = this.xyDataset.getSeries(4);
			mp.clear();
			fit.clear();

			ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.pseudoVoigtFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.pseudoVoigtFitFromTextField.getText()));
			ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.pseudoVoigtFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.pseudoVoigtFitToTextField.getText()));

			double amplitude = Double.parseDouble(this.pseudoVoigtFitAmplitudeTextField.getText());
			double center = Double.parseDouble(this.pseudoVoigtFitCenterTextField.getText());
			double sigma = Double.parseDouble(this.pseudoVoigtFitFwhmTextField.getText()) * FitUtilities.PV_FWHM_TO_SIGMA;
			double shape = Double.parseDouble(this.pseudoVoigtFitShapeTextField.getText());
			double offset = Double.parseDouble(this.pseudoVoigtFitOffsetTextField.getText());

			PseudoVoigtFitResult result = FitUtilities.executePseudoVoigtFit(DriverUtilities.controllerToNumber(fromPosition), DriverUtilities.controllerToNumber(toPosition), new PseudoVoigtFitParameters(amplitude, center, sigma, shape, offset), FitUtilities.Optimizers.LEVENBERG_MARQUARDT, series, true);

			for (int index = 0; index < series.getItemCount(); index++)
				fit.add(series.getX(index).doubleValue(), FitUtilities.getFittedPseudoVoigtFunctionValue(series.getX(index).doubleValue(), result));

			this.pseudoVoigtFitAmplitudeOutTextField.setText(GuiUtilities.parseDouble(result.getAmplitude() + result.getOffset()));
			this.pseudoVoigtFitCenterOutTextField.setText(GuiUtilities.parseDouble(result.getCenter()));
			this.pseudoVoigtFitOffsetOutTextField.setText(GuiUtilities.parseDouble(result.getOffset()));
			this.pseudoVoigtFitFwhmOutTextField.setText(GuiUtilities.parseDouble(result.getFwhm()));
			this.pseudoVoigtFitChi2OutTextField.setText(GuiUtilities.parseDouble(result.getChi2()));
			this.pseudoVoigtFitRmsOutTextField.setText(GuiUtilities.parseDouble(result.getRms()));
		}
	}

	private synchronized void manageEventLinearFitExecute() throws Exception
	{
		if (this.scanIndex < 0)
			GuiUtilities.showErrorPopup("No data to be fitted", this);
		else
		{
			this.linearFitDoChecksOnFields();

			XYSeries series = this.xyDataset.getSeries(this.scanIndex);
			XYSeries fit = this.xyDataset.getSeries(3);
			XYSeries mp = this.xyDataset.getSeries(4);
			mp.clear();
			fit.clear();

			ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.linearFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.linearFitFromTextField.getText()));
			ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.linearFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.linearFitToTextField.getText()));

			LinearFitResult result = FitUtilities.executeLinearFit(DriverUtilities.controllerToNumber(fromPosition), DriverUtilities.controllerToNumber(toPosition), series);

			for (int index = 0; index < series.getItemCount(); index++)
				fit.add(series.getX(index).doubleValue(), FitUtilities.getFittedLinearFunctionValue(series.getX(index).doubleValue(), result));

			this.linearFitInterceptTextField.setText(GuiUtilities.parseDouble(result.getIntercept()));
			this.linearFitInterceptErrorTextField.setText(GuiUtilities.parseDouble(result.getInterceptStdError()));
			this.linearFitSlopeTextField.setText(GuiUtilities.parseDouble(result.getSlope()));
			this.linearFitSlopeErrorTextField.setText(GuiUtilities.parseDouble(result.getSlopeStdError()));
			this.linearFitRSquareTextField.setText(GuiUtilities.parseDouble(result.getrSquare()));

		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventPseudoVoigtFitMoveAxisOnPeak() throws CommunicationPortException
	{
		if (!this.xyDataset.getSeries(3).isEmpty())
		{
			String peakPosition = this.pseudoVoigtFitCenterOutTextField.getText();

			if (GuiUtilities.showConfirmPopup("Confirm movement of the axis on fitted peak position: " + peakPosition + " " + this.measureUnitTextField.getText(), this))
				this.moveAxisOnThePeak(peakPosition);
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventPseudoVoigtFitInitParameters()
	{
		if (this.scanIndex >= 0)
		{
			XYSeries series = this.xyDataset.getSeries(this.scanIndex);

			double amplitude = series.getMaxY();
			double center = 0.0;

			boolean found = false;
			for (int index = 0; index < series.getItemCount() && !found; index++)
			{
				if (GuiUtilities.parseDouble(series.getY(index).doubleValue()).equals(GuiUtilities.parseDouble(amplitude)))
				{
					center = series.getX(index).doubleValue();
					found = true;
				}
			}

			double offset = series.getY(0).doubleValue();
			double fwhm = Math.abs(series.getMaxX() - series.getMinX()) / 2;

			this.pseudoVoigtFitAmplitudeTextField.setText(GuiUtilities.parseDouble(amplitude));
			this.pseudoVoigtFitCenterTextField.setText(GuiUtilities.parseDouble(center));
			this.pseudoVoigtFitOffsetTextField.setText(GuiUtilities.parseDouble(offset));
			this.pseudoVoigtFitShapeTextField.setText(GuiUtilities.parseDouble(0.5));
			this.pseudoVoigtFitFwhmTextField.setText(GuiUtilities.parseDouble(fwhm));
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventPseudoVoigtFitSendData()
	{
		if (!this.xyDataset.getSeries(3).isEmpty())
		{
			String peakPosition = this.pseudoVoigtFitCenterTextField.getText();
			String peakIntensity = this.pseudoVoigtFitAmplitudeTextField.getText();
			String peakFWHM = this.pseudoVoigtFitFwhmTextField.getText();

			if (GuiUtilities.showConfirmPopup("Confirm publishing of the following data?\n\nPeak position: " + peakPosition + " " + this.measureUnitTextField.getText() + "\nPeak Intensity: " + peakIntensity + "\nPeak FWHM: " + peakFWHM, this))
			{
				double firstCoordinate = ListenerRegister.getInstance().getMotorCoordinate(sendDataAxis1);
				double secondCoordinate = ListenerRegister.getInstance().getMotorCoordinate(sendDataAxis2);

				PlottableFitResult fitResult = new PlottableFitResult(firstCoordinate, secondCoordinate, Double.parseDouble(peakPosition), Double.parseDouble(peakIntensity), Double.parseDouble(peakFWHM));

				ListenerRegister.getInstance().signalFitResult(this.axis, fitResult);
			}
		}

	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventMiddlePointMoveAxisOnMiddlePoint() throws CommunicationPortException
	{
		if (!this.xyDataset.getSeries(3).isEmpty())
		{
			String middlePointPosition = this.middlePointPositionTextField.getText();

			if (GuiUtilities.showConfirmPopup("Confirm movement of the axis on fitted middle point position: " + middlePointPosition + " " + this.measureUnitTextField.getText(), this))
				this.moveAxisOnThePeak(middlePointPosition);
		}
	}

	// ----------------------------------------------------------------------------

	private synchronized void manageEventMiddlePointFindMiddlePoint()
	{
		if (this.scanIndex < 0)
			GuiUtilities.showErrorPopup("No data to be fitted", this);
		else
		{
			this.middlePointDoChecksOnFields();

			XYSeries series = this.xyDataset.getSeries(this.scanIndex);
			XYSeries fit = this.xyDataset.getSeries(3);
			XYSeries mp = this.xyDataset.getSeries(4);
			mp.clear();
			fit.clear();

			ControllerPosition plateauFromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointPlateauFromSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointPlateauFromTextField.getText()));
			ControllerPosition plateauToPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointPlateauToSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointPlateauToTextField.getText()));
			ControllerPosition downFromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointDownFromSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointDownFromTextField.getText()));
			ControllerPosition downToPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointDownToSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointDownToTextField.getText()));

			MiddlePointResult result = FitUtilities.findMiddlePoint(DriverUtilities.controllerToNumber(plateauFromPosition), DriverUtilities.controllerToNumber(plateauToPosition), DriverUtilities.controllerToNumber(downFromPosition), DriverUtilities.controllerToNumber(downToPosition), series);

			for (int index = 0; index < series.getItemCount(); index++)
				fit.add(series.getX(index).doubleValue(), FitUtilities.getFittedLinearFunctionValue(series.getX(index).doubleValue(), result));

			this.middlePointSignalHeigtTextField.setText(Integer.toString((int) Math.round(result.getMiddlePointSignalHeight())));
			this.middlePointPositionTextField.setText(GuiUtilities.parseDouble(result.getMiddlePointPosition()));

			mp.add(result.getMiddlePointPosition(), Math.round(result.getMiddlePointSignalHeight()));
		}
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * Private Methods
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	// ----------------------------------------------------------------------------

	private void moveAxisOnThePeak(String peakPosition) throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.numberToController(Double.parseDouble(peakPosition));

		if (axisConfiguration.isMultiple())
		{
			DoubleMoveParameters doubleMoveParameters = new DoubleMoveParameters(this.axis, ListenerRegister.getInstance());

			doubleMoveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
			doubleMoveParameters.setPosition(position.getAbsolutePosition());
			doubleMoveParameters.setSign(position.getSign());
			doubleMoveParameters.setReferenceAxis(axisConfiguration.getMultipleAxis().getDefaultReferenceAxis());

			ProgramsFacade.executeProgram(ProgramsFacade.Programs.DOUBLEMOVE, doubleMoveParameters, this.port);
		}
		else
		{
			MoveParameters moveParameters = new MoveParameters(this.axis, ListenerRegister.getInstance());
			moveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters.setPosition(position.getAbsolutePosition());
			moveParameters.setSign(position.getSign());

			ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, moveParameters, this.port);
		}
	}

	private Color getColor(short scanIndex)
	{
		switch (scanIndex)
		{
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

	private void scanDoChecksOnFields() throws InputFieldsException
	{
		StringUtilities.checkString(this.startTextField.getText(), "Start Position");
		StringUtilities.checkString(this.stopTextField.getText(), "Stop Position");
		StringUtilities.checkString(this.stepsTextField.getText(), "Steps");

		ControllerPosition startPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.startSignComboBox.getSelectedItem()), Double.parseDouble(this.startTextField.getText()));
		ControllerPosition stopPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.stopSignComboBox.getSelectedItem()), Double.parseDouble(this.stopTextField.getText()));

		if (DriverUtilities.controllerToNumber(startPosition) >= DriverUtilities.controllerToNumber(stopPosition))
			throw new InputFieldsException("Start Position cannot be greater than or equal to Stop Position");

		if (Integer.parseInt(this.stepsTextField.getText()) <= 0)
			throw new InputFieldsException("Steps must be greater than 0");

	}

	// ----------------------------------------------------------------------------

	private void gaussianFitDoChecksOnFields() throws InputFieldsException
	{
		StringUtilities.checkString(this.gaussianFitFromTextField.getText(), "Gaussian Fit: From Position");
		StringUtilities.checkString(this.gaussianFitToTextField.getText(), "Gaussian Fit: To Position");

		ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.gaussianFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.gaussianFitFromTextField.getText()));
		ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.gaussianFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.gaussianFitToTextField.getText()));

		if (DriverUtilities.controllerToNumber(fromPosition) >= DriverUtilities.controllerToNumber(toPosition))
			throw new InputFieldsException("Gaussian Fit: From Position cannot be greater than or equal to To Position");
	}

	// ----------------------------------------------------------------------------

	private void pseudoVoigtFitDoChecksOnFields() throws InputFieldsException
	{
		StringUtilities.checkString(this.pseudoVoigtFitFromTextField.getText(), "Pseudo-Voigt Fit: From Position");
		StringUtilities.checkString(this.pseudoVoigtFitToTextField.getText(), "Pseudo-Voigt Fit: To Position");
		StringUtilities.checkString(this.pseudoVoigtFitAmplitudeTextField.getText(), "Pseudo-Voigt Fit: Amplitude");
		StringUtilities.checkString(this.pseudoVoigtFitCenterTextField.getText(), "Pseudo-Voigt Fit: Center");
		StringUtilities.checkString(this.pseudoVoigtFitFwhmTextField.getText(), "Pseudo-Voigt Fit: Sigma");
		StringUtilities.checkString(this.pseudoVoigtFitShapeTextField.getText(), "Pseudo-Voigt Fit: Shape");
		StringUtilities.checkString(this.pseudoVoigtFitOffsetTextField.getText(), "Pseudo-Voigt Fit: Offset");

		double amplitude = Double.parseDouble(this.pseudoVoigtFitAmplitudeTextField.getText());
		double sigma = Double.parseDouble(this.pseudoVoigtFitFwhmTextField.getText());
		double shape = Double.parseDouble(this.pseudoVoigtFitShapeTextField.getText());

		if (amplitude < 0)
			throw new InputFieldsException("Pseudo-Voigt Fit: Amplitude " + amplitude);
		if (sigma < 0)
			throw new InputFieldsException("Pseudo-Voigt Fit: Sigma " + sigma);
		if (shape < 0 || shape > 1)
			throw new InputFieldsException("Pseudo-Voigt Fit: Shape " + sigma);

		ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.pseudoVoigtFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.pseudoVoigtFitFromTextField.getText()));
		ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.pseudoVoigtFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.pseudoVoigtFitToTextField.getText()));

		if (DriverUtilities.controllerToNumber(fromPosition) >= DriverUtilities.controllerToNumber(toPosition))
			throw new InputFieldsException("Pseudo-Voigt Fit: From Position cannot be greater than or equal to To Position");
	}

	// ----------------------------------------------------------------------------

	private void linearFitDoChecksOnFields() throws InputFieldsException
	{
		StringUtilities.checkString(this.linearFitFromTextField.getText(), "Linear Fit: From Position");
		StringUtilities.checkString(this.linearFitToTextField.getText(), "Linear Fit: To Position");

		ControllerPosition fromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.linearFitFromSignComboBox.getSelectedItem()), Double.parseDouble(this.linearFitFromTextField.getText()));
		ControllerPosition toPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.linearFitToSignComboBox.getSelectedItem()), Double.parseDouble(this.linearFitToTextField.getText()));

		if (DriverUtilities.controllerToNumber(fromPosition) >= DriverUtilities.controllerToNumber(toPosition))
			throw new InputFieldsException("Linear Fit: From Position cannot be greater than or equal to To Position");
	}

	// ----------------------------------------------------------------------------

	private void middlePointDoChecksOnFields() throws InputFieldsException
	{
		StringUtilities.checkString(this.middlePointPlateauFromTextField.getText(), "Middle Point: Plateau From Position");
		StringUtilities.checkString(this.middlePointPlateauToTextField.getText(), "Middle Point: Plateau To Position");
		StringUtilities.checkString(this.middlePointDownFromTextField.getText(), "Middle Point: Plateau From Position");
		StringUtilities.checkString(this.middlePointDownToTextField.getText(), "Middle Point: Plateau To Position");

		ControllerPosition plateauFromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointPlateauFromSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointPlateauFromTextField.getText()));
		ControllerPosition plateauToPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointPlateauToSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointPlateauToTextField.getText()));

		if (DriverUtilities.controllerToNumber(plateauFromPosition) >= DriverUtilities.controllerToNumber(plateauToPosition))
			throw new InputFieldsException("Middle Point: Plateau - From Position cannot be greater than or equal to To Position");

		ControllerPosition downFromPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointDownFromSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointDownFromTextField.getText()));
		ControllerPosition downToPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.middlePointDownToSignComboBox.getSelectedItem()), Double.parseDouble(this.middlePointDownToTextField.getText()));

		if (DriverUtilities.controllerToNumber(downFromPosition) >= DriverUtilities.controllerToNumber(downToPosition))
			throw new InputFieldsException("Middle Point: Down - From Position cannot be greater than or equal to To Position");

	}

	// ----------------------------------------------------------------------------

	private void populateFitFields(double fromPosition, double toPosition)
	{
		ControllerPosition huberFromPosition = DriverUtilities.numberToController(fromPosition);
		ControllerPosition huberToPosition = DriverUtilities.numberToController(toPosition);

		String fromSign = huberFromPosition.getSign().toString();
		String toSign = huberToPosition.getSign().toString();
		String from = DriverUtilities.formatControllerPosition(huberFromPosition.getAbsolutePosition());
		String to = DriverUtilities.formatControllerPosition(huberToPosition.getAbsolutePosition());

		this.gaussianFitFromSignComboBox.setSelectedItem(fromSign);
		this.gaussianFitFromTextField.setText(from);
		this.gaussianFitToSignComboBox.setSelectedItem(toSign);
		this.gaussianFitToTextField.setText(to);

		this.pseudoVoigtFitFromSignComboBox.setSelectedItem(fromSign);
		this.pseudoVoigtFitFromTextField.setText(from);
		this.pseudoVoigtFitToSignComboBox.setSelectedItem(toSign);
		this.pseudoVoigtFitToTextField.setText(to);

		this.linearFitFromSignComboBox.setSelectedItem(fromSign);
		this.linearFitFromTextField.setText(from);
		this.linearFitToSignComboBox.setSelectedItem(toSign);
		this.linearFitToTextField.setText(to);

		this.middlePointPlateauFromSignComboBox.setSelectedItem(fromSign);
		this.middlePointPlateauFromTextField.setText(from);
		this.middlePointDownToSignComboBox.setSelectedItem(toSign);
		this.middlePointDownToTextField.setText(to);
	}

	// ----------------------------------------------------------------------------

	private void disableFitItems()
	{
		this.setFitItemStatus(false);
	}

	// ----------------------------------------------------------------------------

	private void enableFitItems()
	{
		this.setFitItemStatus(true);
	}

	// ----------------------------------------------------------------------------

	private void setFitItemStatus(boolean enable)
	{
		this.gaussianFitFromSignComboBox.setEnabled(enable);
		this.gaussianFitFromTextField.setEnabled(enable);
		this.gaussianFitToSignComboBox.setEnabled(enable);
		this.gaussianFitToTextField.setEnabled(enable);
		this.gaussianFitFitPeakButton.setEnabled(enable);
		this.gaussianFitMoveAxisButton.setEnabled(enable);
		if (this.sendDataEnabled)
			this.gaussianFitSendDataButton.setEnabled(enable);

		this.pseudoVoigtFitFromSignComboBox.setEnabled(enable);
		this.pseudoVoigtFitFromTextField.setEnabled(enable);
		this.pseudoVoigtFitToSignComboBox.setEnabled(enable);
		this.pseudoVoigtFitToTextField.setEnabled(enable);
		this.pseudoVoigtFitAmplitudeTextField.setEnabled(enable);
		this.pseudoVoigtFitCenterTextField.setEnabled(enable);
		this.pseudoVoigtFitOffsetTextField.setEnabled(enable);
		this.pseudoVoigtFitShapeTextField.setEnabled(enable);
		this.pseudoVoigtFitFwhmTextField.setEnabled(enable);

		this.pseudoVoigtFitInitParametersButton.setEnabled(enable);
		this.pseudoVoigtFitFitPeakButton.setEnabled(enable);
		this.pseudoVoigtFitMoveAxisButton.setEnabled(enable);
		if (this.sendDataEnabled)
			this.pseudoVoigtFitSendDataButton.setEnabled(enable);

		this.linearFitFromSignComboBox.setEnabled(enable);
		this.linearFitFromTextField.setEnabled(enable);
		this.linearFitToSignComboBox.setEnabled(enable);
		this.linearFitToTextField.setEnabled(enable);
		this.linearFitExecuteButton.setEnabled(enable);

		this.middlePointPlateauFromSignComboBox.setEnabled(enable);
		this.middlePointPlateauFromTextField.setEnabled(enable);
		this.middlePointPlateauToSignComboBox.setEnabled(enable);
		this.middlePointPlateauToTextField.setEnabled(enable);
		this.middlePointDownFromSignComboBox.setEnabled(enable);
		this.middlePointDownFromTextField.setEnabled(enable);
		this.middlePointDownToSignComboBox.setEnabled(enable);
		this.middlePointDownToTextField.setEnabled(enable);
	}

	// ----------------------------------------------------------------------------

	private void calculateSteps()
	{
		ControllerPosition startPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.startSignComboBox.getSelectedItem()), Double.parseDouble(this.startTextField.getText()));
		ControllerPosition stopPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.stopSignComboBox.getSelectedItem()), Double.parseDouble(this.stopTextField.getText()));

		double interval = Math.abs(DriverUtilities.controllerToNumber(stopPosition) - DriverUtilities.controllerToNumber(startPosition));
		double stepValue = Double.parseDouble(this.stepValueTextField.getText());

		this.stepsTextField.setText(Integer.toString((int) Math.round((interval / stepValue))));
	}

	// ----------------------------------------------------------------------------

	private void calculateStepValue()
	{
		ControllerPosition startPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.startSignComboBox.getSelectedItem()), Double.parseDouble(this.startTextField.getText()));
		ControllerPosition stopPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.stopSignComboBox.getSelectedItem()), Double.parseDouble(this.stopTextField.getText()));

		double interval = Math.abs(DriverUtilities.controllerToNumber(stopPosition) - DriverUtilities.controllerToNumber(startPosition));
		int steps = Integer.parseInt(this.stepsTextField.getText());

		this.stepValueTextField.setText(DriverUtilities.formatControllerPosition(interval / (double) steps));
	}

	// ----------------------------------------------------------------------------

	private String getScanMessage()
	{
		String message = "Confirm activation of the scan, with the following parameters:\n\n";

		message += "AXIS: " + this.axisConfiguration.getName() + "\n";
		message += "KIND OF MOVEMENT: " + DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()).toString() + "\n";
		message += "START POSITION: " + this.startSignComboBox.getSelectedItem() + this.startTextField.getText() + " " + this.measureUnitTextField.getText() + "\n";
		message += "STOP POSITION: " + this.stopSignComboBox.getSelectedItem() + this.stopTextField.getText() + " " + this.measureUnitTextField.getText() + "\n";
		message += "NUMBER OF STEPS: " + this.stepsTextField.getText() + "\n";
		message += "STEP VALUE: " + this.stepValueTextField.getText() + " " + this.measureUnitTextField.getText() + "\n";
		message += "SCAN TIME: " + this.scanTimeComboBox.getSelectedItem() + "\n\n";

		return message;
	}

	// ----------------------------------------------------------------------------

	private void writeDataFileHeading(XYSeries series, BufferedWriter writer) throws IOException
	{
		String comment = FileIni.getInstance().getProperty("FileCommentString");

		writer.write(comment + "---------------------------------------------------------------------------------");
		writer.newLine();
		writer.write(comment + "SCANNED AXIS: " + this.axisConfiguration.getName());
		writer.newLine();
		writer.write(comment + "KIND OF MOVEMENT: " + DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()).toString());
		writer.newLine();
		writer.write(comment + "START POSITION: " + String.format("%7.4f", series.getMinX()) + " " + this.measureUnitTextField.getText());
		writer.newLine();
		writer.write(comment + "STOP POSITION: " + String.format("%7.4f", series.getMaxX()) + " " + this.measureUnitTextField.getText());
		writer.newLine();
		writer.write(comment + "NUMBER OF STEPS: " + Integer.parseInt(this.stepsTextField.getText()));
		writer.newLine();
		writer.write(comment + "STEP VALUE: " + Double.parseDouble(this.stepValueTextField.getText()) + " " + this.measureUnitTextField.getText());
		writer.newLine();
		writer.write(comment + "SCAN TIME: " + this.scanTimeComboBox.getSelectedItem());
		writer.newLine();
		writer.write(comment + "---------------------------------------------------------------------------------");
		writer.newLine();

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

	protected Thread getScanThread()
	{
		return new ScanThread(this);
	}

	protected class ScanThread extends Thread
	{
		protected ScanPanel panel;

		public ScanThread(ScanPanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				ScanParameters scanParameters = new ScanParameters(panel.axis, ListenerRegister.getInstance());
				scanParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement((String) panel.kindOfMovementComboBox.getSelectedItem()));
				scanParameters.setStartSign(DriverUtilities.parseSign((String) panel.startSignComboBox.getSelectedItem()));
				scanParameters.setStartPosition(Double.parseDouble(panel.startTextField.getText()));
				scanParameters.setStopSign(DriverUtilities.parseSign((String) panel.stopSignComboBox.getSelectedItem()));
				scanParameters.setStopPosition(Double.parseDouble(panel.stopTextField.getText()));
				scanParameters.setScanTime(GuiUtilities.parseScanTime((String) panel.scanTimeComboBox.getSelectedItem()));
				scanParameters.setNumberOfSteps(Integer.parseInt(panel.stepsTextField.getText()));
				scanParameters.setStepValue(Double.parseDouble(panel.stepValueTextField.getText()));
				scanParameters.setPanel(this.panel);

				this.addCustomParameters(scanParameters);

				ProgramsFacade.executeProgram(getScanProgramName(), scanParameters, panel.port);
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this.panel);
			}
		}

		protected void addCustomParameters(ScanParameters scanParameters)
		{
		}

		protected String getScanProgramName()
		{
			return ProgramsFacade.Programs.SCAN;
		}

	}

}
