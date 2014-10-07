package com.elettra.controller.driver.common;

import com.elettra.common.utilities.StringUtilities;

public abstract class KindOfController
{
	private String kindOfController;

	public KindOfController(String kindOfController)
	{
		StringUtilities.checkString(kindOfController, "kindOfController");
		this.kindOfController = kindOfController;
	}

	public final String getKindOfController()
	{
		return this.kindOfController;
	}
}
