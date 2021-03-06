import java.util.ArrayList;
// burada bir hamlenin capture olup olmadigini bilmiyorum ama aslinda
// move generator direk move yapmayacagi icin bu bilgileri tasisam cok iyi olur 
// yada isCapture captureType gibi functionlar yapabilirim not: bunu ekledim
// move gibi bir class aciip 4 karakterlik stringden o move hakkinda her bilgiyi alabilirim
// once initial position ile butun bitboardlar karsilastirilip hamle yapacak tas bulunur
public class MoveGenerator {
	public MoveGenerator(){

	}
	long row1=0b0000000000000000000000000000000000000000000000000000000011111111L;
	long row2=0b0000000000000000000000000000000000000000000000001111111100000000L;
	long row3=0b0000000000000000000000000000000000000000111111110000000000000000L;
	long row4=0b0000000000000000000000000000000011111111000000000000000000000000L;
	long row5=0b0000000000000000000000001111111100000000000000000000000000000000L;
	long row6=0b0000000000000000111111110000000000000000000000000000000000000000L;
	long row7=0b0000000011111111000000000000000000000000000000000000000000000000L;
	long row8=0b1111111100000000000000000000000000000000000000000000000000000000L;
	long col1=0b1000000010000000100000001000000010000000100000001000000010000000L;
	long col8=0b0000000100000001000000010000000100000001000000010000000100000001L;
	int[] dirs={-9,-8,-7,-1,1,7,8,9};
	long []pieceReach=new long[12];
	int checkpiece[]=new int [12];
	int pinnedpiece[]=new int[10];
	int pinnedcount=0;
	int pinite=0;
	int [] pieceMobs=new int  [12];
	public ArrayList<Move> legalMoves(Board b, int side, Move lastmove){
		b.inCheck=0;
		reset();
		int kingsqu=1;
		if(side==0)
			kingsqu=(int) (Math.log(b.wK)/Math.log(2));
		if(side==1)
			kingsqu=(int)(Math.log(b.bK)/Math.log(2));
		pinnedCalc(b,kingsqu,side);	
		ArrayList<Move> legalmoves = new ArrayList<>();
		ArrayList<Move>moves = newmoveGenerator(b, side, lastmove);
		long kingmove=1;
		long enemyreach=0;
		for(int i =0;i<6;i++){
			enemyreach|=pieceReach[(1-side)*6+i];
		}
		/*for(int i =0;i<pinnedcount;i++){
			System.out.println("pinnedpiece: "+pinnedpiece[i]);
		}
		*/
		//b.printBitboard(enemyreach);
		//position startpos moves e2e4 e7e5 g1f3 b8c6 f1b5 f8c5 e1g1 g8f6 d2d3 c6b4 a2a3 b4a6 c1g5

		if(b.inCheck==0){//pinned piece hamlelerini zaten ��karm�� olucam geriye �ah kontrol� kald�
			for(Move movex: moves){
				kingmove = 1;
				if(movex.pieceType==(5+side*6)){
					int row=7-(movex.move.charAt(2)-48);
					int col=7-(movex.move.charAt(3)-48);
					kingmove<<=row*8+col;
					if((kingmove&enemyreach)==0){
						legalmoves.add(movex);
					}
				}
				else{
					if(movex.pinned==0)
						legalmoves.add(movex);
				}
			}
		}
		else if(b.inCheck==1){//3 ihtimal var			
			int checksqu=0;//ta�� al veya �n�n� kapa veya kac
			for(int i=0;i<6;i++){
				int a=checkpiece[i+(1-side)*6];
				if(a!=0){
					checksqu=a;
				}
			}
			for(Move movex: moves){//sahi kac
				kingmove = 1;
				int row=7-(movex.move.charAt(2)-48);
				int col=7-(movex.move.charAt(3)-48);
				if(movex.pieceType==(5+side*6)&&movex.moveType!=4){
					kingmove<<=row*8+col;
					if((kingmove&enemyreach)==0){
						legalmoves.add(movex);
					}
				}
				else if(row*8+col==checksqu){//capture
					legalmoves.add(movex);
				}
				else if(isbetween(row*8+col,checksqu,kingsqu)){//araya koy
					if(movex.pinned==0)
						legalmoves.add(movex);
			
				}
			}
		}
		else{//multiple checks only king moves
			for(Move movex: moves){
				kingmove = 1;
				if(movex.pieceType==(5+side*6)){
					int row=7-(movex.move.charAt(2)-48);
					int col=7-(movex.move.charAt(3)-48);
					kingmove<<=row*8+col;
					if((kingmove&enemyreach)==0){
						legalmoves.add(movex);
					}
				}
			}
		}
		//System.out.println("is check ?:"+ b.inCheck);
		//for(Move movex: legalmoves){
		//	System.out.println("hamle: "+ movex.move + " tas: "+movex.pieceType);
		//}
		/*if(legalmoves.size()==0){
			System.out.println("empty");
			System.out.println("check: "+b.inCheck);
			for(int i=0;i<pinnedcount;i++){
				System.out.println("pinned: "+pinnedpiece[i]);
			}
			b.printBoard();
		}*/
		return legalmoves;
	}
	private void reset() {
		// TODO Auto-generated method stub
		for(int i =0;i<12;i++){
			pieceReach[i]=0;
			checkpiece[i]=0;
			
		}
		for(int i=0;i<10;i++)
			pinnedpiece[i]=0;
		pinnedcount=0;
		pinite=0;
	}
	// true if squ is between checksqu and kingsqu
	public boolean isbetween(int squ, int checksqu, int kingsqu) {
		// TODO Auto-generated method stub
		int checkrow=checksqu/8;
		int checkcol=checksqu%8;
		int kingrow=kingsqu/8;
		int kingcol=kingsqu%8;
		int squrow=squ/8;
		int squcol=squ%8;
		if(checksqu==squ)
			return true;
		else if(checkrow==kingrow&&kingrow==squrow){
			if((checkcol<=squcol&&kingcol>=squcol)||(checkcol>=squcol&&kingcol<=squcol))
				return true;
		}
		else if(checkcol==kingcol&&kingcol==squcol){
			if((checkrow<=squrow&&kingrow>squrow)||(checkrow>=squrow&&kingrow<squrow))
				return true;
		}
		else if(Math.abs(kingsqu-checksqu)%9==0&&Math.abs(squ-checksqu)%9==0){
			if((kingsqu<squ&&checksqu>=squ)||(kingsqu>squ&&checksqu<=squ))
				return true;
		}
		else if(Math.abs(kingsqu-checksqu)%7==0&&Math.abs(squ-checksqu)%7==0){
			if((kingsqu<squ&&checksqu>=squ)||(kingsqu>squ&&checksqu<=squ))
				return true;
		}
		return false;
	}
	public void pinnedCalc(Board b, int kingsqu, int side) {
		// TODO Auto-generated method stub
		long enemyqueen=1;
		long enemybishop=1;
		long enemyrook=1;
		long allpieces=b.whitePieces|b.blackPieces;
		long myall=1;
		long enmall=1;
		if(side==0){
			enemyqueen=b.bQ;
			enemybishop=b.bB;
			enemyrook=b.bR;
			myall=b.whitePieces;
			enmall=b.blackPieces;
		}
		else{
			enemyqueen=b.wQ;
			enemybishop=b.wB; 
			enemyrook=b.wR;
			myall=b.blackPieces;
			enmall=b.whitePieces;
		}
		//sa� �apraza git
		int temp =kingsqu;
		long squ=1;
		while(temp>8&&temp%8!=0){
			squ=1;
			temp-=9;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp>8&&temp%8!=0){
					squ=1;
					temp-=9;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemybishop&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//sol - �st �apraz git
		temp =kingsqu;
		squ=1;
		while(temp<55&&temp%8!=7){
			squ=1;
			temp+=9;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp<55&&temp%8!=7){
					squ=1;
					temp+=9;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemybishop&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//sol - alt �apraz git
		temp =kingsqu;
		squ=1;
		while(temp>=8&&temp%8!=7){
			squ=1;
			temp-=7;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp>=8&&temp%8!=7){
					squ=1;
					temp-=7;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemybishop&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//sq� - �st �apraz git
		temp =kingsqu;
		squ=1;
		while(temp<=55&&temp%8!=0){
			squ=1;
			temp+=7;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp<=55&&temp%8!=0){
					squ=1;
					temp+=7;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemybishop&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
							break;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//sol git
		temp =kingsqu;
		squ=1;
		while(temp%8!=7){
			squ=1;
			temp+=1;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp%8!=7){
					squ=1;
					temp+=1;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemyrook&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
							break;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//sa� git
		temp =kingsqu;
		squ=1;
		while(temp%8!=0){
			squ=1;
			temp-=1;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp%8!=7){
					squ=1;
					temp-=1;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemyrook&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
							break;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}//�st git
		temp =kingsqu;
		squ=1;
		while(temp<=55){
			squ=1;
			temp+=8;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp<=55){
					squ=1;
					temp+=8;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemyrook&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
							break;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
		//alt git
		temp =kingsqu;
		squ=1;
		while(temp>=8){
			squ=1;
			temp-=8;
			squ<<=temp;
			if((myall&squ)!=0){
				int pinpos=temp;
				while(temp>=8){
					squ=1;
					temp-=8;
					squ<<=temp;
					if((allpieces&squ)!=0){
						if((enemyrook&squ)!=0||(enemyqueen&squ)!=0){
							pinnedpiece[pinnedcount++]=pinpos;
							break;
						}
						else{
							break;
						}
					}
				}
				break;
			}
		}
	}
	//returns a list of available moves
	// a move can have additional info about the move such as 
	// target piece
	// origin piece
	// additional info can be added to the back of the string
	// suan reach degiskenlerini moveGenerator islemi esnasinda yapiyorum
	// yani oncelikle hamle uretmem lazim ki cok mantiksiz degil ama bu hatasiz calisir mi bilmem
	// illaki lazim olacak zaten ve 1 tasta iki is 
	//position startpos moves d2d4 b8c6 c1f4 d7d5 b1c3 c8g4 h2h3 g4h5 g2g4 h5g6 g1f3 e7e6 f4g5 g6c2 d1c2 c6d4 c2a4 b7b5 a4d4 f8c5 d4c5 g8f6 c5c6 e8f8 c6a8
	public ArrayList<Move> newmoveGenerator(Board b, int side , Move lastmove){
		ArrayList<Move> moves=new ArrayList<>();
		long allp= b.whitePieces|b.blackPieces;
		long pawncap=0;
		if(side==0){
			pawncap|=(b.bP<<7)&~col1;
			pawncap|=(b.bP<<9)&~col8;
			pieceReach[6]=pawncap;
		}
		if(side==1){
			pawncap|=(b.wP<<7)&~col1;
			pawncap|=(b.wP<<9)&~col8;
			pieceReach[0]=pawncap;
		}
//		System.out.println("whiteknight: "+Long.toBinaryString(b.wN));
//		System.out.println("whitebishop: "+Long.toBinaryString(b.wB));
//		System.out.println("whiterook: "+Long.toBinaryString(b.wR));
//		System.out.println("whiteking: "+Long.toBinaryString(b.wK));
//		System.out.println("whitepawn: "+Long.toBinaryString(b.wP));
//		System.out.println("blackpawn: "+Long.toBinaryString(b.bP));		
		for(int i =0;i<64;i++){
			long a=1;
			a<<=i;
			if((allp&a)==0||(b.wP&a)!=0||(b.bP&a)!=0){
			}
			else if((a&b.wQ)!=0){
				QueenMovements(moves, b, i, 4, side, 0);
			}
			else if((a&b.wN)!=0){
				KnightMovements(moves, b, i, 1, side, 0);
			}
			else if((a&b.wB)!=0){	
				BishopMovements(moves, b, i, 2, side, 0);
			}
			else if((a&b.wR)!=0){
				RookMovements(moves, b, i, 3, side, 0);
			}
			else if((a&b.wK)!=0){
				KingMovement(moves, b, i, 5, side, 0);
			}
			
			else if((a&b.bN)!=0){
				KnightMovements(moves, b, i, 7, side, 1);
			}
			else if((a&b.bB)!=0){	
				BishopMovements(moves, b, i, 8, side, 1);
			}
			else if((a&b.bR)!=0){
				RookMovements(moves, b, i, 9, side, 1);
			}
			else if((a&b.bK)!=0){
				KingMovement(moves, b, i, 11, side, 1);
			}
			else if((a&b.bQ)!=0){
				QueenMovements(moves, b, i, 10, side, 1);
			}
		}
		enPassantMoves(moves, b, side, b.lastmove);
		pawnMoves(moves, b, side);
		//after generating all reaches look whether incheck
		isCheck(b);
		return moves;
	}
	public void isCheck(Board b) {
		// TODO Auto-generated method stub
		int side=b.sideToMove;
		long king=0;
		if(side==0)
			king=b.wK;
		else
			king=b.bK;
		long reach=0;
		for(int i=0;i<6;i++){
			reach=pieceReach[6*(1-side)+i];
			if((reach&king)!=0){
				b.inCheck++;
			}
		}
	}
	public void QueenMovements(ArrayList<Move>moves,Board b,int squ,int piecetype,int side,int pieceside){
		if(side==pieceside){
			diagonalMoves(moves,b,squ,piecetype,side);		
			antidiagonalMoves(moves,b,squ,piecetype,side);	
			verticalMoves(moves, b, squ, piecetype,side);
			horizontalMoves(moves, b, squ, piecetype,side);
		}
		else{
			diagonalReach(b, squ, piecetype, pieceside);
			antidiagonalReach(b, squ, piecetype, pieceside);
			verticalReach(b, squ, piecetype, pieceside);
			horizontalReach(b, squ, piecetype, pieceside);
		}
	}
	public void BishopMovements(ArrayList<Move>moves,Board b,int squ,int piecetype,int side,int pieceside){
		if(side==pieceside){
			diagonalMoves(moves,b,squ,piecetype,side);		
			antidiagonalMoves(moves,b,squ,piecetype,side);	
		}
		else{
			diagonalReach(b, squ, piecetype, pieceside);
			antidiagonalReach(b, squ, piecetype, pieceside);
		}
	}
	public void RookMovements(ArrayList<Move>moves,Board b,int squ,int piecetype,int side,int pieceside){
		if(side==pieceside){
			verticalMoves(moves,b,squ,piecetype,side);		
			horizontalMoves(moves,b,squ,piecetype,side);	
		}
		else{
			verticalReach(b, squ, piecetype, pieceside);
			horizontalReach(b, squ, piecetype, pieceside);
		}
	}
	public void KnightMovements(ArrayList<Move>moves,Board b,int squ,int piecetype,int side,int pieceside){
		if(side==pieceside){
			knightMoves(moves, b, squ, piecetype, pieceside);
		}else{
			knightReach(b, squ, piecetype, pieceside);
		}
	}
	public void KingMovement(ArrayList<Move>moves,Board b,int squ,int piecetype,int side,int pieceside){
		if(side==pieceside){
			castlingMoves(moves, b, pieceside);
			kingMoves(moves, b, squ, piecetype, pieceside);
		}else{
			kingReach(b, squ, piecetype, pieceside);
		}
	}
	public void kingReach( Board b, int squ, int piecetype, int side) {
		long bitKing=0b0;
		long enemy=0;
		long allmoves=0;
		long ownPiece=0;
		long enemyreach=0;
		if(side==0){
			bitKing=b.wK;
			enemy=b.blackPieces;
			ownPiece=b.whitePieces;
			enemyreach=b.blackReach;
			piecetype=5;
		}
		if(side==1){
			bitKing=b.bK;
			enemy=b.whitePieces;
			ownPiece=b.blackPieces;
			piecetype=11;
			enemyreach=b.whiteReach;
		}
		int moveclockwise[][]=new int[8][2];
		// sahin yerini bulduk simdi buradan hamleleri cikaricaz knight gibi
		int row=7-(squ/8);
		int col=7-(squ%8);
		int orow=row;
		int ocol=col;
		int count=0;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col-1;
		moveclockwise[count][0]=row;
		moveclockwise[count++][1]=col-1;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col-1;
		long possiblemoves=0;//list of possible moves to be checked with pieces
		for(int i=0;i<8;i++){
			int row1=moveclockwise[i][0];
			int col1=moveclockwise[i][1];
			if(row1>=0&row1<8&col1>=0&col1<8){//possible moves
				int finalsqu=((7-row1)*8)+(7-col1);
				long thismove=1;
				thismove<<=finalsqu;
				possiblemoves|=thismove;
			}
		}
		long legalmoves=possiblemoves&~ownPiece;
		pieceReach[piecetype]=legalmoves;
	}
	public void kingMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		long bitKing=0b0;
		long enemy=0;
		long allmoves=0;
		long ownPiece=0;
		long enemyreach=0;
		if(side==0){
			bitKing=b.wK;
			enemy=b.blackPieces;
			ownPiece=b.whitePieces;
			enemyreach=b.blackReach;
			piecetype=5;
		}
		if(side==1){
			bitKing=b.bK;
			enemy=b.whitePieces;
			ownPiece=b.blackPieces;
			piecetype=11;
			enemyreach=b.whiteReach;
		}
		int moveclockwise[][]=new int[8][2];
		// sahin yerini bulduk simdi buradan hamleleri cikaricaz knight gibi
		int row=7-(squ/8);
		int col=7-(squ%8);
		int orow=row;
		int ocol=col;
		int count=0;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col+1;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col;
		moveclockwise[count][0]=row+1;
		moveclockwise[count++][1]=col-1;
		moveclockwise[count][0]=row;
		moveclockwise[count++][1]=col-1;
		moveclockwise[count][0]=row-1;
		moveclockwise[count++][1]=col-1;
		long possiblemoves=0;//list of possible moves to be checked with pieces
		for(int i=0;i<8;i++){
			int row1=moveclockwise[i][0];
			int col1=moveclockwise[i][1];
			if(row1>=0&row1<8&col1>=0&col1<8){//possible moves
				int finalsqu=((7-row1)*8)+(7-col1);
				long thismove=1;
				thismove<<=finalsqu;
				possiblemoves|=thismove;
				if((thismove&ownPiece)==0){
					addMove(moves, b, piecetype, squ, finalsqu);
				}
			}
		}
		long legalmoves=possiblemoves&~ownPiece;
		pieceReach[piecetype]|=legalmoves;
	}
	private void knightReach(Board b, int squ, int piecetype, int side) {
		int count=0;// # of available moves
		long mypiece=0;
		long reach=0;
		char piece = 'N';
		long enemyking=0;
		if(side==0){
			mypiece=b.whitePieces;
			enemyking=b.bK;
		}
		else {
			mypiece=b.blackPieces;
			enemyking=b.wK;
		}
		int row=squ/8;
		int col=squ%8;
		if(row+1<8&&col+2<8){
			reach|=(1L<<8*(row+1)+col+2);
			if((mypiece&(1L<<8*(row+1)+col+2))==0){
				if((enemyking&(1L<<8*(row+1)+col+2))!=0){
					checkpiece[piecetype]=squ;
				}
				count++;
			}
		}
		if(row+2<8&&col+1<8){reach|=(1L<<8*(row+2)+col+1);
		if((mypiece&(1L<<8*(row+2)+col+1))==0){
			count++;
			if((enemyking&(1L<<8*(row+2)+col+1))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row+1<8&&col-2>=0){reach|=(1L<<8*(row+1)+col-2);
		if((mypiece&(1L<<8*(row+1)+col-2))==0){
			count++;
			if((enemyking&(1L<<8*(row+1)+col-2))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row+2<8&&col-1>=0){reach|=(1L<<8*(row+2)+col-1);
		if((mypiece&(1L<<8*(row+2)+col-1))==0){
			count++;
			if((enemyking&(1L<<8*(row+2)+col-1))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row-1>=0&&col+2<8){reach|=(1L<<8*(row-1)+col+2);
		if((mypiece&(1L<<8*(row-1)+col+2))==0){
			count++;
			if((enemyking&(1L<<8*(row-1)+col+2))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row-2>=0&&col+1<8){reach|=(1L<<8*(row-2)+col+1);
		if((mypiece&(1L<<8*(row-2)+col+1))==0){
			count++;
			if((enemyking&(1L<<8*(row-2)+col+1))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row-1>=0&&col-2>=0){reach|=(1L<<8*(row-1)+col-2);
		if((mypiece&(1L<<8*(row-1)+col-2))==0){
			count++;
			if((enemyking&(1L<<8*(row-1)+col-2))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		if(row-2>=0&&col-1>=0){reach|=(1L<<8*(row-2)+col-1);
		if((mypiece&(1L<<8*(row-2)+col-1))==0){
			count++;
			if((enemyking&(1L<<8*(row-2)+col-1))!=0){
				checkpiece[piecetype]=squ;
			}
		}
		}
		pieceReach[piecetype]=reach;
	}
	private void knightMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		// TODO Auto-generated method stub
		int count=0;// # of available moves
		long mypiece=0;
		long reach=0;
		char piece = 'N'; 
		if(side==0){
			mypiece=b.whitePieces;
		}
		else {
			mypiece=b.blackPieces;
		}
		int row=squ/8;
		int col=squ%8;
		if(row+1<8&&col+2<8){
			reach|=(1L<<8*(row+1)+col+2);
			if((mypiece&(1L<<8*(row+1)+col+2))==0){
				count++;
			int squt=8*(row+1)+col+2;
			addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row+2<8&&col+1<8){reach|=(1L<<8*(row+2)+col+1);
		if((mypiece&(1L<<8*(row+2)+col+1))==0){
			count++;
		int squt=8*(row+2)+col+1;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row+1<8&&col-2>=0){reach|=(1L<<8*(row+1)+col-2);
		if((mypiece&(1L<<8*(row+1)+col-2))==0){
			count++;
		int squt=8*(row+1)+col-2;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row+2<8&&col-1>=0){reach|=(1L<<8*(row+2)+col-1);
		if((mypiece&(1L<<8*(row+2)+col-1))==0){
			count++;
		int squt=8*(row+2)+col-1;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row-1>=0&&col+2<8){reach|=(1L<<8*(row-1)+col+2);
		if((mypiece&(1L<<8*(row-1)+col+2))==0){
			count++;
		int squt=8*(row-1)+col+2;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row-2>=0&&col+1<8){reach|=(1L<<8*(row-2)+col+1);
		if((mypiece&(1L<<8*(row-2)+col+1))==0){
			count++;
		int squt=8*(row-2)+col+1;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row-1>=0&&col-2>=0){reach|=(1L<<8*(row-1)+col-2);
		if((mypiece&(1L<<8*(row-1)+col-2))==0){
			count++;
		int squt=8*(row-1)+col-2;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		if(row-2>=0&&col-1>=0){reach|=(1L<<8*(row-2)+col-1);
		if((mypiece&(1L<<8*(row-2)+col-1))==0){
			count++;
		int squt=8*(row-2)+col-1;
		addMove(moves,b,piecetype,squ,squt);
		}
		}
		pieceReach[piecetype]|=reach;
	}
	private void horizontalMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		// TODO Auto-generated method stub
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt%8!=0)){
			squt--;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				reach|=squbit;
				count++;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				count++;
				addMove(moves,b,piecetype,squ,squt);
			}
		}
		squt=squ;
		while((squt%8!=7)){
			squt++;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void verticalReach( Board b, int squ, int piecetype, int side) {
		long my=0;
		long enemy=0;
		long reach=0;
		long enemyking=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
			enemyking=b.bK;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
			enemyking=b.wK;
		}
		long squbit=1;  
		int count=0;
		int squt=squ;
		while((squt>=8)){
			squt-=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if(squt>=8){
						squt-=8;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		squt=squ;
		while((squt<=55)){
			squt+=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if(squt<=55){
						squt+=8;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void horizontalReach(Board b, int squ, int piecetype, int side) {
		long my=0;
		long enemy=0;
		long reach=0;
		long enemyking=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
			enemyking=b.bK;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
			enemyking=b.wK;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt%8!=0)){
			squt--;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				reach|=squbit;
				count++;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if(squt%8!=0){
						squt--;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		squt=squ;
		while((squt%8!=7)){
			squt++;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if(squt%8!=7){
						squt++;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void verticalMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		// TODO Auto-generated method stub
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>=8)){
			squt-=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				count++;
			}
		}
		squt=squ;
		while((squt<=55)){
			squt+=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void antidiagonalMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>8)&&(squt%8!=7)){
			squt-=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves, b, piecetype, squ, squt);
				break;
			}
			else{
				count++;
				reach|=squbit;
				addMove(moves, b, piecetype, squ, squt);
			}
		}
		squt=squ;
		while((squt<55)&&(squt%8!=0)){
			squt+=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves, b, piecetype, squ, squt);
				break;
			}
			else{
				count++;
				reach|=squbit;
				addMove(moves, b, piecetype, squ, squt);
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void antidiagonalReach( Board b, int squ, int piecetype, int side) {
		// TODO Auto-generated method stub
		long my=0;
		long enemy=0;
		long reach=0;
		long enemyking=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
			enemyking=b.bK;
		}if(side==1){
			enemy=b.whitePieces;
			my=b.blackPieces;
			enemyking=b.wK;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>=8)&&(squt%8!=7)){
			squt-=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if((squt>=8)&&(squt%8!=7)){
						squt-=7;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				count++;
				reach|=squbit;

			}
		}
		squt=squ;
		while((squt<=55)&&(squt%8!=0)){
			squt+=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if((squt<=55)&&(squt%8!=0)){
						squt+=7;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				count++;
				reach|=squbit;

			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void diagonalMoves(ArrayList<Move> moves, Board b, int squ, int piecetype, int side) {
		// TODO Auto-generated method stub
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
		}else{
			my=b.blackPieces;
			enemy=b.whitePieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
//position startpos moves e2e4 b8c6 d2d4 g8f6 e4e5 f6d5 g1f3 e7e6 f1b5 h7h6 e1g1 a7a6 b5c6 d7c6 c1d2

		while((squt>8)&&(squt%8!=0)){
			squt-=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				count++;
			}
		}
		squt=squ;
		while((squt<55)&&(squt%8!=7)){
			squt+=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				break;
			}
			else{
				reach|=squbit;
				addMove(moves,b,piecetype,squ,squt);
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	private void diagonalReach(Board b, int squ,int piecetype,int side) {
		// TODO Auto-generated method stub
		long my=0;
		long enemy=0;
		long reach=0;
		long enemyking=0;
		if(side==0){
			my=b.whitePieces;
			enemy=b.blackPieces;
			enemyking=b.bK;
		}else{
			my=b.blackPieces;
			enemy=b.whitePieces;
			enemyking=b.wK;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>8)&&(squt%8!=0)){
			squt-=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if((squt>=8)&&(squt%8!=0)){
						squt-=9;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		squt=squ;
		while((squt<55)&&(squt%8!=7)){
			squt+=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				reach|=squbit;
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				if((enemyking&squbit)!=0){
					checkpiece[piecetype]=squ;
					if((squt<55)&&(squt%8!=7)){
						squt+=9;
						squbit=1;
						squbit<<=squt;
						reach|=squbit;
					}
				}
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		pieceReach[piecetype]|=reach;
	}
	public void addMove(ArrayList<Move> moves, Board b, int piecetype, int squ, int finsqu) {
		// TODO Auto-generated method stub
		int kingsqu=1;
		if(piecetype<6)
			kingsqu=(int)(Math.log(b.wK)/Math.log(2)) ;
		else
			kingsqu=(int)(Math.log(b.bK)/Math.log(2));
		int frow=7-(finsqu/8);
		int fcol=7-(finsqu%8);
		int orow=7-(squ/8);
		int ocol=7-(squ%8);			
		String s="";
		int capt=isCapture(b,""+frow+""+fcol);
		s=""+orow+""+ocol+""+frow+""+fcol+""+capt;
		Move move1=new Move();
		for(int a=0;a<pinnedcount;a++){
				int pinsqu=pinnedpiece[a];
				if(pinsqu==squ){
					if(isbetween(pinsqu, finsqu, kingsqu)||isbetween(finsqu, pinsqu, kingsqu)){
						move1.pinned=0;
					}
					else{
						move1.pinned=1;
						pinite++;
					}
					break;
				}
		}
		move1.move=s;
		move1.pieceType=piecetype;
		moves.add(move1);
	}
	public ArrayList<Move> moveGenerator(Board b, int side , int enemycalc, Move lastmove){
		if(enemycalc==1)
			moveGenerator(b, 1-side, 0, lastmove);
		long pawncap=0;
		long myreach=0;
		if(side==0){
			//myreach=b.whiteReach;
			pawncap|=(b.wP<<7)&~col1;
			pawncap|=(b.wP<<9)&~col8;
		}
		else{
			//myreach=b.blackReach;
			pawncap|=(b.bP>>7)&~col8;
			pawncap|=(b.bP>>9)&~col1;
		}
		myreach|=pawncap;
		ArrayList<Move> moves=new ArrayList<>();
		if(enemycalc==1){
			pawnMoves(moves, b, side);
			enPassantMoves(moves, b, side,lastmove);
			kingMoves(moves, b, side);
		}
		myreach|=knightMoves(moves, b, side);
		//System.out.println("pawn+knight reach:"+ Long.toBinaryString(myreach));

		castlingMoves(moves, b, side);
		//horizontal(moves, b, side, "Q");
		myreach|=queenMoves(moves, b, side);
		myreach|=rookMoves(moves, b, side);
		myreach|=bishopMoves(moves, b, side);
		/*if(enemycalc==0){
			b.printBoard();
			System.out.println("reach: "+ Long.toBinaryString(queen));
		}*/
		if(side==0){
			b.whiteReach=myreach;
		}
		else{
			b.blackReach=myreach;
		}
		return moves;
	}
	// pawn promotion and enpassant not yet implemented
	public void pawnMoves(ArrayList<Move> moves,Board b, int side){
		long bitPawn=0;
		if(side==0)
			bitPawn=b.wP;
		else if(side==1)
			bitPawn=b.bP;
		//one forward move for both sides
		oneForwardPawnMoves(moves, b, bitPawn,side);
		//two forward move for both sides
		twoForwardPawnMoves(moves, b, bitPawn,side);
		//captures
		capturePawnMoves(moves, b, bitPawn, side);
	}
	public long bishopMoves(ArrayList<Move> moves,Board b, int side){
		long allmoves=0;
		allmoves|=diagonalMove(moves, b, side, "B");
		allmoves|=antiDiagMove(moves, b, side, "B");
		return allmoves;
	}
	//position startpos moves e2e4 g8f6 b1c3 e7e5 g1f3 f8b4 a2a3 b4a5 f3e5 d8e7 d2d4 a5c3 b2c3 e7e6 f2f3

	public long queenMoves(ArrayList<Move> moves,Board b, int side){
		long allmoves=0;
		allmoves|=verticalMove(moves, b, side, "Q");
		allmoves|=horizontalMove(moves, b, side, "Q");
		allmoves|=diagonalMove(moves, b, side, "Q");
		allmoves|=antiDiagMove(moves, b, side, "Q");
		return allmoves;
	}
	public long rookMoves(ArrayList<Move> moves,Board b, int side){
		long allmoves=0;
		allmoves|=verticalMove(moves, b, side, "R");
		allmoves|=horizontalMove(moves, b, side, "R");
		return allmoves;
	}
	// bu da tamam
	public long antiDiagMove(ArrayList<Move> moves,Board b, int side,String piece){
		long bitboard=0;
		long mypieces=0;
		long allmoves=0;
		long enemy=0;
		int piecetype=0;
		if(side==0){
			mypieces=b.whitePieces;
			enemy=b.blackPieces;
			if(piece.equals("Q")){
				bitboard=b.wQ;
				piecetype=4;
			}
			if(piece.equals("B")){
				bitboard=b.wB;
				piecetype=2;
			}
		}
		if(side==1){
			mypieces=b.blackPieces;
			enemy=b.whitePieces;
			if(piece.equals("Q")){
				bitboard=b.bQ;
				piecetype=10;
			}
			if(piece.equals("B")){
				bitboard=b.bB;
				piecetype=8;
			}
		}	
		long a=1;
		//1:position startpos moves e2e4 e7e5 d2d4 e5d4 d1d4 d7d5 d4d5 d8d5 e4d5 b8c6 d5c6 c8e6 c6b7 e6a2 b7a8q e8d7 a1a2 g8f6 a8f8 h8f8 a2a7 f6e4 a7c7 d7c7 b1c3 e4c3 b2c3 f7f6 f1c4 c7b7 g1f3 b7a7 e1d1 a7b7 h1e1 b7a7 e1e7 a7b6 e7g7 f8f7 g7f7 b6c6 f7h7

		for(int i=0;i<64;i++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
			a=1;
			a<<=i;
			if((bitboard&a)!=0){ // piece is spotted 
				int row= 7-(i/8);
				int col= 7-(i%8);
				long thispiece=1;
				thispiece<<=i;
				if(piece.equals("b"))
					System.out.println("i: "+i+"  mask antid: "+Long.toBinaryString(mypieces|enemy));

				long allpieces=maskAntid(mypieces|enemy,i);
				if(piece.equals("b"))
					System.out.println("i: "+i+"  mask antid: "+Long.toBinaryString(allpieces));
				//long myrow=getFullRow(mypieces, row);
				long captureup=allpieces-thispiece;
				captureup=captureup-thispiece;
				captureup=captureup^allpieces;
				allmoves|=maskAntid(captureup, i);
				captureup=captureup&(maskAntid(~mypieces,i));
				captureup=maskAntid(captureup, i);
				//System.out.println("anti mask:"+Long.toBinaryString(captureup));
				long revthis=Long.reverse(thispiece);
				long revallpieces=maskAntid(Long.reverse(mypieces|enemy), 63-i);
				long revmycol=maskAntid(Long.reverse(mypieces),63-i);
				long capturedown=maskAntid(Long.reverse(allpieces-thispiece),63-i);
				capturedown=capturedown-revthis;
				capturedown=capturedown^revallpieces;
				allmoves|=maskAntid(Long.reverse(capturedown), i);
				//System.out.println("anti mask:"+piece+Long.toBinaryString(allmoves));
				capturedown=capturedown&(~revmycol);
				capturedown=Long.reverse(capturedown);
				capturedown=maskAntid(capturedown, i);
				long capture=captureup|capturedown;/// burada bir tasin bir rowdaki hareketi
				long c=1;
				for(int l=0;l<64;l++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
					c=1;
					c<<=l;	
					if(((capture&c))!=0){ // piece is spotted
						int frow=7-(l/8);
						int fcol=7-(l%8);
						String s="";
						int capt=isCapture(b,""+frow+""+fcol);
						s=""+row+""+col+""+frow+""+fcol+""+capt;
						Move move1=new Move();
						move1.move=s;
						move1.pieceType=piecetype;
						moves.add(move1);
					}
				}
			}
		}
		return allmoves;
	}
	public long diagonalMove(ArrayList<Move>moves,Board b, int side,String piece){
		long bitboard=0;
		long mypieces=0;
		long allmoves=0;
		long enemy=0;
		int piecetype=0;
		if(side==0){
			mypieces=b.whitePieces;
			enemy=b.blackPieces;
			if(piece.equals("Q")){
				bitboard=b.wQ;
				piecetype=4;
			}
			if(piece.equals("B")){
				bitboard=b.wB;
				piecetype=2;
			}
		}
		if(side==1){
			mypieces=b.blackPieces;
			enemy=b.whitePieces;
			if(piece.equals("Q")){
				bitboard=b.bQ;
				piecetype=10;
			}
			if(piece.equals("B")){
				bitboard=b.bB;
				piecetype=8;
			}
		}	
		for(int i=0;i<64;i++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
			long squ=1;
			squ<<=i;
			if((bitboard&squ)!=0){ // piece is spotted 
				int row= 7-(i/8);
				int col= 7-(i%8);
				long thispiece=1;
				thispiece<<=i;
				long allpieces=maskDiag(mypieces|enemy,i);
				//long myrow=getFullRow(mypieces, row);
				long captureup=allpieces-thispiece;
				captureup=captureup-thispiece;
				captureup=captureup^allpieces;
				allmoves|=maskDiag(captureup, i);
				captureup=captureup&(maskDiag(~mypieces,i));
				captureup=maskDiag(captureup, i);
				//System.out.println("mask up:"+Long.toBinaryString(captureup));
				long revthis=Long.reverse(thispiece);
				long revallpieces=maskDiag(Long.reverse(mypieces|enemy), 63-i);
				long revmycol=maskDiag(Long.reverse(mypieces),63-i);
				long capturedown=maskDiag(Long.reverse(allpieces-thispiece),63-i);
				capturedown=capturedown-revthis;
				capturedown=capturedown^revallpieces;
				allmoves|=maskDiag(Long.reverse(capturedown), i);
				capturedown=capturedown&(~revmycol);
				capturedown=Long.reverse(capturedown);
				capturedown=maskDiag(capturedown, i);
				long capture=captureup|capturedown;/// burada bir tasin bir rowdaki hareketi
				for(int l=0;l<64;l++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
					long squ1=1;
					squ1<<=l;
					long squ2=1;
					if(l==0&&(i%9==0)){
						int count=0;
						for(int y = 1;y<7-row;y++){
							squ2=1;
							squ2<<=(9*y);
							if(((mypieces|enemy)&squ2)!=0){
								count++;
							}
						}
						if(count==0&&(mypieces&1)==0)
						{
							String s="";
							int capt=isCapture(b,""+7+""+7);
							s=""+row+""+col+""+7+""+7+""+capt;
							Move move1=new Move();
							move1.move=s;
							move1.pieceType=piecetype;
							moves.add(move1);	
							allmoves|=1;
						}
					}
					if(((capture&squ1)!=0)){ // piece is spotted
						int frow=7-(l/8);
						int fcol=7-(l%8);
						String s="";
						int capt=isCapture(b,""+frow+""+fcol);
						s=""+row+""+col+""+frow+""+fcol+""+capt;
						Move move1=new Move();
						move1.move=s;
						move1.pieceType=piecetype;
						moves.add(move1);
						//System.out.println("QueenDiag: "+ s);
					}
				}
			}
		}
		return allmoves;
	}

	//dogru calisiyor gibi ama emin degilim bundan da
	public long verticalMove(ArrayList<Move>moves,Board b, int side,String piece){
		long bitboard=0;
		long mypieces=0;
		long allmoves=0;
		long enemy=0;
		int piecetype=0;
		if(side==0){
			mypieces=b.whitePieces;
			enemy=b.blackPieces;
			if(piece.equals("Q")){
				bitboard=b.wQ;
				piecetype=4;
			}
			if(piece.equals("R")){
				bitboard=b.wR;
				piecetype=3;
			}
		}
		if(side==1){
			mypieces=b.blackPieces;
			enemy=b.whitePieces;
			if(piece.equals("Q")){
				bitboard=b.bQ;
				piecetype=10;
			}
			if(piece.equals("R")){
				bitboard=b.bR;
				piecetype=9;
			}
		}
		long nokta=1;	
		for(int i=0;i<64;i++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
			nokta=1;
			nokta<<=i;
			if((bitboard&nokta)!=0){ // piece is spotted 
				int row= 7-(i/8);
				int col= 7-(i%8);
				long thispiece=1;
				thispiece<<=i;
				long allpieces=maskCol(mypieces|enemy,col);
				//long myrow=getFullRow(mypieces, row);
				long captureup=allpieces-thispiece;
				captureup=captureup-thispiece;
				captureup=captureup^allpieces;
				allmoves|=maskCol(captureup, col);
				captureup=captureup&(maskCol(~mypieces,col));
				captureup=maskCol(captureup, col);
				long revthis=Long.reverse(thispiece);
				long revallpieces=maskCol(Long.reverse(mypieces|enemy), 7-col);
				long revmycol=maskCol(Long.reverse(mypieces),7-col);
				long capturedown=maskCol(Long.reverse(allpieces-thispiece),7-col);
				capturedown=capturedown-revthis;
				capturedown=capturedown^revallpieces;
				allmoves|=maskCol(Long.reverse(capturedown), col);
				capturedown=capturedown&(~revmycol);
				capturedown=Long.reverse(capturedown);
				capturedown=maskCol(capturedown, col);
				long capture=captureup|capturedown;/// burada bir tasin bir rowdaki hareketi
				long noktam=1;
				for(int l=0;l<64;l++){// bu yontemle ilk bitte tas oldugunda da dogru calisiyor
					noktam=1;
					noktam<<=l;
					if(((capture&noktam))!=0){ // piece is spotted
						int frow=7-(l/8);
						int fcol=7-(l%8);
						String s="";
						int capt=isCapture(b,""+frow+""+fcol);
						s=""+row+""+col+""+frow+""+fcol+""+capt;
						Move move1=new Move();
						move1.move=s;
						move1.pieceType=piecetype;
						moves.add(move1);
						//System.out.println("QueenCol: "+ s);
					}
				}
			}
		}
		return allmoves;
	}
	public long horizontalMove(ArrayList<Move>moves,Board b, int side,String piece){
		long bitboard = 0;
		long mypieces=0;
		long enemy=0;
		long allmoves=0;
		int piecetype=0;
		if (piece.charAt(0)=='Q') {
			if(side==0){
				bitboard=b.wQ;
				mypieces=b.whitePieces;
				enemy=b.blackPieces;
				piecetype=4;
			}
			else{
				bitboard=b.bQ;
				mypieces=b.blackPieces;
				enemy=b.whitePieces;
				piecetype=10;
			}
		}
		else if (piece.equals("R")) {
			if(side==0){
				bitboard=b.wR;
				mypieces=b.whitePieces;
				enemy=b.blackPieces;
				piecetype=3;
			}
			else{
				bitboard=b.bR;
				mypieces=b.blackPieces;
				enemy=b.whitePieces;
				piecetype=9;
			}
		}
		long nokta=1;
		for(int i=0;i<64;i++){
			nokta=1;
			nokta<<=i;
			if((bitboard&nokta)!=0){ // piece is spotted 
				int row= 7-(i/8);// bring all the pieces on that row
				long thispiece=1;
				thispiece<<=i;
				long allpieces=mypieces|enemy;
				//long myrow=getFullRow(mypieces, row);
				long captureleft=allpieces-thispiece;
				captureleft=captureleft-thispiece;
				captureleft=captureleft^allpieces;
				allmoves|=maskRow(captureleft, row);
				captureleft=captureleft&(~mypieces);
				captureleft=maskRow(captureleft, row);
				long revthis=Long.reverse(thispiece);
				long revallpieces=Long.reverse(mypieces|enemy);
				long revmyrow=Long.reverse(mypieces);
				long captureright=Long.reverse(allpieces-thispiece);
				captureright=captureright-revthis;
				captureright=captureright^revallpieces;
				allmoves|=maskRow(Long.reverse(captureright), row);
				captureright=captureright&(~revmyrow);
				captureright=Long.reverse(captureright);
				captureright=maskRow(captureright, row);
				long capture=captureleft|captureright;/// burada bir tasin bir rowdaki hareketi
				capture=getFullRow(capture, row);
				for(int a=0;a<8;a++){
					if((capture>>a)%2==1){
						int orow=7-(i/8);
						int ocol=7-(i%8);
						int col=7-a;
						String s="";
						int capt=isCapture(b,""+row+""+col);
						s=""+orow+""+ocol+""+row+""+col+""+capt;
						Move move1=new Move();
						move1.move=s;
						move1.pieceType=piecetype;
						moves.add(move1);
						//System.out.println("Row Moves: "+ s);
					}
				}
			}
		}
		return allmoves;
	}
	public long maskAntid(long bitboard,int loc){
		int pos=loc;
		long mask=0;
		while(pos<64&&(pos%8!=0)){
			pos+=7;
		}
		if(pos>=64)
			pos-=7;
		while(pos>0){
			long add=1;
			add<<=pos;
			mask|=add;
			if(pos%8==7){
				pos=0;break;
			}
			pos-=7;
		}
		return bitboard&mask;
	}

	public long maskDiag(long bitboard,int loc){//this finds the corresponding diagonal of a position
		int pos=loc;
		long mask=0;
		while(pos<64&&(pos%8!=7)){
			pos+=9;

		}
		//position startpos moves e2e4 g8f6 b1c3 e7e5 g1f3 f8b4 a2a3 b4a5 f3e5 d8e7 d2d4 a5c3 b2c3 e7e6 f2f3

		if(pos>=64)
			pos-=9;
		while(pos>0){
			long add=1;
			add<<=pos;
			mask|=add;
			if(pos%8==0){
				pos=0;
			}
			pos-=9;
		}
		return bitboard&mask;
	}
	public long maskCol(long bitboard,int col){// this extracts the values of the given col
		long one=1;
		long colm=0;
		for(int i=0;i<8;i++){
			colm|=(one<<((8*i)+(7-col)));
		}
		colm&=bitboard;
		//System.out.println("colm :"+Long.toBinaryString(colm));
		return colm;
	}
	public long maskRow(long bitboard,int row){// this extracts the values of the given row
		long a=0b11111111;
		a<<=((7-row)*8);
		a&=bitboard;
		return a;
	}
	public long reverseBit(long bitboard){
		long a=bitboard;
		int size=0;
		long temp=a;
		while(temp!=0){
			temp>>=1;
		size++;
		}
		int b=0;
		while (a!=0){
			b<<=1;
			b|=( a &1);
			a>>=1;
		}
		//for(int i=size;i<8;i++)
		//b<<=1;
		return b; 
	}
	//this method will return the corresponding row from a bitboard
	public long getFullRow(long bitboard, int row){
		long a = bitboard>>=((7-row)*8);
			long b=0b11111111L;
			if(row==7)
				return a;
			else{
				a=a&b;
				return a;
			}
	}
	//king moves i am only checking the moves if they are possible
	//i mean at this level i assume all moves are legal 
	// without checking if the moves done end up in check which is illegal
	public long kingMoves(ArrayList<Move>moves,Board b, int side){
		long bitKing=0b0;
		long enemy=0;
		long allmoves=0;
		long ownPiece=0;
		long enemyreach=0;
		int piecetype=0;
		if(side==0){
			bitKing=b.wK;
			enemy=b.blackPieces;
			ownPiece=b.whitePieces;
			enemyreach=b.blackReach;
			piecetype=5;
		}
		if(side==1){
			bitKing=b.bK;
			enemy=b.whitePieces;
			ownPiece=b.blackPieces;
			piecetype=11;
			enemyreach=b.whiteReach;
		}
		int moveclockwise[][]=new int[8][2];
		int count=0;
		int orow=0;
		int ocol=0;
		long a=1;
		for(int i=0;i<64;i++){
			a=1;
			a<<=i;
			if((bitKing&a)!=0){
				// sahin yerini bulduk simdi buradan hamleleri cikaricaz knight gibi
				int row=7-(i/8);
				int col=7-(i%8);
				orow=row;
				ocol=col;
				moveclockwise[count][0]=row-1;
				moveclockwise[count++][1]=col;
				moveclockwise[count][0]=row-1;
				moveclockwise[count++][1]=col+1;
				moveclockwise[count][0]=row;
				moveclockwise[count++][1]=col+1;
				moveclockwise[count][0]=row+1;
				moveclockwise[count++][1]=col+1;
				moveclockwise[count][0]=row+1;
				moveclockwise[count++][1]=col;
				moveclockwise[count][0]=row+1;
				moveclockwise[count++][1]=col-1;
				moveclockwise[count][0]=row;
				moveclockwise[count++][1]=col-1;
				moveclockwise[count][0]=row-1;
				moveclockwise[count++][1]=col-1;
			}
		}
		long possiblemoves=0;//list of possible moves to be checked with pieces
		for(int i=0;i<8;i++){
			int row=moveclockwise[i][0];
			int col=moveclockwise[i][1];
			if(row>=0&row<8&col>=0&col<8){//possible moves
				long thismove=1;
				possiblemoves|=(thismove<<((7-row)*8)+(7-col));
			}
		}
		long legalmoves=possiblemoves&~ownPiece;
		legalmoves&=~enemyreach;
		//System.out.println(side+" enemy reach " +Long.toBinaryString(enemyreach));
		for(int i=0;i<64;i++){
			String sa=Long.toBinaryString((legalmoves>>i));
			if(sa.charAt(sa.length()-1)=='1')
			{
				int row=7-(i/8);
				int col=7-(i%8);
				String s="";
				int capt=isCapture(b,""+row+""+col);
				s=""+orow+""+ocol+""+row+""+col+""+capt;
				Move move1=new Move();
				move1.move=s;
				move1.pieceType=piecetype;
				moves.add(move1);
			}
		}
		return possiblemoves;
	}
	// check legality without losing the board information about castling rights
	public int checkLegality(Move x,Board b, int side){
		int tempwkc=b.whitekscastle;
		int tempwqc=b.whiteqscastle;
		int tempbkc=b.blackkscastle;
		int tempbqc=b.blackqscastle;
		b.movePiece(x.move);
		int ret=0;
		if(isCheck(b, side)==0){
			ret= 0;
		}
		else {
			ret= 1;
		}
		b.unmovePiece(x.move);
		b.whitekscastle=tempwkc;
		b.whiteqscastle=tempwqc;
		b.blackkscastle=tempbkc;
		b.blackqscastle=tempbqc;
		return ret;
	}
	//checks if the current board position is check
	// which makes it illegal
	// one means is check
	public int isCheck(Board b,int side){
		moveGenerator(b, 1-side, 0,null);
		long enemy=0;
		if(side==0){
			enemy=b.blackReach;
			if((b.wK&enemy)!=0)
				return 1;
			else
				return 0;
		}
		else{
			enemy=b.whiteReach;
			if((b.bK&enemy)!=0)
				return 1;
			else
				return 0;
		}
	}

	public long castlingMoves(ArrayList<Move>moves,Board b, int side){
		long all=b.blackPieces|b.whitePieces;
		String s="";					
		if(side==0){
			int kingpos=1<<3;
			long h1=0b1;
			long enemyR=1;
			for(int i=0;i<6;i++){
				enemyR|=pieceReach[(1-side)*6+i];
			}
			long myrook=b.wR;
			if((enemyR&kingpos)==0){// is not in check
				if((b.rights[3]==1)&&((myrook&h1)!=0)&&((enemyR&(h1<<2))==0)&&((all&(h1<<2))==0)&&((all&(h1<<1))==0)){
					//kingside for white
					//castling hamlesi iki tasi ayni anda hareket ettirdigi icin 
					//iki hamleyi de move icine atmiyoruz
					s="";					
					s=""+7+""+4+""+7+""+6+""+12;
					Move move1=new Move();
					move1.move=s;
					move1.pieceType=5;
					move1.moveType=4;
					moves.add(move1);
				}
				if((b.rights[2]==1)&&((myrook&(h1<<7))!=0)&&((enemyR&(h1<<3))==0)&&((all&(h1<<6))==0)&&((all&(h1<<5))==0)&&((all&(h1<<4))==0)){
					//queenside for white
					//castling hamlesi iki tasi ayni anda hareket ettirdigi icin 
					//iki hamleyi de move icine atmiyoruz
					s="";					
					s=""+7+""+4+""+7+""+2+""+12;
					Move move1=new Move();
					move1.move=s;
					move1.pieceType=5;
					move1.moveType=4;
					moves.add(move1);
				}
			}
		}
		if(side==1){
			int kingpos=1<<59;
			long h8=(0b1L<<56);
			long enemyR=1;
			for(int i=0;i<6;i++){
				enemyR|=pieceReach[(1-side)*6+i];
			}
			long myrook=b.bR;
			if((enemyR&kingpos)==0){// is not in check
				if((b.rights[5]==1)&&((myrook&h8)!=0)&&((enemyR&(h8<<2))==0)&&((all&(h8<<2))==0)&&((all&(h8<<1))==0)){
					//kingside for black
					//castling hamlesi iki tasi ayni anda hareket ettirdigi icin 
					//iki hamleyi de move icine atmiyoruz
					s="";					
					s=""+0+""+4+""+0+""+6+""+12;
					Move move1=new Move();
					move1.move=s;
					move1.pieceType=11;
					move1.moveType=4;
					moves.add(move1);
				}
				//position startpos moves e2e3 e7e6 d2d4 d8g5 g1f3 g5a5 b1c3 f8b4 c1d2 b8c6 a2a3 b4c3 d2c3 a5d5 f1e2 g8f6 e1g1 f6e4 c3e1

				if((b.rights[4]==1)&&((myrook&(h8<<7))!=0)&&((enemyR&(h8<<3))==0)&&((all&(h8<<6))==0)&&((all&(h8<<5))==0)&&((all&(h8<<4))==0)){
					//queenside for black
					//castling hamlesi iki tasi ayni anda hareket ettirdigi icin 
					//iki hamleyi de move icine atmiyoruz
					s="";					
					s=""+0+""+4+""+0+""+2+""+12;
					Move move1=new Move();
					move1.move=s;
					move1.pieceType=11;
					move1.moveType=4;
					moves.add(move1);
				}
			}
		}

		return 0;
	}

	// atin hareketleri garip oldugu icin direction gibi iki boyutlu dusunmek daha iyi
	public long knightMoves(ArrayList<Move>moves,Board b, int side){
		long bitKnight=0;
		long enemy=0;
		long allmoves=0;
		long ownPiece=0;
		int piecetype=0;
		if(side==0){
			bitKnight=b.wN;
			enemy=b.blackPieces;
			ownPiece=b.whitePieces|b.bK;
			piecetype=1;
		}
		if(side==1){
			bitKnight=b.bN;
			enemy=b.whitePieces;
			ownPiece=b.blackPieces|b.wK;
			piecetype=7;
		}
		for(int i=0;i<64;i++){
			int moveclockwise[][]=new int[8][2];
			int count=0;
			long squ=1;
			squ<<=i;
			if((bitKnight&squ)!=0){
				int row=7-(i/8);
				int col=7-(i%8);// burada tasin nerde oldugunu aldik sonra ona gore 8 tane hamle verecegiz
				// clockwise hesaplayalim
				moveclockwise[count][0]=row-2;
				moveclockwise[count++][1]=col+1;
				moveclockwise[count][0]=row-1;
				moveclockwise[count++][1]=col+2;
				moveclockwise[count][0]=row+1;
				moveclockwise[count++][1]=col+2;
				moveclockwise[count][0]=row+2;
				moveclockwise[count++][1]=col+1;
				moveclockwise[count][0]=row+2;
				moveclockwise[count++][1]=col-1;
				moveclockwise[count][0]=row+1;
				moveclockwise[count++][1]=col-2;
				moveclockwise[count][0]=row-1;
				moveclockwise[count++][1]=col-2;
				moveclockwise[count][0]=row-2;
				moveclockwise[count++][1]=col-1;
				for(int a=0;a<8;a++){
					long hamle=0b1;
					int newrow=moveclockwise[a][0];
					int newcol=moveclockwise[a][1];
					if(newrow>=0&&newrow<=7&&newcol>=0&&newcol<=7){//boardun icine dusuyor
						hamle<<=((7-newrow)*8)+(7-newcol);
						allmoves|=hamle;
						String h=Long.toBinaryString((ownPiece>>(((7-newrow)*8)+(7-newcol))));
						if(h.charAt(h.length()-1)!='1'){
							String s="";					
							int capt=isCapture(b,""+newrow+""+newcol);
							s=""+row+""+col+""+newrow+""+newcol+""+capt;
							Move move1=new Move();
							move1.move=s;
							move1.pieceType=piecetype;
							moves.add(move1);
							//System.out.println("Knight: "+ s);
						} 
					}
				}
			}
		}
		return allmoves;
	}
	// henuz tam test etmedim calisiyor gibi 
	// siyahlar icin de yazdim ama test ugrasmak istemiyorum yav
	// sadece hamle uretiyorum nedense baska seyler de lazim gibi geliyor
	public void capturePawnMoves(ArrayList<Move>moves,Board b, long bitPawn,int side){
		if(side==0){
			long captureright=(bitPawn<<7)&(~col1&b.blackPieces);
			long captureleft=(bitPawn<<9)&(~col8&b.blackPieces);
			int piecetype=0;
			//System.out.println("captureright: "+Long.toBinaryString(captureright));
			//System.out.println("captureleft: "+Long.toBinaryString(captureleft));
			long whitecap=captureleft|captureright;
			for(int i=16;i<64;i++){
				String s="";
				long squ=1;
				squ<<=i;
				if((captureright&squ)!=0){
					int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
					if(row==7){
						addMove1(moves, b, 0, i-7, i, 2);
					}
					else
						addMove(moves, b, 0, i-7, i);
				}
				if((captureleft&squ)!=0){
					int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
					if(row==7){
						addMove1(moves, b, 0, i-9, i, 2);
					}
					else
						addMove(moves, b, 0, i-9, i);	
				}
			}
			pieceReach[0]|=whitecap;
		}
		if(side==1){
			long captureright=(bitPawn>>9)&(~col1&b.whitePieces);
			long captureleft=(bitPawn>>7)&(~col8&b.whitePieces);
			//System.out.println("captureright: "+Long.toBinaryString(captureright));
			//System.out.println("captureleft: "+Long.toBinaryString(captureleft));
			long blackcapture=captureleft|captureright;
			for(int i=0;i<48;i++){
				String s="";
				long squ=1;
				squ<<=i;
				if((captureright&squ)!=0){
					int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
					if(row==0){
						addMove1(moves, b, 6, i+9, i, 2);
					}
					else
						addMove(moves, b, 6, i+9, i);
				}
				if((captureleft&squ)!=0){
					int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
					if(row==0){
						addMove1(moves, b, 6, i+7, i, 2);
					}
					else
						addMove(moves, b, 6, i+7, i);		
				}
			}
			pieceReach[6]|=blackcapture;
		}
	}
	//galib bu da oldu emin deggilim
	public void enPassantMoves(ArrayList<Move>moves,Board b,int side, Move lastmove){
		if(side==0){//white enpassant check where was the last move made and look at its neighbors
			if(lastmove!=null&&lastmove.moveType==1){
				String movestr=lastmove.move;
				int rowofpawn=movestr.charAt(2)-48; 
				int colofpawn=movestr.charAt(3)-48; 
				int a=(((7-rowofpawn)*8)+(7-colofpawn));
				long pos=0b1;
				pos<<=a;
				if((b.wP&(pos<<1))!=0 && (colofpawn!=0)){//soldan enpas var
					String s=""+rowofpawn+""+(colofpawn-1)+""+(rowofpawn-1)+""+colofpawn+""+6;
					//System.out.println("pawn captures: "+s);
					addMove1(moves, b, 0, a+1, a+8, 3);
				}
				if((b.wP&(pos>>1))!=0 && (colofpawn!=7)){//sagdan enpas var
					String s=""+rowofpawn+""+(colofpawn+1)+""+(rowofpawn-1)+""+colofpawn+""+6;
					addMove1(moves, b, 0, a-1, a+8, 3);
				}
			}
		}
		if(side==1){//black enpassant check where was the last move made and look at its neighbors
			if(lastmove!=null&&lastmove.moveType==1){
				String movestr=lastmove.move;
				int rowofpawn=movestr.charAt(2)-48; 
				int colofpawn=movestr.charAt(3)-48; 
				int a=((7-rowofpawn)*8+(7-colofpawn));
				long pos=0b1;
				pos<<=a;
				if((b.bP&(pos<<1))!=0 && (colofpawn!=0)){//soldan enpas var
					String s=""+rowofpawn+""+(colofpawn-1)+""+(rowofpawn+1)+""+colofpawn+""+0;
					//System.out.println("pawn captures: "+s);
					addMove1(moves, b, 6, a+1, a-8, 3);
				}
				if((b.bP&(pos>>1))!=0 && (colofpawn!=7)){//sagdan enpas var
					String s=""+rowofpawn+""+(colofpawn+1)+""+(rowofpawn+1)+""+colofpawn+""+0;
					//System.out.println("pawn captures: "+s);
					addMove1(moves, b, 6, a-1, a-8, 3);
				}
			}
		}
	}
	public void twoForwardPawnMoves(ArrayList<Move>moves,Board b, long bitPawn,int side){
		if(side==0){
			long row2pawns=bitPawn&row2;
			long oneforward=row2pawns<<8;
			long twoforward=row2pawns<<16;
			long row3pieces=row3&(b.whitePieces|b.blackPieces);
			long row4pieces=row4&(b.whitePieces|b.blackPieces);
			long oneflegal=oneforward&~(row3pieces);
			long twoflegal=twoforward&~(row4pieces);
			long finalaval=(oneflegal>>8)&(twoflegal>>16);
			for(int i=8;i<16;i++)
			{
				String s="";
				if((finalaval>>i)%2==1){
					int column= i%8;
					addMove1(moves, b, 0, 8+column, 24+column,1);
				}
			}
		}
		if(side==1){
			long row2pawns=bitPawn&row7;
			long oneforward=row2pawns>>8;
				long twoforward=row2pawns>>16;
				long row6pieces=row6&(b.whitePieces|b.blackPieces);
				long row5pieces=row5&(b.whitePieces|b.blackPieces);
				long oneflegal=oneforward&~(row6pieces);
				long twoflegal=twoforward&~(row5pieces);
				long finalaval=(oneflegal<<8)&(twoflegal<<16);
				//System.out.println("legalmoves: "+Long.toBinaryString(twoflegal));
				//System.out.println("twof: "+Long.toBinaryString(finalaval));
				for(int i=48;i<56;i++)
				{
					if((finalaval>>i)%2==1){
						int column= i%8;
						addMove1(moves, b, 6, 48+column, 32+column,1);
					}
				}
		}
	}
	public void oneForwardPawnMoves(ArrayList<Move>moves,Board b, long bitPawn,int side){
		if(side==0){
			long oneforward=bitPawn<<8;
			long oneflegal=oneforward&~(b.whitePieces|b.blackPieces);
			for(int i=16;i<64;i++)
			{
				String s="";
				long square=1;
				square<<=i;
				if((oneflegal&square)!=0){
					int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
					int column= i%8;
					int frow=7-row;
					int fcol=7-column;
					int orow=frow+1;
					//int capt=isCapture(b,""+frow+""+fcol);
					s=""+orow+""+fcol+""+frow+""+fcol+""+12;
					Move move1=new Move();
					move1.move=s;
					move1.pieceType=0;
					move1.moveType=0;
					if(frow==0){
						addMove1(moves, b, 0, i-8, i, 2);
					}
					else
						addMove(moves, b, 0, i-8, i);
				}
			}
		}
		if(side==1){
			long oneforward=bitPawn>>8;
						long oneflegal=oneforward&~(b.whitePieces|b.blackPieces);
						//System.out.println("onf: "+Long.toBinaryString(oneflegal));
						for(int i=0;i<48;i++)
						{
							String s="";
							if(((oneflegal)>>i)%2==1){
								int row=i/8;// bu degerler 00 olarak h1i kabul ediyor buna dikkat 
								int column= i%8;
								int frow=7-row;
								int fcol=7-column;
								int orow=frow-1;
								//int capt=isCapture(b,""+frow+""+fcol);
								s=""+orow+""+fcol+""+frow+""+fcol+""+12;
								Move move1=new Move();
								move1.move=s;
								move1.pieceType=6;
								if(frow==7){
									addMove1(moves, b, 6, i+8, i, 2);

								}
								else
									addMove(moves, b, 6, i+8, i);
							}
						}
		}
	}

	private void addMove1(ArrayList<Move> moves, Board b, int piecetype, int squ, int finalsqu, int movetype) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				int kingsqu=1;
				if(piecetype<6)
					kingsqu=(int) (Math.log(b.wK)/Math.log(2)) ;
				else
					kingsqu=(int)(Math.log(b.bK)/Math.log(2));
				int frow=7-(finalsqu/8);
				int fcol=7-(finalsqu%8);
				int orow=7-(squ/8);
				int ocol=7-(squ%8);			
				String s="";
				int capt=isCapture(b,""+frow+""+fcol);
				s=""+orow+""+ocol+""+frow+""+fcol+""+capt;
				Move move1=new Move();
				for(int a=0;a<pinnedcount;a++){
						int pinsqu=pinnedpiece[a];
						if(pinsqu==squ){
							if(isbetween(pinsqu, finalsqu, kingsqu)||isbetween(finalsqu, pinsqu, kingsqu)){
								move1.pinned=0;
							}
							else{
								move1.pinned=1;
								pinite++;
							}
							break;
						}
				}
				if(piecetype==0&&movetype==2){
					for(int a=0;a<4;a++){
						Move move2=new Move();
						move2.move=s;
						move2.pinned=move1.pinned;
						move2.pieceType=0;
						move2.moveType=2;
						move2.promotionType=a+1;
						moves.add(move2);
					}
				}
				if(piecetype==6&&movetype==2){
					for(int a=0;a<4;a++){
						Move move2=new Move();
						move2.move=s;
						move2.pinned=move1.pinned;
						move2.pieceType=0;
						move2.moveType=2;
						move2.promotionType=a+7;
						moves.add(move2);
					}
				}
	}
	//checks if the given position is empty or not 
	// if empty return 12 
	public int isCapture(Board b, String move){
		String cp= move.substring(0,2);
		int cr=Integer.parseInt(cp.substring(0,1));
		int cc=Integer.parseInt(cp.substring(1));
		//System.out.println("cr: "+cr+" cc: "+cc+" fr: "+fr+" fc: "+ fc);
		long root=0b1;
		long newPos=((7-cr)*8)+(7-cc);
		long newP=root<<newPos;
		//		System.out.println("root: "+root+"oldP: "+ oldP +"  "+ slide+"  longsuz: "+Math.pow(2, slide));
		//	System.out.println("newbit: "+Long.toBinaryString(newP));
		//		System.out.println("newP: "+ newP+"  "+ newPos+"  longsuz: "+Math.pow(2, newPos));
		if((b.wB&newP)!=0)
		{
			return 2;
		}
		if((b.bB&newP)!=0)
		{
			return 8;
		}
		if((b.wP&newP)!=0)
		{
			return 0;
		}
		if((b.bP&newP)!=0)
		{
			return 6;
		}
		if((b.wN&newP)!=0)
		{
			return 1;
		}
		if((b.bN&newP)!=0)	
		{
			return 7;
		}
		if((b.wR&newP)!=0)
		{
			return 3;
		}
		if((b.bR&newP)!=0)
		{
			return 9;
		}
		if((b.wQ&newP)!=0)
		{
			return 4;
		}
		if((b.bQ&newP)!=0)
		{
			return 10;
		}
		if((b.wK&newP)!=0)
		{
			return 5;
		}
		if((b.bK&newP)!=0)
		{
			return 11;
		}
		return 12;
	}
	int moveTypeDetector(Board b,String moves, int pieceType){
		int orow=moves.charAt(0)-48;
		int ocol=moves.charAt(1)-48;
		int frow=moves.charAt(2)-48;
		int fcol=moves.charAt(3)-48;
		if(pieceType==0||pieceType==6)// pawn
		{
			char row=moves.charAt(3);
			int osqu=8*orow+ocol;
			int fsqu=8*frow+fcol;
			if(frow==7||frow==0){// pawn promotion
				return 2;
			}
			else if((osqu+7)==fsqu||(osqu-7)==fsqu||(osqu+9)==fsqu||(osqu-9)==fsqu)//pawn capture
			{
				int square=8*(7-frow)+(7-fcol);
				if(b.isOccupied(square)==0)
					return 3;// enpassant capture
				else
					return 0;//regular pawn capture
			}
			return 0;
		}
		else if(pieceType==5||pieceType==11){//king
			if(ocol>(fcol+1)||ocol<(fcol-1)) //castling
				return 4;
			return 0;
		}
		else// if the piece is neither a pawn nor a king then the move is a regular one
			return 0;
	}
}
