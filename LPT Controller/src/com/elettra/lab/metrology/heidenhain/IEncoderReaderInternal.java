package com.elettra.lab.metrology.heidenhain;

import com.elettra.common.io.CommunicationPortException;

interface IEncoderReaderInternal
{
	public void release() throws CommunicationPortException;

}
