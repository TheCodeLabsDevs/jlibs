package de.thecodelabs.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * This class broadcasts data packages on a specific port to receive a response from a server.
 */
@SuppressWarnings("unused")
public class DiscoveryClient
{
	private static final Logger LOG = LoggerFactory.getLogger(DiscoveryClient.class);

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
				final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
				c.send(sendPacket);
				LOG.trace("Request packet sent to: 255.255.255.255 (DEFAULT)");
			}
			catch(Exception e)
			{
				LOG.error("Cannot send broadcast on multicast address", e);
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
					final InetAddress broadcast = interfaceAddress.getBroadcast();
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
						LOG.error("Cannot send broadcast on interface " + networkInterface.getDisplayName(), e);
					}

					LOG.trace("Request packet sent to: {} on Interface: {}", broadcast.getHostAddress(), networkInterface.getDisplayName());
				}
			}

			LOG.trace("Done looping over all network interfaces. Now waiting for a reply!");

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
				LOG.error("Error while discover host: {} ({})", messageKey, e.getMessage());
				return null;
			}

			// We have a response
			LOG.trace("Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

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
			LOG.error("IO error while discover", ex);
		}
		return null;
	}
}
