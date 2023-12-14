import java.awt.*;
import java.util.ArrayList;

/**
 * Author: Grace Bech
 * Date: November 14th 2023
 * Class that handles the messages for freehand
 *
 */
public class Message {
    ArrayList<Point> points = new ArrayList<>(); // Variable to keep track of points in case of a polyline
    String type; // Variable to keep track of the shape type
    Color color; // Variable to keep track of the color

    /**
     * Constructor for the Message class, which handles the message
     * @param message The input message to be processed
     */
    public Message(String message) {
        int x = 0; // Variable to store the x-coordinate of the point being processed

        try {
            String[] tokens = message.split(" "); // Splitting the message into tokens using space as the delimiter

            for (int i = 1; i < tokens.length - 1; i++) {
                // Setting the type as the element at index 1
                if (i == 1) {
                    type = tokens[i];
                } else if (i % 2 == 0) {
                    x = Integer.parseInt(tokens[i]); // Parsing the x-coordinate from the token
                    int y = Integer.parseInt(tokens[i + 1]); // Parsing the y-coordinate from the next token
                    points.add(new Point(x, y)); // Creating a Point and adding it to the list of points for freehand
                } else {
                    points.add(new Point(x, Integer.parseInt(tokens[i]))); // Creating a Point and adding it to the list of points for freehand
                }
            }

            color = new Color(Integer.parseInt(tokens[tokens.length - 1])); // Getting the color at the end of the message

        } catch (IllegalArgumentException e) {
            // Handling exceptions in case of errors during message parsing
            System.err.println("Error parsing message: " + e.getMessage());
        }
    }
}



