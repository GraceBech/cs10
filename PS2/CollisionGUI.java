//import net.datastructures.Tree;
//import org.bytedeco.javacv.Blobs;
//
//import java.awt.*;
//
//import javax.swing.*;
//
//import java.util.List;
//import java.util.ArrayList;
//
///**
// * Date : October 10th 2023.
// * Using a quadtree for collision detection
// * Editor : Grace Bech
// * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
// * @author CBK, Spring 2016, updated for blobs
// * @author CBK, Fall 2016, using generic PointQuadtree
// */
//public class CollisionGUI extends DrawingGUI {
//	private static final int width=800, height=600;		// size of the universe
//
//	private List<Blob> blobs;						// all the blobs
//	private List<Blob> colliders;					// the blobs who collided at this step
//	private char blobType = 'b';						// what type of blob to create
//	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
//	private int delay = 100;							// timer control
//
//	public CollisionGUI() {
//		super("super-collider", width, height);
//
//		blobs = new ArrayList<Blob>();
//
//		// Timer drives the animation.
//		startTimer();
//	}
//
//	/**
//	 * Adds an blob of the current blobType at the location
//	 */
//	private void add(int x, int y) {
//		if (blobType=='b') {
//			blobs.add(new Bouncer(x,y,width,height));
//		}
//		else if (blobType=='w') {
//			blobs.add(new Wanderer(x,y));
//		}
//		else {
//			System.err.println("Unknown blob type "+blobType);
//		}
//	}
//
//	/**
//	 * DrawingGUI method, here creating a new blob
//	 */
//	public void handleMousePress(int x, int y) {
//		add(x,y);
//		repaint();
//	}
//
//	/**
//	 * DrawingGUI method
//	 */
//	public void handleKeyPress(char k) {
//		if (k == 'f') { // faster
//			if (delay>1) delay /= 2;
//			setTimerDelay(delay);
//			System.out.println("delay:"+delay);
//		}
//		else if (k == 's') { // slower
//			delay *= 2;
//			setTimerDelay(delay);
//			System.out.println("delay:"+delay);
//		}
//		else if (k == 'r') { // add some new blobs at random positions
//			for (int i=0; i<10; i++) {
//				add((int)(width*Math.random()), (int)(height*Math.random()));
//				repaint();
//			}
//		}
//		else if (k == 'c' || k == 'd') { // control how collisions are handled
//			collisionHandler = k;
//			System.out.println("collision:"+k);
//		}
//		else { // set the type for new blobs
//			blobType = k;
//		}
//	}
//
//	/**
//	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
//	 */
//	public void draw(Graphics g) {
//		// TODO: YOUR CODE HERE
//		// Ask all the blobs to draw themselves.
//		//loop over the lst of blobs
//		// on blob.draw(g)
//
//		g.setColor(Color.BLACK);   // set the blob color to black
//		for (Blob b : blobs){  // iterate through the blobs and then draw all the blobs
//			b.draw(g);
//		}
//
//		// Ask the colliders to draw themselves in red.
//		if(colliders != null){  // if there are any collissions, then draw those blobs in red
//			g.setColor(Color.RED);
//			for (Blob collide : colliders){
//				collide.draw(g);
//			}
//		}
//
//
//
//	}
//
//	/**
//	 * Sets colliders to include all blobs in contact with another blob
//	 */
//	private void findColliders() {
//		// TODO: YOUR CODE HERE
//		// Create the tree
//		// For each blob, see if anybody else collided with it
//		PointQuadtree<Blob> collisionTree = new PointQuadtree(blobs.get(0), 0,0, width, height);  // Create a list collissionTree and keep a list of all the blobs that collide
//
//		for (Blob collidedBlob : blobs) {  // Iterate through all the blobs and check if any of them have collided, if so, add them to the tree - collisionTree
//			if (collidedBlob != blobs.get(0)) { // Check if the collision is not with itself, to make sure it collides with other blobs and not itself
//				collisionTree.insert(collidedBlob);
//			}
//		}
///**
// * Keep an arraylist of the colliders and iterate through all the blobs and iterate through them and call findCircle on it
// */
//		colliders = new ArrayList<>();
//		for(Blob bl: blobs){
//			List<Blob> col = collisionTree.findInCircle(bl.getX(), bl.getY(), 2 * (bl.getR()));
//			if(col.size() > 1){
//				colliders.add(bl); // Add the blob into the colliders list
//			}
//		}
//
//
//	}
//
//
//	/**
//	 * DrawingGUI method, here moving all the blobs and checking for collisions
//	 */
//	public void handleTimer() {
//		// Ask all the blobs to move themselves.
//		for (Blob blob : blobs) {
//			blob.step();
//		}
//		// Check for collisions
//		if (blobs.size() > 0) { // if (!blobs.isEmpty())
//			findColliders();
//			if (collisionHandler=='d') {
//				blobs.removeAll(colliders);
//				colliders = null;
//			}
//		}
//		// Now update the drawing
//		repaint();
//	}
//
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				new CollisionGUI();
//			}
//		});
//	}
//}
