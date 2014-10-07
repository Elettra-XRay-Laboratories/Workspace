package com.elettra.idsccd.driver;

import com.sun.jna.DefaultTypeMapper;

class IDSCCDTypeMapper extends DefaultTypeMapper {

	public IDSCCDTypeMapper() {
		super();

		addTypeConverter(JNAEnum.class, new EnumConverter());
	}

}
