import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 *Editor : Grace Bech
 */
public class RegionFinder {
    private static final int maxColorDiff = 20;                // how similar a pixel color must be to the target color, to belong to a region
    private static final int minRegion = 50;                // how many points in a region to be worth considering

    private BufferedImage image;                            // the image in which to find regions
    private BufferedImage recoloredImage;                   // the image with identified regions recolored


    private ArrayList<ArrayList<Point>> regions;            // a region is a list of points
    // so the identified regions are in a list of lists of points

    public RegionFinder(BufferedImage image) {
        this.image = image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getRecoloredImage() {
        return recoloredImage;
    }

    /**
     * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
     */
    public void findRegions(Color targetColor) {
        // TODO: YOUR CODE HERE
        regions = new ArrayList<ArrayList<Point>>(); // Start a new region, as an arraylist
        BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);  // Store the image in visited
        for (int y = 0; y < image.getHeight(); y++) { // If y is less that the height of the image, then keep incrementing y++
            for (int x = 0; x < image.getWidth(); x++) {  // If x, is less than the width of the image
                Color c = new Color(image.getRGB(x, y)); // Store the color in the variable c

                if (visited.getRGB(x, y) == 0 && colorMatch(targetColor, c)) { // If the pointed has been visited, and the color is the target color, then continue
                    ArrayList<Point> region = new ArrayList<>(); // Create an arraylist of regions
                    Point p = new Point(x, y);   //Get the coordinates x, and Y and store them in the variable p
                    ArrayList<Point> toVisit = new ArrayList<>(); // Start a list of pixels to visit
                    toVisit.add(p); // Add the points to the tovisit list you created
                    while (!toVisit.isEmpty()) { // As long as the tovisit list is empty, do the following

                        Point last = toVisit.remove(toVisit.size() - 1); // Get the last pont in the tovist list
                        if (visited.getRGB(last.x, last.y) == 0) { // If the pointed has been visited, and the color is the target color, then continue
                            visited.setRGB(last.x, last.y, 1);
                            region.add(last); // Add the last element to the region

                            // Check for the available neighbours. If the neigbours meet the minimum conditions, do the following
                            for (int neighbourX = Math.max(0, last.x - 1); neighbourX < Math.min(last.x + +2, image.getWidth()); neighbourX++) {
                                for (int neighbourY = Math.max(0, last.y - 1); neighbourY < Math.min(last.y + 2, image.getHeight()); neighbourY++) {
                                    Color NeighColor = new Color(image.getRGB(neighbourX, neighbourY)); // Get the color of the neighbours and keep it in neighcolor
                                    if (visited.getRGB(neighbourX, neighbourY) == 0 && colorMatch(targetColor, NeighColor)) { // If the pointed has been visited, and the color is the target color, then continue


                                        Point n = new Point(neighbourX, neighbourY);
                                        toVisit.add(n); // Add the points to the toVisit list


                                    }


                                }


                            }
                        }

                    }
                    if (region.size() >= minRegion){ // If the region is greater than the minimum region, then add the region to regions
                        regions.add(region);

                    }


                }
            }
        }
    }

    /**
     * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
     */
    private static boolean colorMatch(Color c1, Color c2) {
        // TODO: YOUR CODE HERE



        if ((Math.abs(c1.getRed() - c2.getRed()) <= maxColorDiff) && (Math.abs(c1.getBlue() - c2.getBlue()) <= maxColorDiff)
                && (Math.abs(c1.getGreen() - c2.getGreen()) <= maxColorDiff)) { // If the color difference between the two colors is less than the maxColorDiff, then return true, otherwise return false
            return true;
        } else {
            return false;

        }

    }

    /**
     * Returns the largest region detected (if any region has been detected)
     */
    public ArrayList<Point> largestRegion() {
        // TODO: YOUR CODE HERE
        if (regions.isEmpty()) {   // Check to see if the region is empty
            return null;
        }
        ArrayList largestRegion = regions.get(0);


        for (int i = 1; i < regions.size() - 1; i++) {  // If the region is smaller than the regions size, then keep incrementing the index, and check index by index
            if (regions.get(i).size() > largestRegion.size()) {
                largestRegion = regions.get(i); // Set the largest region to be the index at i


            }


        }
        return largestRegion;
    }



    /**
     * Sets recoloredImage to be a copy of image,
     * but with each region a uniform random color,
     * so we can see where they are
     */
    public void recolorImage() {
            // First copy the original
            recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
            // Now recolor the regions in it
            // TODO: YOUR CODE HERE

        for (ArrayList<Point>region: regions){
            Color regionColor = new Color((int) (Math.random() * 16777216)); // Set the regionColor to be a random color
            for (Point point: region){
               recoloredImage.setRGB(point.x, point.y, regionColor.getRGB()); // Recolor the region in the image
            }


        }
        }
}