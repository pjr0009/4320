import java.io.*; 
import java.net.*;
import java.util.Scanner;


class UDPServer 
{ 
	public static void main(String args[]) throws Exception 
	{
		//System.out.println("Input Probability that a given packet will be damaged: P(d) = ");
		//Scanner scan = new Scanner(System.in);
		//double Pd = scan.nextDouble();
		int portNumber = 10046;
		DatagramSocket serverSocket = new DatagramSocket(portNumber);
		byte[] receiveData = new byte[1024]; 
		byte[] sendData  = new byte[256];
		System.out.println("LISTENING ON PORT: "+portNumber);	
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

			if(sendData.length < 256){
				length = sendData.length;
			} else{
				length = 256;				
			}

			serverSocket.send(new DatagramPacket(sendData, 0, length, IPAddress, port));
			offset += 256;
			while(offset < sendData.length){
				// get how many bytes are left to send
				long remainingBytes = sendData.length - offset;
				if(remainingBytes < 256){
					length = (int)remainingBytes;	
					serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));
					break;			
				}
				else{

					serverSocket.send(new DatagramPacket(sendData, offset, length, IPAddress, port));

					System.out.println("Packet " + offset/256 + " was sent.");
					offset += 256;

				}


			}
			rh.logRequestComplete();
		} 
	}
}