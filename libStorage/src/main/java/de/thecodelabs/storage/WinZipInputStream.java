package de.thecodelabs.storage;

import javax.annotation.Nonnull;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

// http://stackoverflow.com/questions/7924895/how-can-i-read-from-a-winzip-self-extracting-exe-zip-file-in-java
@SuppressWarnings("unused")
public class WinZipInputStream extends FilterInputStream
{

	private static final byte[] ZIP_LOCAL = {0x50, 0x4b, 0x03, 0x04};
	private int ip;
	private int op;

	public WinZipInputStream(InputStream is)
	{
		super(is);
	}

	@Override
	public int read() throws IOException
	{
		while(ip < ZIP_LOCAL.length)
		{
			int c = super.read();
			if(c == ZIP_LOCAL[ip])
			{
				ip++;
			}
			else
			{
				ip = 0;
			}
		}

		if(op < ZIP_LOCAL.length)
			return ZIP_LOCAL[op++];
		else
			return super.read();
	}

	@Override
	public int read(@Nonnull byte[] b, int off, int len) throws IOException
	{
		if(op == ZIP_LOCAL.length)
			return super.read(b, off, len);
		int l = 0;
		while(l < Math.min(len, ZIP_LOCAL.length))
		{
			b[l++] = (byte) read();
		}
		return l;
	}
}