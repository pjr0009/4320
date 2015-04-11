import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;
class ServerProducer implements Runnable {
  private BlockingQueue<Integer> window;
  public volatile ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
  private int nextPacket = 0;

  public ServerProducer(ArrayList<Packet> packetBufferIn, BlockingQueue<Integer> windowIn){
    this.window = windowIn;
    this.packetBuffer = packetBufferIn;

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
              Packet p = packetBuffer.get(nextPacketIndex);
              p.queued = true;
              System.out.println("Adding packet #" + p.sequenceNumber + " to the window." );
              window.put(p.sequenceNumber);

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