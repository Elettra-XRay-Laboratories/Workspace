package com.elettra.lab.optic.diffractometer.windows;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.panels.DoubleMovePanel;
import com.elettra.controller.gui.panels.EmergencyDumpPanel;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;
import com.elettra.lab.optic.diffractometer.Axis;
import com.elettra.lab.optic.diffractometer.panels.SaveBraggAnglesPanel;
import com.elettra.lab.optic.diffractometer.panels.XRayBeamCenteringPanel;

public class AlignementOfTheMonocromatorWithXBeamWindow extends AbstractGenericFrame
{
	private static final long serialVersionUID = 2150045820630450666L;

	static class ActionCommands
	{
		private static final String PHASE1 = "PHASE1";
		private static final String PHASE2 = "PHASE2";
		private static final String PHASE3 = "PHASE3";
	}

	static class Messages
	{
		public static final String PHASE1 = "  1 - Aprire le fenditure sul detector.\n\n  2 - Posizionare il monocromatore e tubo sull'angolo di Bragg nominale (movimentazione rigida assi THETA'/2THETA')\n      ed eseguire una scansione in 2THETA, alla ricerca del segnale della diffrazione.\n\n  3 - In caso di segnale assente o scarsamente intenso, muovere il detector di una posizione inferiore all'apertura della finestra delle fenditure (avanti o indietro),\n      ed eseguire una scansione in THETA', attorno all'angolo di Bragg nominale, ripetendo la scansione ad ogni movimento del detector, fino ad ottenere la presenza di un picco";
		public static final String PHASE2 = "  1 - Mantenere le fenditure aperte, lasciare fermi in posizione il detector (2THETA) e il monocromatore (THETA').\n      Eseguire una scansione in 2THETA', posizionando l'asse dove il segnale è massimo.\n\n  2 - Chiudere le fenditure (semichiuse), ed eseguire una scansione in 2THETA, posizionando il detector sul massimo.\n\n  3 - Ripetere i passi 1 e 2, ricordando di riaprire le fenditure (per evitare di perdere il segnale), ma con una scansione più fine.\n\n  4 - Trovato il massimo, richiedere la rotazione rigida del sistema tubo/monocromatore dell'angolo dell'angolo ricavabile dal 2THETA,\n      al fine di posizionare il massimo trovato sulla posizione 0 dell'asse 2THETA\n\n  5 - Ripetere la procedura con scansioni più fini per ottenere posizionamenti del picco di Bragg più prossimi allo zero.";
		public static final String PHASE3 = "  1 - Eseguire il salvataggio delle posizioni THETA' e 2THETA', come richiesto.";
	}

	private JButton                phase1Button;
	private JButton                phase2Button;
	private JButton                phase3Button;
	private JTextArea              textArea;
	private MovePanel              panelThetaFirst;
	private MovePanel              panel2ThetaFirst;
	private MovePanel              panel2Theta;
	private DoubleMovePanel        panelTheta2ThetaFirst;
	private JPanel                 phase2Panel;
	private JPanel                 phase3Panel;
	private JPanel                 phase1Panel;

	private JLayeredPane           rightLayeredPane;

	private XRayBeamCenteringPanel xRayBeamCenteringPanel;

	private SaveBraggAnglesPanel   saveBraggAnglesPanel;

	private ScanPanel              twothetaScanPanel1;
	private ScanPanel              thetafirstScanPanel;
	private ScanPanel              twothetaScanPanel2;
	private ScanPanel              twothetafirstScanPanel;

	public static synchronized AlignementOfTheMonocromatorWithXBeamWindow getInstance(ICommunicationPort port) throws HeadlessException, IOException
	{
		return new AlignementOfTheMonocromatorWithXBeamWindow(port);
	}

