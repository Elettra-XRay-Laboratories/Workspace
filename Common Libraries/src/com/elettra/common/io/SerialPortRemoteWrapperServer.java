package com.elettra.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class SerialPortRemoteWrapperServer extends SerialPortWrapper
{
	public static int LISTENING_PORT = 9090;

	public SerialPortRemoteWrapperServer(String portName, String applicationName) throws CommunicationPortException
	{
		super(portName, applicationName);
	}

	public void runServer()
	{
		ServerSocket server = null;
		try
		{
			server = new ServerSocket(LISTENING_PORT);

			while (true)
			{
				try
				{
					Socket socket = server.accept();

					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

					String input = in.readLine();

					String[] data = this.parseInput(input);

					String methodName = data[0];

					String output = "";

					if (methodName.equals("getName"))
					{
						output = this.getName();
					}
					else if (methodName.equals("initialize"))
					{
						StringTokenizer paramTokenizer = new StringTokenizer(data[1], ",");

						System.out.println(paramTokenizer.countTokens());

						int baudrate = Integer.parseInt(paramTokenizer.nextToken());
						int databits = Integer.parseInt(paramTokenizer.nextToken());
						int stopbits = Integer.parseInt(paramTokenizer.nextToken());
						int parity = Integer.parseInt(paramTokenizer.nextToken());

						this.initialize(new SerialPortParameters(baudrate, databits, stopbits, parity));
					}
					else if (methodName.equals("write"))
					{
						this.write(data[1]);
					}
					else if (methodName.equals("read"))
					{
						if (data[1].equals(""))
						{
							output = this.read();
						}
						else
						{
							output = this.read(Integer.parseInt(data[1]));
						}
					}
					else if (methodName.equals("release"))
					{
						output = "REMOTE RELEASE IS NOT POSSIBLE";
					}

					out.println(output);

				}
				catch (Throwable t)
				{
					t.printStackTrace();
				}
			}

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			if (server != null)
			{
				try
        {
	        server.close();
        }
        catch (IOException exception)
        {
	        // TODO Auto-generated catch block
	        exception.printStackTrace();
        }
			}
		}
	}

	private String[] parseInput(String input)
	{
		StringTokenizer tokenizer = new StringTokenizer(input, "::");

		String[] data = new String[2];

		data[0] = tokenizer.nextToken();
		data[1] = tokenizer.nextToken();

		return data;
	}
}
