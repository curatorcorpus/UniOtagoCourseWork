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
	private ArrayList<Position> nextCorners;

    public DisplayPanel(int width, int height) {

    	this.height = height;
    	this.width 	= width;
    	this.size 	= width/2;
    	this.squares = new ArrayList<Square>();
    	this.nextCorners = new ArrayList<Position>();

        setBackground(Color.WHITE);
    }

    public void setSquare(float scale, int r, int g, int b) {

    	// if the square we are adding is an initial square.
    	if(squares.size() == 0) {
	    	int x = width/2 - size/2;
			int y = height/2 - size/2;

	    	squares.add(new Square(new Color(r,g,b), x, y, size));
	    	nextCorners.add(new Position(x, y));
    	} else {

    		int newSize = (int)((float)size * scale);
    		
    		List<Position> newCorners = new ArrayList<Position>();

    		for(Position prevCorner : nextCorners) {

	    		// top left corner.
	    		int x = prevCorner.x - newSize/2;
	    		int y = prevCorner.y - newSize/2;
		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    	newCorners.add(new Position(x, y));

	    		// bot left corner.    		
	    		x = prevCorner.x - newSize/2;
	    		y = (prevCorner.y+size) - newSize/2;
		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
	    		newCorners.add(new Position(x, y));

	    		// top right corner.
	    		x = (prevCorner.x+size)- newSize/2;
	    		y = prevCorner.y - newSize/2;
		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    	newCorners.add(new Position(x, y));

	    		// bot right corner.    		
	    		x = prevCorner.x+size-newSize/2;
	    		y = prevCorner.y+size-newSize/2;
		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    	newCorners.add(new Position(x, y));
    		}
    		size = newSize;

    		nextCorners.clear();
    		nextCorners.addAll(newCorners);
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
