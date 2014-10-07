package com.elettra.idsccd.driver;

public enum IDSCCDDirectRendererModes implements JNAEnum<IDSCCDDirectRendererModes>
{
	DR_GET_OVERLAY_DC,
	DR_GET_MAX_OVERLAY_SIZE,
	DR_GET_OVERLAY_KEY_COLOR,
	DR_RELEASE_OVERLAY_DC,
	DR_SHOW_OVERLAY,
	DR_HIDE_OVERLAY,
	DR_SET_OVERLAY_SIZE,
	DR_SET_OVERLAY_POSITION,
	DR_SET_OVERLAY_KEY_COLOR,
	DR_SET_HWND,
	DR_ENABLE_SCALING,
	DR_DISABLE_SCALING,
	DR_CLEAR_OVERLAY,
	DR_ENABLE_SEMI_TRANSPARENT_OVERLAY,
	DR_DISABLE_SEMI_TRANSPARENT_OVERLAY,
	DR_CHECK_COMPATIBILITY,
	DR_SET_VSYNC_OFF,
	DR_SET_VSYNC_AUTO,
	DR_SET_USER_SYNC,
	DR_GET_USER_SYNC_POSITION_RANGE,
	DR_LOAD_OVERLAY_FROM_FILE,
	DR_STEAL_NEXT_FRAME,
	DR_SET_STEAL_FORMAT,
	DR_GET_STEAL_FORMAT,
	DR_ENABLE_IMAGE_SCALING,
	DR_GET_OVERLAY_SIZE,
	DR_CHECK_COLOR_MODE_SUPPORT,
	DR_GET_OVERLAY_DATA,
	DR_UPDATE_OVERLAY_DATA,
	DR_GET_SUPPORTED;

	private static int start = 1;

	public int getIntValue()
	{
		return start + this.ordinal();
	}

	public IDSCCDDirectRendererModes getForValue(int i)
	{
		for (IDSCCDDirectRendererModes o : IDSCCDDirectRendererModes.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
