package com.elettra.lab.optic.diffractometer.windows;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;

public class AlignementOfTheSampleWithXBeamWindow extends AbstractGenericFrame
{
	private static final long serialVersionUID = 2150045820630450666L;

	static class ActionCommands
	{
		private static final String PHASE1 = "PHASE1";
		private static final String PHASE2 = "PHASE2";
		private static final String PHASE3 = "PHASE3";
		private static final String PHASE4 = "PHASE4";
	}

	static class Messages
	{
		public static final String PHASE1 = "  1 - Chiudere le fenditure sul detector.\n\n  2 - Posizionare gli assi THETA e 2THETA sulle posizioni +0.0000deg.\n\n  3 - Eseguire una scansione in Y, e posizionarsi sul punto di mezzo tra presenza di segnale e assenza di segnale\n\n  4 - Eseguire una scansione in THETA, e posizionarsi sul picco.\n\n  5 - Ripetere i passi 3 e 4 fino a quando l'intensità in Y e in THETA coincidono.";
		public static final String PHASE2 = "  1 - Mantenere le fenditure aperte e lasciare fermo in posizione il detector (2THETA).\n\n  2 - Eseguire una scansioni in Z, al variare dell'angolo W e posizionarsi sul W per il quale la scansione risulta un valore piatto.";
		public static final String PHASE3 = "  1 - Mantenere le fenditure aperte e lasciare fermo in posizione il detector (2THETA).\n\n  2 - Eseguire una scansione in Y, e posizionarsi sul punto di mezzo tra presenza di segnale e assenza di segnale\n\n  3 - Eseguire una scansione in THETA, e posizionarsi sul picco.";
		public static final String PHASE4 = "  1 - Posizionare il campione nelle condizioni di Bragg nominali (assi THETA e 2THETA).\n\n  2 - Avvicinare al massimo il detector verso il campione ed eseguire una scansione fine in THETA, cercando il picco anche con l'oscilloscopio.\n\n  3 - Trovato o visualizzato il picco, eseguire una scansione in THETA più fine, eventualmente iterando piu' volte.\n\n  4 - Chiudere le fenditure, riportare il detector in posizione ed eseguire una scansione in 2THETA fine, eventualmente iterando più volte.\n\n\n  N.B.: L'errore di taglio è il valore dato dalla differenza tra Angolo di Bragg nominale e quello trovato al termine del passo 4.";
	}

	private JButton      phase1Button;
	private JButton      phase2Button;
	private JButton      phase3Button;
	private JButton      phase4Button;
	private JTextArea    textArea;
	private MovePanel    panelY;
	private MovePanel    panelTheta;
	private MovePanel    panel2Theta;
	private MovePanel    panelZ;
	private MovePanel    panelW;
	private JPanel       phase2Panel;
	private JPanel       phase4Panel;
	private JPanel       phase1and3Panel;

	private JLayeredPane rightLayeredPane;
	private ScanPanel    yScanPanel;
	private ScanPanel    thetaScanPanel1;
	private ScanPanel    thetaScanPanel2;
	private ScanPanel    twothetaScanPanel;
	private ScanPanel    zScanPanel;

	public static synchronized AlignementOfTheSampleWithXBeamWindow getInstance(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		return new AlignementOfTheSampleWithXBeamWindow(port);
	}

