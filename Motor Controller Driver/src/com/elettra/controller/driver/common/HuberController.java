package com.elettra.controller.driver.common;

public class HuberController extends KindOfController
{
	public static final String KIND_OF_CONTROLLER = "Huber";

	public HuberController()
	{
		super(KIND_OF_CONTROLLER);
	}

	public boolean equals(Object o)
	{
		return (o instanceof HuberController);
	}

}
