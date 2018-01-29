/**
*	@Author: Jung Woo (Noel) Park
*	Student ID: 1162424
*/

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class DisplayPanel extends JPanel {

	private static int INITIAL_SIZE = 600;

	private int height, width;
	private int size;
	private int preferredWidth, preferredHeight, totalSize;

	private Position previousPosition;

	private ArrayList<Float> scales;
	private List<Square> squares;
	private ArrayList<Position> nextCorners;
	private ArrayList<String[]> inputData;

    public DisplayPanel() {

    	this.size 	= INITIAL_SIZE/2;
    	this.totalSize = 0;
    	this.scales = new ArrayList<Float>();
    	this.squares = new ArrayList<Square>();
    	this.nextCorners = new ArrayList<Position>();
    	this.inputData = new ArrayList<String[]>();

        setBackground(Color.WHITE);
    }

    public void addSquareData(String[] data) {
    	
    	float scale = Float.parseFloat(data[0]);
    	int newSize = (int)(size * scale);
    	scales.add(scale);

    	totalSize += newSize;
    	inputData.add(data);
    	size = newSize;
    }

    public void determineBestSize() {

    	size = INITIAL_SIZE / 2;

    	if(totalSize < 560) {
    		for(int i = 1; totalSize < 560; i++) {
    			totalSize = 0;
    			size += i;
    			int changingSize = size;
    			for(float s : scales) {
    				int newSize = (int)(changingSize * s);
    				totalSize += newSize;
    				changingSize = newSize;
    			}System.out.println("Changing " +size);
    		}
    	} else if(totalSize > 560) {
    		for(int i = size; totalSize > 560; i--) {
    			totalSize = 0;
    			int changingSize = i;
    			for(float s : scales) {
    				int newSize = (int)(changingSize * s);
    				totalSize += newSize;
    				changingSize = newSize;
    				System.out.println("Changing " +totalSize);
    			}
    			   				    	
    		}
    	}
    }

    public void generateSquares() {
    	//System.out.println(size);
		for(String[] data : inputData) {

			float scale = Float.parseFloat(data[0]);

			int r = Integer.parseInt(data[1]);
			int g = Integer.parseInt(data[2]);
			int b = Integer.parseInt(data[3]);
			int newSize = (int)((float)size * scale);
System.out.println(scale);
			// if the square we are adding is an initial square.
	    	if(squares.size() == 0) {
		    	int x = INITIAL_SIZE/2 - newSize/2;
				int y = INITIAL_SIZE/2 - newSize/2;
				//System.out.println(x + " " + y);
		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    	nextCorners.add(new Position(x, y));
	    	} else {
	    		
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

	    		nextCorners.clear();
	    		nextCorners.addAll(newCorners);
	    	}
	    	size = newSize;
	    }
    }

    public void render() {
    	super.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(INITIAL_SIZE, INITIAL_SIZE);
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
