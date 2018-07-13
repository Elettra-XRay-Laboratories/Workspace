package com.elettra.lab.optic.diffractometer.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.listeners.IDriverListener;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.lab.optic.diffractometer.Axis;
import javax.swing.JButton;

public final class XRayBeamCenteringPanel extends MovementListener implements ActionListener
{
	static class ActionCommands
	{
		private static final String MOVE = "MOVE";
		private static final String STOP = "STOP";
	}

	private ICommunicationPort port;

	private boolean            isScanActive;

	/**
	 * 
	 */
	private static final long  serialVersionUID = 4782466305885803102L;
	private JTextField         twoThetaFirstPositionTextField;
	private JTextField         thetaFirstPositionTextField;
	private JTextField         twoThetaPositionTextField;
	private JTextField         correctingAngleTextField;

	private JButton            correctPositionButton;

	private JButton            stopButton;

	private double             detectorArm;
	private double             distanceCentersOfRotation;

	/**
	 * Create the panel.
	 * 
	 * @throws IOException
	 * @throws
	 */
	public XRayBeamCenteringPanel(ICommunicationPort port) throws IOException
	{
		this.detectorArm = Double.parseDouble(FileIni.getInstance().getProperty("DetectorArm"));
		this.distanceCentersOfRotation = Double.parseDouble(FileIni.getInstance().getProperty("DistanceCentersOfRotation"));

		this.isScanActive = false;

		this.port = port;

		ListenerRegister.getInstance().addListener(Axis.TWOTHETA, this);
		ListenerRegister.getInstance().addListener(Axis.THETAFIRST, this);
		ListenerRegister.getInstance().addListener(Axis.TWOTHETAFIRST, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 240, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JTabbedPane axisTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_axisTabbedPane = new GridBagConstraints();
		gbc_axisTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_axisTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_axisTabbedPane.gridx = 0;
		gbc_axisTabbedPane.gridy = 0;
		add(axisTabbedPane, gbc_axisTabbedPane);

		JPanel axisPanel = new JPanel();
		axisTabbedPane.addTab("Axis", null, axisPanel, null);
		axisTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_axisPanel = new GridBagLayout();
		gbl_axisPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_axisPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_axisPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_axisPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		axisPanel.setLayout(gbl_axisPanel);

		JLabel lbltheta = new JLabel("2Theta");
		GridBagConstraints gbc_lbltheta = new GridBagConstraints();
		gbc_lbltheta.insets = new Insets(10, 5, 5, 5);
		gbc_lbltheta.anchor = GridBagConstraints.EAST;
		gbc_lbltheta.gridx = 0;
		gbc_lbltheta.gridy = 0;
		axisPanel.add(lbltheta, gbc_lbltheta);

		twoThetaPositionTextField = new JTextField();
		twoThetaPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		twoThetaPositionTextField.setEditable(false);
		twoThetaPositionTextField.setColumns(10);
		GridBagConstraints gbc_twoThetaPositionTextField = new GridBagConstraints();
		gbc_twoThetaPositionTextField.anchor = GridBagConstraints.WEST;
		gbc_twoThetaPositionTextField.insets = new Insets(10, 10, 5, 0);
		gbc_twoThetaPositionTextField.gridx = 1;
		gbc_twoThetaPositionTextField.gridy = 0;
		axisPanel.add(twoThetaPositionTextField, gbc_twoThetaPositionTextField);
		twoThetaPositionTextField.setText(this.readAxisPosition(Axis.TWOTHETA));

		JLabel lbltheta_1 = new JLabel("Theta'");
		GridBagConstraints gbc_lbltheta_1 = new GridBagConstraints();
		gbc_lbltheta_1.insets = new Insets(10, 5, 5, 5);
		gbc_lbltheta_1.anchor = GridBagConstraints.EAST;
		gbc_lbltheta_1.gridx = 0;
		gbc_lbltheta_1.gridy = 1;
		axisPanel.add(lbltheta_1, gbc_lbltheta_1);

		thetaFirstPositionTextField = new JTextField();
		thetaFirstPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		thetaFirstPositionTextField.setEditable(false);
		thetaFirstPositionTextField.setColumns(10);
		GridBagConstraints gbc_thetaFirstPositionTextField = new GridBagConstraints();
		gbc_thetaFirstPositionTextField.anchor = GridBagConstraints.WEST;
		gbc_thetaFirstPositionTextField.insets = new Insets(10, 10, 5, 0);
		gbc_thetaFirstPositionTextField.gridx = 1;
		gbc_thetaFirstPositionTextField.gridy = 1;
		axisPanel.add(thetaFirstPositionTextField, gbc_thetaFirstPositionTextField);
		thetaFirstPositionTextField.setText(this.readAxisPosition(Axis.THETAFIRST));

		JLabel lbltheta_2 = new JLabel("2Theta'");
		GridBagConstraints gbc_lbltheta_2 = new GridBagConstraints();
		gbc_lbltheta_2.insets = new Insets(0, 5, 0, 5);
		gbc_lbltheta_2.anchor = GridBagConstraints.EAST;
		gbc_lbltheta_2.gridx = 0;
		gbc_lbltheta_2.gridy = 2;
		axisPanel.add(lbltheta_2, gbc_lbltheta_2);

		twoThetaFirstPositionTextField = new JTextField();
		twoThetaFirstPositionTextField.setEditable(false);
		twoThetaFirstPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_twoThetaFirstPositionTextField = new GridBagConstraints();
		gbc_twoThetaFirstPositionTextField.insets = new Insets(0, 10, 0, 0);
		gbc_twoThetaFirstPositionTextField.anchor = GridBagConstraints.WEST;
		gbc_twoThetaFirstPositionTextField.gridx = 1;
		gbc_twoThetaFirstPositionTextField.gridy = 2;
		axisPanel.add(twoThetaFirstPositionTextField, gbc_twoThetaFirstPositionTextField);
		twoThetaFirstPositionTextField.setColumns(10);
		twoThetaFirstPositionTextField.setText(this.readAxisPosition(Axis.TWOTHETAFIRST));

		JTabbedPane commandsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_commandsTabbedPane = new GridBagConstraints();
		gbc_commandsTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_commandsTabbedPane.gridx = 0;
		gbc_commandsTabbedPane.gridy = 1;
		add(commandsTabbedPane, gbc_commandsTabbedPane);

		JPanel commandsPanel = new JPanel();
		commandsTabbedPane.addTab("Commands", null, commandsPanel, null);
		commandsTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_commandsPanel = new GridBagLayout();
		gbl_commandsPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_commandsPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_commandsPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_commandsPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		commandsPanel.setLayout(gbl_commandsPanel);

		JLabel lblCorrectingAngle = new JLabel("Correcting Angle");
		GridBagConstraints gbc_lblCorrectingAngle = new GridBagConstraints();
		gbc_lblCorrectingAngle.insets = new Insets(10, 5, 5, 5);
		gbc_lblCorrectingAngle.anchor = GridBagConstraints.EAST;
		gbc_lblCorrectingAngle.gridx = 0;
		gbc_lblCorrectingAngle.gridy = 0;
		commandsPanel.add(lblCorrectingAngle, gbc_lblCorrectingAngle);

		correctingAngleTextField = new JTextField();
		correctingAngleTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		correctingAngleTextField.setEditable(false);
		GridBagConstraints gbc_correctingAngleTextField = new GridBagConstraints();
		gbc_correctingAngleTextField.gridwidth = 2;
		gbc_correctingAngleTextField.insets = new Insets(10, 5, 5, 0);
		gbc_correctingAngleTextField.anchor = GridBagConstraints.WEST;
		gbc_correctingAngleTextField.gridx = 1;
		gbc_correctingAngleTextField.gridy = 0;
		commandsPanel.add(correctingAngleTextField, gbc_correctingAngleTextField);
		correctingAngleTextField.setColumns(10);
		correctingAngleTextField.setText(DriverUtilities.formatControllerPosition(0));

		correctPositionButton = new JButton("CORRECT POSITION");
		correctPositionButton.setActionCommand(ActionCommands.MOVE);
		correctPositionButton.addActionListener(this);
		GridBagConstraints gbc_correctPositionButton = new GridBagConstraints();
		gbc_correctPositionButton.gridwidth = 2;
		gbc_correctPositionButton.insets = new Insets(0, 5, 5, 5);
		gbc_correctPositionButton.gridx = 0;
		gbc_correctPositionButton.gridy = 1;
		commandsPanel.add(correctPositionButton, gbc_correctPositionButton);

		stopButton = new JButton("STOP");
		stopButton.setActionCommand(ActionCommands.STOP);
		stopButton.addActionListener(this);
		stopButton.setForeground(new Color(204, 0, 0));
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.anchor = GridBagConstraints.WEST;
		gbc_stopButton.insets = new Insets(0, 0, 5, 5);
		gbc_stopButton.gridx = 2;
		gbc_stopButton.gridy = 1;
		commandsPanel.add(stopButton, gbc_stopButton);

		this.calculateCorrectingAngle();
	}

	private String readAxisPosition(int axis) throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, GuiUtilities.getNullListener()), this.port));

		return Integer.toString(axis) + ":" + position.getSign().toString() + DriverUtilities.formatControllerPosition(position.getAbsolutePosition()) + DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axis).getMeasureUnit().toString();

	}

	protected void setAxisPositionField(int axis, String text)
	{
		switch (axis)
		{
			case Axis.TWOTHETA:
				twoThetaPositionTextField.setText(text);
				break;
			case Axis.TWOTHETAFIRST:
				twoThetaFirstPositionTextField.setText(text);
				break;
			case Axis.THETAFIRST:
				thetaFirstPositionTextField.setText(text);
				break;
			default:
				break;
		}
	}

	protected void calculateCorrectingAngle()
	{
		double twoTheta = (DriverUtilities.parseHuberAxisPosition(twoThetaPositionTextField.getText()).getSignedPosition() * Math.PI) / 180d;

		double correctingAngle = (Math.atan((this.detectorArm / (this.detectorArm + this.distanceCentersOfRotation)) * Math.tan(twoTheta)) * 180d) / Math.PI;

		correctingAngleTextField.setText((Math.signum(correctingAngle) >= 0 ? "+" : "") + GuiUtilities.parseDouble(correctingAngle).trim());
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		new EnableThread(this, enabled).start();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.MOVE))
				{
					this.manageEventMove();
				}
				else if (e.getActionCommand().equals(ActionCommands.STOP))
				{
					this.manageEventStop();
				}
			}
			catch (CommunicationPortException exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}
		else
		{
			GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
		}
	}

	private void manageEventStop() throws CommunicationPortException
	{
		CommandsFacade.executeCommand(CommandsFacade.Commands.STOP, new CommandParameters(GuiUtilities.getNullListener()), this.port);
	}

	private void manageEventMove() throws CommunicationPortException
	{
		double correctingAngle = Double.parseDouble(correctingAngleTextField.getText());
		double thetaFirst = DriverUtilities.parseHuberAxisPosition(thetaFirstPositionTextField.getText()).getSignedPosition();
		double twoThetaFirst = DriverUtilities.parseHuberAxisPosition(twoThetaFirstPositionTextField.getText()).getSignedPosition();

		thetaFirst = thetaFirst + correctingAngle;
		twoThetaFirst = twoThetaFirst + correctingAngle;

		ControllerPosition thetaFirstPosition = DriverUtilities.numberToController(thetaFirst);
		ControllerPosition twoThetaFirstPosition = DriverUtilities.numberToController(twoThetaFirst);

		MoveParameters moveParameters1 = new MoveParameters(Axis.THETAFIRST, ListenerRegister.getInstance());
		moveParameters1.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters1.setSign(thetaFirstPosition.getSign());
		moveParameters1.setPosition(thetaFirstPosition.getAbsolutePosition());

		MoveParameters moveParameters2 = new MoveParameters(Axis.TWOTHETAFIRST, ListenerRegister.getInstance());
		moveParameters2.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters2.setSign(twoThetaFirstPosition.getSign());
		moveParameters2.setPosition(twoThetaFirstPosition.getAbsolutePosition());

		new XRayBeamCentering(moveParameters1, moveParameters2, this.port).start();
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * IDriverListener
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		new MoveListener(this, axis).start();
	}

	public synchronized void signalScanStart()
	{
		this.isScanActive = true;
	}

	public synchronized void signalScanStop()
	{
		this.isScanActive = false;
	}

	/*
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 * 
	 * 
	 * Inner Classes
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	class XRayBeamCentering extends Thread
	{
		private MoveParameters     moveParameters1;
		private MoveParameters     moveParameters2;
		private ICommunicationPort port;

		public XRayBeamCentering(MoveParameters moveParameters1, MoveParameters moveParameters2, ICommunicationPort port)
		{
			super();

			this.moveParameters1 = moveParameters1;
			this.moveParameters2 = moveParameters2;
			this.port = port;
		}

		public void run()
		{
			try
			{
				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters1, this.port);

				CommandsFacade.waitForTheEndOfMovement(new CommandParameters(this.moveParameters1.getAxis(), ListenerRegister.getInstance()), this.port);

				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters2, this.port);
			}
			catch (CommunicationPortException exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}

	class MoveListener extends Thread
	{
		/**
		 * 
		 */
		private XRayBeamCenteringPanel panel;
		private int                    axis;

		public MoveListener(XRayBeamCenteringPanel panel, int axis)
		{
			this.panel = panel;
			this.axis = axis;
		}

		public void run()
		{
			try
			{
				IDriverListener nullListener = GuiUtilities.getNullListener();

				ControllerPosition controllerPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

				double previousPosition = controllerPosition.getSignedPosition();
				double currentPosition = previousPosition;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(axis, DriverUtilities.formatControllerPositionTextField(axis, controllerPosition));
					if (axis == Axis.TWOTHETA)
						this.panel.calculateCorrectingAngle();

					ObjectUtilities.pause(200);

					controllerPosition = DriverUtilities.parseAxisPositionResponse(axis, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(axis, nullListener), port));

					previousPosition = currentPosition;
					currentPosition = controllerPosition.getSignedPosition();

					if (currentPosition == previousPosition)
						doCycle = ++trial < 5;

				} while (doCycle);
			}
			catch (CommunicationPortException exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}

	class EnableThread extends Thread
	{
		private XRayBeamCenteringPanel panel;
		private boolean                enabled;

		public EnableThread(XRayBeamCenteringPanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.correctPositionButton.setEnabled(enabled);
				this.panel.stopButton.setEnabled(enabled);
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
			finally
			{
			}
		}
	}

}
