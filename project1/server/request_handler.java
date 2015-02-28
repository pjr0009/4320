import java.io.File;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.io.IOException;


class RequestHandler { 

	String request;
	String contentType;
	long contentLength;
	String body;
	File file;
	String specification;
	int responseCode;
	String fileName;

	public RequestHandler(String inputRequest)
	{
		request = inputRequest;
		System.out.println("Received Input Request: " + inputRequest);
		contentType = "text/plain";
	}

	public int requestValidator()
	{
		String[] messageSections = new String[3];
		messageSections = request.split(" ");
		if(messageSections.length == 3)
		{
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

	public String parsedResponse()
	{
		requestValidator();
	 			
		String testResponse = "";
		testResponse += specification;
		testResponse += " " + responseCode;
		testResponse += " Document Follows " + '\r' + '\n';
		testResponse += "Content-Type: " + contentType + '\r' + '\n';
		testResponse += "Content-Length: " + contentLength + '\r' + '\n';
		testResponse += "Checksum: " + computeChecksum() + '\r' + '\n';
		testResponse += '\r' + '\n' + '\0';
		
		if (responseCode == 200)
		{
			Charset charset = Charset.forName("UTF-8");
			try{			
			for (String line : Files.readAllLines(Paths.get(fileName), charset))
			{
    				testResponse += line;
			}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	



		//testResponse += file contents

		return testResponse;
		
	}

	public void logRequest(){
 	
		System.out.println("Content-Type: " + contentType + '\r' + '\n');
		System.out.println("Content-Length: " + contentLength + '\r' + '\n');
		
	}

	



}