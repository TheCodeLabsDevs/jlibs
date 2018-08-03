package de.tobias.utils.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryThread implements Runnable {

	private int port = 0;
	private String messageKey = "UNDEFINED";

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public static DiscoveryThread getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {

		private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	}

	@SuppressWarnings("FieldCanBeLocal")
	private DatagramSocket socket;

	@Override
	public void run() {
		try {
			//Keep a socket open to listen to all the UDP traffic that is destined for this port
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {
				//Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				//See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				if (message.equals("DISCOVER_" + messageKey + "_MASTER_REQUEST")) {
					System.out.println(getClass().getName() + "Received discovery packet from: " + packet.getAddress().getHostAddress());

					byte[] sendData = ("DISCOVER_" + messageKey + "_MASTER_RESPONSE").getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
