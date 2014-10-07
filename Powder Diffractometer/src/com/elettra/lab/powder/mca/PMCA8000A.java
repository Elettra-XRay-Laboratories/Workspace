package com.elettra.lab.powder.mca;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

interface PMCA8000A extends Library
{
	PMCA8000A INSTANCE = (PMCA8000A) Native.loadLibrary("PMCADLL", PMCA8000A.class);

	public static interface PmcaDeviceType
	{
		public static final int PMCA_AUTO_DETECT = 0;
		public static final int PMCA_8000        = 1;
		public static final int PMCA_8000A       = 2;
	}

	int PmcaPowerUp(int port);

	int PmcaConnect(int port, int baudrate, int PmcaDeviceType);

	int PmcaDisconnect();

	int PmcaSetGain(int gain);

	int PmcaSetGroup(int group);

	int PmcaClearDataAndTime();

	int PmcaSetAcquisitionTime(int timeInSeconds);

	int PmcaStartAcquisition(int setStamp);

	int PmcaStopAcquisition();

	int PmcaGetData(Pointer buffer, int channel, int count);
}
