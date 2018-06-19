package com.elettra.lab.areadetector.facility;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.EthernetPortParameters;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.KindOfPort;
import com.elettra.common.io.SerialPortParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.IAxisConfigurationMap;
import com.elettra.controller.driver.common.MultipleAxis;
import com.elettra.controller.driver.programs.DefaultAxisConfigurationMap;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.controller.gui.panels.TwoMotorsMovePanel;
import com.elettra.controller.gui.windows.AbstractGenericFrame;

public class MainAreaDetector extends AbstractGenericFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long   serialVersionUID = 2414626770270274046L;
	/**
	 * 
	 */

	private static final String APPLICATION_NAME = "Area Detector Facility";

	static class ActionCommands
	{
		private static final String EXIT = "EXIT";
	}

	/**
	 * 
	 */

	public MainAreaDetector(ICommunicationPort port) throws HeadlessException
	{
		super(APPLICATION_NAME, port);

		try
		{
			this.addWindowFocusListener(new MainWindowAdapter(this));

			this.setBounds(5, 5, 1435, 900);

			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 1400};
			gridBagLayout.rowHeights = new int[] { 0 };
			gridBagLayout.columnWeights = new double[] { 0.0 };
			gridBagLayout.rowWeights = new double[] { 1.0 };
			getContentPane().setLayout(gridBagLayout);

			
			// -----------------------------------------------------------------------------------
			// DETECTOR
			
			JPanel leftPanel = new JPanel();
			GridBagConstraints gbc_leftPanel = new GridBagConstraints();
			gbc_leftPanel.insets = new Insets(10, 0, 0, 5);
			gbc_leftPanel.anchor = GridBagConstraints.WEST;
			gbc_leftPanel.fill = GridBagConstraints.BOTH;
			gbc_leftPanel.gridx = 0;
			gbc_leftPanel.gridy = 0;
			getContentPane().add(leftPanel, gbc_leftPanel);
			GridBagLayout gbl_leftPanel = new GridBagLayout();
			gbl_leftPanel.columnWidths = new int[] { 280, 280, 280, 280, 280 };
			gbl_leftPanel.rowHeights = new int[] { 425, 425 };
			gbl_leftPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
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
			movePanel7.add(new MovePanel(Axis.DETECTOR_ROTATION, this.getPort()));
			
			JPanel movePanel5 = new JPanel();
			movePanel5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel5 = new GridBagConstraints();
			gbc_movePanel5.insets = new Insets(10, 0, 5, 5);
			gbc_movePanel5.fill = GridBagConstraints.BOTH;
			gbc_movePanel5.gridx = 1;
			gbc_movePanel5.gridy = 0;
			leftPanel.add(movePanel5, gbc_movePanel5);
			movePanel5.add(new MovePanel(Axis.DETECTOR_X, this.getPort()));
			
			JPanel movePanel9 = new JPanel();
			movePanel9.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel9 = new GridBagConstraints();
			gbc_movePanel9.insets = new Insets(10, 0, 5, 5);
			gbc_movePanel9.fill = GridBagConstraints.BOTH;
			gbc_movePanel9.gridx = 2;
			gbc_movePanel9.gridy = 0;
			leftPanel.add(movePanel9, gbc_movePanel9);
			movePanel9.add(new TwoMotorsMovePanel(Axis.DETECTOR_Z, this.getPort()));

			JPanel movePanel6 = new JPanel();
			movePanel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel6 = new GridBagConstraints();
			gbc_movePanel6.insets = new Insets(0, 5, 5, 5);
			gbc_movePanel6.fill = GridBagConstraints.BOTH;
			gbc_movePanel6.gridx = 3;
			gbc_movePanel6.gridy = 0;
			leftPanel.add(movePanel6, gbc_movePanel6);
			movePanel6.add(new MovePanel(Axis.DETECTOR_Z1, this.getPort()));
      
			JPanel movePanel8 = new JPanel();
			movePanel8.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel8 = new GridBagConstraints();
			gbc_movePanel8.insets = new Insets(0, 0, 5, 5);
			gbc_movePanel8.fill = GridBagConstraints.BOTH;
			gbc_movePanel8.gridx = 4;
			gbc_movePanel8.gridy = 0;
			leftPanel.add(movePanel8, gbc_movePanel8);
			movePanel8.add(new MovePanel(Axis.DETECTOR_Z2, this.getPort()));
		
			// ---------------------------------------------------------------------------
			// SAMPLE HOLDER
			
			JPanel movePanel20 = new JPanel();
			movePanel20.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel20 = new GridBagConstraints();
			gbc_movePanel20.insets = new Insets(10, 5, 5, 5);
			gbc_movePanel20.fill = GridBagConstraints.BOTH;
			gbc_movePanel20.gridx = 0;
			gbc_movePanel20.gridy = 1;
			leftPanel.add(movePanel20, gbc_movePanel20);
			movePanel20.add(new MovePanel(Axis.SAMPLE_X, this.getPort()));


			JPanel movePanel21 = new JPanel();
			movePanel21.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel21 = new GridBagConstraints();
			gbc_movePanel21.insets = new Insets(10, 0, 5, 5);
			gbc_movePanel21.fill = GridBagConstraints.BOTH;
			gbc_movePanel21.gridx = 1;
			gbc_movePanel21.gridy = 1;
			leftPanel.add(movePanel21, gbc_movePanel21);
			movePanel21.add(new MovePanel(Axis.SAMPLE_Y, this.getPort()));


			JPanel movePanel22 = new JPanel();
			movePanel22.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel22 = new GridBagConstraints();
			gbc_movePanel22.insets = new Insets(10, 0, 5, 5);
			gbc_movePanel22.fill = GridBagConstraints.BOTH;
			gbc_movePanel22.gridx = 2;
			gbc_movePanel22.gridy = 1;
			leftPanel.add(movePanel22, gbc_movePanel22);
			movePanel22.add(new MovePanel(Axis.SAMPLE_Z, this.getPort()));


			JPanel movePanel23 = new JPanel();
			movePanel23.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			GridBagConstraints gbc_movePanel23 = new GridBagConstraints();
			gbc_movePanel23.insets = new Insets(10, 5, 5, 5);
			gbc_movePanel23.fill = GridBagConstraints.BOTH;
			gbc_movePanel23.gridx = 3;
			gbc_movePanel23.gridy = 1;
			leftPanel.add(movePanel23, gbc_movePanel23);
			movePanel23.add(new MovePanel(Axis.SAMPLE_W, this.getPort()));

			// ---------------------------------------------------------------------------
			// STATUS
			
			JPanel lateralPanel = new JPanel();
			GridBagConstraints gbc_lateralPanel = new GridBagConstraints();
			gbc_lateralPanel.insets = new Insets(0, 0, 0, 5);
			gbc_lateralPanel.anchor = GridBagConstraints.WEST;
			gbc_lateralPanel.fill = GridBagConstraints.BOTH;
			gbc_lateralPanel.gridx = 4;
			gbc_lateralPanel.gridy = 1;
			leftPanel.add(lateralPanel, gbc_lateralPanel);
			GridBagLayout gbl_lateralPanel = new GridBagLayout();
			gbl_lateralPanel.columnWidths = new int[] { 0 };
			gbl_lateralPanel.rowHeights = new int[] { 0, 0 };
			gbl_lateralPanel.columnWeights = new double[] { 1.0 };
			gbl_lateralPanel.rowWeights = new double[] { 1.0, 1.0 };
			lateralPanel.setLayout(gbl_lateralPanel);

			JTabbedPane statusTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_statusTabbedPane = new GridBagConstraints();
			gbc_statusTabbedPane.insets = new Insets(10, 5, 5, 5);
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


			JButton exitButton = new JButton("EXIT");
			exitButton.setActionCommand(ActionCommands.EXIT);
			exitButton.addActionListener(this);
			GridBagConstraints gbc_exitButton = new GridBagConstraints();
			gbc_exitButton.fill = GridBagConstraints.BOTH;
			gbc_exitButton.insets = new Insets(250, 0, 5, 5);
			gbc_exitButton.gridx = 0;
			gbc_exitButton.gridy = 1;
			lateralPanel.add(exitButton, gbc_exitButton);
			
			
		}
		catch (Exception e)
		{
			GuiUtilities.showErrorPopup("Exception captured: " + e.getClass().getName() + " - " + e.getMessage(), (JPanel) this.getContentPane().getComponent(0));
		}
	}

	private class MainWindowAdapter extends WindowAdapter
	{
		private MainAreaDetector main;

		public MainWindowAdapter(MainAreaDetector main)
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

		if (kindOfPort.equals(CommunicationPortUtilies.getSerialPort64()) || kindOfPort.equals(CommunicationPortUtilies.getSerialPort()))
		{
			String portName = GuiUtilities.getPortNames().listIterator().next();

			port = CommunicationPortFactory.getPort(portName, kindOfPort);

			SerialPortParameters parameters = new SerialPortParameters();
			parameters.deserialize(GuiUtilities.getPortConfFileName(portName));

			port.initialize(parameters);
		}
		else if (kindOfPort.equals(CommunicationPortUtilies.getEthernetPort()))
		{
			port = CommunicationPortFactory.getPort("Eth1", kindOfPort);

			EthernetPortParameters parameters = new EthernetPortParameters();
			parameters.deserialize(GuiUtilities.getPortConfFileName("Eth1"));

			port.initialize(parameters);
		}

		return port;
	}

	static void restoreSavedAxisPosition(ICommunicationPort port) throws IOException, CommunicationPortException
	{
		DriverUtilities.restoreSavedAxisPosition(Axis.DETECTOR_ROTATION, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.DETECTOR_X, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.DETECTOR_Z1, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.DETECTOR_Z1, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.SAMPLE_X, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.SAMPLE_Y, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.SAMPLE_Z, GuiUtilities.getNullListener(), port);
		DriverUtilities.restoreSavedAxisPosition(Axis.SAMPLE_W, GuiUtilities.getNullListener(), port);
	}

	static IAxisConfigurationMap getAxisConf()
	{
		DefaultAxisConfigurationMap map = new DefaultAxisConfigurationMap();

		map.setAxisConfiguration(Axis.DETECTOR_ROTATION, new AxisConfiguration(DriverUtilities.getDecimalGrades(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Detector \u03c9", 0.0, 0.0));
		map.setAxisConfiguration(Axis.DETECTOR_X, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Detector X", 0.0, 0.0));
		map.setAxisConfiguration(Axis.DETECTOR_Z1, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Detector Z1", 0.0, 0.0));
		map.setAxisConfiguration(Axis.DETECTOR_Z2, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Detector Z2", 0.0, 0.0));
		map.setAxisConfiguration(Axis.DETECTOR_Z, new AxisConfiguration(DriverUtilities.getMillimeters(), new MultipleAxis(Axis.DETECTOR_Z1, Axis.DETECTOR_Z2, DriverUtilities.getPlus(), 2), "Detector Z (Z1,Z2)"));

		map.setAxisConfiguration(Axis.SAMPLE_X, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Sample X", 0.0, 0.0));
		map.setAxisConfiguration(Axis.SAMPLE_Y, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Sample Y", 0.0, 0.0));
		map.setAxisConfiguration(Axis.SAMPLE_Z, new AxisConfiguration(DriverUtilities.getMillimeters(), 1000, 2000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Sample Z", 0.0, 0.0));
		map.setAxisConfiguration(Axis.SAMPLE_W, new AxisConfiguration(DriverUtilities.getDecimalGrades(), 2000, 5000, 33, 0, DriverUtilities.getPlus(), false, false, 1, "Sample \u03c9", 0.0, 0.0));
		
		
		return map;
	}
	
	static void restoreAxisConfiguration()
	{
		
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
