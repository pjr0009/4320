public class Packet 
{
	//Class variables
	boolean ACK;
	boolean NAK;	
	int sequenceNumber;
	final int PACKET_SIZE = 512;
	byte[] payload = new byte[PACKET_SIZE];
	
	
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

	public boolean getACK()
	{
		return this.ACK;
	}

	public boolean getNAK()
	{
		return this.NAK;
	}
	
	public byte[] getParsedResponse()
	{
		String response = "Hello.";
		return response.getBytes();
	}

}
