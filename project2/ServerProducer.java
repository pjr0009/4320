import java.util.ArrayList;
import java.util.Arrays;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.Collections;

class ServerProducer implements Runnable {
  private BlockingQueue<Integer> window;
  public volatile ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
  DatagramSocket socket;
  private int nextPacket = 0;

  public ServerProducer(DatagramSocket socketIn,  ArrayList<Packet> packetBufferIn, BlockingQueue<Integer> windowIn){
    this.window = windowIn;
    this.packetBuffer = packetBufferIn;
    this.socket = socketIn;
    
    // String InetAddr = "131.204.14.207";
    // try{ 
    //   InetAddress IPAddress = InetAddress.getByName(InetAddr);
    //   Packet finishPacket = new Packet(0, new byte[538], IPAddress, 10046);
    //   String payload = "finished";
    //   finishPacket.payload = payload.getBytes();
    //   this.packetBuffer.add(finishPacket);
    // } 

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
              window.put(packet.sequenceNumber);
              
              // now we actually need to send out the added packet
              byte[] response = packet.getParsedResponse();
              int responseLength = (response.length);
              try {
                socket.send(new DatagramPacket(response, responseLength, packet.IPAddress, packet.portNumber));
                updateInterface();
              }
              catch (IOException e)
              {
               System.out.println(e);
              }




            }
          }
          // Thread.sleep(1000);

        } catch(InterruptedException e){
          System.out.println(e);
        }
      }
    }

  }

  public void updateInterface(){
    Object[] sequenceNumbers = window.toArray();
    
    //clear window attribution: http://stackoverflow.com/questions/4888362/commands-in-java-to-clear-the-screen
    final String ANSI_CLS = "\u001b[2J";
    final String ANSI_HOME = "\u001b[H";
    System.out.print(ANSI_CLS + ANSI_HOME);
    System.out.flush();
    String header = "";
    for(int i = 0; i < 70; i++){
      header += "*";
    }
    System.out.println(header);
    System.out.print("WINDOW: ");
    System.out.println(Arrays.toString(sequenceNumbers));
    System.out.print("PACKETS LEFT TO SEND: ");
    for(int i =0; i < packetBuffer.size(); i++){
      System.out.print(" " +packetBuffer.get(i).sequenceNumber);
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