package com.elettra.lab.powder.mca;

public class MainTest
{
	public static void main(String[] args) throws MCAException
	{
		IMCA mca = MCAFactory.getMCA(4, 115200, 4096);

		try
		{
			mca.doAcquisition(10);

			int[] buffer = mca.getData();

			String temp = "";
			for (int i = 0; i < 1024; i++)
				temp += buffer[i] + ", ";

			System.out.println(temp);
		}
		finally
		{
			mca.disconnect();
		}
	}
}
