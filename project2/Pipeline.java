import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;

public class Pipeline implements Runnable {

	


	InetAddress IpAddress;
	int baseSeqNumber = 0;
	int nextSeqNumber = 0;
	DatagramSocket serverSocket;
	BlockingQueue<Integer> window;
  ArrayList<Packet> packetBuffer = new ArrayList<Packet>();

	public Pipeline(DatagramSocket socketObjectIn, BlockingQueue<Integer> windowIn)
	{
		this.window = windowIn;
		this.serverSocket = socketObjectIn;
	}

	public void run()
	{
		while(true){
			try{
				int packetIndex = window.take();	
				Packet packet = packetBuffer.get(packetIndex);
				if(packet.getACK() != 1){
					window.put(packetIndex);
				}
			
			
				if(packet.getACK() != 1){
					// inspect the packet at window base to see if it's been acked
					byte[] response = packet.getParsedResponse();
					int responseLength = (response.length);
					try {
						System.out.println("SENDING NEW PACKET");
						System.out.println("\nPacket Index: " + packetIndex);
						System.out.println("IP Address: " + packet.IPAddress);
						System.out.println("PortNumber: " + packet.portNumber);
						System.out.println("Sequence Number: " + packet.portNumber);
						System.out.println("Sending Packet " + packetIndex + " ...");
						serverSocket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
						System.out.println("Packet " + packetIndex + " sent");
					}
					catch (IOException e)
					{
						System.out.println(e);
					}
					System.out.println("Consume packet "+packetIndex);
					 
				} else {
					System.out.println("here");
					packet.setACK("1");
					packetBuffer.set(packetIndex, packet);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
		  }

		}
	}
}
