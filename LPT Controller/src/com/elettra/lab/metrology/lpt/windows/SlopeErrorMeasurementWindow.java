package com.elettra.lab.metrology.lpt.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.EmergencyStopPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.panels.LPTMovePanel;
import com.elettra.lab.metrology.lpt.panels.LPTScanPanel;

public class SlopeErrorMeasurementWindow extends AbstractGenericFrame
{
	static class ActionCommands
	{
		private static final String	EXIT	= "EXIT";
	}

	private static final long	serialVersionUID	= -513690344812082943L;

	public static synchronized SlopeErrorMeasurementWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new SlopeErrorMeasurementWindow(port);
	}

	private SlopeErrorMeasurementWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Slope Error Measurement", port);

		this.setIconImage(ImageIO.read(new File("ltpcontroller.jpg")));

		this.setBounds(1680, 20, 1590, 900);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 300, 1250 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
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
		gbl_leftPanel.rowHeights = new int[] { 450, 450 };
		gbl_leftPanel.columnWeights = new double[] { 0.0 };
		gbl_leftPanel.rowWeights = new double[] { 1.0, 1.0 };
		leftPanel.setLayout(gbl_leftPanel);

		JPanel movePanel6 = new JPanel();
		movePanel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
		gbc_movePanel6.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel6.fill = GridBagConstraints.BOTH;
		gbc_movePanel6.gridx = 0;
		gbc_movePanel6.gridy = 0;
		leftPanel.add(movePanel6, gbc_movePanel6);
		movePanel6.add(new LPTMovePanel(Axis.MOTOR5, this.getPort()));

		// --------------

		JPanel buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.insets = new Insets(0, 0, 0, 0);
		gbc_buttonPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 1;
		leftPanel.add(buttonPanel, gbc_buttonPanel);

		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[] { 0 };
		gbl_buttonPanel.rowHeights = new int[] { 290, 90, 30 };
		gbl_buttonPanel.columnWeights = new double[] { 0.0 };
		gbl_buttonPanel.rowWeights = new double[] { 0, 0, 0 };
		buttonPanel.setLayout(gbl_buttonPanel);

		GridBagConstraints gbc_emptyPanel2 = new GridBagConstraints();
		gbc_emptyPanel2.insets = new Insets(50, 0, 55, 5);
		gbc_emptyPanel2.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel2.gridx = 0;
		gbc_emptyPanel2.gridy = 0;
		buttonPanel.add(new EmergencyStopPanel(), gbc_emptyPanel2);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(5, 0, 5, 5);
		gbc_emergencyDumpPanel.gridx = 0;
		gbc_emergencyDumpPanel.gridy = 1;
		buttonPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(ActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.gridx = 0;
		gbc_exitButton.gridy = 2;
		buttonPanel.add(exitButton, gbc_exitButton);

		// --------------------------------------------------------------------

		JPanel scan4Panel = new JPanel();
		scan4Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan4Panel.setBounds(new Rectangle(0, 0, 1250, 845));
		scan4Panel.add(new LPTScanPanel(Axis.MOTOR5, this.getPort(), false));

		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.insets = new Insets(10, 10, 5, 5);
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(scan4Panel, gbc_rightPanel);

		// --------------------------------------------------------------------
	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
