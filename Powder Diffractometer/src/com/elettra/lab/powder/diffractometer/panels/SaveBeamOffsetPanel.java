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
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.driver.programs.ProgramsFacade.Programs;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.lab.powder.diffractometer.Axis;

public final class SaveBeamOffsetPanel extends MovementListener implements ActionListener
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
	private JButton            saveBeamOffsetButton;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public SaveBeamOffsetPanel(ICommunicationPort port) throws IOException
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
		tabbedPaneUp.addTab("Diffractometer Alignement", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 145 };
		gbl_panelUp.rowHeights = new int[] { 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0 };
		gbl_panelUp.rowWeights = new double[] { 1.0 };
		panelUp.setLayout(gbl_panelUp);

		saveBeamOffsetButton = new JButton("SAVE 2THETA POSITION AS BEAM OFFSET");
		saveBeamOffsetButton.addActionListener(this);
		saveBeamOffsetButton.setActionCommand(ActionCommands.SAVE);
		GridBagConstraints gbc_saveSampleZButton = new GridBagConstraints();
		gbc_saveSampleZButton.gridx = 0;
		gbc_saveSampleZButton.gridy = 0;
		panelUp.add(saveBeamOffsetButton, gbc_saveSampleZButton);
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
		new StartSave(this, this.port).start();
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

	class StartSave extends Thread
	{
		private SaveBeamOffsetPanel panel;
		private ICommunicationPort  port;

		public StartSave(SaveBeamOffsetPanel panel, ICommunicationPort port)
		{
			super();

			this.port = port;
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				String sample2Theta = CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.TWOTHETA, GuiUtilities.getNullListener()), this.port);

				if (GuiUtilities.showConfirmPopup("Do you confirm the storing of this value of 2Theta as the beam offset?\n\n2Theta Position -> " + DriverUtilities.parseAxisPositionResponse(Axis.TWOTHETA, sample2Theta), this.panel))
				{
					References.getInstance().save(References.BEAM_OFFSET, sample2Theta);

					if (GuiUtilities.showConfirmPopup("Do you want set the beam offset as the new position of the 0 point for the 2Theta axis?", this.panel))
					{
						CommandsFacade.executeCommand(Commands.POS, new CommandParameters(Axis.TWOTHETA, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);

						if (GuiUtilities.showConfirmPopup("Do you want to move the Omega axis on the same offset?", this.panel))
						{
							MoveParameters moveParameters = new MoveParameters(Axis.OMEGA, ListenerRegister.getInstance());
							moveParameters.setKindOfMovement(DriverUtilities.getAbsolute());
							moveParameters.setSign(DriverUtilities.getPlus());
							moveParameters.setPosition(0.0);

							ProgramsFacade.executeProgram(Programs.MOVE, moveParameters, this.port);
							CommandsFacade.waitForTheEndOfMovement(new CommandParameters(Axis.OMEGA, ListenerRegister.getInstance()), this.port);

							if (GuiUtilities.showConfirmPopup("Do you want set the beam offset as the new position of the 0 point for the Omega axis?", this.panel))
								CommandsFacade.executeCommand(Commands.POS, new CommandParameters(Axis.OMEGA, DriverUtilities.getPlus(), 0.0, ListenerRegister.getInstance()), this.port);
						}
					}
				}
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

	class EnableThread extends Thread
	{
		private SaveBeamOffsetPanel panel;
		private boolean             enabled;

		public EnableThread(SaveBeamOffsetPanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.saveBeamOffsetButton.setEnabled(enabled);
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
