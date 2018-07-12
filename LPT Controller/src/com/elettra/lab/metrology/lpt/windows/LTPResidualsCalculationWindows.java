package com.elettra.lab.metrology.lpt.windows;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.metrology.lpt.panels.References;

public class LTPResidualsCalculationWindows extends AbstractGenericFrame
{
	public static class KindOfCurve
	{
		public static final int	SPHERE		    = 0;
		public static final int	ELLIPSE		    = 1;
		public static final int	ELLIPSE_FIXED	= 2;
	}

	static class ActionCommands
	{
		private static final String	EXIT		  = "EXIT";
		private static final String	CALCULATE	= "CALCULATE";
	}

	private static final long	 serialVersionUID	= -513690344812082943L;

	private JTextField	       slopeErrorRms;
	private JTextField	       figureErrorRms;
	private JTextField	       radiusOfCurvature;
	private JTextField	       tiltValue;

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

		XYSeries series_slope = new XYSeries("Slope Error RMS");

		xyDataset_slope = new XYSeriesCollection();
		xyDataset_slope.addSeries(series_slope);

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

		JPanel panelOut = new JPanel();
		GridBagLayout gbl_panelOut = new GridBagLayout();
		gbl_panelOut.columnWidths = new int[] { 140, 150, 140, 150, 620 };
		gbl_panelOut.rowHeights = new int[] { 0, 0 };
		gbl_panelOut.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
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

		if (this.kindOfCurve == KindOfCurve.SPHERE)
		{
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
		}

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

		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[] { 1200, 300 };
		gbl_bottomPanel.rowHeights = new int[] { 50, 50 };
		gbl_bottomPanel.columnWeights = new double[] { 0.0 };
		gbl_bottomPanel.rowWeights = new double[] { 0.0, 0.0 };
		bottomPanel.setLayout(gbl_bottomPanel);

		JButton calculateButton = new JButton("Calculate");
		calculateButton.setActionCommand(ActionCommands.CALCULATE);
		calculateButton.addActionListener(this);
		GridBagConstraints gbc_calculateButton = new GridBagConstraints();
		gbc_calculateButton.fill = GridBagConstraints.BOTH;
		gbc_calculateButton.insets = new Insets(0, 0, 5, 5);
		gbc_calculateButton.gridx = 1;
		gbc_calculateButton.gridy = 0;
		bottomPanel.add(calculateButton, gbc_calculateButton);

		JButton exitButton = new JButton("EXIT");
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
		{
			this.calculateSphere();
		}

		// --------------------------------------------------------------------
	}

	public void manageOtherActions(ActionEvent event)
	{
		try
		{
			if (event.getActionCommand().equals(ActionCommands.CALCULATE))
			{
				if (this.kindOfCurve == KindOfCurve.SPHERE)
				{
					this.calculateSphere();
				}
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

		this.plot_figure.getRangeAxis(0).setLowerBound(0);
		this.plot_figure.getRangeAxis(0).setUpperBound(10);
		this.plot_figure.getDomainAxis(0).setLowerBound(-10);
		this.plot_figure.getDomainAxis(0).setUpperBound(10);

		this.plot_slope.getRangeAxis(0).setLowerBound(0);
		this.plot_slope.getRangeAxis(0).setUpperBound(10);
		this.plot_slope.getDomainAxis(0).setLowerBound(-10);
		this.plot_slope.getDomainAxis(0).setUpperBound(10);
	}

	private static void plotData(double[] x, double[] y, XYSeries series, XYPlot plot)
	{
		double maxValue = y[0];
		double minValue = y[0];
		
		for (int index = 0; index < x.length; index++)
		{
			maxValue = (y[index] > maxValue) ? y[index] : maxValue;
			minValue = (y[index] < minValue) ? y[index] : minValue;

			series.add(x[index], y[index]);
		}

		plot.getRangeAxis(0).setLowerBound(minValue * 1.1);
		plot.getRangeAxis(0).setUpperBound(maxValue * 1.1);
		plot.getDomainAxis(0).setLowerBound(x[0]);
		plot.getDomainAxis(0).setUpperBound(x[x.length-1]);
	}
	
	@SuppressWarnings("unchecked")
	private void calculateSphere() throws NumberFormatException, IOException
	{
		this.resetPanel();
		
		HashMap<String, double[]> sphereParameters = (HashMap<String, double[]>) this.parameters;

		double[] x = sphereParameters.get("x");
		double[] xc = sphereParameters.get("xc");

		double focalDistance = Double.parseDouble(References.getInstance().get(References.FOCAL_DISTANCE));
		double x0 = Double.parseDouble(References.getInstance().get(References.LTP_X_0));

		double b = 0.0;
		double[] correction = new double[x.length];
		double[] z = new double[x.length];
		double[] h = new double[x.length];

		// TODO: add calculation of correction

		for (int index = 0; index < x.length; index++)
		{
			b = (xc[index] - x0) / 1000;
			z[index] = 0.5 * Math.atan(b / focalDistance) - correction[index];
		}
		
		SimpleRegression regression = new SimpleRegression();
		
		for (int index = 0; index < x.length; index++)
			regression.addData(x[index], z[index]);

		double fitIntercept = regression.getIntercept();
		double fitSlope = regression.getSlope();

		double yfit = 0.0;
		double[] slopeError = new double[x.length];
		
		for (int index = 0; index < x.length; index++)
		{
			yfit = fitIntercept + fitSlope*x[index];	
			
			slopeError[index] = (z[index] - yfit)*1000000; // to urad
		}	
		
		this.slopeErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(slopeError)/slopeError.length)));
		this.radiusOfCurvature.setText(GuiUtilities.parseDouble(1/fitSlope));
		this.tiltValue.setText(GuiUtilities.parseDouble((sum(z, z.length)/z.length)*1000000));
		
		double dx = (x[1] - x[0])*1e6; // to nm

		for (int index = 0; index < x.length; index++)
			h[index] = dx*(sum(slopeError, index)/1000000);

		this.figureErrorRms.setText(GuiUtilities.parseDouble(Math.sqrt(squareSum(h)/h.length)));
		
		plotData(x, slopeError, this.xyDataset_slope.getSeries(0), plot_slope);
		plotData(x, h, this.xyDataset_figure.getSeries(0), plot_figure);

	}
	
	private double sum(double[] array, int max_index)
	{
		double sum = 0.0;
				
    for(int i = 0; i < max_index; i++)
    	sum += array[i];
			
		return sum;
	}
	
	private double squareSum(double[] array)
	{
		double sum = 0.0;
				
    for(int i = 0; i < array.length; i++)
    	sum += Math.pow(array[i], 2);
			
		return sum;
	}
}
