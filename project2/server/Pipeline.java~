import java.util.ArrayList;
import java.net.*;
import java.io.IOException;

public class Pipeline implements Runnable {

	
	ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
	ArrayList<Packet> window = new ArrayList<Packet>();
	InetAddress IpAddress;
	int portNumber;
	DatagramSocket serverSocket;

	public Pipeline(ArrayList<Packet> packetBuffer, InetAddress IpAddressIn, int portNumberIn, DatagramSocket socketObjectIn)
	{
		this.packetBuffer = packetBuffer;
		this.IpAddress = IpAddressIn;
		this.portNumber = portNumberIn;
		this.serverSocket = socketObjectIn;
	}

	public void run()
	{
		while (packetBuffer.size() > 0)
		{
			//Check if there is an open slot in the window
			if (window.size() < 8)
			{
				//Dequeue packet from packetBuffer
				Packet packet = packetBuffer.get(0);
				window.add(packet);
				byte[] response = packet.getParsedResponse();
				int responseLength = (int)(response.length);
				System.out.println(responseLength);
				try {
					serverSocket.send(new DatagramPacket(response, responseLength, IpAddress, portNumber));
				}
				catch (IOException e)
				{
					System.out.println(e);
				}
				System.out.println("Pipeline Size: " + window.size());
				packetBuffer.remove(0);
			}
		}		
	}

}
