import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.MaskFormatter;
import javax.swing.text.html.MinimalHTMLWriter;
//projemdeki suan icin en buyuk sorun her hamlenin legal olup olmadıgını anlamak 
//icin cok fazla islem yapıyorum gereksiz hamle uretiyorum
public class main extends JPanel{

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Board.generateZobrist();
		Board startB=new Board(1);
		startB.cBoard=startB.iniB;
		//startB.movePiece("207312");
		//startB.unmovePiece("672010");
		//startB.printBoard();
		//startB.printBitboard(startB.whitePieces);
		UCI uci = new UCI(startB);
		String inputfile="gamesout.txt";
		String outputfile="elleyap.txt";
		//simul();
		ArrayList<Move> moves=new ArrayList<>();
		MoveGenerator movegen=new MoveGenerator();
		//movegen.addMove(moves, startB, 0, 53, 45);
	/*	MoveGenerator movegen=new MoveGenerator();
		System.out.println("isbet: "+		movegen.isbetween(51, 37, 58));
		long b=1;
   	 System.out.println("bakalım olacak mı"+(b<<0));
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
        String board="";
	    	for(int i =0;i<8;i++)
	        {
	             String a = input.nextLine();
	             board=board+a;
	        }
	    	int side=input.nextInt();
	     startB.texttoBoard(board,side);
	     startB.printBoard();
	     ArrayList<Move>movelist=new ArrayList<>();
	     movelist=movegen.legalMoves(startB, side, null);
	     for(Move move: movelist){
	    	 startB.makeMove(move);
	    	 startB.printBoard();
		     ArrayList<Move>movelist1=new ArrayList<>();
		     movelist1=movegen.legalMoves(startB, 1-side, null);
		     for(Move move2:movelist1){
		    	 System.out.println("move: "+ move2.move);
		     }
		     startB.unmakeMove(move);
	     } */
		//System.out.println(movegen.isbetween(52, 38, 59));
	/*	MoveGenerator movegen=new MoveGenerator();
		ArrayList<Move>moves=new ArrayList<>();
		moves=movegen.legalMoves(startB, 0, null);
		startB.printBoard();
		long reach=0;
		for(int i =6;i<12;i++)
			reach|=movegen.pieceReach[i];
		String a= "13" ;
		for(int i=0;i<movegen.pinnedcount;i++)
			System.out.println("açmazda: "+movegen.pinnedpiece[i]);
		for(int i=0;i<6;i++)
			System.out.println("şah çekiyor: "+movegen.checkpiece[6+i]);
		System.out.println("incheck: "+startB.inCheck);
		for(Move movex: moves){
			System.out.println("legal: "+movex.move);
		}*/
		//System.out.println((int)(Math.log(startB.bK)/Math.log(2)));
		//String outputfile="gamesout.txt";
		//String inputfile = "C:\\Users\\toshıba\\Desktop\\Hepsi.pgn";
		//uci.Converter(inputfile,outputfile);
		
		while(true)
		{
				
			uci.uciCommunication();
		}
	//position startpos moves e2e4 b8c6 d2d4 e7e5 d4d5 f8b4 c2c3 d8h4 c3b4 h4e4 g1e2 e4b4 c1d2
	//position startpos moves e2e4 b8c6 d2d4 e7e5 d4d5 f8b4 c2c3 d8h4 c3b4 h4e4 g1e2 e4b4 c1d2 b4b2 d5c6 d7c6 d2c3 b2c3
	//position startpos moves e2e4 b8c6 d2d4 e7e5 d4d5 d8h4 d5c6 h4e4 g1e2 d7c6 b1c3 e4h4 g2g3 h4g4 f1g2 c8f5 e1g1 f8c5 c1e3 c5e3 f2e3 g8f6 e3e4 f5e6

		
		//DrawGraph.draw();
		//readWrite();

		
		//startB.printBoard();
		//position startpos moves e2e4 d7d5 e4d5 d8d5 b1c3 d5a5 g1f3 a5c5 d2d4 c5a5 f1c4 c8g4 e1g1 b8d7 d1d3 e7e6 f3e5 d7e5 d4e5 a8d8 d3e4 g4f5 e4b7

		//String input = "position startpos moves e2e4 d7d5 e4d5 d8d5 b1c3 d5a5 g1f3 a5c5 d2d4 c5a5 f1c4 c8g4 e1g1 b8d7 d1d3 e7e6 f3e5 d7e5 d4e5 a8d8 d3e4 g4f5 e4b7";
		//uci.inputPosition("startpos moves e2e3 e7e5 g1e2 b8c6 f2f4 d7d5 e2c3 d5d4 c3e4 d4e3 f1b5 c6b4");
		//uci.inputPosition(input);
		//startB.printBoard();
		//Evaluate eval = new Evaluate(startB);
		//eval.positionalValues1();
		// adjacent calculator

