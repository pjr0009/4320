import java.io.*; 
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

class UDPServer 
{ 
	public static void main(String args[]) throws Exception 
	{
		//System.out.println("Input Probability that a given packet will be damaged: P(d) = ");
		//Scanner scan = new Scanner(System.in);
		//double Pd = scan.nextDouble();

		BlockingQueue window = new ArrayBlockingQueue(8); // blocking window, consumer
    ArrayList<Packet> packetBuffer = new ArrayList<Packet>();

		final int PACKET_SIZE = 512;
		int portNumber = 10046;
		DatagramSocket serverSocket = new DatagramSocket(portNumber);
		byte[] receiveData = new byte[1024]; 
		byte[] sendData  = new byte[PACKET_SIZE];
		System.out.println("LISTENING ON PORT: "+ portNumber);	
		Pipeline pipeline = new Pipeline(serverSocket, window, packetBuffer);			
		//Create a new pipline thread
		Thread thread = new Thread(pipeline);	
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			String sentence = new String(receivePacket.getData()); 
			InetAddress IPAddress = receivePacket.getAddress(); 
			RequestHandler rh  = new RequestHandler(sentence);
			int port = receivePacket.getPort();
			rh.requestValidator();			
			rh.logRequest();

			sendData = rh.checkSummedResponse().getBytes("UTF-8");
            		int offset = 0;
			int length = 0;
			
			System.out.println("Total message length: " + sendData.length);

			if(sendData.length < PACKET_SIZE){
				length = sendData.length;
			} else{
				length = PACKET_SIZE;				
			}

			//serverSocket.send(new DatagramPacket(sendData, 0, length, IPAddress, port));
			int snBase = 0;
			packetBuffer.add(new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port));
			snBase++;
			offset += PACKET_SIZE;
			while(offset < sendData.length){
				// get how many bytes are left to send
				long remainingBytes = sendData.length - offset;
				if(remainingBytes < PACKET_SIZE){
					length = (int)remainingBytes;
					packetBuffer.add(new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port));
					snBase++;
					break;			
				}
				else{
					packetBuffer.add(new Packet(snBase, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port));

					offset += PACKET_SIZE;
					snBase++;
				}
			}
			
			thread.start();
			while(packetBuffer.size() > 0){
				// add level of inderection so that when we update packets to acked they can be updated in the queue
				window.put(packetBuffer.indexOf(packetBuffer.get(0)));
			}

			rh.logRequestComplete();
		} 
	}
}
