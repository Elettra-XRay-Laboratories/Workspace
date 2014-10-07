package com.elettra.lab.optic.diffractometer.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.lab.optic.diffractometer.Axis;

public class SaveBraggAnglesPanel extends TubeMonocromatorFileManager implements ActionListener
{
	static class ActionCommands
	{
		private static final String SAVE = "SAVE";
	}

	/**
	 * 
	 */
	private static final long  serialVersionUID = -959332255677691218L;

	private ICommunicationPort port;

	private JTextField         axis1TextField;
	private JTextField         axis2TextField;
	private JComboBox<String>  monocromatorComboBox;
	private JComboBox<String>  xrayTubeComboBox;

	private JButton            saveSettingsButton;

	/**
	 * Create the panel.
	 */
	public SaveBraggAnglesPanel(ICommunicationPort port)
	{
		this.port = port;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 240, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Commands", null, panel, null);
		tabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblAxis = new JLabel("Axis 1");
		GridBagConstraints gbc_lblAxis = new GridBagConstraints();
		gbc_lblAxis.anchor = GridBagConstraints.EAST;
		gbc_lblAxis.insets = new Insets(10, 5, 5, 5);
		gbc_lblAxis.gridx = 0;
		gbc_lblAxis.gridy = 0;
		panel.add(lblAxis, gbc_lblAxis);

		axis1TextField = new JTextField();
		axis1TextField.setHorizontalAlignment(SwingConstants.CENTER);
		axis1TextField.setText("Theta'");
		axis1TextField.setBackground(new Color(0, 102, 51));
		axis1TextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		axis1TextField.setForeground(new Color(255, 255, 255));
		axis1TextField.setEditable(false);
		GridBagConstraints gbc_axis1TextField = new GridBagConstraints();
		gbc_axis1TextField.anchor = GridBagConstraints.WEST;
		gbc_axis1TextField.insets = new Insets(10, 10, 5, 0);
		gbc_axis1TextField.gridx = 1;
		gbc_axis1TextField.gridy = 0;
		panel.add(axis1TextField, gbc_axis1TextField);
		axis1TextField.setColumns(5);

		JLabel lblAxis_1 = new JLabel("Axis 2");
		GridBagConstraints gbc_lblAxis_1 = new GridBagConstraints();
		gbc_lblAxis_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblAxis_1.anchor = GridBagConstraints.EAST;
		gbc_lblAxis_1.gridx = 0;
		gbc_lblAxis_1.gridy = 1;
		panel.add(lblAxis_1, gbc_lblAxis_1);

		axis2TextField = new JTextField();
		axis2TextField.setText("2Theta'");
		axis2TextField.setHorizontalAlignment(SwingConstants.CENTER);
		axis2TextField.setForeground(Color.WHITE);
		axis2TextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		axis2TextField.setEditable(false);
		axis2TextField.setColumns(5);
		axis2TextField.setBackground(new Color(0, 102, 51));
		GridBagConstraints gbc_axis2TextField = new GridBagConstraints();
		gbc_axis2TextField.anchor = GridBagConstraints.WEST;
		gbc_axis2TextField.insets = new Insets(0, 10, 5, 0);
		gbc_axis2TextField.gridx = 1;
		gbc_axis2TextField.gridy = 1;
		panel.add(axis2TextField, gbc_axis2TextField);

		JLabel lblXrayTube = new JLabel("X-Ray Tube");
		GridBagConstraints gbc_lblXrayTube = new GridBagConstraints();
		gbc_lblXrayTube.anchor = GridBagConstraints.EAST;
		gbc_lblXrayTube.insets = new Insets(5, 5, 5, 5);
		gbc_lblXrayTube.gridx = 0;
		gbc_lblXrayTube.gridy = 2;
		panel.add(lblXrayTube, gbc_lblXrayTube);

		xrayTubeComboBox = new JComboBox<String>();
		xrayTubeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { Filaments.CHROME, Filaments.COPPER, Filaments.MOLIBDENUM, Filaments.SILVER, Filaments.TUNGSTEN }));
		xrayTubeComboBox.setMaximumRowCount(5);
		GridBagConstraints gbc_xrayTubeComboBox = new GridBagConstraints();
		gbc_xrayTubeComboBox.anchor = GridBagConstraints.WEST;
		gbc_xrayTubeComboBox.insets = new Insets(0, 10, 5, 0);
		gbc_xrayTubeComboBox.gridx = 1;
		gbc_xrayTubeComboBox.gridy = 2;
		panel.add(xrayTubeComboBox, gbc_xrayTubeComboBox);

		JLabel lblMonocromator = new JLabel("Monocromator");
		GridBagConstraints gbc_lblMonocromator = new GridBagConstraints();
		gbc_lblMonocromator.anchor = GridBagConstraints.EAST;
		gbc_lblMonocromator.insets = new Insets(0, 5, 5, 5);
		gbc_lblMonocromator.gridx = 0;
		gbc_lblMonocromator.gridy = 3;
		panel.add(lblMonocromator, gbc_lblMonocromator);

		monocromatorComboBox = new JComboBox<String>();
		monocromatorComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { Monochromators.SI_1_1_1 }));
		monocromatorComboBox.setMaximumRowCount(1);
		GridBagConstraints gbc_monocromatorComboBox = new GridBagConstraints();
		gbc_monocromatorComboBox.anchor = GridBagConstraints.WEST;
		gbc_monocromatorComboBox.insets = new Insets(0, 10, 5, 0);
		gbc_monocromatorComboBox.gridx = 1;
		gbc_monocromatorComboBox.gridy = 3;
		panel.add(monocromatorComboBox, gbc_monocromatorComboBox);

		saveSettingsButton = new JButton("SAVE SETTINGS");
		saveSettingsButton.setActionCommand(ActionCommands.SAVE);
		saveSettingsButton.addActionListener(this);
		GridBagConstraints gbc_saveSettingsButton = new GridBagConstraints();
		gbc_saveSettingsButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_saveSettingsButton.insets = new Insets(10, 5, 5, 5);
		gbc_saveSettingsButton.gridwidth = 2;
		gbc_saveSettingsButton.gridx = 0;
		gbc_saveSettingsButton.gridy = 4;
		panel.add(saveSettingsButton, gbc_saveSettingsButton);

	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ActionCommands.SAVE))
		{
			try
			{
				this.manageEventSaveSettings();
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}

	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		this.xrayTubeComboBox.setEnabled(enabled);
		this.monocromatorComboBox.setEnabled(enabled);
		this.saveSettingsButton.setEnabled(enabled);
		this.axis1TextField.setEnabled(enabled);
		this.axis2TextField.setEnabled(enabled);
	}

	private void manageEventSaveSettings() throws IOException
	{
		String xrayTube = (String) this.xrayTubeComboBox.getSelectedItem();
		String monocromator = (String) this.monocromatorComboBox.getSelectedItem();

		String fileName = this.getFileName(xrayTube, monocromator);

		ControllerPosition position1 = DriverUtilities.parseAxisPositionResponse(Axis.THETAFIRST, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.THETAFIRST, GuiUtilities.getNullListener()), this.port));

		String thetaFirst = DriverUtilities.formatControllerPositionTextField(Axis.THETAFIRST, position1);

		ControllerPosition position2 = DriverUtilities.parseAxisPositionResponse(Axis.TWOTHETAFIRST, CommandsFacade.executeAction(CommandsFacade.Actions.REQUEST_AXIS_POSITION, new CommandParameters(Axis.TWOTHETAFIRST, GuiUtilities.getNullListener()), this.port));

		String twoThetaFirst = DriverUtilities.formatControllerPositionTextField(Axis.TWOTHETAFIRST, position2);

		if (GuiUtilities.showConfirmPopup("Confirm Saving of the following settings?\n\nTheta'       : " + thetaFirst + "\n2Theta'      : " + twoThetaFirst + "\nX-Ray Tube   : " + xrayTube + "\nMonocromator : " + monocromator + "\n ", this))
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			try
			{
				writer.write(thetaFirst + "&" + twoThetaFirst);
				writer.newLine();
				writer.flush();
			}
			finally
			{
				writer.close();
			}
		}
	}

}
