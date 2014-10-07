package com.elettra.lab.metrology.lpt.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.EmergencyStopPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.metrology.lpt.Axis;
import com.elettra.lab.metrology.lpt.panels.LPTScanPanel;

public class FreeMovementsAndScansWindow extends AbstractGenericFrame
{
	static class ActionCommands
	{
		private static final String EXIT = "EXIT";
	}

	private static final long serialVersionUID = -513690344812082943L;

	private JLayeredPane      rightLayeredPanel;
	private JComboBox<String> choicheComboBox;
	private JPanel            scan0Panel;
	private JPanel            scan1Panel;
	private JPanel            scan2Panel;
	private JPanel            scan3Panel;
	private JPanel            scan4Panel;

	public static synchronized FreeMovementsAndScansWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new FreeMovementsAndScansWindow(port);
	}

	private FreeMovementsAndScansWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Free Movements and Scans", port);

		this.setBounds(5, 5, 2870, 900);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1435, 1250, 185 };
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
		gbl_leftPanel.columnWidths = new int[] { 280, 280, 280, 280, 280 };
		gbl_leftPanel.rowHeights = new int[] { 450, 450 };
		gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0, 0 };
		gbl_leftPanel.rowWeights = new double[] { 1.0, 1.0 };
		leftPanel.setLayout(gbl_leftPanel);

		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel1 = new GridBagConstraints();
		gbc_movePanel1.insets = new Insets(10, 5, 5, 5);
		gbc_movePanel1.fill = GridBagConstraints.BOTH;
		gbc_movePanel1.gridx = 0;
		gbc_movePanel1.gridy = 0;
		leftPanel.add(movePanel1, gbc_movePanel1);
		movePanel1.add(new MovePanel(Axis.MOTOR1, this.getPort()));

		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel2 = new GridBagConstraints();
		gbc_movePanel2.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel2.fill = GridBagConstraints.BOTH;
		gbc_movePanel2.gridx = 1;
		gbc_movePanel2.gridy = 0;
		leftPanel.add(movePanel2, gbc_movePanel2);
		movePanel2.add(new MovePanel(Axis.MOTOR2, this.getPort()));

		JPanel movePanel7 = new JPanel();
		movePanel7.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel7 = new GridBagConstraints();
		gbc_movePanel7.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel7.fill = GridBagConstraints.BOTH;
		gbc_movePanel7.gridx = 2;
		gbc_movePanel7.gridy = 0;
		leftPanel.add(movePanel7, gbc_movePanel7);
		movePanel7.add(new MovePanel(Axis.MOTOR3, this.getPort()));

		JPanel movePanel5 = new JPanel();
		movePanel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
		gbc_movePanel5.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel5.fill = GridBagConstraints.BOTH;
		gbc_movePanel5.gridx = 3;
		gbc_movePanel5.gridy = 0;
		leftPanel.add(movePanel5, gbc_movePanel5);
		movePanel5.add(new MovePanel(Axis.MOTOR4, this.getPort()));

		JPanel movePanel6 = new JPanel();
		movePanel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
		gbc_movePanel6.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel6.fill = GridBagConstraints.BOTH;
		gbc_movePanel6.gridx = 4;
		gbc_movePanel6.gridy = 0;
		leftPanel.add(movePanel6, gbc_movePanel6);
		movePanel6.add(new MovePanel(Axis.MOTOR5, this.getPort()));

		// --------------

		JPanel buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.insets = new Insets(0, 0, 0, 0);
		gbc_buttonPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonPanel.gridx = 4;
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

		rightLayeredPanel = new JLayeredPane();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.insets = new Insets(10, 10, 5, 5);
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightLayeredPanel, gbc_rightPanel);

		Rectangle bounds = new Rectangle(0, 0, 1250, 845);

		scan0Panel = new JPanel();
		scan0Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan0Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan0Panel, 0);
		rightLayeredPanel.add(scan0Panel);
		scan0Panel.add(new LPTScanPanel(Axis.MOTOR1, this.getPort(), false));

		scan1Panel = new JPanel();
		scan1Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan1Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan1Panel, 0);
		rightLayeredPanel.add(scan1Panel);
		scan1Panel.add(new LPTScanPanel(Axis.MOTOR2, this.getPort(), false));

		scan2Panel = new JPanel();
		scan2Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan2Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan2Panel, 0);
		rightLayeredPanel.add(scan2Panel);
		scan2Panel.add(new LPTScanPanel(Axis.MOTOR3, this.getPort(), false));

		scan3Panel = new JPanel();
		scan3Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan3Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan3Panel, 0);
		rightLayeredPanel.add(scan3Panel);
		scan3Panel.add(new LPTScanPanel(Axis.MOTOR4, this.getPort(), false));

		scan4Panel = new JPanel();
		scan4Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan4Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan4Panel, 0);
		rightLayeredPanel.add(scan4Panel);
		scan4Panel.add(new LPTScanPanel(Axis.MOTOR5, this.getPort(), false));

		rightLayeredPanel.moveToFront(scan0Panel);

		// --------------------------------------------------------------------

		JPanel choicePanel = new JPanel();
		GridBagConstraints gbc_choicePanel = new GridBagConstraints();
		gbc_choicePanel.insets = new Insets(0, 0, 0, 0);
		gbc_choicePanel.fill = GridBagConstraints.BOTH;
		gbc_choicePanel.gridx = 2;
		gbc_choicePanel.gridy = 0;
		getContentPane().add(choicePanel, gbc_choicePanel);
		GridBagLayout gbl_choicePanel = new GridBagLayout();
		gbl_choicePanel.columnWidths = new int[] { 0 };
		gbl_choicePanel.rowHeights = new int[] { 0 };
		gbl_choicePanel.columnWeights = new double[] { 0.0 };
		gbl_choicePanel.rowWeights = new double[] { 1.0 };
		choicePanel.setLayout(gbl_choicePanel);

		JLabel label = new JLabel("Select Axis:  ");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.NONE;
		gbc_label.insets = new Insets(10, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.NORTHEAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;

		choicePanel.add(label, gbc_label);

		choicheComboBox = new JComboBox<String>();
		choicheComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "MOTOR1", "MOTOR2", "MOTOR3", "MOTOR4", "MOTOR5" }));
		choicheComboBox.addActionListener(this);
		GridBagConstraints gbc_choiceComboBox = new GridBagConstraints();
		gbc_choiceComboBox.fill = GridBagConstraints.NONE;
		gbc_choiceComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_choiceComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_choiceComboBox.gridx = 1;
		gbc_choiceComboBox.gridy = 0;

		choicePanel.add(choicheComboBox, gbc_choiceComboBox);
	}

	public void manageOtherActions(ActionEvent event)
	{
		if (event.getSource().equals(choicheComboBox))
		{
			int layer = choicheComboBox.getSelectedIndex();

			switch (layer)
			{
				case 0:
					rightLayeredPanel.moveToFront(scan0Panel);
					break;
				case 1:
					rightLayeredPanel.moveToFront(scan1Panel);
					break;
				case 2:
					rightLayeredPanel.moveToFront(scan2Panel);
					break;
				case 3:
					rightLayeredPanel.moveToFront(scan3Panel);
					break;
				case 4:
					rightLayeredPanel.moveToFront(scan4Panel);
					break;
				default:
					break;
			}
		}
	}
}
