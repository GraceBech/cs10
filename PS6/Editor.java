

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;

/**
 * Client-server graphical editor
 * editor : Grace Bech
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI

 */

public class Editor extends JFrame {
	private static String serverIP = "localhost"; // IP address of sketch server

	private static final int width = 800, height = 800;	// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;	// drawing/moving/recoloring/deleting objects
	 private String shapeType = "ellipse";	// type of object to add
	private Color color = Color.black; // current drawing color
	ArrayList<Point> points = new ArrayList<>();

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

	private Point pointOne = null;


	// Communication
	private EditorCommunicator comm;	// communication with the sketch server

	public Editor() {
		super("Graphical Editor");
		sketch = new Sketch();
		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();
		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {

		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// TODO:  OUR CODE
		// Looping over all the shapes in sketch and drawing them
		for(Map.Entry<Integer, Shape> shapes: sketch.getMap().entrySet()) {
			if(shapes.getValue() != null){
				shapes.getValue().draw(g);
			}
		}
		if(curr != null){
			curr.draw(g);
		}

	}

	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		// TODO: OUR CODE

		// initializing a new shape(depending on which one) and setting it to curr if we are in Draw mode

			if (mode == Mode.DRAW) {
				drawFrom = p;
				if (shapeType.equals("ellipse")) {
					curr = new Ellipse(p.x, p.y, color);
				} else if (shapeType.equals("segment")) {
					curr = new Segment(p.x, p.y, p.x, p.y, color);

//					curr = new Polyline(p.x, p.y, color);
				} else if (shapeType.equals("rectangle")) {
					curr = new Rectangle(p.x, p.y, p.x, p.y, color);
				} else if (shapeType.equals("freehand")) {
					ArrayList<Point> newPolyPoint = new ArrayList<>();
					newPolyPoint.add(p);
					curr = new Polyline(newPolyPoint, color);
//
			}
			// changing the color if the mode is set to Recolor
			else if (mode == Mode.RECOLOR) {
				if(curr.contains(p.x, p.y)) {
					curr.setColor(color);
					curr = sketch.getMap().get(currShapeID(p));
					comm.send("Recolor " + currShapeID(p) + " " + color.getRGB());

					repaint();
				}
			}
//				if (shape.contains(p.x, p.y)){
////                shape.setColor(color);
////                repaint();  // redraw
		}
			// updating moveFrom and pointOne if the mode is set to move
			else if (mode == Mode.MOVE && curr != null) {
				if (curr.contains(p.x, p.y)) {
					curr.moveBy(moveFrom.x - drawFrom.x, moveFrom.y - drawFrom.y);
					repaint();  // redraw
//				moveFrom = p;
//				pointOne = p;
				}
			}
			// sending a message to the communicator to delete the shape selected for delete
			else if (mode == Mode.DELETE) {
				comm.send("Delete " + currShapeID(p));

			}




	}
	private void handleRecolor(Point p) {
		curr = sketch.getMap().get(currShapeID(p));
		if (curr != null) {
			curr.draw(getGraphics());
			curr.setColor(color);
			comm.send("Recolor " + currShapeID(p) + " " + color.getRGB());
//			sketch.getShape(p.x, p.y).setColor(color);

		}
	}
	private void updateMoveFromAndPointOne(Point p) {
		moveFrom = p;
		pointOne = p;
	}
	/**
	 * Helper Methode to retrieve the shapes by their IDs
	 * @param p point where the current shape selected is at
	 * @return the integer id for each shape
	 */
	private int currShapeID(Point p) {
		ArrayList<Integer> IdKeys = new ArrayList<>(Sketch.sketches.descendingKeySet());
		for (int i = 0; i < IdKeys.size(); i++) {
			if (Sketch.sketches.get(i) != null && Sketch.sketches.get(i).contains(p.x, p.y)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper Methode that runs into the sketch and retrieves the shape associated with an ID of interest
	 * @param id represents each shape distinctively in the sketch
	 * @return the shape type
	 */
	public Shape shapeByID(int id){
		for(Map.Entry<Integer, Shape> entry: sketch.getMap().entrySet()){
			if(entry.getKey() == id && entry.getValue() != null){
				return entry.getValue();
			}
		}
		return null;
	}


	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		// TODO: YOUR CODE HERE
		System.out.println("dragged to " + p); // Add this line to print the dragged position

		if (mode == Mode.DRAW) {
			// Update drawing based on drag
			if (shapeType.equals("ellipse")) {
				Ellipse myEllipse = (Ellipse) curr; // casting curr to Ellipse
				myEllipse.setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			} else if (shapeType.equals("segment")) {
				Segment mySegment = (Segment) curr; // casting curr to segment
				mySegment.setEnd(p.x, p.y);
			} else if (shapeType.equals("rectangle")) {
				Rectangle myRectangle = (Rectangle) curr; // casting curr to rectangle
				myRectangle.setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			} else if (shapeType.equals("freehand")) {
				Polyline Poly = (Polyline) curr; // casting curr to polyline
				Poly.points.add(p);
			}
		} else if (mode == Mode.MOVE) {
			// Handle move

			movingId = currShapeID(pointOne);
			comm.send("Move " + movingId + " " + (p.x - moveFrom.x) + " " + (p.y - moveFrom.y));
			moveFrom = p;
		} else if (mode == Mode.RECOLOR) {
			// Recolor the shape...
			handleRecolor(p);
		} else if (mode == Mode.DELETE) {
			// Delete the shape
			int shapeId = currShapeID(p);
			if (shapeId != -1) {
				comm.send("Delete " + shapeId);
//				sketch.getShape(p.x, p.y).draw()= null;
			}
		}

		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it
	 */
	private void handleRelease() {
		// TODO: OUR CODE
		if (mode == Mode.DRAW){
			comm.send("Draw "+ curr.toString());
			points.clear(); // come back to this on why freehand can't stay
		}
		else if (mode == Mode.MOVE){
			moveFrom = null;
		}
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});
	}
}
//    private void handlePress(Point p) {
//        // TODO: YOUR CODE HERE
//        // In drawing mode, start drawing a new shape
//        // In moving mode, start dragging if clicked in the shape
//        // In recoloring mode, change the shape's color if clicked in it
//        // In deleting mode, delete the shape if clicked in it
//        // Be sure to refresh the canvas (repaint) if the appearance has changed
//        drawFrom = p;
//        if (mode == Mode.DRAW){
//            shape = new Ellipse8( p.x, p.y, color);
//            repaint();
//        }
//// if the ellipse is moving and it contains x and y points
//        else if (mode == Mode.MOVE){
//            if ( shape.contains( p.x, p.y)){
//                shape.moveBy(moveFrom.x - drawFrom.x, moveFrom.y - drawFrom.y );
//                repaint();  // redraw
//            }
//        }
//// If it's on recolor mode and it is within the canvas
//        else if (mode == Mode.RECOLOR){
//            if (shape.contains(p.x, p.y)){
//                shape.setColor(color);
//                repaint();  // redraw
//            }
//        }
//// if the ellipse is delete, and within the canvas
//        else if (mode == Mode.DELETE){
//            if (shape.contains(p.x, p.y)) {
//                shape = null;  // set the shape to null
//                repaint();
//            }