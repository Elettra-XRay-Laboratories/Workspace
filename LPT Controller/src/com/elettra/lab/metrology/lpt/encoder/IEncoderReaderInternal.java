package com.elettra.lab.metrology.lpt.encoder;

import com.elettra.common.io.CommunicationPortException;

interface IEncoderReaderInternal
{
	public void release() throws CommunicationPortException;

}
