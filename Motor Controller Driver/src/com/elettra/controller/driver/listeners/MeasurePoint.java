package com.elettra.controller.driver.listeners;

import java.util.HashMap;
import java.util.StringTokenizer;

import com.elettra.common.utilities.StringUtilities;

public class MeasurePoint
{
	private double                  x;
	private double                  measure;
	double                          additionalInformation1;
	double                          additionalInformation2;

	private HashMap<String, Object> customData;

	public MeasurePoint(double x, double measure)
	{
		this(x, measure, 0.0, 0.0);
	}

	public MeasurePoint(double x, double measure, double additionalInformation1, double additionalInformation2)
	{
		super();
		this.x = x;
		this.measure = measure;
		this.additionalInformation1 = additionalInformation1;
		this.additionalInformation2 = additionalInformation2;
		this.customData = null;
	}

	public MeasurePoint(String fileRow)
	{
		this(0, 0, 0.0, 0.0);

		StringUtilities.checkString(fileRow, "fileRow");

		StringTokenizer tokenizer = new StringTokenizer(fileRow, " ");

		if (!(tokenizer.countTokens() == 2 || tokenizer.countTokens() == 4))
			throw new IllegalArgumentException("fileRow must contain 2 or 4 tokens separated by a space");

		boolean addInfo = (tokenizer.countTokens() == 4);

		this.x = Double.parseDouble(tokenizer.nextToken());
		this.measure = Double.parseDouble(tokenizer.nextToken());

		if (addInfo)
		{
			this.additionalInformation1 = Double.parseDouble(tokenizer.nextToken());
			this.additionalInformation2 = Double.parseDouble(tokenizer.nextToken());
		}
	}

	public double getX()
	{
		return this.x;
	}

	public double getMeasure()
	{
		return this.measure;
	}

	public synchronized void setX(double x)
	{
		this.x = x;
	}

	public synchronized void setCounts(int counts)
	{
		this.measure = counts;
	}

	public double getAdditionalInformation1()
	{
		return this.additionalInformation1;
	}

	public double getAdditionalInformation2()
	{
		return this.additionalInformation2;
	}

	public void setAdditionalInformation1(double additionalInformation1)
	{
		this.additionalInformation1 = additionalInformation1;
	}

	public void setAdditionalInformation2(double additionalInformation2)
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

	public synchronized void setCustomData(HashMap<String, Object> customData)
	{
		this.customData = customData;
	}

	public HashMap<String, Object> getCustomData()
	{
		return this.customData;
	}

}
