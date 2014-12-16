package com.elettra.lab.metrology.heidenhain;

import com.elettra.common.io.CommunicationPortException;

public interface IEncoderReader
{
	public double readPosition() throws CommunicationPortException;
}
