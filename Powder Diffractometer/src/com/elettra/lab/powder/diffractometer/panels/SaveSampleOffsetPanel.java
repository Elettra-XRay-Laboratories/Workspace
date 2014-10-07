package com.elettra.lab.powder.diffractometer.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.commands.CommandsFacade.Commands;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.lab.powder.diffractometer.Axis;

public final class SaveSampleOffsetPanel extends MovementListener implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 986591557129181448L;

	static class ActionCommands
	{
		private static final String SAVE = "SAVE";
	}

	private ICommunicationPort port;
	private boolean            isScanActive;
	private JButton            saveSampleOffsetButton;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public SaveSampleOffsetPanel(ICommunicationPort port) throws IOException
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
		tabbedPaneUp.addTab("Sample Alignement", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 145 };
		gbl_panelUp.rowHeights = new int[] { 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0 };
		gbl_panelUp.rowWeights = new double[] { 1.0 };
		panelUp.setLayout(gbl_panelUp);

		saveSampleOffsetButton = new JButton("SAVE OMEGA POSITION AS PARALLEL REFERENCE PLANE");
		saveSampleOffsetButton.addActionListener(this);
		saveSampleOffsetButton.setActionCommand(ActionCommands.SAVE);
		GridBagConstraints gbc_saveSampleZButton = new GridBagConstraints();
		gbc_saveSampleZButton.gridx = 0;
		gbc_saveSampleZButton.gridy = 0;
		panelUp.add(saveSampleOffsetButton, gbc_saveSampleZButton);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.SAVE))
					this.manageEventSave();
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

	private void manageEventSave() throws IOException
	{
		String sampleOmega = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.OMEGA, GuiUtilities.getNullListener()), this.port);

		if (GuiUtilities.showConfirmPopup("Do you confirm the storing of this value of Omega as the sample offset?\n\nOmega Position -> " + DriverUtilities.parseAxisPositionResponse(Axis.OMEGA, sampleOmega), this))
		{
			References.getInstance().save(References.SAMPLE_OFFSET, sampleOmega);

			if (GuiUtilities.showConfirmPopup("Do you want set the beam offset as the new position of the 0 point for the Omega axis?", this))
				CommandsFacade.executeCommand(Commands.POS, new CommandParameters(Axis.OMEGA, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);
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
		private SaveSampleOffsetPanel panel;
		private boolean               enabled;

		public EnableThread(SaveSampleOffsetPanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.saveSampleOffsetButton.setEnabled(enabled);
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
