import java.util.ArrayList;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.*;
class ServerProducer implements Runnable {
  private BlockingQueue<Integer> window;
  public volatile ArrayList<Integer> packetBuffer = new ArrayList<Integer>();

  public ServerProducer(ArrayList<Integer> packetBufferIn, BlockingQueue<Integer> windowIn){
    this.window = windowIn;
    this.packetBuffer = packetBufferIn;

  }

  public void run() {
    // add level of inderection so that when we update packets to acked they can be updated in the queue
    while(true){
      if(packetBuffer.size() > 3){

        try {
          window.put(packetBuffer.remove(0));
          Thread.sleep(1000);

        } catch(InterruptedException e){
          System.out.println(e);
        }



        System.out.println(packetBuffer.size());
      }
      // Packet new_packet = packetBuffer.get(0);
      // try{
      //   window.put(packetBuffer.indexOf(new_packet));      
      // } catch (InterruptedException e) {
      //   System.out.println(e);
      // }
    }
  }






}