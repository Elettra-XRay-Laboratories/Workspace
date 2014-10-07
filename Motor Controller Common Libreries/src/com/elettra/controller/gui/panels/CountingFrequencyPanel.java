package com.elettra.controller.gui.panels;

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
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.programs.MeasureParameters;
import com.elettra.controller.driver.programs.MeasureResult;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.MovementListener;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class CountingFrequencyPanel extends MovementListener implements ActionListener
{
	private boolean           stop;

	/**
	 * 
	 */
	private static final long serialVersionUID = 986591557129181448L;

	static class ActionCommands
	{
		private static final String START = "START";
		private static final String STOP  = "STOP";
	}

	private ICommunicationPort port;
	private boolean            isScanActive;
	private JButton            startButton;
	private JTextField         frequency;
	private JButton            stopButton;

	private JComboBox          scanTimeComboBox;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public CountingFrequencyPanel(ICommunicationPort port) throws IOException
	{
		this.isScanActive = false;
		this.port = port;
		this.stop = true;

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
		tabbedPaneUp.addTab("Counting Frequency", null, panelUp, null);
		tabbedPaneUp.setEnabledAt(0, true);
		tabbedPaneUp.setBackgroundAt(0, UIManager.getColor("Panel.background"));
		tabbedPaneUp.setForegroundAt(0, new Color(51, 102, 0));
		GridBagLayout gbl_panelUp = new GridBagLayout();
		gbl_panelUp.columnWidths = new int[] { 80, 80, 0 };
		gbl_panelUp.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelUp.columnWeights = new double[] { 0.0, 1.0, 1.0 };
		gbl_panelUp.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		panelUp.setLayout(gbl_panelUp);

		JLabel lblNewLabel = new JLabel("Counting Frequency");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(10, 5, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelUp.add(lblNewLabel, gbc_lblNewLabel);

		frequency = new JTextField();
		frequency.setHorizontalAlignment(SwingConstants.RIGHT);
		frequency.setText("0");
		frequency.setEditable(false);
		GridBagConstraints gbc_frequency = new GridBagConstraints();
		gbc_frequency.anchor = GridBagConstraints.WEST;
		gbc_frequency.insets = new Insets(10, 0, 5, 5);
		gbc_frequency.gridx = 1;
		gbc_frequency.gridy = 0;
		panelUp.add(frequency, gbc_frequency);
		frequency.setColumns(10);

		JLabel lblPhotonssecond = new JLabel("ph/s");
		GridBagConstraints gbc_lblPhotonssecond = new GridBagConstraints();
		gbc_lblPhotonssecond.anchor = GridBagConstraints.WEST;
		gbc_lblPhotonssecond.insets = new Insets(10, 0, 5, 0);
		gbc_lblPhotonssecond.gridx = 2;
		gbc_lblPhotonssecond.gridy = 0;
		panelUp.add(lblPhotonssecond, gbc_lblPhotonssecond);

		startButton = new JButton("START");
		startButton.addActionListener(this);

		JLabel lblScanFrequency = new JLabel("Scan Frequency");
		GridBagConstraints gbc_lblScanFrequency = new GridBagConstraints();
		gbc_lblScanFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_lblScanFrequency.anchor = GridBagConstraints.EAST;
		gbc_lblScanFrequency.gridx = 0;
		gbc_lblScanFrequency.gridy = 1;
		panelUp.add(lblScanFrequency, gbc_lblScanFrequency);

		scanTimeComboBox = new JComboBox();
		scanTimeComboBox.setMaximumRowCount(6);
		scanTimeComboBox.setModel(new DefaultComboBoxModel(new String[] { "0.5", "1", "2", "5", "10" }));
		GridBagConstraints gbc_scanTimeComboBox = new GridBagConstraints();
		gbc_scanTimeComboBox.anchor = GridBagConstraints.WEST;
		gbc_scanTimeComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_scanTimeComboBox.gridx = 1;
		gbc_scanTimeComboBox.gridy = 1;
		panelUp.add(scanTimeComboBox, gbc_scanTimeComboBox);

		JLabel lblSeconds = new JLabel("seconds");
		GridBagConstraints gbc_lblSeconds = new GridBagConstraints();
		gbc_lblSeconds.anchor = GridBagConstraints.WEST;
		gbc_lblSeconds.insets = new Insets(0, 0, 5, 0);
		gbc_lblSeconds.gridx = 2;
		gbc_lblSeconds.gridy = 1;
		panelUp.add(lblSeconds, gbc_lblSeconds);
		startButton.setActionCommand(ActionCommands.START);
		GridBagConstraints gbc_startButton = new GridBagConstraints();
		gbc_startButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_startButton.insets = new Insets(0, 5, 0, 5);
		gbc_startButton.gridx = 0;
		gbc_startButton.gridy = 2;
		panelUp.add(startButton, gbc_startButton);

		stopButton = new JButton("STOP");
		stopButton.addActionListener(this);
		stopButton.setForeground(Color.RED);
		stopButton.setActionCommand(ActionCommands.STOP);
		GridBagConstraints gbc_stopButton = new GridBagConstraints();
		gbc_stopButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_stopButton.insets = new Insets(0, 0, 0, 5);
		gbc_stopButton.gridx = 1;
		gbc_stopButton.gridy = 2;
		panelUp.add(stopButton, gbc_stopButton);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.START))
					this.manageEventStart();
				else if (e.getActionCommand().equals(ActionCommands.STOP))
					this.manageEventStop();
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

	private void manageEventStart() throws IOException
	{
		this.stop = false;
		this.scanTimeComboBox.setEnabled(false);

		new StartThread(this).start();
	}

	private void manageEventStop() throws IOException
	{
		this.stop = true;
		this.scanTimeComboBox.setEnabled(true);
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
		private CountingFrequencyPanel panel;
		private boolean                enabled;

		public EnableThread(CountingFrequencyPanel panel, boolean enabled)
		{
			this.panel = panel;
			this.enabled = enabled;
		}

		public void run()
		{
			try
			{
				this.panel.startButton.setEnabled(enabled);
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

	class StartThread extends Thread
	{
		private CountingFrequencyPanel panel;
		private MeasureParameters        countParameters;

		public StartThread(CountingFrequencyPanel panel)
		{
			this.panel = panel;
			this.countParameters = new MeasureParameters(0, GuiUtilities.getNullListener());
			this.countParameters.setScanTime(Double.parseDouble((String) panel.scanTimeComboBox.getSelectedItem()));
		}

		public void run()
		{
			try
			{
				while (!this.panel.stop)
				{
					MeasureResult result = (MeasureResult) ProgramsFacade.executeProgram(ProgramsFacade.Programs.COUNT, this.countParameters, this.panel.port);

					this.panel.frequency.setText(Integer.toString((int) (result.getMeasure()/Double.parseDouble((String) panel.scanTimeComboBox.getSelectedItem()))));

					ObjectUtilities.pause(200);
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
}
