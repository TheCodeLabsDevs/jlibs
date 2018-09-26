package de.tobias.switcher;

public interface Switcher
{
	void addCallback(SwitcherCallback switcherCallback);

	void removeSwitcherCallback(SwitcherCallback switcherCallback);

	void connect(String address);

	void dispose();

	String getProductName();

	void onDisconnect();
}
