package com.elettra.controller.gui.fit;

public interface IFitResultListener
{
	public void signalFitResult(int axis, PlottableFitResult fitResult);

	public void signalInitialization(int axis, MovementMatrix matrix);
}
