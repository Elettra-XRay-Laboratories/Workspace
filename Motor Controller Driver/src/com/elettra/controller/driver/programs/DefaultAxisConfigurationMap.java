package com.elettra.controller.driver.programs;

import java.util.HashMap;

import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.controller.driver.common.AxisConfiguration;
import com.elettra.controller.driver.common.IAxisConfigurationMap;
import com.elettra.controller.driver.common.UnexistingAxisConfigurationException;

public final class DefaultAxisConfigurationMap implements IAxisConfigurationMap
{
	private HashMap<Integer, AxisConfiguration> axisConfigurationMap;
  
	public DefaultAxisConfigurationMap()
	{
		this.axisConfigurationMap = new HashMap<Integer, AxisConfiguration>();
	}

	public synchronized void setAxisConfiguration(int axis, AxisConfiguration configuration)
	{
		ObjectUtilities.checkObject(configuration, "configuration");
		
		this.axisConfigurationMap.put(Integer.valueOf(axis), configuration);
	}
	
	public synchronized AxisConfiguration getAxisConfiguration(int axis)
  {
		if (!this.axisConfigurationMap.containsKey(Integer.valueOf(axis)))
			throw new UnexistingAxisConfigurationException(axis);
		
	  return this.axisConfigurationMap.get(Integer.valueOf(axis));
  }	
}
