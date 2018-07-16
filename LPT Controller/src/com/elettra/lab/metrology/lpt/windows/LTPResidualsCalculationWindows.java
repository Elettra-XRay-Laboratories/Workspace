package com.elettra.lab.metrology.lpt.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ArrayUtilities;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.metrology.lpt.panels.LPTScanPanel;
import com.elettra.lab.metrology.lpt.panels.References;

public class LTPResidualsCalculationWindows extends AbstractGenericFrame
{
	public static class KindOfCurve
	{
		public static final int	CALIBRATION		= -1;
		public static final int	SPHERE		    = 0;
		public static final int	ELLIPSE		    = 1;
		public static final int	ELLIPSE_FIXED	= 2;
	}

	static class ActionCommands
	{
		private static final String	EXIT		  = "EXIT";
		private static final String	SAVE_DATA	= "SAVE_DATA";
	}

	private static final long	 serialVersionUID	= -513690344812082943L;

	private JTextField	       slopeErrorRms;
	private JTextField	       figureErrorRms;
	private JTextField	       radiusOfCurvature;
	private JTextField	       tiltValue;
	private JTextField	       a;
	private JTextField	       b;
	private JTextField	       slopeErrorRms_b;
	private JTextField	       figureErrorRms_b;

	private int	               kindOfCurve;
	private Object	           parameters;

	private XYSeriesCollection	xyDataset_slope;
	private XYSeriesCollection	xyDataset_figure;
	private XYPlot	           plot_slope;
	private XYPlot	           plot_figure;

	public static synchronized LTPResidualsCalculationWindows getInstance(ICommunicationPort port, int kindOfCurve, Object parameters) throws HeadlessException,
	    IOException
	{
		return new LTPResidualsCalculationWindows(port, kindOfCurve, parameters);
	}

