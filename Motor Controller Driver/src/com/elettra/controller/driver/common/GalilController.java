package com.elettra.controller.driver.common;

public class GalilController extends KindOfController
{
	public static final String KIND_OF_CONTROLLER = "Galil";

	public GalilController()
	{
		super(KIND_OF_CONTROLLER);
	}

	public boolean equals(Object o)
	{
		return (o instanceof GalilController);
	}

}
