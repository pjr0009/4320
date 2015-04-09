public class Packet 
{
	//Class variables
	boolean ACK = false;
	boolean NAK = false;	
	int sequenceNumber;
	final int PACKET_SIZE = 512;
	byte[] payload = new byte[PACKET_SIZE];
	long checksumValue;
	InetAddress IpAddress;
	
	
	public Packet(int sequenceNumberIn, byte[] payloadIn, InetAddress IpAddressIn)
	{
		this.sequenceNumber = sequenceNumberIn;
		this.payload = payloadIn;
		this.IpAddress = IpAddressIn;
	}

        public Packet(int sequenceNumberIn)
        {
                this.ACK = false;
                this.NAK = false;
                this.sequenceNumber = sequenceNumberIn;
		this.payload = null;
        }



	public void setACK(String arg)
	{
		if (arg.equals("1"))
		{	
			this.ACK = true;
		}
		else
		{
			this.ACK = false;
		}
	}

	public void setNAK(String arg)
	{
		if (arg.equals("1"))
		{		
			this.NAK = true;
		}
		else
		{
			this.NAK = false;
		}
	}

	public int getACK()
	{	
		
		return (ACK ? 1 : 0);
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
		response += "," + computeChecksum() + ":END";
		if(payload != null){
			response += (new String(payload));
		}
		return response.getBytes();
	}
	
	public long computeChecksum()
	{
		return 1;
	}
	
	public int getSequenceNumber()
	{
		return this.sequenceNumber;
	}
}
