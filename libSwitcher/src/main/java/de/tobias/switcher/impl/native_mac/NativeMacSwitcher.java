package de.tobias.switcher.impl.native_mac;

import de.tobias.switcher.Switcher;

public class NativeMacSwitcher implements Switcher
{
	private long nativePointer;

	private long getNativePointer()
	{
		return nativePointer;
	}

	private void setNativePointer(long nativePointer)
	{
		this.nativePointer = nativePointer;
	}

	public NativeMacSwitcher()
	{
		init();
	}

	private native void init();

	private native void deinit();

	@Override
	protected void finalize() throws Throwable
	{
		System.out.println("finalize");
		deinit();
		super.finalize();
	}

	public void dispose() {
		deinit();
	}

	@Override
	public native void connect();

	@Override
	public native String getProductName();

	@Override
	public String toString()
	{
		return "NativeMacSwitcher{" +
				"nativePointer=" + nativePointer +
				'}';
	}
}
