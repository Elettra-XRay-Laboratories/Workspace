package com.elettra.lab.powder.diffractometer.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.lab.powder.diffractometer.Axis;

public final class SampleZReferencePanel extends MovementListener implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 986591557129181448L;

	static class ActionCommands
	{
		private static final String RESTORE = "RESTORE";
	}

	private ICommunicationPort port;
	private boolean            isScanActive;
	private JButton            restoreSampleZButton;
	private JTextField         sampleZReference;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public SampleZReferencePanel(ICommunicationPort port) throws IOException
	{
		this.isScanActive = false;
		this.port = port;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		setLayout(gridBagLayout);

		JTabbedPane tabbedPaneUp = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPaneUp = new GridBagConstraints();
		gbc_tabbedPaneUp.fill = GridBagConstraints.BOTH;
		gbc_tabbedPaneUp.insets = new Insets(-20, 0, 0, 0);
		gbc_tabbedPaneUp.gridx = 0;
		gbc_tabbedPaneUp.gridy = 0;
		add(tabbedPaneUp, gbc_tabbedPaneUp);

		JPanel panelUp = new JPanel();
		tabbedPaneUp.addTab("Sample Z Reference Point", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 0, 145 };
		gbl_panelUp.rowHeights = new int[] { 0, 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 1.0 };
		gbl_panelUp.rowWeights = new double[] { 0.0, 1.0 };
		panelUp.setLayout(gbl_panelUp);

		restoreSampleZButton = new JButton("RESTORE SAMPLE Z REFERENCE POINT");
		restoreSampleZButton.addActionListener(this);

		JLabel lblNewLabel = new JLabel("Sample Z Reference Point");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(10, 5, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelUp.add(lblNewLabel, gbc_lblNewLabel);

		sampleZReference = new JTextField();
		sampleZReference.setText(DriverUtilities.formatControllerPositionTextField(Axis.Z, DriverUtilities.parseAxisPositionResponse(Axis.Z, References.getInstance().get(References.SAMPLE_Z_AT_PSI_0))));
		sampleZReference.setEditable(false);
		GridBagConstraints gbc_sampleZReference = new GridBagConstraints();
		gbc_sampleZReference.anchor = GridBagConstraints.WEST;
		gbc_sampleZReference.insets = new Insets(10, 0, 5, 5);
		gbc_sampleZReference.gridx = 1;
		gbc_sampleZReference.gridy = 0;
		panelUp.add(sampleZReference, gbc_sampleZReference);
		sampleZReference.setColumns(10);
		restoreSampleZButton.setActionCommand(ActionCommands.RESTORE);
		GridBagConstraints gbc_saveSampleZButton = new GridBagConstraints();
		gbc_saveSampleZButton.insets = new Insets(0, 5, 0, 0);
		gbc_saveSampleZButton.anchor = GridBagConstraints.WEST;
		gbc_saveSampleZButton.gridwidth = 2;
		gbc_saveSampleZButton.gridx = 0;
		gbc_saveSampleZButton.gridy = 1;
		panelUp.add(restoreSampleZButton, gbc_saveSampleZButton);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.RESTORE))
					this.manageEventRestore();
			}
			catch (IOException exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}
		else
		{
			GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
		}
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		new EnableThread(this, enabled).start();
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
	 * EventManagement
	 * 
	 * 
	 * ----------------------------------------------------------------------------
	 * ----------------------------------------------------------
	 */

	private void manageEventRestore() throws IOException
	{
		ControllerPosition position = DriverUtilities.parseAxisPositionResponse(Axis.Z, References.getInstance().get(References.SAMPLE_Z_AT_PSI_0));

		if (GuiUtilities.showConfirmPopup("Do you confirm the restore of Z as the sample reference position?\n\nZ Position -> " + position.getSignedPosition(), this))
		{
			MoveParameters moveParameters = new MoveParameters(Axis.Z, ListenerRegister.getInstance());
			moveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
			moveParameters.setPosition(position.getAbsolutePosition());
			moveParameters.setSign(position.getSign());

			ProgramsFacade.executeProgram(Programs.MOVE, moveParameters, this.port);
		}
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

	class EnableThread extends Thread
	{
		private SampleZReferencePanel panel;
		private boolean               enabled;

		public EnableThread(SampleZReferencePanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.restoreSampleZButton.setEnabled(enabled);
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
