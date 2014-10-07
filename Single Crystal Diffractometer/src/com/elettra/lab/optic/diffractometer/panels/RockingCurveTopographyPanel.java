package com.elettra.lab.optic.diffractometer.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.chart.controllers.thread.ChartThreadController;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;

import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.fit.IFitResultListener;
import com.elettra.controller.gui.fit.MovementMatrix;
import com.elettra.controller.gui.fit.PlottableFitResult;
import com.elettra.lab.optic.diffractometer.Axis;

public class RockingCurveTopographyPanel extends JPanel implements ActionListener, IFitResultListener
{
	public class Coordinates
	{
		private double x;
		private double z;

		public Coordinates(double x, double z)
		{
			super();
			this.x = x;
			this.z = z;
		}

		public double getX()
		{
			return this.x;
		}

		public double getZ()
		{
			return this.z;
		}

		public void setX(double x)
		{
			this.x = x;
		}

		public void setZ(double z)
		{
			this.z = z;
		}
	}

	static class ActionCommands
	{
		private static final String CLEAN  = "CLEAN";
		private static final String EXPORT = "EXPORT";
	}

	private HashMap<Coordinates, Coord3d> peakIntensityData;
	private HashMap<Coordinates, Coord3d> peakFWHMData;
	private HashMap<Coordinates, Coord3d> peakPositionData;

	/**
   * 
   */
	private static final long             serialVersionUID = 5658088935775992753L;
	private JPanel                        peakIntensityPanel;
	private JPanel                        peakFWHMPanel;
	private JPanel                        braggAnglePositionPanel;

	/**
	 * Create the panel.
	 */
	public RockingCurveTopographyPanel()
	{
		ListenerRegister.getInstance().addFitResultListener(Axis.THETA, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 850, 150, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		this.setBounds(new Rectangle(0, 0, 1000, 845));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 0, 5);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);

		this.peakIntensityData = new HashMap<Coordinates, Coord3d>();
		this.peakFWHMData = new HashMap<Coordinates, Coord3d>();
		this.peakPositionData = new HashMap<Coordinates, Coord3d>();

		peakIntensityPanel = new JPanel();
		tabbedPane.addTab("Peak Intensity", null, peakIntensityPanel, null);
		peakIntensityPanel.setLayout(new BorderLayout(0, 0));
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		this.addChart(peakIntensityPanel, this.getChart(this.getEmptyCoordinates()), "Peak Intensity", "Counts");

		peakFWHMPanel = new JPanel();
		tabbedPane.addTab("Peak FWHM", null, peakFWHMPanel, null);
		peakFWHMPanel.setLayout(new BorderLayout(0, 0));
		tabbedPane.setForegroundAt(1, new Color(0, 102, 51));

		this.addChart(peakFWHMPanel, this.getChart(this.getEmptyCoordinates()), "Peak FWHM", "FWHM (deg)");

		braggAnglePositionPanel = new JPanel();
		tabbedPane.addTab("Bragg Angle Position", null, braggAnglePositionPanel, null);
		braggAnglePositionPanel.setLayout(new BorderLayout(0, 0));
		tabbedPane.setForegroundAt(2, new Color(0, 102, 51));

		this.addChart(braggAnglePositionPanel, this.getChart(this.getEmptyCoordinates()), "Bragg Angle Position", "2Theta (deg)");

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JButton cleanPlotsButton = new JButton("CLEAN THE PLOTS");
		cleanPlotsButton.addActionListener(this);
		cleanPlotsButton.setActionCommand(ActionCommands.CLEAN);
		GridBagConstraints gbc_cleanPlotsButton = new GridBagConstraints();
		gbc_cleanPlotsButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_cleanPlotsButton.insets = new Insets(20, 10, 5, 0);
		gbc_cleanPlotsButton.gridx = 0;
		gbc_cleanPlotsButton.gridy = 0;
		panel.add(cleanPlotsButton, gbc_cleanPlotsButton);

