package awpost;

import javax.ws.rs.core.MultivaluedMap;

import processing.core.PApplet;
import processing.serial.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class AWPost extends PApplet {
	Serial myPort;        // The serial port
	int graphXPos = 1;    // the horizontal position of the graph:
	
	private final String BaseURI = "http://a4w.heroku.com/messages";
	private WebResource wr;
	private int lastVal = 0;
	private boolean report = true;

	public void setup() {
		size(400, 300);        // window size
		
		Client client = Client.create();
		wr = client.resource(BaseURI);
		
		// List all the available serial ports
		println(Serial.list());
		// I know that the first port in the serial list on my mac
		// is usually my Arduino module, so I open Serial.list()[0].
		// Open whatever port is the one you're using.
		myPort = new Serial(this, Serial.list()[0], 9600);

		// set inital background:
		background(48,31,65);
	}
	
	public void draw() {
		background(lastVal == 0 ? 0 : 255);
	}
	
	public void keyPressed() {
		switch (key) {
		case 'o': // turn reporting on/off
			report = !report;
			println(report ? "will report" : "will NOT report");
			break;
		case 'p':	// panic button
			lastVal = (lastVal == 0) ? 1 : 0;
			report();
			break;
		}
	}
	
	private void report() {
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("device_id", "1");
		formData.add("status", (lastVal == 0) ? "OFF" : "ON");
		ClientResponse response = wr.type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
		Status s = response.getClientResponseStatus();
		println(s.getStatusCode() + ": " + s.toString());
	}

	public void serialEvent (Serial myPort) {
		// get the byte:
		lastVal = myPort.read();
		
		println((lastVal == 0) ? "OFF" : "ON");
		
		if (report) {
			report();
		}
	}
}