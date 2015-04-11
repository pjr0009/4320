import java.net.*;

public class Packet {
	//Class variables
	boolean ACK = false;
	boolean NAK = false;
	boolean queued = false;
	int sequenceNumber = -1;
	final int PACKET_SIZE = 512;
	byte[] payload = new byte[PACKET_SIZE];
	long checksumValue;
	public int portNumber;
	public InetAddress IPAddress;


	public Packet(int sequenceNumberIn, byte[] payloadIn, InetAddress IPAddressIn, int portIn) {
		this.sequenceNumber = sequenceNumberIn;
		this.payload = payloadIn;
		this.IPAddress = IPAddressIn;
		this.portNumber = portIn;
	}

	public Packet(int sequenceNumberIn) {
		this.ACK = false;
		this.NAK = false;
		this.sequenceNumber = sequenceNumberIn;
		this.payload = null;
	}


	public boolean isQueueable() {
		if((getACK() == 0) && (getNAK() == 0) && (queued == false)){
			return true;
		} else {
			return false;
		}
	}
	public boolean isHTTP(){
		if((getACK()==0) && (getNAK() == 0) && (sequenceNumber == -1)){
			return true;
		} else {
			return false;
		}
	}

	public void setACK(String arg) {
		if (arg.equals("1")) {
			this.ACK = true;
		} else {
			this.ACK = false;
		}
	}

	public void setNAK(String arg) {
		if (arg.equals("1")) {
			this.NAK = true;
		} else {
			this.NAK = false;
		}
	}

	public int getACK() {

		return (ACK ? 1 : 0);
	}

	public int getNAK() {
		return (this.NAK ? 1 : 0);
	}

	public byte[] getParsedResponse() {
		//Format: Sequence Number, ACK, NAK, Checksum
		String response = "START:";
		response += sequenceNumber;
		response += "," + getACK();
		response += "," + getNAK();
		response += "," + computeChecksum() + ":END";
		if (payload != null) {
			response += (new String(payload));
		}
		return response.getBytes();
	}

	public long computeChecksum() {
		return 1;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

}