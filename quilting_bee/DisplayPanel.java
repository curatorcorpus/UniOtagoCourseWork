/**
*	@Author: Jung Woo (Noel) Park
*	Student ID: 1162424
*/

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class DisplayPanel extends JPanel {

	private int height, width;
	private int size;

	private Position previousPosition;

	private List<Square> squares;

    public DisplayPanel(int width, int height) {

    	this.height = height;
    	this.width 	= width;
    	this.size 	= width/2;
    	this.squares = new ArrayList<Square>();

        setBackground(Color.WHITE);
    }

    public void setSquare(float scale, int r, int g, int b) {

    	// if the square we are adding is an initial square.
    	if(squares.size() == 0) {
	    	int x = width/2 - size/2;
			int y = height/2 - size/2;

			previousPosition = new Position(x,y);

	    	squares.add(new Square(new Color(r,g,b), x, y, size));
    	} else {
    		int newSize = (int)((float)size * scale);
    		System.out.println(newSize);
    		
    		// top left corner.
    		int x = previousPosition.x - newSize/2;
    		int y = previousPosition.y - newSize/2;
	    	squares.add(new Square(new Color(r,g,b), x, y, newSize));

    		// bot left corner.    		
    		x = previousPosition.x - newSize/2;
    		y = (previousPosition.y+size) - newSize/2;
	    	squares.add(new Square(new Color(r,g,b), x, y, newSize));

    		// top right corner.
    		x = (previousPosition.x+size)- newSize/2;
    		y = previousPosition.y - newSize/2;
	    	squares.add(new Square(new Color(r,g,b), x, y, newSize));

    		// bot right corner.    		
    		x = previousPosition.x+size-newSize/2;
    		y = previousPosition.y+size-newSize/2;
	    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
    	}
    }

    public void rerender() {
    	super.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(Square s : squares) {

	        Color c = s.getColor();
	        Position p = s.getPosition();
	        int size = s.getSize();

	        g.setColor(c);
	        g.fillRect(p.x, p.y, size, size);
	    }
    }
}
