import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.text.ChangedCharSetException;
import javax.swing.text.MaskFormatter;
//projemdeki þuan için en büyük sorun her hamlenin legal olup olmadýðýný anlamak 
//için çok fazla iþlem yapýyorum gereksiz hamle üretiyorum
public class main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Board startB=new Board(1);
		startB.cBoard=startB.iniB;
		//startB.movePiece("207312");
		//startB.unmovePiece("672010");
		//startB.printBoard();
		//startB.printBitboard(startB.whitePieces);
		
		UCI uci = new UCI(startB);
		

		while(true)
		{
			uci.uciCommunication();
		}
		
		
		/*BufferedReader reader=new BufferedReader(new FileReader("output2.txt"));
		StringTokenizer st=new StringTokenizer(reader.readLine());
		String hamle="startpos moves ";// burada þey hatasý var yani ben zaten hamleleri çevirdim
		for(int i=0;i<20;i++){
			hamle+=st.nextToken()+" ";
		}
		uci.inputPosition(hamle);
		//uci.inputGo("depth 1");
		uci.inputPrint(); */
//position startpos moves d2d4 b8c6 c1f4 d7d5 b1c3 c8g4 h2h3 g4h5 g2g4 h5g6 g1f3 e7e6 f4g5 g6c2 d1c2 c6d4 c2a4 b7b5 a4d4 f8c5 d4c5 d8d7 c3b5 h7h6 b5c7 d7c7 c5c7 h6g5 c7c6 e8e7 c6a8 e7d6 f3g5 f7f6 g5f7 d6c5 a8a7 c5c4 f7h8 f6f5 a7g7 f5g4 h3g4 d5d4 g7g8 c4d5 f1g2 d5e5 h8g6 e5f6 h1h7 e6e5 g8f7

		
		
		// Enpassant debugger
		/*ArrayList< Move> list = new ArrayList<>();
		startB.printBoard();
		startB.movePiece("624212");	
		Move move1=new Move();
		move1.moveType=1;
		move1.move="644412";
		startB.printBoard();
		startB.makeMove(move1);
		MoveGenerator movegen=new MoveGenerator();
		movegen.enPassantMoves(list, startB, 1, move1);
		for(Move hamle: list){
			System.out.println("enpas: "+ hamle.move);
			startB.makeMove(hamle);
			startB.printBoard();
			startB.unmakeMove(hamle);
		}
		startB.printBoard(); */
		
		
		
		/*uci.inputPosition("startpos moves e2e3 e7e5 g1e2 b8c6 f2f4 d7d5 e2c3 d5d4 c3e4 d4e3 f1b5 c6b4");
		MoveGenerator movegen=new MoveGenerator();
		int i =movegen.isCheck(startB, 1);*/
		//uci.Converter2();
		//uci.Reader();

		
		/*System.out.println("arda");
		BufferedReader reader=new BufferedReader(new FileReader("C:\\Users\\toshýba\\Desktop\\twic1148.pgn"));
		String line;
		int count=0;
        while ((line = reader.readLine()) != null) {
            if(line.contains("WhiteElo")){
            	count++;
            }

        }
        System.out.println("oyun sayýsý: "+ count);
        uci.Reader();*/
        //System.out.println(uci.pgnToAlg("h6", 1));
		/*
		Evaluate eval=new Evaluate(startB);
		System.out.println("passed pawn: "+ eval.passedPawn(15, 0));
		System.out.println("rook behind pawn: "+ eval.isrookbehind(15, 0));
		System.out.println("is rook connected: "+eval.isrookconnected(0, 0));
		System.out.println("is rook on open file: "+eval.isrookOpenFile(0, 0));
		System.out.println("is rook on 7th rank: "+eval.isrookSeventh(0,0));
		System.out.println("is pawn backward: "+eval.backPawn(51, 1));
		System.out.println("is bishop on large : "+eval.isbislargeDiag(9, 0));
		System.out.println("center pawn: "+ eval.weakSquare(0));
		System.out.println("castle: "+ startB.isBlackCastled);
		System.out.println("positional value score: "+ eval.positionalValues());
		System.out.println("iso passed: "+ eval.isoPawn(48, 1));*/


/*
		MoveGenerator movegen=new MoveGenerator();
		ArrayList<Move> allmoves=new ArrayList<>();
		startB.changeSide();
		startB.printBoard();
		//System.out.println("lastmove: "+ startB.lastmove.move+" movetype: "+ startB.lastmove.moveType);
		movegen.diagonalMove(allmoves, startB, 1, "B");
		for(Move i:allmoves){
			System.out.println("bishop antidiag black : "+ i.move);
			}  */
		//long e=movegen.maskAntid(startB.whitePieces, 33);
		//System.out.println("aa"+Long.toBinaryString(e));
		//for(int i=0;i<64;i++)
			//if((startB.whitePieces>>i)%2==1)
				//System.out.print("1");
			//else
				//System.out.print("0");
		
		//System.out.println("is Occupied"+startB.isOccupied(14));
		//Search search=new Search(startB, 0, 3);
		//String s=search.minimax(startB, 0, 3);
		//System.out.println(" hamle seq: "+ s);
		
		//startB.printBoard();
		//search.printPath();
//		Evaluate eval=new Evaluate();
//		System.out.println(eval.materialcount(startB));
//			System.out.println("blackreach: "+Long.toBinaryString(startB.blackReach));
			//System.out.println("a"+i);
		//System.out.print(Long.toBinaryString(~startB.whitePieces));
		//System.out.println(Long.toBinaryString(e));
		//System.out.println(a+"  "+b+"  c: "+c);
	}
}
