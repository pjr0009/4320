public class Packet 
{
	//Class variables
	boolean ACK;
	boolean NAK;	
	int sequenceNumber;
	final int PACKET_SIZE = 512;
	byte[] payload = new byte[PACKET_SIZE];
	long checksumValue;
	
	
	public Packet(int sequenceNumberIn, byte[] payloadIn)
	{
		this.ACK = false;
		this.NAK = false;
		this.sequenceNumber = sequenceNumberIn;
		this.payload = payloadIn;
	}

	public void setACK()
	{
		this.ACK = true;
	}

	public void setNAK()
	{
		this.NAK = true;
	}

	public int getACK()
	{
		return (this.ACK ? 1 : 0);
	}

	public int getNAK()
	{
		return (this.NAK ? 1 : 0);
	}
	
	public byte[] getParsedResponse()
	{
		//Format: Sequence Number, ACK, NAK, Checksum
		String response = "START:";		
		response += sequenceNumber;
		response += "," + getACK();
		response += "," + getNAK();
		response += "," + computeChecksum() + ":END" + (new String(payload));
		return response.getBytes();
	}
	
	public long computeChecksum()
	{
		return 1;
	}
}
