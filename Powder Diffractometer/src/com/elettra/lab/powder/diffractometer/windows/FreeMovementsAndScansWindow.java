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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.CountingFrequencyPanel;
import com.elettra.controller.gui.panels.DoubleMovePanel;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.EmergencyStopPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.powder.diffractometer.Axis;
import com.elettra.lab.powder.diffractometer.panels.I0ScanPanel;
import com.elettra.lab.powder.diffractometer.panels.PsiMovementPanel;
import com.elettra.lab.powder.diffractometer.panels.SampleZReferencePanel;

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
	private JComponent        scan3Panel;
	private JPanel            scan4Panel;
	private JPanel            scan5Panel;
	private JPanel            scan6Panel;
	private JComponent        scan7Panel;

	public static synchronized FreeMovementsAndScansWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new FreeMovementsAndScansWindow(port);
	}

	private FreeMovementsAndScansWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Free Movements and Scans", port);

		this.setBounds(5, 5, 2870, 900);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1470, 800, 500 };
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
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
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
		movePanel1.add(new MovePanel(Axis.ALPHA, this.getPort()));

		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel2 = new GridBagConstraints();
		gbc_movePanel2.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel2.fill = GridBagConstraints.BOTH;
		gbc_movePanel2.gridx = 1;
		gbc_movePanel2.gridy = 0;
		leftPanel.add(movePanel2, gbc_movePanel2);
		movePanel2.add(new MovePanel(Axis.BETA, this.getPort()));

		JPanel movePanel7 = new JPanel();
		movePanel7.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel7 = new GridBagConstraints();
		gbc_movePanel7.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel7.fill = GridBagConstraints.BOTH;
		gbc_movePanel7.gridx = 2;
		gbc_movePanel7.gridy = 0;
		leftPanel.add(movePanel7, gbc_movePanel7);
		movePanel7.add(new MovePanel(Axis.Z, this.getPort()));

		JPanel movePanel3 = new JPanel();
		movePanel3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel3 = new GridBagConstraints();
		gbc_movePanel3.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel3.fill = GridBagConstraints.BOTH;
		gbc_movePanel3.gridx = 3;
		gbc_movePanel3.gridy = 0;
		leftPanel.add(movePanel3, gbc_movePanel3);
		movePanel3.add(new MovePanel(Axis.X, this.getPort()));

		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel4 = new GridBagConstraints();
		gbc_movePanel4.insets = new Insets(10, 0, 5, 5);
		gbc_movePanel4.fill = GridBagConstraints.BOTH;
		gbc_movePanel4.gridx = 4;
		gbc_movePanel4.gridy = 0;
		leftPanel.add(movePanel4, gbc_movePanel4);
		movePanel4.add(new MovePanel(Axis.Y, this.getPort()));

		JPanel movePanel5 = new JPanel();
		movePanel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
		gbc_movePanel5.insets = new Insets(0, 5, 5, 5);
		gbc_movePanel5.fill = GridBagConstraints.BOTH;
		gbc_movePanel5.gridx = 0;
		gbc_movePanel5.gridy = 1;
		leftPanel.add(movePanel5, gbc_movePanel5);
		movePanel5.add(new MovePanel(Axis.OMEGA, this.getPort()));

		JPanel movePanel6 = new JPanel();
		movePanel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
		gbc_movePanel6.insets = new Insets(0, 0, 5, 5);
		gbc_movePanel6.fill = GridBagConstraints.BOTH;
		gbc_movePanel6.gridx = 1;
		gbc_movePanel6.gridy = 1;
		leftPanel.add(movePanel6, gbc_movePanel6);
		movePanel6.add(new MovePanel(Axis.TWOTHETA, this.getPort()));

		JPanel doubleMovePanel2 = new JPanel();
		doubleMovePanel2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_doubleMovePanel2 = new GridBagConstraints();
		gbc_doubleMovePanel2.insets = new Insets(0, 0, 5, 5);
		gbc_doubleMovePanel2.fill = GridBagConstraints.BOTH;
		gbc_doubleMovePanel2.gridx = 2;
		gbc_doubleMovePanel2.gridy = 1;
		leftPanel.add(doubleMovePanel2, gbc_doubleMovePanel2);
		doubleMovePanel2.add(new DoubleMovePanel(Axis.OMEGA2THETA, this.getPort()));

		JPanel doubleMovePanel1 = new JPanel();
		doubleMovePanel1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_doubleMovePanel1 = new GridBagConstraints();
		gbc_doubleMovePanel1.insets = new Insets(0, 0, 5, 5);
		gbc_doubleMovePanel1.fill = GridBagConstraints.BOTH;
		gbc_doubleMovePanel1.gridx = 3;
		gbc_doubleMovePanel1.gridy = 1;
		leftPanel.add(doubleMovePanel1, gbc_doubleMovePanel1);
		doubleMovePanel1.add(new PsiMovementPanel(this.getPort()));

		JPanel movePanel8 = new JPanel();
		movePanel8.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_movePanel8 = new GridBagConstraints();
		gbc_movePanel8.insets = new Insets(0, 0, 5, 5);
		gbc_movePanel8.fill = GridBagConstraints.BOTH;
		gbc_movePanel8.gridx = 4;
		gbc_movePanel8.gridy = 1;
		leftPanel.add(movePanel8, gbc_movePanel8);
		movePanel8.add(new MovePanel(Axis.PHI, this.getPort()));

		rightLayeredPanel = new JLayeredPane();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.insets = new Insets(10, 50, 5, 5);
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightLayeredPanel, gbc_rightPanel);

		Rectangle bounds = new Rectangle(0, 0, 750, 845);

		scan0Panel = new JPanel();
		scan0Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan0Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan0Panel, 0);
		rightLayeredPanel.add(scan0Panel);
		scan0Panel.add(new I0ScanPanel(Axis.OMEGA, this.getPort(), false));

		scan1Panel = new JPanel();
		scan1Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan1Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan1Panel, 0);
		rightLayeredPanel.add(scan1Panel);
		scan1Panel.add(new I0ScanPanel(Axis.TWOTHETA, this.getPort(), false));

		scan2Panel = new JPanel();
		scan2Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan2Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan2Panel, 0);
		rightLayeredPanel.add(scan2Panel);
		scan2Panel.add(new I0ScanPanel(Axis.Z, this.getPort(), false));

		scan3Panel = new JPanel();
		scan3Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan3Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan3Panel, 0);
		rightLayeredPanel.add(scan3Panel);
		scan3Panel.add(new I0ScanPanel(Axis.ALPHA, this.getPort(), false));

		scan4Panel = new JPanel();
		scan4Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan4Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan4Panel, 0);
		rightLayeredPanel.add(scan4Panel);
		scan4Panel.add(new I0ScanPanel(Axis.BETA, this.getPort(), false));

		scan5Panel = new JPanel();
		scan5Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan5Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan5Panel, 0);
		rightLayeredPanel.add(scan5Panel);
		scan5Panel.add(new I0ScanPanel(Axis.X, this.getPort(), false));

		scan6Panel = new JPanel();
		scan6Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan6Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan6Panel, 0);
		rightLayeredPanel.add(scan6Panel);
		scan6Panel.add(new I0ScanPanel(Axis.Y, this.getPort(), false));

		scan7Panel = new JPanel();
		scan7Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		scan7Panel.setBounds(bounds);
		rightLayeredPanel.setLayer(scan7Panel, 0);
		rightLayeredPanel.add(scan7Panel);
		scan7Panel.add(new I0ScanPanel(Axis.PHI, this.getPort(), false));

		rightLayeredPanel.moveToFront(scan0Panel);

		JPanel choicePanel = new JPanel();
		GridBagConstraints gbc_choicePanel = new GridBagConstraints();
		gbc_choicePanel.insets = new Insets(0, 0, 0, 400);
		gbc_choicePanel.fill = GridBagConstraints.BOTH;
		gbc_choicePanel.gridx = 2;
		gbc_choicePanel.gridy = 0;
		getContentPane().add(choicePanel, gbc_choicePanel);
		GridBagLayout gbl_choicePanel = new GridBagLayout();
		gbl_choicePanel.columnWidths = new int[] { 0, 0 };
		gbl_choicePanel.rowHeights = new int[] { 0, 0 };
		gbl_choicePanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_choicePanel.rowWeights = new double[] { 1.0, 1.0 };
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
		choicheComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "OMEGA", "2THETA", "Z", "ALPHA", "BETA", "X", "Y", "PHI" }));
		choicheComboBox.addActionListener(this);
		GridBagConstraints gbc_choiceComboBox = new GridBagConstraints();
		gbc_choiceComboBox.fill = GridBagConstraints.NONE;
		gbc_choiceComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_choiceComboBox.anchor = GridBagConstraints.NORTHWEST;
		gbc_choiceComboBox.gridx = 1;
		gbc_choiceComboBox.gridy = 0;

		choicePanel.add(choicheComboBox, gbc_choiceComboBox);

		JPanel exitPanel = new JPanel();
		GridBagConstraints gbc_exitPanel = new GridBagConstraints();
		gbc_exitPanel.fill = GridBagConstraints.BOTH;
		gbc_exitPanel.insets = new Insets(5, 5, 5, 10);
		gbc_exitPanel.gridx = 0;
		gbc_exitPanel.gridy = 1;
		gbc_exitPanel.gridwidth = 2;
		choicePanel.add(exitPanel, gbc_exitPanel);
		GridBagLayout gbl_exitPanel = new GridBagLayout();
		gbl_exitPanel.columnWidths = new int[] { 300 };
		gbl_exitPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_exitPanel.columnWeights = new double[] { 1.0 };
		gbl_exitPanel.rowWeights = new double[] { 1.0, 1.0, 0.05, 0.02 };
		exitPanel.setLayout(gbl_exitPanel);

		JPanel buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanel.insets = new Insets(200, 0, 5, 5);
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		exitPanel.add(buttonsPanel, gbc_buttonsPanel);

		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[] { 0 };
		gbl_buttonsPanel.rowHeights = new int[] { 0, 0 };
		gbl_buttonsPanel.columnWeights = new double[] { 1.0 };
		gbl_buttonsPanel.rowWeights = new double[] { 1.0, 1.0 };
		buttonsPanel.setLayout(gbl_buttonsPanel);

		JPanel button0Panel = new JPanel();
		GridBagConstraints gbc_button0Panel = new GridBagConstraints();
		gbc_button0Panel.insets = new Insets(0, 0, 5, 0);
		gbc_button0Panel.fill = GridBagConstraints.BOTH;
		gbc_button0Panel.gridx = 0;
		gbc_button0Panel.gridy = 0;
		button0Panel.setLayout(new BorderLayout(0, 0));
		button0Panel.add(new SampleZReferencePanel(port));
		buttonsPanel.add(button0Panel, gbc_button0Panel);

		JPanel button1Panel = new JPanel();
		GridBagConstraints gbc_button1Panel = new GridBagConstraints();
		gbc_button1Panel.insets = new Insets(0, 0, 5, 0);
		gbc_button1Panel.fill = GridBagConstraints.BOTH;
		gbc_button1Panel.gridx = 0;
		gbc_button1Panel.gridy = 1;
		button1Panel.setLayout(new BorderLayout(0, 0));
		button1Panel.add(new CountingFrequencyPanel(port));
		buttonsPanel.add(button1Panel, gbc_button1Panel);

		GridBagConstraints gbc_emptyPanel2 = new GridBagConstraints();
		gbc_emptyPanel2.insets = new Insets(10, 0, 45, 5);
		gbc_emptyPanel2.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel2.gridx = 0;
		gbc_emptyPanel2.gridy = 1;
		exitPanel.add(new EmergencyStopPanel(), gbc_emptyPanel2);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 5);
		gbc_emergencyDumpPanel.gridx = 0;
		gbc_emergencyDumpPanel.gridy = 2;
		exitPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(ActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.gridx = 0;
		gbc_exitButton.gridy = 3;
		exitPanel.add(exitButton, gbc_exitButton);

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
				case 5:
					rightLayeredPanel.moveToFront(scan5Panel);
					break;
				case 6:
					rightLayeredPanel.moveToFront(scan6Panel);
					break;
				case 7:
					rightLayeredPanel.moveToFront(scan7Panel);
					break;

				default:
					break;
			}
		}
	}
}
