/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/

/**
*	Class that represents the data structure for the pentominoes objects.
*/
public class Pentomino {
	
	public static enum Type { 
		O,P,Q,R,S,T,U,V,W,X,Y,Z
	} // List of Conway's Pentominoes types.

	public static Type getEnumType(String type) {

		String upperChar = type.toUpperCase();
		Type pentoType = null;
		switch(upperChar) {
			case "O":
				pentoType = Type.O;
				break;
			case "P":
				pentoType = Type.P;			
				break;
			case "Q":
				pentoType = Type.Q;			
				break;
			case "R":
				pentoType = Type.R;			
				break;
			case "S":
				pentoType = Type.S;			
				break;
			case "T":
				pentoType = Type.T;
				break;
			case "U":
				pentoType = Type.U;			
				break;
			case "V":
				pentoType = Type.V;			
				break;
			case "W":
				pentoType = Type.W;			
				break;
			case "X":
				pentoType = Type.X;			
				break;				
			case "Y":
				pentoType = Type.Y;			
				break;
			case "Z":
				pentoType = Type.Z;			
				break;
		}
		return pentoType;
	}

	private Type type;	   // The pentominoes type from the Conway's Pentominoes encoding [O-Z].
	private	Point[] shape; // The shape will also start from the most LHS possible.

	/**
	*	Constructor.
	*	
	*	@param accepts shapes - list of points (starting point and offsets that represent the shape).
	*	@param accepts type of pentominoes being instantiated.
	*/
	public Pentomino(Point[] shape, Type type) {

		this.type = type;
		this.shape = shape;
	}

	/**
	*	Method for getting pentominoes shape.
	* 
	*	@return a list of Points (offsets).
	*/
	public Point[] getShape() {

		return shape;
	}

	/**
	*	Method for setting the pentominoes shape offsets.
	* 	
	* 	@param accepts a list of new points (shape points).
	*/
	public void setShape(Point[] shape) {

		this.shape = shape;
	}

	/**
	*	Method for getting the type of pentomino.
	*	
	*	@return pentomino type.
	*/
	public Type getType() {

		return type;
	}

	/**
	*	Method for setting the type of pentomino.
	*
	*	@param type of pentomino.
	*/
	public void setType(Type type) {

		this.type = type;
	}

	/**
	*	Method for obtaining the string representation of the pentomino.	
	*
	*	@return a string representation of the Pentomino.
	*/
	public String toString() {

		// All pentominoes can be represented by a 5x5 matrix.
		String[][] matrixForm = new String[5][5];

		// Get starting Point.
		int row = shape[0].x;
		int col = shape[0].y;

		// loops through and 
		matrixForm[row][col] = type.name();
		for(int i = 1; i < shape.length; i++) {

			Point pos = shape[i];
			int rowOffset = row + pos.x;
			int colOffset = col + pos.y;

			matrixForm[rowOffset][colOffset] = type.name();
		}

		// Builds string representation of the pentominoes.
		StringBuilder sb = new StringBuilder();
		for(int y = 0; y < 5; y++) {
			for(int x = 0; x < 5; x++) {
				if(matrixForm[x][y]!=null) {
					sb.append(matrixForm[x][y]);
				} else {
					sb.append(".");
				}
			}
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1); // removes the last spacing.

		return sb.toString();
	}
}