package com.elettra.controller.gui.windows;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import com.elettra.common.io.ICommunicationPort;

public abstract class AbstractCommunicationPortFrame extends JFrame
{
	/**
   * 
   */
	private static final long  serialVersionUID = -2454321586644455493L;

	private ICommunicationPort port;

	public AbstractCommunicationPortFrame(String title, ICommunicationPort port) throws HeadlessException
	{
		super(title);

		this.port = port;
	}

	protected final ICommunicationPort getPort()
	{
		return this.port;
	}
}
