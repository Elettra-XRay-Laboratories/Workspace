package com.elettra.lab.metrology.lpt.encoder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.CommunicationPortFactory;
import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.SerialPort64;
import com.elettra.common.io.SerialPortParameters;

public class HeidenheinEncoderReader implements IEncoderReader, IEncoderReaderInternal
{
	private ICommunicationPort	port;

	public HeidenheinEncoderReader() throws CommunicationPortException
	{
		// TODO: to init file
		this.port = CommunicationPortFactory.getPort("COM3", new SerialPort64());
		port.initialize(new SerialPortParameters(9600, 7, 2, 2));
	}

	public double readPosition() throws CommunicationPortException
	{
		byte[] command = new byte[] { (byte) 0x02 };
		double position = 0.0;

		boolean gotcha = false;
		while (!gotcha)
		{
			port.write(command);

			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
			}

			byte[] out = port.readBytes();

			if (out != null)
			{
				position = Double.parseDouble(new String(out, 1, out.length - 1)) * ((char) out[0] == '+' ? 1 : -1);
				gotcha = true;
			}
		}

		return position;
	}

	public void release() throws CommunicationPortException
	{
		this.port.release();
	}
}
