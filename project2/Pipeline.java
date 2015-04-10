import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;

public class Pipeline implements Runnable {

	


	InetAddress IpAddress;
	int baseSeqNumber = 0;
	int nextSeqNumber = 0;
	DatagramSocket serverSocket;
	BlockingQueue window;
  ArrayList<Packet> packetBuffer = new ArrayList<Packet>();

	public Pipeline(DatagramSocket socketObjectIn, BlockingQueue windowIn, ArrayList<Packet> packetBufferIn)
	{
		this.window = windowIn;
		this.packetBuffer = packetBufferIn;
		this.serverSocket = socketObjectIn;
	}

	public void run()
	{
		while(true){
			try {
			    Thread.sleep(5000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			System.out.println(packetBuffer.size());
			int packetIndex = (Integer)window.peek();
			Packet packet = packetBuffer.get(packetIndex);
			if(packet.getACK() == 1){
				try {
					// inspect the packet at window base to see if it's been acked
					window.take(); //consume
					byte[] response = packet.getParsedResponse();
					int responseLength = (int)(response.length);
					try {
						
						System.out.println("IP: " + packet.IPAddress);
						System.out.println("PortNumber: " + packet.portNumber);
						serverSocket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
						System.out.println("Packet sent");
					}
					catch (IOException e)
					{
						System.out.println(e);
					}
					System.out.println("Consume packet "+packetIndex);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				packet.setACK("1");
			}
			// packetBuffer.remove(0);

		}
	}
}
