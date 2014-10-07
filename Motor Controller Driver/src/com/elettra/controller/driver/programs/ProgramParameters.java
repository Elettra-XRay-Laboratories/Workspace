package com.elettra.controller.driver.programs;

import java.util.HashMap;

import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.listeners.IDriverListener;

public abstract class ProgramParameters
{
	private int                     axis;
	private IDriverListener         listener;

	private HashMap<String, Object> customParameters;

	public ProgramParameters(int axis, IDriverListener listener)
	{
		ObjectUtilities.checkObject(listener, "listener");

		this.axis = axis;
		this.listener = listener;
		this.customParameters = new HashMap<String, Object>();

	}

	public final int getAxis()
	{
		return axis;
	}

	public final IDriverListener getListener()
	{
		return this.listener;
	}

	public synchronized void addCustomParameter(String key, Object data)
	{
		this.customParameters.put(key, data);
	}

	public Object getCustomParameter(String key)
	{
		return this.customParameters.get(key);
	}

	public synchronized void setCustomParameter(HashMap<String, Object> customParameters)
	{
		this.customParameters = customParameters;
	}

	public HashMap<String, Object> getCustomParameters()
	{
		return this.customParameters;
	}

}
