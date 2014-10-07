package com.elettra.lab.powder.basement;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.SerialPortParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.IAxisConfigurationMap;
import com.elettra.controller.driver.programs.DefaultAxisConfigurationMap;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.ScanPanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;

public class MainBasement extends AbstractGenericFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long   serialVersionUID = 2414626770270274046L;
	/**
	 * 
	 */

	private static final String APPLICATION_NAME = "Basement Alignement";

	static class ActionCommands
	{
		private static final String EXIT = "EXIT";
	}

	/**
	 * 
	 */

	public MainBasement(ICommunicationPort port) throws HeadlessException
	{
		super(APPLICATION_NAME, port);

		try
		{
			this.addWindowFocusListener(new MainWindowAdapter(this));

			this.setBounds(5, 5, 2870, 900);

			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 600, 1670, 600 };
			gridBagLayout.rowHeights = new int[] { 0 };
			gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
			gridBagLayout.rowWeights = new double[] { 1.0 };
			getContentPane().setLayout(gridBagLayout);

			JPanel leftPanel = new JPanel();
			GridBagConstraints gbc_leftPanel = new GridBagConstraints();
			gbc_leftPanel.insets = new Insets(10, 0, 0, 5);
			gbc_leftPanel.anchor = GridBagConstraints.WEST;
			gbc_leftPanel.fill = GridBagConstraints.BOTH;
			gbc_leftPanel.gridx = 0;
			gbc_leftPanel.gridy = 0;
			getContentPane().add(leftPanel, gbc_leftPanel);
			GridBagLayout gbl_leftPanel = new GridBagLayout();
			gbl_leftPanel.columnWidths = new int[] { 280, 280 };
			gbl_leftPanel.rowHeights = new int[] { 450, 450 };
			gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0 };
			gbl_leftPanel.rowWeights = new double[] { 0.0, 0.0 };
			leftPanel.setLayout(gbl_leftPanel);

			JPanel movePanel7 = new JPanel();
			movePanel7.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel7 = new GridBagConstraints();
			gbc_movePanel7.insets = new Insets(10, 5, 5, 5);
			gbc_movePanel7.fill = GridBagConstraints.BOTH;
			gbc_movePanel7.gridx = 0;
			gbc_movePanel7.gridy = 0;
			leftPanel.add(movePanel7, gbc_movePanel7);
			movePanel7.add(new MovePanel(Axis.ZFIRST, this.getPort()));

			JPanel movePanel5 = new JPanel();
			movePanel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
			gbc_movePanel5.insets = new Insets(10, 0, 5, 5);
			gbc_movePanel5.fill = GridBagConstraints.BOTH;
			gbc_movePanel5.gridx = 1;
			gbc_movePanel5.gridy = 0;
			leftPanel.add(movePanel5, gbc_movePanel5);
			movePanel5.add(new MovePanel(Axis.TWOTHETAFIRST, this.getPort()));

			JPanel movePanel6 = new JPanel();
			GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
			gbc_movePanel6.insets = new Insets(0, 5, 5, 5);
			gbc_movePanel6.fill = GridBagConstraints.BOTH;
			gbc_movePanel6.gridx = 0;
			gbc_movePanel6.gridy = 1;
			leftPanel.add(movePanel6, gbc_movePanel6);

			JPanel doubleMovePanel1 = new JPanel();
			GridBagConstraints gbc_doubleMovePanel1 = new GridBagConstraints();
			gbc_doubleMovePanel1.insets = new Insets(0, 0, 5, 5);
			gbc_doubleMovePanel1.fill = GridBagConstraints.BOTH;
			gbc_doubleMovePanel1.gridx = 1;
			gbc_doubleMovePanel1.gridy = 1;
			leftPanel.add(doubleMovePanel1, gbc_doubleMovePanel1);

			JPanel rightPanel = new JPanel();
			GridBagConstraints gbc_rightPanel = new GridBagConstraints();
			gbc_rightPanel.fill = GridBagConstraints.BOTH;
			gbc_rightPanel.insets = new Insets(0, 0, 0, 5);
			gbc_rightPanel.gridx = 1;
			gbc_rightPanel.gridy = 0;
			getContentPane().add(rightPanel, gbc_rightPanel);
			GridBagLayout gbl_rightPanel = new GridBagLayout();
			gbl_rightPanel.columnWidths = new int[] { 0, 0 };
			gbl_rightPanel.rowHeights = new int[] { 0 };
			gbl_rightPanel.columnWeights = new double[] { 1.0, 1.0 };
			gbl_rightPanel.rowWeights = new double[] { 1.0 };
			rightPanel.setLayout(gbl_rightPanel);

			Rectangle bounds = new Rectangle(0, 0, 750, 845);

			JPanel scan0Panel = new JPanel();
			scan0Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			scan0Panel.setBounds(bounds);
			scan0Panel.add(new ScanPanel(Axis.ZFIRST, this.getPort(), false));
			GridBagConstraints gbc_scan0Panel = new GridBagConstraints();
			gbc_scan0Panel.insets = new Insets(10, 5, 5, 5);
			gbc_scan0Panel.fill = GridBagConstraints.BOTH;
			gbc_scan0Panel.gridx = 0;
			gbc_scan0Panel.gridy = 0;
			rightPanel.add(scan0Panel, gbc_scan0Panel);

			JPanel scan1Panel = new JPanel();
			scan1Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			scan1Panel.setBounds(bounds);
			scan1Panel.add(new ScanPanel(Axis.TWOTHETAFIRST, this.getPort(), false));
			GridBagConstraints gbc_scan1Panel = new GridBagConstraints();
			gbc_scan1Panel.insets = new Insets(10, 5, 5, 5);
			gbc_scan1Panel.fill = GridBagConstraints.BOTH;
			gbc_scan1Panel.gridx = 1;
			gbc_scan1Panel.gridy = 0;
			rightPanel.add(scan1Panel, gbc_scan1Panel);

			JPanel lateralPanel = new JPanel();
			GridBagConstraints gbc_lateralPanel = new GridBagConstraints();
			gbc_lateralPanel.insets = new Insets(0, 0, 0, 5);
			gbc_lateralPanel.anchor = GridBagConstraints.WEST;
			gbc_lateralPanel.fill = GridBagConstraints.BOTH;
			gbc_lateralPanel.gridx = 2;
			gbc_lateralPanel.gridy = 0;
			getContentPane().add(lateralPanel, gbc_lateralPanel);
			GridBagLayout gbl_lateralPanel = new GridBagLayout();
			gbl_lateralPanel.columnWidths = new int[] { 0 };
			gbl_lateralPanel.rowHeights = new int[] { 0, 0 };
			gbl_lateralPanel.columnWeights = new double[] { 1.0 };
			gbl_lateralPanel.rowWeights = new double[] { 1.0, 1.0 };
			lateralPanel.setLayout(gbl_lateralPanel);

			JTabbedPane statusTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_statusTabbedPane = new GridBagConstraints();
			gbc_statusTabbedPane.insets = new Insets(10, 5, 600, 300);
			gbc_statusTabbedPane.fill = GridBagConstraints.BOTH;
			gbc_statusTabbedPane.gridx = 0;
			gbc_statusTabbedPane.gridy = 0;
			lateralPanel.add(statusTabbedPane, gbc_statusTabbedPane);

			JPanel statusPanel = new JPanel();
			statusTabbedPane.addTab("Huber Status", null, statusPanel, null);
			GridBagLayout gbl_statusPanel = new GridBagLayout();
			gbl_statusPanel.columnWidths = new int[] { 0, 0 };
			gbl_statusPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_statusPanel.columnWeights = new double[] { 1.0, 1.0 };
			gbl_statusPanel.rowWeights = new double[] { 1.0, 1.0 };
			statusPanel.setLayout(gbl_statusPanel);
			statusTabbedPane.setForegroundAt(0, new Color(0, 102, 51));

			JLabel lblSwVersion = new JLabel("SW Version");
			GridBagConstraints gbc_lblSwVersion = new GridBagConstraints();
			gbc_lblSwVersion.insets = new Insets(10, 5, 5, 5);
			gbc_lblSwVersion.anchor = GridBagConstraints.EAST;
			gbc_lblSwVersion.gridx = 0;
			gbc_lblSwVersion.gridy = 0;
			statusPanel.add(lblSwVersion, gbc_lblSwVersion);

			JTextField softwareVersionTextField = new JTextField();
			softwareVersionTextField.setText(this.getSWVersion());
			GridBagConstraints gbc_softwareVersionTextField = new GridBagConstraints();
			gbc_softwareVersionTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_softwareVersionTextField.insets = new Insets(10, 0, 5, 50);
			gbc_softwareVersionTextField.gridx = 1;
			gbc_softwareVersionTextField.gridy = 0;
			statusPanel.add(softwareVersionTextField, gbc_softwareVersionTextField);
			softwareVersionTextField.setColumns(10);

			JLabel lblNewLabel_3 = new JLabel("IO Status");
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.insets = new Insets(4, 0, 0, 5);
			gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTHEAST;
			gbc_lblNewLabel_3.gridx = 0;
			gbc_lblNewLabel_3.gridy = 1;
			statusPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);

			JTextField ioStatusTextField = new JTextField();
			ioStatusTextField.setText(this.getIOStatus());
			GridBagConstraints gbc_ioStatusTextField = new GridBagConstraints();
			gbc_ioStatusTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_ioStatusTextField.anchor = GridBagConstraints.NORTH;
			gbc_ioStatusTextField.insets = new Insets(0, 0, 0, 50);
			gbc_ioStatusTextField.gridx = 1;
			gbc_ioStatusTextField.gridy = 1;
			statusPanel.add(ioStatusTextField, gbc_ioStatusTextField);
			ioStatusTextField.setColumns(10);

			JPanel exitPanel = new JPanel();
			GridBagConstraints gbc_exitPanel = new GridBagConstraints();
			gbc_exitPanel.fill = GridBagConstraints.BOTH;
			gbc_exitPanel.insets = new Insets(5, 5, 5, 300);
			gbc_exitPanel.gridx = 0;
			gbc_exitPanel.gridy = 1;
			gbc_exitPanel.gridheight = 2;
			lateralPanel.add(exitPanel, gbc_exitPanel);
			GridBagLayout gbl_exitPanel = new GridBagLayout();
			gbl_exitPanel.columnWidths = new int[] { 0 };
			gbl_exitPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_exitPanel.columnWeights = new double[] { 1.0 };
			gbl_exitPanel.rowWeights = new double[] { 1.0, 0.05, 0.02 };
			exitPanel.setLayout(gbl_exitPanel);

			GridBagConstraints gbc_emptyPanel2 = new GridBagConstraints();
			gbc_emptyPanel2.insets = new Insets(0, 0, 5, 5);
			gbc_emptyPanel2.fill = GridBagConstraints.BOTH;
			gbc_emptyPanel2.gridx = 0;
			gbc_emptyPanel2.gridy = 0;
			exitPanel.add(new JPanel(), gbc_emptyPanel2);

			GridBagConstraints gbc_emergencyDumpPanel = new GridBagConstraints();
			gbc_emergencyDumpPanel.fill = GridBagConstraints.BOTH;
			gbc_emergencyDumpPanel.insets = new Insets(0, 0, 5, 5);
			gbc_emergencyDumpPanel.gridx = 0;
			gbc_emergencyDumpPanel.gridy = 1;
			exitPanel.add(new JPanel(), gbc_emergencyDumpPanel);

			JButton exitButton = new JButton("EXIT");
			exitButton.setActionCommand(ActionCommands.EXIT);
			exitButton.addActionListener(this);
			GridBagConstraints gbc_exitButton = new GridBagConstraints();
			gbc_exitButton.fill = GridBagConstraints.BOTH;
			gbc_exitButton.insets = new Insets(0, 0, 5, 5);
			gbc_exitButton.gridx = 0;
			gbc_exitButton.gridy = 2;
			exitPanel.add(exitButton, gbc_exitButton);

		}
		catch (Exception e)
		{
			GuiUtilities.showErrorPopup("Exception captured: " + e.getClass().getName() + " - " + e.getMessage(), (JPanel) this.getContentPane().getComponent(0));
		}
	}

	private class MainWindowAdapter extends WindowAdapter
	{
		private MainBasement main;

		public MainWindowAdapter(MainBasement main)
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

		String portName = GuiUtilities.getPortNames().listIterator().next();

		ICommunicationPort port = CommunicationPortFactory.getPort(portName, CommunicationPortUtilies.getSerialPort());

		SerialPortParameters parameters = new SerialPortParameters();
		parameters.deserialize(GuiUtilities.getPortConfFileName(portName));

		port.initialize(parameters);

		return port;
	}

	static void restoreSavedAxisPosition(ICommunicationPort port) throws IOException, CommunicationPortException
	{
		DriverUtilities.restoreSavedAxisPosition(Axis.ZFIRST, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.TWOTHETAFIRST, GuiUtilities.getNullListener(), port);
	}

	static IAxisConfigurationMap getAxisConf()
	{
		DefaultAxisConfigurationMap map = new DefaultAxisConfigurationMap();

		map.setAxisConfiguration(Axis.ZFIRST, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Z'", 0.0, 0.0));
		map.setAxisConfiguration(Axis.TWOTHETAFIRST, new AxisConfiguration(DriverUtilities.getDecimalGrades(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "2Theta'", 0.0, 0.0));

		return map;
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

	public void dispose()
	{
		super.dispose();

		this.terminate();
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

	protected void manageOtherActions(ActionEvent event)
	{
	}
}
