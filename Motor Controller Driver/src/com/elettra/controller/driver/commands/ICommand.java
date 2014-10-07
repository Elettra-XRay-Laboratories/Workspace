package com.elettra.controller.driver.commands;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.common.io.CommunicationPortException;

interface ICommand
{
	public CommandResult execute(CommandParameters commandParameters, ICommunicationPort port) throws CommunicationPortException;
}
