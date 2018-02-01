package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.comm.SerialPort;

import com.elettra.common.utilities.ISerializable;

public final class SerialPortParameters extends CommunicationPortParameters implements ISerializable
{

	/**
   * 
   */
	private static final long serialVersionUID = -5457112274003305226L;

	private int               baudrate         = 9600;
	private int               databits         = SerialPort.DATABITS_8;
	private int               stopbits         = SerialPort.STOPBITS_1;
	private int               parity           = SerialPort.PARITY_NONE;

	public SerialPortParameters()
	{
		super();
	}

	public SerialPortParameters(int baudrate)
	{
		super();

		this.baudrate = baudrate;
	}

	public String toString()
	{
	 return String.valueOf(this.baudrate) + "," +
			 String.valueOf(this.databits) + "," +
			 String.valueOf(this.stopbits) + "," +
			 String.valueOf(this.parity);
	}
	
	public SerialPortParameters(int baudrate, int databits, int stopbits, int parity)
	{
		this(baudrate);

		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
	}

	public int getBaudrate()
	{
		return this.baudrate;
	}

	public int getDatabits()
	{
		return this.databits;
	}

	public int getStopbits()
	{
		return this.stopbits;
	}

	public int getParity()
	{
		return this.parity;
	}

	public synchronized void serialize(String filePath) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

		try
		{
			writer.write("baudrate="
			    + baudrate);
			writer.newLine();

			writer.write("databits="
			    + databits);
			writer.newLine();

			writer.write("stopbits="
			    + stopbits);
			writer.newLine();

			writer.write("parity="
			    + parity);
			writer.newLine();

			writer.flush();
		}
		finally
		{
			writer.close();
		}
	}

	public synchronized void deserialize(String filePath) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		try
		{
			String baudrateString = reader.readLine();
			String databitsString = reader.readLine();
			String stopbitsString = reader.readLine();
			String parityString = reader.readLine();

			this.baudrate = Integer.parseInt(baudrateString.substring(baudrateString.indexOf("=") + 1));
			this.databits = Integer.parseInt(databitsString.substring(databitsString.indexOf("=") + 1));
			this.stopbits = Integer.parseInt(stopbitsString.substring(stopbitsString.indexOf("=") + 1));
			this.parity = Integer.parseInt(parityString.substring(parityString.indexOf("=") + 1));

		}
		finally
		{
			reader.close();
		}

	}
}
