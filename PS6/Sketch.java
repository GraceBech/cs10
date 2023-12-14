
import javax.swing.plaf.SliderUI;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Author: Grace Bech
 */

public class Sketch {


    public Sketch(){

    }
    int size = 0;
    static TreeMap<Integer, Shape> sketches = new TreeMap<>();

    public void add(Shape newOne){
        sketches.put(size, newOne);
        size++;
    }

    public Map<Integer, Shape> getMap(){
        return sketches;
    }

    public Shape getShape(int x, int y){
        for(Integer shapeId: sketches.descendingKeySet()){
            if(sketches.get(shapeId).contains(x, y)){
                return sketches.get(shapeId);
            }
        }
        return null;
    }
    public void delete(int x, int y){
        // Iterate through sketches in descending order to prioritize newer sketches
        for(Integer shapeId: sketches.descendingKeySet()){
            if(sketches.get(shapeId).contains(x, y)){
                sketches.remove(shapeId);
            }
        }
    }
    public void recolor(int x, int y, Color color){
        getShape(x, y).setColor(color);
    }

    public void moveTo(int newX, int newY){
        try {
            Point movePoint = new Point(newX, newY);
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
}


