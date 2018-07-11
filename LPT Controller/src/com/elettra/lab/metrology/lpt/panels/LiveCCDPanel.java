package com.elettra.lab.metrology.lpt.panels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.listeners.MeasurePoint;
import com.elettra.controller.driver.listeners.Progress;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ScanParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MeasureListener;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.programs.LPTLiveCCDProgram;
import com.elettra.lab.metrology.lpt.programs.LPTScanProgram;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LiveCCDPanel extends MeasureListener
{
	private static final double	HEIGHT_TO_WIDTH_RATIO	= ((double) IIDSCCD.DIM_Y / (double) IIDSCCD.DIM_X);

	/**
	 * 
	 */
	private static final long	  serialVersionUID	    = 2126885439311849757L;
	private JLabel	            imageLabel;
	private Image	              imageDisabled;
	private JButton	            buttonOn;
	private JButton	            buttonOff;
	private JCheckBox	          drawMainCheckBox;
	private JCheckBox	          drawSecondaryCheckBox;
	private JTextField	        centroid_x_position;
	private JTextField	        centroid_y_position;
	private JTextField	        centroid_x_fwhm;
	private JTextField	        centroid_y_fwhm;
	private JSlider             gainSlider;
	private JComboBox           colorModeCombo;
	
	private boolean	            acquire;

	private JButton	            buttonSave;

	public LiveCCDPanel()
	{
		ListenerRegister.getInstance().addListener(Axis.CCD, this);

		this.acquire = false;

		try
		{
			imageDisabled = ImageIO.read(new File("Files/disabled.png")).getScaledInstance(645, (int) (645 * HEIGHT_TO_WIDTH_RATIO), Image.SCALE_FAST);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}

		GridBagLayout gridBagLayout = new GridBagLayout();

		gridBagLayout.columnWidths = new int[] { 645, 205 };
		gridBagLayout.rowHeights = new int[] { 300, 545 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0 };

		this.setLayout(gridBagLayout);

		this.imageLabel = new JLabel("");
		this.imageLabel.setIcon(new ImageIcon(imageDisabled));

		JPanel imageLabelPanel = new JPanel();
		imageLabelPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		imageLabelPanel.add(this.imageLabel);

		GridBagConstraints gbc_imagePanel = new GridBagConstraints();
		gbc_imagePanel.anchor = GridBagConstraints.CENTER;
		gbc_imagePanel.fill = GridBagConstraints.BOTH;
		gbc_imagePanel.insets = new Insets(0, 0, 10, 0);
		gbc_imagePanel.gridheight = 2;
		gbc_imagePanel.gridx = 0;
		gbc_imagePanel.gridy = 0;
		add(imageLabelPanel, gbc_imagePanel);

		JTabbedPane scanManagementTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanManagementTabbedPane = new GridBagConstraints();
		gbc_scanManagementTabbedPane.insets = new Insets(0, 5, 0, 5);
		gbc_scanManagementTabbedPane.anchor = GridBagConstraints.NORTHEAST;
		gbc_scanManagementTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_scanManagementTabbedPane.gridx = 1;
		gbc_scanManagementTabbedPane.gridy = 0;
		add(scanManagementTabbedPane, gbc_scanManagementTabbedPane);

		JPanel scanManagementPanel = new JPanel();
		scanManagementTabbedPane.addTab("Live CCD Management", null, scanManagementPanel, null);
		scanManagementTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_scanManagementPanel = new GridBagLayout();
		gbl_scanManagementPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_scanManagementPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_scanManagementPanel.columnWeights = new double[] { 0, 0, 0 };
		gbl_scanManagementPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		scanManagementPanel.setLayout(gbl_scanManagementPanel);

		GridBagConstraints gbc_label_0 = new GridBagConstraints();
		gbc_label_0.anchor = GridBagConstraints.EAST;
		gbc_label_0.insets = new Insets(0, 5, 5, 0);
		gbc_label_0.gridx = 0;
		gbc_label_0.gridy = 0;

		scanManagementPanel.add(new JLabel("Live Camera"), gbc_label_0);

		buttonOn = new JButton("ON");
		buttonOn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setCameraOn();
			}
		});

		GridBagConstraints gbc_button_0 = new GridBagConstraints();
		gbc_button_0.anchor = GridBagConstraints.EAST;
		gbc_button_0.insets = new Insets(0, 5, 5, 0);
		gbc_button_0.gridx = 1;
		gbc_button_0.gridy = 0;

		scanManagementPanel.add(buttonOn, gbc_button_0);

		buttonOff = new JButton("OFF");
		buttonOff.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setCameraOff();
			}
		});

		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.anchor = GridBagConstraints.WEST;
		gbc_button_1.insets = new Insets(0, 5, 5, 0);
		gbc_button_1.gridx = 2;
		gbc_button_1.gridy = 0;

		scanManagementPanel.add(buttonOff, gbc_button_1);

		drawMainCheckBox = new JCheckBox("");
		drawMainCheckBox.setSelected(true);

		GridBagConstraints gbc_drawMainCheckBox = new GridBagConstraints();
		gbc_drawMainCheckBox.anchor = GridBagConstraints.WEST;
		gbc_drawMainCheckBox.insets = new Insets(5, 0, 5, 5);
		gbc_drawMainCheckBox.gridx = 0;
		gbc_drawMainCheckBox.gridy = 1;
		scanManagementPanel.add(drawMainCheckBox, gbc_drawMainCheckBox);

		GridBagConstraints gbc_drawMainCheckBoxLabel = new GridBagConstraints();
		gbc_drawMainCheckBoxLabel.anchor = GridBagConstraints.WEST;
		gbc_drawMainCheckBoxLabel.insets = new Insets(5, -40, 5, 5);
		gbc_drawMainCheckBoxLabel.gridx = 1;
		gbc_drawMainCheckBoxLabel.gridy = 1;
		gbc_drawMainCheckBoxLabel.gridwidth = 2;
		scanManagementPanel.add(new JLabel("Draw Primary Grid"), gbc_drawMainCheckBoxLabel);

		drawSecondaryCheckBox = new JCheckBox("");
		drawSecondaryCheckBox.setSelected(true);

		GridBagConstraints gbc_drawSecondaryCheckBox = new GridBagConstraints();
		gbc_drawSecondaryCheckBox.anchor = GridBagConstraints.WEST;
		gbc_drawSecondaryCheckBox.insets = new Insets(5, 0, 5, 5);
		gbc_drawSecondaryCheckBox.gridx = 0;
		gbc_drawSecondaryCheckBox.gridy = 2;
		scanManagementPanel.add(drawSecondaryCheckBox, gbc_drawSecondaryCheckBox);

		GridBagConstraints gbc_drawSecondaryCheckBoxLabel = new GridBagConstraints();
		gbc_drawSecondaryCheckBoxLabel.anchor = GridBagConstraints.WEST;
		gbc_drawSecondaryCheckBoxLabel.insets = new Insets(5, -40, 5, 5);
		gbc_drawSecondaryCheckBoxLabel.gridx = 1;
		gbc_drawSecondaryCheckBoxLabel.gridy = 2;
		gbc_drawSecondaryCheckBoxLabel.gridwidth = 2;
		scanManagementPanel.add(new JLabel("Draw Secondary Grid"), gbc_drawSecondaryCheckBoxLabel);

		GridBagConstraints gbc_colorModeLabel = new GridBagConstraints();
		gbc_colorModeLabel.insets = new Insets(20, 5, 0, 5);
		gbc_colorModeLabel.anchor = GridBagConstraints.EAST;
		gbc_colorModeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorModeLabel.gridx = 0;
		gbc_colorModeLabel.gridy = 3;
		scanManagementPanel.add(new JLabel("Color Mode"), gbc_colorModeLabel);

		this.colorModeCombo = new JComboBox();
		this.colorModeCombo.setModel(new DefaultComboBoxModel(IDSCCDColorModes.get_values()));
		this.colorModeCombo.setSelectedIndex(1);
		this.colorModeCombo.setMaximumRowCount(3);

		GridBagConstraints gbc_colorModeCombo = new GridBagConstraints();
		gbc_colorModeCombo.insets = new Insets(20, 5, 0, 5);
		gbc_colorModeCombo.anchor = GridBagConstraints.WEST;
		gbc_colorModeCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorModeCombo.gridx = 1;
		gbc_colorModeCombo.gridy = 3;
		scanManagementPanel.add(this.colorModeCombo, gbc_colorModeCombo);
		

		GridBagConstraints gbc_gainLabel = new GridBagConstraints();
		gbc_gainLabel.insets = new Insets(0, 5, 0, 5);
		gbc_gainLabel.anchor = GridBagConstraints.EAST;
		gbc_gainLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainLabel.gridx = 0;
		gbc_gainLabel.gridy = 4;
		scanManagementPanel.add(new JLabel("Gain"), gbc_gainLabel);

		this.gainSlider = new JSlider(0, 100);
		this.gainSlider.setMajorTickSpacing(10);
		this.gainSlider.setPaintTicks(true);
		this.gainSlider.setPaintLabels(true);

		GridBagConstraints gbc_gainSlider = new GridBagConstraints();
		gbc_gainSlider.insets = new Insets(0, 5, 0, 5);
		gbc_gainSlider.anchor = GridBagConstraints.NORTHEAST;
		gbc_gainSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainSlider.gridwidth = 2;
		gbc_gainSlider.gridx = 1;
		gbc_gainSlider.gridy = 4;
		scanManagementPanel.add(this.gainSlider, gbc_gainSlider);

	
		
		JTabbedPane positioningTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_positioningTabbedPane = new GridBagConstraints();
		gbc_positioningTabbedPane.insets = new Insets(0, 5, 0, 5);
		gbc_positioningTabbedPane.anchor = GridBagConstraints.NORTHEAST;
		gbc_positioningTabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_positioningTabbedPane.gridx = 1;
		gbc_positioningTabbedPane.gridy = 1;
		add(positioningTabbedPane, gbc_positioningTabbedPane);

		JPanel positioningPanel = new JPanel();
		positioningTabbedPane.addTab("X0/Y0 Positioning", null, positioningPanel, null);
		positioningTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_positioningPanel = new GridBagLayout();
		gbl_positioningPanel.columnWidths = new int[] { 0, 0 };
		gbl_positioningPanel.rowHeights = new int[] { 0, 0, 0, 0, 0};
		gbl_positioningPanel.columnWeights = new double[] { 0, 0 };
		gbl_positioningPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		positioningPanel.setLayout(gbl_positioningPanel);

		GridBagConstraints gbc_imagePanel_2 = new GridBagConstraints();
		gbc_imagePanel_2.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_2.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_2.insets = new Insets(10, 5, 5, 0);
		gbc_imagePanel_2.gridx = 0;
		gbc_imagePanel_2.gridy = 0;

		positioningPanel.add(new JLabel("Centroid X Position (um)"), gbc_imagePanel_2);

		GridBagConstraints gbc_imagePanel_3 = new GridBagConstraints();
		gbc_imagePanel_3.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_3.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_3.insets = new Insets(0, 5, 5, 0);
		gbc_imagePanel_3.gridx = 0;
		gbc_imagePanel_3.gridy = 1;

		positioningPanel.add(new JLabel("Centroid Y Position (um)"), gbc_imagePanel_3);

		GridBagConstraints gbc_imagePanel_4 = new GridBagConstraints();
		gbc_imagePanel_4.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_4.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_4.insets = new Insets(0, 5, 5, 0);
		gbc_imagePanel_4.gridx = 0;
		gbc_imagePanel_4.gridy = 2;

		positioningPanel.add(new JLabel("Centroid X FWHM (um)"), gbc_imagePanel_4);

		GridBagConstraints gbc_imagePanel_5 = new GridBagConstraints();
		gbc_imagePanel_5.anchor = GridBagConstraints.EAST;
		gbc_imagePanel_5.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_5.insets = new Insets(0, 5, 5, 0);
		gbc_imagePanel_5.gridx = 0;
		gbc_imagePanel_5.gridy = 3;

		positioningPanel.add(new JLabel("Centroid Y FWHM (um)"), gbc_imagePanel_5);

		this.centroid_x_position = new JTextField(GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_x_position.setEditable(false);
		this.centroid_x_position.setColumns(8);

		GridBagConstraints gbc_imagePanel_6 = new GridBagConstraints();
		gbc_imagePanel_6.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_6.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_6.insets = new Insets(10, 5, 5, 5);
		gbc_imagePanel_6.gridx = 1;
		gbc_imagePanel_6.gridy = 0;

		positioningPanel.add(this.centroid_x_position, gbc_imagePanel_6);

		this.centroid_y_position = new JTextField(GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_y_position.setEditable(false);
		this.centroid_y_position.setColumns(8);

		GridBagConstraints gbc_imagePanel_7 = new GridBagConstraints();
		gbc_imagePanel_7.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_7.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_7.insets = new Insets(0, 5, 5, 5);
		gbc_imagePanel_7.gridx = 1;
		gbc_imagePanel_7.gridy = 1;

		positioningPanel.add(this.centroid_y_position, gbc_imagePanel_7);

		this.centroid_x_fwhm = new JTextField(GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_x_fwhm.setEditable(false);
		this.centroid_x_fwhm.setColumns(8);

		GridBagConstraints gbc_imagePanel_8 = new GridBagConstraints();
		gbc_imagePanel_8.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_8.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_8.insets = new Insets(0, 5, 5, 5);
		gbc_imagePanel_8.gridx = 1;
		gbc_imagePanel_8.gridy = 2;

		positioningPanel.add(this.centroid_x_fwhm, gbc_imagePanel_8);

		this.centroid_y_fwhm = new JTextField(GuiUtilities.parseDouble(0.0, 1, true));
		this.centroid_y_fwhm.setEditable(false);
		this.centroid_y_fwhm.setColumns(8);

		GridBagConstraints gbc_imagePanel_9 = new GridBagConstraints();
		gbc_imagePanel_9.anchor = GridBagConstraints.WEST;
		gbc_imagePanel_9.fill = GridBagConstraints.VERTICAL;
		gbc_imagePanel_9.insets = new Insets(0, 5, 5, 5);
		gbc_imagePanel_9.gridx = 1;
		gbc_imagePanel_9.gridy = 3;

		positioningPanel.add(this.centroid_y_fwhm, gbc_imagePanel_9);

		buttonSave = new JButton("Save X0/Y0 Position as Reference");
		buttonSave.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					manageEventSave();
				}
				catch (IOException e1)
				{
					GuiUtilities.showErrorPopup("Positions not saved!\n\n" + e1.getMessage(), null);
				}
			}
		});

		GridBagConstraints gbc_button_save = new GridBagConstraints();
		gbc_button_save.anchor = GridBagConstraints.WEST;
		gbc_button_save.insets = new Insets(5, 5, 5, 0);
		gbc_button_save.gridx = 0;
		gbc_button_save.gridy = 4;
		gbc_button_save.gridwidth = 2;

		positioningPanel.add(buttonSave, gbc_button_save);
	}

	protected synchronized void setCameraOn()
	{
		this.buttonOff.setEnabled(true);
		this.buttonOn.setEnabled(false);

		this.acquire = true;

		(new LPTLiveCCDThread(this)).start();
	}

	protected synchronized void setCameraOff()
	{
		this.buttonOff.setEnabled(false);
		this.buttonOn.setEnabled(true);

		this.acquire = false;
	}

	protected synchronized void manageEventSave() throws IOException
	{
		if (GuiUtilities.showConfirmPopup(
		    "Do you confirm the storing of X0/Y0 values as the reference position?\n\nPosition -> (" + this.centroid_x_position.getText() + ","
		        + this.centroid_y_position.getText() + ")", this))
		{
			References.getInstance().save(References.LTP_X_0, this.centroid_x_position.getText());
			References.getInstance().save(References.LTP_Y_0, this.centroid_y_position.getText());

			GuiUtilities.showMessagePopup("Reference Positions Saved", this);
		}
	}

	@Override
	public void signalMeasure(int axis, MeasurePoint point, Progress progress, ICommunicationPort port) throws CommunicationPortException
	{
		this.drawCapture(point);
	}

	protected void drawCapture(MeasurePoint point)
	{
		BufferedImage capture = (BufferedImage) point.getCustomData(LPTScanProgram.LAST_IMAGE);

		int imageHSize = 645;

		BufferedImage resizedImage = new BufferedImage(imageHSize, (int) (imageHSize * HEIGHT_TO_WIDTH_RATIO), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(capture, 0, 0, imageHSize, (int) (imageHSize * HEIGHT_TO_WIDTH_RATIO), null);
		g.dispose();

		this.imageLabel.setIcon(new ImageIcon(resizedImage));

		this.centroid_x_position.setText(GuiUtilities.parseDouble((double) point.getCustomData(LPTLiveCCDProgram.X), 1, true));
		this.centroid_y_position.setText(GuiUtilities.parseDouble((double) point.getCustomData(LPTLiveCCDProgram.Y), 1, true));
		this.centroid_x_fwhm.setText(GuiUtilities.parseDouble((double) point.getCustomData(LPTLiveCCDProgram.X_FWHM), 1, true));
		this.centroid_y_fwhm.setText(GuiUtilities.parseDouble((double) point.getCustomData(LPTLiveCCDProgram.Y_FWHM), 1, true));
	}

	class LPTLiveCCDThread extends Thread
	{
		protected LiveCCDPanel	panel;

		public LPTLiveCCDThread(LiveCCDPanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				((LPTLiveCCDProgram) ProgramsFacade.getProgram(LPTLiveCCDProgram.PROGRAM_NAME)).initialize();

				try
				{
					ScanParameters scanParameters = new ScanParameters(Axis.CCD, ListenerRegister.getInstance());
					scanParameters.setPanel(this.panel);
					
					scanParameters.addCustomParameter(LPTLiveCCDProgram.DIM_X, IIDSCCD.DIM_X);
					scanParameters.addCustomParameter(LPTLiveCCDProgram.DIM_Y, IIDSCCD.DIM_Y);

					while (panel.acquire)
					{
						scanParameters.addCustomParameter(LPTLiveCCDProgram.COLOR_MODE, IDSCCDColorModes.get_from_index(this.panel.colorModeCombo.getSelectedIndex()));
						scanParameters.addCustomParameter(LPTLiveCCDProgram.GAIN, this.panel.gainSlider.getValue());					
						scanParameters.addCustomParameter(LPTLiveCCDProgram.DRAW_MAIN_GRID, new Boolean(this.panel.drawMainCheckBox.isSelected()));
						scanParameters.addCustomParameter(LPTLiveCCDProgram.DRAW_SECONDARY_GRID, new Boolean(this.panel.drawSecondaryCheckBox.isSelected()));

						ProgramsFacade.executeProgram(LPTLiveCCDProgram.PROGRAM_NAME, scanParameters, null);
						sleep(500);
					}

					panel.imageLabel.setIcon(new ImageIcon(panel.imageDisabled));
				}
				finally
				{
					((LPTLiveCCDProgram) ProgramsFacade.getProgram(LPTLiveCCDProgram.PROGRAM_NAME)).terminate();
				}
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
				
				GuiUtilities.showErrorPopup(exception.getMessage(), this.panel);
			}
		}
	}

	public synchronized void setCCDOff()
	{
		this.acquire = false;
	}
}
