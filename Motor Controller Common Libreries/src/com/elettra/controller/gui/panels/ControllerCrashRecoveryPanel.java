package com.elettra.controller.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;

public class ControllerCrashRecoveryPanel extends JPanel implements ActionListener
{
	/**
   * 
   */
	private static final long serialVersionUID = -3376307440591186627L;

	static class ActionCommands
	{
		private static final String RUNMINUS = "RUN-";
		private static final String RUNPLUS  = "RUN+";
		private static final String SET      = "SET";
		private static final String REF      = "REF";
		private static final String STOP     = "STOP";
		private static final String MOVE     = "MOVE";
	}

	private int                axis;
	private ICommunicationPort port;
	private JTextField         emergencyDumpTextField;

	private JTextField         axisName;
	private JButton            runPlusButton;
	private JButton            runMinusButton;
	private JButton            refButton;
	private JButton            stopButton;

	/**
	 * Create the panel.
	 * 
	 * @throws IOException
	 */
	public ControllerCrashRecoveryPanel(int axis, ICommunicationPort port) throws IOException
	{
		this.axis = axis;
		this.port = port;

		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 325 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0 };
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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.insets = new Insets(-20, 0, 5, 0);
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		add(tabbedPane, gbc_tabbedPane);

		JPanel recoveryPanel = new JPanel();
		tabbedPane.addTab("Recovery", null, recoveryPanel, null);
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_recoveryPanel = new GridBagLayout();
		gbl_recoveryPanel.columnWidths = new int[] { 0, 0 };
		gbl_recoveryPanel.rowHeights = new int[] { 0, 0 };
		gbl_recoveryPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_recoveryPanel.rowWeights = new double[] { 0.0, 0.0 };
		recoveryPanel.setLayout(gbl_recoveryPanel);

		JLabel lblEmergencyDump = new JLabel("Emergency Dump");
		GridBagConstraints gbc_lblEmergencyDump = new GridBagConstraints();
		gbc_lblEmergencyDump.insets = new Insets(10, 5, 5, 5);
		gbc_lblEmergencyDump.anchor = GridBagConstraints.WEST;
		gbc_lblEmergencyDump.gridx = 0;
		gbc_lblEmergencyDump.gridy = 0;
		recoveryPanel.add(lblEmergencyDump, gbc_lblEmergencyDump);

		emergencyDumpTextField = new JTextField();
		emergencyDumpTextField.setEditable(false);
		emergencyDumpTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_emergencyDumpTextField = new GridBagConstraints();
		gbc_emergencyDumpTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_emergencyDumpTextField.insets = new Insets(10, 0, 5, 5);
		gbc_emergencyDumpTextField.gridx = 1;
		gbc_emergencyDumpTextField.gridy = 0;
		recoveryPanel.add(emergencyDumpTextField, gbc_emergencyDumpTextField);
		emergencyDumpTextField.setColumns(10);
		emergencyDumpTextField.setText(DriverUtilities.formatControllerPositionTextField(this.axis, DriverUtilities.parseAxisPositionResponse(this.axis, DriverUtilities.getEmergencyAxisPosition(this.axis))));

		JButton setButton = new JButton("SET");
		setButton.addActionListener(this);
		setButton.setActionCommand(ActionCommands.SET);
		GridBagConstraints gbc_setButton = new GridBagConstraints();
		gbc_setButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_setButton.insets = new Insets(0, 5, 5, 5);
		gbc_setButton.gridx = 0;
		gbc_setButton.gridy = 1;
		recoveryPanel.add(setButton, gbc_setButton);

		JButton moveButton = new JButton("MOVE");
		moveButton.addActionListener(this);
		moveButton.setActionCommand(ActionCommands.MOVE);
		GridBagConstraints gbc_moveButton = new GridBagConstraints();
		gbc_moveButton.insets = new Insets(0, 0, 5, 5);
		gbc_moveButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_moveButton.gridx = 1;
		gbc_moveButton.gridy = 1;
		recoveryPanel.add(moveButton, gbc_moveButton);
		moveButton.setEnabled(axisConfiguration.isRefEnabled());

		JPanel huberPanel = new JPanel();
		tabbedPane.addTab("Huber", null, huberPanel, null);
		tabbedPane.setForegroundAt(1, new Color(0, 102, 51));

		GridBagLayout gbl_huberPanel = new GridBagLayout();
		gbl_huberPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_huberPanel.rowHeights = new int[] { 0, 0 };
		gbl_huberPanel.columnWeights = new double[] { 0.5, 0.1, 0.1 };
		gbl_huberPanel.rowWeights = new double[] { 1.0, 1.0 };
		huberPanel.setLayout(gbl_huberPanel);

		JLabel lblRun = new JLabel("Run");
		GridBagConstraints gbc_lblRun = new GridBagConstraints();
		gbc_lblRun.anchor = GridBagConstraints.EAST;
		gbc_lblRun.insets = new Insets(10, 0, 5, 5);
		gbc_lblRun.gridx = 0;
		gbc_lblRun.gridy = 0;
		huberPanel.add(lblRun, gbc_lblRun);

