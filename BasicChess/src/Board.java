
import java.util.ArrayList;
import java.security.*;

public class Board {
	public String [][]iniB= {
			{"R","N","B","Q","K","B","N","R"},
			{"P","P","P","P","P","P","P","P"},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{"p","p","p","p","p","p","p","p"},
			{"r","n","b","q","k","b","n","r"}
	};
	public long hashKey=0;
	public static long[] []zobristKeys=new long[64][12];
	public static long []zobristRights=new long [13];//first 4, castling, 8 enpas, 1 sidetoMove
	public 	String[][]cBoard=new String[8][8];
	public long iwP=0b0000000000000000000000000000000000000000000000001111111100000000L;
	public long ibP=0b0000000011111111000000000000000000000000000000000000000000000000L;
	public long iwR=0b0000000000000000000000000000000000000000000000000000000010000001L;
	public long ibR=0b1000000100000000000000000000000000000000000000000000000000000000L;
	public long iwB=0b0000000000000000000000000000000000000000000000000000000000100100L;
	public long ibB=0b0010010000000000000000000000000000000000000000000000000000000000L;
	public long iwN=0b0000000000000000000000000000000000000000000000000000000001000010L;
	public long ibN=0b0100001000000000000000000000000000000000000000000000000000000000L;
	public long iwK=0b0000000000000000000000000000000000000000000000000000000000001000L;
	public long ibK=0b0000100000000000000000000000000000000000000000000000000000000000L;
	public long iwQ=0b0000000000000000000000000000000000000000000000000000000000010000L;
	public long ibQ=0b0001000000000000000000000000000000000000000000000000000000000000L;
	long wP;
	long bP;
	long wN;
	long bN;
	long wB;
	long bB;
	long wQ;
	long bQ;
	long wR;
	long bR;
	long wK;
	long bK;
	int sideToMove=0;
	int moveNumber=0;
	int isWhiteCastled=0;
	int isBlackCastled=0;
	int whiteqscastle=1;
	int whitekscastle=1;
	int blackqscastle=1;
	int blackkscastle=1;
	int enpas=0;
	int inirights[]={0,0,1,1,1,1,0};
	int rights[]={0,0,1,1,1,1,0};
	int temp[]={0,0,1,1,1,1,0};
	int inCheck=0;//is current board position is check if plural means plural check
	Move lastmove;
	long  whitePieces=iwB|iwQ|iwR|iwP|iwN|iwK;
	long  blackPieces=ibB|ibQ|ibR|ibP|ibN|ibK;
	long  blackCapturablePieces=ibB|ibQ|ibR|ibP|ibN;
	long  whiteCapturablePieces=iwB|iwQ|iwR|iwP|iwN;
	long  whiteReach=0;//all the places white can capture or go
	long blackReach=0;
	public Board(int start){
		if(start==1){
			initializeBoard();
			lastmove=null;
			sideToMove=0;
		}
	}
	public Board(Board b,Move move){
		b.makeMove(move);
	}
	//for make and unmake i dont update castling rights yet
	public void unmakeMove(Move movex){
		String moves=movex.move;
		int count=0;
		if(movex.moveType==4){
			unmovePiece(movex.move);
			if(moves.charAt(0)=='7')//white king
			{
				count++;
				if(moves.charAt(3)=='6'){//kingside
					unmovePiece("777512");
				}
				if(moves.charAt(3)=='2')//queenside
				{
					unmovePiece("707312");
				}
				isWhiteCastled=0;
			}
			else{
				if(moves.charAt(3)=='6'){//kingside
					unmovePiece("070512");
				}
				if(moves.charAt(3)=='2')//queenside
				{
					unmovePiece("000312");
				}
				isBlackCastled=0;
			}	
		}
		else if(movex.moveType==3)// enpassant move
		{
			count++;
			unmovePiece(movex.move);
			if(sideToMove==1){
				int row=3;
				int col=movex.move.charAt(3)-48;
				int a= (7-row)*8+(7-col);
				long squ=0b1;
				removePiece(a+8, 6);
				squ<<=a;
				bP=bP|squ;
			}
			if(sideToMove==0){
				int row=4;
				int col=movex.move.charAt(3)-48;
				int a= (7-row)*8+(7-col);
				long squ=0b1;
				removePiece(a-8, 0);
				squ<<=a;
				wP=wP|squ;
			}
		}
		else if (movex.moveType==2){//promotion
			count++;
			String capture=movex.move.substring(4);
			int capflag=0;
			if(!capture.equals("12"))
				capflag=1;
			if(sideToMove==1){
				int col=movex.move.charAt(3)-48;
				int squ=56+(7-col);	
				removePiece(squ, movex.promotionType);
				addPiece(squ, 0);

			}
			else if(sideToMove==0){
				int col=movex.move.charAt(3)-48;
				int squ=(7-col);
				removePiece(squ, movex.promotionType);
				addPiece(squ, 6);
			}
			unmovePiece(movex.move);
		}
		if(count==0)
			unmovePiece(movex.move);
		whitePieces=wB|wQ|wR|wP|wN|wK;
		blackPieces=bB|bQ|bR|bP|bN|bK;
		restoreRights();
		changeSide();
	}
	//this method will make a move update the status of the board
	// change the side
	// most importantly for special types of moves it will make the 
	// necessary changes so this method is more than just changing bit values of bitboards
	public void makeMove(Move movex){
		storeRights();
		movePiece(movex.move);
		if(movex.moveType==4)//castling move
		{
			String moves=movex.move;
			if(moves.charAt(0)=='7')// white king
			{
				if(moves.charAt(3)=='6'){//kingside
					movePiece("777512");
					isWhiteCastled=1;
					rights[0]=1;
				}
				if(moves.charAt(3)=='2')//queenside
				{
					movePiece("707312");
					isWhiteCastled=1;
					rights[0]=1;
				}
			}
			else{
				isBlackCastled=1;
				rights[1]=1;
				if(moves.charAt(3)=='6'){//kingside
					movePiece("070512");
				}
				if(moves.charAt(3)=='2')//queenside
				{
					movePiece("000312");
				}
			}	
		}
		else if(movex.moveType==3)// enpassant move
		{
			if(sideToMove==0){
				int row=3;
				int col=movex.move.charAt(3)-48;
				int a= (7-row)*8+(7-col);
				long squ=0b1;
				squ<<=a;
				bP=bP&~squ;
			}
			if(sideToMove==1){
				int row=4;
				int col=movex.move.charAt(3)-48;
				int a= (7-row)*8+(7-col);
				long squ=0b1;
				squ<<=a;
				wP=wP&~squ;
			}
		}
		else if (movex.moveType==2){//promotion
			if(sideToMove==0){
				int ocol=movex.move.charAt(1)-48;
				int fcol=movex.move.charAt(3)-48;
				int squ=56+(7-fcol);
				addPiece(squ, movex.promotionType);
				removePiece(squ, 0);
			}
			else if(sideToMove==1){
				int col=movex.move.charAt(1)-48;
				int fcol=movex.move.charAt(3)-48;
				int squ=(7-fcol);
				//System.out.println("move: "+movex.move +" side "+ sideToMove+"col:  "+ col+" squ: "+ squ);
				addPiece(squ, movex.promotionType);
				removePiece(squ, 6);
			}
		}
		whitePieces=wB|wQ|wR|wP|wN|wK;
		blackPieces=bB|bQ|bR|bP|bN|bK;
		lastmove=movex;
		updateRights(movex);
		changeSide();
	}
	
