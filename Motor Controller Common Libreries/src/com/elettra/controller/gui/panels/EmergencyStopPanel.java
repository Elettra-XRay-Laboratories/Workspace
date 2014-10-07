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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.gui.common.GuiUtilities;

public class EmergencyStopPanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7456243706339213740L;

	static class ActionCommands
	{
		private static final String EMERGENCY_STOP = "EMERGENCY_STOP";
	}

	/**
	 * Create the panel.
	 */
	public EmergencyStopPanel()
	{
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
		tabbedPane.addTab("Motor Controller Safety", null, panel, null);
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0 };
		gbl_panel.rowHeights = new int[] { 0 };
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowWeights = new double[] { 1.0 };
		panel.setLayout(gbl_panel);

		JButton emergencyDumpButton = new JButton("EMERGENCY STOP");
		emergencyDumpButton.setForeground(Color.RED);
		emergencyDumpButton.setFont(new Font("Tahoma", Font.BOLD, 24));
		emergencyDumpButton.addActionListener(this);
		emergencyDumpButton.setActionCommand(ActionCommands.EMERGENCY_STOP);
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
			if (e.getActionCommand().equals(ActionCommands.EMERGENCY_STOP))
				this.manageEventEmergencyStop();
		}
		catch (Exception exception)
		{
			GuiUtilities.showErrorPopup(exception.getMessage(), this);
		}
	}

	private void manageEventEmergencyStop() throws IOException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.EMERGENCY_STOP, new CommandParameters(GuiUtilities.getNullListener()), CommunicationPortFactory.getEmergencyPort());
	}
}
