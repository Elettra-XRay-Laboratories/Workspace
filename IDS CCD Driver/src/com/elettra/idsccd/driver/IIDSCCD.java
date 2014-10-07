package com.elettra.idsccd.driver;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;

@SuppressWarnings("deprecation")
public interface IIDSCCD
{
	public static final double PIXEL_SIZE = 3.3;

	public abstract void exit() throws IDSCCDException, IllegalAccessException;

	public abstract BufferedImage buildImage(int[] buffer, int dimx, int dimy);

	public abstract Point getCentroid(int[] buffer, int dimx, int dimy, boolean dump) throws OptimizationException, FunctionEvaluationException;

	public abstract Point getCentroid(BufferedImage image, boolean dump) throws OptimizationException, FunctionEvaluationException;

	public abstract BufferedImage getImageByFile(String outputPath) throws IDSCCDException, IOException;

	public abstract int[] getImageByMemory(int dimx, int dimy, IDSCCDColorModes mode) throws IDSCCDException, IOException;

	public abstract void setColorMode(IDSCCDColorModes mode) throws IDSCCDException;

	public abstract boolean isOpenGLSupported();

	public abstract boolean isDirect3DSupported();

	public abstract void setDisplayMode(IDSCCDDisplayModes mode) throws IDSCCDException;

	public abstract void init() throws IDSCCDException, IllegalAccessException;

}
