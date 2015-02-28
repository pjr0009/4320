import java.io.*; 
import java.net.*; 
import java.util.Random;
  
class UDPClient { 
    public static void main(String args[]) throws Exception 
    { 
      // input stream reader for user input
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
      
      // datagram socket to recieve packets
      DatagramSocket clientSocket = new DatagramSocket(); 
      
      //host internet address name (should probably use a user friendly name here)
      String InetAddr= "131.204.14.207";
      
      for(int i = 0; i < args.length; i++) {
          if(args[i].equals("--local")){
            InetAddr = "127.0.0.1"; // if local switch is present, use local host, meaning client & server are running on same machine.
            System.out.println("Using Localhost");
          }
      }

      InetAddress IPAddress = InetAddress.getByName(InetAddr); 
      
      // use an array list for recieve data here, we want our packets to be stored
      // in recieve data, and have it grow dynamically (may want to limit it?)
      // send data should be small because we're just sending http requests
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[256]; 
  
      // get http request from user
      System.out.println("Enter valid HTTP/1.0 request. Currently, only GET requests are supported.");
      System.out.print("Request: ");

      String request = inFromUser.readLine(); 
      sendData = request.getBytes();
      
      // construct outgoing request packet
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10046); 
  

      clientSocket.send(sendPacket); 
  
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
 	
      int i = 0;
      // recieve first packet
      clientSocket.receive(receivePacket);
      String data = "";
      while (receivePacket.getData() != null) {
        data +=  new String(receivePacket.getData());
      	receiveData = new byte[256];
        receivePacket = new DatagramPacket(receiveData, receiveData.length); 
      	try {
          clientSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
          break;
        }
      }
      System.out.println(data);
  
      clientSocket.close(); 
} 





public byte[] gremlin(double inputProbability, byte byteArray[])
{
    double damageProbability = inputProbability;
    Random randomGenerator = new Random();
    double randomDouble = randomGenerator.nextDouble();
    //Make sure value is not 0.0
    while (randomDouble == 0.0)
    {
        randomDouble = randomGenerator.nextDouble();
    }

    if (randomDouble <= damageProbability)
    {
        int numBytesChanged = 0;
        
        //Gremlin function corrupts the file
        //.5 probability that one byte is changed
        if (randomDouble <= 0.5)
        {
            //change one byte
            numBytesChanged = 1;
        }
        //.3 probability that two bytes are changed
        else if (randomDouble < 0.8)
        {
            //change two bytes
            numBytesChanged = 2;
        }
        //.2 probability that 3 bytes are changed
        else 
        {
            //change three bytes
            numBytesChanged = 3;
        }
         
        //Change however many number of bytes needs to be changed (at random)
        int randomInt;
        for (int i = 0; i < numBytesChanged; i++)
        {
            randomInt = randomGenerator.nextInt(256);
            byte indexElement = byteArray[randomInt];
            byte mask = (byte)Integer.parseInt("01111111", 2);
            byte corruptedValue = (byte)(indexElement & mask);
            byteArray[randomInt] = corruptedValue;
        }
        //The packet was corrupted.
        System.out.println("Debug:The packet was corrupted in the Gremlin function."); //***add packet number
        return byteArray;
    }
    else
    {
        //Gremlin function does not corrupt the file        
        System.out.println("Debug:The packet was not corrupted in the Gremlin function.");//**add packet number
        return byteArray;
    }
}
}