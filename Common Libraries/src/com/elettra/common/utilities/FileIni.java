package com.elettra.common.utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FileIni
{
	private static FileIni uniqueInstance;

	private Properties     fileIni;

	private FileIni() throws IOException
	{
		this.fileIni = new Properties();
		this.fileIni.load(new FileReader("Properties.ini"));
	}

	public static synchronized FileIni getInstance() throws IOException
	{
		if (uniqueInstance == null)
			uniqueInstance = new FileIni();

		return uniqueInstance;
	}

	public String getProperty(String name)
	{
		return this.fileIni.getProperty(name);
	}
}
