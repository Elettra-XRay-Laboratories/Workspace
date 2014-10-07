package com.elettra.controller.driver.common;

import java.util.StringTokenizer;

public final class AxisConfiguration
{
	private MeasureUnit  measureUnit;
	private int          sts;
	private int          sls;
	private int          ramp;
	private double       stepDistance;
	private boolean      isBrokenAxis;
	private boolean      isRefEnabled;
	private Sign         signToPositive;
	private String       name;
	private boolean      isMultiple;
	private MultipleAxis multipleAxis;
	private int          refSign;
	private double       mtValue;
	private double       referenceShift;
	private boolean      isBlocked;
	private boolean      isLimited;
	private double       limitDown;
	private double       limitUp;

	public AxisConfiguration(String configurationString)
	{
		super();

		this.deserialize(configurationString);
	}

	public AxisConfiguration(MeasureUnit measureUnit, MultipleAxis multipleAxis, String name)
	{
		this(measureUnit, 0, 0, 0, 0, DriverUtilities.getPlus(), false, true, 1, name, 0.0, 0.0);
		this.isMultiple = true;
		this.multipleAxis = multipleAxis;
	}

	public AxisConfiguration(MeasureUnit measureUnit, int sts, int sls, int ramp, double stepDistance, Sign signToPositive, boolean isBrokenAxis, boolean isRefEnabled, int refSign, String name, double mtValue, double referenceShift)
	{
		super();

		this.measureUnit = measureUnit;
		this.sts = sts;
		this.sls = sls;
		this.ramp = ramp;
		this.stepDistance = stepDistance;
		this.signToPositive = signToPositive;
		this.isBrokenAxis = isBrokenAxis;
		this.isRefEnabled = isRefEnabled;
		this.refSign = refSign;
		this.name = name;
		this.isMultiple = false;
		this.multipleAxis = null;
		this.mtValue = mtValue;
		this.referenceShift = referenceShift;
		this.isBlocked = false;
		this.isLimited = false;
		this.limitDown = 0.0;
		this.limitUp = 0.0;
	}

	public MeasureUnit getMeasureUnit()
	{
		return this.measureUnit;
	}

	public int getSts()
	{
		return this.sts;
	}

	public int getSls()
	{
		return this.sls;
	}

	public int getRamp()
	{
		return this.ramp;
	}

	public double getStepDistance()
	{
		return this.stepDistance;
	}

	public Sign getSignToPositive()
	{
		return this.signToPositive;
	}

	public boolean isBrokenAxis()
	{
		return this.isBrokenAxis;
	}

	public boolean isRefEnabled()
	{
		return this.isRefEnabled;
	}

	public int getRefSign()
	{
		return this.refSign;
	}

	public String getName()
	{
		return this.name;
	}

	public double getMtValue()
	{
		return this.mtValue;
	}

	public double getReferenceShift()
	{
		return this.referenceShift;
	}

	public boolean isMultiple()
	{
		return this.isMultiple;
	}

	public MultipleAxis getMultipleAxis()
	{
		return this.multipleAxis;
	}
	
	public boolean isBlocked()
  {
  	return this.isBlocked;
  }

	public boolean isLimited()
  {
  	return this.isLimited;
  }

	public double getLimitDown()
  {
  	return this.limitDown;
  }

	public double getLimitUp()
  {
  	return this.limitUp;
  }
	
	public synchronized void setBlocked(boolean isBlocked)
  {
  	this.isBlocked = isBlocked;
  }

	public synchronized void setLimited(boolean isLimited)
  {
  	this.isLimited = isLimited;
  }

	public synchronized void setLimitDown(double limitDown)
  {
  	this.limitDown = limitDown;
  }

	public synchronized void setLimitUp(double limitUp)
  {
  	this.limitUp = limitUp;
  }

	public String getHuberString()
	{
		return "S" + Integer.toString(this.sts) + "L" + Integer.toString(this.sls) + "B" + Integer.toString(this.ramp);
	}

	private void deserialize(String configurationString)
	{
		StringTokenizer tokenizer = new StringTokenizer(configurationString, ",");

		this.measureUnit = DriverUtilities.parseMeasureUnit(tokenizer.nextToken());
		this.sts = Integer.parseInt(tokenizer.nextToken());
		this.sls = Integer.parseInt(tokenizer.nextToken());
		this.ramp = Integer.parseInt(tokenizer.nextToken());
		this.stepDistance = Double.parseDouble(tokenizer.nextToken());
		this.signToPositive = DriverUtilities.parseSign(tokenizer.nextToken());
		this.isBrokenAxis = Boolean.parseBoolean(tokenizer.nextToken());
		this.isRefEnabled = Boolean.parseBoolean(tokenizer.nextToken());
		this.refSign = Integer.parseInt(tokenizer.nextToken());
		this.name = tokenizer.nextToken();
		this.isMultiple = false;
		this.multipleAxis = null;
		this.mtValue = Double.parseDouble(tokenizer.nextToken());
		this.referenceShift = Double.parseDouble(tokenizer.nextToken());
		this.isBlocked = false;
		this.isLimited = false;
		this.limitDown = 0.0;
		this.limitUp = 0.0;
	}

}
