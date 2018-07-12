package com.elettra.idsccd.driver;

public class Point
{
	public double x;
	public double y;
	public double fwhm_x;
	public double fwhm_y;
	
	public Point(double x, double y)
	{
		this(x, y, 0.0, 0.0);
	}
	
	public Point(double x, double y, double fwhm_x, double fwhm_y)
	{
		this.x = x;
		this.y = y;	
		this.fwhm_x = fwhm_x;
		this.fwhm_y = fwhm_y;	
	}
}
