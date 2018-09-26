package de.tobias.switcher.impl.native_mac;

import de.tobias.switcher.Switcher;
import de.tobias.switcher.SwitcherCallback;
import de.tobias.switcher.SwitcherInput;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
		callbacks = new LinkedList<>();
		this.init();
	}

	/*
	Native methods
	 */

	private native void init();

	private native void deinit();

	@Override
	public native void connect(String address);

	@Override
	public native String getProductName();

	@Override
	public void onDisconnect()
	{
		fireCallbacks(callback -> callback.onDisconnect(this));
	}

	@Override
	public void dispose() {
		deinit();
	}

	/*
	Callback
	 */

	private List<SwitcherCallback> callbacks;

	@Override
	public void addCallback(SwitcherCallback switcherCallback)
	{
		callbacks.add(switcherCallback);
	}

	@Override
	public void removeSwitcherCallback(SwitcherCallback switcherCallback)
	{
		callbacks.remove(switcherCallback);
	}

	private void fireCallbacks(Consumer<SwitcherCallback> consumer) {
		for(SwitcherCallback callback : callbacks)
		{
			try
			{
				consumer.accept(callback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		System.out.println("finalize");
		deinit();
		super.finalize();
	}

	@Override
	public String toString()
	{
		return "NativeMacSwitcher{" +
				"nativePointer=" + nativePointer +
				'}';
	}
}
