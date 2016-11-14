package com.elettra.lab.optic.diffractometer.windows;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;

public class OpticPreAlignementWindow extends AbstractGenericFrame
{

	/**
   * 
   */
	private static final long serialVersionUID = 3397601605264419623L;

	static class ActionCommands
	{
		private static final String PHASE1 = "PHASE1";
		private static final String PHASE2 = "PHASE2";
	}

	static class Messages
	{
		public static final String PHASE1 = "  1 - Posizionare il laser al posto del detector, eseguire il comando REF per l'asse 2THETA.\n\n  2 - Muovere il monocromatore (asse THETA') fino ad ottenere l'allineamento del fascio laser.\n\n  3 - Eseguire l'operazione SET dell'asse THETA', ponendo +90.0000 come posizione.";
		public static final String PHASE2 = "  1 - Muovere il monocromatore (asse THETA') alla posizione ASSOLUTA +45.000\n\n  2 - Muovere il tubo (asse 2THETA') fino ad ottenere l'allineamento del fascio laser.\n\n  3 - Eseguire l'operazione SET, ponendo +90.0000 come posizione.";
	}

	public static synchronized OpticPreAlignementWindow getInstance(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		return new OpticPreAlignementWindow(port);
	}

	private MovePanel panelThetaFirst;
	private MovePanel panel2ThetaFirst;
	private MovePanel panel2Theta;
	private JButton   phase1Button;
	private JButton   phase2Button;
	private JTextArea textArea;

	private OpticPreAlignementWindow(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		super("Optic Pre-Alignement", port);

		this.setBounds(5, 5, 820, 1000);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.05, 0.75, 0.05 };
		getContentPane().setLayout(gridBagLayout);

		panelThetaFirst = new MovePanel(Axis.THETAFIRST, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 0;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panelThetaFirst);
		this.getContentPane().add(movePanel1, gbc_movepanel1);

		panel2ThetaFirst = new MovePanel(Axis.TWOTHETAFIRST, this.getPort());
		panel2ThetaFirst.setEnabled(false);
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 1;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panel2ThetaFirst);
		this.getContentPane().add(movePanel2, gbc_movepanel2);

		panel2Theta = new MovePanel(Axis.TWOTHETA, this.getPort());
		GridBagConstraints gbc_movepanel3 = new GridBagConstraints();
		gbc_movepanel3.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel3.fill = GridBagConstraints.BOTH;
		gbc_movepanel3.gridx = 2;
		gbc_movepanel3.gridy = 0;
		JPanel movePanel3 = new JPanel();
		movePanel3.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel3.add(panel2Theta);
		this.getContentPane().add(movePanel3, gbc_movepanel3);

		textArea = new JTextArea();
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textArea.setEditable(false);
		textArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		textArea.setText(Messages.PHASE1);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 4;
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(10, 5, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		getContentPane().add(textArea, gbc_textArea);

		phase1Button = new JButton("OPTIC ALIGNEMENT OF THE MONOCROMATOR");
		phase1Button.setEnabled(false);
		phase1Button.setActionCommand(ActionCommands.PHASE1);
		phase1Button.addActionListener(this);
		GridBagConstraints gbc_phase1Button = new GridBagConstraints();
		gbc_phase1Button.insets = new Insets(10, 0, 5, 5);
		gbc_phase1Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase1Button.gridx = 2;
		gbc_phase1Button.gridy = 1;
		getContentPane().add(phase1Button, gbc_phase1Button);

		phase2Button = new JButton("OPTIC ALIGNEMENT OF THE X-RAY TUBE");
		phase2Button.setActionCommand(ActionCommands.PHASE2);
		phase2Button.addActionListener(this);
		GridBagConstraints gbc_phase2Button = new GridBagConstraints();
		gbc_phase2Button.insets = new Insets(0, 0, 5, 5);
		gbc_phase2Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase2Button.gridx = 2;
		gbc_phase2Button.gridy = 2;
		getContentPane().add(phase2Button, gbc_phase2Button);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emergencyDumpPanel.gridx = 2;
		gbc_emergencyDumpPanel.gridy = 3;
		getContentPane().add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JPanel emptyPanel = new JPanel();
		GridBagConstraints gbc_emptyPanel = new GridBagConstraints();
		gbc_emptyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emptyPanel.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel.gridx = 2;
		gbc_emptyPanel.gridy = 4;
		getContentPane().add(emptyPanel, gbc_emptyPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.insets = new Insets(0, 0, 5, 5);
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.gridx = 2;
		gbc_exitButton.gridy = 5;
		getContentPane().add(exitButton, gbc_exitButton);

	}

	public void manageOtherActions(ActionEvent event)
	{
		if (event.getActionCommand().equals(ActionCommands.PHASE1))
		{
			panelThetaFirst.setEnabled(true);
			panel2ThetaFirst.setEnabled(false);
			panel2Theta.setEnabled(true);

			phase1Button.setEnabled(false);
			phase2Button.setEnabled(true);

			textArea.setText(Messages.PHASE1);
		}
		else if (event.getActionCommand().equals(ActionCommands.PHASE2))
		{
			panelThetaFirst.setEnabled(true);
			panel2ThetaFirst.setEnabled(true);
			panel2Theta.setEnabled(false);

			phase1Button.setEnabled(true);
			phase2Button.setEnabled(false);

			textArea.setText(Messages.PHASE2);
		}
	}

}
