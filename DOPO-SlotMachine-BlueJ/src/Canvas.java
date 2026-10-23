import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
 
/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example.
 *
 * @author: Bruce Quig
 * @author: Michael Kolling (mik)
 *
 * @version: 1.6 (shapes)
 */
public class Canvas{
    // Note: The implementation of this class (specifically the handling of
    // shape identity and colors) is slightly more complex than necessary. This
    // is done on purpose to keep the interface and instance fields of the
    // shape objects in this project clean and simple for educational purposes.
 
    private static Canvas canvasSingleton;

    // list of initial colors with a name
    private static final Map<String, Color> COLORS = Map.ofEntries(
        Map.entry("black", Color.decode("#000000")),
        Map.entry("red", Color.decode("#FF0000")),
        Map.entry("blue", Color.decode("#0000FF")),
        Map.entry("yellow", Color.decode("#FFFF00")),
        Map.entry("green", Color.decode("#008000")),
        Map.entry("white", Color.decode("#FFFFFF")),
        Map.entry("magenta", Color.decode("#FF00FF")),
        Map.entry("aqua", Color.decode("#00FFFF")),
        Map.entry("beige", Color.decode("#F5F5DC")),
        Map.entry("chocolate", Color.decode("#D2691E")),
        Map.entry("orange", Color.decode("#FFA500")),
        Map.entry("purple", Color.decode("#800080")),
        Map.entry("gray", Color.decode("#808080")),
        Map.entry("silver", Color.decode("#C0C0C0")),
        Map.entry("pink", Color.decode("#FFC0CB")),
        Map.entry("coral", Color.decode("#FF7F50")),
        Map.entry("crimson", Color.decode("#DC143C")),
        Map.entry("gold", Color.decode("#FFD700")),
        Map.entry("teal", Color.decode("#008080")),
        Map.entry("turquoise", Color.decode("#40E0D0")),
        Map.entry("navy", Color.decode("#000080")),
        Map.entry("lime", Color.decode("#00FF00")),
        Map.entry("olive", Color.decode("#808000")),
        Map.entry("indigo", Color.decode("#4B0082")),
        Map.entry("violet", Color.decode("#EE82EE")),
        Map.entry("brown", Color.decode("#A52A2A"))
    );
 
    /**
     * Factory method to get the canvas singleton object.
     */
    public static Canvas getCanvas(){
        if(canvasSingleton == null) {
            canvasSingleton = new Canvas("BlueJ Shapes Demo", 1290, 900,
                                         Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }
 
    //  ----- instance part -----
 
    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List <Object> objects;
    private HashMap <Object,ShapeDescription> shapes;
   
    /**
     * Create a Canvas.
     * @param title  title to appear in Canvas Frame
     * @param width  the desired width for the canvas
     * @param height  the desired height for the canvas
     * @param bgClour  the desired background colour of the canvas
     */
    private Canvas(String title, int width, int height, Color bgColour){
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList <Object>();
        shapes = new HashMap <Object,ShapeDescription>();
    }
 
    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. This method can also be used to bring an already
     * visible canvas to the front of other windows.
     * @param visible  boolean value representing the desired visibility of
     * the canvas (true or false)
     */
    public void setVisible(boolean visible){
        if(graphic == null) {
            // first time: instantiate the offscreen image and fill it with
            // the background colour
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D)canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }
 
    /**
     * Draw a given shape onto the canvas.
     * @param  referenceObject  an object to define identity for this shape
     * @param  color            the color of the shape
     * @param  shape            the shape object to be drawn on the canvas
     */
     // Note: this is a slightly backwards way of maintaining the shape
     // objects. It is carefully designed to keep the visible shape interfaces
     // in this project clean and simple for educational purposes.
    public void draw(Object referenceObject, String color, Shape shape){
        objects.remove(referenceObject);   // just in case it was already there
        objects.add(referenceObject);      // add at the end
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }
 
    /**
     * Erase a given shape's from the screen.
     * @param  referenceObject  the shape object to be erased
     */
    public void erase(Object referenceObject){
        objects.remove(referenceObject);   // just in case it was already there
        shapes.remove(referenceObject);
        redraw();
    }
 
    /**
     * Set the foreground colour of the Canvas.
     * @param  newColour   the new colour for the foreground of the Canvas
     */
    public void setForegroundColor(String colorString) {
        Color color;

        // If its null, by default puts the color black
        if (colorString == null) {
            color = Color.decode("#000000");
        } else if (colorString.startsWith("#")) {
            // if the user puts a hexagesimal code, it will be decoded and painted
            try {
                color = Color.decode(colorString);
            } catch (NumberFormatException e) {
                color = Color.decode("#000000");
            }
        } else {
            // If its a name of a color, example: "red", search it on the dictionary
            color = COLORS.getOrDefault(colorString.toLowerCase(), Color.decode("#A7A7A7"));
        }

        graphic.setColor(color);
    }
 
    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     * @param  milliseconds  the number
     */
    public void wait(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        } catch (Exception e){
            // ignoring exception at the moment
        }
    }
 
    /**
     * Redraw ell shapes currently on the Canvas.
     */
    private void redraw(){
        erase();
        for(Iterator i=objects.iterator(); i.hasNext(); ) {
                       shapes.get(i.next()).draw(graphic);
        }
        canvas.repaint();
    }
       
    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase(){
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }
 
 
    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel{
        public void paint(Graphics g){
            g.drawImage(canvasImage, 0, 0, null);
        }
    }
   
    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class ShapeDescription{
        private Shape shape;
        private String colorString;
 
        public ShapeDescription(Shape shape, String color){
            this.shape = shape;
            colorString = color;
        }
 
        public void draw(Graphics2D graphic){
            setForegroundColor(colorString);
            graphic.draw(shape);
            graphic.fill(shape);
        }
    }
 
}