	private AlignementOfTheMonocromatorWithXBeamWindow(ICommunicationPort port) throws HeadlessException, IOException
	{
		super("Alignement of the Monocromator with the X-Beam", port);

		this.setBounds(5, 5, 3350, 1000);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
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
		gbl_leftPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_leftPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
		gbl_leftPanel.rowWeights = new double[] { 0.1, 0.0, 0.0, 0.0, 0.05, 0.75, 0.05 };
		leftPanel.setLayout(gbl_leftPanel);

		panelThetaFirst = new MovePanel(Axis.THETAFIRST, this.getPort());
		GridBagConstraints gbc_movepanel1 = new GridBagConstraints();
		gbc_movepanel1.insets = new Insets(10, 5, 0, 5);
		gbc_movepanel1.fill = GridBagConstraints.BOTH;
		gbc_movepanel1.gridx = 0;
		gbc_movepanel1.gridy = 0;
		JPanel movePanel1 = new JPanel();
		movePanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel1.add(panelThetaFirst);
		leftPanel.add(movePanel1, gbc_movepanel1);

		panel2ThetaFirst = new MovePanel(Axis.TWOTHETAFIRST, this.getPort());
		GridBagConstraints gbc_movepanel2 = new GridBagConstraints();
		gbc_movepanel2.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel2.fill = GridBagConstraints.BOTH;
		gbc_movepanel2.gridx = 1;
		gbc_movepanel2.gridy = 0;
		JPanel movePanel2 = new JPanel();
		movePanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel2.add(panel2ThetaFirst);
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

		panelTheta2ThetaFirst = new DoubleMovePanel(Axis.THETA2THETAFIRST, this.getPort());
		GridBagConstraints gbc_movepanel4 = new GridBagConstraints();
		gbc_movepanel4.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel4.fill = GridBagConstraints.BOTH;
		gbc_movepanel4.gridx = 3;
		gbc_movepanel4.gridy = 0;
		JPanel movePanel4 = new JPanel();
		movePanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		movePanel4.add(panelTheta2ThetaFirst);
		leftPanel.add(movePanel4, gbc_movepanel4);

		GridBagConstraints gbc_movepanel5 = new GridBagConstraints();
		gbc_movepanel5.insets = new Insets(10, 0, 0, 5);
		gbc_movepanel5.fill = GridBagConstraints.BOTH;
		gbc_movepanel5.gridx = 4;
		gbc_movepanel5.gridy = 0;
		JPanel movePanel5 = new JPanel();

		GridBagLayout gbl_panelUtility = new GridBagLayout();
		gbl_panelUtility.columnWidths = new int[] { 0 };
		gbl_panelUtility.rowHeights = new int[] { 0, 0 };
		gbl_panelUtility.columnWeights = new double[] { 1.0 };
		gbl_panelUtility.rowWeights = new double[] { 1.0, 1.0 };
		movePanel5.setLayout(gbl_panelUtility);

		leftPanel.add(movePanel5, gbc_movepanel5);

		JPanel panelUtility1 = new JPanel();
		panelUtility1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		xRayBeamCenteringPanel = new XRayBeamCenteringPanel(getPort());
		xRayBeamCenteringPanel.setEnabled(false);
		panelUtility1.add(xRayBeamCenteringPanel);
		GridBagConstraints gbc_correctAnglePanel = new GridBagConstraints();
		gbc_correctAnglePanel.anchor = GridBagConstraints.NORTH;
		gbc_correctAnglePanel.fill = GridBagConstraints.BOTH;
		gbc_correctAnglePanel.insets = new Insets(0, 0, 0, 5);
		gbc_correctAnglePanel.gridx = 0;
		gbc_correctAnglePanel.gridy = 0;
		movePanel5.add(panelUtility1, gbc_correctAnglePanel);

		JPanel panelUtility2 = new JPanel();
		panelUtility2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		saveBraggAnglesPanel = new SaveBraggAnglesPanel(getPort());
		saveBraggAnglesPanel.setEnabled(false);
		panelUtility2.add(saveBraggAnglesPanel);
		GridBagConstraints gbc_saveBraggAnglePanel = new GridBagConstraints();
		gbc_saveBraggAnglePanel.anchor = GridBagConstraints.NORTH;
		gbc_saveBraggAnglePanel.fill = GridBagConstraints.BOTH;
		gbc_saveBraggAnglePanel.insets = new Insets(5, 0, 0, 5);
		gbc_saveBraggAnglePanel.gridx = 0;
		gbc_saveBraggAnglePanel.gridy = 1;
		movePanel5.add(panelUtility2, gbc_saveBraggAnglePanel);

		textArea = new JTextArea();
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textArea.setEditable(false);
		textArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		textArea.setText(Messages.PHASE1);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridheight = 6;
		gbc_textArea.gridwidth = 4;
		gbc_textArea.insets = new Insets(10, 5, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 1;
		leftPanel.add(textArea, gbc_textArea);

		phase1Button = new JButton("LOOKUP OF THE SIGNAL");
		phase1Button.setEnabled(false);
		phase1Button.setActionCommand(ActionCommands.PHASE1);
		phase1Button.addActionListener(this);
		GridBagConstraints gbc_phase1Button = new GridBagConstraints();
		gbc_phase1Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase1Button.insets = new Insets(10, 0, 5, 0);
		gbc_phase1Button.gridx = 4;
		gbc_phase1Button.gridy = 1;
		leftPanel.add(phase1Button, gbc_phase1Button);

		phase2Button = new JButton("MAXIMIZATION OF THE SIGNAL");
		phase2Button.setActionCommand(ActionCommands.PHASE2);
		phase2Button.addActionListener(this);
		GridBagConstraints gbc_phase2Button = new GridBagConstraints();
		gbc_phase2Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase2Button.insets = new Insets(0, 0, 5, 0);
		gbc_phase2Button.gridx = 4;
		gbc_phase2Button.gridy = 2;
		leftPanel.add(phase2Button, gbc_phase2Button);

		phase3Button = new JButton("SAVING SETTINGS");
		phase3Button.setActionCommand(ActionCommands.PHASE3);
		phase3Button.addActionListener(this);
		GridBagConstraints gbc_phase3Button = new GridBagConstraints();
		gbc_phase3Button.fill = GridBagConstraints.HORIZONTAL;
		gbc_phase3Button.insets = new Insets(0, 0, 5, 0);
		gbc_phase3Button.gridx = 4;
		gbc_phase3Button.gridy = 3;
		leftPanel.add(phase3Button, gbc_phase3Button);

		GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
		gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
		gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emergencyDumpPanel.gridx = 4;
		gbc_emergencyDumpPanel.gridy = 4;
		leftPanel.add(new EmergencyDumpPanel(this.getPort()), gbc_emergencyDumpPanel);

		JPanel emptyPanel = new JPanel();
		GridBagConstraints gbc_emptyPanel = new GridBagConstraints();
		gbc_emptyPanel.fill = GridBagConstraints.BOTH;
		gbc_emptyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_emptyPanel.gridx = 4;
		gbc_emptyPanel.gridy = 5;
		leftPanel.add(emptyPanel, gbc_emptyPanel);

		JButton exitButton = new JButton("EXIT");
		exitButton.setActionCommand(CommonActionCommands.EXIT);
		exitButton.addActionListener(this);
		GridBagConstraints gbc_exitButton = new GridBagConstraints();
		gbc_exitButton.fill = GridBagConstraints.BOTH;
		gbc_exitButton.anchor = GridBagConstraints.CENTER;
		gbc_exitButton.insets = new Insets(0, 0, 5, 0);
		gbc_exitButton.gridx = 4;
		gbc_exitButton.gridy = 6;
		leftPanel.add(exitButton, gbc_exitButton);

		rightLayeredPane = new JLayeredPane();
		rightLayeredPane.setLayout(null);
		GridBagConstraints gbc_rightLayeredPane = new GridBagConstraints();
		gbc_rightLayeredPane.fill = GridBagConstraints.BOTH;
		gbc_rightLayeredPane.insets = new Insets(10, 50, 0, 10);
		gbc_rightLayeredPane.gridx = 1;
		gbc_rightLayeredPane.gridy = 0;
		getContentPane().add(rightLayeredPane, gbc_rightLayeredPane);

		Rectangle bounds = new Rectangle(0, 0, 1665, 862);

		phase1Panel = new JPanel();
		phase1Panel.setBounds(bounds);
		phase1Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightLayeredPane.setLayer(phase1Panel, 0);
		rightLayeredPane.add(phase1Panel);
		GridBagLayout gbl_phase1and3Panel = new GridBagLayout();
		gbl_phase1and3Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase1and3Panel.rowHeights = new int[] { 0 };
		gbl_phase1and3Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase1and3Panel.rowWeights = new double[] { 1.0 };
		phase1Panel.setLayout(gbl_phase1and3Panel);

		GridBagConstraints gbc_Scan2ThetaConstraints = new GridBagConstraints();
		gbc_Scan2ThetaConstraints.fill = GridBagConstraints.NONE;
		gbc_Scan2ThetaConstraints.anchor = GridBagConstraints.CENTER;
		gbc_Scan2ThetaConstraints.gridx = 0;
		gbc_Scan2ThetaConstraints.gridy = 0;
		twothetaScanPanel1 = new ScanPanel(Axis.TWOTHETA, this.getPort(), false);
		phase1Panel.add(twothetaScanPanel1, gbc_Scan2ThetaConstraints);

		GridBagConstraints gbc_ScanThetaConstraints = new GridBagConstraints();
		gbc_ScanThetaConstraints.fill = GridBagConstraints.NONE;
		gbc_ScanThetaConstraints.anchor = GridBagConstraints.WEST;
		gbc_ScanThetaConstraints.gridx = 1;
		gbc_ScanThetaConstraints.gridy = 0;
		thetafirstScanPanel = new ScanPanel(Axis.THETAFIRST, this.getPort(), false);
		phase1Panel.add(thetafirstScanPanel, gbc_ScanThetaConstraints);

		phase2Panel = new JPanel();
		phase2Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		phase2Panel.setBounds(bounds);
		rightLayeredPane.setLayer(phase2Panel, 0);
		rightLayeredPane.add(phase2Panel);
		GridBagLayout gbl_phase2Panel = new GridBagLayout();
		gbl_phase2Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase2Panel.rowHeights = new int[] { 0 };
		gbl_phase2Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase2Panel.rowWeights = new double[] { 1.0 };
		phase2Panel.setLayout(gbl_phase2Panel);

		GridBagConstraints gbc_Scan2ThetaConstraints2 = new GridBagConstraints();
		gbc_Scan2ThetaConstraints2.fill = GridBagConstraints.NONE;
		gbc_Scan2ThetaConstraints2.anchor = GridBagConstraints.CENTER;
		gbc_Scan2ThetaConstraints2.gridx = 0;
		gbc_Scan2ThetaConstraints2.gridy = 0;
		twothetaScanPanel2 = new ScanPanel(Axis.TWOTHETA, this.getPort(), false);
		phase2Panel.add(twothetaScanPanel2, gbc_Scan2ThetaConstraints2);

		GridBagConstraints gbc_Scan2ThetaFirstConstraints = new GridBagConstraints();
		gbc_Scan2ThetaFirstConstraints.fill = GridBagConstraints.NONE;
		gbc_Scan2ThetaFirstConstraints.anchor = GridBagConstraints.WEST;
		gbc_Scan2ThetaFirstConstraints.gridx = 1;
		gbc_Scan2ThetaFirstConstraints.gridy = 0;
		twothetafirstScanPanel = new ScanPanel(Axis.TWOTHETAFIRST, this.getPort(), false);
		phase2Panel.add(twothetafirstScanPanel, gbc_Scan2ThetaFirstConstraints);

		phase3Panel = new JPanel();
		phase3Panel.setBounds(bounds);
		rightLayeredPane.setLayer(phase3Panel, 0);
		rightLayeredPane.add(phase3Panel);
		GridBagLayout gbl_phase4Panel = new GridBagLayout();
		gbl_phase4Panel.columnWidths = new int[] { 0, 0 };
		gbl_phase4Panel.rowHeights = new int[] { 0 };
		gbl_phase4Panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_phase4Panel.rowWeights = new double[] { 1.0 };
		phase3Panel.setLayout(gbl_phase4Panel);

		rightLayeredPane.moveToFront(phase1Panel);
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
	}

	private void manageEventPhase3()
	{
		rightLayeredPane.moveToFront(phase3Panel);

		this.panelThetaFirst.setEnabled(false);
		this.panel2ThetaFirst.setEnabled(false);
		this.panel2Theta.setEnabled(false);
		this.panelTheta2ThetaFirst.setEnabled(false);
		this.xRayBeamCenteringPanel.setEnabled(false);
		this.saveBraggAnglesPanel.setEnabled(true);

		this.phase1Button.setEnabled(true);
		this.phase2Button.setEnabled(true);
		this.phase3Button.setEnabled(false);

		this.textArea.setText(Messages.PHASE3);
	}

	private void manageEventPhase2()
	{
		rightLayeredPane.moveToFront(phase2Panel);

		this.twothetaScanPanel1.resetPanel();
		this.twothetaScanPanel2.resetPanel();
		this.twothetafirstScanPanel.resetPanel();

		this.panelThetaFirst.setEnabled(true);
		this.panel2ThetaFirst.setEnabled(true);
		this.panel2Theta.setEnabled(true);
		this.panelTheta2ThetaFirst.setEnabled(true);
		this.xRayBeamCenteringPanel.setEnabled(true);
		this.saveBraggAnglesPanel.setEnabled(false);

		this.phase1Button.setEnabled(true);
		this.phase2Button.setEnabled(false);
		this.phase3Button.setEnabled(true);

		this.textArea.setText(Messages.PHASE2);
	}

	private void manageEventPhase1()
	{
		rightLayeredPane.moveToFront(phase1Panel);

		this.twothetaScanPanel1.resetPanel();
		this.twothetaScanPanel2.resetPanel();
		this.thetafirstScanPanel.resetPanel();

		this.panelThetaFirst.setEnabled(true);
		this.panel2ThetaFirst.setEnabled(true);
		this.panel2Theta.setEnabled(true);
		this.panelTheta2ThetaFirst.setEnabled(true);
		this.xRayBeamCenteringPanel.setEnabled(false);
		this.saveBraggAnglesPanel.setEnabled(false);

		this.phase1Button.setEnabled(false);
		this.phase2Button.setEnabled(true);
		this.phase3Button.setEnabled(true);

		this.textArea.setText(Messages.PHASE1);
	}
}
