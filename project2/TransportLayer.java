import java.util.ArrayList;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import java.io.PrintWriter;
import java.util.concurrent.*;


public class TransportLayer implements Runnable {
  public volatile BlockingQueue<Packet> buffer = new ArrayBlockingQueue<Packet>(8); // blocking window, consumer
  DatagramSocket socket;
  int baseSeqNumber = 0;
  // int nextSeqNumber = 0;
  int windowSize = 8;
  InetAddress IPAddress;
  int portNumber;
  File file;
  PrintWriter writer;
  int packetCount;
  public TransportLayer(DatagramSocket socketObjectIn, InetAddress IPAddressIn, int portNumberIn) {
    this.socket = socketObjectIn;
    this.portNumber = portNumberIn;
    this.IPAddress = IPAddressIn;
    try{
      this.file = new File("Output.html");
      this.writer = new PrintWriter(new FileOutputStream(file, true));
    } catch(Exception e){
      System.out.println(e);
    }
  }

  public void run() {
    while (true) {
      synchronized(this){
        if(buffer.size() > 0){
          int limit = 0;
          // for (int i = baseSeqNumber; i < limit; i++) {
            if (buffer.peek().getACK() == 1) {
              try{
                buffer.take();
              }catch(Exception e){
                System.out.println(e);
              } 
              updateInterface();
            } else{
              // if a packet has arrived that hasnt been acknowledged, send ack
              try {
                Packet current_packet = buffer.take();
                packetCount += 1;
                current_packet.setACK("1");
                Packet ack = new Packet(current_packet.sequenceNumber);
                ack.setACK("1");
                byte[] response = ack.getParsedResponse();
                int responseLength = response.length;
                updateInterface();
                socket.send(new DatagramPacket(response, responseLength, IPAddress, portNumber));
                baseSeqNumber += 1;

                if(true || current_packet.sequenceNumber == baseSeqNumber){
                  writer.append(new String(current_packet.payload));
                  writer.flush();
                  Thread.sleep(250);
                }

              } catch (Exception e) {
                System.out.println(e);
              }
            }
          // }
        }
      }
    }
  }



  public void updateInterface(){
    
    // clear window attribution: http://stackoverflow.com/questions/4888362/commands-in-java-to-clear-the-screen
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
    Object[] packetArray = buffer.toArray();
    for(int i = 0; i < packetArray.length; i++){
      Packet p = (Packet)packetArray[i];
      System.out.print(" " + p.sequenceNumber);
    }
    System.out.println("");
    System.out.println("Total Packets Recieved: " + packetCount);

  }



  public void demux(String data) {
    String headerString = "";
    String fullHeaderString = "";
    String payload = "";
    Matcher headerMatcher = Pattern.compile("START:(.+?):END").matcher(data);

    if (headerMatcher.find()) {
      headerString = headerMatcher.group(1);
      // extract the whole header from "START to END" 
      fullHeaderString = headerMatcher.group(0);
    }

    String[] headers = headerString.split(",");
    // Extract the data of the packet by removing the fullHeaderString
    payload = data.replace(fullHeaderString, "");
    byte[] payloadBytes = payload.getBytes();
    // create new packet with payload and header values, then
    // add the packet to the buffer
    Packet packet = new Packet(Integer.parseInt(headers[0]), payloadBytes, IPAddress, portNumber);
    try{
      buffer.put(packet);
    } catch (Exception e) {
      System.out.println(e);
    }
  }


}