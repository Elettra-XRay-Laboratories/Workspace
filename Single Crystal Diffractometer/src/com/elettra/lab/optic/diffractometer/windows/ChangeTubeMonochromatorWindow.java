package com.elettra.lab.optic.diffractometer.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.panels.ChangeTubeMonochromatorPanel;

public class ChangeTubeMonochromatorWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long serialVersionUID = 44065158592830698L;

	public static synchronized ChangeTubeMonochromatorWindow getInstance(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		return new ChangeTubeMonochromatorWindow(port);
	}

	private ChangeTubeMonochromatorWindow(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		super("Change X-ray Tube/Monochromator", port);
		
		setBounds(100, 100, 300, 500);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.02 };
		getContentPane().setLayout(gridBagLayout);

		JPanel upperPanel = new JPanel();
		upperPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_upperPanel = new GridBagConstraints();
		gbc_upperPanel.insets = new Insets(10, 5, 5, 5);
		gbc_upperPanel.fill = GridBagConstraints.BOTH;
		gbc_upperPanel.gridx = 0;
		gbc_upperPanel.gridy = 0;
		getContentPane().add(upperPanel, gbc_upperPanel);

		upperPanel.add(new ChangeTubeMonochromatorPanel(this.getPort()));

		JPanel exitPanel = new JPanel();
		GridBagConstraints gbc_exitPanel = new GridBagConstraints();
		gbc_exitPanel.fill = GridBagConstraints.BOTH;
		gbc_exitPanel.gridx = 0;
		gbc_exitPanel.gridy = 1;
		getContentPane().add(exitPanel, gbc_exitPanel);
		GridBagLayout gbl_exitPanel = new GridBagLayout();
		gbl_exitPanel.columnWidths = new int[] { 0 };
		gbl_exitPanel.rowHeights = new int[] { 0 };
		gbl_exitPanel.columnWeights = new double[] { 1.0 };
		gbl_exitPanel.rowWeights = new double[] { 1.0 };
		exitPanel.setLayout(gbl_exitPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.addActionListener(this);
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.fill = GridBagConstraints.VERTICAL;
		gbc_exitButton.anchor = GridBagConstraints.EAST;
		gbc_exitButton.gridx = 0;
		gbc_exitButton.gridy = 0;
		exitPanel.add(exitButton, gbc_exitButton);
	}

	protected void manageOtherActions(ActionEvent event)
	{
	}

}
