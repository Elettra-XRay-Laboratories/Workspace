package com.elettra.controller.gui.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.ControllerCrashRecoveryPanel;

public class ControllerCrashRecoveryWindow extends AbstractGenericFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -144133142410049380L;

	public static synchronized ControllerCrashRecoveryWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new ControllerCrashRecoveryWindow(port);
	}

	public ControllerCrashRecoveryWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Controller Crash Recovery", port);

		setBounds(100, 100, 750, 600);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.8, 0.2 };
		getContentPane().setLayout(gridBagLayout);

		JPanel upperPanel = new JPanel();
		GridBagConstraints gbc_upperPanel = new GridBagConstraints();
		gbc_upperPanel.insets = new Insets(0, 0, 5, 0);
		gbc_upperPanel.fill = GridBagConstraints.BOTH;
		gbc_upperPanel.gridx = 0;
		gbc_upperPanel.gridy = 0;
		getContentPane().add(upperPanel, gbc_upperPanel);
		GridBagLayout gbl_upperPanel = new GridBagLayout();
		gbl_upperPanel.columnWidths = new int[] { 0, 0 };
		gbl_upperPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_upperPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_upperPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
		upperPanel.setLayout(gbl_upperPanel);

		JPanel panel1 = new JPanel();
		panel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel1.add(new ControllerCrashRecoveryPanel(1, port));
		GridBagConstraints gbc_panel1 = new GridBagConstraints();
		gbc_panel1.fill = GridBagConstraints.BOTH;
		gbc_panel1.gridx = 0;
		gbc_panel1.gridy = 0;
		gbc_panel1.insets = new Insets(10, 5, 5, 5);
		upperPanel.add(panel1, gbc_panel1);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel2.add(new ControllerCrashRecoveryPanel(2, port));
		GridBagConstraints gbc_panel2 = new GridBagConstraints();
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.gridx = 0;
		gbc_panel2.gridy = 1;
		gbc_panel2.insets = new Insets(5, 5, 5, 5);
		upperPanel.add(panel2, gbc_panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel3.add(new ControllerCrashRecoveryPanel(3, port));
		GridBagConstraints gbc_panel3 = new GridBagConstraints();
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 0;
		gbc_panel3.gridy = 2;
		gbc_panel3.insets = new Insets(5, 5, 5, 5);
		upperPanel.add(panel3, gbc_panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel4.add(new ControllerCrashRecoveryPanel(4, port));
		GridBagConstraints gbc_panel4 = new GridBagConstraints();
		gbc_panel4.fill = GridBagConstraints.BOTH;
		gbc_panel4.gridx = 0;
		gbc_panel4.gridy = 3;
		gbc_panel4.insets = new Insets(5, 5, 5, 5);
		upperPanel.add(panel4, gbc_panel4);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel5.add(new ControllerCrashRecoveryPanel(5, port));
		GridBagConstraints gbc_panel5 = new GridBagConstraints();
		gbc_panel5.fill = GridBagConstraints.BOTH;
		gbc_panel5.insets = new Insets(10, 5, 5, 5);
		gbc_panel5.gridx = 1;
		gbc_panel5.gridy = 0;
		upperPanel.add(panel5, gbc_panel5);

		JPanel panel6 = new JPanel();
		panel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel6.add(new ControllerCrashRecoveryPanel(6, port));
		GridBagConstraints gbc_panel6 = new GridBagConstraints();
		gbc_panel6.fill = GridBagConstraints.BOTH;
		gbc_panel6.insets = new Insets(5, 5, 5, 5);
		gbc_panel6.gridx = 1;
		gbc_panel6.gridy = 1;
		upperPanel.add(panel6, gbc_panel6);

		JPanel panel7 = new JPanel();
		panel7.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel7.add(new ControllerCrashRecoveryPanel(7, port));
		GridBagConstraints gbc_panel7 = new GridBagConstraints();
		gbc_panel7.fill = GridBagConstraints.BOTH;
		gbc_panel7.insets = new Insets(5, 5, 5, 5);
		gbc_panel7.gridx = 1;
		gbc_panel7.gridy = 2;
		upperPanel.add(panel7, gbc_panel7);

		JPanel panel8 = new JPanel();
		panel8.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel8.add(new ControllerCrashRecoveryPanel(8, port));
		GridBagConstraints gbc_panel8 = new GridBagConstraints();
		gbc_panel8.fill = GridBagConstraints.BOTH;
		gbc_panel8.insets = new Insets(5, 5, 5, 5);
		gbc_panel8.gridx = 1;
		gbc_panel8.gridy = 3;
		upperPanel.add(panel8, gbc_panel8);

		JPanel bottomPanel = new JPanel();
		GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
		gbc_bottomPanel.fill = GridBagConstraints.BOTH;
		gbc_bottomPanel.gridx = 0;
		gbc_bottomPanel.gridy = 1;
		getContentPane().add(bottomPanel, gbc_bottomPanel);
		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[] { 0, 0 };
		gbl_bottomPanel.rowHeights = new int[] { 0 };
		gbl_bottomPanel.columnWeights = new double[] { 1.0, 0.3 };
		gbl_bottomPanel.rowWeights = new double[] { 1.0 };
		bottomPanel.setLayout(gbl_bottomPanel);

		JPanel emptyPanel = new JPanel();
		GridBagConstraints gbc_emptyPanel = new GridBagConstraints();
		gbc_emptyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emptyPanel.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel.gridx = 0;
		gbc_emptyPanel.gridy = 0;
		bottomPanel.add(emptyPanel, gbc_emptyPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.addActionListener(this);
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.gridx = 1;
		gbc_exitButton.gridy = 0;
		bottomPanel.add(exitButton, gbc_exitButton);
	}

	protected void manageOtherActions(ActionEvent event)
	{
	}

}
