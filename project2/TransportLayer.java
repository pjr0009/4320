import java.util.ArrayList;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;


public class TransportLayer implements Runnable {
  volatile ArrayList < Packet > buffer = new ArrayList < Packet > ();
  DatagramSocket socket;
  int baseSeqNumber = 0;
  // int nextSeqNumber = 0;
  int windowSize = 8;
  InetAddress IPAddress;
  int portNumber;
  
  public TransportLayer(DatagramSocket socketObjectIn, InetAddress IPAddressIn, int portNumberIn) {
    this.socket = socketObjectIn;
    this.portNumber = portNumberIn;
    this.IPAddress = IPAddressIn;
  }

  public void run() {
    while (true) {
      // if we've gotten here it means we've recieved all the packets in some form, now we just need to check if they're valid
      for (int i = baseSeqNumber; i < baseSeqNumber + 8; i++) {
        if (buffer.get(i).getACK() != 1) {
          // if a packet has arrived that hasnt been acknowledged, send ack
          System.out.println("Base Seq Number "+baseSeqNumber);
          System.out.println("Seq Number End"+baseSeqNumber+8);


          System.out.println("Sending ack for packet in window at index "+i);
          Packet current_packet = buffer.get(i);
          Packet ack = new Packet(current_packet.sequenceNumber);
          buffer.get(i).setACK("1");
          ack.setACK("1");
          byte[] response = ack.getParsedResponse();
          int responseLength = response.length;
          try {
            System.out.println("Sending ACK");
            try {
              wait(5000);
            } catch (InterruptedException e) {
              System.out.println(e);
            }
            socket.send(new DatagramPacket(response, responseLength, IPAddress, portNumber));

          } catch (IOException e) {
            System.out.println(e);
          }
        }
      }
    }
  }



  public void demux(String data) {
    System.out.println("packet recieved");
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
    System.out.println("IP Address: " + packet.IPAddress);
    System.out.println("PortNumber: " + packet.portNumber);
    System.out.println("Sequence Number: " + packet.portNumber);
    System.out.println(buffer.size());
    buffer.add(packet);
  }


}