	// Update the values of the bitboards correspondingly
	// takes a string of length 4 
	// dont check legality
	// dont check if the starting position have a piece
	// so it moves no piece to a new square which is dumb
	public void movePiece(String x){ 
		String move = x;
		String cp= move.substring(0,2);
		String fp= move.substring(2,4);
		int cr=Integer.parseInt(cp.substring(0,1));
		int cc=Integer.parseInt(cp.substring(1));
		int fr=Integer.parseInt(fp.substring(0,1));
		int fc=Integer.parseInt(fp.substring(1));
		//System.out.println("cr: "+cr+" cc: "+cc+" fr: "+fr+" fc: "+ fc);
		long root=0b1;
		long slide=((7-cr)*8)+(7-cc);
		long newPos=((7-fr)*8)+(7-fc);
		long oldP=root<<slide;
		long newP=root<<newPos;
		//		System.out.println("root: "+root+"oldP: "+ oldP +"  "+ slide+"  longsuz: "+Math.pow(2, slide));
		//		System.out.println("newbit: "+Long.toBinaryString(newP));
		//		System.out.println("newP: "+ newP+"  "+ newPos+"  longsuz: "+Math.pow(2, newPos));
		if(((wB>>newPos)&1)==1)
		{
			wB^=newP;
		}
		if(((bB>>newPos)&1)==1)
		{
			bB^=newP;
		}
		if(((wP>>newPos)&1)==1)
		{
			wP^=newP;
		}
		if(((bP>>newPos)&1)==1)
		{
			bP^=newP;
		}
		if(((wN>>newPos)&1)==1)
		{
			wN^=newP;
		}
		if(((bN>>newPos)&1)==1)
		{
			bN^=newP;
		}
		if(((wR>>newPos)&1)==1)
		{
			wR^=newP;
		}
		if(((bR>>newPos)&1)==1)
		{
			bR^=newP;
		}
		if(((wQ>>newPos)&1)==1)
		{
			wQ^=newP;
		}
		if(((bQ>>newPos)&1)==1)
		{
			bQ^=newP;
		}
		if(((wK>>newPos)&1)==1)
		{
			wK^=newP;
		}
		if(((bK>>newPos)&1)==1)
		{
			bK^=newP;
		}
		if(((wB>>slide)&1)==1)
		{
			wB^=oldP;
			wB|=newP;
		}
		if(((bB>>slide)&1)==1)
		{
			bB^=oldP;
			bB|=newP;
		}
		if(((wP>>slide)&1)==1)
		{
			wP^=oldP;
			wP|=newP;
		}
		if(((bP>>slide)&1)==1)
		{
			bP^=oldP;
			bP|=newP;
		}
		if(((wN>>slide)&1)==1)
		{
			wN^=oldP;
			wN|=newP;
		}
		if(((bN>>slide)&1)==1)
		{
			bN^=oldP;
			bN|=newP;
		}
		if(((wR>>slide)&1)==1)
		{
			wR^=oldP;
			wR|=newP;
		}
		if(((bR>>slide)&1)==1)
		{
			bR^=oldP;
			bR|=newP;
		}
		if(((wQ>>slide)&1)==1)
		{
			wQ^=oldP;
			wQ|=newP;
		}
		if(((bQ>>slide)&1)==1)
		{
			bQ^=oldP;
			bQ|=newP;
		}
		if(((wK>>slide)&1)==1)
		{
			wK^=oldP;
			wK|=newP;
		}
		if(((bK>>slide)&1)==1)
		{
			bK^=oldP;
			bK|=newP;
		}
		whitePieces=wB|wQ|wR|wP|wN|wK;
		blackPieces=bB|bQ|bR|bP|bN|bK;
		blackCapturablePieces=bB|bQ|bR|bP|bN;
		whiteCapturablePieces=wB|wQ|wR|wP|wN;	
	}
	// makes the move back
	public void unmovePiece(String x){
		String move = x;
		String fp= move.substring(0,2);
		String cp= move.substring(2,4);
		String uncap=move.substring(4);
		int cr=Integer.parseInt(cp.substring(0,1));
		int cc=Integer.parseInt(cp.substring(1));
		int fr=Integer.parseInt(fp.substring(0,1));
		int fc=Integer.parseInt(fp.substring(1));
		int uncapture=Integer.parseInt(uncap);
		//System.out.println("cr: "+cr+" cc: "+cc+" fr: "+fr+" fc: "+ fc);
		long root=0b1;
		long slide=((7-cr)*8)+(7-cc);
		long newPos=((7-fr)*8)+(7-fc);
		long oldP=root<<slide;
		long newP=root<<newPos;
		//		System.out.println("root: "+root+"oldP: "+ oldP +"  "+ slide+"  longsuz: "+Math.pow(2, slide));
		//		System.out.println("newbit: "+Long.toBinaryString(newP));
		//		System.out.println("newP: "+ newP+"  "+ newPos+"  longsuz: "+Math.pow(2, newPos));
		int a = 0;
		
		
		if(((wB>>slide)&1)==1)
		{
			wB^=oldP;
			wB|=newP;
		}
		if(((bB>>slide)&1)==1)
		{
			bB^=oldP;
			bB|=newP;
		}
		if(((wP>>slide)&1)==1)
		{
			wP^=oldP;
			wP|=newP;
		}
		if(((bP>>slide)&1)==1)
		{
			bP^=oldP;
			bP|=newP;
		}
		if(((wN>>slide)&1)==1)
		{
			wN^=oldP;
			wN|=newP;
		}
		if(((bN>>slide)&1)==1)
		{
			bN^=oldP;
			bN|=newP;
		}
		if(((wR>>slide)&1)==1)
		{
			wR^=oldP;
			wR|=newP;
		}
		if(((bR>>slide)&1)==1)
		{
			bR^=oldP;
			bR|=newP;
		}
		if(((wQ>>slide)&1)==1)
		{
			wQ^=oldP;
			wQ|=newP;
		}
		if(((bQ>>slide)&1)==1)
		{
			bQ^=oldP;
			bQ|=newP;
		}
		if(((wK>>slide)&1)==1)
		{
			wK^=oldP;
			wK|=newP;
		}
		if(((bK>>slide)&1)==1)
		{
			bK^=oldP;
			bK|=newP;
		}
		if(uncapture==12)
		{
			
		}
		else if(uncapture==2)
		{
			wB|=oldP;
		}
		else if(uncapture==8)
		{
			bB|=oldP;
		}
		else if(uncapture==0)
		{
			wP|=oldP;
		}
		else if(uncapture==6)
		{
			bP|=oldP;
		}
		else if(uncapture==1)
		{
			wN|=oldP;
		}
		else if(uncapture==7)
		{
			bN|=oldP;
		}
		else if(uncapture==3)
		{
			wR|=oldP;
		}
		else if(uncapture==9)
		{
			bR|=oldP;
		}
		else if(uncapture==4)
		{
			wQ|=oldP;
		}
		else if(uncapture==10)
		{
			bQ|=oldP;
		}
		else if(uncapture==5)
		{
			wK|=oldP;
		}
		else if(uncapture==11)
		{
			bK|=oldP;
		}
		whitePieces=wB|wQ|wR|wP|wN|wK;
		blackPieces=bB|bQ|bR|bP|bN|bK;
		blackCapturablePieces=bB|bQ|bR|bP|bN;
		whiteCapturablePieces=wB|wQ|wR|wP|wN;	
	}
	public int isOccupied(int square){
		long all=whitePieces|blackPieces;
		long a=0b1;
		a<<=square;
		if((a&all)==0){
			return 0;
		}
		else
			return 1;
	}

