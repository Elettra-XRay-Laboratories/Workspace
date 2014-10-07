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
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;
import com.elettra.lab.optic.diffractometer.panels.MovementMatrixPanel;
import com.elettra.lab.optic.diffractometer.panels.RockingCurveTopographyPanel;

public class RockingCurveMeasureWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long           serialVersionUID = -6824894547503916913L;

	private MovePanel                   panelZ;
	private MovePanel                   panelX;
	private MovePanel                   panelTheta;

	private MovementMatrixPanel         movementMatrixPanel;

	private ScanPanel                   scanThetaPanel;

	private RockingCurveTopographyPanel rockingCurveTopographyPanel;

	public static synchronized RockingCurveMeasureWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new RockingCurveMeasureWindow(port);
	}

	private RockingCurveMeasureWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Rocking Curve Topography", port);

		this.setBounds(5, 5, 3350, 1000);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 3350 / 2, 3350 / 2 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, };
		getContentPane().setLayout(gridBagLayout);

		JPanel leftPanel = new JPanel();
		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftPanel.fill = GridBagConstraints.BOTH;
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
		gbl_leftPanel.rowWeights = new double[] { 1.0, 0.2 };
		leftPanel.setLayout(gbl_leftPanel);

		panelX = new MovePanel(Axis.X, this.getPort());
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 0;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panelX);
		leftPanel.add(movePanel2, gbc_movepanel2);

		panelZ = new MovePanel(Axis.Z, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 1;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panelZ);
		leftPanel.add(movePanel1, gbc_movepanel1);

		panelTheta = new MovePanel(Axis.THETA, this.getPort());
		GridBagConstraints gbc_movepanel3 = new GridBagConstraints();
		gbc_movepanel3.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel3.fill = GridBagConstraints.BOTH;
		gbc_movepanel3.gridx = 2;
		gbc_movepanel3.gridy = 0;
		JPanel movePanel3 = new JPanel();
		movePanel3.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel3.add(panelTheta);
		leftPanel.add(movePanel3, gbc_movepanel3);

		scanThetaPanel = new ScanPanel(Axis.THETA, this.getPort(), true, Axis.X, Axis.Z);

		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 5, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 3;
		gbc_movepanel4.gridy = 0;
		gbc_movepanel4.gridheight = 2;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel4.add(scanThetaPanel);
		leftPanel.add(movePanel4, gbc_movepanel4);

		movementMatrixPanel = new MovementMatrixPanel(this.getPort());

		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(10, 5, 5, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 0;
		gbc_movepanel5.gridy = 1;
		gbc_movepanel5.gridwidth = 3;
		JPanel movePanel5 = new JPanel();
		movePanel5.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		GridBagLayout gbl_movePanel5 = new GridBagLayout();
		gbl_movePanel5.columnWidths = new int[] { 0 };
		gbl_movePanel5.rowHeights = new int[] { 0 };
		gbl_movePanel5.columnWeights = new double[] { 1.0 };
		gbl_movePanel5.rowWeights = new double[] { 1.0 };
		movePanel5.setLayout(gbl_movePanel5);
		GridBagConstraints gbc_movepanel5inner = new GridBagConstraints();
		gbc_movepanel5inner.insets = new Insets(5, 5, 5, 5);
		gbc_movepanel5inner.fill = GridBagConstraints.VERTICAL;
		gbc_movepanel5inner.anchor = GridBagConstraints.WEST;
		gbc_movepanel5inner.gridx = 0;
		gbc_movepanel5inner.gridy = 0;
		movePanel5.add(movementMatrixPanel, gbc_movepanel5inner);
		leftPanel.add(movePanel5, gbc_movepanel5);

		JPanel rightPanel = new JPanel();
		GridBagConstraints gbc_rightPanel = new GridBagConstraints();
		gbc_rightPanel.insets = new Insets(0, 0, 0, 5);
		gbc_rightPanel.fill = GridBagConstraints.BOTH;
		gbc_rightPanel.gridx = 1;
		gbc_rightPanel.gridy = 0;
		getContentPane().add(rightPanel, gbc_rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] { (int) ((3350 / 2) * 0.8), (int) ((3350 / 2) * 0.18) };
		gbl_rightPanel.rowHeights = new int[] { 0, 0 };
		gbl_rightPanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_rightPanel.rowWeights = new double[] { 1.0, 0.02 };
		rightPanel.setLayout(gbl_rightPanel);

		rockingCurveTopographyPanel = new RockingCurveTopographyPanel();

		GridBagConstraints gbc_movepanel6 = new GridBagConstraints();
		gbc_movepanel6.insets = new Insets(10, 5, 5, 5);
		gbc_movepanel6.fill = GridBagConstraints.BOTH;
		gbc_movepanel6.gridheight = 2;
		gbc_movepanel6.gridx = 0;
		gbc_movepanel6.gridy = 0;
		JPanel movePanel6 = new JPanel();
		movePanel6.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		GridBagLayout gbl_movePanel6 = new GridBagLayout();
		gbl_movePanel6.columnWidths = new int[] { 0 };
		gbl_movePanel6.rowHeights = new int[] { 0 };
		gbl_movePanel6.columnWeights = new double[] { 1.0 };
		gbl_movePanel6.rowWeights = new double[] { 1.0 };
		movePanel6.setLayout(gbl_movePanel6);
		GridBagConstraints gbc_movepanel6inner = new GridBagConstraints();
		gbc_movepanel6inner.insets = new Insets(5, 10, 5, 5);
		gbc_movepanel6inner.fill = GridBagConstraints.BOTH;
		gbc_movepanel6inner.anchor = GridBagConstraints.CENTER;
		gbc_movepanel6inner.gridx = 0;
		gbc_movepanel6inner.gridy = 0;
		movePanel6.add(rockingCurveTopographyPanel, gbc_movepanel6inner);
		rightPanel.add(movePanel6, gbc_movepanel6);

		JPanel emptyPanel = new JPanel();

		GridBagLayout gbl_emptyPanel = new GridBagLayout();
		gbl_emptyPanel.columnWidths = new int[] { 0 };
		gbl_emptyPanel.rowHeights = new int[] { 0, 0 };
		gbl_emptyPanel.columnWeights = new double[] { 1.0 };
		gbl_emptyPanel.rowWeights = new double[] { 1.0, 0.05 };
		emptyPanel.setLayout(gbl_emptyPanel);
		GridBagConstraints gbc_emptyPanel = new GridBagConstraints();
		gbc_emptyPanel.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel.insets = new Insets(0, 0, 5, 5);
		gbc_emptyPanel.anchor = GridBagConstraints.CENTER;
		gbc_emptyPanel.gridx = 1;
		gbc_emptyPanel.gridy = 0;

		rightPanel.add(emptyPanel, gbc_emptyPanel);

		GridBagConstraints gbc_emptyPanelinner = new GridBagConstraints();
		gbc_emptyPanelinner.fill = GridBagConstraints.BOTH;
		gbc_emptyPanelinner.anchor = GridBagConstraints.CENTER;
		gbc_emptyPanelinner.gridx = 0;
		gbc_emptyPanelinner.gridy = 0;
		emptyPanel.add(new JPanel(), gbc_emptyPanelinner);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.gridx = 0;
		gbc_emergencyDumpPanel.gridy = 1;
		emptyPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.gridx = 1;
		gbc_exitButton.gridy = 1;
		rightPanel.add(exitButton, gbc_exitButton);

	}

	public void manageOtherActions(ActionEvent event)
	{
	}
}
