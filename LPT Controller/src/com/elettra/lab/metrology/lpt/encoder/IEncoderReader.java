package com.elettra.lab.metrology.lpt.encoder;

import com.elettra.common.io.CommunicationPortException;

public interface IEncoderReader
{
	public double readPosition() throws CommunicationPortException;
}
