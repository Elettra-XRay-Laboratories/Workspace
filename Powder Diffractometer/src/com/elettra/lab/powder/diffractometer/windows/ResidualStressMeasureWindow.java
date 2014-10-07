package com.elettra.lab.powder.diffractometer.windows;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.CountingFrequencyPanel;
import com.elettra.controller.gui.panels.DoubleMovePanel;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.powder.diffractometer.Axis;
import com.elettra.lab.powder.diffractometer.panels.I0ScanPanel;
import com.elettra.lab.powder.diffractometer.panels.PsiMovementPanel;
import com.elettra.lab.powder.diffractometer.panels.ResidualStressPanel;
import com.elettra.lab.powder.diffractometer.panels.SampleZReferencePanel;

public class ResidualStressMeasureWindow extends AbstractGenericFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5290599217704961808L;

	static class ActionCommands
	{
		private static final String EXIT = "EXIT";
	}

	public static synchronized ResidualStressMeasureWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new ResidualStressMeasureWindow(port);
	}

	private ResidualStressMeasureWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Alignement of the Sample with the Beam", port);

		this.setBounds(5, 5, 2870, 900);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 600, 1670, 600 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		getContentPane().setLayout(gridBagLayout);

		JPanel leftPanel = new JPanel();
		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftPanel.anchor = GridBagConstraints.WEST;
		gbc_leftPanel.fill = GridBagConstraints.BOTH;
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 280, 280 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_leftPanel.rowWeights = new double[] { 1.0, 1.0 };
		leftPanel.setLayout(gbl_leftPanel);

		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel7 = new GridBagConstraints();
		gbc_movePanel7.insets = new Insets(10, 5, 5, 5);
		gbc_movePanel7.fill = GridBagConstraints.BOTH;
		gbc_movePanel7.gridx = 0;
		gbc_movePanel7.gridy = 0;
		leftPanel.add(movePanel1, gbc_movePanel7);
		movePanel1.add(new MovePanel(Axis.OMEGA, this.getPort()));

		JPanel doubleMovePanel1 = new JPanel();
		doubleMovePanel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
		gbc_movePanel5.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel5.fill = GridBagConstraints.BOTH;
		gbc_movePanel5.gridx = 1;
		gbc_movePanel5.gridy = 0;
		leftPanel.add(doubleMovePanel1, gbc_movePanel5);
		doubleMovePanel1.add(new DoubleMovePanel(Axis.OMEGA2THETA, this.getPort()));

		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
		gbc_movePanel6.insets = new Insets(0, 5, 5, 5);
		gbc_movePanel6.fill = GridBagConstraints.BOTH;
		gbc_movePanel6.gridx = 0;
		gbc_movePanel6.gridy = 1;
		leftPanel.add(movePanel2, gbc_movePanel6);
		movePanel2.add(new MovePanel(Axis.TWOTHETA, this.getPort()));

		JPanel doubleMovePanel2 = new JPanel();
		doubleMovePanel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_doubleMovePanel1 = new GridBagConstraints();
		gbc_doubleMovePanel1.insets = new Insets(0, 0, 5, 5);
		gbc_doubleMovePanel1.fill = GridBagConstraints.BOTH;
		gbc_doubleMovePanel1.gridx = 1;
		gbc_doubleMovePanel1.gridy = 1;
		leftPanel.add(doubleMovePanel2, gbc_doubleMovePanel1);
		doubleMovePanel2.add(new PsiMovementPanel(this.getPort()));

		JPanel rightPanel = new JPanel();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.insets = new Insets(0, 0, 0, 5);
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightPanel, gbc_rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] { 0, 0 };
		gbl_rightPanel.rowHeights = new int[] { 0 };
		gbl_rightPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_rightPanel.rowWeights = new double[] { 1.0 };
		rightPanel.setLayout(gbl_rightPanel);

		Rectangle bounds = new Rectangle(0, 0, 750, 845);

		JPanel scan0Panel = new JPanel();
		scan0Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan0Panel.setBounds(bounds);
		scan0Panel.add(new ResidualStressPanel(this.getPort()));
		GridBagConstraints gbc_scan0Panel = new GridBagConstraints();
		gbc_scan0Panel.insets = new Insets(10, 85, 5, 5);
		gbc_scan0Panel.fill = GridBagConstraints.BOTH;
		gbc_scan0Panel.gridx = 0;
		gbc_scan0Panel.gridy = 0;
		rightPanel.add(scan0Panel, gbc_scan0Panel);

		JPanel scan1Panel = new JPanel();
		scan1Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan1Panel.setBounds(bounds);
		scan1Panel.add(new I0ScanPanel(Axis.OMEGA2THETA, this.getPort(), true));
		GridBagConstraints gbc_scan1Panel = new GridBagConstraints();
		gbc_scan1Panel.insets = new Insets(10, 5, 5, 5);
		gbc_scan1Panel.fill = GridBagConstraints.BOTH;
		gbc_scan1Panel.gridx = 1;
		gbc_scan1Panel.gridy = 0;
		rightPanel.add(scan1Panel, gbc_scan1Panel);

		JPanel lateralPanel = new JPanel();
		GridBagConstraints gbc_lateralPanel = new GridBagConstraints();
		gbc_lateralPanel.insets = new Insets(0, 0, 0, 5);
		gbc_lateralPanel.anchor = GridBagConstraints.WEST;
		gbc_lateralPanel.fill = GridBagConstraints.BOTH;
		gbc_lateralPanel.gridx = 2;
		gbc_lateralPanel.gridy = 0;
		getContentPane().add(lateralPanel, gbc_lateralPanel);
		GridBagLayout gbl_lateralPanel = new GridBagLayout();
		gbl_lateralPanel.columnWidths = new int[] { 0 };
		gbl_lateralPanel.rowHeights = new int[] { 0, 0 };
		gbl_lateralPanel.columnWeights = new double[] { 1.0 };
		gbl_lateralPanel.rowWeights = new double[] { 1.0, 1.0 };
		lateralPanel.setLayout(gbl_lateralPanel);

		JPanel buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanel.insets = new Insets(10, 5, 450, 300);
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		lateralPanel.add(buttonsPanel, gbc_buttonsPanel);

		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[] { 0 };
		gbl_buttonsPanel.rowHeights = new int[] { 0, 0 };
		gbl_buttonsPanel.columnWeights = new double[] { 1.0 };
		gbl_buttonsPanel.rowWeights = new double[] { 1.0, 1.0 };
		buttonsPanel.setLayout(gbl_buttonsPanel);

		JPanel button0Panel = new JPanel();
		GridBagConstraints gbc_button0Panel = new GridBagConstraints();
		gbc_button0Panel.insets = new Insets(0, 5, 5, 5);
		gbc_button0Panel.fill = GridBagConstraints.BOTH;
		gbc_button0Panel.gridx = 0;
		gbc_button0Panel.gridy = 0;
		button0Panel.setLayout(new BorderLayout(0, 0));
		button0Panel.add(new SampleZReferencePanel(port));
		buttonsPanel.add(button0Panel, gbc_button0Panel);

		JPanel button1Panel = new JPanel();
		GridBagConstraints gbc_button1Panel = new GridBagConstraints();
		gbc_button1Panel.insets = new Insets(0, 5, 5, 5);
		gbc_button1Panel.fill = GridBagConstraints.BOTH;
		gbc_button1Panel.gridx = 0;
		gbc_button1Panel.gridy = 1;
		button1Panel.setLayout(new BorderLayout(0, 0));
		button1Panel.add(new CountingFrequencyPanel(port));
		buttonsPanel.add(button1Panel, gbc_button1Panel);

		JPanel exitPanel = new JPanel();
		GridBagConstraints gbc_exitPanel = new GridBagConstraints();
		gbc_exitPanel.fill = GridBagConstraints.BOTH;
		gbc_exitPanel.insets = new Insets(5, 5, 5, 300);
		gbc_exitPanel.gridx = 0;
		gbc_exitPanel.gridy = 1;
		gbc_exitPanel.gridheight = 2;
		lateralPanel.add(exitPanel, gbc_exitPanel);
		GridBagLayout gbl_exitPanel = new GridBagLayout();
		gbl_exitPanel.columnWidths = new int[] { 0 };
		gbl_exitPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_exitPanel.columnWeights = new double[] { 1.0 };
		gbl_exitPanel.rowWeights = new double[] { 1.0, 0.05, 0.02 };
		exitPanel.setLayout(gbl_exitPanel);

		JPanel emptyPanel2 = new JPanel();
		GridBagConstraints gbc_emptyPanel2 = new GridBagConstraints();
		gbc_emptyPanel2.insets = new Insets(0, 0, 5, 5);
		gbc_emptyPanel2.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel2.gridx = 0;
		gbc_emptyPanel2.gridy = 0;
		exitPanel.add(emptyPanel2, gbc_emptyPanel2);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 5);
		gbc_emergencyDumpPanel.gridx = 0;
		gbc_emergencyDumpPanel.gridy = 1;
		exitPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(ActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.gridx = 0;
		gbc_exitButton.gridy = 2;
		exitPanel.add(exitButton, gbc_exitButton);
	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
