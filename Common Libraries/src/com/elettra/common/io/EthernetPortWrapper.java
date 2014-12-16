package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.elettra.common.utilities.StringUtilities;

public class EthernetPortWrapper implements ICommunicationPort
{
	private Socket	       tcpipSocket	= null;
	private PrintWriter	   outputStream	= null;
	private BufferedReader	inputStream	= null;

	private String	       portName;

	public EthernetPortWrapper(String portName, String applicationName)
	{
		StringUtilities.checkString(portName, "portName");
		StringUtilities.checkString(applicationName, "applicationName");

		this.portName = portName;
	}

	public synchronized String getName()
	{
		return this.portName;
	}

	public synchronized void initialize(CommunicationPortParameters parameters) throws CommunicationPortException
	{
		try
		{
			this.tcpipSocket = new Socket(((EthernetPortParameters) parameters).getIpAddress(), ((EthernetPortParameters) parameters).getPort());

			this.inputStream = new BufferedReader(new InputStreamReader(this.tcpipSocket.getInputStream()));
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized void write(String buffer) throws CommunicationPortException
	{
		this.write(buffer.getBytes());
	}

	public synchronized void write(byte[] buffer) throws CommunicationPortException
	{
		try
		{
			if (this.outputStream == null)
				this.outputStream = new PrintWriter(this.tcpipSocket.getOutputStream(), true);

			this.outputStream.println(buffer);
			this.outputStream.flush();

		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public byte[] readBytes() throws CommunicationPortException
	{
		throw new UnsupportedOperationException();
	}

	public byte[] readBytes(int bytes) throws CommunicationPortException
	{
		throw new UnsupportedOperationException();
	}

	public synchronized String read() throws CommunicationPortException
	{
		try
		{
			if (this.inputStream == null)
				this.inputStream = new BufferedReader(new InputStreamReader(this.tcpipSocket.getInputStream()));

			return this.inputStream.readLine();
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized String read(int bytes) throws CommunicationPortException
	{
		try
		{
			if (this.inputStream == null)
				this.inputStream = new BufferedReader(new InputStreamReader(this.tcpipSocket.getInputStream()));

			char[] cbuf = new char[bytes];

			this.inputStream.read(cbuf, 0, bytes);

			return String.copyValueOf(cbuf);
		}
		catch (Throwable t)
		{
			throw new CommunicationPortException(t);
		}
	}

	public synchronized void release()
	{
		if (this.tcpipSocket != null)
		{
			try
			{
				this.inputStream.close();
			}
			catch (Throwable t)
			{
			}

			try
			{
				this.outputStream.close();
			}
			catch (Throwable t)
			{
			}

			try
			{
				this.tcpipSocket.close();
			}
			catch (Throwable t)
			{
			}
		}
	}

}