	private LTPResidualsCalculationWindows(ICommunicationPort port, int kindOfCurve, Object parameters) throws HeadlessException, IOException
	{
		super("Residuals Calculation", port);

		this.kindOfCurve = kindOfCurve;
		this.parameters = parameters;

		this.setIconImage(ImageIO.read(new File("ltpcontroller.jpg")));

		this.setBounds(100, 100, 1520, 740);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 750, 750 };
		gridBagLayout.rowHeights = new int[] { 600, 140 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
		getContentPane().setLayout(gridBagLayout);

		JPanel leftPanel = new JPanel();

		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftPanel.anchor = GridBagConstraints.WEST;
		gbc_leftPanel.fill = GridBagConstraints.BOTH;
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);

		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 750 };
		gbl_leftPanel.rowHeights = new int[] { 580 };
		gbl_leftPanel.columnWeights = new double[] { 0.0 };
		gbl_leftPanel.rowWeights = new double[] { 0.0 };
		leftPanel.setLayout(gbl_leftPanel);

		JTabbedPane slopeGraphTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_slopeGraphTabbedPane = new GridBagConstraints();
		gbc_slopeGraphTabbedPane.insets = new Insets(5, 5, 5, 5);
		gbc_slopeGraphTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_slopeGraphTabbedPane.gridx = 0;
		gbc_slopeGraphTabbedPane.gridy = 0;
		leftPanel.add(slopeGraphTabbedPane, gbc_slopeGraphTabbedPane);

		JPanel slopeGraphPanel = new JPanel();
		slopeGraphPanel.setForeground(new Color(0, 0, 0));
		slopeGraphTabbedPane.addTab("Slope Error R.M.S.", null, slopeGraphPanel, null);
		GridBagLayout gbl_slopeGraphPanel = new GridBagLayout();
		gbl_slopeGraphPanel.columnWidths = new int[] { 0, 0 };
		gbl_slopeGraphPanel.rowHeights = new int[] { 0, 0 };
		gbl_slopeGraphPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_slopeGraphPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		slopeGraphPanel.setLayout(gbl_slopeGraphPanel);

		JPanel slopeInnerPanel = new JPanel();
		slopeInnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_slopeInnerPanel = new GridBagConstraints();
		gbc_slopeInnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_slopeInnerPanel.fill = GridBagConstraints.BOTH;
		gbc_slopeInnerPanel.gridx = 0;
		gbc_slopeInnerPanel.gridy = 0;
		slopeGraphPanel.add(slopeInnerPanel, gbc_slopeInnerPanel);

		xyDataset_slope = new XYSeriesCollection();
		xyDataset_slope.addSeries(new XYSeries("Slope Error RMS"));
		if (this.kindOfCurve == KindOfCurve.CALIBRATION)
			xyDataset_slope.addSeries(new XYSeries("Slope Error RMS Bessy"));

		JFreeChart slopeGraphs = ChartFactory.createXYLineChart(null, "Position (mm)", "Slope Error RMS  (\u03bcrad)", xyDataset_slope, PlotOrientation.VERTICAL,
		    false, true, false);

		plot_slope = slopeGraphs.getXYPlot();
		plot_slope.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		plot_slope.getRangeAxis(0).setAutoRange(false);
		plot_slope.getRangeAxis(0).setLowerBound(-10);
		plot_slope.getRangeAxis(0).setUpperBound(10);
		plot_slope.getDomainAxis(0).setAutoRange(false);
		plot_slope.getDomainAxis(0).setLowerBound(-10);
		plot_slope.getDomainAxis(0).setUpperBound(10);
		plot_slope.setBackgroundPaint(Color.BLACK);

		XYLineAndShapeRenderer xylineandshaperenderer_slope = (XYLineAndShapeRenderer) plot_slope.getRenderer();
		xylineandshaperenderer_slope.setBaseShapesVisible(false);
		xylineandshaperenderer_slope.setSeriesPaint(0, Color.RED);
		xylineandshaperenderer_slope.setSeriesStroke(0, new BasicStroke(1.5f));
		if (this.kindOfCurve == KindOfCurve.CALIBRATION)
		{
			xylineandshaperenderer_slope.setSeriesPaint(1, Color.WHITE);
			xylineandshaperenderer_slope.setSeriesStroke(1, new BasicStroke(3.0f));
		}

		slopeGraphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		slopeInnerPanel.setLayout(new FlowLayout());
		slopeInnerPanel.add(new ChartPanel(slopeGraphs, 700, 500, 700, 500, 700, 500, false, false, false, false, false, false));

		// --------------------------------------------------------------------------------------------------

		JPanel rightPanel = new JPanel();

		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.insets = new Insets(0, 0, 0, 5);
		gbc_rightPanel.anchor = GridBagConstraints.WEST;
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightPanel, gbc_rightPanel);

		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] { 750 };
		gbl_rightPanel.rowHeights = new int[] { 580 };
		gbl_rightPanel.columnWeights = new double[] { 0.0 };
		gbl_rightPanel.rowWeights = new double[] { 0.0 };
		rightPanel.setLayout(gbl_rightPanel);

		JTabbedPane figureGraphTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_figureGraphTabbedPane = new GridBagConstraints();
		gbc_figureGraphTabbedPane.insets = new Insets(5, 5, 5, 5);
		gbc_figureGraphTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_figureGraphTabbedPane.gridx = 0;
		gbc_figureGraphTabbedPane.gridy = 0;
		rightPanel.add(figureGraphTabbedPane, gbc_figureGraphTabbedPane);

		JPanel figureGraphPanel = new JPanel();
		figureGraphPanel.setForeground(new Color(0, 0, 0));
		figureGraphTabbedPane.addTab("Figure Error R.M.S.", null, figureGraphPanel, null);
		GridBagLayout gbl_figureGraphPanel = new GridBagLayout();
		gbl_figureGraphPanel.columnWidths = new int[] { 0, 0 };
		gbl_figureGraphPanel.rowHeights = new int[] { 0, 0 };
		gbl_figureGraphPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_figureGraphPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		figureGraphPanel.setLayout(gbl_figureGraphPanel);

		JPanel figureInnerPanel = new JPanel();
		figureInnerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_figureInnerPanel = new GridBagConstraints();
		gbc_figureInnerPanel.insets = new Insets(10, 5, 5, 5);
		gbc_figureInnerPanel.fill = GridBagConstraints.BOTH;
		gbc_figureInnerPanel.gridx = 0;
		gbc_figureInnerPanel.gridy = 0;
		figureGraphPanel.add(figureInnerPanel, gbc_figureInnerPanel);

		XYSeries series_figure = new XYSeries("Figure Error RMS");

		xyDataset_figure = new XYSeriesCollection();
		xyDataset_figure.addSeries(series_figure);
		if (this.kindOfCurve == KindOfCurve.CALIBRATION)
			xyDataset_figure.addSeries(new XYSeries("Figure Error RMS Bessy"));

		JFreeChart figureGraphs = ChartFactory.createXYLineChart(null, "Mirror Scan (mm)", "Figure Error RMS  (nm)", xyDataset_figure, PlotOrientation.VERTICAL,
		    false, true, false);

		plot_figure = figureGraphs.getXYPlot();
		plot_figure.getRangeAxis(0).setStandardTickUnits(NumberAxis.createStandardTickUnits());
		plot_figure.getRangeAxis(0).setAutoRange(false);
		plot_figure.getRangeAxis(0).setLowerBound(-10);
		plot_figure.getRangeAxis(0).setUpperBound(10);
		plot_figure.getDomainAxis(0).setAutoRange(false);
		plot_figure.getDomainAxis(0).setLowerBound(-10);
		plot_figure.getDomainAxis(0).setUpperBound(10);
		plot_figure.setBackgroundPaint(Color.BLACK);

		XYLineAndShapeRenderer xylineandshaperenderer_figure = (XYLineAndShapeRenderer) plot_figure.getRenderer();
		xylineandshaperenderer_figure.setBaseShapesVisible(false);
		xylineandshaperenderer_figure.setSeriesPaint(0, Color.CYAN);
		xylineandshaperenderer_figure.setSeriesStroke(0, new BasicStroke(1.5f));
		if (this.kindOfCurve == KindOfCurve.CALIBRATION)
		{
			xylineandshaperenderer_figure.setSeriesStroke(1, new BasicStroke(3.0f));
			xylineandshaperenderer_figure.setSeriesPaint(1, Color.WHITE);
		}

		figureGraphs.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		figureInnerPanel.setLayout(new FlowLayout());
		figureInnerPanel.add(new ChartPanel(figureGraphs, 700, 500, 700, 500, 700, 500, false, false, false, false, false, false));

		// --------------

		JPanel bottomPanel = new JPanel();
		GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
		gbc_bottomPanel.insets = new Insets(0, 0, 0, 5);
		gbc_bottomPanel.anchor = GridBagConstraints.WEST;
		gbc_bottomPanel.fill = GridBagConstraints.BOTH;
		gbc_bottomPanel.gridx = 0;
		gbc_bottomPanel.gridy = 1;
		gbc_bottomPanel.gridwidth = 2;
		getContentPane().add(bottomPanel, gbc_bottomPanel);

		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[] { 1200, 300 };
		gbl_bottomPanel.rowHeights = new int[] { 50, 50 };
		gbl_bottomPanel.columnWeights = new double[] { 0.0 };
		gbl_bottomPanel.rowWeights = new double[] { 0.0, 0.0 };
		bottomPanel.setLayout(gbl_bottomPanel);

		JPanel panelOut = new JPanel();
		GridBagLayout gbl_panelOut = new GridBagLayout();
		gbl_panelOut.columnWidths = new int[] { 140, 150, 140, 150, 140, 150, 330 };
		gbl_panelOut.rowHeights = new int[] { 0, 0 };
		gbl_panelOut.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panelOut.rowWeights = new double[] { 0.0, 0.0 };
		panelOut.setLayout(gbl_panelOut);
		GridBagConstraints gbc_panelOut = new GridBagConstraints();
		gbc_panelOut.fill = GridBagConstraints.BOTH;
		gbc_panelOut.insets = new Insets(0, 5, 0, 0);
		gbc_panelOut.gridx = 0;
		gbc_panelOut.gridy = 0;
		gbc_panelOut.gridheight = 2;
		bottomPanel.add(panelOut, gbc_panelOut);

		JLabel label1 = new JLabel("Slope Error Rms (\u03bcrad)");
		GridBagConstraints gbc_label1 = new GridBagConstraints();
		gbc_label1.fill = GridBagConstraints.BOTH;
		gbc_label1.anchor = GridBagConstraints.EAST;
		gbc_label1.insets = new Insets(5, 5, 5, 5);
		gbc_label1.gridx = 0;
		gbc_label1.gridy = 0;
		panelOut.add(label1, gbc_label1);

		slopeErrorRms = new JTextField(10);
		slopeErrorRms.setEditable(false);
		GridBagConstraints gbc_slopeErrorRms = new GridBagConstraints();
		gbc_slopeErrorRms.fill = GridBagConstraints.BOTH;
		gbc_slopeErrorRms.anchor = GridBagConstraints.WEST;
		gbc_slopeErrorRms.insets = new Insets(5, 5, 5, 5);
		gbc_slopeErrorRms.gridx = 1;
		gbc_slopeErrorRms.gridy = 0;
		panelOut.add(slopeErrorRms, gbc_slopeErrorRms);

		JLabel label2 = new JLabel("Figure Error Rms (nm)");
		GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.fill = GridBagConstraints.BOTH;
		gbc_label2.anchor = GridBagConstraints.EAST;
		gbc_label2.insets = new Insets(10, 5, 0, 5);
		gbc_label2.gridx = 0;
		gbc_label2.gridy = 1;
		panelOut.add(label2, gbc_label2);

		figureErrorRms = new JTextField(10);
		figureErrorRms.setEditable(false);
		GridBagConstraints gbc_figureErrorRms = new GridBagConstraints();
		gbc_figureErrorRms.fill = GridBagConstraints.BOTH;
		gbc_figureErrorRms.anchor = GridBagConstraints.WEST;
		gbc_figureErrorRms.insets = new Insets(5, 5, 5, 5);
		gbc_figureErrorRms.gridx = 1;
		gbc_figureErrorRms.gridy = 1;
		panelOut.add(figureErrorRms, gbc_figureErrorRms);

		JLabel label3 = new JLabel("Radius of Curvature (mm)");
		GridBagConstraints gbc_label3 = new GridBagConstraints();
		gbc_label3.fill = GridBagConstraints.BOTH;
		gbc_label3.anchor = GridBagConstraints.EAST;
		gbc_label3.insets = new Insets(10, 5, 0, 5);
		gbc_label3.gridx = 2;
		gbc_label3.gridy = 0;
		panelOut.add(label3, gbc_label3);

		radiusOfCurvature = new JTextField(10);
		radiusOfCurvature.setEditable(false);
		GridBagConstraints gbc_radiusOfCurvature = new GridBagConstraints();
		gbc_radiusOfCurvature.fill = GridBagConstraints.BOTH;
		gbc_radiusOfCurvature.anchor = GridBagConstraints.WEST;
		gbc_radiusOfCurvature.insets = new Insets(5, 5, 5, 5);
		gbc_radiusOfCurvature.gridx = 3;
		gbc_radiusOfCurvature.gridy = 0;
		panelOut.add(radiusOfCurvature, gbc_radiusOfCurvature);

		JLabel label4 = new JLabel("Tilt (\u03bcrad)");
		GridBagConstraints gbc_label4 = new GridBagConstraints();
		gbc_label4.fill = GridBagConstraints.BOTH;
		gbc_label4.anchor = GridBagConstraints.EAST;
		gbc_label4.insets = new Insets(10, 5, 0, 5);
		gbc_label4.gridx = 2;
		gbc_label4.gridy = 1;
		panelOut.add(label4, gbc_label4);

		tiltValue = new JTextField(10);
		tiltValue.setEditable(false);
		GridBagConstraints gbc_tiltValue = new GridBagConstraints();
		gbc_tiltValue.fill = GridBagConstraints.BOTH;
		gbc_tiltValue.anchor = GridBagConstraints.WEST;
		gbc_tiltValue.insets = new Insets(5, 5, 5, 5);
		gbc_tiltValue.gridx = 3;
		gbc_tiltValue.gridy = 1;
		panelOut.add(tiltValue, gbc_tiltValue);

		if (this.kindOfCurve == KindOfCurve.ELLIPSE)
		{
			JLabel label5 = new JLabel("a (mm)");
			GridBagConstraints gbc_label5 = new GridBagConstraints();
			gbc_label5.fill = GridBagConstraints.BOTH;
			gbc_label5.anchor = GridBagConstraints.EAST;
			gbc_label5.insets = new Insets(10, 5, 0, 5);
			gbc_label5.gridx = 4;
			gbc_label5.gridy = 0;
			panelOut.add(label5, gbc_label5);

			a = new JTextField(10);
			a.setEditable(false);
			GridBagConstraints gbc_a = new GridBagConstraints();
			gbc_a.fill = GridBagConstraints.BOTH;
			gbc_a.anchor = GridBagConstraints.WEST;
			gbc_a.insets = new Insets(5, 5, 5, 5);
			gbc_a.gridx = 5;
			gbc_a.gridy = 0;
			panelOut.add(a, gbc_a);

			JLabel label6 = new JLabel("b (mm)");
			GridBagConstraints gbc_label6 = new GridBagConstraints();
			gbc_label6.fill = GridBagConstraints.BOTH;
			gbc_label6.anchor = GridBagConstraints.EAST;
			gbc_label6.insets = new Insets(10, 5, 0, 5);
			gbc_label6.gridx = 4;
			gbc_label6.gridy = 1;
			panelOut.add(label6, gbc_label6);

			b = new JTextField(10);
			b.setEditable(false);
			GridBagConstraints gbc_b = new GridBagConstraints();
			gbc_b.fill = GridBagConstraints.BOTH;
			gbc_b.anchor = GridBagConstraints.WEST;
			gbc_b.insets = new Insets(5, 5, 5, 5);
			gbc_b.gridx = 5;
			gbc_b.gridy = 1;
			panelOut.add(b, gbc_b);
		}
		else if (this.kindOfCurve == KindOfCurve.CALIBRATION)
		{
			JLabel label5 = new JLabel("Slope Error Rms B (\u03bcrad)");
			GridBagConstraints gbc_label5 = new GridBagConstraints();
			gbc_label5.fill = GridBagConstraints.BOTH;
			gbc_label5.anchor = GridBagConstraints.EAST;
			gbc_label5.insets = new Insets(10, 5, 0, 5);
			gbc_label5.gridx = 4;
			gbc_label5.gridy = 0;
			panelOut.add(label5, gbc_label5);

			slopeErrorRms_b = new JTextField(10);
			slopeErrorRms_b.setEditable(false);
			GridBagConstraints gbc_slopeErrorRms_b = new GridBagConstraints();
			gbc_slopeErrorRms_b.fill = GridBagConstraints.BOTH;
			gbc_slopeErrorRms_b.anchor = GridBagConstraints.WEST;
			gbc_slopeErrorRms_b.insets = new Insets(5, 5, 5, 5);
			gbc_slopeErrorRms_b.gridx = 5;
			gbc_slopeErrorRms_b.gridy = 0;
			panelOut.add(slopeErrorRms_b, gbc_slopeErrorRms_b);

			JLabel label6 = new JLabel("Figure Error Rms B (nm)");
			GridBagConstraints gbc_label6 = new GridBagConstraints();
			gbc_label6.fill = GridBagConstraints.BOTH;
			gbc_label6.anchor = GridBagConstraints.EAST;
			gbc_label6.insets = new Insets(10, 5, 0, 5);
			gbc_label6.gridx = 4;
			gbc_label6.gridy = 1;
			panelOut.add(label6, gbc_label6);

			figureErrorRms_b = new JTextField(10);
			figureErrorRms_b.setEditable(false);
			GridBagConstraints gbc_figureErrorRms_b = new GridBagConstraints();
			gbc_figureErrorRms_b.fill = GridBagConstraints.BOTH;
			gbc_figureErrorRms_b.anchor = GridBagConstraints.WEST;
			gbc_figureErrorRms_b.insets = new Insets(5, 5, 5, 5);
			gbc_figureErrorRms_b.gridx = 5;
			gbc_figureErrorRms_b.gridy = 1;
			panelOut.add(figureErrorRms_b, gbc_figureErrorRms_b);
		}

		// --------------------

		JButton calculateButton = new JButton("Save Data");
		calculateButton.setActionCommand(ActionCommands.SAVE_DATA);
		calculateButton.addActionListener(this);
		GridBagConstraints gbc_calculateButton = new GridBagConstraints();
		gbc_calculateButton.fill = GridBagConstraints.BOTH;
		gbc_calculateButton.insets = new Insets(0, 0, 5, 5);
		gbc_calculateButton.gridx = 1;
		gbc_calculateButton.gridy = 0;
		bottomPanel.add(calculateButton, gbc_calculateButton);

		JButton exitButton = new JButton("Close");
		exitButton.setActionCommand(ActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.insets = new Insets(0, 0, 10, 5);
		gbc_exitButton.gridx = 1;
		gbc_exitButton.gridy = 1;
		bottomPanel.add(exitButton, gbc_exitButton);

		// --------------------------------------------------------------------

		if (this.kindOfCurve == KindOfCurve.SPHERE)
			this.calculateSphere();
		else if (this.kindOfCurve == KindOfCurve.ELLIPSE)
			this.populateEllipse();
		else if (this.kindOfCurve == KindOfCurve.ELLIPSE_FIXED)
			this.calculateEllipseFixed();
		else if (this.kindOfCurve == KindOfCurve.CALIBRATION)
			this.populateCalibration();

		// --------------------------------------------------------------------
	}

	public void manageOtherActions(ActionEvent event)
	{
		try
		{
			if (event.getActionCommand().equals(ActionCommands.SAVE_DATA))
			{
				this.saveData();
			}
		}
		catch (Exception e)
		{
			GuiUtilities.showErrorPopup("Exception captured: " + e.getClass().getName() + " - " + e.getMessage(), (JPanel) this.getContentPane().getComponent(0));
		}

	}

	private void resetPanel()
	{
		this.xyDataset_figure.getSeries(0).clear();
		this.xyDataset_slope.getSeries(0).clear();
		if (this.kindOfCurve == KindOfCurve.CALIBRATION)
			this.xyDataset_slope.getSeries(1).clear();

		this.plot_figure.getRangeAxis(0).setLowerBound(0);
		this.plot_figure.getRangeAxis(0).setUpperBound(10);
		this.plot_figure.getDomainAxis(0).setLowerBound(-10);
		this.plot_figure.getDomainAxis(0).setUpperBound(10);

		this.plot_slope.getRangeAxis(0).setLowerBound(0);
		this.plot_slope.getRangeAxis(0).setUpperBound(10);
		this.plot_slope.getDomainAxis(0).setLowerBound(-10);
		this.plot_slope.getDomainAxis(0).setUpperBound(10);
	}

	private static void plotData(double[] x, double[] y, XYSeries series, XYPlot plot, double minX, double maxX, double minY, double maxY)
	{
		for (int index = 0; index < x.length; index++)
		{
			maxY = (y[index] > maxY) ? y[index] : maxY;
			minY = (y[index] < minY) ? y[index] : minY;

			series.add(x[index], y[index]);
		}

		minX = x[0] < minX ? x[0] : minX;
		maxX = x[x.length - 1] > maxX ? x[x.length - 1] : maxX;
		double delta = x[0] - x[1];

		plot.getRangeAxis(0).setLowerBound(minY * 1.1);
		plot.getRangeAxis(0).setUpperBound(maxY * 1.1);
		plot.getDomainAxis(0).setLowerBound(minX - delta);
		plot.getDomainAxis(0).setUpperBound(maxX - delta);
	}

	private void saveData() throws IOException
	{
		String fileOut = GuiUtilities.showDatFileChooser(null, "RMS_Calculation_output.dat");

		if (!fileOut.isEmpty())
		{
			boolean proceed = true;

			File file = new File(fileOut);
			if (file.exists())
				proceed = GuiUtilities.showConfirmPopup("Confirm Overwriting File?", null);

			if (proceed)
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));

				try
				{
					XYSeries series_slope = this.xyDataset_slope.getSeries(0);
					XYSeries series_figure = this.xyDataset_figure.getSeries(0);

					writer.write("Slope  Error RMS   (urad): " + this.slopeErrorRms.getText());
					writer.newLine();
					writer.write("Figure Error RMS     (nm): " + this.figureErrorRms.getText());
					writer.newLine();
					writer.write("Radius of Curvature  (mm): " + this.radiusOfCurvature.getText());
					writer.newLine();
					writer.write("Tilt               (urad): " + this.tiltValue.getText());
					writer.newLine();

					if (this.kindOfCurve == KindOfCurve.ELLIPSE)
					{
						writer.write("a                    (mm): " + this.a.getText());
						writer.newLine();
						writer.write("b                    (mm): " + this.b.getText());
						writer.newLine();
					}
					else if (this.kindOfCurve == KindOfCurve.CALIBRATION)
					{
						writer.write("Slope  Error RMS B (urad): " + this.slopeErrorRms_b.getText());
						writer.newLine();
						writer.write("Figure Error RMS B   (nm): " + this.figureErrorRms_b.getText());
						writer.newLine();
					}

					writer.newLine();
					writer.write("Position  Slope Error  Figure Error");
					writer.newLine();
					writer.write("------------------------------------------------");
					writer.newLine();

					int nItems = series_slope.getItemCount();

					for (int index = 0; index < nItems; index++)
					{
						writer.write(GuiUtilities.parseDouble(series_slope.getX(index).doubleValue()).trim() + " "
						    + String.format("%9.4f", series_slope.getY(index).doubleValue()) + " " + String.format("%9.4f", series_figure.getY(index).doubleValue()));
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
		}
	}

	@SuppressWarnings("unchecked")
	private void populateCalibration()
	{
		HashMap<String, double[]> calibrationParameters = (HashMap<String, double[]>) this.parameters;

		double[] headers = calibrationParameters.get("headers");
		double[] headers_b = calibrationParameters.get("headers_b");

		this.slopeErrorRms.setText(GuiUtilities.parseDouble(headers[0]));
		this.figureErrorRms.setText(GuiUtilities.parseDouble(headers[1]));
		this.tiltValue.setText(GuiUtilities.parseDouble(headers[2]));
		this.radiusOfCurvature.setText(GuiUtilities.parseDouble(headers[3]));
		this.slopeErrorRms_b.setText(GuiUtilities.parseDouble(headers_b[0]));
		this.figureErrorRms_b.setText(GuiUtilities.parseDouble(headers_b[1]));

		double[] x = calibrationParameters.get("x");
		double[] slopeError = calibrationParameters.get("rms_s");
		double[] h = calibrationParameters.get("rms_h");
		double[] x_b = calibrationParameters.get("x_b");
		double[] slopeError_b = calibrationParameters.get("rms_s_b");
		double[] h_b = calibrationParameters.get("rms_h_b");

		plotData(x, slopeError, this.xyDataset_slope.getSeries(0), this.plot_slope, x[0], x[x.length - 1], slopeError[0], slopeError[0]);
		plotData(x, h, this.xyDataset_figure.getSeries(0), this.plot_figure, x[0], x[x.length - 1], h[0], h[0]);
		plotData(x_b, slopeError_b, this.xyDataset_slope.getSeries(1), this.plot_slope, this.xyDataset_slope.getSeries(0).getMinX(), this.xyDataset_slope
		    .getSeries(0).getMaxX(), this.xyDataset_slope.getSeries(0).getMinY(), this.xyDataset_slope.getSeries(0).getMaxY());
		plotData(x_b, h_b, this.xyDataset_figure.getSeries(1), this.plot_figure, this.xyDataset_figure.getSeries(0).getMinX(), this.xyDataset_figure.getSeries(0)
		    .getMaxX(), this.xyDataset_figure.getSeries(0).getMinY(), this.xyDataset_figure.getSeries(0).getMaxY());
	}

	@SuppressWarnings("unchecked")
	private void populateEllipse()
	{
		HashMap<String, double[]> calibrationParameters = (HashMap<String, double[]>) this.parameters;

		double[] headers = calibrationParameters.get("headers");

		this.slopeErrorRms.setText(GuiUtilities.parseDouble(headers[0]));
		this.figureErrorRms.setText(GuiUtilities.parseDouble(headers[1]));
		this.tiltValue.setText(GuiUtilities.parseDouble(headers[2]));
		this.radiusOfCurvature.setText(GuiUtilities.parseDouble(headers[3]));
		this.a.setText(GuiUtilities.parseDouble(headers[4]));
		this.b.setText(GuiUtilities.parseDouble(headers[5]));

		double[] x = calibrationParameters.get("x");
		double[] slopeError = calibrationParameters.get("rms_s");
		double[] h = calibrationParameters.get("rms_h");

		plotData(x, slopeError, this.xyDataset_slope.getSeries(0), this.plot_slope, x[0], x[x.length - 1], slopeError[0], slopeError[0]);
		plotData(x, h, this.xyDataset_figure.getSeries(0), this.plot_figure, x[0], x[x.length - 1], h[0], h[0]);
	}

	@SuppressWarnings("unchecked")
	private void calculateEllipseFixed() throws NumberFormatException, IOException
	{
		this.resetPanel();

		HashMap<String, double[]> ellipseParameters = (HashMap<String, double[]>) this.parameters;

		double[] x = ellipseParameters.get("x");
		double[] z = ellipseParameters.get("y");
		double[] h = new double[x.length];

		String pythonFolder = FileIni.getInstance().getProperty(LPTScanPanel.PYTHON_FOLDER);
		String ellipseFile = GuiUtilities.showDatFileChooser(pythonFolder, null, "ellipse_fixed.dat");

		if (!ellipseFile.isEmpty())
		{
			this.loadFixedEllipse(ellipseFile, ellipseParameters);

			double[] se = ellipseParameters.get("se");

			SimpleRegression regression = new SimpleRegression();

			for (int index = 0; index < x.length; index++)
				regression.addData(x[index], z[index]);

			double fitSlope = regression.getSlope();

			double[] slopeError = new double[x.length];
			double tilt = sum(z, z.length) / z.length;
			double tilt_e = sum(se, se.length) / se.length;

			for (int index = 0; index < x.length; index++)
				slopeError[index] = ((z[index] - tilt) - (se[index] - tilt_e)) * 1000000; // to

			this.slopeErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(slopeError) / slopeError.length)));
			this.radiusOfCurvature.setText(GuiUtilities.parseDouble(1 / fitSlope));
			this.tiltValue.setText(GuiUtilities.parseDouble(tilt * 1000000));

			double dx = (x[1] - x[0]) * 1e6; // to nm

			for (int index = 0; index < x.length; index++)
				h[index] = dx * (sum(slopeError, index) / 1000000);

			this.figureErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(h) / h.length)));

			plotData(x, slopeError, this.xyDataset_slope.getSeries(0), plot_slope, x[0], x[x.length - 1], slopeError[0], slopeError[0]);
			plotData(x, h, this.xyDataset_figure.getSeries(0), plot_figure, x[0], x[x.length - 1], h[0], h[0]);
		}
		else
		{
			this.dispose();
			this.setVisible(false);
		}
	}

	private void loadFixedEllipse(String ellipseFile, HashMap<String, double[]> ellipseParameters) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(ellipseFile));

		String row = reader.readLine();

		Vector<Double> xe_v = new Vector<>();
		Vector<Double> se_v = new Vector<>();

		while (row != null)
		{
			row = row.trim();

			if (!(row.isEmpty() || row.startsWith("!")))
			{
				MeasurePoint measurePoint = new MeasurePoint(row);

				xe_v.add(Double.valueOf(measurePoint.getX()));
				se_v.add(Double.valueOf(measurePoint.getMeasure()));
			}

			row = reader.readLine();
		}

		reader.close();

		double[] x = ellipseParameters.get("x");

		if (x.length != xe_v.size())
			throw new IllegalArgumentException("Scan and Fixed Ellipse data file should have the same number of points");

		double[] xe = toPrimitive(xe_v.toArray(new Double[xe_v.size()]));
		double[] se = toPrimitive(se_v.toArray(new Double[xe_v.size()]));

		if (((int) x[0] != (int) xe[0]) && ((int) x[x.length - 1] != (int) xe[xe.length - 1]))
			throw new IllegalArgumentException("Scan and Fixed Ellipse data do not have the same x range");

		ellipseParameters.put("xe", xe);
		ellipseParameters.put("se", se);
	}

	private double[] toPrimitive(Double[] array)
	{
		double[] outArray = new double[array.length];

		for (int i = 0; i < array.length; i++)
			outArray[i] = array[i].doubleValue();

		return outArray;
	}

	// ------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void calculateSphere() throws NumberFormatException, IOException
	{
		this.resetPanel();

		HashMap<String, double[]> sphereParameters = (HashMap<String, double[]>) this.parameters;

		double[] x = sphereParameters.get("x");
		double[] z = sphereParameters.get("y");
		double[] h = new double[x.length];

		SimpleRegression regression = new SimpleRegression();

		for (int index = 0; index < x.length; index++)
			regression.addData(x[index], z[index]);

		double fitIntercept = regression.getIntercept();
		double fitSlope = regression.getSlope();

		double yfit = 0.0;
		double[] slopeError = new double[x.length];

		for (int index = 0; index < x.length; index++)
		{
			yfit = fitIntercept + fitSlope * x[index];

			slopeError[index] = (z[index] - yfit) * 1000000; // to urad
		}

		double tilt = (sum(z, z.length) / z.length);

		this.slopeErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(slopeError) / slopeError.length)));
		this.radiusOfCurvature.setText(GuiUtilities.parseDouble(1 / fitSlope));
		this.tiltValue.setText(GuiUtilities.parseDouble(tilt * 1000000));

		double dx = (x[1] - x[0]) * 1e6; // to nm

		for (int index = 0; index < x.length; index++)
			h[index] = dx * (sum(slopeError, index) / 1000000);

		this.figureErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(h) / h.length)));

		plotData(x, slopeError, this.xyDataset_slope.getSeries(0), plot_slope, x[0], x[x.length - 1], slopeError[0], slopeError[0]);
		plotData(x, h, this.xyDataset_figure.getSeries(0), plot_figure, x[0], x[x.length - 1], h[0], h[0]);

	}

	private double sum(double[] array, int max_index)
	{
		double sum = 0.0;

		for (int i = 0; i < max_index; i++)
			sum += array[i];

		return sum;
	}

	private double squareSum(double[] array)
	{
		double sum = 0.0;

		for (int i = 0; i < array.length; i++)
			sum += Math.pow(array[i], 2);

		return sum;
	}
}
