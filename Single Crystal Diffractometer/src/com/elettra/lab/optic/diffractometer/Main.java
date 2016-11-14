package com.elettra.lab.optic.diffractometer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.EthernetPortParameters;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.KindOfPort;
import com.elettra.common.io.SerialPortParameters;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.IAxisConfigurationMap;
import com.elettra.controller.driver.common.MultipleAxis;
import com.elettra.controller.driver.programs.DefaultAxisConfigurationMap;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.windows.AbstractCommunicationPortFrame;
import com.elettra.controller.gui.windows.ControllerCrashRecoveryWindow;
import com.elettra.lab.optic.diffractometer.windows.AlignementOfTheMonocromatorWithXBeamWindow;
import com.elettra.lab.optic.diffractometer.windows.AlignementOfTheSampleWithXBeamWindow;
import com.elettra.lab.optic.diffractometer.windows.ChangeTubeMonochromatorWindow;
import com.elettra.lab.optic.diffractometer.windows.FreeMovementsAndScansWindow;
import com.elettra.lab.optic.diffractometer.windows.OpticPreAlignementWindow;
import com.elettra.lab.optic.diffractometer.windows.RockingCurveMeasureWindow;
import com.elettra.lab.optic.diffractometer.windows.Theta2ThetaScanWindow;
import com.elettra.lab.optic.diffractometer.windows.TubeStabilityScanWindow;
import com.elettra.lab.optic.diffractometer.Axis;

public class Main extends AbstractCommunicationPortFrame implements ActionListener
{
	private static final String APPLICATION_NAME = "Single Crystal Diffractometer";

	static class ActionCommands
	{
		private static final String EXIT                                          = "EXIT";
		private static final String OPTIC_PRE_ALIGNEMENT                          = "OPTIC_PRE_ALIGNEMENT";
		private static final String ALIGNEMENT_OF_THE_MONOCROMATOR_WITH_THE_XBEAM = "ALIGNEMENT_OF_THE_MONOCROMATOR_WITH_THE_XBEAM";
		private static final String ALIGNEMENT_OF_THE_SAMPLE_WITH_THE_XBEAM       = "ALIGNEMENT_OF_THE_SAMPLE_WITH_THE_XBEAM";
		private static final String FREE_MOVEMENTS_AND_SCANS                      = "FREE_MOVEMENTS_AND_SCANS";
		private static final String ROCKING_CURVE_TOPOGRAPHY                      = "ROCKING_CURVE_TOPOGRAPHY";
		private static final String THETA_2THETA_SCAN                             = "THETA_2THETA_SCAN";
		private static final String TUBE_STABILITY_SCAN                           = "TUBE_STABILITY_SCAN";
		private static final String CHANGE_TUBE_MONOCHROMATOR                     = "CHANGE_TUBE_MONOCHROMATOR";
		private static final String CONTROLLER_CRASH_RECOVERY                     = "CONTROLLER_CRASH_RECOVERY";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6750631794437120752L;
	private JTextField        softwareVersionTextField;
	private JTextField        ioStatusTextField;

	public Main(ICommunicationPort port) throws HeadlessException
	{
		super(APPLICATION_NAME, port);

		try
		{
			this.setIconImage(Toolkit.getDefaultToolkit().getImage("singlecrystaldiffractometer.ico"));
			this.addWindowFocusListener(new MainWindowAdapter(this));
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.setBounds(50, 50, 450, 760);

			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 0, 0 };
			gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
			gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gridBagLayout.rowWeights = new double[] { 0.0, 0.4, 0.3, 0.6, 1.0, Double.MIN_VALUE };
			getContentPane().setLayout(gridBagLayout);

			JPanel headPanel = new JPanel();
			GridBagConstraints gbc_headPanel = new GridBagConstraints();
			gbc_headPanel.insets = new Insets(0, 5, 5, 0);
			gbc_headPanel.gridx = 0;
			gbc_headPanel.gridy = 0;
			getContentPane().add(headPanel, gbc_headPanel);
			GridBagLayout gbl_headPanel = new GridBagLayout();
			gbl_headPanel.columnWidths = new int[] { 683, 0 };
			gbl_headPanel.rowHeights = new int[] { 23, 0, 0, 0 };
			gbl_headPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_headPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			headPanel.setLayout(gbl_headPanel);

			JLabel lblNewLabel = new JLabel("HARD X-RAY OPTIC LAB");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 19));
			lblNewLabel.setForeground(new Color(0, 102, 51));
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
			gbc_lblNewLabel.insets = new Insets(0, 5, 5, 0);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			headPanel.add(lblNewLabel, gbc_lblNewLabel);

