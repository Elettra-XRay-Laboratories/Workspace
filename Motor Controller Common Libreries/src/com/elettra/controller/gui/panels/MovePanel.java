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
import javax.swing.JCheckBox;
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
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MovePanel extends MovementListener implements ActionListener
{
	static class ActionCommands
	{
		private static final String STEPMINUS = "STEP-";
		private static final String STEPPLUS  = "STEP+";
		private static final String FASTMINUS = "FAST-";
		private static final String FASTPLUS  = "FAST+";
		private static final String RUNMINUS  = "RUN-";
		private static final String RUNPLUS   = "RUN+";
		private static final String SET       = "SET";
		private static final String REF       = "REF";
		private static final String STOP      = "STOP";
		private static final String MOVE      = "MOVE";
	}

	/**
	 * 
	 */
	private static final long  serialVersionUID = 768167582629608648L;

	protected int                axis;
	protected ICommunicationPort port;

	protected JTextField         axisPosition;
	protected JTextField         measureUnit;
	protected JTextField         position;
	protected JComboBox          signComboBox;
	protected JComboBox          kindOfMovementComboBox;
	protected JTextField         axisName;

	protected boolean            isScanActive;

	protected JButton            setButton;
	protected JButton            moveButton;
	protected JButton            stopButton;
	protected JButton            stepMinusButton;
	protected JButton            stepPlusButton;
	protected JButton            runMinusButton;
	protected JButton            runPlusButton;
	protected JButton            fastMinusButton;
	protected JButton            fastPlusButton;
	protected JButton            refButton;
	protected JLabel             lblMove;
	protected JLabel             lblMove_1;
	protected JTabbedPane        tabbedPaneLimits;
	protected JPanel             panelLimits;
	protected JTextField         limitDown;
	protected JTextField         limitDownUm;
	protected JComboBox          ldSignComboBox;
	protected JCheckBox          blockedCheckBox;
	protected JLabel             lblLimit;
	protected JLabel             lblLimit_1;
	protected JComboBox          luSignComboBox;
	protected JLabel             lblBlocked;
	protected JTextField         limitUp;
	protected JTextField         limitUpUm;
	protected JCheckBox          limitedCheckBox;
	protected JLabel             lblLimited;

	protected AxisConfiguration  axisConfiguration;

	protected JPanel panelDown;

	protected JTabbedPane tabbedPaneDown;

	/**
	 * Create the panel.
	 * 
	 * @throws CommunicationPortException
	 */
	public MovePanel(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		this.isScanActive = false;

		this.axis = axis;
		this.port = port;

		ListenerRegister.getInstance().addListener(axis, this);

		axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(this.axis);

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
		tabbedPaneUp.addTab("Controller", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 56, 57, 56, 47, 0 };
		gbl_panelUp.rowHeights = new int[] { 30, 20, 10, 20, 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelUp.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelUp.setLayout(gbl_panelUp);

		JLabel lblCurrent = new JLabel("Current Position");
		GridBagConstraints gbc_lblCurrent = new GridBagConstraints();
		gbc_lblCurrent.gridwidth = 2;
		gbc_lblCurrent.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrent.gridx = 0;
		gbc_lblCurrent.gridy = 0;
		panelUp.add(lblCurrent, gbc_lblCurrent);

		axisPosition = new JTextField();
		axisPosition.setToolTipText("Current Axis Position");
		axisPosition.setEditable(false);
		GridBagConstraints gbc_axisPosition = new GridBagConstraints();
		gbc_axisPosition.gridwidth = 2;
		gbc_axisPosition.insets = new Insets(0, 0, 5, 0);
		gbc_axisPosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_axisPosition.gridx = 2;
		gbc_axisPosition.gridy = 0;
		panelUp.add(axisPosition, gbc_axisPosition);
		axisPosition.setColumns(10);
		axisPosition.setText(this.readAxisPosition());

		fastPlusButton = new JButton("F+");
		fastPlusButton.setActionCommand(ActionCommands.FASTPLUS);
		fastPlusButton.addActionListener(this);

		lblMove = new JLabel("Move +");
		GridBagConstraints gbc_lblMove = new GridBagConstraints();
		gbc_lblMove.insets = new Insets(0, 0, 5, 5);
		gbc_lblMove.gridx = 0;
		gbc_lblMove.gridy = 1;
		panelUp.add(lblMove, gbc_lblMove);
		GridBagConstraints gbc_fastPlusButton = new GridBagConstraints();
		gbc_fastPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_fastPlusButton.ipadx = 2;
		gbc_fastPlusButton.insets = new Insets(0, 0, 5, 5);
		gbc_fastPlusButton.gridx = 1;
		gbc_fastPlusButton.gridy = 1;
		panelUp.add(fastPlusButton, gbc_fastPlusButton);

		runPlusButton = new JButton("R+");
		runPlusButton.setActionCommand(ActionCommands.RUNPLUS);
		runPlusButton.addActionListener(this);
		GridBagConstraints gbc_runPlusButton = new GridBagConstraints();
		gbc_runPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_runPlusButton.insets = new Insets(0, 0, 5, 5);
		gbc_runPlusButton.gridx = 2;
		gbc_runPlusButton.gridy = 1;
		panelUp.add(runPlusButton, gbc_runPlusButton);

		fastMinusButton = new JButton("F-");
		fastMinusButton.setActionCommand(ActionCommands.FASTMINUS);
		fastMinusButton.addActionListener(this);

		stepPlusButton = new JButton("S+");
		stepPlusButton.setActionCommand(ActionCommands.STEPPLUS);
		stepPlusButton.addActionListener(this);
		GridBagConstraints gbc_stepPlusButton = new GridBagConstraints();
		gbc_stepPlusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_stepPlusButton.insets = new Insets(0, 0, 5, 0);
		gbc_stepPlusButton.gridx = 3;
		gbc_stepPlusButton.gridy = 1;
		panelUp.add(stepPlusButton, gbc_stepPlusButton);

		lblMove_1 = new JLabel("Move -");
		GridBagConstraints gbc_lblMove_1 = new GridBagConstraints();
		gbc_lblMove_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblMove_1.gridx = 0;
		gbc_lblMove_1.gridy = 2;
		panelUp.add(lblMove_1, gbc_lblMove_1);
		GridBagConstraints gbc_fastMinusButton = new GridBagConstraints();
		gbc_fastMinusButton.fill = GridBagConstraints.BOTH;
		gbc_fastMinusButton.ipadx = 6;
		gbc_fastMinusButton.insets = new Insets(0, 0, 5, 5);
		gbc_fastMinusButton.gridx = 1;
		gbc_fastMinusButton.gridy = 2;
		panelUp.add(fastMinusButton, gbc_fastMinusButton);

		runMinusButton = new JButton("R-");
		runMinusButton.setActionCommand(ActionCommands.RUNMINUS);
		runMinusButton.addActionListener(this);
		GridBagConstraints gbc_runMinusButton = new GridBagConstraints();
		gbc_runMinusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_runMinusButton.insets = new Insets(0, 0, 5, 5);
		gbc_runMinusButton.gridx = 2;
		gbc_runMinusButton.gridy = 2;
		panelUp.add(runMinusButton, gbc_runMinusButton);

		stopButton = new JButton(ActionCommands.STOP);
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);

		stepMinusButton = new JButton("S-");
		stepMinusButton.setActionCommand(ActionCommands.STEPMINUS);
		stepMinusButton.addActionListener(this);
		GridBagConstraints gbc_stepMinusButton = new GridBagConstraints();
		gbc_stepMinusButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_stepMinusButton.insets = new Insets(0, 0, 5, 0);
		gbc_stepMinusButton.gridx = 3;
		gbc_stepMinusButton.gridy = 2;
		panelUp.add(stepMinusButton, gbc_stepMinusButton);

		refButton = new JButton(ActionCommands.REF);
		refButton.setActionCommand(ActionCommands.REF);
		refButton.addActionListener(this);
		GridBagConstraints gbc_refButton = new GridBagConstraints();
		gbc_refButton.fill = GridBagConstraints.VERTICAL;
		gbc_refButton.gridwidth = 2;
		gbc_refButton.anchor = GridBagConstraints.WEST;
		gbc_refButton.insets = new Insets(0, 0, 0, 5);
		gbc_refButton.gridx = 1;
		gbc_refButton.gridy = 3;
		panelUp.add(refButton, gbc_refButton);
		refButton.setEnabled(axisConfiguration.isRefEnabled());
		stopButton.setForeground(new Color(204, 0, 0));
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.gridwidth = 2;
		gbc_stopButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_stopButton.gridx = 2;
		gbc_stopButton.gridy = 3;
		panelUp.add(stopButton, gbc_stopButton);

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
		measureUnit.setText(axisConfiguration.getMeasureUnit().toString());
		GridBagConstraints gbc_measureUnit = new GridBagConstraints();
		gbc_measureUnit.anchor = GridBagConstraints.SOUTHWEST;
		gbc_measureUnit.insets = new Insets(0, 0, 5, 0);
		gbc_measureUnit.gridx = 4;
		gbc_measureUnit.gridy = 0;
		panelDown.add(measureUnit, gbc_measureUnit);
		measureUnit.setColumns(3);

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

		setButton = new JButton(ActionCommands.SET);
		setButton.setActionCommand(ActionCommands.SET);
		setButton.addActionListener(this);
		GridBagConstraints gbc_setButton = new GridBagConstraints();
		gbc_setButton.anchor = GridBagConstraints.WEST;
		gbc_setButton.fill = GridBagConstraints.VERTICAL;
		gbc_setButton.insets = new Insets(0, 0, 0, 5);
		gbc_setButton.gridx = 3;
		gbc_setButton.gridy = 1;
		panelDown.add(setButton, gbc_setButton);

		tabbedPaneLimits = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneLimits = new GridBagConstraints();
		gbc_tabbedPaneLimits.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneLimits.gridx = 0;
		gbc_tabbedPaneLimits.gridy = 3;
		add(tabbedPaneLimits, gbc_tabbedPaneLimits);

		panelLimits = new JPanel();
		tabbedPaneLimits.addTab("Motor Limits", null, panelLimits, null);
		tabbedPaneLimits.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelLimits = new GridBagLayout();
		gbl_panelLimits.columnWidths = new int[] { 61, 42, 75, 54, 0 };
		gbl_panelLimits.rowHeights = new int[] { 0, 24, 0, 0 };
		gbl_panelLimits.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_panelLimits.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelLimits.setLayout(gbl_panelLimits);

		lblBlocked = new JLabel("Blocked");
		GridBagConstraints gbc_lblBlocked = new GridBagConstraints();
		gbc_lblBlocked.anchor = GridBagConstraints.EAST;
		gbc_lblBlocked.insets = new Insets(5, 0, 5, 5);
		gbc_lblBlocked.gridx = 0;
		gbc_lblBlocked.gridy = 0;
		panelLimits.add(lblBlocked, gbc_lblBlocked);

		blockedCheckBox = new JCheckBox("");
		blockedCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				axisConfiguration.setBlocked(blockedCheckBox.isSelected());
			}
		});

		GridBagConstraints gbc_blockedMotorCheckBox = new GridBagConstraints();
		gbc_blockedMotorCheckBox.anchor = GridBagConstraints.WEST;
		gbc_blockedMotorCheckBox.insets = new Insets(5, 0, 5, 5);
		gbc_blockedMotorCheckBox.gridx = 1;
		gbc_blockedMotorCheckBox.gridy = 0;
		panelLimits.add(blockedCheckBox, gbc_blockedMotorCheckBox);

		lblLimited = new JLabel("Limited");
		GridBagConstraints gbc_lblLimited = new GridBagConstraints();
		gbc_lblLimited.anchor = GridBagConstraints.EAST;
		gbc_lblLimited.insets = new Insets(5, 0, 5, 5);
		gbc_lblLimited.gridx = 2;
		gbc_lblLimited.gridy = 0;
		panelLimits.add(lblLimited, gbc_lblLimited);

		limitedCheckBox = new JCheckBox("");
		limitedCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean enabled = limitedCheckBox.isSelected();

				ldSignComboBox.setEnabled(enabled);
				limitDown.setEnabled(enabled);
				luSignComboBox.setEnabled(enabled);
				limitUp.setEnabled(enabled);

				axisConfiguration.setLimited(enabled);
			}
		});
		GridBagConstraints gbc_limitedCheckBox = new GridBagConstraints();
		gbc_limitedCheckBox.anchor = GridBagConstraints.WEST;
		gbc_limitedCheckBox.insets = new Insets(5, 0, 5, 0);
		gbc_limitedCheckBox.gridx = 3;
		gbc_limitedCheckBox.gridy = 0;
		panelLimits.add(limitedCheckBox, gbc_limitedCheckBox);

		lblLimit = new JLabel("Limit Down");
		GridBagConstraints gbc_lblLimit = new GridBagConstraints();
		gbc_lblLimit.insets = new Insets(0, 0, 5, 5);
		gbc_lblLimit.anchor = GridBagConstraints.EAST;
		gbc_lblLimit.gridx = 0;
		gbc_lblLimit.gridy = 1;
		panelLimits.add(lblLimit, gbc_lblLimit);

		ldSignComboBox = new JComboBox();
		ldSignComboBox.setModel(new DefaultComboBoxModel(new String[] { "+", "-" }));
		ldSignComboBox.setSelectedIndex(1);
		ldSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_ldSignComboBox = new GridBagConstraints();
		gbc_ldSignComboBox.anchor = GridBagConstraints.SOUTHEAST;
		gbc_ldSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_ldSignComboBox.gridx = 1;
		gbc_ldSignComboBox.gridy = 1;
		panelLimits.add(ldSignComboBox, gbc_ldSignComboBox);

		limitDown = new JTextField();
		limitDown.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					limitDown.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(limitDown.getText()))));
					axisConfiguration.setLimitDown(Math.abs(Double.parseDouble(limitDown.getText())));
				}
				catch (Throwable t)
				{
					limitDown.setText("<not a number>");
				}
			}
		});
		limitDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					limitDown.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(limitDown.getText()))));
					axisConfiguration.setLimitDown(Math.abs(Double.parseDouble(limitDown.getText())));
				}
				catch (Throwable t)
				{
					limitDown.setText("<not a number>");
				}
			}
		});
		limitDown.setText(DriverUtilities.formatControllerPosition(axisConfiguration.getLimitDown()));
		limitDown.setHorizontalAlignment(SwingConstants.RIGHT);
		limitDown.setColumns(8);
		GridBagConstraints gbc_limitDown = new GridBagConstraints();
		gbc_limitDown.anchor = GridBagConstraints.SOUTHEAST;
		gbc_limitDown.insets = new Insets(0, 0, 5, 5);
		gbc_limitDown.gridx = 2;
		gbc_limitDown.gridy = 1;
		panelLimits.add(limitDown, gbc_limitDown);

		limitDownUm = new JTextField();
		limitDownUm.setText((String) null);
		limitDownUm.setText(axisConfiguration.getMeasureUnit().toString());
		limitDownUm.setEditable(false);
		limitDownUm.setColumns(3);
		GridBagConstraints gbc_limitDownUm = new GridBagConstraints();
		gbc_limitDownUm.anchor = GridBagConstraints.SOUTHWEST;
		gbc_limitDownUm.insets = new Insets(0, 0, 5, 0);
		gbc_limitDownUm.gridx = 3;
		gbc_limitDownUm.gridy = 1;
		panelLimits.add(limitDownUm, gbc_limitDownUm);

		lblLimit_1 = new JLabel("Limit Up");
		GridBagConstraints gbc_lblLimit_1 = new GridBagConstraints();
		gbc_lblLimit_1.anchor = GridBagConstraints.EAST;
		gbc_lblLimit_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblLimit_1.gridx = 0;
		gbc_lblLimit_1.gridy = 2;
		panelLimits.add(lblLimit_1, gbc_lblLimit_1);

		luSignComboBox = new JComboBox();
		luSignComboBox.setModel(new DefaultComboBoxModel(new String[] { "+", "-" }));
		luSignComboBox.setSelectedIndex(0);
		luSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_luSignComboBox = new GridBagConstraints();
		gbc_luSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_luSignComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_luSignComboBox.gridx = 1;
		gbc_luSignComboBox.gridy = 2;
		panelLimits.add(luSignComboBox, gbc_luSignComboBox);

		limitUp = new JTextField();
		limitUp.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					limitUp.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(limitUp.getText()))));
					axisConfiguration.setLimitUp(Math.abs(Double.parseDouble(limitUp.getText())));
				}
				catch (Throwable t)
				{
					limitUp.setText("<not a number>");
				}
			}
		});
		limitUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					limitUp.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(limitUp.getText()))));
					axisConfiguration.setLimitUp(Math.abs(Double.parseDouble(limitUp.getText())));
				}
				catch (Throwable t)
				{
					limitUp.setText("<not a number>");
				}
			}
		});
		limitUp.setText(DriverUtilities.formatControllerPosition(axisConfiguration.getLimitUp()));
		limitUp.setHorizontalAlignment(SwingConstants.RIGHT);
		limitUp.setColumns(8);
		GridBagConstraints gbc_limitUp = new GridBagConstraints();
		gbc_limitUp.insets = new Insets(0, 0, 0, 5);
		gbc_limitUp.fill = GridBagConstraints.HORIZONTAL;
		gbc_limitUp.gridx = 2;
		gbc_limitUp.gridy = 2;
		panelLimits.add(limitUp, gbc_limitUp);

		limitUpUm = new JTextField();
		limitUpUm.setText(axisConfiguration.getMeasureUnit().toString());
		limitUpUm.setEditable(false);
		limitUpUm.setColumns(3);
		GridBagConstraints gbc_limitUpUm = new GridBagConstraints();
		gbc_limitUpUm.anchor = GridBagConstraints.WEST;
		gbc_limitUpUm.gridx = 3;
		gbc_limitUpUm.gridy = 2;
		panelLimits.add(limitUpUm, gbc_limitUpUm);

		blockedCheckBox.setSelected(axisConfiguration.isBlocked());
		limitedCheckBox.setSelected(axisConfiguration.isLimited());

		ldSignComboBox.setEnabled(axisConfiguration.isLimited());
		limitDown.setEnabled(axisConfiguration.isLimited());
		luSignComboBox.setEnabled(axisConfiguration.isLimited());
		limitUp.setEnabled(axisConfiguration.isLimited());
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
				else if (e.getActionCommand().equals(ActionCommands.FASTPLUS))
				{
					this.manageEventFastPlus();
				}
				else if (e.getActionCommand().equals(ActionCommands.FASTMINUS))
				{
					this.manageEventFastMinus();
				}
				else if (e.getActionCommand().equals(ActionCommands.STEPPLUS))
				{
					this.manageEventStepPlus();
				}
				else if (e.getActionCommand().equals(ActionCommands.STEPMINUS))
				{
					this.manageEventStepMinus();
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
		new MoveListener(this).start();
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
			MoveParameters moveParameters = new MoveParameters(this.axis, ListenerRegister.getInstance());

			moveParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()));
			moveParameters.setSign(DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()));
			moveParameters.setPosition(Double.parseDouble(this.position.getText()));

			new StartMoveProgram(moveParameters, port).start();
		}
		catch (NumberFormatException exception)
		{
			position.setText("<not a number>");

			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	private void manageEventStop() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STOP, new CommandParameters(this.axis, GuiUtilities.getNullListener()), this.port);
	}

	private void manageEventRef() throws CommunicationPortException
	{
		if (GuiUtilities.showConfirmPopup("Warning: the requested operation is irreversible and it will erase every setting on the controller (SET command)\nContinue?", this))
		{
			CommandsFacade.executeCommand(CommandsFacade.Commands.REF, new CommandParameters(this.axis, ListenerRegister.getInstance()), this.port);
		}
	}

	private void manageEventSet() throws CommunicationPortException
	{
		try
		{

			if (GuiUtilities.showConfirmPopup("Warning: the requested operation is irreversible and it will alter the positioning system.\nContinue?", this))
			{
				CommandsFacade.executeCommand(CommandsFacade.Commands.POS, new CommandParameters(this.axis, DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()), Double.parseDouble(this.position.getText()), ListenerRegister.getInstance()), this.port);
			}
		}
		catch (NumberFormatException exception)
		{
			position.setText("<not a number>");

			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	private void manageEventRunPlus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.RUN, new CommandParameters(this.axis, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	private void manageEventFastPlus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.FAST, new CommandParameters(this.axis, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	private void manageEventStepPlus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STEP, new CommandParameters(this.axis, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	private void manageEventRunMinus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.RUN, new CommandParameters(this.axis, DriverUtilities.getMinus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	private void manageEventFastMinus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.FAST, new CommandParameters(this.axis, DriverUtilities.getMinus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	private void manageEventStepMinus() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STEP, new CommandParameters(this.axis, DriverUtilities.getMinus(), 0.0, ListenerRegister.getInstance()), this.port);
	}

	protected String readAxisPosition() throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(this.axis, GuiUtilities.getNullListener()), this.port));

		return DriverUtilities.formatControllerPositionTextField(axis, position);
	}

	protected void setAxisPositionField(String text)
	{
		this.axisPosition.setText(text);
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

	class StartRefCommand extends Thread
	{
		private CommandParameters  refParameters;
		private ICommunicationPort port;

		public StartRefCommand(CommandParameters refParameters, ICommunicationPort port)
		{
			super();
			this.refParameters = refParameters;
			this.port = port;
		}

		public void run()
		{
			try
			{
				CommandsFacade.executeCommand(CommandsFacade.Commands.REF, this.refParameters, this.port);
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
		private MovePanel panel;

		public MoveListener(MovePanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition = DriverUtilities.parseAxisPositionResponse(panel.axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

				double previousPosition = controllerPosition.getSignedPosition();
				double currentPosition = previousPosition;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(DriverUtilities.formatControllerPositionTextField(axis, controllerPosition));

					ObjectUtilities.pause(200);

					controllerPosition = DriverUtilities.parseAxisPositionResponse(panel.axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

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
		private MovePanel panel;
		private boolean   enabled;

		public EnableThread(MovePanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				AxisConfiguration axisConfiguration = DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(panel.axis);

				this.panel.axisName.setEnabled(enabled);
				this.panel.axisPosition.setEnabled(enabled);
				this.panel.runMinusButton.setEnabled(enabled);
				this.panel.runPlusButton.setEnabled(enabled);
				this.panel.fastMinusButton.setEnabled(enabled);
				this.panel.fastPlusButton.setEnabled(enabled);
				this.panel.stepMinusButton.setEnabled(enabled);
				this.panel.stepPlusButton.setEnabled(enabled);

				if (axisConfiguration.isRefEnabled())
					this.panel.refButton.setEnabled(enabled);

				this.panel.kindOfMovementComboBox.setEnabled(enabled);
				this.panel.signComboBox.setEnabled(enabled);
				this.panel.position.setEnabled(enabled);
				this.panel.measureUnit.setEnabled(enabled);
				this.panel.moveButton.setEnabled(enabled);
				this.panel.stopButton.setEnabled(enabled);
				this.panel.setButton.setEnabled(enabled);
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
