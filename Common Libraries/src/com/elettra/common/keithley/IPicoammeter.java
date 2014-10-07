package com.elettra.common.keithley;

import java.io.IOException;

public interface IPicoammeter
{
	public double readCurrent() throws IOException;
}
