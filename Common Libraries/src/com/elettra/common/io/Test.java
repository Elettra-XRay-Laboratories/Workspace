package com.elettra.common.io;

public class Test
{

	public static void main(String[] args) throws InterruptedException
	{
    try
    {
    	CommunicationPortFactory.setApplicationName("TEST");
    	
	    ICommunicationPort port = CommunicationPortFactory.getPort("COM3", new SerialPort64());
	    
	    SerialPortParameters parameters = new SerialPortParameters(9600, 7, 2, 2);
	    port.initialize(parameters);
	    
	    byte[] array = new byte[1];
	    array[0] = (byte) 0x02;
	    
	    boolean gotcha = false;
	    
	    while (!gotcha)
	    {
	    	port.write(array);
	    	
	    	Thread.sleep(100);
	    	
	    	String out = port.read();
	    	
	    	if (out != null)
	    	{
	    		System.out.println(out);
	    		gotcha = true;
	    	}
	    }
	    
	    port.release();
    }
    catch (CommunicationPortException e)
    {
	    e.printStackTrace();
    }
	}

}
