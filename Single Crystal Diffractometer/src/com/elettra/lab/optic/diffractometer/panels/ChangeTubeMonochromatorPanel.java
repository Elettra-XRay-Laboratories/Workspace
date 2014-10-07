package com.elettra.lab.optic.diffractometer.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.lab.optic.diffractometer.Axis;

public class ChangeTubeMonochromatorPanel extends TubeMonocromatorFileManager implements ActionListener
{

	static class ActionCommands
	{
		private static final String LOAD   = "LOAD";
		private static final String CHANGE = "CHANGE";
	}

	ICommunicationPort        port;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1374971020543709026L;
	private JTextField        axis1TextField;
	private JTextField        axis2Position;
	private JTextField        axis2TextField;
	private JTextField        axis1Position;
	private JTextField        axis1StoredPosition;
	private JTextField        axis2StoredPosition;

	private JComboBox<String> monochromatorComboBox;

	private JComboBox<String> xrayTubeComboBox;

	/**
	 * Create the panel.
	 * 
	 * @throws CommunicationPortException
	 */
	public ChangeTubeMonochromatorPanel(ICommunicationPort port) throws CommunicationPortException
	{
		this.port = port;

		ListenerRegister.getInstance().addListener(Axis.THETAFIRST, this);
		ListenerRegister.getInstance().addListener(Axis.TWOTHETAFIRST, this);

		AxisConfiguration axisConfiguration1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.THETAFIRST);
		AxisConfiguration axisConfiguration2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(Axis.TWOTHETAFIRST);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.5, 0.5, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JTabbedPane axisTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_axisTabbedPane = new GridBagConstraints();
		gbc_axisTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_axisTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_axisTabbedPane.gridx = 0;
		gbc_axisTabbedPane.gridy = 0;
		add(axisTabbedPane, gbc_axisTabbedPane);

		JPanel axisPanel = new JPanel();
		axisTabbedPane.addTab("Axis", null, axisPanel, null);
		axisTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_axisPanel = new GridBagLayout();
		gbl_axisPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_axisPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_axisPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_axisPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		axisPanel.setLayout(gbl_axisPanel);

		JLabel lblAxis = new JLabel("Axis 1");
		GridBagConstraints gbc_lblAxis = new GridBagConstraints();
		gbc_lblAxis.insets = new Insets(10, 5, 5, 5);
		gbc_lblAxis.anchor = GridBagConstraints.WEST;
		gbc_lblAxis.gridx = 0;
		gbc_lblAxis.gridy = 0;
		axisPanel.add(lblAxis, gbc_lblAxis);

		axis1TextField = new JTextField();
		axis1TextField.setBackground(new Color(0, 102, 51));
		axis1TextField.setForeground(new Color(255, 255, 255));
		axis1TextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		axis1TextField.setHorizontalAlignment(SwingConstants.CENTER);
		axis1TextField.setEditable(false);
		axis1TextField.setColumns(5);
		axis1TextField.setText(axisConfiguration1.getName());
		GridBagConstraints gbc_axis1TextField = new GridBagConstraints();
		gbc_axis1TextField.insets = new Insets(10, 0, 5, 0);
		gbc_axis1TextField.anchor = GridBagConstraints.WEST;
		gbc_axis1TextField.gridx = 1;
		gbc_axis1TextField.gridy = 0;
		axisPanel.add(axis1TextField, gbc_axis1TextField);

		JLabel lblAxis_1 = new JLabel("Axis 2");
		GridBagConstraints gbc_lblAxis_1 = new GridBagConstraints();
		gbc_lblAxis_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblAxis_1.anchor = GridBagConstraints.WEST;
		gbc_lblAxis_1.gridx = 0;
		gbc_lblAxis_1.gridy = 1;
		axisPanel.add(lblAxis_1, gbc_lblAxis_1);

		axis2TextField = new JTextField();
		axis2TextField.setHorizontalAlignment(SwingConstants.CENTER);
		axis2TextField.setForeground(Color.WHITE);
		axis2TextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		axis2TextField.setEditable(false);
		axis2TextField.setColumns(5);
		axis2TextField.setBackground(new Color(0, 102, 51));
		axis2TextField.setText(axisConfiguration2.getName());
		GridBagConstraints gbc_axis2TextField = new GridBagConstraints();
		gbc_axis2TextField.anchor = GridBagConstraints.WEST;
		gbc_axis2TextField.insets = new Insets(0, 0, 5, 0);
		gbc_axis2TextField.gridx = 1;
		gbc_axis2TextField.gridy = 1;
		axisPanel.add(axis2TextField, gbc_axis2TextField);

