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

	public synchronized double readPosition() throws CommunicationPortException
	{
		double position = 0.0;
		boolean gotcha = false;

		while (!gotcha)
		{
			port.write(new byte[] { (byte) 0x02 });

			byte[] out = new byte[18];
			int lastIndex = -1;

			boolean received = false;

			while (!received)
			{
				byte[] temp = port.readBytes();

				if (temp != null)
				{					
					int outIndex = -1;

					for (int i = 0; i < temp.length; i++)
					{
						outIndex = lastIndex + 1 + i;

						if (outIndex <= 17)
						{
							out[outIndex] = temp[i];

							received = outIndex == 17;
						}
					}

					lastIndex = outIndex;
				}
			}

			position = Double.parseDouble(new String(out, 1, out.length - 1).trim()) * ((char) out[0] == '+' ? 1 : -1);
			gotcha = true;
		}

		return position;
	}

	public void release() throws CommunicationPortException
	{
		this.port.release();
	}
}
