package presentation;

import java.awt.Polygon;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0  (15 July 2000)
 */

public class Triangle{
    
    /** Number of vertices of a triangle. */
    public static final int VERTICES=3;
    
    /** Height in pixels. */
    private int height;
    /** Width in pixels. */
    private int width;
    /** X coordinate of the top vertex. */
    private int xPosition;
    /** Y coordinate of the top vertex. */
    private int yPosition;
    /** Name of the fill color. */
    private String color;
    /** Whether the triangle is drawn on the canvas. */
    private boolean isVisible;

    /**
     * Create a new triangle at default position with default color.
     */
    public Triangle(){
        height = 30;
        width = 40;
        xPosition = 0;
        yPosition = 0;
        color = "green";
        isVisible = false;
    }
    
    /**
     * Create a new triangle with custom values for color, height and width
    */
    public Triangle(
        final int width,
        final int height,
        final String color
    ){
        this.width = width;
        this.height = height;
        this.color = color;
    }

    /**
     * Make this triangle visible. If it was already visible, do nothing.
     */
    public void makeVisible(){
        isVisible = true;
        draw();
    }
    
    /**
     * Return the area of the triangle
     */

    public int area(){
        return this.width * this.height / 2;
    }

    /**
     * Convert the triangle to a equilateral one
     */
    public void equilateral(){
        final int area = area();
        final int side = (int) Math.sqrt(4 * area / Math.sqrt(3));

        this.width = side;
        this.height = (int) (side * Math.sqrt(3) / 2);
    }
    
    /**
     * Return the absolute value of a number
     * @param number The int value is going to be converted
     */
    public int abs(final int number) {
        if (number < 0) {
            return number*(-1);
        } else {
            return number;
        }
    }
    
    
    /**
     * Walk to left or right depending if it's lower or higher than zero
     * @param times The int value of the movement
     */
    public void walk(final int times){
        
        if (times > 0){
            //It moves "times" times down to the right
            for (int i =0; i < times; i++){
                // It starts falling vertically, "nozzing down".
                moveDown();
                // It moves to the right.
                moveRight();
            }
        }
        else {
            //It moves "times" times down to the left
            for (int i=0; i < abs(times); i++){
                // It starts falling vertically, "nozzing down".
                moveDown();
                //It moves to the left.
                moveLeft();
            }
        }
                

    }

    /**
     * NEW METHOD
     * Transform the scale of the triangle with a integer type factor
     * @param factor Int value to rescale the triangle
     */
    public void scale(final int factor){
        final int newHeight = height * factor;
        final int newWidth = width * factor;
        this.height = newHeight;
        this.width = newWidth;
    }
    
    /**
     * Make this triangle invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible(){
        erase();
        isVisible = false;
    }
    
    /**
     * Move the triangle a few pixels to the right.
     */
    public void moveRight(){
        moveHorizontal(20);
    }

    /**
     * Move the triangle a few pixels to the left.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }

    /**
     * Move the triangle a few pixels up.
     */
    public void moveUp(){
        moveVertical(-20);
    }

    /**
     * Move the triangle a few pixels down.
     */
    public void moveDown(){
        moveVertical(20);
    }

    /**
     * Move the triangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(final int distance){
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the triangle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(final int distance){
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Slowly move the triangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(final int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
        } else {
            delta = 1;
        }

        for(int i = 0; i < Math.abs(distance); i++){
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly move the triangle vertically.
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(final int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
        } else {
            delta = 1;
        }

        for(int i = 0; i < Math.abs(distance); i++){
            yPosition += delta;
            draw();
        }
    }

    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be !=0.
     * @param newWidht the new width in pixels. newWidht must be !=0.
     */
    public void changeSize(final int newHeight, final int newWidth) {
        /* The if state checks that the height is not 0; if not,
         * it throws and exception.
         */
        if (newHeight !=0){
            erase();
            height = newHeight;
            width = newWidth;
            draw();
        } else{
            throw new IllegalArgumentException("The newHeight can´t be 0.");
        }
    }
    
    /**
     * Change the color. 
     * @param color the new color. Valid colors are "red", "yellow", "blue", "green",
     * "magenta" and "black".
     */
    public void changeColor(final String newColor){
        color = newColor;
        draw();
    }

    /*
     * Draw the triangle with current specifications on screen.
     */
    private void draw(){
        if(isVisible) {
            final Canvas canvas = Canvas.getCanvas();
            final int[] xpoints = { xPosition, xPosition + (width/2), xPosition - (width/2) };
            final int[] ypoints = { yPosition, yPosition + height, yPosition + height };
            canvas.draw(this, color, new Polygon(xpoints, ypoints, 3));
            canvas.wait(10);
        }
    }

    /*
     * Erase the triangle on screen.
     */
    private void erase(){
        if(isVisible) {
            final Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
