package de.thecodelabs.net;

import de.tobias.logger.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class DiscoveryClient
{

	private int port = 0;
	private String messageKey = "UNDEFINED";

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getMessageKey()
	{
		return messageKey;
	}

	public void setMessageKey(String messageKey)
	{
		this.messageKey = messageKey;
	}

	private DatagramSocket c;

	public InetAddress discover()
	{
		// Find the server using UDP broadcast
		try
		{
			// Open a random port to send the package
			c = new DatagramSocket();
			c.setBroadcast(true);

			byte[] sendData = ("DISCOVER_" + messageKey + "_REQUEST").getBytes();

			// Try the 255.255.255.255 first
			try
			{
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
				c.send(sendPacket);
				Logger.trace("Request packet sent to: 255.255.255.255 (DEFAULT)");
			}
			catch(Exception e)
			{
				Logger.error(e);
			}

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements())
			{
				NetworkInterface networkInterface = interfaces.nextElement();

				if(networkInterface.isLoopback() || !networkInterface.isUp())
				{
					continue; // Don't want to broadcast to the loopback interface
				}

				for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if(broadcast == null)
					{
						continue;
					}

					// Send the broadcast package!
					try
					{
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
						c.send(sendPacket);
					}
					catch(Exception e)
					{
						Logger.error(e);
					}

					Logger.trace("Request packet sent to: {0} on Interface: {1}", broadcast.getHostAddress(), networkInterface.getDisplayName());
				}
			}

			Logger.trace("Done looping over all network interfaces. Now waiting for a reply!");

			// Wait for a response
			byte[] recvBuf = new byte[15000];
			c.setSoTimeout(5000);

			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			try
			{
				c.receive(receivePacket);
			}
			catch(SocketTimeoutException e)
			{
				Logger.error("Error while discover host: {0} ({1})", messageKey, e.getMessage());
				return null;
			}

			// We have a response
			Logger.trace("Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

			// Check if the message is correct
			String message = new String(receivePacket.getData()).trim();
			if(message.equals("DISCOVER_" + messageKey + "_RESPONSE"))
			{
				InetAddress addr = receivePacket.getAddress();
				c.close();
				return addr;
			}
			c.close();
		}
		catch(IOException ex)
		{
			Logger.error(ex);
		}
		return null;
	}
}
