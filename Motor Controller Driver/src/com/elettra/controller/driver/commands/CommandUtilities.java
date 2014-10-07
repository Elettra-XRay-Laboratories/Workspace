package com.elettra.controller.driver.commands;

import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.common.DriverUtilities;

public final class CommandUtilities
{
	public static boolean isMotorStopped(String actionResponse)
	{
		StringUtilities.checkString(actionResponse, "actionResponse");

		if (DriverUtilities.getKindOfController().equals(DriverUtilities.getHuberController()))
			return actionResponse.charAt(2) == '1';
		else if (DriverUtilities.getKindOfController().equals(DriverUtilities.getGalilController()))
			return actionResponse.substring(actionResponse.lastIndexOf(" "), actionResponse.length()).charAt(1) == '0';
		else
			return false;
	}
}
