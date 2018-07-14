package com.elettra.lab.metrology.lpt.panels;

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
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.controller.gui.panels.Defaults;
import com.elettra.lab.metrology.lpt.commands.ThreeMotorsSTOPCommand;
import com.elettra.lab.metrology.lpt.commands.ThreeMotorsSTOPParameters;
import com.elettra.lab.metrology.lpt.programs.LPTThreeMotorsMOVEProgram;
import com.elettra.lab.metrology.lpt.programs.ThreeMotorsMoveParameters;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LPTThreeMotorsMovePanel extends MovementListener implements ActionListener
{
	static class ActionCommands
	{
		private static final String	STOP	= "STOP";
		private static final String	MOVE	= "MOVE";
	}

	/**
	 * 
	 */
	private static final long	   serialVersionUID	= 768167582629608648L;

	protected int	               axis1;
	protected int	               axis2;
	protected int	               axis3;
	protected ICommunicationPort	port;

	protected JTextField	       axisPosition1;
	protected JTextField	       axisPosition2;
	protected JTextField	       axisPosition3;
	protected JTextField	       measureUnit;
	protected JTextField	       position;
	protected JComboBox	         signComboBox;
	protected JComboBox	         kindOfMovementComboBox;
	protected JTextField	       axisName1;
	protected JTextField	       axisName2;
	protected JTextField	       axisName3;

	protected boolean	           isScanActive;

	protected JButton	           moveButton;
	protected JButton	           stopButton;
	protected JLabel	           lblMove;
	protected JLabel	           lblMove_1;

	protected AxisConfiguration	 axisConfiguration1;
	protected AxisConfiguration	 axisConfiguration2;
	protected AxisConfiguration	 axisConfiguration3;

	protected JPanel	           panelDown;

	protected JTabbedPane	       tabbedPaneDown;

	/**
	 * Create the panel.
	 * 
	 * @throws CommunicationPortException
	 */
	public LPTThreeMotorsMovePanel(int axis1, int axis2, int axis3, ICommunicationPort port) throws CommunicationPortException
	{
		this.isScanActive = false;

		this.axis1 = axis1;
		this.axis2 = axis2;
		this.axis3 = axis3;
		this.port = port;

		ListenerRegister.getInstance().addListener(axis1, this);
		ListenerRegister.getInstance().addListener(axis2, this);
		ListenerRegister.getInstance().addListener(axis3, this);

		axisConfiguration1 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis1);
		axisConfiguration2 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis2);
		axisConfiguration3 = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis3);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 240, 0 };
		gridBagLayout.rowHeights = new int[] { 20, 133, 102, 103, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
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

		axisName1 = new JTextField();
		axisName1.setHorizontalAlignment(SwingConstants.CENTER);
		axisName1.setFont(new Font("Tahoma", Font.BOLD, 11));
		axisName1.setForeground(new Color(255, 255, 255));
		axisName1.setBackground(new Color(51, 102, 0));
		GridBagConstraints gbc_axisName = new GridBagConstraints();
		gbc_axisName.anchor = GridBagConstraints.EAST;
		gbc_axisName.gridx = 1;
		gbc_axisName.gridy = 0;
		panelTop.add(axisName1, gbc_axisName);
		axisName1.setColumns(10);
		axisName1.setText("Motors " + String.valueOf(this.axis1) + "," + String.valueOf(this.axis2) + "," + String.valueOf(this.axis3));

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
		gbl_panelUp.columnWidths = new int[] { 56, 57, 56, 47, 0 };
		gbl_panelUp.rowHeights = new int[] { 20, 20, 20 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelUp.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelUp.setLayout(gbl_panelUp);

		JLabel lblCurrent1 = new JLabel("Current Position " + String.valueOf(this.axis1));
		GridBagConstraints gbc_lblCurrent1 = new GridBagConstraints();
		gbc_lblCurrent1.gridwidth = 2;
		gbc_lblCurrent1.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrent1.gridx = 0;
		gbc_lblCurrent1.gridy = 0;
		panelUp.add(lblCurrent1, gbc_lblCurrent1);

		axisPosition1 = new JTextField();
		axisPosition1.setToolTipText("Current Axis Position");
		axisPosition1.setEditable(false);
		GridBagConstraints gbc_axisPosition1 = new GridBagConstraints();
		gbc_axisPosition1.gridwidth = 2;
		gbc_axisPosition1.insets = new Insets(0, 0, 5, 0);
		gbc_axisPosition1.fill = GridBagConstraints.HORIZONTAL;
		gbc_axisPosition1.gridx = 2;
		gbc_axisPosition1.gridy = 0;
		panelUp.add(axisPosition1, gbc_axisPosition1);
		axisPosition1.setColumns(10);
		axisPosition1.setText(this.readAxisPosition(this.axis1));

		JLabel lblCurrent2 = new JLabel("Current Position " + String.valueOf(this.axis2));
		GridBagConstraints gbc_lblCurrent2 = new GridBagConstraints();
		gbc_lblCurrent2.gridwidth = 2;
		gbc_lblCurrent2.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrent2.gridx = 0;
		gbc_lblCurrent2.gridy = 1;
		panelUp.add(lblCurrent2, gbc_lblCurrent2);

		axisPosition2 = new JTextField();
		axisPosition2.setToolTipText("Current Axis Position");
		axisPosition2.setEditable(false);
		GridBagConstraints gbc_axisPosition2 = new GridBagConstraints();
		gbc_axisPosition2.gridwidth = 2;
		gbc_axisPosition2.insets = new Insets(0, 0, 5, 0);
		gbc_axisPosition2.fill = GridBagConstraints.HORIZONTAL;
		gbc_axisPosition2.gridx = 2;
		gbc_axisPosition2.gridy = 1;
		panelUp.add(axisPosition2, gbc_axisPosition2);
		axisPosition2.setColumns(10);
		axisPosition2.setText(this.readAxisPosition(this.axis2));

		JLabel lblCurrent3 = new JLabel("Current Position " + String.valueOf(this.axis3));
		GridBagConstraints gbc_lblCurrent3 = new GridBagConstraints();
		gbc_lblCurrent3.gridwidth = 2;
		gbc_lblCurrent3.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrent3.gridx = 0;
		gbc_lblCurrent3.gridy = 2;
		panelUp.add(lblCurrent3, gbc_lblCurrent3);

		axisPosition3 = new JTextField();
		axisPosition3.setToolTipText("Current Axis Position");
		axisPosition3.setEditable(false);
		GridBagConstraints gbc_axisPosition3 = new GridBagConstraints();
		gbc_axisPosition3.gridwidth = 2;
		gbc_axisPosition3.insets = new Insets(0, 0, 5, 0);
		gbc_axisPosition3.fill = GridBagConstraints.HORIZONTAL;
		gbc_axisPosition3.gridx = 2;
		gbc_axisPosition3.gridy = 2;
		panelUp.add(axisPosition3, gbc_axisPosition3);
		axisPosition3.setColumns(10);
		axisPosition3.setText(this.readAxisPosition(this.axis3));

		tabbedPaneDown = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneDown = new GridBagConstraints();
		gbc_tabbedPaneDown.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPaneDown.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneDown.gridx = 0;
		gbc_tabbedPaneDown.gridy = 2;
		add(tabbedPaneDown, gbc_tabbedPaneDown);

		panelDown = new JPanel();
		tabbedPaneDown.addTab("Commands", null, panelDown, null);
		tabbedPaneDown.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelDown = new GridBagLayout();
		gbl_panelDown.columnWidths = new int[] { 33, 30, 35, 64, 46, 0 };
		gbl_panelDown.rowHeights = new int[] { 35, 23, 0 };
		gbl_panelDown.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelDown.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelDown.setLayout(gbl_panelDown);

		JLabel lblPosition = new JLabel("Pos.");
		GridBagConstraints gbc_lblPosition = new GridBagConstraints();
		gbc_lblPosition.anchor = GridBagConstraints.SOUTH;
		gbc_lblPosition.insets = new Insets(0, 0, 5, 5);
		gbc_lblPosition.gridx = 0;
		gbc_lblPosition.gridy = 0;
		panelDown.add(lblPosition, gbc_lblPosition);

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

		kindOfMovementComboBox = new JComboBox();
		kindOfMovementComboBox.setModel(new DefaultComboBoxModel(new String[] { "A", "R" }));

		try
		{
			if (Defaults.getInstance().get(Defaults.ABS_REL).equals("A"))
			{
				kindOfMovementComboBox.setSelectedIndex(0);
			}
			else
			{
				kindOfMovementComboBox.setSelectedIndex(1);
			}
		}
		catch (Exception e1)
		{
			kindOfMovementComboBox.setSelectedIndex(0);
		}

		try
		{
			if (Defaults.getInstance().get(Defaults.ABS_REL_ENABLED).equals("False"))
			{
				kindOfMovementComboBox.setEnabled(false);
			}
			else
			{
				kindOfMovementComboBox.setEnabled(true);
			}
		}
		catch (Exception e1)
		{
			kindOfMovementComboBox.setEnabled(true);
		}

		kindOfMovementComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_kindOfMovementComboBox = new GridBagConstraints();
		gbc_kindOfMovementComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_kindOfMovementComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_kindOfMovementComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_kindOfMovementComboBox.gridx = 1;
		gbc_kindOfMovementComboBox.gridy = 0;
		panelDown.add(kindOfMovementComboBox, gbc_kindOfMovementComboBox);

		signComboBox = new JComboBox();
		signComboBox.setModel(new DefaultComboBoxModel(new String[] { "+", "-" }));
		signComboBox.setSelectedIndex(0);
		signComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_signComboBox = new GridBagConstraints();
		gbc_signComboBox.anchor = GridBagConstraints.SOUTH;
		gbc_signComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_signComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_signComboBox.gridx = 2;
		gbc_signComboBox.gridy = 0;
		panelDown.add(signComboBox, gbc_signComboBox);

		position.setText(DriverUtilities.formatControllerPosition(0));
		position.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_position = new GridBagConstraints();
		gbc_position.anchor = GridBagConstraints.SOUTHEAST;
		gbc_position.insets = new Insets(0, 0, 5, 5);
		gbc_position.gridx = 3;
		gbc_position.gridy = 0;
		panelDown.add(position, gbc_position);
		position.setColumns(8);

		measureUnit = new JTextField();
		measureUnit.setEditable(false);
		measureUnit.setText(axisConfiguration1.getMeasureUnit().toString());
		GridBagConstraints gbc_measureUnit = new GridBagConstraints();
		gbc_measureUnit.anchor = GridBagConstraints.SOUTHWEST;
		gbc_measureUnit.insets = new Insets(0, 0, 5, 0);
		gbc_measureUnit.gridx = 4;
		gbc_measureUnit.gridy = 0;
		panelDown.add(measureUnit, gbc_measureUnit);
		measureUnit.setColumns(3);

		stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);

		stopButton.setForeground(new Color(204, 0, 0));
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.anchor = GridBagConstraints.WEST;
		gbc_stopButton.fill = GridBagConstraints.VERTICAL;
		gbc_stopButton.insets = new Insets(0, 0, 0, 5);
		gbc_stopButton.gridx = 3;
		gbc_stopButton.gridy = 1;
		panelDown.add(stopButton, gbc_stopButton);

		moveButton = new JButton(ActionCommands.MOVE);
		moveButton.setActionCommand(ActionCommands.MOVE);
		moveButton.addActionListener(this);
		GridBagConstraints gbc_moveButton = new GridBagConstraints();
		gbc_moveButton.fill = GridBagConstraints.VERTICAL;
		gbc_moveButton.anchor = GridBagConstraints.EAST;
		gbc_moveButton.gridwidth = 2;
		gbc_moveButton.insets = new Insets(0, 0, 0, 5);
		gbc_moveButton.gridx = 1;
		gbc_moveButton.gridy = 1;
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
				else if (e.getActionCommand().equals(ActionCommands.STOP))
				{
					this.manageEventStop();
				}
			}
			catch (Exception exception)
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
		if (axis == this.axis1)
			new MoveListener(this, axis1, 1).start();
		else if (axis == this.axis2)
			new MoveListener(this, axis2, 2).start();
		else if (axis == this.axis3)
			new MoveListener(this, axis3, 3).start();
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

	protected void manageEventMove() throws CommunicationPortException
	{
		try
		{
			ThreeMotorsMoveParameters moveParameters = new ThreeMotorsMoveParameters(this.axis1, this.axis2, this.axis3, ListenerRegister.getInstance());

			moveParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()));
			moveParameters.setSign(DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()));
			moveParameters.setPosition(Double.parseDouble(this.position.getText()));

			new StartMoveProgram(moveParameters, port, this).start();
		}
		catch (NumberFormatException exception)
		{
			position.setText("<not a number>");

			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	private void manageEventStop() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(ThreeMotorsSTOPCommand.THREEMSTOP,
		    new ThreeMotorsSTOPParameters(this.axis1, this.axis2, this.axis3, GuiUtilities.getNullListener()), this.port);
	}

	protected String readAxisPosition(int axis) throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis,
		    CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis, position);
	}

	protected synchronized void setAxisPositionField(int axisPosition, String text)
	{
		switch (axisPosition)
		{
		case 1:
			this.axisPosition1.setText(text);
			break;
		case 2:
			this.axisPosition2.setText(text);
			break;
		case 3:
			this.axisPosition3.setText(text);
			break;

		default:
			break;
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

	class StartMoveProgram extends Thread
	{
		private ProgramParameters		    moveParameters;
		private ICommunicationPort		  port;
		private LPTThreeMotorsMovePanel	panel;

		public StartMoveProgram(ProgramParameters moveParameters, ICommunicationPort port, LPTThreeMotorsMovePanel panel)
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
				ProgramsFacade.executeProgram(LPTThreeMotorsMOVEProgram.THREEMMOVE, this.moveParameters, this.port);
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

	class MoveListener extends Thread
	{
		/**
		 * 
		 */
		private int		                  axis;
		private int		                  axisPosition;
		private LPTThreeMotorsMovePanel	panel;

		public MoveListener(LPTThreeMotorsMovePanel panel, int axis, int axisPosition)
		{
			this.panel = panel;
			this.axis = axis;
			this.axisPosition = axisPosition;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition = DriverUtilities.parseAxisPositionResponse(axis,
				    CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

				double previousPosition = controllerPosition.getSignedPosition();
				double currentPosition = previousPosition;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(axisPosition, DriverUtilities.formatControllerPositionTextField(axis, controllerPosition));

					ObjectUtilities.pause(200);

					controllerPosition = DriverUtilities.parseAxisPositionResponse(axis,
					    CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

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

	class EnableThread extends Thread
	{
		private LPTThreeMotorsMovePanel	panel;
		private boolean		              enabled;

		public EnableThread(LPTThreeMotorsMovePanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.axisName1.setEnabled(enabled);
				this.panel.axisPosition1.setEnabled(enabled);
				this.panel.axisPosition2.setEnabled(enabled);
				this.panel.axisPosition3.setEnabled(enabled);

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
