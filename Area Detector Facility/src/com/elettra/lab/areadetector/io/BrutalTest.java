package com.elettra.lab.areadetector.io;

import java.io.InputStream;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

public class BrutalTest
{
	public static void main(String[] args) throws InterruptedException
	{
    try
    {
    	SerialPort port = (SerialPort) CommPortIdentifier.getPortIdentifier("COM3").open("test", 2000);
    	
    	port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
  		port.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
  		
  		InputStream input = port.getInputStream();
  		OutputStream output = port.getOutputStream();

  		String cmd = "LOCAL;\r\n";

  		output.write(cmd.getBytes()); 
  	  output.flush();
  		
  		cmd = "REMOTE;\r\n";
  		
  		output.write(cmd.getBytes()); 
  	  output.flush();
  		
  	  cmd = "?V;\r\n";
  		
  		output.write(cmd.getBytes()); 
  	  output.flush();
  	  
  	  Thread.sleep(1000);
  	  
  	  StringBuffer buffer = new StringBuffer();
  	  do{
  	  	buffer.append((char) input.read());
  	  	
  	  } while (input.available() > 0);
  	  
  	  System.out.println(buffer);
  	  
  	  cmd = "";
  	  cmd = "CLR;\r\n";
  	  cmd += "CA4;\r\n";
  	  cmd += "3:-2.000S1000L2000B33;\r\n";
  	  cmd += "4:-2.000S1000L2000B33;\r\n";
  	  cmd += "NL;\r\n";
  	  cmd += "START:;\r\n";
  	  
  		output.write(cmd.getBytes()); 
  	  output.flush();

  	  port.close();
    }
    catch (Exception e)
    {
	    e.printStackTrace();
    }
	}
}