		runPlusButton = new JButton("+");
		runPlusButton.setActionCommand(ActionCommands.RUNPLUS);
		runPlusButton.addActionListener(this);
		GridBagConstraints gbc_runPlusButton = new GridBagConstraints();
		gbc_runPlusButton.anchor = GridBagConstraints.EAST;
		gbc_runPlusButton.fill = GridBagConstraints.VERTICAL;
		gbc_runPlusButton.insets = new Insets(10, 0, 5, 5);
		gbc_runPlusButton.gridx = 1;
		gbc_runPlusButton.gridy = 0;
		huberPanel.add(runPlusButton, gbc_runPlusButton);
		runPlusButton.setEnabled(axisConfiguration.isRefEnabled());

		runMinusButton = new JButton("-");
		runMinusButton.setActionCommand(ActionCommands.RUNMINUS);
		runMinusButton.addActionListener(this);
		GridBagConstraints gbc_runMinusButton = new GridBagConstraints();
		gbc_runMinusButton.anchor = GridBagConstraints.WEST;
		gbc_runMinusButton.fill = GridBagConstraints.VERTICAL;
		gbc_runMinusButton.insets = new Insets(10, 0, 5, 50);
		gbc_runMinusButton.gridx = 2;
		gbc_runMinusButton.gridy = 0;
		huberPanel.add(runMinusButton, gbc_runMinusButton);
		runMinusButton.setEnabled(axisConfiguration.isRefEnabled());

		refButton = new JButton(ActionCommands.REF);
		refButton.setActionCommand(ActionCommands.REF);
		refButton.addActionListener(this);
		GridBagConstraints gbc_refButton = new GridBagConstraints();
		gbc_refButton.fill = GridBagConstraints.VERTICAL;
		gbc_refButton.anchor = GridBagConstraints.EAST;
		gbc_refButton.insets = new Insets(0, 0, 5, 5);
		gbc_refButton.gridx = 1;
		gbc_refButton.gridy = 1;
		huberPanel.add(refButton, gbc_refButton);
		refButton.setEnabled(axisConfiguration.isRefEnabled());

		stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);
		stopButton.setForeground(new Color(204, 0, 0));
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.anchor = GridBagConstraints.WEST;
		gbc_stopButton.fill = GridBagConstraints.VERTICAL;
		gbc_stopButton.insets = new Insets(0, 0, 5, 50);
		gbc_stopButton.gridx = 2;
		gbc_stopButton.gridy = 1;
		huberPanel.add(stopButton, gbc_stopButton);
		stopButton.setEnabled(axisConfiguration.isRefEnabled());
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals(ActionCommands.MOVE))
			{
				this.manageEventMove();
			}
			else if (e.getActionCommand().equals(ActionCommands.STOP))
			{
				this.manageEventStop();
			}
			else if (e.getActionCommand().equals(ActionCommands.REF))
			{
				this.manageEventRef();
			}
			else if (e.getActionCommand().equals(ActionCommands.SET))
			{
				this.manageEventSet();
			}
			else if (e.getActionCommand().equals(ActionCommands.RUNPLUS))
			{
				this.manageEventRunPlus();
			}
			else if (e.getActionCommand().equals(ActionCommands.RUNMINUS))
			{
				this.manageEventRunMinus();
			}
		}
		catch (Exception exception)
		{
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}
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
		MoveParameters moveParameters = new MoveParameters(this.axis, GuiUtilities.getNullListener());

		ControllerPosition position = DriverUtilities.parseHuberAxisPosition(this.emergencyDumpTextField.getText());

		moveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters.setSign(position.getSign());
		moveParameters.setPosition(position.getAbsolutePosition());

		new StartMoveProgram(moveParameters, port).start();
	}

	private void manageEventStop() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STOP, new CommandParameters(GuiUtilities.getNullListener()), this.port);
	}

	private void manageEventRef() throws CommunicationPortException
	{
		if (GuiUtilities.showConfirmPopup("Warning: the requested operation is irreversible and it will erase every setting on the controller (SET command)\nContinue?", this))
		{
			CommandsFacade.executeCommand(CommandsFacade.Commands.REF, new CommandParameters(this.axis, GuiUtilities.getNullListener()), this.port);
		}
	}

	private void manageEventSet() throws CommunicationPortException
	{
		if (GuiUtilities.showConfirmPopup("Warning: the requested operation is irreversible and it will alter the positioning system.\nContinue?", this))
		{
			ControllerPosition position = DriverUtilities.parseHuberAxisPosition(this.emergencyDumpTextField.getText());

			CommandsFacade.executeCommand(CommandsFacade.Commands.POS, new CommandParameters(this.axis, position.getSign(), position.getAbsolutePosition(), GuiUtilities.getNullListener()), this.port);
		}
	}

	private void manageEventRunPlus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.RUN, new CommandParameters(this.axis, DriverUtilities.getPlus(), 0.0, GuiUtilities.getNullListener()), this.port);
	}

	private void manageEventRunMinus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.RUN, new CommandParameters(this.axis, DriverUtilities.getMinus(), 0.0, GuiUtilities.getNullListener()), this.port);
	}

	class StartMoveProgram extends Thread
	{
		private ProgramParameters  moveParameters;
		private ICommunicationPort port;

		public StartMoveProgram(ProgramParameters moveParameters, ICommunicationPort port)
		{
			super();
			this.moveParameters = moveParameters;
			this.port = port;
		}

		public void run()
		{
			try
			{
				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters, this.port);
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
