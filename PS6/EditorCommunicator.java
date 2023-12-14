import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Editor: Grace Bech
 * Handles communication to/from the server for the editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 */
public class EditorCommunicator extends Thread {
	public PrintWriter out;		// to server
	public BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			// TODO: OUR CODE
			String message = in.readLine();

			while(message!=null){
				String Command = message.split(" ")[0];

				// handling messages whose command is Drawing
				if(Command.equals("Draw")){
					String type = message.split(" ")[1];
					int x1 = Integer.parseInt(message.split(" ")[2]);
					int y1 = Integer.parseInt(message.split(" ")[3]);
					int x2 = Integer.parseInt(message.split(" ")[4]);
					int y2 = Integer.parseInt(message.split(" ")[5]);
					Color color = new Color(Integer.parseInt(message.split(" ")[6]));

					if(type.equals("ellipse")){
						editor.getSketch().add(new Ellipse(x1, y1, x2, y2, color));
					}
					else if(type.equals("segment")){
						editor.getSketch().add(new Segment(x1, y1, x2, y2, color));
					}
					else if(type.equals("rectangle")){
						editor.getSketch().add(new Rectangle(x1, y1, x2, y2, color));
					}
					else if(type.equals("freehand")){
						ArrayList<Point> data = new Message(message).points;
						Color c = new Message(message).color;
						Polyline toAdd = new Polyline(data, c);
						editor.getSketch().add(toAdd);
					}

				}
				// handling messages whose command is recoloring
				else if(Command.equals("Recolor")){
					int id = Integer.parseInt(message.split(" ")[1]);
					Color color = new Color(Integer.parseInt(message.split(" ")[2]));
					Shape shape = editor.shapeByID(id);
					shape.setColor(color);
				}
				// handling messages whose command is moving
				else if(Command.equals("Move") && editor.isShowing()){
					int id = Integer.parseInt(message.split(" ")[1]);
					Shape shape = editor.shapeByID(id);
					shape.moveBy(Integer.parseInt(message.split(" ")[2]), Integer.parseInt(message.split(" ")[3]));
				}
				// handling messages whose command is Delete
				else if(Command.equals("Delete")){
					int id = Integer.parseInt(message.split(" ")[1]);
					editor.getSketch().getMap().put(id, null);
				}
				editor.repaint();
				message = in.readLine();
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}

}
