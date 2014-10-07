package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EthernetPortParameters extends CommunicationPortParameters
{

	private String ipAddress;
	private int    port;

	public EthernetPortParameters()
	{
		super();
	}

	public EthernetPortParameters(String ipAddress, int port)
	{
		super();

		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress()
	{
		return this.ipAddress;
	}

	public int getPort()
	{
		return this.port;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public synchronized void serialize(String filePath) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

		try
		{
			writer.write("ipAddress=" + this.ipAddress);
			writer.newLine();

			writer.write("port=" + this.port);
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
			String ipAddressString = reader.readLine();
			String portString = reader.readLine();

			this.ipAddress = ipAddressString.substring(ipAddressString.indexOf("=") + 1).trim();
			this.port = Integer.parseInt(portString.substring(portString.indexOf("=") + 1));
		}
		finally
		{
			reader.close();
		}

	}

}
