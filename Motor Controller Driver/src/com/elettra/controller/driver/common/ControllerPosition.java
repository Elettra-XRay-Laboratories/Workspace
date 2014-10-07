package com.elettra.controller.driver.common;

import com.elettra.common.utilities.ObjectUtilities;

public class ControllerPosition
{
	private double signedPosition;
	private Sign   sign;
	private double absolutePosition;

	public ControllerPosition()
	{
		this(0, null, 0);
	}

	public ControllerPosition(double signedPosition)
	{
		this(signedPosition, signedPosition >= 0 ? DriverUtilities.getPlus() : DriverUtilities.getMinus(), Math.abs(signedPosition));
	}

	public ControllerPosition(Sign sign, double absolutePosition)
	{
		this(sign.sign() * absolutePosition, sign, absolutePosition);
	}

	public ControllerPosition(double signedPosition, Sign sign, double absolutePosition)
	{
		super();

		this.signedPosition = signedPosition;
		this.sign = sign;
		this.absolutePosition = absolutePosition;
	}

	public double getSignedPosition()
	{
		return this.signedPosition;
	}

	public Sign getSign()
	{
		return this.sign;
	}

	public double getAbsolutePosition()
	{
		return this.absolutePosition;
	}

	public void setSignedPosition(double signedPosition)
	{
		this.signedPosition = signedPosition;
	}

	public void setSign(Sign sign)
	{
		this.sign = sign;
	}

	public void setAbsolutePosition(double absolutePosition)
	{
		this.absolutePosition = absolutePosition;
	}

	public String toString()
	{
		ObjectUtilities.checkObject(this.sign, "sign");

		String out = "signedPosition   : " + Double.toString(this.signedPosition) + "\n";
		out += "sign             : " + this.sign + "\n";
		out += "absolutePosition : " + Double.toString(this.absolutePosition);

		return out;
	}

	public boolean equals(Object o)
	{
		boolean isEqual = false;

		if ((o instanceof ControllerPosition))
		{
			ControllerPosition huberPosition = (ControllerPosition) o;

			isEqual = (this.signedPosition == huberPosition.getSignedPosition()) && (this.sign.equals(huberPosition.getSign())) && (this.absolutePosition == huberPosition.getAbsolutePosition());
		}

		return isEqual;
	}
}
