package com.elettra.controller.gui.windows;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.elettra.common.io.ICommunicationPort;
import com.elettra.controller.gui.common.ListenerRegister;

public abstract class AbstractGenericFrame extends AbstractCommunicationPortFrame implements ActionListener
{
	public static class CommonActionCommands
	{
		public static final String EXIT = "EXIT";
	}

	/**
   * 
   */
	private static final long serialVersionUID = 3512456673923102456L;

	public AbstractGenericFrame(String title, ICommunicationPort port) throws HeadlessException
	{
		super(title, port);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		ListenerRegister.getInstance().reset();
	}

	public final void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(CommonActionCommands.EXIT))
		{
			this.dispose();
			this.setVisible(false);
		}

		this.manageOtherActions(event);
	}

	protected abstract void manageOtherActions(ActionEvent event);
}
