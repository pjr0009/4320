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

		// blocking window which will block the producer if it tries to add more than 8 items
		BlockingQueue<Integer> window = new ArrayBlockingQueue<Integer>(8); // blocking window, consumer
  	
		// this is the list of all packets for all requests that are awaiting to go out
  	ArrayList<Integer> packetBuffer = new ArrayList<Integer>();

		//
		final int PACKET_SIZE = 512;
		final int SERVER_PORT_NUMBER = 10046;
		DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT_NUMBER);
		byte[] receiveData = new byte[1024]; 
		byte[] sendData  = new byte[PACKET_SIZE];
		System.out.println("LISTENING ON PORT: "+ SERVER_PORT_NUMBER);	



		// consumer object which will consume packets from the producer thread
		// a new packet should be consumed (enqueued) when the window advances
		ServerConsumer consumer = new ServerConsumer(serverSocket, window);

		// when we recieve a request, the goal is to retrieve the file, break it up into packets,
		// then add it to the buffer of all the packets scheduled to go out, as the window advances,
		// the producer thread should enqueue more outgoing packets to be sent to the client(s)
		ServerProducer producer = new ServerProducer(packetBuffer, window);			
		
		Thread consumerThread = new Thread(consumer);	
		Thread producerThread = new Thread(producer);	
		// consumerThread.start();
		
		producerThread.start();
		consumerThread.start();


		while(true) {
			producer.packetBuffer.add(producer.packetBuffer.size());
      try {
        Thread.sleep(1000);

      } catch(InterruptedException e){
        System.out.println(e);
      }

		// 	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		// 	serverSocket.receive(receivePacket);

		// 	String sentence = new String(receivePacket.getData()); 
		// 	InetAddress IPAddress = receivePacket.getAddress(); 
		// 	RequestHandler rh  = new RequestHandler(sentence);
		// 	int port = receivePacket.getPort();
		// 	rh.requestValidator();			
		// 	rh.logRequest();

		// 	sendData = rh.checkSummedResponse().getBytes("UTF-8");
 	  //           		int offset = 0;
		// 	int length = 0;
			
		// 	System.out.println("Total message length: " + sendData.length);

		// 	if(sendData.length < PACKET_SIZE){
		// 		length = sendData.length;
		// 	} else{
		// 		length = PACKET_SIZE;				
		// 	}

		// 	//serverSocket.send(new DatagramPacket(sendData, 0, length, IPAddress, port));
		// 	int snBase = 0;
		// 	Packet packet = new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port);
		// 	consumer.packetBuffer.add(packet);
		// 	System.out.println("IP Address: " + packet.IPAddress);
		// 	System.out.println("PortNumber: " + packet.portNumber);
		// 	System.out.println("Sequence Number: " + packet.portNumber+ "\n\n");
		// 	snBase++;
		// 	offset += PACKET_SIZE;
		// 	while(offset < sendData.length){
		// 		// get how many bytes are left to send
		// 		long remainingBytes = sendData.length - offset;
		// 		if(remainingBytes < PACKET_SIZE){
		// 			length = (int)remainingBytes;
		// 			packet = new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port);
		// 			System.out.println("IP Address: " + packet.IPAddress);
		// 			System.out.println("PortNumber: " + packet.portNumber);
		// 			System.out.println("Sequence Number: " + packet.portNumber + "\n\n");
		// 			consumer.packetBuffer.add(packet);
		// 			snBase++;
		// 			break;			
		// 		}
		// 		else{
		// 			packet = new Packet(snBase%24, Arrays.copyOfRange(sendData, offset, offset + PACKET_SIZE), IPAddress, port);
		// 			System.out.println("IP Address: " + packet.IPAddress);
		// 			System.out.println("PortNumber: " + packet.portNumber);
		// 			System.out.println("Sequence Number: " + packet.portNumber + "\n\n");
		// 			consumer.packetBuffer.add(packet);

		// 			offset += PACKET_SIZE;
		// 			snBase++;
		// 		}
		// 	}
			
		// 	while(consumer.packetBuffer.size() > 0){
		// 		// add level of inderection so that when we update packets to acked they can be updated in the queue
		// 		window.put(consumer.packetBuffer.indexOf(consumer.packetBuffer.get(0)));
		// 	}

			// rh.logRequestComplete();
		} 
	}
}
