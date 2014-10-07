package com.elettra.idsccd.driver;


import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

@SuppressWarnings("rawtypes")
class EnumConverter implements TypeConverter {

	public Object fromNative(Object input, FromNativeContext context) {
		Integer i = (Integer) input;
		Class targetClass = context.getTargetType();
		if (!JNAEnum.class.isAssignableFrom(targetClass)) {
			return null;
		}
		Object[] enums = targetClass.getEnumConstants();
		if (enums.length == 0) {
			return null;
		}
		// In order to avoid nasty reflective junk and to avoid needing
		// to know about every subclass of JnaEnum, we retrieve the first
		// element of the enum and make IT do the conversion for us.

		JNAEnum instance = (JNAEnum) enums[0];
		return instance.getForValue(i);

	}

	public Object toNative(Object input, ToNativeContext context) {
		JNAEnum j = (JNAEnum) input;
		return new Integer(j.getIntValue());
	}

	public Class nativeType() {
		return Integer.class;
	}
}
