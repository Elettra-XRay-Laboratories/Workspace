package com.elettra.idsccd.driver;

import java.lang.reflect.Method;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

class IDSCCDFunctionMapper implements FunctionMapper {

	@Override
	public String getFunctionName(NativeLibrary library, Method method) {
		if (method.getName().equals("is_InitCamera"))
			return "is_InitCamera";
		else if (method.getName().equals("is_SetDisplayMode"))
			return "is_SetDisplayMode";
		else if (method.getName().equals("is_DirectRenderer"))
			return "is_DirectRenderer";
		else if (method.getName().equals("is_FreezeVideo"))
			return "is_FreezeVideo";
		else if (method.getName().equals("is_ImageFile"))
			return "is_ImageFile";
		else if (method.getName().equals("is_SetColorMode"))
			return "is_SetColorMode";
		else if (method.getName().equals("is_AllocImageMem"))
			return "is_AllocImageMem";
		else if (method.getName().equals("is_SetImageMem"))
			return "is_SetImageMem";
		else if (method.getName().equals("is_GetImageMem"))
			return "is_GetImageMem";
		else if (method.getName().equals("is_GetImageMemPitch"))
			return "is_GetImageMemPitch";
		else if (method.getName().equals("is_FreeImageMem"))
			return "is_FreeImageMem";
		else if (method.getName().equals("is_ExitCamera"))
			return "is_ExitCamera";
		else
			return null;
	}
}
