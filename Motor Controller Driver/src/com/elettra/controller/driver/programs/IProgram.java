package com.elettra.controller.driver.programs;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;

public interface IProgram
{
  public ProgramResult execute(ProgramParameters parameters, ICommunicationPort port) throws CommunicationPortException;
}
