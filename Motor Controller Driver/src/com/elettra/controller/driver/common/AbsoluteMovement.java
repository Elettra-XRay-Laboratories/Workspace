package com.elettra.controller.driver.common;

public class AbsoluteMovement extends KindOfMovement
{
	public static final String KIND_OF_MOVEMENT = "A";
	public static final String KIND_OF_MOVEMENT_TEXT = "Absolute";

	public AbsoluteMovement()
	{
		super(KIND_OF_MOVEMENT, KIND_OF_MOVEMENT_TEXT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof AbsoluteMovement);
	}

}
