import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;

public class ServerConsumer implements Runnable {
	DatagramSocket socket;
	InetAddress IpAddress;
	int baseSeqNumber = 0;
	int nextSeqNumber = 0;
	BlockingQueue <Integer> window;
	public volatile ArrayList<Packet> packetBuffer = new ArrayList<Packet>();


	public ServerConsumer(DatagramSocket socketIn, ArrayList<Packet> packetBufferIn, BlockingQueue < Integer > windowIn) {
		this.socket = socketIn;
		this.window = windowIn;
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
							// means it's been ack'd and can remove
							// otherwise, new packet, send.
							if (p.getACK() == 1){
								// means that we haven't ack'd the packet yet, so the window needn't advance
								packetBuffer.remove(indexOfPacket);
								window.take(); // consume
							} else if (p.getNAK() == 1){
								// resend if NAK! WOWOWOWOW
								Packet packet = packetBuffer.get(indexOfPacket);
	              
	              // now we actually need to send out the added packet
	              byte[] response = packet.getParsedResponse();
	              int responseLength = (response.length);
	              try {
								System.out.println("\n RECIEVED NAK FOR PACKET: " + packet.sequenceNumber);

	              	System.out.println("\n\n RESENDING PACKET: " + packet.sequenceNumber);
	                socket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
	                try{
	                	Thread.sleep(2000);
	                } catch(Exception e){
	                	System.out.println(e);
	                }
	              }
	              catch (IOException e)
	              {
	               System.out.println(e);
	              }
							} else {
								  try{
	                	Thread.sleep(100);
	                } catch(Exception e){
	                	System.out.println(e);
	                }
							}
						}
						// window.put(packetNumber);
					} catch (InterruptedException e) {
						System.out.println(e);
					}

				}
			}
		}
	}
}