package com.elettra.lab.metrology.lpt.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.StabilityParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.panels.TubeStabilityPanel;
import com.elettra.idsccd.driver.IDSCCDColorModes;
import com.elettra.idsccd.driver.IIDSCCD;
import com.elettra.lab.metrology.lpt.programs.LPTSTABILITYProgram;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LPTStabilityPanel extends TubeStabilityPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6697640136632434040L;

	private JSlider	          gainSlider;
	private JComboBox	        colorModeCombo;
	private JTextField	      numberOfCaptures;
	private JTextField	      captureDuration;

	public LPTStabilityPanel(ICommunicationPort port) throws CommunicationPortException
	{
		super(port);

		JTabbedPane scanManagementTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_scanManagementTabbedPane = new GridBagConstraints();
		gbc_scanManagementTabbedPane.insets = new Insets(0, 5, 0, 5);
		gbc_scanManagementTabbedPane.anchor = GridBagConstraints.NORTHEAST;
		gbc_scanManagementTabbedPane.fill = GridBagConstraints.HORIZONTAL;
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

		GridBagConstraints gbc_scanPanel_1 = new GridBagConstraints();
		gbc_scanPanel_1.anchor = GridBagConstraints.EAST;
		gbc_scanPanel_1.insets = new Insets(10, 5, 5, 0);
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
		this.numberOfCaptures.setText("5");
		this.numberOfCaptures.setColumns(4);

		GridBagConstraints gbc_scanPanel_2 = new GridBagConstraints();
		gbc_scanPanel_2.anchor = GridBagConstraints.WEST;
		gbc_scanPanel_2.fill = GridBagConstraints.VERTICAL;
		gbc_scanPanel_2.insets = new Insets(10, 5, 5, 5);
		gbc_scanPanel_2.gridx = 1;
		gbc_scanPanel_2.gridy = 0;

		scanManagementPanel.add(this.numberOfCaptures, gbc_scanPanel_2);

		GridBagConstraints gbc_scanPanel_3 = new GridBagConstraints();
		gbc_scanPanel_3.anchor = GridBagConstraints.EAST;
		gbc_scanPanel_3.insets = new Insets(0, 5, 5, 0);
		gbc_scanPanel_3.gridx = 0;
		gbc_scanPanel_3.gridy = 1;

		scanManagementPanel.add(new JLabel("Step Duration (s)"), gbc_scanPanel_3);

		this.captureDuration = new JTextField("0");

		this.captureDuration.setHorizontalAlignment(SwingConstants.RIGHT);
		this.captureDuration.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					captureDuration.setText(Integer.toString(Math.abs(Integer.parseInt(captureDuration.getText()))));
				}
				catch (Throwable t)
				{
					captureDuration.setText("<not a number>");
				}
			}
		});
		this.captureDuration.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					captureDuration.setText(Integer.toString(Math.abs(Integer.parseInt(captureDuration.getText()))));
				}
				catch (Throwable t)
				{
					captureDuration.setText("<not a number>");
				}
			}
		});
		this.captureDuration.setText("1");
		this.captureDuration.setColumns(4);

		GridBagConstraints gbc_scanPanel_4 = new GridBagConstraints();
		gbc_scanPanel_4.anchor = GridBagConstraints.WEST;
		gbc_scanPanel_4.fill = GridBagConstraints.VERTICAL;
		gbc_scanPanel_4.insets = new Insets(0, 5, 5, 5);
		gbc_scanPanel_4.gridx = 1;
		gbc_scanPanel_4.gridy = 1;

		scanManagementPanel.add(this.captureDuration, gbc_scanPanel_4);

		GridBagConstraints gbc_colorModeLabel = new GridBagConstraints();
		gbc_colorModeLabel.insets = new Insets(20, 5, 0, 5);
		gbc_colorModeLabel.anchor = GridBagConstraints.EAST;
		gbc_colorModeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_colorModeLabel.gridx = 0;
		gbc_colorModeLabel.gridy = 2;
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
		gbc_colorModeCombo.gridy = 2;
		scanManagementPanel.add(this.colorModeCombo, gbc_colorModeCombo);

		GridBagConstraints gbc_gainLabel = new GridBagConstraints();
		gbc_gainLabel.insets = new Insets(0, 5, 0, 5);
		gbc_gainLabel.anchor = GridBagConstraints.EAST;
		gbc_gainLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainLabel.gridx = 0;
		gbc_gainLabel.gridy = 3;
		scanManagementPanel.add(new JLabel("Gain"), gbc_gainLabel);

		this.gainSlider = new JSlider(0, 100);
		this.gainSlider.setMajorTickSpacing(10);
		this.gainSlider.setPaintTicks(true);
		this.gainSlider.setPaintLabels(true);
		this.gainSlider.setValue(0);

		GridBagConstraints gbc_gainSlider = new GridBagConstraints();
		gbc_gainSlider.insets = new Insets(0, 5, 0, 5);
		gbc_gainSlider.anchor = GridBagConstraints.NORTHEAST;
		gbc_gainSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_gainSlider.gridwidth = 2;
		gbc_gainSlider.gridx = 1;
		gbc_gainSlider.gridy = 3;
		scanManagementPanel.add(this.gainSlider, gbc_gainSlider);

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

	protected boolean getAutoRange()
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

	protected Thread getScanThread()
	{
		return new LTPScanThread(this);
	}

	protected class LTPScanThread extends TubeStabilityPanel.ScanThread
	{

		public LTPScanThread(LPTStabilityPanel panel)
		{
			super(panel);
		}

		public void run()
		{
			try
			{
				((LPTSTABILITYProgram) ProgramsFacade.getProgram(LPTSTABILITYProgram.PROGRAM_NAME)).initialize();

				try
				{
					super.run();
				}
				finally
				{
					((LPTSTABILITYProgram) ProgramsFacade.getProgram(LPTSTABILITYProgram.PROGRAM_NAME)).terminate();
				}
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), super.panel);
			}
		}

		protected String getScanProgramName()
		{
			return LPTSTABILITYProgram.PROGRAM_NAME;
		}

		protected void addCustomParameters(StabilityParameters scanParameters)
		{
			LPTStabilityPanel panel = (LPTStabilityPanel) this.panel;

			scanParameters.addCustomParameter(LPTSTABILITYProgram.DIM_X, IIDSCCD.DIM_X);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.DIM_Y, IIDSCCD.DIM_Y);
			scanParameters.addCustomParameter(LPTSTABILITYProgram.NUMBER_OF_CAPTURES, Integer.parseInt(panel.numberOfCaptures.getText()));
			scanParameters.addCustomParameter(LPTSTABILITYProgram.STEP_DURATION, Integer.parseInt(panel.captureDuration.getText()));
			scanParameters.addCustomParameter(LPTSTABILITYProgram.COLOR_MODE, IDSCCDColorModes.get_from_index(panel.colorModeCombo.getSelectedIndex()));
			scanParameters.addCustomParameter(LPTSTABILITYProgram.GAIN, panel.gainSlider.getValue());
		}

	}

}
