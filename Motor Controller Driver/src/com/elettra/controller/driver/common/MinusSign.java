package com.elettra.controller.driver.common;

class MinusSign extends Sign
{
	public static final String SIGN = "-";

	public MinusSign()
  {
	  super(SIGN);
  }

	public double sign()
  {
	  return -1.0;
  }
	
	public boolean equals(Object o)
	{
		return (o instanceof MinusSign);
	}
}
