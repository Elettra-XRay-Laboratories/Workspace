package com.elettra.controller.driver.programs;

import java.util.HashMap;

public class MeasureResult extends ProgramResult
{
	double                          measure;
	double                          additionalInformation1;
	double                          additionalInformation2;

	private HashMap<String, Object> customData;

	public MeasureResult(double measure)
	{
		this(measure, 0.0, 0.0);

		this.customData = new HashMap<String, Object>();
	}

	public MeasureResult(double measure, double additionalInformation1, double additionalInformation2)
	{
		super();
		this.measure = measure;
		this.additionalInformation1 = additionalInformation1;
		this.additionalInformation2 = additionalInformation2;
	}

	public double getMeasure()
	{
		return this.measure;
	}

	public double getAdditionalInformation1()
	{
		return this.additionalInformation1;
	}

	public double getAdditionalInformation2()
	{
		return this.additionalInformation2;
	}

	public synchronized void setAdditionalInformation1(double additionalInformation1)
	{
		this.additionalInformation1 = additionalInformation1;
	}

	public synchronized void setAdditionalInformation2(double additionalInformation2)
	{
		this.additionalInformation2 = additionalInformation2;
	}

	public synchronized void addCustomData(String key, Object data)
	{
		this.customData.put(key, data);
	}

	public Object getCustomData(String key)
	{
		return this.customData.get(key);
	}

	public HashMap<String, Object> getCustomData()
	{
		return this.customData;
	}

}
