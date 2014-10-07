package com.elettra.lab.powder.mca;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

final class MCA implements IMCA
{
	private int numberOfChannels;

	public MCA(int comport, int baudrate, int numberOfChannels) throws MCAException
	{
		try
		{
			int res = PMCA8000A.INSTANCE.PmcaPowerUp(comport);

			if (res != 0)
				throw new MCAException("PmcaPowerUp returns " + res);

			res = PMCA8000A.INSTANCE.PmcaConnect(comport, baudrate, PMCA8000A.PmcaDeviceType.PMCA_8000A);

			if (res != 0)
				throw new MCAException("PmcaConnect returns " + res);

			this.numberOfChannels = numberOfChannels;

			res = PMCA8000A.INSTANCE.PmcaSetGain(this.numberOfChannels);

			if (res != 0)
				throw new MCAException("PmcaSetGain returns " + res);

			res = PMCA8000A.INSTANCE.PmcaSetGroup(1);

			if (res != 0)
				throw new MCAException("PmcaSetGroup returns " + res);
		}
		catch (MCAException e)
		{
			PMCA8000A.INSTANCE.PmcaDisconnect();

			throw e;
		}
		catch (Throwable t)
		{
			PMCA8000A.INSTANCE.PmcaDisconnect();

			throw new MCAException(t);
		}
	}

	public void clearDataAndTime() throws MCAException
	{
		try
		{
			int res = PMCA8000A.INSTANCE.PmcaClearDataAndTime();

			if (res != 0)
				throw new MCAException("PmcaClearDataAndTime returns " + res);
		}
		catch (Throwable t)
		{
			throw new MCAException(t);
		}
	}

	public void doAcquisition(int timeInSeconds) throws MCAException
	{
		try
		{
			int res = PMCA8000A.INSTANCE.PmcaClearDataAndTime();

			if (res != 0)
				throw new MCAException("PmcaClearDataAndTime returns " + res);

			res = PMCA8000A.INSTANCE.PmcaSetAcquisitionTime(timeInSeconds);

			if (res != 0)
				throw new MCAException("setAcquisitionTime returns " + res);

			res = PMCA8000A.INSTANCE.PmcaStartAcquisition(0);

			if (res != 0)
				throw new MCAException("PmcaStartAcquisition returns " + res);

			Thread.sleep(timeInSeconds * 1000);

			res = PMCA8000A.INSTANCE.PmcaStopAcquisition();

			if (res != 0)
				throw new MCAException("PmcaStopAcquisition returns " + res);
		}
		catch (Throwable t)
		{
			throw new MCAException(t);
		}
	}

	public int[] getData() throws MCAException
	{
		try
		{
			Pointer ptr = new Memory(this.numberOfChannels * 4);
			int res = PMCA8000A.INSTANCE.PmcaGetData(ptr, 0, this.numberOfChannels);

			if (res != 0)
				throw new MCAException("PmcaGetData returns " + res);

			int[] buffer = new int[this.numberOfChannels];
			ptr.read(0, buffer, 0, this.numberOfChannels);

			return buffer;
		}
		catch (Throwable t)
		{
			throw new MCAException(t);
		}
	}

	public void disconnect() throws MCAException
	{
		try
		{
			int res = PMCA8000A.INSTANCE.PmcaDisconnect();

			if (res != 0)
				throw new MCAException("PmcaDisconnect returns " + res);
		}
		catch (Throwable t)
		{
			throw new MCAException(t);
		}
	}

}
