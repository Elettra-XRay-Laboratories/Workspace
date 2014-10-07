package com.elettra.lab.powder.diffractometer.panels;

import com.elettra.controller.driver.common.ControllerPosition;
import com.elettra.controller.driver.programs.ProgramParameters;
import com.elettra.controller.gui.common.GuiUtilities;
import com.elettra.lab.powder.diffractometer.Axis;

public class PsiMoveParameters extends ProgramParameters
{

	private ControllerPosition axisZPosition;
	private ControllerPosition axisAlphaPosition;
	private ControllerPosition axisBetaPosition;
	private ControllerPosition axisZPositionOut;
	private ControllerPosition axisAlphaPositionOut;
	private ControllerPosition axisBetaPositionOut;

	public PsiMoveParameters(ControllerPosition axisZPosition, ControllerPosition axisAlphaPosition, ControllerPosition axisBetaPosition, ControllerPosition axisZPositionOut, ControllerPosition axisAlphaPositionOut, ControllerPosition axisBetaPositionOut)
	{
		super(Axis.PSI, GuiUtilities.getNullListener());

		this.axisZPosition = axisZPosition;
		this.axisAlphaPosition = axisAlphaPosition;
		this.axisBetaPosition = axisBetaPosition;
		this.axisZPositionOut = axisZPositionOut;
		this.axisAlphaPositionOut = axisAlphaPositionOut;
		this.axisBetaPositionOut = axisBetaPositionOut;
	}

	public ControllerPosition getAxisZPosition()
	{
		return this.axisZPosition;
	}

	public ControllerPosition getAxisAlphaPosition()
	{
		return this.axisAlphaPosition;
	}

	public ControllerPosition getAxisBetaPosition()
	{
		return this.axisBetaPosition;
	}

	public ControllerPosition getAxisZPositionOut()
	{
		return this.axisZPositionOut;
	}

	public ControllerPosition getAxisAlphaPositionOut()
	{
		return this.axisAlphaPositionOut;
	}

	public ControllerPosition getAxisBetaPositionOut()
	{
		return this.axisBetaPositionOut;
	}
}