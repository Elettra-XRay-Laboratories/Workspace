package com.elettra.idsccd.driver;

public enum IDSCCDErrorCodes implements JNAEnum<IDSCCDErrorCodes>
{
	IS_NO_SUCCESS, // function call failed
	IS_SUCCESS, // function call succeeded
	IS_INVALID_HANDLE, // a handle other than the camera handle is invalid
	IS_IO_REQUEST_FAILED, // an io request to the driver failed
	IS_CANT_OPEN_DEVICE, // returned by is_InitCamera
	IS_CANT_CLOSE_DEVICE,
	IS_CANT_SETUP_MEMORY,
	IS_NO_HWND_FOR_ERROR_REPORT,
	IS_ERROR_MESSAGE_NOT_CREATED,
	IS_ERROR_STRING_NOT_FOUND,
	IS_HOOK_NOT_CREATED,
	IS_TIMER_NOT_CREATED,
	IS_CANT_OPEN_REGISTRY,
	IS_CANT_READ_REGISTRY,
	IS_CANT_VALIDATE_BOARD,
	IS_CANT_GIVE_BOARD_ACCESS,
	IS_NO_IMAGE_MEM_ALLOCATED,
	IS_CANT_CLEANUP_MEMORY,
	IS_CANT_COMMUNICATE_WITH_DRIVER,
	IS_FUNCTION_NOT_SUPPORTED_YET,
	IS_OPERATING_SYSTEM_NOT_SUPPORTED,
	IS_INVALID_VIDEO_IN,
	IS_INVALID_IMG_SIZE,
	IS_INVALID_ADDRESS,
	IS_INVALID_VIDEO_MODE,
	IS_INVALID_AGC_MODE,
	IS_INVALID_GAMMA_MODE,
	IS_INVALID_SYNC_LEVEL,
	IS_INVALID_CBARS_MODE,
	IS_INVALID_COLOR_MODE,
	IS_INVALID_SCALE_FACTOR,
	IS_INVALID_IMAGE_SIZE,
	IS_INVALID_IMAGE_POS,
	IS_INVALID_CAPTURE_MODE,
	IS_INVALID_RISC_PROGRAM,
	IS_INVALID_BRIGHTNESS,
	IS_INVALID_CONTRAST,
	IS_INVALID_SATURATION_U,
	IS_INVALID_SATURATION_V,
	IS_INVALID_HUE,
	IS_INVALID_HOR_FILTER_STEP,
	IS_INVALID_VERT_FILTER_STEP,
	IS_INVALID_EEPROM_READ_ADDRESS,
	IS_INVALID_EEPROM_WRITE_ADDRESS,
	IS_INVALID_EEPROM_READ_LENGTH,
	IS_INVALID_EEPROM_WRITE_LENGTH,
	IS_INVALID_BOARD_INFO_POINTER,
	IS_INVALID_DISPLAY_MODE,
	IS_INVALID_ERR_REP_MODE,
	IS_INVALID_BITS_PIXEL,
	IS_INVALID_MEMORY_POINTER,
	IS_FILE_WRITE_OPEN_ERROR,
	IS_FILE_READ_OPEN_ERROR,
	IS_FILE_READ_INVALID_BMP_ID,
	IS_FILE_READ_INVALID_BMP_SIZE,
	IS_FILE_READ_INVALID_BIT_COUNT,
	IS_WRONG_KERNEL_VERSION,
	IS_RISC_INVALID_XLENGTH,
	IS_RISC_INVALID_YLENGTH,
	IS_RISC_EXCEED_IMG_SIZE,
	IS_DD_MAIN_FAILED,
	IS_DD_PRIMSURFACE_FAILED,
	IS_DD_SCRN_SIZE_NOT_SUPPORTED,
	IS_DD_CLIPPER_FAILED,
	IS_DD_CLIPPER_HWND_FAILED,
	IS_DD_CLIPPER_CONNECT_FAILED,
	IS_DD_BACKSURFACE_FAILED,
	IS_DD_BACKSURFACE_IN_SYSMEM,
	IS_DD_MDL_MALLOC_ERR,
	IS_DD_MDL_SIZE_ERR,
	IS_DD_CLIP_NO_CHANGE,
	IS_DD_PRIMMEM_NULL,
	IS_DD_BACKMEM_NULL,
	IS_DD_BACKOVLMEM_NULL,
	IS_DD_OVERLAYSURFACE_FAILED,
	IS_DD_OVERLAYSURFACE_IN_SYSMEM,
	IS_DD_OVERLAY_NOT_ALLOWED,
	IS_DD_OVERLAY_COLKEY_ERR,
	IS_DD_OVERLAY_NOT_ENABLED,
	IS_DD_GET_DC_ERROR,
	IS_DD_DDRAW_DLL_NOT_LOADED,
	IS_DD_THREAD_NOT_CREATED,
	IS_DD_CANT_GET_CAPS,
	IS_DD_NO_OVERLAYSURFACE,
	IS_DD_NO_OVERLAYSTRETCH,
	IS_DD_CANT_CREATE_OVERLAYSURFACE,
	IS_DD_CANT_UPDATE_OVERLAYSURFACE,
	IS_DD_INVALID_STRETCH,
	IS_EV_INVALID_EVENT_NUMBER,
	IS_INVALID_MODE,
	IS_CANT_FIND_FALCHOOK,
	IS_CANT_FIND_HOOK,
	IS_CANT_GET_HOOK_PROC_ADDR,
	IS_CANT_CHAIN_HOOK_PROC,
	IS_CANT_SETUP_WND_PROC,
	IS_HWND_NULL,
	IS_INVALID_UPDATE_MODE,
	IS_NO_ACTIVE_IMG_MEM,
	IS_CANT_INIT_EVENT,
	IS_FUNC_NOT_AVAIL_IN_OS,
	IS_CAMERA_NOT_CONNECTED,
	IS_SEQUENCE_LIST_EMPTY,
	IS_CANT_ADD_TO_SEQUENCE,
	IS_LOW_OF_SEQUENCE_RISC_MEM,
	IS_IMGMEM2FREE_USED_IN_SEQ,
	IS_IMGMEM_NOT_IN_SEQUENCE_LIST,
	IS_SEQUENCE_BUF_ALREADY_LOCKED,
	IS_INVALID_DEVICE_ID,
	IS_INVALID_BOARD_ID,
	IS_ALL_DEVICES_BUSY,
	IS_HOOK_BUSY,
	IS_TIMED_OUT,
	IS_NULL_POINTER,
	IS_WRONG_HOOK_VERSION,
	IS_INVALID_PARAMETER, // a parameter specified was invalid
	IS_NOT_ALLOWED,
	IS_OUT_OF_MEMORY,
	IS_INVALID_WHILE_LIVE,
	IS_ACCESS_VIOLATION, // an internal exception occurred
	IS_UNKNOWN_ROP_EFFECT,
	IS_INVALID_RENDER_MODE,
	IS_INVALID_THREAD_CONTEXT,
	IS_NO_HARDWARE_INSTALLED,
	IS_INVALID_WATCHDOG_TIME,
	IS_INVALID_WATCHDOG_MODE,
	IS_INVALID_PASSTHROUGH_IN,
	IS_ERROR_SETTING_PASSTHROUGH_IN,
	IS_FAILURE_ON_SETTING_WATCHDOG,
	IS_NO_USB20, // the usb port doesnt support usb 2.0
	IS_CAPTURE_RUNNING, // there is already a capture running
	IS_MEMORY_BOARD_ACTIVATED, // operation could not execute while mboard is enabled
	IS_MEMORY_BOARD_DEACTIVATED, // operation could not execute while mboard is disabled
	IS_NO_MEMORY_BOARD_CONNECTED, // no memory board connected
	IS_TOO_LESS_MEMORY, // image size is above memory capacity
	IS_IMAGE_NOT_PRESENT, // requested image is no longer present in the camera
	IS_MEMORY_MODE_RUNNING,
	IS_MEMORYBOARD_DISABLED,
	IS_TRIGGER_ACTIVATED, // operation could not execute while trigger is enabled
	IS_WRONG_KEY,
	IS_CRC_ERROR,
	IS_NOT_YET_RELEASED, // this feature is not available yet
	IS_NOT_CALIBRATED, // the camera is not calibrated
	IS_WAITING_FOR_KERNEL, // a request to the kernel exceeded
	IS_NOT_SUPPORTED, // operation mode is not supported
	IS_TRIGGER_NOT_ACTIVATED, // operation could not execute while trigger is disabled
	IS_OPERATION_ABORTED,
	IS_BAD_STRUCTURE_SIZE,
	IS_INVALID_BUFFER_SIZE,
	IS_INVALID_PIXEL_CLOCK,
	IS_INVALID_EXPOSURE_TIME,
	IS_AUTO_EXPOSURE_RUNNING,
	IS_CANNOT_CREATE_BB_SURF, // error creating backbuffer surface  
	IS_CANNOT_CREATE_BB_MIX, // backbuffer mixer surfaces can not be created
	IS_BB_OVLMEM_NULL, // backbuffer overlay mem could not be locked  
	IS_CANNOT_CREATE_BB_OVL, // backbuffer overlay mem could not be created  
	IS_NOT_SUPP_IN_OVL_SURF_MODE, // function not supported in overlay surface mode  
	IS_INVALID_SURFACE, // surface invalid
	IS_SURFACE_LOST, // surface has been lost  
	IS_RELEASE_BB_OVL_DC, // error releasing backbuffer overlay DC  
	IS_BB_TIMER_NOT_CREATED, // backbuffer timer could not be created  
	IS_BB_OVL_NOT_EN, // backbuffer overlay has not been enabled  
	IS_ONLY_IN_BB_MODE, // only possible in backbuffer mode 
	IS_INVALID_COLOR_FORMAT, // invalid color format
	IS_INVALID_WB_BINNING_MODE, // invalid binning mode for AWB 
	IS_INVALID_I2C_DEVICE_ADDRESS, // invalid I2C device address
	IS_COULD_NOT_CONVERT, // current image couldn't be converted
	IS_TRANSFER_ERROR, // transfer failed
	IS_PARAMETER_SET_NOT_PRESENT, // the parameter set is not present
	IS_INVALID_CAMERA_TYPE, // the camera type in the ini file doesn't match
	IS_INVALID_HOST_IP_HIBYTE, // HIBYTE of host address is invalid
	IS_CM_NOT_SUPP_IN_CURR_DISPLAYMODE, // color mode is not supported in the current display mode
	IS_NO_IR_FILTER,
	IS_STARTER_FW_UPLOAD_NEEDED, // device starter firmware is not compatible    
	IS_DR_LIBRARY_NOT_FOUND, // the DirectRender library could not be found
	IS_DR_DEVICE_OUT_OF_MEMORY, // insufficient graphics adapter video memory
	IS_DR_CANNOT_CREATE_SURFACE, // the image or overlay surface could not be created
	IS_DR_CANNOT_CREATE_VERTEX_BUFFER, // the vertex buffer could not be created
	IS_DR_CANNOT_CREATE_TEXTURE, // the texture could not be created  
	IS_DR_CANNOT_LOCK_OVERLAY_SURFACE, // the overlay surface could not be locked
	IS_DR_CANNOT_UNLOCK_OVERLAY_SURFACE, // the overlay surface could not be unlocked
	IS_DR_CANNOT_GET_OVERLAY_DC, // cannot get the overlay surface DC 
	IS_DR_CANNOT_RELEASE_OVERLAY_DC, // cannot release the overlay surface DC
	IS_DR_DEVICE_CAPS_INSUFFICIENT, // insufficient graphics adapter capabilities
	IS_INCOMPATIBLE_SETTING, // Operation is not possible because of another incompatible setting
	IS_DR_NOT_ALLOWED_WHILE_DC_IS_ACTIVE, // user App still has DC handle.
	IS_DEVICE_ALREADY_PAIRED, // The device is already paired
	IS_SUBNETMASK_MISMATCH, // The subnetmasks of the device and the adapter differ
	IS_SUBNET_MISMATCH, // The subnets of the device and the adapter differ
	IS_INVALID_IP_CONFIGURATION, // The IP configuation of the device is invalid
	IS_DEVICE_NOT_COMPATIBLE, // The device is incompatible to the driver
	IS_NETWORK_FRAME_SIZE_INCOMPATIBLE, // The frame size settings of the device and the network adapter are incompatible
	IS_NETWORK_CONFIGURATION_INVALID, // The network adapter configuration is invalid
	IS_ERROR_CPU_IDLE_STATES_CONFIGURATION, // The setting of the CPU idle state configuration failed
	IS_DEVICE_BUSY, // The device is busy. The operation must be executed again later.
	IS_SENSOR_INITIALIZATION_FAILED; // The sensor initialization failed

	private static int start = -1;

	public int getIntValue()
	{
		return start + this.ordinal();
	}

	public IDSCCDErrorCodes getForValue(int i)
	{
		for (IDSCCDErrorCodes o : IDSCCDErrorCodes.values())
			if (o.getIntValue() == i)
				return o;

		return null;
	}
}
