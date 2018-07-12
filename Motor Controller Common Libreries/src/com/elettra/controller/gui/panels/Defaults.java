package com.elettra.controller.gui.panels;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Defaults
{
	private static final String	FILE_NAME	     = "Files\\Defaults.properties";

	public static final String ABS_REL	       = "ABS_REL";
	public static final String ABS_REL_ENABLED = "ABS_REL_ENABLED";

	private static Defaults	  uniqueInstance;

	private Properties	        file;

	private Defaults() throws IOException
	{
		this.file = new Properties();

		File input = new File(FILE_NAME);
		if (input.exists())
			this.file.load(new FileReader(FILE_NAME));
		else
			input.createNewFile();
	}

	public static synchronized Defaults getInstance() throws IOException
	{
		if (uniqueInstance == null)
			uniqueInstance = new Defaults();

		return uniqueInstance;
	}

	public String get(String name)
	{
		return this.file.getProperty(name);
	}

	public synchronized void set(String name, String value)
	{
		this.file.setProperty(name, value);
	}

	public synchronized void saveAll() throws IOException
	{
		this.file.store(new FileWriter(FILE_NAME), null);
	}

	public synchronized void save(String name, String value) throws IOException
	{
		this.set(name, value);
		this.saveAll();
	}
}