			JLabel lblNewLabel_2 = new JLabel("SINGLE CRYSTAL DIFFRACTOMETER");
			lblNewLabel_2.setForeground(new Color(0, 102, 51));
			lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 19));
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.insets = new Insets(0, 5, 5, 0);
			gbc_lblNewLabel_2.gridx = 0;
			gbc_lblNewLabel_2.gridy = 1;
			headPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

			JLabel lblNewLabel_1 = new JLabel("MANAGER");
			lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 19));
			lblNewLabel_1.setForeground(new Color(0, 102, 51));
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.gridx = 0;
			gbc_lblNewLabel_1.gridy = 2;
			headPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);

			JTabbedPane alignementOperationsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_alignementOperationsTabbedPane = new GridBagConstraints();
			gbc_alignementOperationsTabbedPane.insets = new Insets(0, 5, 5, 0);
			gbc_alignementOperationsTabbedPane.fill = GridBagConstraints.BOTH;
			gbc_alignementOperationsTabbedPane.gridx = 0;
			gbc_alignementOperationsTabbedPane.gridy = 1;
			getContentPane().add(alignementOperationsTabbedPane, gbc_alignementOperationsTabbedPane);

			JPanel alignementOperationsPanel = new JPanel();
			alignementOperationsTabbedPane.addTab("Alignement Operations", null, alignementOperationsPanel, null);
			GridBagLayout gbl_alignementOperationsPanel = new GridBagLayout();
			gbl_alignementOperationsPanel.columnWidths = new int[] { 0, 0 };
			gbl_alignementOperationsPanel.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_alignementOperationsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_alignementOperationsPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
			alignementOperationsPanel.setLayout(gbl_alignementOperationsPanel);

			JButton opticPreAlignementButton = new JButton("OPTIC PRE-ALIGNEMENT");
			opticPreAlignementButton.addActionListener(this);
			opticPreAlignementButton.setActionCommand(ActionCommands.OPTIC_PRE_ALIGNEMENT);
			opticPreAlignementButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_opticPreAlignementButton = new GridBagConstraints();
			gbc_opticPreAlignementButton.fill = GridBagConstraints.BOTH;
			gbc_opticPreAlignementButton.insets = new Insets(10, 5, 5, 5);
			gbc_opticPreAlignementButton.gridx = 0;
			gbc_opticPreAlignementButton.gridy = 0;
			alignementOperationsPanel.add(opticPreAlignementButton, gbc_opticPreAlignementButton);

			JButton monocromatorAlignementButton = new JButton("ALIGNEMENT OF THE MONOCROMATOR WITH THE X-BEAM");
			monocromatorAlignementButton.addActionListener(this);
			monocromatorAlignementButton.setActionCommand(ActionCommands.ALIGNEMENT_OF_THE_MONOCROMATOR_WITH_THE_XBEAM);
			monocromatorAlignementButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_monocromatorAlignementButton = new GridBagConstraints();
			gbc_monocromatorAlignementButton.fill = GridBagConstraints.BOTH;
			gbc_monocromatorAlignementButton.insets = new Insets(0, 5, 5, 5);
			gbc_monocromatorAlignementButton.gridx = 0;
			gbc_monocromatorAlignementButton.gridy = 1;
			alignementOperationsPanel.add(monocromatorAlignementButton, gbc_monocromatorAlignementButton);

			JButton sampleAlignementButton = new JButton("ALIGNEMENT OF THE SAMPLE WITH THE X-BEAM");
			sampleAlignementButton.addActionListener(this);
			sampleAlignementButton.setActionCommand(ActionCommands.ALIGNEMENT_OF_THE_SAMPLE_WITH_THE_XBEAM);
			sampleAlignementButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_sampleAlignementButton = new GridBagConstraints();
			gbc_sampleAlignementButton.insets = new Insets(0, 5, 5, 5);
			gbc_sampleAlignementButton.fill = GridBagConstraints.BOTH;
			gbc_sampleAlignementButton.gridx = 0;
			gbc_sampleAlignementButton.gridy = 2;
			alignementOperationsPanel.add(sampleAlignementButton, gbc_sampleAlignementButton);
			alignementOperationsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

			JTabbedPane measureOperationsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_measureOperationsTabbedPane = new GridBagConstraints();
			gbc_measureOperationsTabbedPane.insets = new Insets(0, 5, 5, 0);
			gbc_measureOperationsTabbedPane.fill = GridBagConstraints.BOTH;
			gbc_measureOperationsTabbedPane.gridx = 0;
			gbc_measureOperationsTabbedPane.gridy = 2;
			getContentPane().add(measureOperationsTabbedPane, gbc_measureOperationsTabbedPane);

			JPanel measureOperationPanel = new JPanel();
			measureOperationsTabbedPane.addTab("Measure Operations", null, measureOperationPanel, null);
			GridBagLayout gbl_measureOperationPanel = new GridBagLayout();
			gbl_measureOperationPanel.columnWidths = new int[] { 0, 0 };
			gbl_measureOperationPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_measureOperationPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_measureOperationPanel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
			measureOperationPanel.setLayout(gbl_measureOperationPanel);

			JButton rockingCurveTopographyButton = new JButton("ROCKING CURVE TOPOGRAPHY");
			rockingCurveTopographyButton.addActionListener(this);
			rockingCurveTopographyButton.setActionCommand(ActionCommands.ROCKING_CURVE_TOPOGRAPHY);
			rockingCurveTopographyButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_rockingCurveTopografyButton = new GridBagConstraints();
			gbc_rockingCurveTopografyButton.fill = GridBagConstraints.BOTH;
			gbc_rockingCurveTopografyButton.insets = new Insets(10, 5, 5, 5);
			gbc_rockingCurveTopografyButton.gridx = 0;
			gbc_rockingCurveTopografyButton.gridy = 0;
			measureOperationPanel.add(rockingCurveTopographyButton, gbc_rockingCurveTopografyButton);

			JButton theta2ThetaScanButton = new JButton("THETA - 2THETA SCAN");
			theta2ThetaScanButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			theta2ThetaScanButton.addActionListener(this);
			theta2ThetaScanButton.setActionCommand(ActionCommands.THETA_2THETA_SCAN);
			GridBagConstraints gbc_theta2ThetaScanButton = new GridBagConstraints();
			gbc_theta2ThetaScanButton.fill = GridBagConstraints.BOTH;
			gbc_theta2ThetaScanButton.insets = new Insets(0, 5, 5, 5);
			gbc_theta2ThetaScanButton.gridx = 0;
			gbc_theta2ThetaScanButton.gridy = 1;
			measureOperationPanel.add(theta2ThetaScanButton, gbc_theta2ThetaScanButton);
			measureOperationsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

			JTabbedPane supportOperationsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_supportOperationsTabbedPane = new GridBagConstraints();
			gbc_supportOperationsTabbedPane.insets = new Insets(0, 5, 5, 0);
			gbc_supportOperationsTabbedPane.fill = GridBagConstraints.BOTH;
			gbc_supportOperationsTabbedPane.gridx = 0;
			gbc_supportOperationsTabbedPane.gridy = 3;
			getContentPane().add(supportOperationsTabbedPane, gbc_supportOperationsTabbedPane);

			JPanel supportOperationsPanel = new JPanel();
			supportOperationsTabbedPane.addTab("Support Operations", null, supportOperationsPanel, null);
			GridBagLayout gbl_supportOperationsPanel = new GridBagLayout();
			gbl_supportOperationsPanel.columnWidths = new int[] { 0, 0 };
			gbl_supportOperationsPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_supportOperationsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_supportOperationsPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
			supportOperationsPanel.setLayout(gbl_supportOperationsPanel);

			JButton freeMovementsButton = new JButton("FREE MOVEMENTS AND SCANS OF ALL THE AXIS");
			freeMovementsButton.addActionListener(this);
			freeMovementsButton.setActionCommand(ActionCommands.FREE_MOVEMENTS_AND_SCANS);
			freeMovementsButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_freeMovementsButton = new GridBagConstraints();
			gbc_freeMovementsButton.fill = GridBagConstraints.BOTH;
			gbc_freeMovementsButton.insets = new Insets(10, 5, 5, 5);
			gbc_freeMovementsButton.gridx = 0;
			gbc_freeMovementsButton.gridy = 0;
			supportOperationsPanel.add(freeMovementsButton, gbc_freeMovementsButton);

			JButton replacementTubeMonocromatorButton = new JButton("REPLACEMENT OF THE TUBE/MONOCROMATOR");
			replacementTubeMonocromatorButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			replacementTubeMonocromatorButton.addActionListener(this);
			replacementTubeMonocromatorButton.setActionCommand(ActionCommands.CHANGE_TUBE_MONOCHROMATOR);
			GridBagConstraints gbc_replacementTubeMonocromatorButton = new GridBagConstraints();
			gbc_replacementTubeMonocromatorButton.fill = GridBagConstraints.BOTH;
			gbc_replacementTubeMonocromatorButton.insets = new Insets(0, 5, 5, 5);
			gbc_replacementTubeMonocromatorButton.gridx = 0;
			gbc_replacementTubeMonocromatorButton.gridy = 1;
			supportOperationsPanel.add(replacementTubeMonocromatorButton, gbc_replacementTubeMonocromatorButton);

			JButton stabilityTestButton = new JButton("STABILITY TEST OF THE TUBE/DETECTOR");
			stabilityTestButton.addActionListener(this);
			stabilityTestButton.setActionCommand(ActionCommands.TUBE_STABILITY_SCAN);
			stabilityTestButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_stabilityTestButton = new GridBagConstraints();
			gbc_stabilityTestButton.fill = GridBagConstraints.BOTH;
			gbc_stabilityTestButton.insets = new Insets(0, 5, 5, 5);
			gbc_stabilityTestButton.gridx = 0;
			gbc_stabilityTestButton.gridy = 2;
			supportOperationsPanel.add(stabilityTestButton, gbc_stabilityTestButton);

			JButton recoveryCrashButton = new JButton("RECOVERY CRASH OF THE CONTROLLER");
			recoveryCrashButton.addActionListener(this);
			recoveryCrashButton.setActionCommand(ActionCommands.CONTROLLER_CRASH_RECOVERY);
			recoveryCrashButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_recoveryCrashButton = new GridBagConstraints();
			gbc_recoveryCrashButton.insets = new Insets(0, 5, 5, 5);
			gbc_recoveryCrashButton.fill = GridBagConstraints.BOTH;
			gbc_recoveryCrashButton.gridx = 0;
			gbc_recoveryCrashButton.gridy = 3;
			supportOperationsPanel.add(recoveryCrashButton, gbc_recoveryCrashButton);
			supportOperationsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

			JTabbedPane statusTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_statusTabbedPane = new GridBagConstraints();
			gbc_statusTabbedPane.insets = new Insets(0, 5, 5, 5);
			gbc_statusTabbedPane.fill = GridBagConstraints.BOTH;
			gbc_statusTabbedPane.gridx = 0;
			gbc_statusTabbedPane.gridy = 4;
			getContentPane().add(statusTabbedPane, gbc_statusTabbedPane);

			JPanel statusPanel = new JPanel();
			statusTabbedPane.addTab("Status", null, statusPanel, null);
			GridBagLayout gbl_statusPanel = new GridBagLayout();
			gbl_statusPanel.columnWidths = new int[] { 0, 0, 0, 0 };
			gbl_statusPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_statusPanel.columnWeights = new double[] { 0.0, 0.2, 0.8, Double.MIN_VALUE };
			gbl_statusPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			statusPanel.setLayout(gbl_statusPanel);
			statusTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

			JLabel lblSwVersion = new JLabel("SW Version");
			GridBagConstraints gbc_lblSwVersion = new GridBagConstraints();
			gbc_lblSwVersion.insets = new Insets(10, 5, 5, 5);
			gbc_lblSwVersion.anchor = GridBagConstraints.EAST;
			gbc_lblSwVersion.gridx = 0;
			gbc_lblSwVersion.gridy = 0;
			statusPanel.add(lblSwVersion, gbc_lblSwVersion);

			softwareVersionTextField = new JTextField();
			softwareVersionTextField.setText(this.getSWVersion());
			GridBagConstraints gbc_softwareVersionTextField = new GridBagConstraints();
			gbc_softwareVersionTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_softwareVersionTextField.insets = new Insets(10, 0, 5, 5);
			gbc_softwareVersionTextField.gridx = 1;
			gbc_softwareVersionTextField.gridy = 0;
			statusPanel.add(softwareVersionTextField, gbc_softwareVersionTextField);
			softwareVersionTextField.setColumns(10);

			JTextArea textArea = new JTextArea();
			textArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
			textArea.setEditable(false);
			textArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			GridBagConstraints gbc_textArea = new GridBagConstraints();
			gbc_textArea.insets = new Insets(10, 0, 10, 5);
			gbc_textArea.gridheight = 2;
			gbc_textArea.fill = GridBagConstraints.BOTH;
			gbc_textArea.gridx = 2;
			gbc_textArea.gridy = 0;
			statusPanel.add(textArea, gbc_textArea);

			JLabel lblNewLabel_3 = new JLabel("IO Status");
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.insets = new Insets(4, 0, 0, 5);
			gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTHEAST;
			gbc_lblNewLabel_3.gridx = 0;
			gbc_lblNewLabel_3.gridy = 1;
			statusPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);

			ioStatusTextField = new JTextField();
			ioStatusTextField.setText(this.getIOStatus());
			GridBagConstraints gbc_ioStatusTextField = new GridBagConstraints();
			gbc_ioStatusTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_ioStatusTextField.anchor = GridBagConstraints.NORTH;
			gbc_ioStatusTextField.insets = new Insets(0, 0, 0, 5);
			gbc_ioStatusTextField.gridx = 1;
			gbc_ioStatusTextField.gridy = 1;
			statusPanel.add(ioStatusTextField, gbc_ioStatusTextField);
			ioStatusTextField.setColumns(10);

			JButton exitButton = new JButton("EXIT");
			exitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
			exitButton.setForeground(Color.RED);
			exitButton.addActionListener(this);
			exitButton.setActionCommand(ActionCommands.EXIT);
			GridBagConstraints gbc_exitButton = new GridBagConstraints();
			gbc_exitButton.anchor = GridBagConstraints.EAST;
			gbc_exitButton.gridwidth = 3;
			gbc_exitButton.insets = new Insets(0, 0, 5, 5);
			gbc_exitButton.gridx = 0;
			gbc_exitButton.gridy = 2;
			statusPanel.add(exitButton, gbc_exitButton);
		}
		catch (Exception e)
		{
			GuiUtilities.showErrorPopup("Exception captured: " + e.getClass().getName() + " - " + e.getMessage(), (JPanel) this.getContentPane().getComponent(0));
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		try
		{
			String eventName = event.getActionCommand();

			if (eventName.equals(ActionCommands.EXIT))
				this.terminate();
			else if (eventName.equals(ActionCommands.OPTIC_PRE_ALIGNEMENT))
				OpticPreAlignementWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.ALIGNEMENT_OF_THE_MONOCROMATOR_WITH_THE_XBEAM))
				AlignementOfTheMonocromatorWithXBeamWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.ALIGNEMENT_OF_THE_SAMPLE_WITH_THE_XBEAM))
				AlignementOfTheSampleWithXBeamWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.ROCKING_CURVE_TOPOGRAPHY))
				RockingCurveMeasureWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.THETA_2THETA_SCAN))
				Theta2ThetaScanWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.FREE_MOVEMENTS_AND_SCANS))
				FreeMovementsAndScansWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.CHANGE_TUBE_MONOCHROMATOR))
				ChangeTubeMonochromatorWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.TUBE_STABILITY_SCAN))
				TubeStabilityScanWindow.getInstance(this.getPort()).setVisible(true);
			else if (eventName.equals(ActionCommands.CONTROLLER_CRASH_RECOVERY))
				ControllerCrashRecoveryWindow.getInstance(this.getPort()).setVisible(true);
		}
		catch (Exception e)
		{
			GuiUtilities.showErrorPopup("Exception captured: " + e.getClass().getName() + " - " + e.getMessage(), (JPanel) this.getContentPane().getComponent(0));
		}

	}

	private class MainWindowAdapter extends WindowAdapter
	{
		private Main main;

		public MainWindowAdapter(Main main)
		{
			this.main = main;
		}

		public void windowClosing(WindowEvent event)
		{
			try
			{
				CommunicationPortFactory.releasePort(this.main.getPort());
			}
			catch (CommunicationPortException e)
			{
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			new WaitFrameThread().start();
			new MainFrameThread().start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// PRIVATE STATIC METHODS
	// ------------------------------------------------------------------------------------------------------

	static ICommunicationPort initializeCommunicationPort() throws IOException
	{
		CommunicationPortFactory.setApplicationName(APPLICATION_NAME);

		ICommunicationPort port = null;
		KindOfPort kindOfPort = GuiUtilities.getKindOfPort();

		if (kindOfPort.equals(CommunicationPortUtilies.getSerialPort()))
		{
			String portName = GuiUtilities.getPortNames().listIterator().next();

			port = CommunicationPortFactory.getPort(portName, kindOfPort);

			SerialPortParameters parameters = new SerialPortParameters();
			parameters.deserialize(GuiUtilities.getPortConfFileName(portName));

			port.initialize(parameters);
			CommunicationPortFactory.getEmergencyPort().initialize(parameters);
		}
		else if (kindOfPort.equals(CommunicationPortUtilies.getEthernetPort()))
		{
			port = CommunicationPortFactory.getPort("Eth1", kindOfPort);

			EthernetPortParameters parameters = new EthernetPortParameters();
			parameters.deserialize(GuiUtilities.getPortConfFileName("Eth1"));

			port.initialize(parameters);
			CommunicationPortFactory.getEmergencyPort().initialize(parameters);
		}

		return port;
	}

	static void restoreSavedAxisPosition(ICommunicationPort port) throws IOException, CommunicationPortException
	{
		DriverUtilities.restoreSavedAxisPosition(Axis.THETA, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.TWOTHETA, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.Z, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.W, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.THETAFIRST, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.TWOTHETAFIRST, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.X, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.Y, GuiUtilities.getNullListener(), port);
	}

	static IAxisConfigurationMap getAxisConf() throws IOException
	{
		DefaultAxisConfigurationMap map = new DefaultAxisConfigurationMap();

		AxisConfiguration[] axisConfigurationArray = DriverUtilities.getAxisConfigurationArray();

		map.setAxisConfiguration(Axis.THETA, axisConfigurationArray[0]);
		map.setAxisConfiguration(Axis.TWOTHETA, axisConfigurationArray[1]);
		map.setAxisConfiguration(Axis.Z, axisConfigurationArray[2]);
		map.setAxisConfiguration(Axis.W, axisConfigurationArray[3]);
		map.setAxisConfiguration(Axis.THETAFIRST, axisConfigurationArray[4]);
		map.setAxisConfiguration(Axis.TWOTHETAFIRST, axisConfigurationArray[5]);
		map.setAxisConfiguration(Axis.X, axisConfigurationArray[6]);
		map.setAxisConfiguration(Axis.Y, axisConfigurationArray[7]);

		map.setAxisConfiguration(Axis.THETA2THETA, new AxisConfiguration(DriverUtilities.getDecimalGrades(), new MultipleAxis(Axis.THETA, Axis.TWOTHETA, DriverUtilities.getPlus(), 1), "Theta - 2Theta"));
		map.setAxisConfiguration(Axis.THETA2THETAFIRST, new AxisConfiguration(DriverUtilities.getDecimalGrades(), new MultipleAxis(Axis.THETAFIRST, Axis.TWOTHETAFIRST, DriverUtilities.getPlus(), 1), "Theta' - 2Theta'"));

		return map;
	}

	static void changeAxisMotorConfiguration(ICommunicationPort port) throws CommunicationPortException
	{
		if (DriverUtilities.getKindOfController().equals(DriverUtilities.getGalilController()))
		{
			CommandsFacade.executeCommand(CommandsFacade.Commands.MOTOR_CONFIGURATION, new CommandParameters(Axis.THETA, GuiUtilities.getNullListener()), port);
			CommandsFacade.executeCommand(CommandsFacade.Commands.MOTOR_CONFIGURATION, new CommandParameters(Axis.TWOTHETA, GuiUtilities.getNullListener()), port);
			CommandsFacade.executeCommand(CommandsFacade.Commands.MOTOR_CONFIGURATION, new CommandParameters(Axis.THETAFIRST, GuiUtilities.getNullListener()), port);
			CommandsFacade.executeCommand(CommandsFacade.Commands.MOTOR_CONFIGURATION, new CommandParameters(Axis.TWOTHETAFIRST, GuiUtilities.getNullListener()), port);
		}

	}

	// ------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	// ------------------------------------------------------------------------------------------------------

	private String getSWVersion() throws CommunicationPortException
	{
		return CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_SOFTWARE_VERSION, null, this.getPort());
	}

	private String getIOStatus() throws CommunicationPortException
	{
		return CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_IO_STATUS, null, this.getPort());
	}

	private void terminate()
	{
		try
		{
			this.getPort().release();
		}
		finally
		{
			System.exit(0);
		}

	}
}
