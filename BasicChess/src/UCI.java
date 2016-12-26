import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
public class UCI {
	public UCI (Board b){
		this.board=b;
	}
    static String ENGINENAME="BasicChess";
    Board board;
    MoveGenerator movegen=new MoveGenerator();
    Search search=new Search();
    Move lastmove;
    String []games;
    public void uciCommunication() throws IOException {
        Scanner input = new Scanner(System.in);
        while (true)
        {
            String inputString=input.nextLine();
            if ("uci".equals(inputString))
            {
                inputUCI();
            }
            else if (inputString.startsWith("setoption"))
            {
                inputSetOption(inputString);
            }
            else if ("isready".equals(inputString))
            {
                inputIsReady();
            }
            else if ("ucinewgame".equals(inputString))
            {
                inputUCINewGame();
            }
            else if (inputString.startsWith("position"))
            {
                inputPosition(inputString);
            }
            else if (inputString.startsWith("go"))
            {
                inputGo(inputString);
            }
            else if (inputString.equals("stop"))
            {
                inputQuit();
            }
            else if ("print".equals(inputString))
            {
                inputPrint();
            }
            else if ("whitereach".equals(inputString))
            {
                printReach(0);
            }
            else if ("blackreach".equals(inputString))
            {
                printReach(1);
            }
            else if ("whitepiece".equals(inputString))
            {
                board.printBitboard(board.whitePieces);
            }
            else if ("blackpiece".equals(inputString))
            {
                board.printBitboard(board.blackPieces);
            }
            else if (inputString.startsWith("solve position "))
            {
            		//Solver(inputString.substring(15)+" ");
            }
        }
    }
    public  void inputUCI() {
        System.out.println("id name "+ENGINENAME);
        System.out.println("id author Arda");
        //options go here
        System.out.println("uciok");
    }
    public  void inputSetOption(String inputString) {
        //set options
    }
    public  void inputIsReady() {
         System.out.println("readyok");
    }
    public  void inputUCINewGame() {
        //add code here
    }
    // this method makes all the moves from gui
    public  void inputPosition(String input) {
        input=input.substring(9).concat(" ");
        if (input.contains("startpos ")) {
            input=input.substring(9);
            board.initializeBoard();
            board.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        }
        else if (input.contains("fen")) {
            input=input.substring(4);
            board.importFEN(input);
        }
        if (input.contains("moves")) {
            input=input.substring(input.indexOf("moves")+6);
            while (input.length()>0)
            {
            	String algmove=input.substring(0,4);
            	String mytypemove=algebraToMove(algmove);
            	Move movex=new Move();
            	movex.pieceType=board.detectPieceType(mytypemove);
            	movex.move=mytypemove;
            	movex.moveType=movegen.moveTypeDetector(board, mytypemove, movex.pieceType);
            	// in the case of promotion 	
            	//System.out.println("movestr:"+ mytypemove+"  pieceType:"+ movex.pieceType);
            	if(input.charAt(4)=='q')movex.promotionType=4+(6*board.sideToMove);else if(input.charAt(4)=='n')movex.promotionType=1+(6*board.sideToMove);
            	else if(input.charAt(4)=='b')movex.promotionType=2+(6*board.sideToMove);else if(input.charAt(4)=='r')movex.promotionType=3+(6*board.sideToMove);
               if (board.sideToMove==0) {// belki kiþiye göre degiþtirebilirim burayi
            	   board.makeMove(movex);
                } else {
             	   board.makeMove(movex);
                }
               // algebraToMove(input);position startpos moves e2e4 g8f6 e4e5 f6e4 d2d3 e4c5 b1c3 b8c6 g1f3 b7b6 d3d4 c5e6 f1b5 c6d4 f3d4 c8a6 b5a6 e6c5 a6c4 d7d5 c4d5 c5d3 d1d3 a8b8 d5c6 d8d7 c6d7
               lastmove= movex;
                input=input.substring(input.indexOf(' ')+1);
            }
        }
    }
    public  void inputGo(String inputstring) {
        String move="131312";
        int depth=1;
        if(inputstring.contains("depth")){
            inputstring=inputstring.substring(inputstring.indexOf("depth")+6);
          //  System.out.println(inputstring.charAt(0));
            depth=(int)inputstring.charAt(0)-48;
        }
        /* if (Orion.WhiteToMove) {
            move=Moves.possibleMovesW(Orion.WP,Orion.WN,Orion.WB,Orion.WR,Orion.WQ,Orion.WK,Orion.BP,Orion.BN,Orion.BB,Orion.BR,Orion.BQ,Orion.BK,Orion.EP,Orion.CWK,Orion.CWQ,Orion.CBK,Orion.CBQ);
        } else {
            move=Moves.possibleMovesB(Orion.WP,Orion.WN,Orion.WB,Orion.WR,Orion.WQ,Orion.WK,Orion.BP,Orion.BN,Orion.BB,Orion.BR,Orion.BQ,Orion.BK,Orion.EP,Orion.CWK,Orion.CWQ,Orion.CBK,Orion.CBQ);
        }*/
//position startpos moves e2e4 e7e5 d2d4 e5d4 d1d4 d7d5 d4g7 f8g7 e4d5 d8d5 b1c3 g7c3 b2c3 d5g2 f1g2 b8c6 g2c6 b7c6 c1f4 a8b8 g1e2 b8b2 e2d4 b2a2 a1a2 g8f6 a2a7 h8g8 a7a8 g8g2 a8c8 e8e7 c8c7 f6d7 f4g3 g2g3 h2g3 h7h5 h1h5 f7f5 h5f5 e7d8 g3g4 d8c7 g4g5 d7e5 f5e5 c7b8 e5e7 b8a8 g5g6 c6c5 d4b5 c5c4 g6g7
        search.maxDepth=depth;
       // String movepath= search.minimax(board, board.sideToMove, depth);
        Move nullmove=new Move();
        Move bestmove=search.minimax2(board, board.sideToMove, depth,-1000000,1000000,lastmove,1);
    //position startpos moves e2e4 d7d5 e4d5 d8d5 b1c3 d5d4 g1f3 d4f6 f1e2
      //  System.out.println("minimax2: "+ bestmove.move+ "score : "+ bestmove.moveScore);
        //System.out.println("path: "+bestmove.move);
        System.out.println("bestmove "+moveToAlgebra(bestmove) +" score: "+bestmove.moveScore);
        Move parent= bestmove;
        for(int i=0;i<depth-2;i++){
        	Move child=parent.child;
        	parent=parent.child;
        	if(child!=null)
        	System.out.println("move : "+ moveToAlgebra(child)+" movescore: "+ child.moveScore);
        }
    }
    //burada sey hatasi var bu promotion bilgisi gitmiyor
    public  String moveToAlgebra(Move moves) {
    	String move=moves.move;
        String append="";
        int orow=move.charAt(0)-48;
        int ocol=move.charAt(1)-48;
        int frow=move.charAt(2)-48;
        int fcol=move.charAt(3)-48;
        int start=0,end=0;
        String returnMove="";
        returnMove+=(char)('a'+ocol);
        returnMove+=(char)('8'-orow);
        returnMove+=(char)('a'+fcol);
        returnMove+=(char)('8'-frow);
        if(moves.moveType==2){
        	if(moves.promotionType==4||moves.promotionType==10){
        		append="q";
        	}
        	else if(moves.promotionType==3||moves.promotionType==9){
        		append="r";
        	}else if(moves.promotionType==2||moves.promotionType==8){
        		append="b";
        	}else if(moves.promotionType==1||moves.promotionType==7){
        		append="n";
        	}
        }
        returnMove+=append;
        return returnMove;
    }
    public  String algebraToMove(String input) {
        //int start=0,end=0;
        int orow=('8'-input.charAt(1));
        int ocol=(input.charAt(0)-'a');
        int frow=('8'-input.charAt(3));
        int fcol=(input.charAt(2)-'a');
        int cpt=movegen.isCapture(board, ""+frow+""+fcol);
        String moves=""+orow+""+ocol+""+frow+""+fcol+""+cpt;
        return moves;
   }
    public  void inputQuit() {
        System.exit(0);
    }
    public  void inputPrint() {
    	board.printBoard();
        //System.out.println(Zobrist.getZobristHash(Orion.WP,Orion.WN,Orion.WB,Orion.WR,Orion.WQ,Orion.WK,Orion.BP,Orion.BN,Orion.BB,Orion.BR,Orion.BQ,Orion.BK,Orion.EP,Orion.CWK,Orion.CWQ,Orion.CBK,Orion.CBQ,Orion.WhiteToMove));
    }
    public void printReach (int a){
    	long reach=0;
    	if(a==0)
    		reach=board.whiteReach;
    	if(a==1)
    		reach=board.blackReach;
    	for(int i=0;i<8;i++){
    		for (int j = 0; j < 8; j++) {
    			long squ=1;
    			squ<<=(8*(7-i))+(7-j);
    			if((squ&reach)!=0)
    				System.out.print("*");
    			else
    				System.out.print(" ");
			}
    		System.out.println();
    	}
    }
    public String Solver( int gamenum) throws IOException{
    	String moves=games[gamenum];
    	if(moves.charAt(moves.length()-1)=='+')
    	{
    	String move= "position startpos moves"+moves.substring(0, moves.length()-6);
    	inputPosition(move);
    	return moves.substring(moves.length()-5);
    	}
    	String move= "position startpos moves"+moves.substring(0, moves.length()-5);
    	inputPosition(move);
    	return moves.substring(moves.length()-4);
    	//inputGo("depth 1");
    }
    public void Reader(int gamenum, String inputfile) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(inputfile));
		System.out.println(gamenum);
		String line="";
		int movenum=1;
		int countgame=0;
		int countmove=0;
		while(countgame!=gamenum&&(line= reader.readLine())!= null){
			String moves="";
			countmove=0;
			if(!line.contains("end"))
				line+=reader.readLine();
			StringTokenizer str=new StringTokenizer(line);
			int allmove=str.countTokens();
			movenum=allmove/5;
			while(movenum!=countmove)
			{
				moves+=" "+str.nextToken();
				countmove++;
			}
			games[countgame]=moves;
			//System.out.println(moves);
			countgame++;
		}
		
	/*	System.out.println(" "+line);
		System.out.println(moves);
		System.out.println("move num : "+ movenum);*/
	//	return moves;
    }
	public  void Converter2() throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader("output.txt"));
        PrintStream out = new PrintStream(new FileOutputStream("output2.txt"));
        int count=0;
        System.setOut(out);
        String line="";
        while ((line = reader.readLine())!=null) {
            StringTokenizer st = new StringTokenizer(line);
            if(line.contains("1974"))
            	break;
            while(st.hasMoreTokens()){
            	String tok = st.nextToken();
            	if(tok.equals("end"))
            		break;
            	String hamle=algebraToMove(tok)+" ";	
            	System.out.print(hamle);
            }
            System.out.println(" end");
            count++;
        }
        System.out.println(count);
	}
	public  void Converter(String inputfile , String outputfile) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(inputfile));
        PrintStream out = new PrintStream(new FileOutputStream(outputfile));
        System.setOut(out);
		String line="";
		String temp="";
		String game="";
	//	String algebraic="position startpos moves ";
		int count=0;
        while ((line = reader.readLine()) != null) {
        	game="";
        	if(line.contains("1. ")){
        		do{
        			game+=line;
        			game+="\n";
        			temp=line;
        			//System.out.println(line);
        			line=reader.readLine();
        		}while(!(temp.contains("0-1")||temp.contains("1-0")||temp.contains("1/2-1/2")));
        		//end of a game
        		System.out.print(tokenizer(game));
        		System.out.println(" end of game");
        		count++;
        	}

        }
        System.out.println(count);
        out.close();
     //   System.out.println(game);

	}
	String tokenizer(String game){
		String tok="";
        StringTokenizer st = new StringTokenizer(game);
        while(st.hasMoreTokens()){
        	String token=st.nextToken();
        	
        	if((token.contains("0-1")||token.contains("1-0")||token.contains("1/2-1/2")||token.contains("*")))
        		break;
        	if(token.contains("."))
        		token=st.nextToken();
        		tok+=token+" ";
        }
       return tok;
	}
	// takes a move in pgn format and turns it into a algebraic move
	  String pgnToAlg(String token,int side) {
		// TODO Auto-generated method stub
		int len=token.length();
		String tok=token;
		String move="";
		long mypaw=0;
		int dir=0;
		if(len==2)// pawn forward move buyuk ihtimalle dogru caliþiyor
		{
			if(side==0){
				mypaw=board.wP;
				dir=-1;
			}else{
				mypaw=board.bP;
				dir=1;
			}
			move+=tok.charAt(0);
			int col=7-(tok.charAt(0)-'a');
			int orow=(tok.charAt(1)-49);
			int row=(tok.charAt(1)-49);// burdaki col anlayiþi bizde olan normalin tersi
			long a=0;
			for(int i=0;i<2;i++){
				a=1;
				row+=dir;
				a<<=(8*row+col);
				if((mypaw&a)!=0){
					move+=(row+1);
					move+=tok.charAt(0);
					move+=(orow+1);
					return move;
				}
			}
		}
		else if(len==3){
			if(tok.equals("0-0")){// castling
				if(side==0)
				{
					return "e1g1";
				}
				else{
					return "e8g8";
					
				}
			}
			else{// non-capture non-pawn move
				char piece=tok.charAt(0);
				long bitboard=pgnPieceDetector(piece,side);
				
			}
		}
		return null;
	}
	 long pgnPieceDetector(char harf, int side) {
		// TODO Auto-generated method stub
		if(side==0){
			if(harf=='N')return board.wN;else if(harf=='B')return board.wB; else if(harf=='R')return board.wR; else if(harf=='Q')return board.wQ;
			else if(harf=='K')return board.wK; 
		}
		else if(side==1){
			if(harf=='N')return board.bN;else if(harf=='B')return board.bB; else if(harf=='R')return board.bR; else if(harf=='Q')return board.bQ;
			else if(harf=='K')return board.bK; 
		}
		return 0;
	}
}