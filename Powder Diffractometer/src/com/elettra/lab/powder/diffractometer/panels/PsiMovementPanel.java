package com.elettra.lab.powder.diffractometer.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.lab.powder.diffractometer.Axis;
import com.elettra.lab.powder.diffractometer.panels.PsiUtilities.SampleHolder;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class PsiMovementPanel extends MovementListener implements ActionListener
{
	static class SampleHolderTxt
	{
		private static final String PHI_MOTOR_TXT = "Phi Motor";
		private static final String STATIC_TXT    = "Static";
		private static final String SPINNER_TXT   = "Spinner";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 986591557129181448L;

	static class ActionCommands
	{
		private static final String CALCULATE = "CALCULATE";
		private static final String MOVE      = "MOVE";
	}

	private ICommunicationPort port;
	private boolean            isScanActive;

	private AxisConfiguration  axisConfiguration;
	private AxisConfiguration  axisZConfiguration;
	private AxisConfiguration  axisAlphaConfiguration;
	private AxisConfiguration  axisBetaConfiguration;

	private JTextField         axisName;
	private JTextField         axisZPosition;
	private JTextField         axisAlphaPosition;
	private JTextField         axisBetaPosition;
	private JComboBox          zSignComboBox;
	private JComboBox          signComboBox;
	private JTextField         position;
	private JTextField         measureUnit;
	private JTextField         zMeasureUnit;
	private JTextField         zPosition;
	private JButton            calculateButton;
	private JButton            moveButton;
	private JTextField         axisZPositionOut;
	private JTextField         axisAlphaPositionOut;
	private JTextField         axisBetaPositionOut;
	private JTextField         zMeasureUnitOut;
	private JTextField         alphaMeasureUnitOut;
	private JTextField         betaMeasureUnitOut;
	private JLabel             lblNewLabel;
	private JTextField         psiMovingLabel;
	private JLabel             lblSample;
	private JComboBox          sampleHolderComboBox;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public PsiMovementPanel(ICommunicationPort port) throws IOException
	{
		this.isScanActive = false;
		this.port = port;

		axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.PSI);

		axisZConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.Z);
		axisAlphaConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.ALPHA);
		axisBetaConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.BETA);

		ListenerRegister.getInstance().addListener(Axis.ALPHA, this);
		ListenerRegister.getInstance().addListener(Axis.BETA, this);
		ListenerRegister.getInstance().addListener(Axis.Z, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 245 };
		gridBagLayout.rowHeights = new int[] { 20, 100, 240 };
		gridBagLayout.columnWeights = new double[] { 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
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
		gbl_panelTop.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelTop.rowHeights = new int[] { 0, 0 };
		gbl_panelTop.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelTop.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelTop.setLayout(gbl_panelTop);

		JLabel lblAxis = new JLabel("Axis");
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
		gbc_axisName.anchor = GridBagConstraints.EAST;
		gbc_axisName.gridx = 1;
		gbc_axisName.gridy = 0;
		panelTop.add(axisName, gbc_axisName);
		axisName.setColumns(10);
		axisName.setText(axisConfiguration.getName());

		JTabbedPane tabbedPaneUp = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneUp = new GridBagConstraints();
		gbc_tabbedPaneUp.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneUp.insets = new Insets(-20, 0, 5, 0);
		gbc_tabbedPaneUp.gridx = 0;
		gbc_tabbedPaneUp.gridy = 1;
		add(tabbedPaneUp, gbc_tabbedPaneUp);

		JPanel panelUp = new JPanel();
		tabbedPaneUp.addTab("Huber", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 145, 100 };
		gbl_panelUp.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panelUp.rowWeights = new double[] { 1.0, 1.0, 1.0 };
		panelUp.setLayout(gbl_panelUp);

		JLabel axis1Label = new JLabel(axisZConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis1Label = new GridBagConstraints();
		gbc_axis1Label.anchor = GridBagConstraints.EAST;
		gbc_axis1Label.insets = new Insets(10, 5, 5, 5);
		gbc_axis1Label.gridx = 0;
		gbc_axis1Label.gridy = 0;
		panelUp.add(axis1Label, gbc_axis1Label);

		axisZPosition = new JTextField();
		axisZPosition.setEditable(false);
		GridBagConstraints gbc_axis1Position = new GridBagConstraints();
		gbc_axis1Position.anchor = GridBagConstraints.WEST;
		gbc_axis1Position.insets = new Insets(10, 0, 5, 0);
		gbc_axis1Position.gridx = 1;
		gbc_axis1Position.gridy = 0;
		panelUp.add(axisZPosition, gbc_axis1Position);
		axisZPosition.setColumns(10);
		axisZPosition.setText(this.readAxisPosition(Axis.Z));

		JLabel axis2Label = new JLabel(axisAlphaConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis2Label = new GridBagConstraints();
		gbc_axis2Label.insets = new Insets(0, 5, 5, 5);
		gbc_axis2Label.anchor = GridBagConstraints.EAST;
		gbc_axis2Label.gridx = 0;
		gbc_axis2Label.gridy = 1;
		panelUp.add(axis2Label, gbc_axis2Label);

		axisAlphaPosition = new JTextField();
		axisAlphaPosition.setEditable(false);
		GridBagConstraints gbc_axis2Position = new GridBagConstraints();
		gbc_axis2Position.anchor = GridBagConstraints.WEST;
		gbc_axis2Position.insets = new Insets(0, 0, 5, 0);
		gbc_axis2Position.gridx = 1;
		gbc_axis2Position.gridy = 1;
		panelUp.add(axisAlphaPosition, gbc_axis2Position);
		axisAlphaPosition.setColumns(10);
		axisAlphaPosition.setText(this.readAxisPosition(Axis.ALPHA));

		JLabel axis3Label = new JLabel(axisBetaConfiguration.getName() + " Current Position");
		GridBagConstraints gbc_axis3Label = new GridBagConstraints();
		gbc_axis3Label.insets = new Insets(0, 5, 10, 5);
		gbc_axis3Label.anchor = GridBagConstraints.EAST;
		gbc_axis3Label.gridx = 0;
		gbc_axis3Label.gridy = 2;
		panelUp.add(axis3Label, gbc_axis3Label);

		axisBetaPosition = new JTextField();
		axisBetaPosition.setEditable(false);
		GridBagConstraints gbc_axis3Position = new GridBagConstraints();
		gbc_axis3Position.anchor = GridBagConstraints.WEST;
		gbc_axis3Position.insets = new Insets(0, 0, 10, 0);
		gbc_axis3Position.gridx = 1;
		gbc_axis3Position.gridy = 2;
		panelUp.add(axisBetaPosition, gbc_axis3Position);
		axisBetaPosition.setColumns(10);
		axisBetaPosition.setText(this.readAxisPosition(Axis.BETA));

		JTabbedPane tabbedPaneDown = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneDown = new GridBagConstraints();
		gbc_tabbedPaneDown.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneDown.gridx = 0;
		gbc_tabbedPaneDown.gridy = 2;
		add(tabbedPaneDown, gbc_tabbedPaneDown);

		JPanel panelDown = new JPanel();
		tabbedPaneDown.addTab("Commands", null, panelDown, null);
		tabbedPaneDown.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelDown = new GridBagLayout();
		gbl_panelDown.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panelDown.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelDown.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
		gbl_panelDown.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		panelDown.setLayout(gbl_panelDown);

		JLabel lblSampleZPos = new JLabel("Z at Psi=0");
		GridBagConstraints gbc_lblSampleZPos = new GridBagConstraints();
		gbc_lblSampleZPos.anchor = GridBagConstraints.WEST;
		gbc_lblSampleZPos.fill = GridBagConstraints.VERTICAL;
		gbc_lblSampleZPos.insets = new Insets(5, 5, 5, 5);
		gbc_lblSampleZPos.gridx = 0;
		gbc_lblSampleZPos.gridy = 0;
		panelDown.add(lblSampleZPos, gbc_lblSampleZPos);

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
		gbc_zSignComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_zSignComboBox.insets = new Insets(5, 0, 5, 5);
		gbc_zSignComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_zSignComboBox.gridx = 1;
		gbc_zSignComboBox.gridy = 0;
		panelDown.add(zSignComboBox, gbc_zSignComboBox);

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
		gbc_zPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_zPosition.insets = new Insets(5, 0, 5, 5);
		gbc_zPosition.gridx = 2;
		gbc_zPosition.gridy = 0;
		panelDown.add(zPosition, gbc_zPosition);
		zPosition.setColumns(8);

		zMeasureUnit = new JTextField();
		zMeasureUnit.setEditable(false);
		zMeasureUnit.setText(axisZConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_zMeasureUnit = new GridBagConstraints();
		gbc_zMeasureUnit.anchor = GridBagConstraints.WEST;
		gbc_zMeasureUnit.insets = new Insets(5, 0, 5, 5);
		gbc_zMeasureUnit.gridx = 3;
		gbc_zMeasureUnit.gridy = 0;
		panelDown.add(zMeasureUnit, gbc_zMeasureUnit);
		zMeasureUnit.setColumns(3);

		JLabel lblPosition = new JLabel("Psi");
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.anchor = GridBagConstraints.WEST;
		gbc_lblPosition.insets = new Insets(0, 5, 5, 5);
		gbc_lblPosition.gridx = 0;
		gbc_lblPosition.gridy = 1;
		panelDown.add(lblPosition, gbc_lblPosition);

		signComboBox = new JComboBox();
		signComboBox.setModel(new DefaultComboBoxModel(new String[] { "+", "-" }));
		signComboBox.setSelectedIndex(0);
		signComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_signComboBox = new GridBagConstraints();
		gbc_signComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_signComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_signComboBox.gridx = 1;
		gbc_signComboBox.gridy = 1;
		panelDown.add(signComboBox, gbc_signComboBox);

		position = new JTextField();
		position.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					position.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(position.getText()))));
				}
				catch (Throwable t)
				{
					position.setText("<not a number>");
				}
			}
		});
		position.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					position.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(position.getText()))));
				}
				catch (Throwable t)
				{
					position.setText("<not a number>");
				}
			}
		});
		position.setText(DriverUtilities.formatControllerPosition(0));
		position.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_position = new GridBagConstraints();
		gbc_position.fill = GridBagConstraints.HORIZONTAL;
		gbc_position.insets = new Insets(0, 0, 5, 5);
		gbc_position.gridx = 2;
		gbc_position.gridy = 1;
		panelDown.add(position, gbc_position);
		position.setColumns(8);

		measureUnit = new JTextField();
		measureUnit.setEditable(false);
		measureUnit.setText(axisConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_measureUnit = new GridBagConstraints();
		gbc_measureUnit.anchor = GridBagConstraints.WEST;
		gbc_measureUnit.insets = new Insets(0, 0, 5, 5);
		gbc_measureUnit.gridx = 3;
		gbc_measureUnit.gridy = 1;
		panelDown.add(measureUnit, gbc_measureUnit);
		measureUnit.setColumns(3);

		calculateButton = new JButton(ActionCommands.CALCULATE);
		calculateButton.setActionCommand(ActionCommands.CALCULATE);
		calculateButton.addActionListener(this);

		lblSample = new JLabel("Sample Holder");
		GridBagConstraints gbc_lblSample = new GridBagConstraints();
		gbc_lblSample.gridwidth = 2;
		gbc_lblSample.anchor = GridBagConstraints.WEST;
		gbc_lblSample.insets = new Insets(0, 5, 5, 5);
		gbc_lblSample.gridx = 0;
		gbc_lblSample.gridy = 2;
		panelDown.add(lblSample, gbc_lblSample);

		sampleHolderComboBox = new JComboBox();
		sampleHolderComboBox.setModel(new DefaultComboBoxModel(new String[] { SampleHolderTxt.SPINNER_TXT, SampleHolderTxt.STATIC_TXT, SampleHolderTxt.PHI_MOTOR_TXT }));
		sampleHolderComboBox.setSelectedIndex(0);
		sampleHolderComboBox.setMaximumRowCount(3);
		GridBagConstraints gbc_sampleHolderComboBox = new GridBagConstraints();
		gbc_sampleHolderComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sampleHolderComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_sampleHolderComboBox.gridx = 2;
		gbc_sampleHolderComboBox.gridy = 2;
		panelDown.add(sampleHolderComboBox, gbc_sampleHolderComboBox);

		GridBagConstraints gbc_calculateButton = new GridBagConstraints();
		gbc_calculateButton.anchor = GridBagConstraints.WEST;
		gbc_calculateButton.insets = new Insets(0, 0, 5, 0);
		gbc_calculateButton.gridx = 2;
		gbc_calculateButton.gridy = 3;
		gbc_calculateButton.gridwidth = 2;
		panelDown.add(calculateButton, gbc_calculateButton);

		JLabel axis1LabelOut = new JLabel(axisZConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis1LabelOut = new GridBagConstraints();
		gbc_axis1LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis1LabelOut.insets = new Insets(5, 5, 5, 5);
		gbc_axis1LabelOut.gridx = 0;
		gbc_axis1LabelOut.gridy = 4;
		gbc_axis1LabelOut.gridwidth = 2;
		panelDown.add(axis1LabelOut, gbc_axis1LabelOut);

		axisZPositionOut = new JTextField();
		axisZPositionOut.setEditable(false);
		GridBagConstraints gbc_axis1PositionOut = new GridBagConstraints();
		gbc_axis1PositionOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_axis1PositionOut.insets = new Insets(5, 0, 5, 5);
		gbc_axis1PositionOut.gridx = 2;
		gbc_axis1PositionOut.gridy = 4;
		panelDown.add(axisZPositionOut, gbc_axis1PositionOut);
		axisZPositionOut.setColumns(10);
		axisZPositionOut.setText("");

		zMeasureUnitOut = new JTextField();
		zMeasureUnitOut.setEditable(false);
		zMeasureUnitOut.setText(axisZConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_zMeasureUnitOut = new GridBagConstraints();
		gbc_zMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_zMeasureUnitOut.insets = new Insets(5, 0, 5, 5);
		gbc_zMeasureUnitOut.gridx = 3;
		gbc_zMeasureUnitOut.gridy = 4;
		panelDown.add(zMeasureUnitOut, gbc_zMeasureUnitOut);
		zMeasureUnitOut.setColumns(3);

		JLabel axis2LabelOut = new JLabel(axisAlphaConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis2LabelOut = new GridBagConstraints();
		gbc_axis2LabelOut.insets = new Insets(0, 5, 5, 5);
		gbc_axis2LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis2LabelOut.gridx = 0;
		gbc_axis2LabelOut.gridy = 5;
		gbc_axis2LabelOut.gridwidth = 2;
		panelDown.add(axis2LabelOut, gbc_axis2LabelOut);

		axisAlphaPositionOut = new JTextField();
		axisAlphaPositionOut.setEditable(false);
		GridBagConstraints gbc_axis2PositionOut = new GridBagConstraints();
		gbc_axis2PositionOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_axis2PositionOut.insets = new Insets(0, 0, 5, 5);
		gbc_axis2PositionOut.gridx = 2;
		gbc_axis2PositionOut.gridy = 5;
		panelDown.add(axisAlphaPositionOut, gbc_axis2PositionOut);
		axisAlphaPositionOut.setColumns(10);
		axisAlphaPositionOut.setText("");

		alphaMeasureUnitOut = new JTextField();
		alphaMeasureUnitOut.setEditable(false);
		alphaMeasureUnitOut.setText(axisAlphaConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_alphaMeasureUnitOut = new GridBagConstraints();
		gbc_alphaMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_alphaMeasureUnitOut.insets = new Insets(0, 0, 5, 5);
		gbc_alphaMeasureUnitOut.gridx = 3;
		gbc_alphaMeasureUnitOut.gridy = 5;
		panelDown.add(alphaMeasureUnitOut, gbc_alphaMeasureUnitOut);
		alphaMeasureUnitOut.setColumns(3);

		JLabel axis3LabelOut = new JLabel(axisBetaConfiguration.getName() + " Out");
		GridBagConstraints gbc_axis3LabelOut = new GridBagConstraints();
		gbc_axis3LabelOut.insets = new Insets(0, 5, 10, 5);
		gbc_axis3LabelOut.anchor = GridBagConstraints.WEST;
		gbc_axis3LabelOut.gridx = 0;
		gbc_axis3LabelOut.gridy = 6;
		gbc_axis3LabelOut.gridwidth = 2;
		panelDown.add(axis3LabelOut, gbc_axis3LabelOut);

		axisBetaPositionOut = new JTextField();
		axisBetaPositionOut.setEditable(false);
		GridBagConstraints gbc_axis3PositionOut = new GridBagConstraints();
		gbc_axis3PositionOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_axis3PositionOut.insets = new Insets(0, 0, 10, 5);
		gbc_axis3PositionOut.gridx = 2;
		gbc_axis3PositionOut.gridy = 6;
		panelDown.add(axisBetaPositionOut, gbc_axis3PositionOut);
		axisBetaPositionOut.setColumns(10);
		axisBetaPositionOut.setText("");

		betaMeasureUnitOut = new JTextField();
		betaMeasureUnitOut.setEditable(false);
		betaMeasureUnitOut.setText(axisBetaConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_betaMeasureUnitOut = new GridBagConstraints();
		gbc_betaMeasureUnitOut.anchor = GridBagConstraints.WEST;
		gbc_betaMeasureUnitOut.insets = new Insets(0, 0, 10, 5);
		gbc_betaMeasureUnitOut.gridx = 3;
		gbc_betaMeasureUnitOut.gridy = 6;
		panelDown.add(betaMeasureUnitOut, gbc_betaMeasureUnitOut);
		betaMeasureUnitOut.setColumns(3);

		moveButton = new JButton(ActionCommands.MOVE);
		moveButton.setActionCommand(ActionCommands.MOVE);
		moveButton.addActionListener(this);
		moveButton.setEnabled(false);

		lblNewLabel = new JLabel("Psi Moving");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 5, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 7;
		panelDown.add(lblNewLabel, gbc_lblNewLabel);

		psiMovingLabel = new JTextField("");
		psiMovingLabel.setEditable(false);
		psiMovingLabel.setColumns(2);
		psiMovingLabel.setForeground(new Color(0, 102, 51));
		psiMovingLabel.setBackground(new Color(0, 102, 51));
		GridBagConstraints gbc_psiMovingLabel = new GridBagConstraints();
		gbc_psiMovingLabel.insets = new Insets(0, 0, 5, 5);
		gbc_psiMovingLabel.gridx = 1;
		gbc_psiMovingLabel.gridy = 7;
		panelDown.add(psiMovingLabel, gbc_psiMovingLabel);

		GridBagConstraints gbc_moveButton = new GridBagConstraints();
		gbc_moveButton.anchor = GridBagConstraints.WEST;
		gbc_moveButton.insets = new Insets(0, 0, 5, 0);
		gbc_moveButton.gridx = 2;
		gbc_moveButton.gridy = 7;
		gbc_moveButton.gridwidth = 2;
		panelDown.add(moveButton, gbc_moveButton);

	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.MOVE))
				{
					this.manageEventMove();
				}
				else if (e.getActionCommand().equals(ActionCommands.CALCULATE))
				{
					this.manageEventCalculate();
				}
			}
			catch (CommunicationPortException exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}
		else
		{
			GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
		}
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		new EnableThread(this, enabled).start();
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

	private void manageEventMove() throws CommunicationPortException
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

	private void manageEventCalculate() throws CommunicationPortException
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
			zPosition.setText("<not a number>");

			GuiUtilities.showErrorPopup("Z Position should be a number (double)", this);
		}

		try
		{
			if (!isError)
				psiPosition = new ControllerPosition(DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()), Double.parseDouble(this.position.getText()));
		}
		catch (NumberFormatException exception)
		{
			isError = true;
			position.setText("<not a number>");

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

			this.moveButton.setEnabled(true);
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
		private PsiMovementPanel   panel;
		private PsiMoveParameters  moveParameters;
		private ICommunicationPort port;

		public StartPsiMoveProgram(PsiMovementPanel panel, PsiMoveParameters moveParameters, ICommunicationPort port)
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
				this.panel.moveButton.setEnabled(false);

				this.panel.psiMovingLabel.setForeground(new Color(0, 204, 51));
				this.panel.psiMovingLabel.setBackground(new Color(0, 204, 51));

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
		private PsiMovementPanel panel;

		public PsiMoveListener(PsiMovementPanel panel)
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

				this.panel.psiMovingLabel.setForeground(new Color(51, 102, 51));
				this.panel.psiMovingLabel.setBackground(new Color(51, 102, 51));

				this.panel.moveButton.setEnabled(false);
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

	class EnableThread extends Thread
	{
		private PsiMovementPanel panel;
		private boolean          enabled;

		public EnableThread(PsiMovementPanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.axisName.setEnabled(enabled);
				this.panel.zSignComboBox.setEnabled(enabled);
				this.panel.zPosition.setEnabled(enabled);
				this.panel.zMeasureUnit.setEnabled(enabled);
				this.panel.signComboBox.setEnabled(enabled);
				this.panel.position.setEnabled(enabled);
				this.panel.measureUnit.setEnabled(enabled);
				this.panel.moveButton.setEnabled(false);
				this.panel.calculateButton.setEnabled(enabled);
				this.panel.sampleHolderComboBox.setEnabled(enabled);
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
}
