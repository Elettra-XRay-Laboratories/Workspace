package com.elettra.lab.optic.diffractometer.panels;

import java.io.IOException;
import java.util.HashMap;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.controller.gui.common.MovementListener;

class TubeMonocromatorFileManager extends MovementListener
{
	static class Filaments
	{
		public static final String             SILVER     = "Silver";
		public static final String             MOLIBDENUM = "Molibdenum";
		public static final String             TUNGSTEN   = "Tungsten";
		public static final String             CHROME     = "Chrome";
		public static final String             COPPER     = "Copper";

		private static HashMap<String, String> filaments;

		static
		{
			filaments = new HashMap<String, String>();
			filaments.put(TUNGSTEN, "W");
			filaments.put(SILVER, "Ag");
			filaments.put(MOLIBDENUM, "Mo");
			filaments.put(CHROME, "Cr");
			filaments.put(COPPER, "Cu");
		}

		public static String getFilamentMaterial(String filament)
		{
			return filaments.get(filament);
		}
	}

	static class Monochromators
	{
		public static final String             SI_1_1_1 = "Si (1,1,1)";

		private static HashMap<String, String> monochromators;

		static
		{
			monochromators = new HashMap<String, String>();
			monochromators.put(SI_1_1_1, "Si_1_1_1");
		}

		public static String getMonochromator(String monochromator)
		{
			return monochromators.get(monochromator);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -596885298337880312L;

	public TubeMonocromatorFileManager()
	{
	}

	public void signalAxisMovement(int axis, ICommunicationPort port) throws CommunicationPortException
	{
	}

	public void signalScanStart()
	{
	}

	public void signalScanStop()
	{
	}

	protected final String getFileName(String xrayTube, String monochromator) throws IOException
	{
		String fileDir = FileIni.getInstance().getProperty("MonochromatorFileDir");

		String filament = Filaments.getFilamentMaterial(xrayTube);

		if (filament == null)
			throw new IllegalArgumentException("X-Ray Tube not recognized");

		String monochromatorText = Monochromators.getMonochromator(monochromator);

		if (monochromatorText == null)
			throw new IllegalArgumentException("Monocromator not recognized");

		return fileDir + filament + "_" + monochromatorText + ".txt";
	}

}