		JButton exportDataButton = new JButton("EXPORT DATA");
		exportDataButton.addActionListener(this);
		exportDataButton.setActionCommand(ActionCommands.EXPORT);
		GridBagConstraints gbc_exportDataButton = new GridBagConstraints();
		gbc_exportDataButton.insets = new Insets(0, 10, 0, 0);
		gbc_exportDataButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_exportDataButton.gridx = 0;
		gbc_exportDataButton.gridy = 1;
		panel.add(exportDataButton, gbc_exportDataButton);
	}

	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(ActionCommands.CLEAN))
		{
			this.peakIntensityData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();

			this.addChart(peakIntensityPanel, this.getChart(this.getEmptyCoordinates()), "Peak Intensity", "Counts");

			// ------------------------------------ FWHM

			this.peakFWHMData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();

			this.addChart(peakFWHMPanel, this.getChart(this.getEmptyCoordinates()), "Peak FWHM", "FWHM (deg)");

			// ------------------------------------ Position

			this.peakPositionData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();

			this.addChart(braggAnglePositionPanel, this.getChart(this.getEmptyCoordinates()), "Bragg Angle Position", "2Theta (deg)");
		}
		else if (event.getActionCommand().equals(ActionCommands.EXPORT))
		{
		}
	}

	public synchronized void signalFitResult(int axis, PlottableFitResult fitResult)
	{
		Coordinates point = new Coordinates(fitResult.getFirstCoordinate(), fitResult.getSecondCoordinate());

		Coord3d coordIntensity = new Coord3d(fitResult.getFirstCoordinate(), fitResult.getSecondCoordinate(), fitResult.getPeakIntensity());
		Coord3d coordFWHM = new Coord3d(fitResult.getFirstCoordinate(), fitResult.getSecondCoordinate(), fitResult.getPeakFWHM());
		Coord3d coordPosition = new Coord3d(fitResult.getFirstCoordinate(), fitResult.getSecondCoordinate(), fitResult.getPeakPosition());

		// ------------------------------------ Intensity

		this.peakIntensityData.put(point, coordIntensity);

		Iterator<Coord3d> iteratorIntensity = peakIntensityData.values().iterator();

		List<Coord3d> peakIntensityList = new ArrayList<Coord3d>(this.peakIntensityData.size());
		while (iteratorIntensity.hasNext())
			peakIntensityList.add(iteratorIntensity.next());

		this.addChart(peakIntensityPanel, this.getChart(peakIntensityList), "Peak Intensity", "Counts");

		// ------------------------------------ FWHM

		this.peakFWHMData.put(point, coordFWHM);

		Iterator<Coord3d> iteratorFWHM = peakFWHMData.values().iterator();

		List<Coord3d> peakFWHMList = new ArrayList<Coord3d>(this.peakFWHMData.size());
		while (iteratorFWHM.hasNext())
			peakFWHMList.add(iteratorFWHM.next());

		this.addChart(peakFWHMPanel, this.getChart(peakFWHMList), "Peak FWHM", "FWHM (deg)");

		// ------------------------------------ Position

		this.peakPositionData.put(point, coordPosition);

		Iterator<Coord3d> iteratorPosition = peakPositionData.values().iterator();

		List<Coord3d> peakPositionList = new ArrayList<Coord3d>(this.peakPositionData.size());
		while (iteratorPosition.hasNext())
			peakPositionList.add(iteratorPosition.next());

		this.addChart(braggAnglePositionPanel, this.getChart(peakPositionList), "Bragg Angle Position", "2Theta (deg)");
	}

	public void signalInitialization(int axis, MovementMatrix matrix)
	{
		List<Coord3d> initialization = this.getMatrixOfCoordinates(matrix);

		this.peakIntensityData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();
		this.peakFWHMData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();
		this.peakPositionData = new HashMap<RockingCurveTopographyPanel.Coordinates, Coord3d>();

		Iterator<Coord3d> iterator = initialization.iterator();

		while (iterator.hasNext())
		{
			Coord3d element = iterator.next();
			Coordinates point = new Coordinates(element.x, element.y);

			this.peakIntensityData.put(point, element);
			this.peakFWHMData.put(point, element);
			this.peakPositionData.put(point, element);
		}

		// ------------------------------------

		this.addChart(peakIntensityPanel, this.getChart(initialization), "Peak Intensity", "Counts");
		this.addChart(peakFWHMPanel, this.getChart(initialization), "Peak FWHM", "FWHM (deg)");
		this.addChart(braggAnglePositionPanel, this.getChart(initialization), "Bragg Angle Position", "2Theta (deg)");
	}

	private Chart getChart(List<Coord3d> coordinates)
	{
		// Create the object to represent the function over the given range.
		final Shape surface = (Shape) Builder.buildDelaunay(coordinates);

		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new org.jzy3d.colors.Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(org.jzy3d.colors.Color.BLACK);

		// Create a chart that updates the surface colormapper when scaling changes
		Chart chart = new Chart(Quality.Advanced);
		chart.getScene().getGraph().add(surface);

		// Setup a colorbar for the surface object and add it to the scene
		surface.setLegend(new ColorbarLegend(surface, chart.getView().getAxe().getLayout()));

		return chart;
	}

	private void addChart(final JPanel panel, final Chart chart, final String title, final String zLabel)
	{
		// Setup chart controllers and listeners
		ChartMouseController mouse = new ChartMouseController();
		chart.addController(mouse);

		ChartThreadController thread = new ChartThreadController();
		mouse.addSlaveThreadController(thread);
		chart.addController(thread);

		// trigger screenshot on 's' letter
		chart.getCanvas().addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				switch (e.getKeyChar())
				{
					case 's':
						try
						{
							String fileName = "./data/screenshots/" + title + ".png";

							RockingCurveTopographyPanel.screenshot(chart, fileName);

							GuiUtilities.showMessagePopup("Screenshot of the plot published in: " + fileName, panel);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					default:
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});

		chart.getAxeLayout().setXAxeLabel("X (mm)");
		chart.getAxeLayout().setYAxeLabel("Z (mm)");
		chart.getAxeLayout().setZAxeLabel(zLabel);

		chart.render();

		panel.removeAll();
		panel.add((java.awt.Component) chart.getCanvas(), BorderLayout.CENTER);
	}

	protected static void screenshot(Chart chart, String filename) throws IOException
	{
		File output = new File(filename);

		if (!output.getParentFile().exists())
			output.mkdirs();

		ImageIO.write(chart.screenshot(), "png", output);
	}

	private List<Coord3d> getEmptyCoordinates()
	{
		List<Coord3d> coordinates = new ArrayList<Coord3d>();

		coordinates.add(new Coord3d(-1, 0, 0));
		coordinates.add(new Coord3d(0, 0, 0));
		coordinates.add(new Coord3d(1, 0, 0));

		coordinates.add(new Coord3d(-1, -1, 0));
		coordinates.add(new Coord3d(-1, 1, 0));

		coordinates.add(new Coord3d(0, -1, 0));
		coordinates.add(new Coord3d(0, 1, 0));

		coordinates.add(new Coord3d(1, -1, 0));
		coordinates.add(new Coord3d(1, 1, 0));

		return coordinates;
	}

	private synchronized List<Coord3d> getMatrixOfCoordinates(MovementMatrix matrix)
	{
		List<Coord3d> list = new ArrayList<Coord3d>(matrix.getRows() * matrix.getColumns());

		double xIncrement = Double.valueOf(GuiUtilities.parseDouble(Math.abs(matrix.getxMax() - matrix.getxMin()) / matrix.getRows()));
		double zIncrement = Double.valueOf(GuiUtilities.parseDouble(Math.abs(matrix.getzMax() - matrix.getzMin()) / matrix.getColumns()));

		double xCurrent = 0.0;
		double zCurrent = 0.0;

		for (int row = 0; row < matrix.getRows(); row++)
		{
			if (row == matrix.getRows() - 1)
				xCurrent = matrix.getxMax() - (xIncrement / 2);
			else
				xCurrent = matrix.getxMin() + (xIncrement / 2) + row * xIncrement;

			for (int column = 0; column < matrix.getColumns(); column++)
			{
				if (column == matrix.getColumns() - 1)
					zCurrent = matrix.getzMax() - (zIncrement / 2);
				else
					zCurrent = matrix.getzMin() + (zIncrement / 2) + column * zIncrement;

				list.add(new Coord3d(xCurrent, zCurrent, 0.0));
			}
		}

		return list;
	}
}
