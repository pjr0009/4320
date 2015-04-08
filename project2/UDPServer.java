import java.io.*; 
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

class UDPServer 
{ 
	public static void main(String args[]) throws Exception 
	{
		//System.out.println("Input Probability that a given packet will be damaged: P(d) = ");
		//Scanner scan = new Scanner(System.in);
		//double Pd = scan.nextDouble();
		final int PACKET_SIZE = 512;
		int portNumber = 10046;
		DatagramSocket serverSocket = new DatagramSocket(portNumber);
		ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
		byte[] receiveData = new byte[1024]; 
		byte[] sendData  = new byte[PACKET_SIZE];
		System.out.println("LISTENING ON PORT: "+ portNumber);	
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			String sentence = new String(receivePacket.getData()); InetAddress IPAddress = receivePacket.getAddress(); 
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
			packetBuffer.add(new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE)));
			snBase++;
			offset += PACKET_SIZE;
			while(offset < sendData.length){
				// get how many bytes are left to send
				long remainingBytes = sendData.length - offset;
				if(remainingBytes < PACKET_SIZE){
					length = (int)remainingBytes;
					//serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));
					packetBuffer.add(new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE)));
					snBase++;
					break;			
				}
				else{
					//serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));
					packetBuffer.add(new Packet(snBase, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE)));

					offset += PACKET_SIZE;
					snBase++;
				}
			}
			Pipeline pipeline = new Pipeline(packetBuffer, IPAddress, port, serverSocket);			
			//Create a new pipline thread
			(new Thread(pipeline)).start();	

			rh.logRequestComplete();
		} 
	}
}
