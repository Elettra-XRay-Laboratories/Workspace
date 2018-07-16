package com.elettra.lab.metrology.lpt.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.TubeStabilityPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.metrology.lpt.panels.LPTStabilityPanel;

public class LTPStabilityScanWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long	 serialVersionUID	= -1078811054880810865L;

	private TubeStabilityPanel	stabilityPanel;

	public static synchronized LTPStabilityScanWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new LTPStabilityScanWindow(port);
	}

	private LTPStabilityScanWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Tube Stability Test", port);

		this.setIconImage(ImageIO.read(new File("ltpcontroller.jpg")));

		this.setBounds(5, 5, 780, 850);

		JPanel leftPanel = new JPanel();
				
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0};
		gbl_leftPanel.rowHeights = new int[] { 750, 50, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_leftPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };

		leftPanel.setLayout(gbl_leftPanel);
		leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	
		getContentPane().add(leftPanel);

		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 5, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 0;
		gbc_movepanel4.gridy = 0;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		stabilityPanel = new LPTStabilityPanel(this.getPort());
		movePanel4.add(stabilityPanel);
		leftPanel.add(movePanel4, gbc_movepanel4);

		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(5, 5, 5, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 0;
		gbc_movepanel5.gridy = 1;
		JPanel movePanel5 = new JPanel();
		leftPanel.add(movePanel5, gbc_movepanel5);

		
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

		leftPanel.add(movePanel5, gbc_movepanel5);

	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
