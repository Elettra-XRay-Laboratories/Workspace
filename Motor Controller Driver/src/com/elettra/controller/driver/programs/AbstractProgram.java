package com.elettra.controller.driver.programs;

import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.common.utilities.StringUtilities;

public abstract class AbstractProgram implements IProgram
{

	private AbstractProgram next;
	private String          programName;

	public AbstractProgram(String programName)
	{
		super();

		StringUtilities.checkString(programName, "programName");

		this.programName = programName;
	}

	protected final AbstractProgram setNext(AbstractProgram next)
	{
		ObjectUtilities.checkObject(next, "next");

		this.next = next;

		return this.next;
	}

	protected final boolean isRequestedProgram(String programName)
	{
		StringUtilities.checkString(programName, "programName");

		return this.programName.equals(programName);
	}

	protected final AbstractProgram getProgram(String programName)
	{
		if (this.isRequestedProgram(programName))
			return this;
		else
			return this.next == null ? null : this.next.getProgram(programName);
	}

	protected final String getProgramName()
	{
		return this.programName;
	}

}
