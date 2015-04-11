import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;
class ServerProducer implements Runnable {
  private BlockingQueue<Integer> window;
  public volatile ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
  DatagramSocket socket;
  private int nextPacket = 0;

  public ServerProducer(DatagramSocket socketIn,  ArrayList<Packet> packetBufferIn, BlockingQueue<Integer> windowIn){
    this.window = windowIn;
    this.packetBuffer = packetBufferIn;
    this.socket = socketIn;

  }

  public void run() {
    // add level of inderection so that when we update packets to acked they can be updated in the queue
    while(true){
      if(packetBuffer.size() > 0){
        try {

          synchronized(this){
            // the producer will add to the window, the sequence number
            // of the packet, we use the number rather than the packet object
            // itself so that we can arbitrarily update attributes on packets 
            // like setting the ack, because we can select randomly from a queue
            int nextPacketIndex = findPacketCandidate();
            if(nextPacketIndex > -1){
              Packet packet = packetBuffer.get(nextPacketIndex);
              packet.queued = true;
              System.out.println("Adding packet #" + packet.sequenceNumber + " to the window." );
              window.put(packet.sequenceNumber);
              
              // now we actually need to send out the added packet
              byte[] response = packet.getParsedResponse();
              int responseLength = (response.length);
              try {
               System.out.println("SENDING NEW PACKET");
               System.out.println("IP Address: " + packet.IPAddress);
               System.out.println("PortNumber: " + packet.portNumber);
               System.out.println("Sequence Number: " + packet.portNumber);
               System.out.println("Sending Packet " + packet.sequenceNumber + " ...");
               socket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
               System.out.println("Packet " + packet.sequenceNumber + " sent");
              }
              catch (IOException e)
              {
               System.out.println(e);
              }




            }
          }
          Thread.sleep(1000);

        } catch(InterruptedException e){
          System.out.println(e);
        }
      }
    }

  }


  public int findPacketCandidate(){
    int cand = -1;
    for(int i = 0; i < packetBuffer.size(); i++){
      if(packetBuffer.get(i).isQueueable()){
        cand = i;
        break;
      }
    }
    return cand;
  }




}