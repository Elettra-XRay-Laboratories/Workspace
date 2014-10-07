package com.elettra.lab.optic.diffractometer.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.InputFieldsException;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.common.MovementListener;
import com.elettra.controller.gui.fit.MovementMatrix;
import com.elettra.lab.optic.diffractometer.Axis;

public class MovementMatrixPanel extends MovementListener implements ActionListener
{
	/**
   * 
   */
	private static final long serialVersionUID = -672002431178150225L;

	static class ActionCommands
	{
		private static final String CALCULATE_MATRIX = "CALCULATE_MATRIX";
		private static final String NEXT_POSITION    = "NEXT_POSITION";
		private static final String EXECUTE_MOVEMENT = "EXECUTE_MOVEMENT";
	}

	private ICommunicationPort port;
	private boolean            isScanActive;

	private JTextField         xFinTextField;
	private JTextField         zFinTextField;
	private JTextField         xInTextField;
	private JTextField         zInTextField;
	private JTextField         rowsTextField;
	private JTextField         columnsTextField;
	private JTextField         xTextField;
	private JTextField         zTextField;
	private JTextField         rowIndexTextField;
	private JTextField         columnIndexTextField;
	private JComboBox<String>  xInSignComboBox;
	private JComboBox<String>  xFinSignComboBox;
	private JComboBox<String>  zInSignComboBox;
	private JComboBox<String>  zFinSignComboBox;
	private JButton            nextPositionButton;
	private JButton            calculateButton;
	private JButton            executeMovementButton;

	private List<Double>       xCoordList;
	private List<Double>       zCoordList;

	private int                currentXIndex;
	private int                currentZIndex;
	private MovementMatrix     currentMovementMatrix;

	private JProgressBar       progressBar;

