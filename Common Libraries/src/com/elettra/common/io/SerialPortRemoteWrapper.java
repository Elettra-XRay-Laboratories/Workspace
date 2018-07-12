package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.elettra.common.utilities.StringUtilities;

class SerialPortRemoteWrapper implements ICommunicationPort
{
	private Socket proxy;

	public SerialPortRemoteWrapper(String portName, String applicationName) throws CommunicationPortException
	{
		StringUtilities.checkString(portName, "portName");
		StringUtilities.checkString(applicationName, "applicationName");

		try
		{
			this.proxy = new Socket("localhost", SerialPortRemoteWrapperServer.LISTENING_PORT);
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
		}
	}

	private String callProxyMethod(String methodName, String content) throws IOException
	{
		PrintWriter out = new PrintWriter(this.proxy.getOutputStream(), true);
		out.println(methodName + "::" + content);

		BufferedReader input = new BufferedReader(new InputStreamReader(this.proxy.getInputStream()));
		return input.readLine();

	}

	public synchronized String getName()
	{
		try
		{
			return this.callProxyMethod("getName", "");
		}
		catch (IOException exception)
		{
			return null;
		}

	}

	public synchronized void initialize(CommunicationPortParameters parameters) throws CommunicationPortException
	{
		try
		{
			SerialPortParameters serialPortParameters = (SerialPortParameters) parameters;

			this.callProxyMethod("initialize", "" + serialPortParameters);
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
		}
	}

	public synchronized void write(String buffer) throws CommunicationPortException
	{
		try
		{
			this.callProxyMethod("write", buffer);
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
		}
	}

	public synchronized void write(byte[] buffer) throws CommunicationPortException
	{
		try
		{
			this.callProxyMethod("write", new String(buffer));
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
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
			return this.callProxyMethod("read", "");
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
		}
	}

	public synchronized String read(int bytes) throws CommunicationPortException
	{
		try
		{
			return this.callProxyMethod("read", String.valueOf(bytes));
		}
		catch (Exception exception)
		{
			throw new CommunicationPortException(exception);
		}
	}

	public synchronized void release()
	{
		try
		{
			this.callProxyMethod("release", "");
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
