package com.elettra.controller.driver.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.commands.CommandParameters;
import com.elettra.controller.driver.commands.CommandsFacade;
import com.elettra.controller.driver.listeners.IDriverListener;

public final class DriverUtilities
{
	private static IAxisConfigurationMap axisConfigurationMap = null;
	private static KindOfController      kindOfController     = null;

	public static synchronized String getCountingAxis()
	{
		try
		{
			return FileIni.getInstance().getProperty("CountingAxis");
		}
		catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}

	}

	public static synchronized boolean isDebug()
	{
		try
		{
			return Boolean.parseBoolean(FileIni.getInstance().getProperty("Debug"));
		}
		catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}

	}

	public static synchronized boolean isRemoteShutter()
	{
		try
		{
			return Boolean.parseBoolean(FileIni.getInstance().getProperty("RemoteShutter"));
		}
		catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}

	}

	public static synchronized KindOfController getKindOfController()
	{
		try
		{
			if (kindOfController == null)
				kindOfController = parseKindOfController(FileIni.getInstance().getProperty("KindOfController"));

			return kindOfController;
		}
		catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static KindOfController parseKindOfController(String kindOfControllerString)
	{
		StringUtilities.checkString(kindOfControllerString, "kindOfControllerString");

		if (kindOfControllerString.equalsIgnoreCase(HuberController.KIND_OF_CONTROLLER))
			return new HuberController();
		else if (kindOfControllerString.equalsIgnoreCase(GalilController.KIND_OF_CONTROLLER))
			return new GalilController();
		else
			throw new IllegalArgumentException("kindOfController not recognized: " + kindOfControllerString);
	}

	public static KindOfController getHuberController()
	{
		return new HuberController();
	}

	public static KindOfController getGalilController()
	{
		return new GalilController();
	}

	public static synchronized void initialiazeAxisConfigurationMap(IAxisConfigurationMap axisConfigurationMap)
	{
		ObjectUtilities.checkObject(axisConfigurationMap, "axisConfigurationMap");

		DriverUtilities.axisConfigurationMap = axisConfigurationMap;
	}

	public static synchronized IAxisConfigurationMap getAxisConfigurationMap()
	{
		try
		{
			ObjectUtilities.checkObject(axisConfigurationMap, "axisConfigurationMap");
		}
		catch (IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("AxisConfigurationMap not initialized");
		}

		return DriverUtilities.axisConfigurationMap;
	}

	public static String buildHuberCommand(String commandString)
	{
		return commandString + ";" +  (char)0xD +  (char)0xA;
	}

	public static String buildGalilCommand(String commandString)
	{
		return commandString + "\r\n";
	}

	public static Sign parseSign(String signString)
	{
		StringUtilities.checkString(signString, "signString");

		if (signString.equalsIgnoreCase(PlusSign.SIGN))
			return new PlusSign();
		else if (signString.equalsIgnoreCase(MinusSign.SIGN))
			return new MinusSign();
		else
			throw new IllegalArgumentException("Sign not recognized: " + signString);
	}

	public static MeasureUnit parseMeasureUnit(String measureUnit)
	{
		StringUtilities.checkString(measureUnit, "measureUnit");

		if (measureUnit.equalsIgnoreCase(DecimalGrades.DEG))
			return new DecimalGrades();
		else if (measureUnit.equalsIgnoreCase(Millimeters.MM))
			return new Millimeters();
		else if (measureUnit.equalsIgnoreCase(Steps.STEPS))
			return new Steps();
		else
			throw new IllegalArgumentException("Measure Unit not recognized: " + measureUnit);
	}

	public static Sign getPlus()
	{
		return new PlusSign();
	}

	public static Sign getMinus()
	{
		return new MinusSign();
	}

	public static Sign getSignProduct(Sign sign1, Sign sign2)
	{
		if (sign1.sign() * sign2.sign() > 0)
			return new PlusSign();
		else
			return new MinusSign();
	}

	public static KindOfMovement parseKindOfMovement(String kindOfMovementString)
	{
		StringUtilities.checkString(kindOfMovementString, "kindOfMovementString");

		if (kindOfMovementString.equalsIgnoreCase(AbsoluteMovement.KIND_OF_MOVEMENT))
			return new AbsoluteMovement();
		else if (kindOfMovementString.equalsIgnoreCase(RelativeMovement.KIND_OF_MOVEMENT))
			return new RelativeMovement();
		else
			throw new IllegalArgumentException("Kind Of Movement not recognized: " + kindOfMovementString);
	}

	public static KindOfMovement getAbsolute()
	{
		return new AbsoluteMovement();
	}

	public static KindOfMovement getRelative()
	{
		return new RelativeMovement();
	}

	public static MeasureUnit getMillimeters()
	{
		return new Millimeters();
	}

	public static MeasureUnit getDecimalGrades()
	{
		return new DecimalGrades();
	}

	public static final String getGalilAxis(int axis)
	{
		switch (axis)
		{
			case 1:
				return "A";
			case 2:
				return "B";
			case 3:
				return "C";
			case 4:
				return "D";
			case 5:
				return "E";
			case 6:
				return "F";
			case 7:
				return "G";
			case 8:
				return "H";
			default:
				return null;
		}
	}

	public static final int getAxis(String galilAxis)
	{
		if ("A".equals(galilAxis))
			return 1;
		else if ("B".equals(galilAxis))
			return 2;
		else if ("C".equals(galilAxis))
			return 3;
		else if ("D".equals(galilAxis))
			return 4;
		else if ("E".equals(galilAxis))
			return 5;
		else if ("F".equals(galilAxis))
			return 6;
		else if ("G".equals(galilAxis))
			return 7;
		else if ("H".equals(galilAxis))
			return 8;
		else
			return 0;
	}

	public static double getBacklash(MeasureUnit measureUnit)
	{
		ObjectUtilities.checkObject(measureUnit, "measureUnit");

		if (measureUnit.equals(getMillimeters()))
			return 0.5;
		else if (measureUnit.equals(getDecimalGrades()))
			return 0.2;
		else
			return 0.0;
	}

	public static ControllerPosition parseAxisPositionResponse(int axis, String controllerString)
	{
		if (getKindOfController().equals(getHuberController()))
			return parseHuberAxisPosition(controllerString);
		else if (getKindOfController().equals(getGalilController()))
			return parseGalilAxisPosition(axis, controllerString);
		else
			throw new IllegalArgumentException("Kind of Controller not found");
	}

	public static ControllerPosition parseHuberAxisPosition(String controllerString)
	{
		StringTokenizer tokenizer = new StringTokenizer(controllerString, ":");

		if (tokenizer.countTokens() != 2)
			throw new IllegalArgumentException("Huber String not correct : " + controllerString);

		String temporaryString = tokenizer.nextToken();
		temporaryString = tokenizer.nextToken().trim();

		if (temporaryString.endsWith(";"))
			temporaryString = temporaryString.substring(0, temporaryString.length() - 1);

		if (temporaryString.toLowerCase().endsWith(DriverUtilities.getMillimeters().toString()))
			temporaryString = temporaryString.substring(0, temporaryString.length() - 2);
		else if (temporaryString.toLowerCase().endsWith(DriverUtilities.getDecimalGrades().toString()))
			temporaryString = temporaryString.substring(0, temporaryString.length() - 3);
		else
			throw new IllegalArgumentException("Huber String not correct : " + controllerString);

		return DriverUtilities.numberToController(Double.parseDouble(temporaryString.trim()));
	}

	public static ControllerPosition parseGalilAxisPosition(int axis, String controllerString)
	{
		int index = controllerString.lastIndexOf(" ");

		String temporaryString = controllerString.substring(index + 1, controllerString.length());

		Sign signToPositive = getAxisConfigurationMap().getAxisConfiguration(axis).getSignToPositive();

		return DriverUtilities.numberToController(StepConverter.toDistance(axis, (int) (signToPositive.sign() * Double.parseDouble(temporaryString.trim()))));
	}

	public static ControllerPosition numberToController(double signedPosition)
	{
		Sign sign = signedPosition >= 0 ? DriverUtilities.getPlus() : DriverUtilities.getMinus();

		return new ControllerPosition(signedPosition, sign, Math.abs(signedPosition));
	}

	public static double controllerToNumber(ControllerPosition position)
	{
		return position.getSign().sign() * position.getAbsolutePosition();
	}

	public static String formatControllerPositionTextField(int axis, ControllerPosition position)
	{
		return Integer.toString(axis) + ":" + position.getSign().toString() + DriverUtilities.formatControllerPosition(position.getAbsolutePosition()) + DriverUtilities.getAxisConfigurationMap().getAxisConfiguration(axis).getMeasureUnit().toString();
	}

	public static String formatControllerPosition(double absolutePosition)
	{
		if (absolutePosition < 0)
			throw new IllegalArgumentException("Format only absolute positions");

		return formatDoubleNumber(absolutePosition);
	}

	public static String formatDoubleNumber(double number)
	{
		return String.format("%7.4f", number).trim();
	}

	public static synchronized void saveAxisPosition(int axis, String huberString) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(getAxisPositionFileName(axis)));

		writer.write(huberString);
		writer.newLine();
		writer.flush();

		writer.close();
	}

	public static synchronized String getAxisPosition(int axis) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getAxisPositionFileName(axis)));

		String controllerString = reader.readLine();

		reader.close();

		return controllerString;
	}

	public static synchronized void restoreSavedAxisPosition(int axis, IDriverListener listener, ICommunicationPort port) throws IOException, CommunicationPortException
	{
		try
		{
			ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, DriverUtilities.getAxisPosition(axis));

			CommandsFacade.executeCommand(CommandsFacade.Commands.POS, new CommandParameters(axis, position.getSign(), position.getAbsolutePosition(), listener), port);
		}
		catch (FileNotFoundException e)
		{

		}
	}

	public static synchronized AxisConfiguration[] getAxisConfigurationArray() throws IOException
	{
		AxisConfiguration[] axisConfigurationArray = new AxisConfiguration[8];

		BufferedReader reader = new BufferedReader(new FileReader(getAxisConfigurationsFileName()));

		boolean eof = false;

		for (int index = 0; index < axisConfigurationArray.length && !eof; index++)
		{
			String row = reader.readLine();

			eof = row == null || "".equals(row.trim());

			if (!eof)
				axisConfigurationArray[index] = new AxisConfiguration(row);
		}
		reader.close();

		return axisConfigurationArray;
	}

	private static String getAxisConfigurationsFileName()
	{
		return "Files\\Axis\\AxisConfigurations.txt";
	}

	private static String getAxisPositionFileName(int axis)
	{
		String fileName = "Files\\Axis\\PositionAxis" + Integer.toString(axis) + ".txt";
		return fileName;
	}

	// -----------------------------------------------------------------------------------------------
	// EMERGENCY DUMP
	// -----------------------------------------------------------------------------------------------

	public static synchronized void saveEmergencyAxisPosition(int axis, String huberString) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(getEmergencyAxisPositionFileName(axis)));

		writer.write(huberString);
		writer.newLine();
		writer.flush();

		writer.close();
	}

	public static synchronized String getEmergencyAxisPosition(int axis) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getEmergencyAxisPositionFileName(axis)));

		String controllerString = reader.readLine();

		reader.close();

		return controllerString;
	}

	public static synchronized void restoreSavedEmergencyAxisPosition(int axis, IDriverListener listener, ICommunicationPort port) throws IOException, CommunicationPortException
	{
		try
		{
			ControllerPosition position = DriverUtilities.parseAxisPositionResponse(axis, DriverUtilities.getEmergencyAxisPosition(axis));

			CommandsFacade.executeCommand(CommandsFacade.Commands.POS, new CommandParameters(axis, position.getSign(), position.getAbsolutePosition(), listener), port);
		}
		catch (FileNotFoundException e)
		{

		}
	}

	private static String getEmergencyAxisPositionFileName(int axis)
	{
		String fileName = "Files\\Axis\\EMERGENCY_PositionAxis" + Integer.toString(axis) + ".txt";
		return fileName;
	}

}
