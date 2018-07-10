package com.elettra.idsccd.driver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.jfree.data.xy.XYSeries;

import com.elettra.common.fit.FitUtilities;
import com.elettra.common.fit.GaussianFitResult;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

@SuppressWarnings("deprecation")
class IDSCCD implements IIDSCCD
{
	private IntByReference cameraHandle;
	private boolean        isInitialized;

	public IDSCCD()
	{
		this.cameraHandle = new IntByReference(0);
		this.isInitialized = false;
	}

	//----------------------------------------------------------------------------

	public synchronized void init() throws IDSCCDException, IllegalAccessException
	{
		if (!this.isInitialized)
		{
			IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_InitCamera(this.cameraHandle, 0);

			if (result != IDSCCDErrorCodes.IS_SUCCESS)
				throw new IDSCCDException(String.valueOf(result));
		}
		else
			throw new IllegalAccessException("CCD is already initialized");

		this.isInitialized = true;
	}

	//----------------------------------------------------------------------------

	public void setDisplayMode(IDSCCDDisplayModes mode) throws IDSCCDException
	{
		IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_SetDisplayMode(this.cameraHandle.getValue(), mode);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));
	}

	//----------------------------------------------------------------------------

	public boolean isDirect3DSupported()
	{
		IntByReference params = new IntByReference(IDSCCDDisplayModes.IS_SET_DM_DIRECT3D.getIntValue());

		return (IDSCCDWrapper.INSTANCE.is_DirectRenderer(this.cameraHandle.getValue(), IDSCCDDirectRendererModes.DR_GET_SUPPORTED, params, 4) == IDSCCDErrorCodes.IS_SUCCESS);
	}

	//----------------------------------------------------------------------------

	public boolean isOpenGLSupported()
	{
		IntByReference params = new IntByReference(IDSCCDDisplayModes.IS_SET_DM_OPENGL.getIntValue());

		return (IDSCCDWrapper.INSTANCE.is_DirectRenderer(this.cameraHandle.getValue(), IDSCCDDirectRendererModes.DR_GET_SUPPORTED, params, 4) == IDSCCDErrorCodes.IS_SUCCESS);
	}

	//----------------------------------------------------------------------------

	public void setColorMode(IDSCCDColorModes mode) throws IDSCCDException
	{
		IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_SetColorMode(this.cameraHandle.getValue(), mode);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));
	}

	//----------------------------------------------------------------------------

	public int[] getImageByMemory(int dimx, int dimy, IDSCCDColorModes mode) throws IDSCCDException, IOException
	{
		int bitspixel = 0;

		if (mode == IDSCCDColorModes.IS_CM_MONO8 || mode == IDSCCDColorModes.IS_CM_SENSOR_RAW8)
			bitspixel = 8;
		else
			throw new UnsupportedOperationException("Color mode not supported");

		// ---------------------------------------
		// MEMORY AREA ALLOCATION

		PointerByReference imageBuffer = new PointerByReference();
		IntByReference id = new IntByReference();

		IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_AllocImageMem(this.cameraHandle.getValue(), dimx, dimy, bitspixel, imageBuffer, id);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		// ---------------------------------------
		// MEMORY AREA ACTIVATION

		result = IDSCCDWrapper.INSTANCE.is_SetImageMem(this.cameraHandle.getValue(), imageBuffer.getValue(), id.getValue());

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		// ---------------------------------------
		// ACQUISITION

		result = IDSCCDWrapper.INSTANCE.is_FreezeVideo(this.cameraHandle.getValue(), IDSCCDWaitMode.IS_WAIT);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		// ---------------------------------------
		// IMAGE STREAM READING
		//
		// SUPPORTATI SOLO 8 BIT!!!!

		int[] buffer = JNAUtility.fromUnsignedByteArray(imageBuffer.getValue().getByteArray(0, this.getImageSize(this.cameraHandle.getValue(), dimy, bitspixel)));

		// ---------------------------------------
		// MEMORY AREA DEALLOCATION

		result = IDSCCDWrapper.INSTANCE.is_FreeImageMem(this.cameraHandle.getValue(), imageBuffer.getValue(), id.getValue());

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		return buffer;
	}

	//----------------------------------------------------------------------------

	public BufferedImage getImageByFile(String outputPath) throws IDSCCDException, IOException
	{
		IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_FreezeVideo(this.cameraHandle.getValue(), IDSCCDWaitMode.IS_WAIT);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		IDSCCDWrapper.ImageFileParams.ByReference params = new IDSCCDWrapper.ImageFileParams.ByReference();
		params.pwchFileName = new WString(outputPath);
		params.nFileType = IDSCCDImageFileTypes.IS_IMG_JPG.getIntValue();
		params.nQuality = 80;

		result = IDSCCDWrapper.INSTANCE.is_ImageFile(this.cameraHandle.getValue(), IDSCCDImageFileCommands.IS_IMAGE_FILE_CMD_SAVE, params, params.size());

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		return ImageIO.read(new File(outputPath));
	}

	//----------------------------------------------------------------------------

	public Point getCentroid(BufferedImage image, boolean dump) throws OptimizationException, FunctionEvaluationException
	{
		XYSeries[] histograms = this.createHistogramsFromImage(image);

		return this.getCentroid(image.getWidth(), image.getHeight(), histograms, dump);
	}

	//----------------------------------------------------------------------------

	public Point getCentroid(int[] buffer, int dimx, int dimy, boolean dump) throws OptimizationException, FunctionEvaluationException
	{
		XYSeries[] histograms = this.createHistogramsFromMemory(buffer, dimx, dimy);

		return this.getCentroid(dimx, dimy, histograms, dump);
	}

	//----------------------------------------------------------------------------

	public BufferedImage buildImage(int[] buffer, int dimx, int dimy)
	{
		BufferedImage image = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();

		for (int i = 0; i < dimx; i++)
			for (int j = 0; j < dimy; j++)
				raster.setSample(i, j, 0, buffer[i + (dimx * j)]);

		return image;
	}

	//----------------------------------------------------------------------------

	public synchronized void exit() throws IDSCCDException, IllegalAccessException
	{
		if (this.isInitialized)
		{
			IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_ExitCamera(this.cameraHandle.getValue());

			if (result != IDSCCDErrorCodes.IS_SUCCESS)
				throw new IDSCCDException(String.valueOf(result));

		}
		else
			throw new IllegalAccessException("CCD is not initialized");

		this.isInitialized = false;

	}

	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------
	// PRIVATE METHODS
	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------

	private XYSeries[] createHistogramsFromImage(BufferedImage image)
	{
		XYSeries[] histograms = new XYSeries[2];
		histograms[0] = new XYSeries("X Histogram");
		histograms[1] = new XYSeries("Y Histogram");

		int dimx = image.getWidth();
		int dimy = image.getHeight();

		// HISTO X

		for (int xIndex = 0; xIndex < dimx; xIndex++)
		{
			double sum = 0;

			for (int yIndex = 0; yIndex < dimy; yIndex++)
				sum += this.getGray(image.getRGB(xIndex, yIndex));

			histograms[0].add(xIndex, sum);
		}

		for (int yIndex = 0; yIndex < dimy; yIndex++)
		{
			double sum = 0;

			for (int xIndex = 0; xIndex < dimx; xIndex++)
				sum += this.getGray(image.getRGB(xIndex, yIndex));

			histograms[1].add(yIndex, sum);
		}

		return histograms;
	}

	private int getGray(int argb)
	{
		Color color = new Color(argb);

		return (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
	}

	//----------------------------------------------------------------------------

	private XYSeries[] createHistogramsFromMemory(int[] image, int dimx, int dimy)
	{
		XYSeries[] histograms = new XYSeries[2];
		histograms[0] = new XYSeries("X Histogram");
		histograms[1] = new XYSeries("Y Histogram");

		// HISTO X

		for (int xIndex = 0; xIndex < dimx; xIndex++)
		{
			double sum = 0;

			for (int yIndex = 0; yIndex < dimy; yIndex++)
				sum += this.getGray(image[xIndex + (dimx * yIndex)]);

			histograms[0].add(xIndex, sum);
		}

		for (int yIndex = 0; yIndex < dimy; yIndex++)
		{
			double sum = 0;

			for (int xIndex = 0; xIndex < dimx; xIndex++)
				sum += this.getGray(image[xIndex + (dimx * yIndex)]);

			histograms[1].add(yIndex, sum);
		}

		return histograms;
	}

	//----------------------------------------------------------------------------

	private void dumpHistograms(XYSeries[] histograms)
	{
		try
		{
			BufferedWriter writerX = new BufferedWriter(new FileWriter("histoX.txt"));

			for (int index = 0; index < histograms[0].getItemCount(); index++)
			{
				writerX.write(histograms[0].getDataItem(index).getXValue() + " " + histograms[0].getDataItem(index).getYValue());
				writerX.newLine();
			}

			writerX.flush();
			writerX.close();

			BufferedWriter writerY = new BufferedWriter(new FileWriter("histoY.txt"));

			for (int index = 0; index < histograms[1].getItemCount(); index++)
			{
				writerY.write(histograms[1].getDataItem(index).getXValue() + " " + histograms[1].getDataItem(index).getYValue());
				writerY.newLine();
			}

			writerY.flush();
			writerY.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//----------------------------------------------------------------------------

	private Point getCentroid(int dimx, int dimy, XYSeries[] histograms, boolean dump) throws OptimizationException, FunctionEvaluationException
	{
		if (dump)
			this.dumpHistograms(histograms);

		GaussianFitResult resultX = FitUtilities.executeGaussianFit(0, dimx, FitUtilities.Optimizers.LEVENBERG_MARQUARDT, histograms[0]);
		GaussianFitResult resultY = FitUtilities.executeGaussianFit(0, dimy, FitUtilities.Optimizers.LEVENBERG_MARQUARDT, histograms[1]);

		return new Point(resultX.getPosition(), resultY.getPosition());
	}

	//----------------------------------------------------------------------------

	private int getImageSize(int cameraHandle, int height, int bitspixel) throws IDSCCDException
	{
		IntByReference pitch = new IntByReference();

		IDSCCDErrorCodes result = IDSCCDWrapper.INSTANCE.is_GetImageMemPitch(cameraHandle, pitch);

		if (result != IDSCCDErrorCodes.IS_SUCCESS)
			throw new IDSCCDException(String.valueOf(result));

		int nbytes = bitspixel / 8;

		return (pitch.getValue() * height) / nbytes;
	}

	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------
	//----------------------------------------------------------------------------

	public static void main(String args[])
	{
		System.setProperty("jna.library.path", System.getProperty("user.dir") + File.pathSeparator + "dll");

		boolean byMemory = true;

		try
		{
			IDSCCDColorModes mode = IDSCCDColorModes.IS_CM_MONO8;

			IDSCCD ccd = new IDSCCD();

			ccd.init();
			ccd.setColorMode(mode);

			if (byMemory)
				ccd.setDisplayMode(IDSCCDDisplayModes.IS_SET_DM_DIB);
			else if (ccd.isDirect3DSupported())
				ccd.setDisplayMode(IDSCCDDisplayModes.IS_SET_DM_DIRECT3D);
			else
				throw new UnsupportedOperationException();

			BufferedImage capture = null;
			Point centroid = null;

			long t1 = GregorianCalendar.getInstance().getTimeInMillis();

			if (byMemory)
			{
				int dimx = 2448;
				int dimy = 2048;

				int[] buffer = ccd.getImageByMemory(dimx, dimy, mode);
				centroid = ccd.getCentroid(buffer, dimx, dimy, true);

				capture = ccd.buildImage(buffer, dimx, dimy);
			}
			else
			{
				capture = ccd.getImageByFile("temp.jpg");
				centroid = ccd.getCentroid(capture, true);
			}

			long t2 = GregorianCalendar.getInstance().getTimeInMillis();

			Graphics2D g = capture.createGraphics();
			g.setColor(Color.WHITE);
			g.fillOval((int)centroid.x - 3, (int)centroid.y - 3, 6, 6);

			Graphics2D g2 = capture.createGraphics();
			g2.setColor(Color.WHITE);
			g2.drawOval((int)centroid.x - 15, (int)centroid.y - 15, 30, 30);

			ImageIO.write(capture, "jpg", new File("out.jpg"));

			System.out.println("Centroid " + centroid.x + ", " + centroid.y);
			System.out.println("Millescondi " + (t2 - t1));

			ccd.exit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
