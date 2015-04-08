import java.util.ArrayList;
import java.util.regex.*;
import java.net.*;
import java.io.*;

public class TransportLayer implements Runnable
{
  ArrayList<Packet> buffer = new ArrayList<Packet>(); 
  DatagramSocket socket;
  int baseSeqNumber = 0;
  int nextSeqNumber = 0;
  int windowSize = 8;
  InetAddress IPAddress;
  int portNumber;
  public TransportLayer(DatagramSocket socketObjectIn, InetAddress IPAddressIn, int portNumberIn){
    this.socket = socketObjectIn;
    this.portNumber = portNumberIn;
    this.IPAddress = IPAddressIn;
  }

  public void run(){
    while(true){
      if(buffer.size() == 8){
        // if we've gotten here it means we've recieved all the packets in some form, now we just need to check if they're valid
        Packet new_p = buffer.get(0);
        if(new_p.getACK() == 0){
          Packet ack = new Packet(new_p.sequenceNumber, new byte[0]);
          byte[] response = ack.getParsedResponse();
          int responseLength = (int)(response.length);
          try {
            socket.send(new DatagramPacket(response, responseLength, IPAddress, portNumber));

          }
          catch (IOException e)
          {
            System.out.println(e);
          }
        }
      }

    }

  }

  public void demux(String data){
    String headerString = "";
    String fullHeaderString = "";
    String payload = "";
    Matcher headerMatcher = Pattern.compile("START:(.+?):END").matcher(data);
    
    if(headerMatcher.find()){
      headerString = headerMatcher.group(1);
      // extract the whole header from "START to END" 
      fullHeaderString = headerMatcher.group(0);
    }

    String[] headers = headerString.split(",");
    // Extract the data of the packet by removing the fullHeaderString
    payload = data.replace(fullHeaderString, "");
    byte[] payloadBytes=payload.getBytes();
    // create new packet with payload and header values, then
    // add the packet to the buffer
    Packet packet = new Packet(Integer.parseInt(headers[0]), payloadBytes);
    buffer.add(packet);
  }

}
