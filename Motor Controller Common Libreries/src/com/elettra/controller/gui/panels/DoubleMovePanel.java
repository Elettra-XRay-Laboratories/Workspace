package com.elettra.controller.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
import com.elettra.controller.driver.programs.DoubleMoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DoubleMovePanel extends MovementListener implements ActionListener
{
	static class ActionCommands
	{
		private static final String STOP = "STOP";
		private static final String MOVE = "MOVE";
	}

	/**
	 * 
	 */
	private static final long  serialVersionUID = 768167582629608648L;

	private int                axis;
	private int                axis1;
	private int                axis2;
	private ICommunicationPort port;

	private JTextField         axis1Position;
	private JTextField         measureUnit;
	private JTextField         position;
	private JComboBox          signComboBox;
	private JComboBox          kindOfMovementComboBox;
	private JTextField         axisName;

	private boolean            isScanActive;
	private JTextField         axis2Position;

	private JButton            moveButton;

	private JButton            stopButton;

	private JComboBox          referenceAxisComboBox;

	/**
	 * Create the panel.
	 * 
	 * @throws CommunicationPortException
	 */
	public DoubleMovePanel(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		this.isScanActive = false;
		this.port = port;
		this.axis = axis;

		AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis);

		if (!axisConfiguration.isMultiple())
			throw new IllegalArgumentException("Panel is for a multiple axis");

		this.axis1 = axisConfiguration.getMultipleAxis().getAxis1();
		this.axis2 = axisConfiguration.getMultipleAxis().getAxis2();

		AxisConfiguration axis1Configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis1);
		AxisConfiguration axis2Configuration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis2);

		ListenerRegister.getInstance().addListener(axis1, this);
		ListenerRegister.getInstance().addListener(axis2, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 240, 0 };
		gridBagLayout.rowHeights = new int[] { 20, 225, 110, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		axisName.setColumns(12);
		axisName.setText(axisConfiguration.getName());

		JTabbedPane tabbedPaneUp = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneUp = new GridBagConstraints();
		gbc_tabbedPaneUp.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneUp.insets = new Insets(-20, 0, 5, 0);
		gbc_tabbedPaneUp.gridx = 0;
		gbc_tabbedPaneUp.gridy = 1;
		add(tabbedPaneUp, gbc_tabbedPaneUp);

		JPanel panelUp = new JPanel();
		tabbedPaneUp.addTab("Controller", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 80, 20, 20, 0 };
		gbl_panelUp.rowHeights = new int[] { 40, 0, 30, 10, 10, 10, 30, 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelUp.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelUp.setLayout(gbl_panelUp);

		JLabel axis1Label = new JLabel(axis1Configuration.getName() + " Current Position");
		GridBagConstraints gbc_axis1Label = new GridBagConstraints();
		gbc_axis1Label.insets = new Insets(10, 5, 5, 5);
		gbc_axis1Label.gridx = 0;
		gbc_axis1Label.gridy = 0;
		panelUp.add(axis1Label, gbc_axis1Label);

		axis1Position = new JTextField();
		axis1Position.setEditable(false);
		GridBagConstraints gbc_axis1Position = new GridBagConstraints();
		gbc_axis1Position.anchor = GridBagConstraints.WEST;
		gbc_axis1Position.gridwidth = 2;
		gbc_axis1Position.insets = new Insets(10, 0, 5, 0);
		gbc_axis1Position.gridx = 1;
		gbc_axis1Position.gridy = 0;
		panelUp.add(axis1Position, gbc_axis1Position);
		axis1Position.setColumns(10);
		axis1Position.setText(this.readAxis1Position());

		JLabel axis2Label = new JLabel(axis2Configuration.getName() + " Current Position");
		GridBagConstraints gbc_axis2Label = new GridBagConstraints();
		gbc_axis2Label.insets = new Insets(0, 5, 10, 5);
		gbc_axis2Label.anchor = GridBagConstraints.EAST;
		gbc_axis2Label.gridx = 0;
		gbc_axis2Label.gridy = 1;
		panelUp.add(axis2Label, gbc_axis2Label);

		axis2Position = new JTextField();
		axis2Position.setEditable(false);
		GridBagConstraints gbc_axis2Position = new GridBagConstraints();
		gbc_axis2Position.anchor = GridBagConstraints.WEST;
		gbc_axis2Position.gridwidth = 2;
		gbc_axis2Position.insets = new Insets(0, 0, 10, 0);
		gbc_axis2Position.gridx = 1;
		gbc_axis2Position.gridy = 1;
		panelUp.add(axis2Position, gbc_axis2Position);
		axis2Position.setColumns(10);
		axis2Position.setText(this.readAxis2Position());

		JButton refButton = new JButton("REF");
		refButton.setEnabled(false);
		GridBagConstraints gbc_refButton = new GridBagConstraints();
		gbc_refButton.gridwidth = 2;
		gbc_refButton.anchor = GridBagConstraints.SOUTHWEST;
		gbc_refButton.insets = new Insets(0, 0, 5, 0);
		gbc_refButton.gridx = 1;
		gbc_refButton.gridy = 2;
		panelUp.add(refButton, gbc_refButton);

		JLabel lblFast = new JLabel("Fast");
		GridBagConstraints gbc_lblFast = new GridBagConstraints();
		gbc_lblFast.insets = new Insets(0, 0, 5, 5);
		gbc_lblFast.gridx = 0;
		gbc_lblFast.gridy = 3;
		panelUp.add(lblFast, gbc_lblFast);

		JButton fastPlusButton = new JButton("+");
		fastPlusButton.setEnabled(false);
		GridBagConstraints gbc_fastPlusButton = new GridBagConstraints();
		gbc_fastPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_fastPlusButton.insets = new Insets(0, 0, 5, 5);
		gbc_fastPlusButton.gridx = 1;
		gbc_fastPlusButton.gridy = 3;
		panelUp.add(fastPlusButton, gbc_fastPlusButton);

		JButton fastMinusButton = new JButton("-");
		fastMinusButton.setEnabled(false);
		GridBagConstraints gbc_fastMinusButton = new GridBagConstraints();
		gbc_fastMinusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_fastMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_fastMinusButton.gridx = 2;
		gbc_fastMinusButton.gridy = 3;
		panelUp.add(fastMinusButton, gbc_fastMinusButton);

		JLabel lblRun = new JLabel("Run");
		GridBagConstraints gbc_lblRun = new GridBagConstraints();
		gbc_lblRun.insets = new Insets(0, 0, 5, 5);
		gbc_lblRun.gridx = 0;
		gbc_lblRun.gridy = 4;
		panelUp.add(lblRun, gbc_lblRun);

		JButton runPlusButton = new JButton("+");
		runPlusButton.setEnabled(false);
		GridBagConstraints gbc_runPlusButton = new GridBagConstraints();
		gbc_runPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_runPlusButton.insets = new Insets(0, 0, 5, 5);
		gbc_runPlusButton.gridx = 1;
		gbc_runPlusButton.gridy = 4;
		panelUp.add(runPlusButton, gbc_runPlusButton);

		JButton runMinusButton = new JButton("-");
		runMinusButton.setEnabled(false);
		GridBagConstraints gbc_runMinusButton = new GridBagConstraints();
		gbc_runMinusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_runMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_runMinusButton.gridx = 2;
		gbc_runMinusButton.gridy = 4;
		panelUp.add(runMinusButton, gbc_runMinusButton);

		JLabel lblStep = new JLabel("Step");
		GridBagConstraints gbc_lblStep = new GridBagConstraints();
		gbc_lblStep.insets = new Insets(0, 0, 5, 5);
		gbc_lblStep.gridx = 0;
		gbc_lblStep.gridy = 5;
		panelUp.add(lblStep, gbc_lblStep);

		JButton stepPlusButton = new JButton("+");
		stepPlusButton.setEnabled(false);
		GridBagConstraints gbc_stepPlusButton = new GridBagConstraints();
		gbc_stepPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_stepPlusButton.insets = new Insets(0, 0, 5, 5);
		gbc_stepPlusButton.gridx = 1;
		gbc_stepPlusButton.gridy = 5;
		panelUp.add(stepPlusButton, gbc_stepPlusButton);

		JButton stepMinusButton = new JButton("-");
		stepMinusButton.setEnabled(false);
		GridBagConstraints gbc_stepMinusButton = new GridBagConstraints();
		gbc_stepMinusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_stepMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_stepMinusButton.gridx = 2;
		gbc_stepMinusButton.gridy = 5;
		panelUp.add(stepMinusButton, gbc_stepMinusButton);

		stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);
		stopButton.setForeground(new Color(204, 0, 0));
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.gridwidth = 2;
		gbc_stopButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_stopButton.gridx = 1;
		gbc_stopButton.gridy = 6;
		panelUp.add(stopButton, gbc_stopButton);

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
		gbl_panelDown.columnWidths = new int[] { 50, 30, 50, 50, 40, 0 };
		gbl_panelDown.rowHeights = new int[] { 40, 30, 0, 30, 0 };
		gbl_panelDown.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelDown.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelDown.setLayout(gbl_panelDown);

		JLabel lblAssrel = new JLabel("Ass/Rel");
		GridBagConstraints gbc_lblAssrel = new GridBagConstraints();
		gbc_lblAssrel.anchor = GridBagConstraints.SOUTH;
		gbc_lblAssrel.insets = new Insets(0, 0, 5, 5);
		gbc_lblAssrel.gridx = 0;
		gbc_lblAssrel.gridy = 0;
		panelDown.add(lblAssrel, gbc_lblAssrel);

		kindOfMovementComboBox = new JComboBox();
		kindOfMovementComboBox.setModel(new DefaultComboBoxModel(new String[] { "A", "R" }));
		kindOfMovementComboBox.setSelectedIndex(0);
		kindOfMovementComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_kindOfMovementComboBox = new GridBagConstraints();
		gbc_kindOfMovementComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_kindOfMovementComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_kindOfMovementComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_kindOfMovementComboBox.gridx = 1;
		gbc_kindOfMovementComboBox.gridy = 0;
		panelDown.add(kindOfMovementComboBox, gbc_kindOfMovementComboBox);

		JLabel lblPosition = new JLabel("Position");
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.insets = new Insets(0, 0, 5, 5);
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
		gbc_position.gridwidth = 2;
		gbc_position.insets = new Insets(0, 0, 5, 5);
		gbc_position.fill = GridBagConstraints.HORIZONTAL;
		gbc_position.gridx = 2;
		gbc_position.gridy = 1;
		panelDown.add(position, gbc_position);
		position.setColumns(8);

		measureUnit = new JTextField();
		measureUnit.setEditable(false);
		measureUnit.setText(axisConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_measureUnit = new GridBagConstraints();
		gbc_measureUnit.anchor = GridBagConstraints.WEST;
		gbc_measureUnit.insets = new Insets(0, 0, 5, 0);
		gbc_measureUnit.gridx = 4;
		gbc_measureUnit.gridy = 1;
		panelDown.add(measureUnit, gbc_measureUnit);
		measureUnit.setColumns(3);

		moveButton = new JButton(ActionCommands.MOVE);
		moveButton.setActionCommand(ActionCommands.MOVE);
		moveButton.addActionListener(this);

		JLabel lblAxis_1 = new JLabel("Axis");
		GridBagConstraints gbc_lblAxis_1 = new GridBagConstraints();
		gbc_lblAxis_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblAxis_1.gridx = 0;
		gbc_lblAxis_1.gridy = 2;
		panelDown.add(lblAxis_1, gbc_lblAxis_1);

		referenceAxisComboBox = new JComboBox();
		referenceAxisComboBox.setMaximumRowCount(2);
		referenceAxisComboBox.setModel(new DefaultComboBoxModel(new String[] { axis1Configuration.getName(), axis2Configuration.getName() }));
		referenceAxisComboBox.setSelectedIndex(axisConfiguration.getMultipleAxis().getDefaultReferenceAxis() - 1);
		GridBagConstraints gbc_referenceAxisComboBox = new GridBagConstraints();
		gbc_referenceAxisComboBox.gridwidth = 2;
		gbc_referenceAxisComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_referenceAxisComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_referenceAxisComboBox.gridx = 1;
		gbc_referenceAxisComboBox.gridy = 2;
		panelDown.add(referenceAxisComboBox, gbc_referenceAxisComboBox);
		GridBagConstraints gbc_moveButton = new GridBagConstraints();
		gbc_moveButton.anchor = GridBagConstraints.EAST;
		gbc_moveButton.gridwidth = 2;
		gbc_moveButton.insets = new Insets(0, 0, 0, 5);
		gbc_moveButton.gridx = 1;
		gbc_moveButton.gridy = 3;
		panelDown.add(moveButton, gbc_moveButton);

		JButton setButton = new JButton("SET");
		setButton.setEnabled(false);
		GridBagConstraints gbc_setButton = new GridBagConstraints();
		gbc_setButton.insets = new Insets(0, 0, 0, 5);
		gbc_setButton.gridx = 3;
		gbc_setButton.gridy = 3;
		panelDown.add(setButton, gbc_setButton);
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
				else if (e.getActionCommand().equals(ActionCommands.STOP))
				{
					this.manageEventStop();
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

	public synchronized void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		new DoubleMoveListener(this).start();
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
			DoubleMoveParameters moveParameters = new DoubleMoveParameters(this.axis, ListenerRegister.getInstance());

			moveParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()));
			moveParameters.setSign(DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()));
			moveParameters.setPosition(Double.parseDouble(this.position.getText()));
			moveParameters.setReferenceAxis(this.referenceAxisComboBox.getSelectedIndex() + 1);

			new StartDoubleMoveProgram(moveParameters, this.port, this).start();
		}
		catch (NumberFormatException exception)
		{
			position.setText("<not a number>");

			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	private void manageEventStop() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STOP, new CommandParameters(GuiUtilities.getNullListener()), this.port);
	}

	private String readAxis1Position() throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis1, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(this.axis1, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis1, position);

	}

	private String readAxis2Position() throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis2, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(this.axis2, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis2, position);
	}

	protected void setAxis1PositionField(String text)
	{
		this.axis1Position.setText(text);
	}

	protected void setAxis2PositionField(String text)
	{
		this.axis2Position.setText(text);
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

	class StartDoubleMoveProgram extends Thread
	{
		private DoubleMoveParameters moveParameters;
		private ICommunicationPort   port;
		private DoubleMovePanel      panel;

		public StartDoubleMoveProgram(DoubleMoveParameters moveParameters, ICommunicationPort port, DoubleMovePanel panel)
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
				ProgramsFacade.executeProgram(ProgramsFacade.Programs.DOUBLEMOVE, this.moveParameters, this.port);
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this.panel);
			}
			finally
			{
			}
		}
	}

	class DoubleMoveListener extends Thread
	{
		/**
		 * 
		 */
		private DoubleMovePanel panel;

		public DoubleMoveListener(DoubleMovePanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition1 = DriverUtilities.parseAxisPositionResponse(panel.axis1, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(panel.axis1, nullListener), port));

				ObjectUtilities.pause(200);

				ControllerPosition controllerPosition2 = DriverUtilities.parseAxisPositionResponse(panel.axis2, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(panel.axis2, nullListener), port));

				double previousPosition1 = controllerPosition1.getSignedPosition();
				double currentPosition1 = previousPosition1;
				double previousPosition2 = controllerPosition2.getSignedPosition();
				double currentPosition2 = previousPosition2;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxis1PositionField(DriverUtilities.formatControllerPositionTextField(axis1, controllerPosition1));
					this.panel.setAxis2PositionField(DriverUtilities.formatControllerPositionTextField(axis2, controllerPosition2));

					ObjectUtilities.pause(200);

					controllerPosition1 = DriverUtilities.parseAxisPositionResponse(panel.axis1, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(panel.axis1, nullListener), port));

					ObjectUtilities.pause(200);

					controllerPosition2 = DriverUtilities.parseAxisPositionResponse(panel.axis2, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(panel.axis2, nullListener), port));

					previousPosition1 = currentPosition1;
					currentPosition1 = controllerPosition1.getSignedPosition();
					previousPosition2 = currentPosition2;
					currentPosition2 = controllerPosition2.getSignedPosition();

					if (currentPosition1 == previousPosition1 && currentPosition2 == previousPosition2)
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

	class EnableThread extends Thread
	{
		private DoubleMovePanel panel;
		private boolean         enabled;

		public EnableThread(DoubleMovePanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.axisName.setEnabled(enabled);
				this.panel.kindOfMovementComboBox.setEnabled(enabled);
				this.panel.signComboBox.setEnabled(enabled);
				this.panel.position.setEnabled(enabled);
				this.panel.measureUnit.setEnabled(enabled);
				this.panel.moveButton.setEnabled(enabled);
				this.panel.stopButton.setEnabled(enabled);
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
