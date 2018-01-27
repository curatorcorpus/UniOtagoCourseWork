
import java.awt.Color;

public class Square {

	private Color color;
	private Position p;
	private int size;

	public Square(Color color, int x, int y, int size) {
		
		this.color = color;
		this.p = new Position(x, y);
		this.size = size;
	}

	public Color getColor() {
		return color;
	}

	public Position getPosition() {
		return p;
	}

	public int getSize() {
		return size;
	}
}