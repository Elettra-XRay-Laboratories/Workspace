package com.elettra.controller.gui.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.elettra.common.io.CommunicationPortUtilies;
import com.elettra.common.io.KindOfPort;
import com.elettra.common.utilities.FileIni;
import com.elettra.common.utilities.ObjectUtilities;
import com.elettra.common.utilities.StringUtilities;
import com.elettra.controller.driver.listeners.IDriverListener;

public final class GuiUtilities
{
	class DirFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			return file.isDirectory();
		}

		public String getDescription()
		{
			return "Directory";
		}
	}

	abstract class GenericFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if (file.isDirectory())
				return true;
			String fname = file.getName().toLowerCase();
			return fname.endsWith(this.getExtension());
		}

		protected abstract String getExtension();
	}

	class TxtFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if (file.isDirectory())
				return true;
			String fname = file.getName().toLowerCase();
			return fname.endsWith("txt");
		}

		public String getDescription()
		{
			return "Text Files";
		}
	}

	class DatFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if (file.isDirectory())
				return true;
			String fname = file.getName().toLowerCase();
			return fname.endsWith("dat");
		}

		public String getDescription()
		{
			return "Data Files";
		}
	}

	class PythonFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if (file.isDirectory())
				return true;
			String fname = file.getName().toLowerCase();
			return fname.endsWith("py");
		}

		public String getDescription()
		{
			return "Python Files";
		}
	}

	public static IDriverListener getNullListener()
	{
		return new NullListener();
	}

	public static void showErrorPopup(String errorMessage, JPanel panel)
	{
		JOptionPane.showMessageDialog(panel, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static boolean showConfirmPopup(String message, JPanel panel)
	{
		return JOptionPane.showConfirmDialog(panel, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

	public static void showMessagePopup(String message, JPanel panel)
	{
		JOptionPane.showMessageDialog(panel, message, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public static String showDatFileChooser(JPanel panel, String proposedFileName)
	{
		return showDatFileChooser(System.getProperty("user.dir"), panel, proposedFileName);
	}

	public static String showDatFileChooser(String workingDirectory, JPanel panel, String proposedFileName)
	{
		return showFileChooser(workingDirectory, panel, new GuiUtilities().new DatFileFilter(), proposedFileName);
	}

	public static String showTxtFileChooser(JPanel panel, String proposedFileName)
	{
		return showTxtFileChooser(System.getProperty("user.dir"), panel, proposedFileName);
	}

	public static String showTxtFileChooser(String workingDirectory, JPanel panel, String proposedFileName)
	{
		return showFileChooser(workingDirectory, panel, new GuiUtilities().new TxtFileFilter(), proposedFileName);
	}

	public static String showPythonFileChooser(JPanel panel, String proposedFileName)
	{
		return showTxtFileChooser(System.getProperty("user.dir"), panel, proposedFileName);
	}

	public static String showPythonFileChooser(String workingDirectory, JPanel panel, String proposedFileName)
	{
		return showFileChooser(workingDirectory, panel, new GuiUtilities().new PythonFileFilter(), proposedFileName);
	}

	public static String showFileChooser(String workingDirectory, JPanel panel, FileFilter filter, String proposedFileName)
	{
		JFileChooser fileChooser = new JFileChooser(workingDirectory);
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(new File(proposedFileName));
		String fileName = "";

		int n = fileChooser.showOpenDialog(panel);

		if (n == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();
			fileName = f.getAbsolutePath();
		}

		return fileName;
	}

	public static String showDirChooser(JPanel panel)
	{
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setFileFilter(new GuiUtilities().new DirFileFilter());
		String fileName = "";

		int n = fileChooser.showOpenDialog(panel);

		if (n == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();
			fileName = f.getAbsolutePath();
		}

		return fileName;
	}

	public static KindOfPort getKindOfPort() throws IOException
	{
		return CommunicationPortUtilies.parseKindOfPort(FileIni.getInstance().getProperty("KindOfPort"));
	}

	public static String getPortConfFileName(String portName) throws IOException
	{
		return FileIni.getInstance().getProperty("PortConfFileDir") + portName + "_Conf.txt";
	}

	public static void savePortNames(List<String> names) throws IOException
	{
		ObjectUtilities.checkObject(names, "names");

		BufferedWriter writer = new BufferedWriter(new FileWriter(FileIni.getInstance().getProperty("PortNameFilePath")));

		try
		{
			ListIterator<String> iterator = names.listIterator();

			while (iterator.hasNext())
			{
				writer.write(iterator.next());
				writer.newLine();
			}

			writer.flush();
		}
		finally
		{
			writer.close();
		}

	}

	public static List<String> getPortNames() throws IOException
	{
		List<String> list = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(FileIni.getInstance().getProperty("PortNameFilePath")));

		try
		{
			String name = reader.readLine();

			while (name != null)
			{
				list.add(name);

				name = reader.readLine();
			}
		}
		finally
		{
			reader.close();
		}

		return list;
	}

	public static double parseScanTime(String time)
	{
		StringUtilities.checkString(time, "Scan Time");

		if (!time.endsWith("s"))
			throw new IllegalArgumentException("Scan Time String not recognized: " + time);

		return Double.parseDouble(time.substring(0, time.length() - 1).trim());
	}

	public static String parseDouble(double number)
	{
		return String.format("%7.4f", number);
	}

	public static String parseDouble(double number, int digits, boolean trim)
	{
		String value = String.format("%7." + digits + "f", number);

		return trim ? value.trim() : value;
	}

	public static String parseDateElement(double element)
	{
		String temp = Integer.toString((int) element);

		return (temp.length() == 1) ? "0" + temp : temp;
	}

	public static String getNowString()
	{
		return GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) + "-"
		    + GuiUtilities.parseDateElement(GregorianCalendar.getInstance().get(GregorianCalendar.MONTH)) + "-"
		    + GuiUtilities.parseDateElement(GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH)) + "_"
		    + GuiUtilities.parseDateElement(GregorianCalendar.getInstance().get(GregorianCalendar.HOUR_OF_DAY)) + "-"
		    + GuiUtilities.parseDateElement(GregorianCalendar.getInstance().get(GregorianCalendar.MINUTE));
	}
}
