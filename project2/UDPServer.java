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
  	ArrayList<Packet> packetBuffer = new ArrayList<Packet>();

		//
		final int PACKET_SIZE = 512;
		final int SERVER_PORT_NUMBER = 10046;
		DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT_NUMBER);
		byte[] requestBuffer = new byte[PACKET_SIZE]; 
		byte[] fileStreamBuffer  = new byte[PACKET_SIZE];
		System.out.println("LISTENING ON PORT: "+ SERVER_PORT_NUMBER);	



		// consumer object which will consume packets from the producer thread
		// a new packet should be consumed (enqueued) when the window advances
		ServerConsumer consumer = new ServerConsumer(serverSocket, packetBuffer, window);

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
			// producer.packetBuffer.add();
      try {
        Thread.sleep(1000);

      } catch(InterruptedException e){
        System.out.println(e);
      }


      //create a new udp packet
			DatagramPacket incomingRequest = new DatagramPacket(requestBuffer, requestBuffer.length);
			
			//wait for server socket to recieve incoming request
			serverSocket.receive(incomingRequest);
      System.out.println("here");

		  // parse the incoming GET request
		  String requestText = new String(incomingRequest.getData()); 
			
			// create a request handler object which will
			RequestHandler requestHandler  = new RequestHandler(requestText);


			fileStreamBuffer = requestHandler.checkSummedResponse().getBytes("UTF-8");

			InetAddress ip = incomingRequest.getAddress(); 
		 	int port = incomingRequest.getPort();

 
			
			System.out.println("Total message length: " + fileStreamBuffer.length);

	    
			// now that we've sucessfully interpreted the request, and created the entire http response
			// we can begin breaking this response up into packets, and pipelining them out to the client
			// process
	    int offset = 0;
		 	int length = 0;
			if(fileStreamBuffer.length < PACKET_SIZE){
				length = fileStreamBuffer.length;
			} else{
				length = PACKET_SIZE;				
			}

			int sequenceNumber = 0;

			boolean done = false;
			while(offset < fileStreamBuffer.length && (done == false)){
		  	// get how many bytes are left to send
				long remainingBytes = fileStreamBuffer.length - offset;
				if(remainingBytes < PACKET_SIZE){
					length = (int)remainingBytes;
					done = true;
				}
				// create a packet object
				Packet packet = new Packet(sequenceNumber%24, Arrays.copyOfRange(fileStreamBuffer, offset, offset + PACKET_SIZE), ip, port);
				// add it to the producers queue of outgoing packets
				packetBuffer.add(packet);	
				offset += PACKET_SIZE;
				sequenceNumber++;			
			}
			
		// 	while(consumer.packetBuffer.size() > 0){
		// 		// add level of inderection so that when we update packets to acked they can be updated in the queue
		// 		window.put(consumer.packetBuffer.indexOf(consumer.packetBuffer.get(0)));
		// 	}

			// rh.logRequestComplete();
		} 
	}
}
