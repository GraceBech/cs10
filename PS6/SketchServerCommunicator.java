import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Editor: Grace Bech
 * Handles communication between the server and one client, for SketchServer
 *@author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param
	 */
	public void send(String msg) {
		out.println(msg);
	}


	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// TODO: OUR CODE
			// Tell the client the current state of the world
			if (!Sketch.sketches.isEmpty()){
				String currState  = "";
				currState += "Draw ";
				for(Map.Entry<Integer, Shape> record: Sketch.sketches.entrySet()){
					// building a string that has the ids of the shapes
					currState+= (record.getValue().toString()+ " ");
					System.out.println(currState);
					helperServer(currState);
				}
			}

			// TODO: OUR CODE
			// Keep getting and handling messages from the client
			//broadcast to all clients
			String message = in.readLine();
			while((message = in.readLine())!=null) {
				helperServer(message);
			}

			// handle the message
			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Helper function to hold all the commands
	public void helperServer(String line){
		System.out.println("received:  ");
		System.out.println(line);
		String type = "";
		String NewCommand;

		NewCommand = line.split(" ")[0];
		type = line.split(" ")[1];
		System.out.println(type);
		System.out.println(NewCommand);


		if (NewCommand.equals("Draw")) {
			Message received = new Message(line);
			System.out.println(received.points);

			if (type.equals("ellipse")) {
				server.getSketch().add(new Ellipse(received.points.get(0).x, received.points.get(0).y, received.color));
			}
			else if (type.equals("segment")) {
				server.getSketch().add(new Segment(received.points.get(0).x, received.points.get(0).y, received.color));
			}
			else if (type.equals("rectangle")) {
				server.getSketch().add(new Rectangle(received.points.get(0).x,received.points.get(0).y, received.color));
			}
			else if (type.equals("freehand")) {
				server.getSketch().add(new Polyline(received.points, received.color));
			}

		}


		else if (NewCommand.equals("Move") || NewCommand.equals("Delete") || NewCommand.equals("Recolor")) {
			int id = Integer.parseInt(line.split(" ")[1]);

			if (NewCommand.equals("Move")) {
				System.out.println(id + "the Shape ID");
				Shape shape = server.getSketch().getMap().get(id);
				shape.moveBy(Integer.parseInt(line.split(" ")[2]), Integer.parseInt(line.split(" ")[3]));
			}
			else if (NewCommand.equals("Delete") ) {
				server.getSketch().getMap().remove(id);
			}
			else {
				Color color = new Color(Integer.parseInt(line.split(" ")[2]));
				Shape shape = server.getSketch().getMap().get(id);
				shape.setColor(color);
				server.getSketch().getMap().put(id, shape);
			}
		}
		server.broadcast(line);
	}
}