		JLabel lblAxisCurrent = new JLabel("Axis 1 Current Position");
		GridBagConstraints gbc_lblAxisCurrent = new GridBagConstraints();
		gbc_lblAxisCurrent.insets = new Insets(10, 5, 5, 5);
		gbc_lblAxisCurrent.anchor = GridBagConstraints.EAST;
		gbc_lblAxisCurrent.gridx = 0;
		gbc_lblAxisCurrent.gridy = 2;
		axisPanel.add(lblAxisCurrent, gbc_lblAxisCurrent);

		axis1Position = new JTextField();
		axis1Position.setEditable(false);
		axis1Position.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_axis1Position = new GridBagConstraints();
		gbc_axis1Position.anchor = GridBagConstraints.WEST;
		gbc_axis1Position.insets = new Insets(10, 0, 5, 0);
		gbc_axis1Position.gridx = 1;
		gbc_axis1Position.gridy = 2;
		axisPanel.add(axis1Position, gbc_axis1Position);
		axis1Position.setColumns(10);
		axis1Position.setText(this.readAxisPosition(Axis.THETAFIRST));

		JLabel lblAxisCurrent_1 = new JLabel("Axis 2 Current Position");
		GridBagConstraints gbc_lblAxisCurrent_1 = new GridBagConstraints();
		gbc_lblAxisCurrent_1.insets = new Insets(0, 5, 0, 5);
		gbc_lblAxisCurrent_1.anchor = GridBagConstraints.EAST;
		gbc_lblAxisCurrent_1.gridx = 0;
		gbc_lblAxisCurrent_1.gridy = 3;
		axisPanel.add(lblAxisCurrent_1, gbc_lblAxisCurrent_1);

		axis2Position = new JTextField();
		axis2Position.setEditable(false);
		axis2Position.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_axis2Position = new GridBagConstraints();
		gbc_axis2Position.anchor = GridBagConstraints.WEST;
		gbc_axis2Position.gridx = 1;
		gbc_axis2Position.gridy = 3;
		axisPanel.add(axis2Position, gbc_axis2Position);
		axis2Position.setColumns(10);
		axis2Position.setText(this.readAxisPosition(Axis.TWOTHETAFIRST));

		JTabbedPane commandsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_commandsTabbedPane = new GridBagConstraints();
		gbc_commandsTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_commandsTabbedPane.gridx = 0;
		gbc_commandsTabbedPane.gridy = 1;
		add(commandsTabbedPane, gbc_commandsTabbedPane);

		JPanel commandsPanel = new JPanel();
		commandsTabbedPane.addTab("Commands", null, commandsPanel, null);
		commandsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_commandsPanel = new GridBagLayout();
		gbl_commandsPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_commandsPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_commandsPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_commandsPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		commandsPanel.setLayout(gbl_commandsPanel);

		JLabel lblXrayTube = new JLabel("X-Ray Tube");
		GridBagConstraints gbc_lblXrayTube = new GridBagConstraints();
		gbc_lblXrayTube.insets = new Insets(10, 5, 5, 5);
		gbc_lblXrayTube.anchor = GridBagConstraints.WEST;
		gbc_lblXrayTube.gridx = 0;
		gbc_lblXrayTube.gridy = 0;
		commandsPanel.add(lblXrayTube, gbc_lblXrayTube);

