package com.elettra.controller.driver.common;

class PlusSign extends Sign
{
	public static final String SIGN = "+";

	public PlusSign()
  {
	  super(SIGN);
  }

	public double sign()
  {
	  return 1.0;
  }
	
	public boolean equals(Object o)
	{
		return (o instanceof PlusSign);
	}

}
