import java.util.ArrayList;

import javax.lang.model.element.NestingKind;

// aim of this class is to implement minimax first 
// by implementing a basic search routine 
// that expands all 
public class Search {
	public int sidetoMove;
	public int maxDepth=0;
	public String bestmove="";
	//public String []bestpath;
	public Evaluate eval;
	public MoveGenerator movegen=new MoveGenerator();
	public Search(Board b,int dep){
		eval=new Evaluate(b);
		maxDepth=dep;
		//bestpath=new String [maxDepth+1];
	}
	public Search(){
		
	}
	// this is shorter minimax
	//position startpos moves e2e4 d7d5 e4d5 d8d5 b1c3 d5c6 d2d4 g8f6 f1b5
	public Move minimax2(Board b,int side, int depth,double alpha,double beta, Move lastmove ,int qui){
		ArrayList<Move>list=movegen.moveGenerator(b, side,1, lastmove);
		Move bestmove=new Move();
		if(side==0)
			bestmove.moveScore=-99999;
		else
			bestmove.moveScore= 99999;
		ArrayList<Move> legallist=new ArrayList<>();
		for (Move hamle: list){
			b.makeMove(hamle);
			int check= movegen.isCheck(b,side);
			if(check==0)
			{
				legallist.add(hamle);
			}
			b.unmakeMove(hamle);
		}
		// position startpos moves e2e3 e7e5 g1e2 b8c6 f2f4 d7d5 e2c3 d5d4 c3e4 d4e3 f1b5 c6b4 

		// iterative deepening routine beginning
		if(maxDepth==depth&& depth!=1){
			for(Move hamle: legallist){
				hamle.side=side;
				//System.out.println("move: "+hamle.move+" depth: "+depth);
				b.makeMove(hamle);
				hamle.moveScore=minimax2(b, 1-side, 1,alpha,beta,hamle,0).moveScore;
				b.unmakeMove(hamle);
			}
			legallist.sort(null);// ascending or descending

		} 
		if(maxDepth!=depth&&maxDepth!=1){
			for(Move hamle: legallist){
				eval=new Evaluate(b);
				hamle.side=side;
				//System.out.println("move: "+hamle.move);
				b.makeMove(hamle);
				hamle.moveScore=eval.materialcount()+eval.positionalValues();
				b.unmakeMove(hamle);
			}
			legallist.sort(null);// ascending or descend
		} 
		if(legallist.size()==0){
			return bestmove;
		}
//position startpos moves d2d4 b8c6 c1f4 d7d5 b1c3 c8g4 h2h3 g4h5 g2g4 h5g6 g1f3 e7e6 f4g5 g6c2 d1c2 c6d4 c2a4 b7b5 a4d4 f8c5 d4c5 d8d7 c3b5 h7h6 b5c7 d7c7 c5c7 h6g5 c7c6 e8e7 c6a8 e7d6 f3g5 f7f6 g5f7 d6c5 a8a7 c5c4 f7h8 f6f5 a7g7 f5g4 h3g4 d5d4 g7g8 c4d5 f1g2 d5e5 h8g6 e5f6 h1h7 e6e5 g8f7
		for(Move hamle: legallist){
				long impPieces=0;
				b.makeMove(hamle);
				if(depth==1){
				eval=new Evaluate(b);
				hamle.moveScore=eval.materialcount()+eval.positionalValues();
				if(side==0){
					impPieces=b.wN|b.wB|b.wQ|b.wK|b.wR;
					if((eval.bReach&impPieces)!=0)
						if(hamle.moveScore>alpha&&qui==1){
						   hamle.moveScore=quiescence(b,alpha,beta,1-side,2);// black to move
						}
				}
				if(side==1){
					impPieces=b.bN|b.bB|b.bQ|b.bK|b.bR;
					if((eval.wReach&impPieces)!=0&&qui==1)
						if(hamle.moveScore<beta){
							   hamle.moveScore=quiescence(b,alpha,beta,1-side,2);// white to move
						}
				}
				//System.out.println("depth: "+depth+"  score: "+ hamle.moveScore+"  "+ hamle.move +" alpha "+alpha +" beta "+beta);
			}else if(depth>1){
				//System.out.println("depth: "+depth+"  alpha: "+ alpha +"  beta: "+ beta);
				hamle.child=minimax2(b, 1-side, depth-1,alpha,beta,hamle,1);
				hamle.moveScore=hamle.child.moveScore;
			}
				b.unmakeMove(hamle);
			if(side==0){
				if(hamle.moveScore>=bestmove.moveScore){
					bestmove=hamle;
					//System.out.println("depth: "+depth+"  score: "+ hamle.moveScore+"  "+ hamle.move +" alpha "+alpha +" beta "+beta);
				}
				if(bestmove.moveScore>alpha) alpha = bestmove.moveScore;
				if(alpha > beta ) return bestmove;
				
			}else if(side==1){
				if(hamle.moveScore<=bestmove.moveScore){
					bestmove=hamle;
				}
					if(bestmove.moveScore<beta) beta = bestmove.moveScore;
					if(alpha > beta ) return bestmove;
					//System.out.println("side: "+side +" depth: "+depth+" best sofar: "+ bestmove.move+ "  score: "+bestmove.moveScore);
			}
		}
		return bestmove;
	}
	// ilk pozisyon çok önemli 
//position startpos moves g2g3 d7d5 f1g2 e7e5 e2e3 b8c6 d2d4 e5d4 e3d4 f8b4 c1d2 d8e7 d1e2 b4d2 b1d2 c6d4 e2e7 g8e7 e1c1 e8d8 d2e4 e7f5 e4g5 c8e6 g3g4 h7h6 g5e6 f7e6 g4f5 d4f5 g2d5 e6d5 d1d5
//position startpos moves e2e4 g8f6 b1c3 e7e5 d2d3 f8b4 c1g5 b4c3 b2c3 b8c6 g1f3 d7d6 d3d4 e5d4 f3d4 c8g4 f2f3 c6d4 c3d4 g4d7 f1c4 a7a5 a1b1 b7b6 e4e5 h7h6 e5f6 h6g5 f6g7 d8e7 d1e2
//position startpos moves d2d4 g7g6 c2c4 f8g7 e2e4 d7d6 b1c3 e7e5 g1f3 c8g4 d4d5 b8d7 f1d3 g7h6 c1h6 g8h6 e1g1 f7f5 d1d2 f5f4 f3e1 d7f6 f2f3 g4d7 d3c2 g6g5 c2a4 g5g4 e1d3 d7a4 c3a4 d8d7 a4c3 h8g8 c4c5 h6f7 a1c1 g4f3 f1f3 d7g4 g1h1 f6e4 c3e4 g4g2
//System.out.println("depth: "+depth+"best sofar: "+ bestmove.move+ "  score: "+bestmove.moveScore);
//position startpos moves g1f3 g8f6 c2c4 g7g6 b1c3 f8g7 e2e4 d7d6 d2d4 e8g8 h2h3 e7e5 d4d5 a7a5 c1e3 b8a6 g2g4 f6d7 a2a3 d7b6 h1g1 c8d7 b2b3 g8h8 h3h4 f7f5 g4f5 g6f5 f3g5 h7h6 e4f5 d7f5 d1h5 d8e8 h5e8 a8e8 g5e4 b6d7 f1d3 b7b6 e1e2 a6c5 e4c5 d7c5 d3f5 f8f5 a1b1 e5e4 c3b5 f5f7 g1g4 h8h7 b5d4 a5a4 b3b4 g7d4 e3d4 c5d3 b1g1 e8e7 g4g8 d3e5 g8c8 f7f4 d4e5 d6e5 c4c5
//position startpos moves d2d4 g8f6 c2c4 e7e6 g1f3 b7b6 g2g3 c8a6 b2b3 d7d5 c4d5 e6d5 f1g2 f8d6 e1g1 e8g8 b1c3 f8e8 c1b2 a6b7 a1c1 b8a6 e2e3 c7c5 f1e1 a6c7 d4c5 b6c5 c3a4 c7e6 e1e2 a8c8 f3e1 d6f8 e2d2 e6g5 e1d3 f6e4 d2c2 d8a5 d3f4 c5c4 a4c3 c4b3 a2b3 e4c3 c2c3 c8d8 c3c7 d5d4 b2d4 b7g2 g1g2 g8h8 d4b6 a5b6 d1d2

	
	//returns a score
	// generate moves until no important captures left
	 int quiescence(Board b,double alpha, double beta, int side,int depth) {
		// TODO Auto-generated method stub
		int score=0;
		int captAval=1;
		do{
			MoveGenerator movegen=new MoveGenerator();
			ArrayList<Move>list=new ArrayList<>();
			list=movegen.moveGenerator(b, side, 1, null);
			ArrayList<Move> legalcaplist=new ArrayList<>();
			if(depth==0){
				Evaluate eval=new Evaluate(b);
				double movescore=eval.materialcount()+eval.positionalValues();
				return (int) movescore;
			}
			for (Move hamle: list){
				String to=hamle.move.substring(2, 4);
 				int cap= movegen.isCapture(b, to);
				b.makeMove(hamle);
				int check= movegen.isCheck(b,side);
				if(check==0&&cap!=0&&cap!=12&&cap!=6)// important capture
				{
					legalcaplist.add(hamle);
				}
				b.unmakeMove(hamle);
			}
			if(legalcaplist.size()==0){// quiet movee base case
				captAval=0;
				Evaluate eval=new Evaluate(b);
				double movescore=eval.materialcount()+eval.positionalValues();
				return (int) movescore;
				
			}
			else{// there are legal captures
				for(Move hamle : legalcaplist){
					b.makeMove(hamle);
					double movescore=quiescence(b, alpha, beta, 1-side, depth-1);
					b.unmakeMove(hamle);
					if(side==0){
						if(movescore>alpha){
							alpha = movescore;
							score=(int) movescore;
						}
						if(alpha > beta ) return score;
						
					}else if(side==1){
							if(movescore<beta) {
								beta = movescore;
								score=(int) movescore;
							}
					}
						if(alpha > beta ) return (int) movescore;
					}
				}
			//System.out.println("quiescence"+" side: "+ side);
			captAval=0;
		}
		while(captAval==1); 
		return score;
	}
	//this minimax checks all nodes and returns best path with score	
	// çok sakat var
	//þuan program çalýþýyor ve yaklaþýk 1dkda 4ply için sonucu buldu
	// yani 5 ply için yaklaþýk yarým saatte bulacak hamleyi hahaha
	// also returns the best path
	public String minimax(Board b,int side, int depth,Move lastmove){
		
		double movescore=0;
		String bestmove="";
		if(side==0)
			movescore=-9999999;
		else
			movescore=99999999;
		if(depth==0){
			eval=new Evaluate(b);
			double s=(eval.materialcount()+eval.positionalValues());
			String a=Double.toString(s);
			int add=7-a.length();
			if(movegen.isCheck(b, b.sideToMove-1)==1){
				 a="";
				a=Integer.toString((2*b.sideToMove-1)*9999999);

			}
			for (int i = 0; i < add; i++) {
				a="0"+a;
			}
			if(a.length()>7)
				return(a.substring(0,7));
			return a;
		}
		
		else{
			ArrayList<Move>list=movegen.moveGenerator(b, side,1,lastmove);
			int checkcount=0;
			//System.out.println("ardaaa");position startpos moves e2e3 e7e5 g1f3 d7d5 f3e5 d8h4 d1f3 h4f2 f3f2
			for(Move hamle : list){
				String move=hamle.move;	
				int movelen=move.length();
				String movesix=move;
				if(movelen==5){
					movesix+=" ";
				}
				b.makeMove(hamle);
				int check= movegen.isCheck(b, side);
			//	position startpos moves c2c4 c7c5 b2b4 c5b4 d2d4 d7d5 c4d5 d8d5 e2e4 d5e4 f1e2 e4e2 d1e2 g8f6 e2e7 f8e7 b1c3 b4c3 a1b1 b8c6 b1b7 c8b7 g1f3 c6d4 f3d4 b7g2 h1g1 a8b8 g1g2 b8b2 c1b2 c3b2 g2g7
			//  position startpos moves e2e3 b7b6 a2a4 e7e6 b2b4 c7c6 c2c4 d7d6 d2d4 d6d5 c4d5 e6d5 e3e4 d5e4 g2g3 e4e3 f2e3 c6c5 d4c5 d8d6 c5b6 d6d8 d1f3 a7b6 f3e2 g7g6 h2h4 g6g5 e2c4 g5h4 c4h4 a8a7 h4d8
			// position startpos moves e2e3 e7e6 f1b5 a7a6
				if(check==0){// not check legal move
					checkcount++;
					String movestring=minimax(b, 1-side, depth-1,lastmove);
					int scoreindex= (depth-1)*6;
					String s="";
						while(movestring.charAt(scoreindex)=='0'){
						scoreindex++;
					}
					s=""+movestring.substring(scoreindex);
					double moves=Double.parseDouble(s);
					if(side==0){
						if(moves>=movescore)
						{
							movescore=moves;
							bestmove=movesix+movestring;
						}
					}
					if(side==1){
						if(moves<=movescore)
						{
							movescore=moves;
							bestmove=movesix+movestring;
						}
					}
				}
				
				b.unmakeMove(hamle);// burada sorun var tabiki
				//b.changeSide();
			}
			if(checkcount==0)//checkmate!!!
			{
				String s="";
				int dep=depth;
				for(int i=0;i<dep;i++){
					s+="aaaaaa";
				}
				if(b.sideToMove==0)
					s+="-100000";
				else
					s+="1000000";
				return s;
			}
		}
		return bestmove;
	}
	int digit(int N){
		int count=0;
		N=Math.abs(N);
		while(N>=1)
		{
			N/=10;
			count++;
		}
		return count;
	}
	public void printPath(){
		System.out.println();
	}
}
