import java.util.ArrayList;

public class Pipeline implements Runnable{

	
	ArrayList<Packet> packetBuffer = new ArrayList<Packet>();
	ArrayList<Packet> window = new ArrayList<Packet>();

	public Pipeline(ArrayList<Packet> packetBuffer)
	{
		this.packetBuffer = packetBuffer;
		System.out.print("PacketBuffer[0]: " + packetBuffer.get(0));
	}

	public void run()
	{
		while (packetBuffer.size() > 0)
		{
			//Check if there is an open slot in the window
			if (window.size() < 8)
			{
				//Dequeue packet from packetBuffer
				window.add(packetBuffer.get(0));
				packetBuffer.remove(0);
			}
		}		
	}

}
