import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.regex.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.concurrent.*;




class UDPClient {
  public static void main(String args[]) throws Exception {
    double gremlinDamageProbability;
    double gremlinLossProbability;
    Scanner scan = new Scanner(System. in );
    System.out.print("Input damage probability of Gremlin Activation: ");
    gremlinDamageProbability = scan.nextDouble();
    System.out.print("Input loss probability of Gremlin Activation: ");
    gremlinLossProbability = scan.nextDouble();
    final int PORT_NUMBER = 10046;
    // input stream reader for user input
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System. in ));

    // datagram socket to recieve packets
    DatagramSocket clientSocket = new DatagramSocket();

    //host internet address name (should probably use a user friendly name here)
    String InetAddr = "131.204.14.207";

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--local")) {
        InetAddr = "127.0.0.1"; // if local switch is present, use local host, meaning client & server are running on same machine.
        System.out.println("Using Localhost");
      }
    }

    InetAddress IPAddress = InetAddress.getByName(InetAddr);

    byte[] sendData = new byte[538];
    byte[] receiveData = new byte[538];

    // get http request from user
    //System.out.println("Enter valid HTTP/1.0 request. Currently, only GET requests are supported.");
    // System.out.print("Request: ");
    String request = "GET content/test3.html HTTP/1.0";
    //String request = inFromUser.readLine(); 
    sendData = request.getBytes();

    // construct outgoing request packet
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT_NUMBER);


    clientSocket.send(sendPacket);


    // Now that we've sent out the request, we need to recieve all the data
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);



    int i = 0;

    TransportLayer transport = new TransportLayer(clientSocket, IPAddress, PORT_NUMBER, gremlinDamageProbability, gremlinLossProbability);
    Thread clientThread = new Thread(transport);
    // recieve first packet
    clientSocket.receive(receivePacket);
    clientThread.start();
    String data = "";
    while (receivePacket.getData() != null && receivePacket.getData().length > 0) {
      clientSocket.setSoTimeout(10000);
      //extract checksum from recieved message
      data = new String(receivePacket.getData());
      transport.demux(data); 
      // create a new packet and try to receieve next data        
      receivePacket = new DatagramPacket(receiveData, receiveData.length);
      try {
        clientSocket.receive(receivePacket);
      } catch (SocketTimeoutException e) {
        System.exit(1);
      }
    }
    clientSocket.close();
  }




  public static long computeChecksum(String data) {

    // parse response into bytestream
    byte[] bytes = data.getBytes();

    //create checksum object.
    Checksum checksum = new CRC32();
    checksum.update(bytes, 0, bytes.length);

    // get checksum value
    long checksumValue = checksum.getValue();

    return checksumValue;

  }

  //Returns null if packet lost
}