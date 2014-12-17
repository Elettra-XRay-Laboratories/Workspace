package com.elettra.lab.metrology.lpt.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.common.DriverUtilities;
import com.elettra.controller.driver.programs.MoveParameters;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.driver.programs.ProgramsFacade;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.controller.gui.common.ListenerRegister;
import com.elettra.controller.gui.panels.MovePanel;
import com.elettra.lab.metrology.lpt.encoder.EncoderReaderFactory;
import com.elettra.lab.metrology.lpt.programs.LPTMOVEProgram;

public final class LPTMovePanel extends MovePanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 768167582649608648L;
	private JTextField	      precision;
	private AbstractButton	  precisionCheckBox;

	/**
	 * Create the panel.
	 * 
	 * @throws CommunicationPortException
	 */
	public LPTMovePanel(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		super(axis, port);

		this.fastMinusButton.setEnabled(false);
		this.fastPlusButton.setEnabled(false);
		this.stepMinusButton.setEnabled(false);
		this.stepPlusButton.setEnabled(false);

		GridBagLayout gbl_panelDown = (GridBagLayout) panelDown.getLayout();
		gbl_panelDown.columnWidths = new int[] { 33, 30, 35, 64, 46, 0 };
		gbl_panelDown.rowHeights = new int[] { 35, 23, 23, 0 };
		gbl_panelDown.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelDown.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };

		JLabel lblPrecision = new JLabel("Prec.");
		GridBagConstraints gbc_lblprecision = new GridBagConstraints();
		gbc_lblprecision.anchor = GridBagConstraints.SOUTH;
		gbc_lblprecision.insets = new Insets(0, 0, 5, 5);
		gbc_lblprecision.gridx = 0;
		gbc_lblprecision.gridy = 2;
		panelDown.add(lblPrecision, gbc_lblprecision);

		precision = new JTextField();
		precision.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					precision.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(precision.getText()))));
				}
				catch (Throwable t)
				{
					precision.setText("<not a number>");
				}
			}
		});
		precision.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					precision.setText(DriverUtilities.formatControllerPosition(Math.abs(Double.parseDouble(precision.getText()))));
				}
				catch (Throwable t)
				{
					precision.setText("<not a number>");
				}
			}
		});

		precision.setText(DriverUtilities.formatControllerPosition(0.001));
		precision.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_precision = new GridBagConstraints();
		gbc_precision.anchor = GridBagConstraints.SOUTHEAST;
		gbc_precision.insets = new Insets(5, 5, 5, 5);
		gbc_precision.gridx = 1;
		gbc_precision.gridy = 2;
		gbc_precision.gridwidth = 2;
		panelDown.add(precision, gbc_precision);
		precision.setColumns(8);

		GridBagConstraints gbc_measureUnit = new GridBagConstraints();
		gbc_measureUnit.anchor = GridBagConstraints.SOUTHWEST;
		gbc_measureUnit.insets = new Insets(0, 0, 5, 0);
		gbc_measureUnit.gridx = 3;
		gbc_measureUnit.gridy = 2;
		panelDown.add(measureUnit, gbc_measureUnit);

		precisionCheckBox = new JCheckBox("");
		precisionCheckBox.setSelected(true);
		precisionCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				precision.setEnabled(precisionCheckBox.isSelected());
			}
		});

		GridBagConstraints gbc_precisionCheckBox = new GridBagConstraints();
		gbc_precisionCheckBox.anchor = GridBagConstraints.WEST;
		gbc_precisionCheckBox.insets = new Insets(5, 0, 5, 5);
		gbc_precisionCheckBox.gridx = 4;
		gbc_precisionCheckBox.gridy = 2;
		panelDown.add(precisionCheckBox, gbc_precisionCheckBox);
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

	public synchronized void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
		new LPTMoveListener(this).start();
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

	protected void manageEventMove() throws CommunicationPortException
	{
		try
		{
			MoveParameters moveParameters = new MoveParameters(this.axis, ListenerRegister.getInstance());

			moveParameters.setKindOfMovement(DriverUtilities.parseKindOfMovement((String) this.kindOfMovementComboBox.getSelectedItem()));
			moveParameters.setSign(DriverUtilities.parseSign((String) this.signComboBox.getSelectedItem()));
			moveParameters.setPosition(Double.parseDouble(this.position.getText()));

			if (precisionCheckBox.isSelected())
				moveParameters.addCustomParameter("PRECISION", Double.parseDouble(this.precision.getText()));

			new LPTStartMoveProgram(moveParameters, this.port).start();
		}
		catch (NumberFormatException exception)
		{
			this.position.setText("<not a number>");

			GuiUtilities.showErrorPopup("Position should be a number (double)", this);
		}
	}

	protected String readAxisPosition() throws CommunicationPortException
	{
		ControllerPosition position = DriverUtilities.numberToController(EncoderReaderFactory.getEncoderReader().readPosition());

		return DriverUtilities.formatControllerPositionTextField(this.axis, position);
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

	class LPTStartMoveProgram extends Thread
	{
		private ProgramParameters		moveParameters;
		private ICommunicationPort	port;

		public LPTStartMoveProgram(ProgramParameters moveParameters, ICommunicationPort port)
		{
			super();
			this.moveParameters = moveParameters;
			this.port = port;
		}

		public void run()
		{
			try
			{
				ProgramsFacade.executeProgram(LPTMOVEProgram.PROGRAM_NAME, this.moveParameters, this.port);
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

	class LPTMoveListener extends Thread
	{
		/**
		 * 
		 */
		private LPTMovePanel	panel;

		public LPTMoveListener(LPTMovePanel panel)
		{
			this.panel = panel;
		}

		public void run()
		{
			try
			{
				ControllerPosition controllerPosition = DriverUtilities.numberToController(EncoderReaderFactory.getEncoderReader().readPosition());

				double previousPosition = controllerPosition.getSignedPosition();
				double currentPosition = previousPosition;

				boolean doCycle = true;

				int trial = 0;

				do
				{
					this.panel.setAxisPositionField(DriverUtilities.formatControllerPositionTextField(axis, controllerPosition));

					ObjectUtilities.pause(200);

					controllerPosition = DriverUtilities.numberToController(EncoderReaderFactory.getEncoderReader().readPosition());

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
}
