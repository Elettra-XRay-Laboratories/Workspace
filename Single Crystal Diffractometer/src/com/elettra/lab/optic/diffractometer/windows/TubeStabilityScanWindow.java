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
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.TubeStabilityPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;

public class TubeStabilityScanWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long  serialVersionUID = -1078811054880810865L;

	private MovePanel          panel2thetafirst;
	private MovePanel          panel2theta;

	private TubeStabilityPanel stabilityPanel;

	public static synchronized TubeStabilityScanWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new TubeStabilityScanWindow(port);
	}

	private TubeStabilityScanWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Tube Stability Test", port);

		this.setBounds(5, 5, 1300, 1000);

		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, 1.0, 1.0 };
		gbl_leftPanel.rowWeights = new double[] { 0.3, 0.7 };

		getContentPane().setLayout(gbl_leftPanel);

		panel2theta = new MovePanel(Axis.TWOTHETA, this.getPort());
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 0;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panel2theta);
		getContentPane().add(movePanel2, gbc_movepanel2);

		panel2thetafirst = new MovePanel(Axis.TWOTHETAFIRST, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 1;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panel2thetafirst);
		getContentPane().add(movePanel1, gbc_movepanel1);

		stabilityPanel = new TubeStabilityPanel(this.getPort());

		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 5, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 3;
		gbc_movepanel4.gridy = 0;
		gbc_movepanel4.gridheight = 2;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel4.add(stabilityPanel);
		getContentPane().add(movePanel4, gbc_movepanel4);

		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(10, 5, 5, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 0;
		gbc_movepanel5.gridy = 1;
		gbc_movepanel5.gridwidth = 2;
		JPanel movePanel5 = new JPanel();
		GridBagLayout gbl_movePanel5 = new GridBagLayout();
		gbl_movePanel5.columnWidths = new int[] { 0, 0 };
		gbl_movePanel5.rowHeights = new int[] { 0, 0 };
		gbl_movePanel5.columnWeights = new double[] { 0.6, 0.4 };
		gbl_movePanel5.rowWeights = new double[] { 1.0, 0.02 };
		movePanel5.setLayout(gbl_movePanel5);
		GridBagConstraints gbc_movepanel5inner = new GridBagConstraints();
		gbc_movepanel5inner.insets = new Insets(5, 5, 5, 5);
		gbc_movepanel5inner.fill = GridBagConstraints.BOTH;
		gbc_movepanel5inner.gridx = 1;
		gbc_movepanel5inner.gridy = 1;
		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		movePanel5.add(exitButton, gbc_movepanel5inner);

		getContentPane().add(movePanel5, gbc_movepanel5);
	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
