import java.util.ArrayList;
import java.util.List;

/**
 * October 10th 2023
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * Editor : Grace Bech
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;                            // the point anchoring this node
	private int x1, y1;                            // upper-left corner of the region
	private int x2, y2;                            // bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;    // children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 *
	 * @param quadrant 1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant == 1) return c1;
		if (quadrant == 2) return c2;
		if (quadrant == 3) return c3;
		if (quadrant == 4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 *
	 * @param quadrant 1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant == 1 && c1 != null) || (quadrant == 2 && c2 != null) || (quadrant == 3 && c3 != null) || (quadrant == 4 && c4 != null);
	}

	/**
	 * Inserts the point into the tree
	 *Check all the quadrants and see if they have children.
	 * if the quadrants have children, add the point (p2) to those quadrants
	 * @return
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		if (getPoint().getX() <= p2.getX() && getPoint().getY() >= p2.getY()) {  // Check all first quadrant and check all its inner quadrants and its children
			if (hasChild(1)) {
				c1.insert(p2);
			} else {
				c1 = new PointQuadtree<>(p2, (int) getPoint().getX(), y1, x2, (int) getPoint().getY());

			}
		} else if (getPoint().getX() >= p2.getX() && getPoint().getY() >= p2.getY()) {
			if (hasChild(2)) {
				c2.insert(p2);
			} else {
				c2 = new PointQuadtree<>(p2, x1,y1, (int) getPoint().getX(), (int) getPoint().getY());

			}
		} else if (getPoint().getX() >= p2.getX() && getPoint().getY() <= p2.getY()) {
			if (hasChild(3)) {
				c3.insert(p2);
			} else {
				c3 = new PointQuadtree<>(p2, x1, (int) getPoint().getY(), (int) getPoint().getX(), y2);


			}
		} else if (getPoint().getX() <= p2.getX() && getPoint().getY() <= p2.getY()) {
			if (hasChild(4)) {
				c4.insert(p2);
			} else {
				c4 = new PointQuadtree<>(p2, (int) getPoint().getX(), (int) getPoint().getY(), x2, y2);


			}


		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {

		// TODO: YOUR CODE HERE
		int sizeCount = 0; // Create an instance variable to keep count of size
		if (hasChild(1)) {
			sizeCount += getChild(1).size(); // if the first quadrant has a child, get all its other children and increment the size
		}


		if (hasChild(2)) {
			sizeCount += getChild(2).size(); // if the first quadrant has a child, get all its other children and increment the size
		}

		if (hasChild(3)) {
			sizeCount += getChild(3).size(); // if the first quadrant has a child, get all its other children and increment the size
		}

		if (hasChild(4)) {
			sizeCount += getChild(4).size(); // if the first quadrant has a child, get all its other children and increment the size
		}
		return sizeCount + 1;
	}


	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		List<E> totalPoints = new ArrayList<>();  // Create an arraylist to keep all the points
		helperAllpoints(totalPoints);  // Add the points found to the helperAllpointlist
		return totalPoints;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 *
	 * @param cx circle center x
	 * @param cy circle center y
	 * @param cr circle radius
	 * @return the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		List<E> collisionsTracker = new ArrayList<>();  // Create a list to keep track of all the collisions the blobs have had
		circleHelper(collisionsTracker, cx, cy, cr);
		System.out.println("The size is: " + collisionsTracker.size());
		return collisionsTracker;  // Return the list of all the collisions

	}

	// TODO: YOUR CODE HERE for any helper methods.

	/**
	 * Check all is all the quadrants have children, if so, get their children and add them to the total points
	 * Check if the quadrants and their children have children
	 * @param totalPoints
	 */
	public void helperAllpoints(List<E> totalPoints) {  // Create a helper function, and store all the circles in a list called total points
		totalPoints.add(getPoint());  // If you find any children in the trees, add those into the total points
		if (hasChild(1)) {
			getChild(1).helperAllpoints(totalPoints);
		}
		if (hasChild(2)) {
			getChild(2).helperAllpoints(totalPoints);
		}
		if (hasChild(3)) {
			getChild(3).helperAllpoints(totalPoints);
		}
		if (hasChild(4)) {
			getChild(4).helperAllpoints(totalPoints);


		}
	}

	/**
	 * Create a helper function to loop through the pointInCircle to be used in the findCircle method
	 * @param collisionsTracker
	 * @param cx
	 * @param cy
	 * @param cr
	 */
	public void circleHelper(List<E>collisionsTracker,double cx, double cy, double cr) {
		if (!Geometry.circleIntersectsRectangle(cx, cy, cr, getX1(), getY1(), getX2(), getY2())) {
		return;

		}
		if(Geometry.pointInCircle(getPoint().getX(), getPoint().getY(), cx, cy, cr)){
			collisionsTracker.add(getPoint());
		}
		if (hasChild(1)) {
			getChild(1).circleHelper(collisionsTracker, cx, cy, cr);
		}
		if (hasChild(2)) {
			getChild(2).circleHelper(collisionsTracker, cx, cy, cr);
		}
		if (hasChild(3)) {
			getChild(3).circleHelper(collisionsTracker, cx, cy, cr);

		}
		if (hasChild(4)) {
			getChild(4).circleHelper(collisionsTracker, cx, cy, cr);

		}
	}
}