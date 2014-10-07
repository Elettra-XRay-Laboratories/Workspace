package com.elettra.controller.gui.fit;

public interface IMotorCoordinateListener
{
  public void signalMotorCoordinate(int axis, double position);
  public double getMotorCoordinate(int axis);
}