	private AlignementOfTheSampleWithXBeamWindow(ICommunicationPort port) throws HeadlessException, CommunicationPortException
	{
		super("Alignement of the Sample with the X-Beam", port);

		this.setBounds(5, 5, 3350, 1000);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 0.2, 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		getContentPane().setLayout(gridBagLayout);

		JPanel leftPanel = new JPanel();
		GridBagConstraints gbc_leftPanel = new GridBagConstraints();
		gbc_leftPanel.insets = new Insets(0, 0, 0, 5);
		gbc_leftPanel.fill = GridBagConstraints.BOTH;
		gbc_leftPanel.gridx = 0;
		gbc_leftPanel.gridy = 0;
		getContentPane().add(leftPanel, gbc_leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_leftPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
		gbl_leftPanel.rowWeights = new double[] { 0.1, 0.0, 0.0, 0.0, 0.0, 0.05, 0.7, 0.05 };
		leftPanel.setLayout(gbl_leftPanel);

		panelY = new MovePanel(Axis.Y, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 0;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panelY);
		leftPanel.add(movePanel1, gbc_movepanel1);

		panelTheta = new MovePanel(Axis.THETA, this.getPort());
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 1;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panelTheta);
		leftPanel.add(movePanel2, gbc_movepanel2);

		panel2Theta = new MovePanel(Axis.TWOTHETA, this.getPort());
		GridBagConstraints gbc_movepanel3 = new GridBagConstraints();
		gbc_movepanel3.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel3.fill = GridBagConstraints.BOTH;
		gbc_movepanel3.gridx = 2;
		gbc_movepanel3.gridy = 0;
		JPanel movePanel3 = new JPanel();
		movePanel3.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel3.add(panel2Theta);
		leftPanel.add(movePanel3, gbc_movepanel3);

		panelZ = new MovePanel(Axis.Z, this.getPort());
		panelZ.setEnabled(false);
		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 3;
		gbc_movepanel4.gridy = 0;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel4.add(panelZ);
		leftPanel.add(movePanel4, gbc_movepanel4);

		panelW = new MovePanel(Axis.W, this.getPort());
		panelW.setEnabled(false);
		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 4;
		gbc_movepanel5.gridy = 0;
		JPanel movePanel5 = new JPanel();
		movePanel5.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel5.add(panelW);
		leftPanel.add(movePanel5, gbc_movepanel5);

		textArea = new JTextArea();
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textArea.setEditable(false);
		textArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		textArea.setText(Messages.PHASE1);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 7;
		gbc_textArea.gridwidth = 4;
		gbc_textArea.insets = new Insets(10, 5, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		leftPanel.add(textArea, gbc_textArea);

		phase1Button = new JButton("Y-THETA ALIGNEMENT");
		phase1Button.setEnabled(false);
		phase1Button.setActionCommand(ActionCommands.PHASE1);
		phase1Button.addActionListener(this);
		GridBagConstraints gbc_phase1Button = new GridBagConstraints();
		gbc_phase1Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase1Button.insets = new Insets(10, 0, 5, 0);
		gbc_phase1Button.gridx = 4;
		gbc_phase1Button.gridy = 1;
		leftPanel.add(phase1Button, gbc_phase1Button);

		phase2Button = new JButton("Z-W ALIGNEMENT");
		phase2Button.setActionCommand(ActionCommands.PHASE2);
		phase2Button.addActionListener(this);
		GridBagConstraints gbc_phase2Button = new GridBagConstraints();
		gbc_phase2Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase2Button.insets = new Insets(0, 0, 5, 0);
		gbc_phase2Button.gridx = 4;
		gbc_phase2Button.gridy = 2;
		leftPanel.add(phase2Button, gbc_phase2Button);

		phase3Button = new JButton("Y-THETA RE-ALIGNEMENT");
		phase3Button.setActionCommand(ActionCommands.PHASE3);
		phase3Button.addActionListener(this);
		GridBagConstraints gbc_phase3Button = new GridBagConstraints();
		gbc_phase3Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase3Button.insets = new Insets(0, 0, 5, 0);
		gbc_phase3Button.gridx = 4;
		gbc_phase3Button.gridy = 3;
		leftPanel.add(phase3Button, gbc_phase3Button);

		phase4Button = new JButton("BRAGG ANGLE RESEARCH");
		phase4Button.setActionCommand(ActionCommands.PHASE4);
		phase4Button.addActionListener(this);
		GridBagConstraints gbc_phase4Button = new GridBagConstraints();
		gbc_phase4Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase4Button.insets = new Insets(0, 0, 5, 0);
		gbc_phase4Button.gridx = 4;
		gbc_phase4Button.gridy = 4;
		leftPanel.add(phase4Button, gbc_phase4Button);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emergencyDumpPanel.gridx = 4;
		gbc_emergencyDumpPanel.gridy = 5;
		leftPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JPanel emptyPanel = new JPanel();
		GridBagConstraints gbc_emptyPanel = new GridBagConstraints();
		gbc_emptyPanel.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emptyPanel.gridx = 4;
		gbc_emptyPanel.gridy = 6;
		leftPanel.add(emptyPanel, gbc_emptyPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.anchor = GridBagConstraints.CENTER;
		gbc_exitButton.insets = new Insets(0, 0, 5, 0);
		gbc_exitButton.gridx = 4;
		gbc_exitButton.gridy = 7;
		leftPanel.add(exitButton, gbc_exitButton);

		rightLayeredPane = new JLayeredPane();
		rightLayeredPane.setLayout(null);
		GridBagConstraints gbc_rightLayeredPane = new GridBagConstraints();
		gbc_rightLayeredPane.fill = GridBagConstraints.BOTH;
		gbc_rightLayeredPane.insets = new Insets(10, 50, 5, 10);
		gbc_rightLayeredPane.gridx = 1;
		gbc_rightLayeredPane.gridy = 0;
		getContentPane().add(rightLayeredPane, gbc_rightLayeredPane);

		Rectangle bounds = new Rectangle(0, 0, 1665, 862);

		phase1and3Panel = new JPanel();
		phase1and3Panel.setBounds(bounds);
		rightLayeredPane.setLayer(phase1and3Panel, 0);
		phase1and3Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightLayeredPane.add(phase1and3Panel);
		GridBagLayout gbl_phase1and3Panel = new GridBagLayout();
		gbl_phase1and3Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase1and3Panel.rowHeights = new int[] { 0 };
		gbl_phase1and3Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase1and3Panel.rowWeights = new double[] { 1.0 };
		phase1and3Panel.setLayout(gbl_phase1and3Panel);

		GridBagConstraints gbc_ScanYConstraints = new GridBagConstraints();
		gbc_ScanYConstraints.fill = GridBagConstraints.NONE;
		gbc_ScanYConstraints.anchor = GridBagConstraints.CENTER;
		gbc_ScanYConstraints.gridx = 0;
		gbc_ScanYConstraints.gridy = 0;
		yScanPanel = new ScanPanel(Axis.Y, this.getPort(), false);
		phase1and3Panel.add(yScanPanel, gbc_ScanYConstraints);

		GridBagConstraints gbc_ScanThetaConstraints = new GridBagConstraints();
		gbc_ScanThetaConstraints.fill = GridBagConstraints.NONE;
		gbc_ScanThetaConstraints.anchor = GridBagConstraints.WEST;
		gbc_ScanThetaConstraints.gridx = 1;
		gbc_ScanThetaConstraints.gridy = 0;
		thetaScanPanel1 = new ScanPanel(Axis.THETA, this.getPort(), false);
		phase1and3Panel.add(thetaScanPanel1, gbc_ScanThetaConstraints);

		phase2Panel = new JPanel();
		phase2Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightLayeredPane.setLayer(phase2Panel, 0);
		phase2Panel.setBounds(bounds);
		rightLayeredPane.add(phase2Panel);
		GridBagLayout gbl_phase2Panel = new GridBagLayout();
		gbl_phase2Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase2Panel.rowHeights = new int[] { 0 };
		gbl_phase2Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase2Panel.rowWeights = new double[] { 1.0 };
		phase2Panel.setLayout(gbl_phase2Panel);

		GridBagConstraints gbc_ScanZConstraints = new GridBagConstraints();
		gbc_ScanZConstraints.fill = GridBagConstraints.NONE;
		gbc_ScanZConstraints.anchor = GridBagConstraints.WEST;
		gbc_ScanZConstraints.insets = new Insets(0, 50, 0, 0);
		gbc_ScanZConstraints.gridx = 0;
		gbc_ScanZConstraints.gridy = 0;
		zScanPanel = new ScanPanel(Axis.Z, this.getPort(), false);
		phase2Panel.add(zScanPanel, gbc_ScanZConstraints);

		GridBagConstraints gbc_EmptyConstraints = new GridBagConstraints();
		gbc_EmptyConstraints.fill = GridBagConstraints.NONE;
		gbc_EmptyConstraints.anchor = GridBagConstraints.WEST;
		gbc_EmptyConstraints.gridx = 1;
		gbc_EmptyConstraints.gridy = 0;
		phase2Panel.add(new JPanel(), gbc_EmptyConstraints);

		phase4Panel = new JPanel();
		phase4Panel.setBounds(bounds);
		phase4Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightLayeredPane.setLayer(phase4Panel, 0);
		rightLayeredPane.add(phase4Panel);
		GridBagLayout gbl_phase4Panel = new GridBagLayout();
		gbl_phase4Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase4Panel.rowHeights = new int[] { 0 };
		gbl_phase4Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase4Panel.rowWeights = new double[] { 1.0 };
		phase4Panel.setLayout(gbl_phase4Panel);

		GridBagConstraints gbc_Scan2ThetaConstraints = new GridBagConstraints();
		gbc_Scan2ThetaConstraints.fill = GridBagConstraints.NONE;
		gbc_Scan2ThetaConstraints.anchor = GridBagConstraints.CENTER;
		gbc_Scan2ThetaConstraints.gridx = 0;
		gbc_Scan2ThetaConstraints.gridy = 0;
		twothetaScanPanel = new ScanPanel(Axis.TWOTHETA, this.getPort(), false);
		phase4Panel.add(twothetaScanPanel, gbc_Scan2ThetaConstraints);

		GridBagConstraints gbc_ScanThetaConstraints2 = new GridBagConstraints();
		gbc_ScanThetaConstraints2.fill = GridBagConstraints.NONE;
		gbc_ScanThetaConstraints2.anchor = GridBagConstraints.WEST;
		gbc_ScanThetaConstraints2.gridx = 1;
		gbc_ScanThetaConstraints2.gridy = 0;
		thetaScanPanel2 = new ScanPanel(Axis.THETA, this.getPort(), false);
		phase4Panel.add(thetaScanPanel2, gbc_ScanThetaConstraints2);

		rightLayeredPane.moveToFront(phase1and3Panel);
	}

	public void manageOtherActions(ActionEvent event)
	{
		if (event.getActionCommand().equals(ActionCommands.PHASE1))
		{
			this.manageEventPhase1();
		}
		else if (event.getActionCommand().equals(ActionCommands.PHASE2))
		{
			this.manageEventPhase2();
		}
		else if (event.getActionCommand().equals(ActionCommands.PHASE3))
		{
			this.manageEventPhase3();
		}
		else if (event.getActionCommand().equals(ActionCommands.PHASE4))
		{
			this.manageEventPhase4();
		}
	}

	private void manageEventPhase4()
	{
		rightLayeredPane.moveToFront(phase4Panel);

		this.twothetaScanPanel.resetPanel();
		this.thetaScanPanel2.resetPanel();
		this.thetaScanPanel1.resetPanel();

		this.panelY.setEnabled(false);
		this.panelTheta.setEnabled(true);
		this.panel2Theta.setEnabled(true);
		this.panelZ.setEnabled(false);
		this.panelW.setEnabled(false);

		this.phase1Button.setEnabled(true);
		this.phase2Button.setEnabled(true);
		this.phase3Button.setEnabled(true);
		this.phase4Button.setEnabled(false);

		this.textArea.setText(Messages.PHASE4);
	}

	private void manageEventPhase3()
	{
		rightLayeredPane.moveToFront(phase1and3Panel);

		this.yScanPanel.resetPanel();
		this.thetaScanPanel2.resetPanel();
		this.thetaScanPanel1.resetPanel();

		this.panelY.setEnabled(true);
		this.panelTheta.setEnabled(true);
		this.panel2Theta.setEnabled(true);
		this.panelZ.setEnabled(false);
		this.panelW.setEnabled(false);

		this.phase1Button.setEnabled(true);
		this.phase2Button.setEnabled(true);
		this.phase3Button.setEnabled(false);
		this.phase4Button.setEnabled(true);

		this.textArea.setText(Messages.PHASE3);
	}

	private void manageEventPhase2()
	{
		rightLayeredPane.moveToFront(phase2Panel);

		this.zScanPanel.resetPanel();

		this.panelY.setEnabled(false);
		this.panelTheta.setEnabled(false);
		this.panel2Theta.setEnabled(false);
		this.panelZ.setEnabled(true);
		this.panelW.setEnabled(true);

		this.phase1Button.setEnabled(true);
		this.phase2Button.setEnabled(false);
		this.phase3Button.setEnabled(true);
		this.phase4Button.setEnabled(true);

		this.textArea.setText(Messages.PHASE2);
	}

	private void manageEventPhase1()
	{
		rightLayeredPane.moveToFront(phase1and3Panel);

		this.yScanPanel.resetPanel();
		this.thetaScanPanel2.resetPanel();
		this.thetaScanPanel1.resetPanel();

		this.panelY.setEnabled(true);
		this.panelTheta.setEnabled(true);
		this.panel2Theta.setEnabled(true);
		this.panelZ.setEnabled(false);
		this.panelW.setEnabled(false);

		this.phase1Button.setEnabled(false);
		this.phase2Button.setEnabled(true);
		this.phase3Button.setEnabled(true);
		this.phase4Button.setEnabled(true);

		this.textArea.setText(Messages.PHASE1);
	}
}
