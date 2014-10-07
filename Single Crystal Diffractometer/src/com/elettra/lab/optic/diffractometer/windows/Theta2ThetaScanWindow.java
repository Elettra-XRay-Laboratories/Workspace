package com.elettra.lab.optic.diffractometer.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.DoubleMovePanel;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;

public class Theta2ThetaScanWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long serialVersionUID = 3126339410734690081L;
	/**
   * 
   */

	private MovePanel         panelZ;
	private MovePanel         panelX;
	private DoubleMovePanel   panelTheta2Theta;

	private ScanPanel         scanTheta2ThetaPanel;

	public static synchronized Theta2ThetaScanWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new Theta2ThetaScanWindow(port);
	}

	private Theta2ThetaScanWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("2THETA - THETA Scan", port);

		this.setBounds(5, 5, 1675, 1000);

		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
		gbl_leftPanel.rowWeights = new double[] { 0.5, 0.5 };

		getContentPane().setLayout(gbl_leftPanel);

		panelX = new MovePanel(Axis.X, this.getPort());
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 0;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panelX);
		getContentPane().add(movePanel2, gbc_movepanel2);

		panelZ = new MovePanel(Axis.Z, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 1;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panelZ);
		getContentPane().add(movePanel1, gbc_movepanel1);

		panelTheta2Theta = new DoubleMovePanel(Axis.THETA2THETA, this.getPort());
		GridBagConstraints gbc_movepanel3 = new GridBagConstraints();
		gbc_movepanel3.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel3.fill = GridBagConstraints.BOTH;
		gbc_movepanel3.gridx = 2;
		gbc_movepanel3.gridy = 0;
		JPanel movePanel3 = new JPanel();
		movePanel3.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel3.add(panelTheta2Theta);
		getContentPane().add(movePanel3, gbc_movepanel3);

		scanTheta2ThetaPanel = new ScanPanel(Axis.THETA2THETA, this.getPort(), false);

		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 5, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 3;
		gbc_movepanel4.gridy = 0;
		gbc_movepanel4.gridheight = 2;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel4.add(scanTheta2ThetaPanel);
		getContentPane().add(movePanel4, gbc_movepanel4);

		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(10, 5, 5, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 0;
		gbc_movepanel5.gridy = 1;
		gbc_movepanel5.gridwidth = 3;
		JPanel movePanel5 = new JPanel();
		GridBagLayout gbl_movePanel5 = new GridBagLayout();
		gbl_movePanel5.columnWidths = new int[] { 0, 0 };
		gbl_movePanel5.rowHeights = new int[] { 0, 0, 0 };
		gbl_movePanel5.columnWeights = new double[] { 1.0, 0.2 };
		gbl_movePanel5.rowWeights = new double[] { 1.0, 0.1, 0.05 };
		movePanel5.setLayout(gbl_movePanel5);

		GridBagConstraints gbc_movepanel5inner = new GridBagConstraints();
		gbc_movepanel5inner.insets = new Insets(5, 5, 0, 5);
		gbc_movepanel5inner.fill = GridBagConstraints.BOTH;
		gbc_movepanel5inner.anchor = GridBagConstraints.EAST;
		gbc_movepanel5inner.gridx = 1;
		gbc_movepanel5inner.gridy = 1;

		movePanel5.add(new EmergencyDumpPanel(this.getPort()), gbc_movepanel5inner);

		GridBagConstraints gbc_movepanel5inner2 = new GridBagConstraints();
		gbc_movepanel5inner2.insets = new Insets(5, 5, 5, 5);
		gbc_movepanel5inner2.fill = GridBagConstraints.BOTH;
		gbc_movepanel5inner2.anchor = GridBagConstraints.EAST;
		gbc_movepanel5inner2.gridx = 1;
		gbc_movepanel5inner2.gridy = 2;
		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		movePanel5.add(exitButton, gbc_movepanel5inner2);

		getContentPane().add(movePanel5, gbc_movepanel5);

	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
