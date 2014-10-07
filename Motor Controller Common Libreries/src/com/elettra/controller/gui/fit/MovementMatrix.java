package com.elettra.controller.gui.fit;

import com.elettra.controller.gui.common.GuiUtilities;

public final class MovementMatrix
{
	private double xMin;
	private double xMax;
	private double zMin;
	private double zMax;
	private int    rows;
	private int    columns;

	public MovementMatrix(double xMin, double xMax, double zMin, double zMax, int rows, int columns)
	{
		super();

		this.xMin = Double.valueOf(GuiUtilities.parseDouble(xMin));
		this.xMax = Double.valueOf(GuiUtilities.parseDouble(xMax));
		this.zMin = Double.valueOf(GuiUtilities.parseDouble(zMin));
		this.zMax = Double.valueOf(GuiUtilities.parseDouble(zMax));
		this.rows = rows;
		this.columns = columns;
	}

	public double getxMin()
	{
		return this.xMin;
	}

	public double getxMax()
	{
		return this.xMax;
	}

	public double getzMin()
	{
		return this.zMin;
	}

	public double getzMax()
	{
		return this.zMax;
	}

	public int getRows()
	{
		return this.rows;
	}

	public int getColumns()
	{
		return this.columns;
	}
}
