package com.elettra.controller.driver.commands;

public class CommandResult
{
  private String response;

	public CommandResult(String response)
  {
	  super();
	  
	  this.response = response;
  }

	public String getResponse()
  {
	  return response;
  }

	public synchronized void setResponse(String response)
  {
	  this.response = response;
  }
}
