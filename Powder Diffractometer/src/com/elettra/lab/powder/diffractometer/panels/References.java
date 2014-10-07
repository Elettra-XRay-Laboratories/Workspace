package com.elettra.lab.powder.diffractometer.panels;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class References
{
	private static final String FILE_NAME         = "Files\\References.properties";

	public static final String  SAMPLE_Z_AT_PSI_0 = "SAMPLE_Z_AT_PSI_0";
	public static final String  BEAM_OFFSET       = "BEAM_OFFSET";
	public static final String  SAMPLE_OFFSET     = "SAMPLE_OFFSET";

	private static References   uniqueInstance;

	private Properties          file;

	private References() throws IOException
	{
		this.file = new Properties();

		File input = new File(FILE_NAME);
		if (input.exists())
			this.file.load(new FileReader(FILE_NAME));
		else
			input.createNewFile();
	}

	public static synchronized References getInstance() throws IOException
	{
		if (uniqueInstance == null)
			uniqueInstance = new References();

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
