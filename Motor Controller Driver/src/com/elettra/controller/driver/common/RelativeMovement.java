package com.elettra.controller.driver.common;

public class RelativeMovement extends KindOfMovement
{
	public static final String KIND_OF_MOVEMENT = "R";
	public static final String KIND_OF_MOVEMENT_TEXT = "Relative";

	public RelativeMovement()
	{
		super(KIND_OF_MOVEMENT, KIND_OF_MOVEMENT_TEXT);
	}

	public boolean equals(Object o)
	{
		return (o instanceof RelativeMovement);
	}

}
