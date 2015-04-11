import java.io.File;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

class RequestHandler { 

	String request;
	String contentType;
	String action;
	long contentLength;
	String body;
	File file;
	String specification;
	int responseCode;
	String fileName;

	public RequestHandler(String inputRequest)
	{
		request = inputRequest;
		contentType = "text/plain";
	}

	public int validate()
	{
		String[] messageSections = new String[3];
		messageSections = request.split(" ");
		if(messageSections.length == 3)
		{
			action = messageSections[0];
			fileName = messageSections[1];
			file = new File(fileName);
			if(!messageSections[0].equals("GET"))
			{
				responseCode = 402;	
			}
			else if (!file.exists())
			{
		    		responseCode = 404;	
			}
			else if(!messageSections[2].trim().equals("HTTP/1.0"))
			{
				responseCode = 400;
				System.out.println("400 Specification: " + messageSections[2]);	
			}
			else 
			{
				contentLength = file.length();
				responseCode = 200;
			}
			specification = messageSections[2];
		}
		else
		{
			responseCode = 403;
			specification = "Invalid";
		}	    		
		return responseCode;
    	}

	public String parsedResponse() {
		validate();
	 			
		String response = "";
		response += specification;
		response += " " + responseCode;
		response += " Document Follows " + '\r' + '\n';
		response += "Content-Type: " + contentType + '\r' + '\n';
		response += "Content-Length: " + contentLength + '\r' + '\n';
		response += '\r' + '\n' + '\0';
		
		if (responseCode == 200)
		{
			Charset charset = Charset.forName("UTF-8");
			try{			
			for (String line : Files.readAllLines(Paths.get(fileName), charset))
			{
    				response += line;
			}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		logRequest();
		return response;
		
	}

	public String checkSummedResponse() {
		String response = parsedResponse();
		// parse response into bytestream
		byte[] bytes = response.getBytes();

		//create checksum object.
        Checksum checksum = new CRC32();
	    checksum.update(bytes, 0, bytes.length);

	    // get checksum value
        long checksumValue = checksum.getValue();

        //append checksum
        response += new String( ("Checksum: " + checksumValue) );
        return response;


	}

	public void logRequest(){
 	
		System.out.println("Proccessing " + action + " " + fileName + " type: " + contentType);
		
	}


	public void logRequestComplete(){
 	
		System.out.println("Completed: " + responseCode + " " + action + " " + fileName + " type: " + contentType);
		
	}
	



}
