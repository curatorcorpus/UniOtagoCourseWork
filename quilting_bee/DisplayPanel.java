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

	private List<Square> squares;
	private ArrayList<Position> nextCorners;
	private ArrayList<String[]> inputData;

    public DisplayPanel() {

    	this.size 	= INITIAL_SIZE/2;
    	this.totalSize = 0;
    	this.squares = new ArrayList<Square>();
    	this.nextCorners = new ArrayList<Position>();
    	this.inputData = new ArrayList<String[]>();

        setBackground(Color.WHITE);
    }
    private float max = 0;
    public void addSquareData(String[] data) {
    	
    	float scale = Float.parseFloat(data[0]);
    	int newSize = (int)Math.ceil(size * scale);
    	totalSize += newSize;
    	inputData.add(data);

        if(scale > max) {
            max = scale;
        }
    }

    public void determineBestSize() {

        if(totalSize < 560) {
    		for(int i = 1; totalSize < 560; i++) {
    			totalSize = 0;
    			int changingSize = size;
    			for(String [] data : inputData) {
                    float newScale = Float.parseFloat(data[0])+0.01f;
                    data[0] = String.valueOf(newScale);
    				
                    int newSize = (int)(changingSize * newScale);
    				
                    totalSize += newSize;
    				changingSize = newSize;
    			}
    		}
    	} else if(totalSize > 560) {
	        totalSize = 0;
            for(String [] data : inputData) {
                float newScale = Float.parseFloat(data[0])/max;
                data[0] = String.valueOf(newScale);
            }
    	}
    }

    public void generateSquares() {

        int prevSize = size;

        for(String[] data : inputData) {

			float scale = Float.parseFloat(data[0]);
  //          System.out.println(scale);
			int r = Integer.parseInt(data[1]);
			int g = Integer.parseInt(data[2]);
			int b = Integer.parseInt(data[3]);
			int newSize = (int)((float)size * scale);
//System.out.println(newSize);
			// if the square we are adding is an initial square.
	    	if(squares.size() == 0) {
		    	int x = INITIAL_SIZE/2 - newSize/2;
				int y = INITIAL_SIZE/2 - newSize/2;

		    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    	nextCorners.add(new Position(x, y));
                System.out.println(x + " " + y);
                prevSize = newSize;
	    	} else {
	    		
	    		List<Position> newCorners = new ArrayList<Position>();
	    		for(Position prevCorner : nextCorners) {
//                    System.out.println("previous size "+prevCorner.x + " " + prevCorner.y);
		    		// top left corner.
		    		int x = prevCorner.x - newSize/2;
		    		int y = prevCorner.y - newSize/2;
			    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
			    	newCorners.add(new Position(x, y));
//System.out.println("Top left " + x + " " + y);
		    		// bot left corner.    		
		    		x = prevCorner.x - newSize/2;
		    		y = (prevCorner.y+prevSize) - newSize/2;
			    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
		    		newCorners.add(new Position(x, y));
//System.out.println("bot left " +x + " " + y);
		    		// top right corner.
		    		x = (prevCorner.x+prevSize)- newSize/2;
		    		y = prevCorner.y - newSize/2;
			    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
			    	newCorners.add(new Position(x, y));
//System.out.println("top right " +x + " " + y);
		    		// bot right corner.    		
		    		x = prevCorner.x+prevSize-newSize/2;
		    		y = prevCorner.y+prevSize-newSize/2;
			    	squares.add(new Square(new Color(r,g,b), x, y, newSize));
			    	newCorners.add(new Position(x, y));
//                    System.out.println("bottom right " +x + " " + y);
//                                    System.out.println();
	    		}
                prevSize = newSize;

	    		nextCorners.clear();
	    		nextCorners.addAll(newCorners);
	    	}
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
