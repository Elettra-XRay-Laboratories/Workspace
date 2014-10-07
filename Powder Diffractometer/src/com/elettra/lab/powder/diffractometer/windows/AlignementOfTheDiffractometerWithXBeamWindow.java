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
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.powder.diffractometer.Axis;
import com.elettra.lab.powder.diffractometer.panels.I0ScanPanel;
import com.elettra.lab.powder.diffractometer.panels.SaveBeamOffsetPanel;
import com.elettra.lab.powder.diffractometer.panels.StartBasementAlignementPanel;

public class AlignementOfTheDiffractometerWithXBeamWindow extends AbstractGenericFrame
{
	static class ActionCommands
	{
		private static final String EXIT = "EXIT";
	}

	private static final long serialVersionUID = -513690344812082943L;

	public static synchronized AlignementOfTheDiffractometerWithXBeamWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new AlignementOfTheDiffractometerWithXBeamWindow(port);
	}

	private AlignementOfTheDiffractometerWithXBeamWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Alignement of the Diffractometer with the Beam", port);

		this.setBounds(5, 5, 1435, 900);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 300, 835, 300 };
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
		gbl_leftPanel.columnWidths = new int[] { 280 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 0.0 };
		gbl_leftPanel.rowWeights = new double[] { 1.0, 1.0 };
		leftPanel.setLayout(gbl_leftPanel);

		JPanel movePanel7 = new JPanel();
		movePanel7.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel7 = new GridBagConstraints();
		gbc_movePanel7.insets = new Insets(10, 5, 5, 5);
		gbc_movePanel7.fill = GridBagConstraints.BOTH;
		gbc_movePanel7.gridx = 0;
		gbc_movePanel7.gridy = 0;
		leftPanel.add(movePanel7, gbc_movePanel7);
		movePanel7.add(new MovePanel(Axis.OMEGA, this.getPort()));

		JPanel movePanel5 = new JPanel();
		movePanel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
		gbc_movePanel5.insets = new Insets(0, 5, 5, 5);
		gbc_movePanel5.fill = GridBagConstraints.BOTH;
		gbc_movePanel5.gridx = 0;
		gbc_movePanel5.gridy = 1;
		leftPanel.add(movePanel5, gbc_movePanel5);
		movePanel5.add(new MovePanel(Axis.TWOTHETA, this.getPort()));

		JPanel rightPanel = new JPanel();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.insets = new Insets(0, 0, 0, 5);
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightPanel, gbc_rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] { 0 };
		gbl_rightPanel.rowHeights = new int[] { 0 };
		gbl_rightPanel.columnWeights = new double[] { 1.0 };
		gbl_rightPanel.rowWeights = new double[] { 1.0 };
		rightPanel.setLayout(gbl_rightPanel);

		Rectangle bounds = new Rectangle(0, 0, 750, 845);

		JPanel scan0Panel = new JPanel();
		scan0Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan0Panel.setBounds(bounds);
		scan0Panel.add(new I0ScanPanel(Axis.TWOTHETA, this.getPort(), false));
		GridBagConstraints gbc_scan0Panel = new GridBagConstraints();
		gbc_scan0Panel.insets = new Insets(10, 5, 5, 5);
		gbc_scan0Panel.fill = GridBagConstraints.BOTH;
		gbc_scan0Panel.gridx = 0;
		gbc_scan0Panel.gridy = 0;
		rightPanel.add(scan0Panel, gbc_scan0Panel);

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
		gbc_buttonsPanel.insets = new Insets(10, 5, 500, 10);
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		lateralPanel.add(buttonsPanel, gbc_buttonsPanel);
		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[] { 0 };
		gbl_buttonsPanel.rowHeights = new int[] { 0, 0 };
		gbl_buttonsPanel.columnWeights = new double[] { 1.0 };
		gbl_buttonsPanel.rowWeights = new double[] { 1.0, 1.0 };
		buttonsPanel.setLayout(gbl_buttonsPanel);

		JPanel button1Panel = new JPanel();
		GridBagConstraints gbc_button1Panel = new GridBagConstraints();
		gbc_button1Panel.insets = new Insets(0, 5, 5, 5);
		gbc_button1Panel.fill = GridBagConstraints.BOTH;
		gbc_button1Panel.gridx = 0;
		gbc_button1Panel.gridy = 0;
		button1Panel.setLayout(new BorderLayout(0, 0));
		button1Panel.add(new SaveBeamOffsetPanel(port));
		buttonsPanel.add(button1Panel, gbc_button1Panel);

		JPanel button2Panel = new JPanel();
		GridBagConstraints gbc_button2Panel = new GridBagConstraints();
		gbc_button2Panel.insets = new Insets(0, 5, 5, 5);
		gbc_button2Panel.fill = GridBagConstraints.BOTH;
		gbc_button2Panel.gridx = 0;
		gbc_button2Panel.gridy = 1;
		button2Panel.setLayout(new BorderLayout(0, 0));
		button2Panel.add(new StartBasementAlignementPanel());
		buttonsPanel.add(button2Panel, gbc_button2Panel);

		JPanel exitPanel = new JPanel();
		GridBagConstraints gbc_exitPanel = new GridBagConstraints();
		gbc_exitPanel.fill = GridBagConstraints.BOTH;
		gbc_exitPanel.insets = new Insets(5, 5, 5, 10);
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