	public MovementMatrixPanel(ICommunicationPort port) throws CommunicationPortException
	{
		this.isScanActive = false;
		this.port = port;

		this.currentXIndex = -1;
		this.currentZIndex = -1;

		this.xCoordList = new ArrayList<Double>();
		this.zCoordList = new ArrayList<Double>();

		ListenerRegister.getInstance().addListener(Axis.THETA, this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JTabbedPane parametersTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_parametersTabbedPane = new GridBagConstraints();
		gbc_parametersTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_parametersTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_parametersTabbedPane.gridx = 0;
		gbc_parametersTabbedPane.gridy = 0;
		add(parametersTabbedPane, gbc_parametersTabbedPane);

		JPanel parametersPanel = new JPanel();
		parametersPanel.setForeground(Color.BLACK);
		parametersTabbedPane.addTab("Parameters of the Matrix of Movements", null, parametersPanel, null);
		parametersTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
		GridBagLayout gbl_parametersPanel = new GridBagLayout();
		gbl_parametersPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_parametersPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_parametersPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_parametersPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		parametersPanel.setLayout(gbl_parametersPanel);

		JLabel lblMatrixBoundaries = new JLabel("Matrix Boundaries");
		GridBagConstraints gbc_lblMatrixBoundaries = new GridBagConstraints();
		gbc_lblMatrixBoundaries.gridheight = 2;
		gbc_lblMatrixBoundaries.insets = new Insets(10, 5, 5, 10);
		gbc_lblMatrixBoundaries.gridx = 0;
		gbc_lblMatrixBoundaries.gridy = 0;
		parametersPanel.add(lblMatrixBoundaries, gbc_lblMatrixBoundaries);

		JLabel lblXin = new JLabel("Xin");
		GridBagConstraints gbc_lblXin = new GridBagConstraints();
		gbc_lblXin.insets = new Insets(10, 0, 5, 5);
		gbc_lblXin.anchor = GridBagConstraints.EAST;
		gbc_lblXin.gridx = 1;
		gbc_lblXin.gridy = 0;
		parametersPanel.add(lblXin, gbc_lblXin);

		xInSignComboBox = new JComboBox<String>();
		xInSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		xInSignComboBox.setSelectedIndex(0);
		xInSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_xInSignComboBox = new GridBagConstraints();
		gbc_xInSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_xInSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_xInSignComboBox.gridx = 2;
		gbc_xInSignComboBox.gridy = 0;
		parametersPanel.add(xInSignComboBox, gbc_xInSignComboBox);

		xInTextField = new JTextField();
		xInTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		xInTextField.setColumns(10);
		xInTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					xInTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(xInTextField.getText()))));
				}
				catch (Throwable t)
				{
					xInTextField.setText("<not a number>");
				}
			}
		});
		xInTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					xInTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(xInTextField.getText()))));
				}
				catch (Throwable t)
				{
					xInTextField.setText("<not a number>");
				}
			}
		});
		xInTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_xInTextField = new GridBagConstraints();
		gbc_xInTextField.anchor = GridBagConstraints.WEST;
		gbc_xInTextField.insets = new Insets(10, 0, 5, 5);
		gbc_xInTextField.gridx = 3;
		gbc_xInTextField.gridy = 0;
		parametersPanel.add(xInTextField, gbc_xInTextField);

		JLabel lblXfin = new JLabel("Xfin");
		GridBagConstraints gbc_lblXfin = new GridBagConstraints();
		gbc_lblXfin.anchor = GridBagConstraints.EAST;
		gbc_lblXfin.insets = new Insets(10, 0, 5, 5);
		gbc_lblXfin.gridx = 4;
		gbc_lblXfin.gridy = 0;
		parametersPanel.add(lblXfin, gbc_lblXfin);

		xFinSignComboBox = new JComboBox<String>();
		xFinSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		xFinSignComboBox.setSelectedIndex(0);
		xFinSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_xFinSignComboBox = new GridBagConstraints();
		gbc_xFinSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_xFinSignComboBox.insets = new Insets(10, 0, 5, 5);
		gbc_xFinSignComboBox.gridx = 5;
		gbc_xFinSignComboBox.gridy = 0;
		parametersPanel.add(xFinSignComboBox, gbc_xFinSignComboBox);

		xFinTextField = new JTextField();
		xFinTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		xFinTextField.setColumns(10);
		xFinTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					xFinTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(xFinTextField.getText()))));
				}
				catch (Throwable t)
				{
					xFinTextField.setText("<not a number>");
				}
			}
		});
		xFinTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					xFinTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(xFinTextField.getText()))));
				}
				catch (Throwable t)
				{
					xFinTextField.setText("<not a number>");
				}
			}
		});
		xFinTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_xFinTextField = new GridBagConstraints();
		gbc_xFinTextField.anchor = GridBagConstraints.WEST;
		gbc_xFinTextField.insets = new Insets(10, 0, 5, 5);
		gbc_xFinTextField.gridx = 6;
		gbc_xFinTextField.gridy = 0;
		parametersPanel.add(xFinTextField, gbc_xFinTextField);

		JLabel lblZin = new JLabel("Zin");
		GridBagConstraints gbc_lblZin = new GridBagConstraints();
		gbc_lblZin.insets = new Insets(0, 0, 5, 5);
		gbc_lblZin.anchor = GridBagConstraints.EAST;
		gbc_lblZin.gridx = 1;
		gbc_lblZin.gridy = 1;
		parametersPanel.add(lblZin, gbc_lblZin);

		zInSignComboBox = new JComboBox<String>();
		zInSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		zInSignComboBox.setSelectedIndex(0);
		zInSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_zInSignComboBox = new GridBagConstraints();
		gbc_zInSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_zInSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_zInSignComboBox.gridx = 2;
		gbc_zInSignComboBox.gridy = 1;
		parametersPanel.add(zInSignComboBox, gbc_zInSignComboBox);

		zInTextField = new JTextField();
		zInTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		zInTextField.setColumns(10);
		zInTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					zInTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zInTextField.getText()))));
				}
				catch (Throwable t)
				{
					zInTextField.setText("<not a number>");
				}
			}
		});
		zInTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					zInTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zInTextField.getText()))));
				}
				catch (Throwable t)
				{
					zInTextField.setText("<not a number>");
				}
			}
		});
		zInTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_zInTextField = new GridBagConstraints();
		gbc_zInTextField.anchor = GridBagConstraints.WEST;
		gbc_zInTextField.insets = new Insets(0, 0, 5, 5);
		gbc_zInTextField.gridx = 3;
		gbc_zInTextField.gridy = 1;
		parametersPanel.add(zInTextField, gbc_zInTextField);

		JLabel lblZfin = new JLabel("Zfin");
		GridBagConstraints gbc_lblZfin = new GridBagConstraints();
		gbc_lblZfin.anchor = GridBagConstraints.EAST;
		gbc_lblZfin.insets = new Insets(0, 0, 5, 5);
		gbc_lblZfin.gridx = 4;
		gbc_lblZfin.gridy = 1;
		parametersPanel.add(lblZfin, gbc_lblZfin);

		zFinSignComboBox = new JComboBox<String>();
		zFinSignComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "+", "-" }));
		zFinSignComboBox.setSelectedIndex(0);
		zFinSignComboBox.setMaximumRowCount(2);
		GridBagConstraints gbc_zFinSignComboBox = new GridBagConstraints();
		gbc_zFinSignComboBox.anchor = GridBagConstraints.EAST;
		gbc_zFinSignComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_zFinSignComboBox.gridx = 5;
		gbc_zFinSignComboBox.gridy = 1;
		parametersPanel.add(zFinSignComboBox, gbc_zFinSignComboBox);

		zFinTextField = new JTextField();
		zFinTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		zFinTextField.setColumns(10);
		zFinTextField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					zFinTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zFinTextField.getText()))));
				}
				catch (Throwable t)
				{
					zFinTextField.setText("<not a number>");
				}
			}
		});
		zFinTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					zFinTextField.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(zFinTextField.getText()))));
				}
				catch (Throwable t)
				{
					zFinTextField.setText("<not a number>");
				}
			}
		});
		zFinTextField.setText(DriverUtilities.formatControllerPosition(0));
		GridBagConstraints gbc_ZFinTextField = new GridBagConstraints();
		gbc_ZFinTextField.anchor = GridBagConstraints.WEST;
		gbc_ZFinTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ZFinTextField.gridx = 6;
		gbc_ZFinTextField.gridy = 1;
		parametersPanel.add(zFinTextField, gbc_ZFinTextField);

		JLabel lblMatrixDimension = new JLabel("Matrix Dimension");
		GridBagConstraints gbc_lblMatrixDimension = new GridBagConstraints();
		gbc_lblMatrixDimension.insets = new Insets(5, 5, 0, 5);
		gbc_lblMatrixDimension.gridx = 0;
		gbc_lblMatrixDimension.gridy = 2;
		parametersPanel.add(lblMatrixDimension, gbc_lblMatrixDimension);

		JLabel lblRows = new JLabel("rows");
		GridBagConstraints gbc_lblRows = new GridBagConstraints();
		gbc_lblRows.insets = new Insets(5, 0, 0, 5);
		gbc_lblRows.anchor = GridBagConstraints.EAST;
		gbc_lblRows.gridx = 1;
		gbc_lblRows.gridy = 2;
		parametersPanel.add(lblRows, gbc_lblRows);

		rowsTextField = new JTextField();
		rowsTextField.setText("0");
		rowsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_rowsTextField = new GridBagConstraints();
		gbc_rowsTextField.anchor = GridBagConstraints.EAST;
		gbc_rowsTextField.insets = new Insets(5, 0, 0, 5);
		gbc_rowsTextField.gridx = 2;
		gbc_rowsTextField.gridy = 2;
		parametersPanel.add(rowsTextField, gbc_rowsTextField);
		rowsTextField.setColumns(2);

		JLabel lblXColumns = new JLabel("x columns");
		GridBagConstraints gbc_lblXColumns = new GridBagConstraints();
		gbc_lblXColumns.anchor = GridBagConstraints.EAST;
		gbc_lblXColumns.insets = new Insets(5, 0, 0, 5);
		gbc_lblXColumns.gridx = 3;
		gbc_lblXColumns.gridy = 2;
		parametersPanel.add(lblXColumns, gbc_lblXColumns);

		columnsTextField = new JTextField();
		columnsTextField.setText("0");
		columnsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		columnsTextField.setColumns(2);
		GridBagConstraints gbc_columnsTextField = new GridBagConstraints();
		gbc_columnsTextField.anchor = GridBagConstraints.EAST;
		gbc_columnsTextField.insets = new Insets(5, 0, 0, 5);
		gbc_columnsTextField.gridx = 4;
		gbc_columnsTextField.gridy = 2;
		parametersPanel.add(columnsTextField, gbc_columnsTextField);

		calculateButton = new JButton("CALCULATE MATRIX OF MOVEMENTS");
		calculateButton.addActionListener(this);
		calculateButton.setActionCommand(ActionCommands.CALCULATE_MATRIX);
		GridBagConstraints gbc_calculateButton = new GridBagConstraints();
		gbc_calculateButton.insets = new Insets(5, 0, 0, 10);
		gbc_calculateButton.gridx = 7;
		gbc_calculateButton.gridy = 2;
		parametersPanel.add(calculateButton, gbc_calculateButton);

		JTabbedPane movementTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_movementTabbedPane = new GridBagConstraints();
		gbc_movementTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_movementTabbedPane.gridx = 0;
		gbc_movementTabbedPane.gridy = 1;
		add(movementTabbedPane, gbc_movementTabbedPane);

		JPanel movementPanel = new JPanel();
		movementTabbedPane.addTab("Movement of the Motors", null, movementPanel, null);
		GridBagLayout gbl_movementPanel = new GridBagLayout();
		gbl_movementPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_movementPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_movementPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_movementPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		movementPanel.setLayout(gbl_movementPanel);

		JLabel lblRowIndex = new JLabel("row index");
		GridBagConstraints gbc_lblRowIndex = new GridBagConstraints();
		gbc_lblRowIndex.insets = new Insets(10, 5, 5, 5);
		gbc_lblRowIndex.anchor = GridBagConstraints.EAST;
		gbc_lblRowIndex.gridx = 0;
		gbc_lblRowIndex.gridy = 0;
		movementPanel.add(lblRowIndex, gbc_lblRowIndex);

		rowIndexTextField = new JTextField();
		rowIndexTextField.setEditable(false);
		rowIndexTextField.setText("0");
		rowIndexTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		rowIndexTextField.setColumns(3);
		GridBagConstraints gbc_rowIndexTextField = new GridBagConstraints();
		gbc_rowIndexTextField.anchor = GridBagConstraints.EAST;
		gbc_rowIndexTextField.insets = new Insets(10, 0, 5, 5);
		gbc_rowIndexTextField.gridx = 1;
		gbc_rowIndexTextField.gridy = 0;
		movementPanel.add(rowIndexTextField, gbc_rowIndexTextField);

		JLabel lblX = new JLabel("X");
		GridBagConstraints gbc_lblX = new GridBagConstraints();
		gbc_lblX.insets = new Insets(10, 0, 5, 5);
		gbc_lblX.anchor = GridBagConstraints.EAST;
		gbc_lblX.gridx = 2;
		gbc_lblX.gridy = 0;
		movementPanel.add(lblX, gbc_lblX);

		xTextField = new JTextField();
		xTextField.setEditable(false);
		xTextField.setText("0.0000");
		xTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		xTextField.setColumns(10);
		GridBagConstraints gbc_xTextField = new GridBagConstraints();
		gbc_xTextField.anchor = GridBagConstraints.EAST;
		gbc_xTextField.insets = new Insets(10, 0, 5, 5);
		gbc_xTextField.gridx = 3;
		gbc_xTextField.gridy = 0;
		movementPanel.add(xTextField, gbc_xTextField);

		nextPositionButton = new JButton("NEXT POSITION");
		nextPositionButton.addActionListener(this);
		nextPositionButton.setActionCommand(ActionCommands.NEXT_POSITION);
		GridBagConstraints gbc_nextPositionButton = new GridBagConstraints();
		gbc_nextPositionButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_nextPositionButton.insets = new Insets(10, 0, 5, 10);
		gbc_nextPositionButton.gridx = 4;
		gbc_nextPositionButton.gridy = 0;
		movementPanel.add(nextPositionButton, gbc_nextPositionButton);

		JLabel lblColumnIndex = new JLabel("column index");
		GridBagConstraints gbc_lblColumnIndex = new GridBagConstraints();
		gbc_lblColumnIndex.anchor = GridBagConstraints.EAST;
		gbc_lblColumnIndex.insets = new Insets(0, 5, 5, 5);
		gbc_lblColumnIndex.gridx = 0;
		gbc_lblColumnIndex.gridy = 1;
		movementPanel.add(lblColumnIndex, gbc_lblColumnIndex);

		columnIndexTextField = new JTextField();
		columnIndexTextField.setEditable(false);
		columnIndexTextField.setText("0");
		columnIndexTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		columnIndexTextField.setColumns(3);
		GridBagConstraints gbc_columnIndexTextField = new GridBagConstraints();
		gbc_columnIndexTextField.anchor = GridBagConstraints.EAST;
		gbc_columnIndexTextField.insets = new Insets(0, 0, 5, 5);
		gbc_columnIndexTextField.gridx = 1;
		gbc_columnIndexTextField.gridy = 1;
		movementPanel.add(columnIndexTextField, gbc_columnIndexTextField);

		JLabel lblZ = new JLabel("Z");
		GridBagConstraints gbc_lblZ = new GridBagConstraints();
		gbc_lblZ.insets = new Insets(0, 0, 5, 5);
		gbc_lblZ.anchor = GridBagConstraints.EAST;
		gbc_lblZ.gridx = 2;
		gbc_lblZ.gridy = 1;
		movementPanel.add(lblZ, gbc_lblZ);

		zTextField = new JTextField();
		zTextField.setText("0.0000");
		zTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		zTextField.setEditable(false);
		zTextField.setColumns(10);
		GridBagConstraints gbc_zTextField = new GridBagConstraints();
		gbc_zTextField.anchor = GridBagConstraints.EAST;
		gbc_zTextField.insets = new Insets(0, 0, 5, 5);
		gbc_zTextField.gridx = 3;
		gbc_zTextField.gridy = 1;
		movementPanel.add(zTextField, gbc_zTextField);

		executeMovementButton = new JButton("EXECUTE MOVEMENT");
		executeMovementButton.addActionListener(this);
		executeMovementButton.setActionCommand(ActionCommands.EXECUTE_MOVEMENT);
		GridBagConstraints gbc_executeMovementButton = new GridBagConstraints();
		gbc_executeMovementButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_executeMovementButton.insets = new Insets(0, 0, 5, 10);
		gbc_executeMovementButton.gridx = 4;
		gbc_executeMovementButton.gridy = 1;
		movementPanel.add(executeMovementButton, gbc_executeMovementButton);

		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(10, 5, 0, 10);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 5;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 2;
		movementPanel.add(progressBar, gbc_progressBar);
		movementTabbedPane.setForegroundAt(0, new Color(0, 102, 51));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (!this.isScanActive)
		{
			try
			{
				if (e.getActionCommand().equals(ActionCommands.CALCULATE_MATRIX))
				{
					this.manageEventCalculateMatrix();
				}
				else if (e.getActionCommand().equals(ActionCommands.NEXT_POSITION))
				{
					this.manageEventNextPosition();
				}
				else if (e.getActionCommand().equals(ActionCommands.EXECUTE_MOVEMENT))
				{
					this.manageEventExecuteMovement();
				}
			}
			catch (Exception exception)
			{
				GuiUtilities.showErrorPopup(exception.getMessage(), this);
			}
		}
		else
		{
			GuiUtilities.showErrorPopup("It is impossibile to execute the requested action: SCAN ACTIVE", this);
		}
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

	private void manageEventCalculateMatrix()
	{
		StringUtilities.checkString(this.xInTextField.getText(), "Xin");
		StringUtilities.checkString(this.xFinTextField.getText(), "Xfin");
		StringUtilities.checkString(this.zInTextField.getText(), "Zin");
		StringUtilities.checkString(this.zFinTextField.getText(), "Zfin");

		ControllerPosition xIn = new ControllerPosition(DriverUtilities.parseSign((String) this.xInSignComboBox.getSelectedItem()), Double.parseDouble(this.xInTextField.getText()));
		ControllerPosition xFin = new ControllerPosition(DriverUtilities.parseSign((String) this.xFinSignComboBox.getSelectedItem()), Double.parseDouble(this.xFinTextField.getText()));
		ControllerPosition zIn = new ControllerPosition(DriverUtilities.parseSign((String) this.zInSignComboBox.getSelectedItem()), Double.parseDouble(this.zInTextField.getText()));
		ControllerPosition zFin = new ControllerPosition(DriverUtilities.parseSign((String) this.zFinSignComboBox.getSelectedItem()), Double.parseDouble(this.zFinTextField.getText()));

		if (DriverUtilities.controllerToNumber(xIn) >= DriverUtilities.controllerToNumber(xFin))
			throw new InputFieldsException("Xin cannot be greater than or equal to Xfin");

		if (DriverUtilities.controllerToNumber(zIn) >= DriverUtilities.controllerToNumber(zFin))
			throw new InputFieldsException("Zin cannot be greater than or equal to Zfin");

		if (Integer.parseInt(this.rowsTextField.getText()) <= 0)
			throw new InputFieldsException("Rows must be greater than 0");

		if (Integer.parseInt(this.columnsTextField.getText()) <= 0)
			throw new InputFieldsException("Columns must be greater than 0");

		int rows = Integer.parseInt(this.rowsTextField.getText());
		int columns = Integer.parseInt(this.columnsTextField.getText());

		this.currentMovementMatrix = new MovementMatrix(xIn.getSignedPosition(), xFin.getSignedPosition(), zIn.getSignedPosition(), zFin.getSignedPosition(), rows, columns);

		// ---------------------------------------------------------------------

		double xIncrement = Double.valueOf(GuiUtilities.parseDouble(Math.abs(this.currentMovementMatrix.getxMax() - this.currentMovementMatrix.getxMin()) / this.currentMovementMatrix.getRows()));
		double zIncrement = Double.valueOf(GuiUtilities.parseDouble(Math.abs(this.currentMovementMatrix.getzMax() - this.currentMovementMatrix.getzMin()) / this.currentMovementMatrix.getColumns()));

		double xCurrent = 0.0;
		double zCurrent = 0.0;

		this.xCoordList = new ArrayList<Double>(this.currentMovementMatrix.getRows());
		this.zCoordList = new ArrayList<Double>(this.currentMovementMatrix.getColumns());

		for (int row = 0; row < this.currentMovementMatrix.getRows(); row++)
		{
			if (row == this.currentMovementMatrix.getRows() - 1)
				xCurrent = this.currentMovementMatrix.getxMax() - (xIncrement / 2);
			else
				xCurrent = this.currentMovementMatrix.getxMin() + (xIncrement / 2) + row * xIncrement;

			this.xCoordList.add(xCurrent);
		}

		for (int column = 0; column < this.currentMovementMatrix.getColumns(); column++)
		{
			if (column == this.currentMovementMatrix.getColumns() - 1)
				zCurrent = this.currentMovementMatrix.getzMax() - (zIncrement / 2);
			else
				zCurrent = this.currentMovementMatrix.getzMin() + (zIncrement / 2) + column * zIncrement;

			this.zCoordList.add(zCurrent);
		}

		this.currentXIndex = -1;
		this.currentZIndex = -1;

		ListenerRegister.getInstance().signalInitialization(Axis.THETA, this.currentMovementMatrix);

		this.nextPositionButton.setEnabled(true);
		this.executeMovementButton.setEnabled(false);

		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(this.currentMovementMatrix.getRows() * this.currentMovementMatrix.getColumns());
		this.progressBar.setValue(0);
	}

	// ----------------------------------------------------------------------------

	private void manageEventNextPosition()
	{
		this.nextPositionButton.setEnabled(false);
		this.executeMovementButton.setEnabled(true);

		this.currentXIndex++;

		boolean doMove = true;

		// fine X
		if (this.currentXIndex == this.currentMovementMatrix.getRows())
		{
			this.currentXIndex = 0;
			this.currentZIndex++;

			// fine Z
			if (this.currentZIndex == this.currentMovementMatrix.getColumns())
			{
				this.executeMovementButton.setEnabled(false);
				doMove = false;
			}
		}
		else if (this.currentZIndex == -1)
			this.currentZIndex++;

		if (doMove)
		{
			this.rowIndexTextField.setText(Integer.toString(this.currentXIndex));
			this.columnIndexTextField.setText(Integer.toString(this.currentZIndex));

			this.xTextField.setText(GuiUtilities.parseDouble(this.xCoordList.get(this.currentXIndex).doubleValue()));
			this.zTextField.setText(GuiUtilities.parseDouble(this.zCoordList.get(this.currentZIndex).doubleValue()));
		}
	}

	// ----------------------------------------------------------------------------

	private void manageEventExecuteMovement() throws CommunicationPortException
	{
		ControllerPosition xPosition = DriverUtilities.numberToController(this.xCoordList.get(this.currentXIndex).doubleValue());

		MoveParameters moveParameters1 = new MoveParameters(Axis.X, ListenerRegister.getInstance());

		moveParameters1.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters1.setSign(xPosition.getSign());
		moveParameters1.setPosition(xPosition.getAbsolutePosition());

		ControllerPosition zPosition = DriverUtilities.numberToController(this.zCoordList.get(this.currentZIndex).doubleValue());

		MoveParameters moveParameters2 = new MoveParameters(Axis.Z, ListenerRegister.getInstance());

		moveParameters2.setKindOfMovement(DriverUtilities.getAbsolute());
		moveParameters2.setSign(zPosition.getSign());
		moveParameters2.setPosition(zPosition.getAbsolutePosition());

		new ExecuteMovement(this, moveParameters1, moveParameters2, port).start();
	}

	class ExecuteMovement extends Thread
	{
		private MoveParameters      moveParameters1;
		private MoveParameters      moveParameters2;
		private ICommunicationPort  port;
		private MovementMatrixPanel panel;

		public ExecuteMovement(MovementMatrixPanel panel, MoveParameters moveParameters1, MoveParameters moveParameters2, ICommunicationPort port)
		{
			super();

			this.panel = panel;
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

				ListenerRegister.getInstance().signalMotorCoordinate(this.moveParameters1.getAxis(), this.moveParameters1.getSign().sign() * this.moveParameters1.getPosition());

				ProgramsFacade.executeProgram(ProgramsFacade.Programs.MOVE, this.moveParameters2, this.port);
				CommandsFacade.waitForTheEndOfMovement(new CommandParameters(this.moveParameters2.getAxis(), ListenerRegister.getInstance()), this.port);

				ListenerRegister.getInstance().signalMotorCoordinate(this.moveParameters2.getAxis(), this.moveParameters2.getSign().sign() * this.moveParameters2.getPosition());

				panel.progressBar.setValue(panel.progressBar.getValue() + 1);
				panel.executeMovementButton.setEnabled(false);
				panel.nextPositionButton.setEnabled(true);
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

}
