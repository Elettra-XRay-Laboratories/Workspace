package com.elettra.idsccd.driver;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

interface IDSCCDWrapper extends Library
{

	public static class ImageFileParams extends Structure
	{
		public static class ByReference extends ImageFileParams implements Structure.ByReference
		{
		}

		public WString        pwchFileName;
		public int            nFileType;
		public int            nQuality;
		public Pointer        ppcImageMem;
		public IntByReference pnImageID;
		public byte[]         reserved = new byte[32];

		@SuppressWarnings("rawtypes")
		protected List getFieldOrder()
		{
			return Arrays.asList(new String[] { "pwchFileName", "nFileType", "nQuality", "ppcImageMem", "pnImageID", "reserved" });
		}
	}

	IDSCCDWrapper INSTANCE = (IDSCCDWrapper) Native.loadLibrary("ueye_api_64", IDSCCDWrapper.class, new JNAOptions());

	public IDSCCDErrorCodes is_InitCamera(IntByReference cameraHandle, int windowHande);

	public IDSCCDErrorCodes is_SetDisplayMode(int cameraHandle, IDSCCDDisplayModes mode);

	public IDSCCDErrorCodes is_ExitCamera(int cameraHandle);

	public IDSCCDErrorCodes is_DirectRenderer(int cameraHandle, IDSCCDDirectRendererModes mode, ByReference param, int size);

	public IDSCCDErrorCodes is_FreezeVideo(int cameraHandle, IDSCCDWaitMode wait);

	public IDSCCDErrorCodes is_ImageFile(int cameraHandle, IDSCCDImageFileCommands command, Structure.ByReference param, int size);

	public IDSCCDErrorCodes is_SetColorMode(int cameraHandle, IDSCCDColorModes mode);

	public IDSCCDErrorCodes is_AllocImageMem(int cameraHandle, int width, int height, int bitspixel, PointerByReference imageBuffer, IntByReference id);

	public IDSCCDErrorCodes is_SetImageMem(int cameraHandle, Pointer imageBuffer, int id);

	public IDSCCDErrorCodes is_GetImageMem(int cameraHandle, PointerByReference imageBuffer);

	public IDSCCDErrorCodes is_GetImageMemPitch(int cameraHandle, IntByReference pitch);

	public IDSCCDErrorCodes is_FreeImageMem(int cameraHandle, Pointer imageBuffer, int id);
	
	public IDSCCDErrorCodes is_SetHardwareGain(int cameraHandle, int nMaster, int nRed, int nGreen, int nBlue);
}
