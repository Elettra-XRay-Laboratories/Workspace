package com.elettra.idsccd.driver;

import java.util.HashMap;


import com.sun.jna.Library;

public class JNAOptions extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5012036831798987995L;

	public JNAOptions() {
		super();

		this.put(Library.OPTION_TYPE_MAPPER, new IDSCCDTypeMapper());
		this.put(Library.OPTION_FUNCTION_MAPPER, new IDSCCDFunctionMapper());
	}

}
