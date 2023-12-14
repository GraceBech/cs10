import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016

 */
public class Polyline implements Shape {
	// TODO: CODE HERE
	ArrayList<Point> points;
	Color color;

	String type = "polyline";
	int x1;
	int x2;
	int y1;
	int y2;

	public String getType(){
		return type;
	}
	public Polyline(int x1, int y1, Color color) {
		this.x1 = x1;
		this.x2 = x1;
		this.y1 = y1;
		this.y2 = y1;
		this.color = color;
		this.points = new ArrayList<>();
		points.add(new Point(x1, y1));
	}
	public Polyline(ArrayList<Point> points, Color color) {
		this.color = color;
		this.points = new ArrayList<>(points); // Initialize and copy the points list
	}
	@Override
	public void moveBy(int dx, int dy) {
		for(Point point: points){
			point.x+=dx;
			point.y+=dy;
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color=color;
	}

	@Override
	public boolean contains(int x, int y) {
		Segment segment;
		for (int i = 1; i < points.size(); i++){
			int startX = points.get(i - 1).x;
			int startY = points.get(i - 1).y;
			int endX = points.get(i).x;
			int endY = points.get(i).y;

			segment = new Segment(startX, startY, endX, endY, color);

			if(segment.contains(x, y)){
				return true;
			}
		}

		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		if (!points.isEmpty() && points.size() > 1) {
			for (int i = 1; i < points.size(); i++) {
				g.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y);
			}
		}
//		g.setColor(color);
//		if(points.isEmpty() || points.size() == 1){
//		} else{
//			// Draw lines between consecutive points to visualize the shape
//			for(int i = 1; i<points.size(); i++){
//				g.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y);
//			}
//		}
	}

	@Override
	public String toString() {
		StringBuilder pointsString = new StringBuilder();
		for (Point point : points) {
			pointsString.append(point.x).append(" ");
			pointsString.append(point.y);
		}
		return "freehand "+pointsString+" "+color.getRGB();
	}
}
