package com.elettra.lab.metrology.lpt.panels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jfree.data.xy.XYSeries;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ScanParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.programs.LPTScanProgram;
import com.elettra.lab.metrology.lpt.windows.LTPResidualsCalculationWindows;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LPTScanPanel extends ScanPanel
{
	public static final String	PYTHON_FOLDER	            = "PYTHON_FOLDER";
	public static final String	CALIBRATION_PYTHON_SCRIPT	= "CALIBRATION_PYTHON_SCRIPT";
	public static final String	ELLIPSE_PYTHON_SCRIPT	    = "ELLIPSE_PYTHON_SCRIPT";
	public static final String	PYTHON_EXE	              = "PYTHON_EXE";

	private static final double	HEIGHT_TO_WIDTH_RATIO	    = ((double) IIDSCCD.DIM_Y / (double) IIDSCCD.DIM_X);

	private JLabel	            imageLabel;

	/**
	 * 
	 */
	private static final long	  serialVersionUID	        = -1195319609096497524L;

	private JTextField	        centroid_x_position;
	private JTextField	        centroid_y_position;
	private JTextField	        numberOfCaptures;
	protected JCheckBox	        renderCheckBox;
	private JSlider	            gainSlider;
	private JComboBox	          colorModeCombo;

	private BufferedImage	      imageEnabled;
	private BufferedImage	      imageDisabled;

	public LPTScanPanel(int axis, ICommunicationPort port, boolean sendDataEnabled) throws CommunicationPortException
	{
		super(axis, port, sendDataEnabled);

		try
		{
			imageEnabled = ImageIO.read(new File("Files/enabled.png"));
			imageDisabled = ImageIO.read(new File("Files/disabled.png"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}

		GridBagLayout gridBagLayout = (GridBagLayout) this.getLayout();

		gridBagLayout.columnWidths = new int[] { 700, 290 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0, 0 };
		gridBagLayout.rowWeights = new double[] { 2, 58, 15, 25, Double.MIN_VALUE };

		this.setLayout(gridBagLayout);

		JPanel imagePanel = new JPanel();
		JTabbedPane panelTopTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelTopTabbedPane.addTab("Scan Image", null, imagePanel, null);
		panelTopTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		imagePanel.setBounds(new Rectangle(0, 0, 100, 100));
		gbc_panelTop.anchor = GridBagConstraints.EAST;
		gbc_panelTop.fill = GridBagConstraints.BOTH;
		gbc_panelTop.insets = new Insets(0, 10, 5, 0);
		gbc_panelTop.gridx = 1;
		gbc_panelTop.gridy = 1;
		add(panelTopTabbedPane, gbc_panelTop);

		GridBagLayout panelGridBagLayout = new GridBagLayout();

		panelGridBagLayout.columnWidths = new int[] { 60, 100 };
		panelGridBagLayout.rowHeights = new int[] { 10, 210, 20, 20, 20, 20 };
		panelGridBagLayout.columnWeights = new double[] { 0, 0 };
		panelGridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0 };

		imagePanel.setLayout(panelGridBagLayout);

		renderCheckBox = new JCheckBox("");
		renderCheckBox.setSelected(false);
		renderCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (renderCheckBox.isSelected())
					imageLabel.setIcon(new ImageIcon(imageEnabled));
				else
					imageLabel.setIcon(new ImageIcon(imageDisabled));
			}
		});

		GridBagConstraints gbc_renderCheckBox = new GridBagConstraints();
		gbc_renderCheckBox.anchor = GridBagConstraints.WEST;
		gbc_renderCheckBox.insets = new Insets(5, 0, 5, 5);
		gbc_renderCheckBox.gridx = 0;
		gbc_renderCheckBox.gridy = 0;
		imagePanel.add(renderCheckBox, gbc_renderCheckBox);

		GridBagConstraints gbc_renderCheckBoxLabel = new GridBagConstraints();
		gbc_renderCheckBoxLabel.anchor = GridBagConstraints.WEST;
		gbc_renderCheckBoxLabel.insets = new Insets(5, -90, 5, 5);
		gbc_renderCheckBoxLabel.gridx = 1;
		gbc_renderCheckBoxLabel.gridy = 0;
		imagePanel.add(new JLabel("Render Image"), gbc_renderCheckBoxLabel);

		this.imageLabel = new JLabel("");
		this.imageLabel.setIcon(new ImageIcon(imageDisabled));

		JPanel imageLabelPanel = new JPanel();
		imageLabelPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		imageLabelPanel.add(this.imageLabel);

		GridBagConstraints gbc_imagePanel = new GridBagConstraints();
		gbc_imagePanel.anchor = GridBagConstraints.CENTER;
		gbc_imagePanel.fill = GridBagConstraints.BOTH;
		gbc_imagePanel.insets = new Insets(0, 0, 10, 0);
		gbc_imagePanel.gridwidth = 2;
		gbc_imagePanel.gridx = 0;
		gbc_imagePanel.gridy = 1;
		imagePanel.add(imageLabelPanel, gbc_imagePanel);

		GridBagConstraints gbc_imagePanel_2 = new GridBagConstraints();
		gbc_imagePanel_2.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_2.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_2.insets = new Insets(0, 5, 5, 0);
		gbc_imagePanel_2.gridx = 0;
		gbc_imagePanel_2.gridy = 2;

		imagePanel.add(new JLabel("Centroid X Position (um)"), gbc_imagePanel_2);

		GridBagConstraints gbc_imagePanel_3 = new GridBagConstraints();
		gbc_imagePanel_3.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_3.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_3.insets = new Insets(0, 5, 5, 0);
		gbc_imagePanel_3.gridx = 0;
		gbc_imagePanel_3.gridy = 3;

		imagePanel.add(new JLabel("Centroid Y Position (um)"), gbc_imagePanel_3);

		this.centroid_x_position = new JTextField(GuiUtilities.parseDouble(0.0, 1, true) + " ± " + GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_x_position.setEditable(false);
		this.centroid_x_position.setColumns(10);

		this.centroid_y_position = new JTextField(GuiUtilities.parseDouble(0.0, 1, true) + " ± " + GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_y_position.setEditable(false);
		this.centroid_y_position.setColumns(10);

		GridBagConstraints gbc_imagePanel_4 = new GridBagConstraints();
		gbc_imagePanel_4.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_4.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_4.insets = new Insets(0, 5, 5, 5);
		gbc_imagePanel_4.gridx = 1;
		gbc_imagePanel_4.gridy = 2;

		imagePanel.add(this.centroid_x_position, gbc_imagePanel_4);

		GridBagConstraints gbc_imagePanel_5 = new GridBagConstraints();
		gbc_imagePanel_5.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_5.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_5.insets = new Insets(0, 5, 5, 5);
		gbc_imagePanel_5.gridx = 1;
		gbc_imagePanel_5.gridy = 3;

		imagePanel.add(this.centroid_y_position, gbc_imagePanel_5);

		GridBagConstraints gbc_colorModeLabel = new GridBagConstraints();
		gbc_colorModeLabel.insets = new Insets(10, 5, 0, 5);
		gbc_colorModeLabel.anchor = GridBagConstraints.EAST;
		gbc_colorModeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorModeLabel.gridx = 0;
		gbc_colorModeLabel.gridy = 4;
		imagePanel.add(new JLabel("Color Mode"), gbc_colorModeLabel);

		this.colorModeCombo = new JComboBox();
		this.colorModeCombo.setModel(new DefaultComboBoxModel(IDSCCDColorModes.get_values()));
		this.colorModeCombo.setSelectedIndex(1);
		this.colorModeCombo.setMaximumRowCount(3);

		GridBagConstraints gbc_colorModeCombo = new GridBagConstraints();
		gbc_colorModeCombo.insets = new Insets(10, 5, 0, 5);
		gbc_colorModeCombo.anchor = GridBagConstraints.WEST;
		gbc_colorModeCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorModeCombo.gridx = 1;
		gbc_colorModeCombo.gridy = 4;
		imagePanel.add(this.colorModeCombo, gbc_colorModeCombo);

		JPanel sliderPanel = new JPanel();
		GridBagLayout gbl_sliderPanel = new GridBagLayout();
		gbl_sliderPanel.columnWidths = new int[] { 0, 0 };
		gbl_sliderPanel.rowHeights = new int[] { 0 };
		gbl_sliderPanel.columnWeights = new double[] { 0.1, 0.9 };
		gbl_sliderPanel.rowWeights = new double[] { 0.0 };
		sliderPanel.setLayout(gbl_sliderPanel);

		GridBagConstraints gbc_sliderPanel = new GridBagConstraints();
		gbc_sliderPanel.insets = new Insets(0, 5, 0, 5);
		gbc_sliderPanel.anchor = GridBagConstraints.EAST;
		gbc_sliderPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderPanel.gridx = 0;
		gbc_sliderPanel.gridy = 5;
		gbc_sliderPanel.gridwidth = 2;
		imagePanel.add(sliderPanel, gbc_sliderPanel);

		GridBagConstraints gbc_gainLabel = new GridBagConstraints();
		gbc_gainLabel.insets = new Insets(0, 5, 0, 5);
		gbc_gainLabel.anchor = GridBagConstraints.EAST;
		gbc_gainLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainLabel.gridx = 0;
		gbc_gainLabel.gridy = 0;
		sliderPanel.add(new JLabel("Gain"), gbc_gainLabel);

		this.gainSlider = new JSlider(0, 100);
		this.gainSlider.setMajorTickSpacing(10);
		this.gainSlider.setPaintTicks(true);
		this.gainSlider.setPaintLabels(true);
		this.gainSlider.setValue(0);

		GridBagConstraints gbc_gainSlider = new GridBagConstraints();
		gbc_gainSlider.insets = new Insets(0, 5, 0, 5);
		gbc_gainSlider.anchor = GridBagConstraints.NORTHEAST;
		gbc_gainSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainSlider.gridx = 1;
		gbc_gainSlider.gridy = 0;
		sliderPanel.add(this.gainSlider, gbc_gainSlider);

		JTabbedPane scanManagementTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanManagementTabbedPane = new GridBagConstraints();
		gbc_scanManagementTabbedPane.insets = new Insets(5, 10, 0, 0);
		gbc_scanManagementTabbedPane.anchor = GridBagConstraints.NORTHEAST;
		gbc_scanManagementTabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_scanManagementTabbedPane.gridx = 1;
		gbc_scanManagementTabbedPane.gridy = 2;
		gbc_scanManagementTabbedPane.gridheight = 2;
		add(scanManagementTabbedPane, gbc_scanManagementTabbedPane);

		JPanel scanManagementPanel = new JPanel();
		scanManagementTabbedPane.addTab("Calcutions Management", null, scanManagementPanel, null);
		scanManagementTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_scanManagementPanel = new GridBagLayout();
		gbl_scanManagementPanel.columnWidths = new int[] { 0, 0 };
		gbl_scanManagementPanel.rowHeights = new int[] { 0 };
		gbl_scanManagementPanel.columnWeights = new double[] { 0.1, 0.9 };
		gbl_scanManagementPanel.rowWeights = new double[] { 0.0 };
		scanManagementPanel.setLayout(gbl_scanManagementPanel);

		GridBagConstraints gbc_scanPanel_1 = new GridBagConstraints();
		gbc_scanPanel_1.anchor = GridBagConstraints.EAST;
		gbc_scanPanel_1.insets = new Insets(0, 5, 5, 0);
		gbc_scanPanel_1.gridx = 0;
		gbc_scanPanel_1.gridy = 0;

		scanManagementPanel.add(new JLabel("Number of Captures per Step"), gbc_scanPanel_1);

		this.numberOfCaptures = new JTextField("0");

		this.numberOfCaptures.setHorizontalAlignment(SwingConstants.RIGHT);
		this.numberOfCaptures.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					numberOfCaptures.setText(Integer.toString(Math.abs(Integer.parseInt(numberOfCaptures.getText()))));
				}
				catch (Throwable t)
				{
					numberOfCaptures.setText("<not a number>");
				}
			}
		});
		this.numberOfCaptures.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					numberOfCaptures.setText(Integer.toString(Math.abs(Integer.parseInt(numberOfCaptures.getText()))));
				}
				catch (Throwable t)
				{
					numberOfCaptures.setText("<not a number>");
				}
			}
		});
		this.numberOfCaptures.setText("1");
		this.numberOfCaptures.setColumns(4);

		GridBagConstraints gbc_scanPanel_2 = new GridBagConstraints();
		gbc_scanPanel_2.anchor = GridBagConstraints.WEST;
		gbc_scanPanel_2.fill = GridBagConstraints.VERTICAL;
		gbc_scanPanel_2.insets = new Insets(0, 5, 5, 5);
		gbc_scanPanel_2.gridx = 1;
		gbc_scanPanel_2.gridy = 0;

		scanManagementPanel.add(this.numberOfCaptures, gbc_scanPanel_2);

		int width = 280;
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5, 1));
		buttonPanel.setMaximumSize(new Dimension(width, 0));
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.insets = new Insets(5, 10, 0, 0);
		gbc_buttonPanel.anchor = GridBagConstraints.NORTHEAST;
		gbc_buttonPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPanel.gridx = 1;
		gbc_buttonPanel.gridy = 3;
		gbc_buttonPanel.gridheight = 1;
		add(buttonPanel, gbc_buttonPanel);

		JButton buttonTemp = new JButton("Launch Generic Python Script");
		buttonTemp.setPreferredSize(new Dimension(width, 30));
		buttonPanel.add(buttonTemp);

		buttonTemp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				try
				{
					if (scanIndex >= 0)
					{
						String pythonExe = FileIni.getInstance().getProperty(PYTHON_EXE);
						String pythonFolder = FileIni.getInstance().getProperty(PYTHON_FOLDER);
						String fileIn = pythonFolder + "input" + File.separator + "input_generic.dat";
						String fileOut = pythonFolder + "output" + File.separator + "output_generic.dat";
						String pythonScript = GuiUtilities.showPythonFileChooser(pythonFolder, null, "*.py");

						if (!pythonScript.isEmpty())
						{
							String content = launchPythonScript(pythonExe, pythonScript, fileIn, fileOut, 1);

							if (content.contains("Exception") || content.contains("Error"))
								GuiUtilities.showErrorPopup("Python Script Failed: \n" + content, null);
						}
					}
					else
						GuiUtilities.showErrorPopup("No Scan to analyze", null);
				}
				catch (Exception e1)
				{
					GuiUtilities.showErrorPopup(e1.getMessage(), null);
				}
			}
		});

		JButton buttonCalibration = new JButton("Launch Calibration Script");
		buttonCalibration.setPreferredSize(new Dimension(width, 30));
		buttonPanel.add(buttonCalibration);

		buttonCalibration.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				try
				{
					if (scanIndex >= 0)
					{
						String pythonExe = FileIni.getInstance().getProperty(PYTHON_EXE);
						String pythonFolder = FileIni.getInstance().getProperty(PYTHON_FOLDER);
						String fileIn = pythonFolder + "input" + File.separator + "input_calibration.dat";
						String fileOut = pythonFolder + "output" + File.separator + "output_calibration.dat";
						String pythonScript = pythonFolder + FileIni.getInstance().getProperty(CALIBRATION_PYTHON_SCRIPT);

						String content = launchPythonScript(pythonExe, pythonScript, fileIn, fileOut, 0);

						if (content.contains("Exception") || content.contains("Error"))
							GuiUtilities.showErrorPopup("Python Script Failed: \n" + content, null);
						else
						{
							HashMap<String, double[]> outmap = getRMSFromFile(fileOut);
							HashMap<String, double[]> outmap_bessy = getRMSFromFile(fileOut + ".bessy.dat");

							outmap.put("x_b", outmap_bessy.get("x"));
							outmap.put("rms_s_b", outmap_bessy.get("rms_s"));
							outmap.put("rms_h_b", outmap_bessy.get("rms_h"));
							outmap.put("headers_b", outmap_bessy.get("headers"));

							LTPResidualsCalculationWindows.getInstance(null, LTPResidualsCalculationWindows.KindOfCurve.CALIBRATION, outmap).setVisible(true);
						}
					}
					else
						GuiUtilities.showErrorPopup("No Scan to analyze", null);
				}
				catch (Exception e1)
				{
					GuiUtilities.showErrorPopup(e1.getMessage(), null);
				}
			}
		});

		JButton buttonSphere = new JButton("Launch Residual Calculations (Sphere)");
		buttonSphere.setPreferredSize(new Dimension(width, 30));
		buttonPanel.add(buttonSphere);

		buttonSphere.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (scanIndex >= 0)
						LTPResidualsCalculationWindows.getInstance(null, LTPResidualsCalculationWindows.KindOfCurve.SPHERE, getCurrentSlopes()).setVisible(true);
					else
						GuiUtilities.showErrorPopup("No Scan to analyze", null);
				}
				catch (Exception e1)
				{
					GuiUtilities.showErrorPopup(e1.getMessage(), null);
				}
			}
		});

		JButton buttonEllipse = new JButton("Launch Residual Calculations (Ellipse)");
		buttonEllipse.setPreferredSize(new Dimension(width, 30));
		buttonPanel.add(buttonEllipse);

		buttonEllipse.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (scanIndex >= 0)
					{
						String pythonExe = FileIni.getInstance().getProperty(PYTHON_EXE);
						String pythonFolder = FileIni.getInstance().getProperty(PYTHON_FOLDER);
						String fileIn = pythonFolder + "input" + File.separator + "input_ellipse.dat";
						String fileOut = pythonFolder + "output" + File.separator + "output_ellipse.dat";
						String pythonScript = pythonFolder + FileIni.getInstance().getProperty(ELLIPSE_PYTHON_SCRIPT);

						String content = launchPythonScript(pythonExe, pythonScript, fileIn, fileOut, 0);

						if (content.contains("Exception") || content.contains("Error"))
							GuiUtilities.showErrorPopup("Python Script Failed: \n" + content, null);
						else
							LTPResidualsCalculationWindows.getInstance(null, LTPResidualsCalculationWindows.KindOfCurve.ELLIPSE, getRMSFromFile(fileOut)).setVisible(true);
					}
					else
						GuiUtilities.showErrorPopup("No Scan to analyze", null);
				}
				catch (Exception e1)
				{
					GuiUtilities.showErrorPopup(e1.getMessage(), null);
				}
			}
		});

		JButton buttonEllipseFixed = new JButton("Launch Residual Calculations (Fixed Ellipse)");
		buttonEllipseFixed.setPreferredSize(new Dimension(width, 30));
		buttonPanel.add(buttonEllipseFixed);

		buttonEllipseFixed.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (scanIndex >= 0)
						LTPResidualsCalculationWindows.getInstance(null, LTPResidualsCalculationWindows.KindOfCurve.ELLIPSE_FIXED, getCurrentSlopes()).setVisible(true);
					else
						GuiUtilities.showErrorPopup("No Scan to analyze", null);
				}
				catch (Exception e1)
				{
					GuiUtilities.showErrorPopup(e1.getMessage(), null);
				}
			}
		});
	}

	public synchronized void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
		if (axis == Axis.MOTOR5)
			point.setX(((Double) point.getCustomData(LPTScanProgram.ENCODER_POSITION)).doubleValue());

		super.signalMeasure(axis, point, progress, port);

		this.drawCapture(point);
	}

	protected Thread getScanThread()
	{
		return new LPTScanThread(this);
	}

	protected String getMeasureTabName()
	{
		return "Slope Error";
	}

	protected String getOrdinateLabel()
	{
		return "Slope Error (rad)";
	}

	protected boolean isOrdinateInteger()
	{
		return false;
	}

	@Override
	protected double getPlotUpperBound()
	{
		return 0.001;
	}

	@Override
	protected double getPlotLowerBound()
	{
		return -0.001;
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
		return "Centroid X Position";
	}

	protected String getAdditionalInfo1Name()
	{
		return "Centroid X Position (\u03bcm)";
	}

	protected String getAdditionaInfo2TabName()
	{
		return "Centroid Y Position";
	}

	protected String getAdditionalInfo2Name()
	{
		return "Centroid Y Position (\u03bcm)";
	}

	protected String getAdditionalInfo1Format()
	{
		return "%9.1f";
	}

	protected String getAdditionalInfo2Format()
	{
		return "%9.1f";
	}

	protected void drawCapture(MeasurePoint point)
	{
		if (this.renderCheckBox.isSelected())
		{
			BufferedImage capture = (BufferedImage) point.getCustomData(LPTScanProgram.LAST_IMAGE);

			int imageHSize = 250;

			BufferedImage resizedImage = new BufferedImage(imageHSize, (int) (imageHSize * HEIGHT_TO_WIDTH_RATIO), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resizedImage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(capture, 0, 0, imageHSize, (int) (imageHSize * HEIGHT_TO_WIDTH_RATIO), null);
			g.dispose();

			this.imageLabel.setIcon(new ImageIcon(resizedImage));
		}

		this.centroid_x_position.setText(GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.X)).doubleValue(), 1, true) + " ± "
		    + GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.X_STANDARD_DEVIATION)).doubleValue(), 1, true));
		this.centroid_y_position.setText(GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.Y)).doubleValue(), 1, true) + " ± "
		    + GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.Y_STANDARD_DEVIATION)).doubleValue(), 1, true));
	}

	private String launchPythonScript(String pythonExe, String pythonScript, String fileIn, String fileOut, int showPlot) throws IOException
	{
		writeScanFile(fileIn);

		ProcessBuilder processBuilder = new ProcessBuilder(pythonExe, pythonScript, fileIn, References.getInstance().get(References.LTP_X_0), References
		    .getInstance().get(References.LTP_Y_0), References.getInstance().get(References.FOCAL_DISTANCE), String.valueOf(showPlot), fileOut);

		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		String content = "";
		String row = reader.readLine();

		while (row != null)
		{
			content += row + "\n";
			row = reader.readLine();
		}

		reader.close();
		return content;
	}

	private HashMap<String, double[]> getRMSFromFile(String fileName) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		String header = reader.readLine();

		StringTokenizer tokenizer = new StringTokenizer(header, "=");
		tokenizer.nextToken();
		int nHeader = Integer.parseInt(tokenizer.nextToken());

		String points = reader.readLine();
		tokenizer = new StringTokenizer(points, "=");
		tokenizer.nextToken();
		int nPoints = Integer.parseInt(tokenizer.nextToken());

		double[] headers = new double[nHeader];

		String row = null;

		for (int i = 0; i < nHeader; i++)
		{
			row = reader.readLine().trim();

			tokenizer = new StringTokenizer(row, "=");
			tokenizer.nextToken();
			headers[i] = Double.parseDouble(tokenizer.nextToken());
		}

		double[] x = new double[nPoints];
		double[] rms_s = new double[nPoints];
		double[] rms_h = new double[nPoints];

		for (int index = 0; index < nPoints; index++)
		{
			row = reader.readLine();

			if (row != null)
			{
				row = row.trim();

				if (!(row.isEmpty()))
				{
					tokenizer = new StringTokenizer(row, ",");

					x[index] = Double.parseDouble(tokenizer.nextToken());
					rms_s[index] = Double.parseDouble(tokenizer.nextToken());
					rms_h[index] = Double.parseDouble(tokenizer.nextToken());
				}
			}
		}

		reader.close();

		HashMap<String, double[]> data = new HashMap<>();
		data.put("x", x);
		data.put("rms_s", rms_s);
		data.put("rms_h", rms_h);
		data.put("headers", headers);

		return data;
	}

	private HashMap<String, double[]> getCurrentSlopes()
	{
		XYSeries series = this.xyDataset.getSeries(this.scanIndex);
		XYSeries seriesAddInfo1 = this.xyDatasetAddInfo1.getSeries(this.scanIndex);
		XYSeries seriesAddInfo2 = this.xyDatasetAddInfo2.getSeries(this.scanIndex);

		int nItem = series.getItemCount();

		double[] x = new double[nItem];
		double[] y = new double[nItem];
		double[] xc = new double[nItem];
		double[] yc = new double[nItem];

		for (int index = 0; index < nItem; index++)
		{
			x[index] = series.getX(index).doubleValue();
			y[index] = series.getY(index).doubleValue();
			xc[index] = seriesAddInfo1.getY(index).doubleValue();
			yc[index] = seriesAddInfo2.getY(index).doubleValue();
		}

		HashMap<String, double[]> currentSlopes = new HashMap<>();
		currentSlopes.put("x", x);
		currentSlopes.put("y", y);
		currentSlopes.put("xc", xc);
		currentSlopes.put("yc", yc);

		return currentSlopes;
	}

	class LPTScanThread extends ScanPanel.ScanThread
	{
		public LPTScanThread(LPTScanPanel panel)
		{
			super(panel);
		}

		public void run()
		{
			try
			{
				((LPTScanProgram) ProgramsFacade.getProgram(LPTScanProgram.PROGRAM_NAME)).initialize();

				try
				{
					super.run();
				}
				finally
				{
					((LPTScanProgram) ProgramsFacade.getProgram(LPTScanProgram.PROGRAM_NAME)).terminate();
				}
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), super.panel);
			}
		}

		protected void addCustomParameters(ScanParameters scanParameters)
		{

			scanParameters.addCustomParameter(LPTScanProgram.COLOR_MODE,
			    IDSCCDColorModes.get_from_index(((LPTScanPanel) super.panel).colorModeCombo.getSelectedIndex()));
			scanParameters.addCustomParameter(LPTScanProgram.GAIN, ((LPTScanPanel) super.panel).gainSlider.getValue());
			scanParameters.addCustomParameter(LPTScanProgram.DIM_X, IIDSCCD.DIM_X);
			scanParameters.addCustomParameter(LPTScanProgram.DIM_Y, IIDSCCD.DIM_Y);
			scanParameters.addCustomParameter(LPTScanProgram.NUMBER_OF_CAPTURES, Integer.parseInt(((LPTScanPanel) super.panel).numberOfCaptures.getText()));
			scanParameters.addCustomParameter(LPTScanProgram.DRAW_IMAGE, Boolean.valueOf(((LPTScanPanel) super.panel).renderCheckBox.isSelected()));
		}

		protected String getScanProgramName()
		{
			return LPTScanProgram.PROGRAM_NAME;
		}
	}
}
