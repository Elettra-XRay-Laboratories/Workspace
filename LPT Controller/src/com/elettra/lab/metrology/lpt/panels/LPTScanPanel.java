package com.elettra.lab.metrology.lpt.panels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
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

public class LPTScanPanel extends ScanPanel
{
	private static final double	HEIGHT_TO_WIDTH_RATIO	= ((double) IIDSCCD.DIM_Y / (double) IIDSCCD.DIM_X);

	private JLabel	            imageLabel;

	/**
	 * 
	 */
	private static final long	  serialVersionUID	    = -1195319609096497524L;

	private JTextField	        centroid_x_position;
	private JTextField	        centroid_y_position;
	private JTextField	        numberOfCaptures;
	private JCheckBox	          renderCheckBox;

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

		gridBagLayout.columnWidths = new int[] { 750, 450 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0, 0 };
		gridBagLayout.rowWeights = new double[] { 2, 58, 15, 25, Double.MIN_VALUE };

		this.setLayout(gridBagLayout);

		JPanel imagePanel = new JPanel();
		JTabbedPane panelTopTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelTopTabbedPane.addTab("Scan Image", null, imagePanel, null);
		panelTopTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		imagePanel.setBounds(new Rectangle(0, 0, 400, 400));
		gbc_panelTop.anchor = GridBagConstraints.EAST;
		gbc_panelTop.fill = GridBagConstraints.BOTH;
		gbc_panelTop.insets = new Insets(0, 10, 5, 0);
		gbc_panelTop.gridx = 1;
		gbc_panelTop.gridy = 1;
		add(panelTopTabbedPane, gbc_panelTop);

		GridBagLayout panelGridBagLayout = new GridBagLayout();

		panelGridBagLayout.columnWidths = new int[] { 60, 300 };
		panelGridBagLayout.rowHeights = new int[] { 10, 380, 20, 20 };
		panelGridBagLayout.columnWeights = new double[] { 0, 0 };
		panelGridBagLayout.rowWeights = new double[] { 0, 0, 0, 0 };

		imagePanel.setLayout(panelGridBagLayout);

		renderCheckBox = new JCheckBox("");
		renderCheckBox.setSelected(true);
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
		this.imageLabel.setIcon(new ImageIcon(imageEnabled));

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
		gbl_scanManagementPanel.rowHeights = new int[] { 0, };
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

		JPanel calculationManagementPanel = new JPanel();
		scanManagementTabbedPane.addTab("CCD Management", null, calculationManagementPanel, null);
		scanManagementTabbedPane.setForegroundAt(1, new Color(0, 102, 51));
		GridBagLayout gbl_calculationManagementPanel = new GridBagLayout();
		gbl_calculationManagementPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_calculationManagementPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_calculationManagementPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_calculationManagementPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		calculationManagementPanel.setLayout(gbl_calculationManagementPanel);

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
		return "Slope Error (mrad)";
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
		return "Centroid X Position (um)";
	}

	protected String getAdditionaInfo2TabName()
	{
		return "Centroid Y Position";
	}

	protected String getAdditionalInfo2Name()
	{
		return "Centroid Y Position (um)";
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

			int imageHSize = 390;

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

		this.centroid_x_position.setText(GuiUtilities.parseDouble(point.getAdditionalInformation1(), 1, true) + " ± "
		    + GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.X_STANDARD_DEVIATION)).doubleValue(), 1, true));
		this.centroid_y_position.setText(GuiUtilities.parseDouble(point.getAdditionalInformation2(), 1, true) + " ± "
		    + GuiUtilities.parseDouble(((Double) point.getCustomData(LPTScanProgram.Y_STANDARD_DEVIATION)).doubleValue(), 1, true));
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
			scanParameters.addCustomParameter(LPTScanProgram.COLOR_MODE, IDSCCDColorModes.IS_CM_MONO8);
			scanParameters.addCustomParameter(LPTScanProgram.DIM_X, IIDSCCD.DIM_X);
			scanParameters.addCustomParameter(LPTScanProgram.DIM_Y, IIDSCCD.DIM_Y);
			scanParameters.addCustomParameter(LPTScanProgram.NUMBER_OF_CAPTURES, Integer.parseInt(((LPTScanPanel) super.panel).numberOfCaptures.getText()));
		}

		protected String getScanProgramName()
		{
			return LPTScanProgram.PROGRAM_NAME;
		}
	}
}