		/*		
		Evaluate eval = new Evaluate(startB);
		long adjac = eval.adjacentSquCalc(44);
		for(int i = 0;i<64;i++){
			long a =1;
			a<<= i;
			if((a&adjac)!=0)
				System.out.println(i);
		}  */



		//attacked point
		/*		Evaluate eval = new Evaluate(startB);
		eval.positionalValues();
		startB.printBoard();
		System.out.println(eval.blackkingattackedpoint);
		System.out.println(eval.whitekingattackedpoint); */

		/*	ArrayList<Individual> indlist = evol.initializeGeneration(4);
		ArrayList<Individual> next = evol.nextGen(indlist, 0.75, 0);
		 int count =  0 ; 
		for(Individual i : indlist){
			System.out.println("İlk Individual weights : "+ count++);
			i.printWeights();
		}
		for(Individual i : next){
			System.out.println(" Individual weights : "+count++);
			i.printWeights();
		} */
			
			
			
			// grid

	
				
	/*	PrintStream out = new PrintStream(new FileOutputStream("genel.txt"));
		System.setOut(out);
		System.out.println("gamefile : "+gamefile);
		System.out.println("generation num: "+generationnum);
		System.out.println("position num: "+positionnum);
		System.out.println("population size: "+populationsize);
		System.out.println("Best rates: ");
		System.out.println("i: "+ besti);
		System.out.println("j: "+ bestj);
	*/


		// mutation
		/*	ArrayList<Individual> indlist = evol.initializeGeneration(1);
		for(Individual i : indlist){
			System.out.println("Initial weights "+i);
			i.printWeights();
			i = evol.mutate(i, 0);
			System.out.println("then weights "+i);
			i.printWeights();
		}  */



		// statik pozisyon cozdurme
		/*	Search src = new Search();
		int count=0;
		for(int i = 0; i<1000;i++){
			String move = uci.Solver(i);
			System.out.print(i+"  grandmaster move: "+ move);
			Move bestmove= src.minimax2(startB, 0, 1, -99999, 99999, null, 0);// simdilik nullmove veriyorum enpas
			String bstmv=uci.moveToAlgebra(bestmove);
			System.out.println("  my bestmove: "+ bstmv);
			if(move.substring(0,4).equals(bstmv)){
				count++;
			}
		}
		System.out.println("tutturdum: "+ count); */



		/*BufferedReader reader=new BufferedReader(new FileReader("output2.txt"));
		StringTokenizer st=new StringTokenizer(reader.readLine());
		String hamle="startpos moves ";// burada sey hatası var yani ben zaten hamleleri cevirdim
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
		BufferedReader reader=new BufferedReader(new FileReader("C:\\Users\\toshıba\\Desktop\\twic1148.pgn"));
		String line;
		int count=0;
        while ((line = reader.readLine()) != null) {
            if(line.contains("WhiteElo")){
            	count++;
            }

        }
        System.out.println("oyun sayısı: "+ count);
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


		
	/*	MoveGenerator movegen=new MoveGenerator();
		ArrayList<Move> allmoves=new ArrayList<>();
		startB.printBoard();
		//System.out.println("lastmove: "+ startB.lastmove.move+" movetype: "+ startB.lastmove.moveType);
		allmoves = movegen.moveGenerator(startB, 0, 1, null);
		for(Move i:allmoves){
			startB.makeMove(i);
			Evaluate eval = new Evaluate(startB);
			double score = eval.positionalValues();
			i.moveScore=score;
			System.out.println("move: "+ i.move+" movetype: "+ i.moveType+" score: "+i.moveScore);
			startB.unmakeMove(i);
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
	public static void readWrite() throws IOException{
		String inputfile="grap4rastgele.txt";
		String outputfile="deneme.txt";
		BufferedReader reader=new BufferedReader(new FileReader(inputfile));
		PrintStream out = new PrintStream(new FileOutputStream(outputfile));
		System.setOut(out);
		String line = reader.readLine();
		int count =1;
		int total=0;
		 while ((line = reader.readLine()) != null) {
			 if(line.startsWith("individual: ")){
				 int i=line.indexOf("solved");
				 line=line.substring(i+8);
				 int a = Integer.parseInt(line);
				 total+=a;
				 //System.out.println(count);
				 count++;
			 }
		 }
		 System.out.println(total/count);
	}
	
	public static void simul() throws IOException{
	
		String outputfile="3hamleli2li.txt";
		// evolution
		Evolution evol = new Evolution();
		String gamefile= "gamesout.txt";	
		int generationnum=100;
		int populationsize=10;// 10 cok az
		int positionnum=1000;
		double crossoverrate=0.25;
		double mutationrate=0.07; 
		int upperbound=2;// bu positional parametreler icin taslara 3 verdim aklımdan kullanmıyorum
		Individual bestind = new Individual();
		int besti=0;
		int bestj=0;
		bestind.positionssolved=0;
		evol.startEvol(generationnum, populationsize, positionnum , 
						crossoverrate, mutationrate,upperbound,gamefile,outputfile);
	}
}
