import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;

public class ServerConsumer implements Runnable {




	InetAddress IpAddress;
	int baseSeqNumber = 0;
	int nextSeqNumber = 0;
	DatagramSocket serverSocket;
	BlockingQueue <Integer> window;
	public volatile ArrayList<Packet> packetBuffer = new ArrayList<Packet>();


	public ServerConsumer(DatagramSocket socketObjectIn, ArrayList<Packet> packetBufferIn, BlockingQueue < Integer > windowIn) {
		this.window = windowIn;
		this.serverSocket = socketObjectIn;
		this.packetBuffer = packetBufferIn;

	}
	public int findBySequenceNumber(int s){
		int index = -1;
		for(int i = 0; i < packetBuffer.size(); i++){
			if(packetBuffer.get(i).sequenceNumber == s){
				index = i;
				break;
			}
		}
		return index;
	}
	public void run() {
		// main loop for the server consumer, this loop will check the oldest packet (which is send window base)
		// and see if it's ack'd. If it is, we will progress the window (and the producer will be able to enqueue)
		// the next packet.
		while (true) {
			if(window.size() > 0){
				synchronized(this) {
					try {
						int sequenceNumber = window.peek();

						// if we cant find the packet in the packet buffer by sequence number
						// that must mean its a duplicate ack and we can progress the window.
						int indexOfPacket = findBySequenceNumber(sequenceNumber);
						if(indexOfPacket > -1){
							Packet p = packetBuffer.get(indexOfPacket);
							System.out.println("Inspecting packet with seq #"+ p.sequenceNumber + " for ack");
							if (p.getACK() == 1){
								// means that we haven't ack'd the packet yet, so the window needn't advance
								window.take(); // consume
							}
						}
						// window.put(packetNumber);
					} catch (InterruptedException e) {
						System.out.println(e);
					}

				}
				try{
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}

			// try{
			// 	int packetIndex = window.take();	
			// 	Packet packet = packetBuffer.get(packetIndex);
			// 	if(packet.getACK() != 1){
			// 		window.put(packetIndex);
			// 	}


			// 	if(packet.getACK() != 1){
			// 		// inspect the packet at window base to see if it's been acked
			// 		byte[] response = packet.getParsedResponse();
			// 		int responseLength = (response.length);
			// 		try {
			// 			System.out.println("SENDING NEW PACKET");
			// 			System.out.println("\nPacket Index: " + packetIndex);
			// 			System.out.println("IP Address: " + packet.IPAddress);
			// 			System.out.println("PortNumber: " + packet.portNumber);
			// 			System.out.println("Sequence Number: " + packet.portNumber);
			// 			System.out.println("Sending Packet " + packetIndex + " ...");
			// 			serverSocket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
			// 			System.out.println("Packet " + packetIndex + " sent");
			// 		}
			// 		catch (IOException e)
			// 		{
			// 			System.out.println(e);
			// 		}
			// 		System.out.println("Consume packet "+packetIndex);

			// 	} else {
			// 		System.out.println("here");
			// 		packet.setACK("1");
			// 		packetBuffer.set(packetIndex, packet);
			// 	}

			// } catch (InterruptedException e) {
			// 	e.printStackTrace();
			//  }

		}
	}
}