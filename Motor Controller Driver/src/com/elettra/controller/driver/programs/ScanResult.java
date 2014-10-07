package com.elettra.controller.driver.programs;

import java.util.ArrayList;
import java.util.List;

import com.elettra.controller.driver.listeners.MeasurePoint;

public class ScanResult extends ProgramResult
{
	private List<MeasurePoint> data;

	public ScanResult()
	{
		this(new ArrayList<MeasurePoint>());
	}

	public ScanResult(List<MeasurePoint> data)
	{
		this.data = data;
	}

	public synchronized void addMeasurePoint(MeasurePoint point)
	{
		this.data.add(point);
	}
	
	public List<MeasurePoint> getData()
	{
		return this.data;
	}
}
