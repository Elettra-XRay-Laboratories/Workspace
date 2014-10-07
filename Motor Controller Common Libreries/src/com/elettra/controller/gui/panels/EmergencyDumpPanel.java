package com.elettra.controller.gui.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.gui.common.GuiUtilities;

public class EmergencyDumpPanel extends JPanel implements ActionListener
{
	static class ActionCommands
	{
		private static final String EMERGENCY_DUMP = "EMERGENCY_DUMP";
	}

	private ICommunicationPort port;
	/**
   * 
   */
	private static final long  serialVersionUID = -8430976464748825704L;

	/**
	 * Create the panel.
	 */
	public EmergencyDumpPanel(ICommunicationPort port)
	{
		this.port = port;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		setLayout(gridBagLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Huber Crash Recovery System", null, panel, null);
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0 };
		gbl_panel.rowHeights = new int[] { 0 };
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowWeights = new double[] { 1.0 };
		panel.setLayout(gbl_panel);

		JButton emergencyDumpButton = new JButton("EMERGENCY DUMP");
		emergencyDumpButton.addActionListener(this);
		emergencyDumpButton.setActionCommand(ActionCommands.EMERGENCY_DUMP);
		GridBagConstraints gbc_emergencyDumpButton = new GridBagConstraints();
		gbc_emergencyDumpButton.insets = new Insets(10, 5, 10, 5);
		gbc_emergencyDumpButton.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpButton.gridx = 0;
		gbc_emergencyDumpButton.gridy = 0;
		panel.add(emergencyDumpButton, gbc_emergencyDumpButton);

	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals(ActionCommands.EMERGENCY_DUMP))
				this.manageEventEmergencyDump();
		}
		catch (Exception exception)
		{
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}
	}

	private void manageEventEmergencyDump() throws IOException
	{
		if (GuiUtilities.showConfirmPopup("Confirm override of the Emergency Dump?", this))
		{
			String axis1Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(1, GuiUtilities.getNullListener()), this.port);
			String axis2Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(2, GuiUtilities.getNullListener()), this.port);
			String axis3Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(3, GuiUtilities.getNullListener()), this.port);
			String axis4Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(4, GuiUtilities.getNullListener()), this.port);
			String axis5Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(5, GuiUtilities.getNullListener()), this.port);
			String axis6Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(6, GuiUtilities.getNullListener()), this.port);
			String axis7Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(7, GuiUtilities.getNullListener()), this.port);
			String axis8Position = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(8, GuiUtilities.getNullListener()), this.port);

			DriverUtilities.saveEmergencyAxisPosition(1, axis1Position);
			DriverUtilities.saveEmergencyAxisPosition(2, axis2Position);
			DriverUtilities.saveEmergencyAxisPosition(3, axis3Position);
			DriverUtilities.saveEmergencyAxisPosition(4, axis4Position);
			DriverUtilities.saveEmergencyAxisPosition(5, axis5Position);
			DriverUtilities.saveEmergencyAxisPosition(6, axis6Position);
			DriverUtilities.saveEmergencyAxisPosition(7, axis7Position);
			DriverUtilities.saveEmergencyAxisPosition(8, axis8Position);
		}
	}
}