		xrayTubeComboBox = new JComboBox<String>();
		xrayTubeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { Filaments.CHROME, Filaments.COPPER, Filaments.MOLIBDENUM, Filaments.SILVER, Filaments.TUNGSTEN }));
		xrayTubeComboBox.setMaximumRowCount(5);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.WEST;
		gbc_comboBox.insets = new Insets(10, 0, 5, 0);
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		commandsPanel.add(xrayTubeComboBox, gbc_comboBox);

		JLabel lblMonochromator = new JLabel("Monochromator");
		GridBagConstraints gbc_lblMonochromator = new GridBagConstraints();
		gbc_lblMonochromator.insets = new Insets(0, 5, 5, 5);
		gbc_lblMonochromator.anchor = GridBagConstraints.WEST;
		gbc_lblMonochromator.gridx = 0;
		gbc_lblMonochromator.gridy = 1;
		commandsPanel.add(lblMonochromator, gbc_lblMonochromator);

		monochromatorComboBox = new JComboBox<String>();
		monochromatorComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { Monochromators.SI_1_1_1 }));
		monochromatorComboBox.setMaximumRowCount(1);
		GridBagConstraints gbc_comboBox2 = new GridBagConstraints();
		gbc_comboBox2.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox2.anchor = GridBagConstraints.WEST;
		gbc_comboBox2.gridx = 1;
		gbc_comboBox2.gridy = 1;
		commandsPanel.add(monochromatorComboBox, gbc_comboBox2);

		JLabel lblAxisStored = new JLabel("Axis 1 Stored Position");
		GridBagConstraints gbc_lblAxisStored = new GridBagConstraints();
		gbc_lblAxisStored.insets = new Insets(10, 5, 5, 5);
		gbc_lblAxisStored.anchor = GridBagConstraints.WEST;
		gbc_lblAxisStored.gridx = 0;
		gbc_lblAxisStored.gridy = 2;
		commandsPanel.add(lblAxisStored, gbc_lblAxisStored);

		axis1StoredPosition = new JTextField();
		axis1StoredPosition.setEditable(false);
		axis1StoredPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_axis1StoredPosition = new GridBagConstraints();
		gbc_axis1StoredPosition.anchor = GridBagConstraints.WEST;
		gbc_axis1StoredPosition.insets = new Insets(10, 0, 5, 0);
		gbc_axis1StoredPosition.gridx = 1;
		gbc_axis1StoredPosition.gridy = 2;
		commandsPanel.add(axis1StoredPosition, gbc_axis1StoredPosition);
		axis1StoredPosition.setColumns(10);

		JLabel lblAxisStored_1 = new JLabel("Axis 2 Stored Position");
		GridBagConstraints gbc_lblAxisStored_1 = new GridBagConstraints();
		gbc_lblAxisStored_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblAxisStored_1.anchor = GridBagConstraints.WEST;
		gbc_lblAxisStored_1.gridx = 0;
		gbc_lblAxisStored_1.gridy = 3;
		commandsPanel.add(lblAxisStored_1, gbc_lblAxisStored_1);

		axis2StoredPosition = new JTextField();
		axis2StoredPosition.setEditable(false);
		axis2StoredPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_axis2StoredPosition = new GridBagConstraints();
		gbc_axis2StoredPosition.insets = new Insets(0, 0, 5, 0);
		gbc_axis2StoredPosition.anchor = GridBagConstraints.WEST;
		gbc_axis2StoredPosition.gridx = 1;
		gbc_axis2StoredPosition.gridy = 3;
		commandsPanel.add(axis2StoredPosition, gbc_axis2StoredPosition);
		axis2StoredPosition.setColumns(10);

		JButton loadSettingsButton = new JButton("LOAD SETTINGS");
		loadSettingsButton.addActionListener(this);
		loadSettingsButton.setActionCommand(ActionCommands.LOAD);
		GridBagConstraints gbc_loadSettingsButton = new GridBagConstraints();
		gbc_loadSettingsButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_loadSettingsButton.gridwidth = 2;
		gbc_loadSettingsButton.insets = new Insets(10, 5, 5, 5);
		gbc_loadSettingsButton.gridx = 0;
		gbc_loadSettingsButton.gridy = 4;
		commandsPanel.add(loadSettingsButton, gbc_loadSettingsButton);

		JButton changeButton = new JButton("CHANGE TUBE/MONOCHROMATOR");
		changeButton.addActionListener(this);
		changeButton.setActionCommand(ActionCommands.CHANGE);
		GridBagConstraints gbc_changeButton = new GridBagConstraints();
		gbc_changeButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_changeButton.gridwidth = 2;
		gbc_changeButton.insets = new Insets(0, 5, 5, 5);
		gbc_changeButton.gridx = 0;
		gbc_changeButton.gridy = 5;
		commandsPanel.add(changeButton, gbc_changeButton);
	}

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		new MoveListener(axis, this).start();
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals(ActionCommands.LOAD))
				this.manageEventLoadSettings();
			else if (e.getActionCommand().equals(ActionCommands.CHANGE))
				this.manageEventChangeTubeMonochromator();
		}
		catch (Exception exception)
		{
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}

	}

	protected void setAxisPositionField(int axis, String text)
	{
		if (axis == Axis.THETAFIRST)
			this.axis1Position.setText(text);
		else if (axis == Axis.TWOTHETAFIRST)
			this.axis2Position.setText(text);
	}

	private String readAxisPosition(int axis) throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis, position);
	}

	private void manageEventLoadSettings() throws IOException
	{
		String xrayTube = (String) this.xrayTubeComboBox.getSelectedItem();
		String monochromator = (String) this.monochromatorComboBox.getSelectedItem();

		String fileName = this.getFileName(xrayTube, monochromator);

		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		try
		{
			String row = reader.readLine();

			if (row == null)
				throw new IllegalArgumentException("Configuration File is Empty");

			StringTokenizer tokenizer = new StringTokenizer(row, "&");

			if (tokenizer.countTokens() != 2)
				throw new IllegalArgumentException("Configuration row not recognized");

			this.axis1StoredPosition.setText(tokenizer.nextToken());
			this.axis2StoredPosition.setText(tokenizer.nextToken());
		}
		finally
		{
			reader.close();
		}
	}

	private void manageEventChangeTubeMonochromator() throws CommunicationPortException
	{
		double thetaFirst = DriverUtilities.parseHuberAxisPosition(axis1StoredPosition.getText()).getSignedPosition();
		double twoThetaFirst = DriverUtilities.parseHuberAxisPosition(axis2StoredPosition.getText()).getSignedPosition();

		if (GuiUtilities.showConfirmPopup("Confirm change of position of X-ray Tube/Monochromator with the following settings?\n\nTheta'       : " + thetaFirst + "\n2Theta'      : " + twoThetaFirst + "\n ", this))
		{
			ControllerPosition thetaFirstPosition = DriverUtilities.numberToController(thetaFirst);
			ControllerPosition twoThetaFirstPosition = DriverUtilities.numberToController(twoThetaFirst);

			MoveParameters moveParameters1 = new MoveParameters(Axis.THETAFIRST, ListenerRegister.getInstance());
			moveParameters1.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters1.setSign(thetaFirstPosition.getSign());
			moveParameters1.setPosition(thetaFirstPosition.getAbsolutePosition());

			MoveParameters moveParameters2 = new MoveParameters(Axis.TWOTHETAFIRST, ListenerRegister.getInstance());
			moveParameters2.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters2.setSign(twoThetaFirstPosition.getSign());
			moveParameters2.setPosition(twoThetaFirstPosition.getAbsolutePosition());

			new ChangeTubeMonocromator(moveParameters1, moveParameters2, this.port).start();
		}
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

	class ChangeTubeMonocromator extends Thread
	{
		private MoveParameters     moveParameters1;
		private MoveParameters     moveParameters2;
		private ICommunicationPort port;

		public ChangeTubeMonocromator(MoveParameters moveParameters1, MoveParameters moveParameters2, ICommunicationPort port)
		{
			super();

			this.moveParameters1 = moveParameters1;
			this.moveParameters2 = moveParameters2;
			this.port = port;
		}

		public void run()
		{
			try
			{
				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters1, this.port);

				CommandsFacade.waitForTheEndOfMovement(new CommandParameters(this.moveParameters1.getAxis(), ListenerRegister.getInstance()), this.port);

				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters2, this.port);
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

	class MoveListener extends Thread
	{
		/**
		 * 
		 */
		private ChangeTubeMonochromatorPanel panel;
		private int                          axis;

		public MoveListener(int axis, ChangeTubeMonochromatorPanel panel)
		{
			this.panel = panel;
			this.axis = axis;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

				double previousPosition = controllerPosition.getSignedPosition();
				double currentPosition = previousPosition;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(axis, DriverUtilities.formatControllerPositionTextField(axis, controllerPosition));

					ObjectUtilities.pause(200);

					controllerPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

					previousPosition = currentPosition;
					currentPosition = controllerPosition.getSignedPosition();

					if (currentPosition == previousPosition)
						doCycle = ++trial < 5;

				} while (doCycle);
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
}