	// initializes the Starting position
	// Sets the bitboards to the initial values
	public void initializeBoard(){
		wP=iwP;
		wB=iwB;
		wR=iwR;
		wQ=iwQ;
		wN=iwN;
		wK=iwK;
		bP=ibP;
		bB=ibB;
		bR=ibR;
		bQ=ibQ;
		bN=ibN;
		bK=ibK;
		sideToMove=0;
		whiteReach=0;
		blackReach=0;
		resetrights();
	}
	private void resetrights() {
		// TODO Auto-generated method stub
		for(int i = 0;i<7;i++)
			rights[i]=inirights[i];
	}
	public void printBitboard(long a){
		for(int i=0;i<8;i++){
    		for (int j = 0; j < 8; j++) {
    			long squ=1;
    			squ<<=(8*(7-i))+(7-j);
    			if((squ&a)!=0)
    				System.out.print("*");
    			else
    				System.out.print("0");
			}
    		System.out.println();
    	}
	}
	// Prints an 8x8 matrix created from the 12 bitboards
	public void printBoard(){
		String [][] board=new String [8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j]="*";
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if(((wB>>(i*8+j))&1)==1)
					board[i][j]="b";
				if(((bB>>(i*8+j))&1)==1)
					board[i][j]="B";
				if(((wR>>(i*8+j))&1)==1)
					board[i][j]="r";
				if(((bR>>(i*8+j))&1)==1)
					board[i][j]="R";
				if(((wP>>(i*8+j))&1)==1)
					board[i][j]="p";
				if(((bP>>(i*8+j))&1)==1)
					board[i][j]="Y";
				if(((wN>>(i*8+j))&1)==1)
					board[i][j]="n";
				if(((bN>>(i*8+j))&1)==1)
					board[i][j]="N";
				if(((wR>>(i*8+j))&1)==1)
					board[i][j]="r";
				if(((bR>>(i*8+j))&1)==1)
					board[i][j]="R";
				if(((wK>>(i*8+j))&1)==1)
					board[i][j]="k";
				if(((bK>>(i*8+j))&1)==1)
					board[i][j]="K";
				if(((wQ>>(i*8+j))&1)==1)
					board[i][j]="q";
				if(((bQ>>(i*8+j))&1)==1)
					board[i][j]="Q";
			}
		}
		for(int i=0;i<8;i++){
			for (int j = 0; j < 8; j++) {
				cBoard[7-i][7-j]=board[i][j];
			}

		}
		for(int i=0;i<8;i++){
			for (int j = 0; j < 8; j++) {
				System.out.print(cBoard[i][j]);
			}
			System.out.println();
		}
		System.out.println("side to move: "+sideToMove);
	}
	//this takes input a text image of the board and changes the bitboards
	public void texttoBoard(String text,int side){
		resetbitboards();
		for(int i =0;i<64;i++){
			char x=text.charAt(i);
			long squ=1;
			squ<<=63-i;
			if(x=='*'){}
			else if(x=='Y'){
				bP|=squ;
			}else if(x=='N'){
				bN|=squ;
			}else if(x=='B'){
				bB|=squ;
			}else if(x=='R'){
				bR|=squ;
			}else if(x=='Q'){
				bQ|=squ;
			}else if(x=='K'){
				bK|=squ;
			}else if(x=='p'){
				wP|=squ;
			}else if(x=='n'){
				wN|=squ;
			}else if(x=='r'){
				wR|=squ;
			}else if(x=='q'){
				wQ|=squ;
			}else if(x=='k'){
				wK|=squ;
			}else if(x=='b'){
				wB|=squ;
			}
		}
		whitePieces=wP|wB|wN|wR|wQ|wK;
		blackPieces=bP|bB|bN|bR|bQ|bK;
		sideToMove=side;
	}
	private void resetbitboards() {
		// TODO Auto-generated method stub
		wK=0;
		wB=0;
		wP=0;
		wN=0;
		wR=0;
		wQ=0;
		bK=0;
		bQ=0;
		bB=0;
		bR=0;
		bN=0;
		bP=0;
	}
	//"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1" input syntax
	void importFEN(String s){
		
	}
	// given a move in programs notation
	// finds the piece associated with the move
	// also detects if its promotion enpassant or castling or regular move
	int detectPieceType(String move){
		int i=sideToMove;
		int row=move.charAt(0)-48;
		int col=move.charAt(1)-48;
		int pos=(7-row)*8+(7-col);
		long square=1;
		square<<=pos;
		if((square&wP)!=0)return 0;else	if((square&wN)!=0)return 1;else	if((square&wB)!=0)return 2;else	if((square&wR)!=0)return 3;
		else if((square&wQ)!=0)return 4;else if((square&wK)!=0)return 5;else if((square&bP)!=0)return 6;else if((square&bN)!=0)return 7;
		else if((square&bB)!=0)return 8;else if((square&bR)!=0)return 9;else if((square&bQ)!=0)return 10;else if((square&bK)!=0)return 11;
		return 12;
	}
	void changeSide(){
		sideToMove=1-sideToMove;
	}
	//adds piece of piecetype to the given pos
	void addPiece(int pos, int pieceType){
		long squ=1;
		squ<<=pos;
		if(pieceType==0)wP|=squ;else if(pieceType==1)wN|=squ;else if(pieceType==2)wB|=squ;else if(pieceType==3)wR|=squ;
		else if(pieceType==4)wQ|=squ;else if(pieceType==5)wK|=squ;else if(pieceType==6)bP|=squ;else if(pieceType==7)bN|=squ;
		else if(pieceType==8)bB|=squ;else if(pieceType==9)bR|=squ;else if(pieceType==10)bQ|=squ;else if(pieceType==11)bK|=squ;
		
	}
	//removes piece of piecetype from the given pos
		void removePiece(int pos, int pieceType){
			long squ=1;
			squ<<=pos;
			long bitb=0;
			if(pieceType==0)wP&=~squ;
			else if(pieceType==1)wN&=~squ;
			else if(pieceType==2)wB&=~squ;
			else if(pieceType==3)wR&=~squ;
			else if(pieceType==4)wQ&=~squ;
			else if(pieceType==5)wK&=~squ;
			else if(pieceType==6)bP&=~squ;
			else if(pieceType==7)bN&=~squ;
			else if(pieceType==8)bB&=~squ;
			else if(pieceType==9)bR&=~squ;
			else if(pieceType==10)bQ&=~squ;
			else if(pieceType==11)bK&=~squ;

		}
		void storeRights(){
			for(int i=0;i<7;i++)
				temp[i]=rights[i];
		}
		void restoreRights(){
			for(int i=0;i<7;i++)
				rights[i]=temp[i];
		}
		void updateRights(Move movex){
			if(movex.pieceType==5)
			{
				rights[2]=0;
				rights[3]=0;
			}
			if(movex.pieceType==11)
			{
				rights[4]=0;
				rights[5]=0;
			}
			if(movex.pieceType==3){
				int fromcol=movex.move.charAt(1);
				if(fromcol==0)
					rights[2]=0;
				else
					rights[3]=0;
			}
			if(movex.pieceType==9){
				int fromcol=movex.move.charAt(1);
				if(fromcol==0)
					rights[4]=0;
				else
					rights[5]=0;
			}
		}
		public static void generateZobrist() {
			SecureRandom rand=new SecureRandom();
			
			// TODO Auto-generated method stub
			for(int i = 0;i<64;i++){
				for(int j=0;j<12;j++){
					zobristKeys[i][j]=rand.nextLong();
				}
			}
			for(int i=0;i<13;i++){
				zobristRights[i]=rand.nextLong();
			}	
		}
		
		public void calculateHashVal(){
			long returnKey=0;
			for(int i =0;i<64;i++){
				long a =1;
				if((wP&(a<<i))!=0){
					returnKey^=zobristKeys[i][0];
				}
				else if((bP&(a<<i))!=0){
					returnKey^=zobristKeys[i][6];
					
				}else if((wN&(a<<i))!=0){
					returnKey^=zobristKeys[i][1];

				}else if((bN&(a<<i))!=0){
					returnKey^=zobristKeys[i][7];
					
				}else if((wK&(a<<i))!=0){
					returnKey^=zobristKeys[i][2];
					
				}else if((bK&(a<<i))!=0){
					returnKey^=zobristKeys[i][8];
					
				}else if((wR&(a<<i))!=0){
					returnKey^=zobristKeys[i][3];
					
				}else if((bR&(a<<i))!=0){
					returnKey^=zobristKeys[i][9];
					
				}else if((wQ&(a<<i))!=0){
					returnKey^=zobristKeys[i][4];
					
				}else if((bQ&(a<<i))!=0){
					returnKey^=zobristKeys[i][10];
					
				}else if((wK&(a<<i))!=0){
					returnKey^=zobristKeys[i][5];
					
				}else if((bK&(a<<i))!=0){
					returnKey^=zobristKeys[i][11];
					
				}
				
			}
			for(int i = 2;i<6;i++){
				if(rights[i]==1)
					returnKey^=zobristRights[i-2];
			}
			if(sideToMove==0)
				returnKey^=zobristRights[12];
			
			hashKey=returnKey;
		}
		
}
