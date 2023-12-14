import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Date : September 28th, 2023
 * Webcam-based drawing
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *Editor : Grace Bech
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
    private char displayMode = 'w';            // what to display: 'w': live webcam, 'r': recolored image, 'p': painting
    private RegionFinder finder;            // handles the finding
    private Color targetColor;            // color of regions of interest (set by mouse press)
    private Color paintColor = Color.blue;    // the color to put into the painting from the "brush"
    private BufferedImage painting;            // the resulting masterpiece

    /**
     * Initializes the region finder and the drawing
     */
    public CamPaint() {
        finder = new RegionFinder(image); // Passing in nothing is calling a constructor that has no image
        clearPainting();
    }

    /**
     * Resets the painting to a blank image
     */
    protected void clearPainting() {
        painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
     * depending on display variable ('w', 'r', or 'p')
     */
    @Override
    public void draw(Graphics g) {
        // TODO: YOUR CODE HERE
        if (displayMode == 'w') { // If the key is 'W' then call drawImage on it
            g.drawImage(image, 0, 0, null);
        } else if (displayMode == 'r') {   // If the key is 'r' then call drawImage on it
            g.drawImage(finder.getRecoloredImage(), 0, 0, null);


        } else if (displayMode == 'p') {    // If the key is 'p' then call drawImage on it
            g.drawImage(painting, 0,0, null);
        }


    }


    /**
     * Webcam method, here finding regions and updating the painting.
     */
    @Override
    public void processImage() {
        // TODO: YOUR CODE HERE
        if (targetColor != null){ // if you have the target color, and it's not null
            finder.setImage(image);
            finder.findRegions(targetColor);
            finder.recolorImage();
            ArrayList<Point> pBrush = finder.largestRegion(); // Create an arraylist of points and keep them in Paint brush (Pbrush)
            if (pBrush != null){ // If the paint brush is not null, then call painting on every pixel in the pBrush
                for(Point point : pBrush){
                    painting.setRGB(point.x, point.y, paintColor.getRGB());
                }

            }
        }
    }

    /**
     * Overrides the DrawingGUI method to set the track color.
     */
    @Override
    public void handleMousePress(int x, int y) {
        // TODO: YOUR CODE HERE
        if (image != null){ // If there is an image , then set the target color to be the color of that image
            targetColor = new Color(image.getRGB(x, y));
        }
    }

    /**
     * DrawingGUI method, here doing various drawing commands
     */
    @Override
    public void handleKeyPress(char k) {
        if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
            displayMode = k;
        }
        else if (k == 'c') { // clear
            clearPainting();
        }
        else if (k == 'o') { // save the recolored image
            saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
        }
        else if (k == 's') { // save the painting
            saveImage(painting, "pictures/painting.png", "png");
        }
        else {
            System.out.println("unexpected key "+k);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CamPaint();
            }
        });
    }
}