package com.elettra.common.utilities;

import java.io.IOException;
import java.io.Serializable;

public interface ISerializable extends Serializable
{
	public void serialize(String filePath) throws IOException;

	public void deserialize(String filePath) throws IOException;
}
