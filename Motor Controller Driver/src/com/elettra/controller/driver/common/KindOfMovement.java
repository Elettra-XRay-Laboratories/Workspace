package com.elettra.controller.driver.common;

public abstract class KindOfMovement
{
	private String kindOfMovement;
	private String kindOfMovementText;

	public KindOfMovement(String kindOfMovement, String kindOfMovementText)
	{
		this.kindOfMovement = kindOfMovement;
		this.kindOfMovementText = kindOfMovementText;
	}

	public final String getKindOfMovement()
	{
		return this.kindOfMovement;
	}

	public final String toString()
	{
		return this.kindOfMovementText;
	}
}
