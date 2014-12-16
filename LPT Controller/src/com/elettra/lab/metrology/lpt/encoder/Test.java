package com.elettra.lab.metrology.lpt.encoder;

import com.elettra.common.io.CommunicationPortException;
import com.elettra.common.io.CommunicationPortFactory;

public class Test
{

	public static void main(String[] args) throws InterruptedException
	{
    try
    {
    	CommunicationPortFactory.setApplicationName("TEST");
    	
	    IEncoderReader reader =  EncoderReaderFactory.getEncoderReader();
    	
	    System.out.println(reader.readPosition());
	    
	    EncoderReaderFactory.releaseEncoderReader();
    }
    catch (CommunicationPortException e)
    {
	    e.printStackTrace();
    }
	}

}
