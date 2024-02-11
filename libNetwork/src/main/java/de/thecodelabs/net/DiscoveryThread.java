package de.thecodelabs.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Server of discovery unit. This thread periodically listens for packages on a specific port.
 */
@SuppressWarnings("unused")
public class DiscoveryThread implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(DiscoveryThread.class);

	private int port = 0;
	private String requestMessageKey = "UNDEFINED";
	private String responseMessageKey = "UNDEFINED";

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getRequestMessageKey()
	{
		return requestMessageKey;
	}

	public void setRequestMessageKey(String requestMessageKey)
	{
		this.requestMessageKey = requestMessageKey;
	}

	public String getResponseMessageKey()
	{
		return responseMessageKey;
	}

	public void setResponseMessageKey(String responseMessageKey)
	{
		this.responseMessageKey = responseMessageKey;
	}

	public static DiscoveryThread getInstance()
	{
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder
	{
		private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	}

	@SuppressWarnings("FieldCanBeLocal")
	private DatagramSocket socket;

	@Override
	public void run()
	{
		try
		{
			//Keep a socket open to listen to all the UDP traffic that is destined for this port
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while(true)
			{
				if(Thread.interrupted())
				{
					return;
				}

				//Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				//See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				if(message.equals(requestMessageKey))
				{
					LOG.trace("Received discovery packet from: {} using token {}", packet.getAddress().getHostAddress(), requestMessageKey);

					byte[] sendData = responseMessageKey.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
				}
			}
		}
		catch(IOException e)
		{
			LOG.error("Error while running discovery thread", e);
		}
	}
}
