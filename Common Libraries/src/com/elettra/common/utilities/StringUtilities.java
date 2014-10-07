package com.elettra.common.utilities;

public final class StringUtilities
{
  public static boolean isEmpty(String string)
  {
  	return string == null || string.trim().isEmpty();
  }
  
	public static void checkString(String string, String stringName)
  {
  	if (StringUtilities.isEmpty(string)) throw new IllegalArgumentException("String " + stringName + " cannot be empty");
  }
}
