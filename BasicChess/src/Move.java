import java.util.ArrayList;

// this holds some information about a move
// belki bunu yaparsam path generation kolaylasir
public class Move implements Comparable <Move>{
	public Move parent;
	public String move;
	public int pieceType;
	public int moveType=0;
	public int pinned=0;
	public int side=0;// whose move is this
	public double moveScore=0;
	public Move child;
	int promotionType=0;// bu sayilar piecetype ile ayni
	  static final int NORMAL = 0;
	  static final int PAWNDOUBLE = 1;
	  static final int PAWNPROMOTION = 2;
	  static final int ENPASSANT = 3;
	  static final int CASTLING = 4;
	@Override
	public int compareTo(Move move1) {
		// TODO Auto-generated method stub
		int compareScore = (int) ((Move) move1).moveScore;

		//ascending order
		if(move1.side==1)

		return (int) (this.moveScore - compareScore);

		//descending order
		else
			return (int) (compareScore - this.moveScore);
	}
}
