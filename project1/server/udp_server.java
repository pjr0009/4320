import java.io.*; 
import java.net.*;
import java.util.Scanner;
import java.util.zip;

class UDPServer 
{ 
	public static void main(String args[]) throws Exception 
	{
		//System.out.println("Input Probability that a given packet will be damaged: P(d) = ");
		//Scanner scan = new Scanner(System.in);
		//double Pd = scan.nextDouble();

		DatagramSocket serverSocket = new DatagramSocket(10046);
		byte[] receiveData = new byte[1024]; 
		byte[] sendData  = new byte[256];
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			//System.out.println("Request packet received.");

			String sentence = new String(receivePacket.getData()); InetAddress IPAddress = 				receivePacket.getAddress(); 
			RequestHandler rh  = new RequestHandler(sentence);
			int port = receivePacket.getPort();
			rh.requestValidator();			
			rh.logRequest();

			sendData = rh.parsedResponse().getBytes("UTF-8");
                        int offset = 0;
			int length = 0;
			if(sendData.length < 256){
				length = sendData.length;
			} else{
				length = 256;				
			}
			System.out.println("length: " + length);

			serverSocket.send(new DatagramPacket(sendData, 0, length, IPAddress, port));
			offset += 256;
			while(offset < sendData.length){
				System.out.println("Sending packet of length " + length + " starting at offset " + offset); 
				
				// get how many bytes are left to send
				long remainingBytes = sendData.length - offset;
				System.out.println("remaining: " + remainingBytes);
				if(remainingBytes < 256){
					length = (int)remainingBytes;	
					serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));
					System.out.println("last packet sent");
					break;			
				}
				else{

					serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));

					System.out.println("Packet " + offset/256 + "was sent.");
					offset += 256;

				}


			}
			// checksum coming soon
			
		} 
	}
}