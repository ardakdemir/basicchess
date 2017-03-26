//given a board evaluates the static score of the current position
public class Evaluate {
	Board myboard;
	long col1=0b1000000010000000100000001000000010000000100000001000000010000000L;
	long blkA=0b0000000000000000111111111111111111111111000000000000000000000000L;
	long whtA=0b0000000000000000000000001111111111111111111111110000000000000000L;
	long col8=0b0000000100000001000000010000000100000001000000010000000100000001L;
	long wReach=0;
	long bReach=0;
	double weights[]=new double[100];
	int wNmob;
	int bNmob;
	long whitekingadjacent=0;
	int whitekingattackedpoint=0; // attacking white
	int blackkingattackedpoint=0; // attacking black
	int whitekingdefendedpoint=0; // defending white
	int blackkingdefendedpoint=0; // defending black
	long []pieceReach=new long[12];
	long blackkingadjacent=0;
	long tempreach=0;
	// static values
	public Evaluate(Board b, Individual ind){
		myboard=b;
		for(int i = 0;i<100;i++)
			this.weights[i]=ind.weights[i];
		wReach=0;
		bReach=0;
	}
	public Evaluate(Board b){
		myboard=b;
		int count=0;// weak square
		weights[count++]=-5;//weakcount;
		weights[count++]=-15;//enemy knight on weak;
		weights[count++]=20;//center pawn count;
		count+=7;// king value
		weights[count++]=50;//pawn shield;		
		weights[count++]=-3;//king adjacent attacked;
		weights[count++]=51;//king castled
		count+=7;//bishop value
		weights[count++]=34;//bishop mob;
		weights[count++]=7;//bishop on large diag;
		weights[count++]=27;//bishop pair;
		count+=7;//knight value
		weights[count++]=28;//knight mob;
		weights[count++]=16;//knight supported
		weights[count++]=-10;//knight perip
		weights[count++]=14;//knight perip1
		weights[count++]=20;//knight perip2
		weights[count++]=26;//knight perip3
		count+=4;// pawn vlaue
		weights[count++]=-23;//iso pawn ;
		weights[count++]=-14;//double pawn ;
		weights[count++]=34;//pass pawn ;
		weights[count++]=40;//rookbhd pawn ;
		weights[count++]=-19;//back pawn ;
		weights[count++]=0;//rank of passed pawn
		weights[count++]=0;//blocked pawn pen
		weights[count++]=0;//blocked pass pawn pen
		count+=2;// rook vlaue
		weights[count++]=22;//rook open file ;
		weights[count++]=-6;//rook semi-open file ;
		weights[count++]=50;//rook on seventh;
		weights[count++]=10;//rook mob;
		weights[count++]=3;//rook con;
		weights[count++]=-11;//closed
		count+=4;// queen vlaue
		weights[count++]=12;//queen mob;
		int piece= 94;
		weights[piece++]=100;//pawn
		weights[piece++]=295;//knight
		weights[piece++]=315;//bishop
		weights[piece++]=489;//rook
		weights[piece++]=921;//queen

	}

	// these are not values of the pieces they are positional values positional parameters
	public double positionalValues(){
		// TODO Auto-generated method stub
		long allp=myboard.whitePieces|myboard.blackPieces;
		int wbishopcount=0;
		int bbishopcount=0;
		double rookValue=0;
		double pawnValue=0;
		double knightValue=0;
		double bishopValue=0;
		double queenValue=0;
		double kingValue=0;
		whitekingattackedpoint=0;
		blackkingattackedpoint=0;
		int wkingsqu= (int) (Math.log(myboard.wK)/Math.log(2));
		int bkingsqu= (int) (Math.log(myboard.bK)/Math.log(2));
		if(wkingsqu==0&&(myboard.wK&1)==0)
			wkingsqu=63;
		if(bkingsqu==0&&(myboard.bK&1)==0)
			bkingsqu=63;
		whitekingadjacent=adjacentSquCalc(wkingsqu);
		blackkingadjacent=adjacentSquCalc(bkingsqu);
		double weaks=weakSquare(0)-weakSquare(1);
		long a=1;
		for(int i=0;i<64;i++)
		{
			tempreach=0;
			a=1;
			a<<=i;
			if((a&allp)==0){// if the square is empty which is the case most times

			}
			else if((a&myboard.wB)!=0){
				bishopValue+=bishopValue(i, 0);
				wbishopcount++;
			}
			else if((a&myboard.bB)!=0){
				bishopValue-=bishopValue(i,1);
				bbishopcount++;
			}
			else if((a&myboard.wP)!=0){
				pawnValue+=pawnValue(i, 0);
			}
			else if((a&myboard.bP)!=0){
				pawnValue-=pawnValue(i,1);
			}else if((a&myboard.wN)!=0){
				knightValue+=knightValue(i, 0);
			}
			else if((a&myboard.bN)!=0){
				knightValue-=knightValue(i,1);
			}else if((a&myboard.wR)!=0){
				rookValue+=rookValue(i, 0);
			}
			else if((a&myboard.bR)!=0){
				rookValue-=rookValue(i,1);
			}else if((a&myboard.wQ)!=0){
				queenValue+=queenValue(i, 0);
			}
			else if((a&myboard.bQ)!=0){
				queenValue-=queenValue(i,1);
			}else if((a&myboard.wK)!=0){
				kingValue+=kingValue(i, 0);
			}
			else if((a&myboard.bK)!=0){
				kingValue-=kingValue(i,1);
			}
		}
		double bishoppair=0;
		if(wbishopcount>=2)
			bishoppair+=weights[22];
		if(bbishopcount>=2)
			bishoppair-=weights[22];
		int kingattacked= (int) (weights[11]*((whitekingattackedpoint-blackkingattackedpoint)/100));
		int kingdefended=   (int) ((int) -1*(weights[11]*((whitekingdefendedpoint-blackkingdefendedpoint)/100)));
		/*	System.out.println("rooks: "+rookValue);
		System.out.println("queens: "+queenValue);
		System.out.println("knights: "+knightValue);
		System.out.println("bishop: "+bishopValue);
		System.out.println("pawn: "+pawnValue);
		System.out.println("king: "+kingValue);
		System.out.println("weaks: "+weaks);
		System.out.println("bishop pair: "+bishoppair);
		 */
		kingattacked+=kingdefended;
		return (rookValue+pawnValue+knightValue+bishopValue+queenValue+kingValue+bishoppair+weaks+kingattacked);
		//+weaks+kingattacked+kingdefended);
		// simdilik bu sayilari elle girdim
	}
	public double positionalValues1(){
		// TODO Auto-generated method stub
		long allp=myboard.whitePieces|myboard.blackPieces;
		int wbishopcount=0;
		int bbishopcount=0;
		double rookValue=0;
		double pawnValue=0;
		double knightValue=0;
		double bishopValue=0;
		double queenValue=0;
		double kingValue=0;
		double rookValueB=0;
		double pawnValueB=0;
		double knightValueB=0;
		double bishopValueB=0;
		double queenValueB=0;
		double kingValueB=0;
		whitekingattackedpoint=0;
		whitekingdefendedpoint=0;
		blackkingattackedpoint=0;
		blackkingdefendedpoint=0;
		int wkingsqu= (int) (Math.log(myboard.wK)/Math.log(2));
		int bkingsqu= (int) (Math.log(myboard.bK)/Math.log(2));
		if(wkingsqu==0&&(myboard.wK&1)==0)
			wkingsqu=63;
		if(bkingsqu==0&&(myboard.bK&1)==0)
			bkingsqu=63;
		whitekingadjacent=adjacentSquCalc(wkingsqu);
		blackkingadjacent=adjacentSquCalc(bkingsqu);
		double weakswhite=weakSquare(0);
		double weaksblack=weakSquare(1);
		long a=1;
		for(int i=0;i<64;i++)
		{
			tempreach=0;
			a=1;
			a<<=i;
			if((a&allp)==0){// if the square is empty which is the case most times

			}
			else if((a&myboard.wB)!=0){
				bishopValue+=bishopValue(i, 0);
				wbishopcount++;
			}
			else if((a&myboard.bB)!=0){
				bishopValueB+=bishopValue(i,1);
				bbishopcount++;
			}
			else if((a&myboard.wP)!=0){
				pawnValue+=pawnValue(i, 0);
			}
			else if((a&myboard.bP)!=0){
				pawnValueB+=pawnValue(i,1);
			}else if((a&myboard.wN)!=0){
				knightValue+=knightValue(i, 0);
			}
			else if((a&myboard.bN)!=0){
				knightValueB+=knightValue(i,1);
			}else if((a&myboard.wR)!=0){
				rookValue+=rookValue(i, 0);
			}
			else if((a&myboard.bR)!=0){
				rookValueB+=rookValue(i,1);
			}else if((a&myboard.wQ)!=0){
				queenValue+=queenValue(i, 0);
			}
			else if((a&myboard.bQ)!=0){
				queenValueB+=queenValue(i,1);
			}else if((a&myboard.wK)!=0){
				kingValue+=kingValue(i, 0);
			}
			else if((a&myboard.bK)!=0){
				kingValueB+=kingValue(i,1);
			}
		}
		//position startpos moves e2e4 e7e6 d2d4 d8h4 b1c3 f8b4 f1d3 b8c6 g1f3 h4g4 e1g1 g8f6 e4e5 f6d5 c3d5 e6d5 c2c3 b4e7 d3e2 h7h5 h2h3 g4g6 e2d3 f7f5 e5f6 g6f6 c1g5 f6e6 d1a4 e7g5

		double bishoppair=0;
		double bishoppairb=0;
		if(wbishopcount>=2)
			bishoppair+=weights[22];
		if(bbishopcount>=2)
			bishoppairb+=weights[22];
		int kingattacked= (int) (weights[11]*((whitekingattackedpoint-blackkingattackedpoint)/100));
		myboard.printBoard();
		System.out.println("wrooks: "+rookValue);
		System.out.println("wqueens: "+queenValue);
		System.out.println("wknights: "+knightValue);
		System.out.println("wbishop: "+bishopValue);
		System.out.println("wpawn: "+pawnValue);
		System.out.println("wking: "+kingValue);
		System.out.println("wweaks: "+weakswhite);
		System.out.println("wbishop pair: "+bishoppair);
		System.out.println("brooks: "+rookValueB);
		System.out.println("bqueens: "+queenValueB);
		System.out.println("bknights: "+knightValueB);
		System.out.println("bbishop: "+bishopValueB);
		System.out.println("bpawn: "+pawnValueB);
		System.out.println("bking: "+kingValueB);
		System.out.println("bweaks: "+weaksblack);
		System.out.println("bbishop pair: "+bishoppairb);	
		System.out.println("whitekingattacked:"+whitekingattackedpoint);
		System.out.println("whitekingdefended:"+whitekingdefendedpoint);
		System.out.println("blackkingattacked:"+blackkingattackedpoint);
		System.out.println("blackkingdefended:"+blackkingdefendedpoint);	
		System.out.println("whiteadjacent: "+ Long.toBinaryString(whitekingadjacent));
		System.out.println("blackadjacent: "+ Long.toBinaryString(blackkingadjacent));
		return (rookValue+pawnValue+knightValue+bishopValue+queenValue+kingValue+bishoppair+weakswhite+kingattacked);// simdilik bu sayilari elle girdim
	}

	//returns a value of the given position by only conidering available captures
	//for a depth of 2 
	//test
	//position startpos moves e2e4 e7e6 g1f3 d8f6 e4e5 f6f4 d2d3 f8b4 c2c3 f4g4 c3b4 b8c6 c1d2 c6e5
	//position startpos moves d2d4 g8f6 c2c4 g7g6 b1c3 f8g7 e2e4 d7d6 g1f3 e8g8 f1e2 e7e5 e1g1 b8c6 d4d5 c6e7 b2b4 f6h5 f1e1 h5f4 e2f1 f7f5 c1f4 e5f4 e4e5 h7h6 d1d2 g6g5 e5d6 c7d6 f3d4 e7g6 a1d1 g6e5 c4c5 a7a5 a2a3 a5b4 a3b4 d8f6 c3b5 d6c5 b4c5 c8d7 b5c7 a8c8 d5d6 b7b6 c7d5 f6f7 d5e7 g8h8
	//position startpos moves d2d4 g8f6 c2c4 g7g6 b1c3 f8g7 e2e4 d7d6 g1f3 e8g8 f1e2 e7e5 e1g1 b8c6 d4d5 c6e7 b2b4 f6h5 f1e1 h5f4 e2f1 f7f5 c1f4 e5f4 e4e5 h7h6 d1d2 g6g5 e5d6 c7d6 f3d4 e7g6 a1d1 g6e5 c4c5 g8h7
	double captureRoutine(int side){
		double bestcapVal=0;
		if(side==0){
			long capture=wReach&myboard.blackPieces;
			for(int i=0;i<64;i++){
				long squ=1;
				if(((squ<<i)&capture)!=0){//there is an available capture
					double capturedVal=capturedValCalc(i);
					int attacker=findleastValuableAttacker(i, 0);
					int defender=findleastValuableAttacker(i, 1);
					//myboard.printBoard();
					//System.out.println("squ: "+i+" side: " +side+" attacker: "+attacker+" defender: "+ defender);
					double attackval=pieceVal(attacker);
					double diff=0;
					if(defender!=12){//there is a defender of the captured piece
						diff=capturedVal-attackval;
					}
					else
						diff=capturedVal;

					//System.out.println("nokta: "+ i+" attacker: "+ attacker+" defender: "+ defender+" diff: "+diff);

					if(diff>bestcapVal){
						bestcapVal=diff;
					}

				}
			}
		}
		if(side==1){
			long capture=bReach&myboard.whitePieces;
			for(int i=0;i<64;i++){
				long squ=1;
				if(((squ<<i)&capture)!=0){//there is an available capture
					double capturedVal=capturedValCalc(i);
					int attacker=findleastValuableAttacker(i, 1);
					//System.out.println("attack val: "+ attacker);
					int defender=findleastValuableAttacker(i, 0);
					double attackval=pieceVal(attacker);
					double diff=0;
					if(defender!=12){//there is a defender of the captured piece
						diff=capturedVal-attackval;
					}
					else
						diff=capturedVal;
					if(diff>bestcapVal){
						bestcapVal=diff;
					}

				}
			}
		}
		return bestcapVal;
	}
	double capturedValCalc(int i) {
		// TODO Auto-generated method stub
		long pos=1;
		pos<<=i;
		if((pos&myboard.wP)!=0||(pos&myboard.bP)!=0)
			return pieceVal(0);
		else if((pos&myboard.wN)!=0||(pos&myboard.bN)!=0)
			return pieceVal(1);
		else if((pos&myboard.wB)!=0||(pos&myboard.bB)!=0)
			return pieceVal(2);
		else if((pos&myboard.wR)!=0||(pos&myboard.bR)!=0)
			return pieceVal(3);
		else if((pos&myboard.wQ)!=0||(pos&myboard.bQ)!=0)
			return pieceVal(4);
		return 0;
	}
	double pieceVal(int piece){
		if(piece==0||piece==6)
			return weights[94];
		else if(piece==1||piece==7)
			return weights[95];
		else if(piece==2||piece==8)
			return weights[96];
		else if(piece==3||piece==9)
			return weights[97];
		else if(piece==4||piece==10)
			return weights[98];
		return 0;
	}

	//finds any attacker to a square
	//returns 12 if no attacker to that square
	int findleastValuableAttacker(int squ,int side){
		long pos =1;
		int attackerVal=9999;
		int attacker=12;
		pos<<=squ;
		if(side==0){
			for(int i =0;i<6;i++)
				if((pieceReach[i]&pos)!=0)
				{
					int val = (int) weights[94+i];
					if(val<attackerVal){
						attackerVal=val;
						attacker=i;
					}
				}
		}
		if(side==1){
			for(int i =0;i<6;i++)
				if((pieceReach[i+6]&pos)!=0)
				{
					int val = (int) weights[94+i];
					if(val<attackerVal){
						attackerVal=val;
						attacker=i+6;
					}
				}
		}
		return attacker;
	}
	int isAttacked(int squ,int side){
		long pos=1;
		pos<<=squ;
		if(side==0){
			if((pos&wReach)!=0){
				return 1;
			}
			else{
				return 0;
			}
		}
		if(side==1){
			if((pos&wReach)!=0){
				return 1;
			}
			else{
				return 0;
			}
		}
		return 1;
	}
	long adjacentSquCalc(int squ) {
		// TODO Auto-generated method stub
		long a = 0;
		int col=squ%8;
		int pos=squ-8;
		int temp = pos;
		for(int row = 0 ; row<3;row++){
			if(pos>=0&&pos<64){
				for(int colu=-1;colu<2;colu++){
					long b = 1;
					int temp1 = pos;
					pos+=colu;
					if(pos%8-col<2&&pos%8-col>-2&& pos<64&& pos>=0){
						b<<=pos;
						a |= b;
					}
					pos=temp1;
				}
			}
			pos=temp+ 8 * (row +1);
		}
		return a;
	}
	//kontrol etmedim burada hem rakip atlar weak squarede mi kontrolu
	// hem de weak square sayisi bulunuyor baska weak square ile alakali seyler de ekleneilir
	double weakSquare(int side){
		long mychain=0;
		long leftcap=0;
		long rightcap=0;
		long cap=0;
		long weak=0;
		long ptr=1;
		int enemyknightonWeak=0;
		int weakcount=0;
		int centerpawncount=0;
		if(side==0){
			mychain=myboard.wP;
			leftcap=(mychain<<9)&~col8;
			rightcap=(mychain<<7)&~col1;
			wReach|=leftcap;
			wReach|=rightcap;
			cap=leftcap|rightcap;
			reachCalc('P', cap, side);
			for(int i=0;i<2;i++){
				cap|=(cap<<8);
			}
			weak=(whtA&~cap);
			for(int a=16;a<32;a++){
				ptr=1;
				ptr<<=a;
				if((ptr&weak)!=0){
					weakcount++;
					if((ptr&myboard.bN)!=0)
						enemyknightonWeak++;
				}
			}
		}
		else{
			mychain=myboard.bP;
			leftcap=(mychain>>9)&~col1;
			rightcap=(mychain>>7)&~col8;
			bReach|=leftcap;
			bReach|=rightcap;
			cap=leftcap|rightcap;
			reachCalc('P', cap, side);
			for(int i=0;i<2;i++){
				cap|=(cap>>8);
			}
			weak=(blkA&~cap);
			for(int a=32;a<48;a++){
				ptr=1;
				ptr<<=a;
				if((ptr&weak)!=0){
					weakcount++;
					if((ptr&myboard.wN)!=0)
						enemyknightonWeak++;
				}
			}
		}
		long temp=mychain;
		if((temp>>=27)%2==1)
			centerpawncount++;
		if((temp>>=1)%2==1)
			centerpawncount++;
		if((temp>>=7)%2==1)
			centerpawncount++;
		if((temp>>=1)%2==1)
			centerpawncount++;
		//System.out.println(side+" weakcount: "+weakcount+" enemyknight:"+ enemyknightonWeak+" center pawn: "+ centerpawncount);
		return weakcount*weights[0]+enemyknightonWeak*weights[1]+centerpawncount*weights[2];
	}
	// bu karisik biraz daha
	double kingValue(int squ,int side) {
		// TODO Auto-generated method stub
		int pawnshield=0;
		int iscastled=0;
		if(side==0&&myboard.isWhiteCastled==1)
			pawnshield=pawnshield(squ, side);
		if(side==1&&myboard.isBlackCastled==1)
			pawnshield=pawnshield(squ, side);
		pawnshield=pawnshield(squ, side);
		iscastled=isCastled(side);
		kingMob(squ,side);
		//System.out.println(side+" king at: "+squ+"pawnshi: "+ pawnshield+" iscastl:"+ iscastled);
		return pawnshield*weights[10]+iscastled*weights[12];
	}

	private int kingMob(int squ, int side) {
		// TODO Auto-generated method stub
		long kare = 1;
		long reach=0;
		int count=0;
		kare<<=squ;
		int row = squ/8;
		int col = squ%8;
		if(row+1<8&&col+1<8){reach|=(1L<<8*(row+1)+col+1);
		count++;
		}
		if(col+1<8){reach|=(1L<<8*(row)+col+1);
		count++;
		}
		if(row+1<8&&col-1>=0){reach|=(1L<<8*(row+1)+col-1);
		count++;
		}
		if(col-1>=0){reach|=(1L<<8*(row)+col-1);
		count++;
		}
		if(row-1>=0&&col+1<8){reach|=(1L<<8*(row-1)+col+1);
		count++;
		}
		if(row+1<8){reach|=(1L<<8*(row+1)+col);
		count++;
		}
		if(row-1>=0){reach|=(1L<<8*(row-1)+col);
		count++;
		}
		if(row-1>=0&&col-1>=0){reach|=(1L<<8*(row-1)+col-1);
		count++;
		}
		if(side==0){
			wReach|=reach;
		}
		if(side==1){
			bReach|=reach;
		}
		reachCalc('Q', reach, side);
		//count=bitCounter(reach);
		return count;
	}
	int isCastled(int side) {
		// TODO Auto-generated method stub
		if(side==0){
			if (myboard.rights[0]==1)
				return 1;
			else 
				return 0;
		}
		else{
			if (myboard.rights[1]==1)
				return 1;
			else 
				return 0;
		}
	}
	//number of pawns in front of the king after castled
	int pawnshield(int squ,int side) {
		// TODO Auto-generated method stub
		int dir=1;
		int pawncount=0;
		int temp=squ;
		long mypwn=0;
		long a=1;
		if(side==0){
			mypwn=myboard.wP;
		}
		if(side==1){
			dir=-1;
			mypwn=myboard.bP;

		}
		temp+=dir*8;
		if((squ%8)==0){//en sag
			a=1;
			a<<=(temp+1);
			if((a&mypwn)!=0)
				pawncount++;
			a=1;
			a<<=(temp);
			if((a&mypwn)!=0)
				pawncount++;
		}
		else if((squ%8)==7){//en sol
			a=1;
			a<<=(temp);
			if((a&mypwn)!=0)
				pawncount++;
			a=1;
			a<<=(temp-1);
			if((a&mypwn)!=0)
				pawncount++;
		}
		else{// orta
			a=1;
			a<<=(temp+1);
			if((a&mypwn)!=0)
				pawncount++;
			a=1;
			a<<=(temp);
			if((a&mypwn)!=0)
				pawncount++;
			a=1;
			a<<=(temp-1);
			if((a&mypwn)!=0)
				pawncount++;
		}
		return pawncount++;
	}

	double bishopValue(int squ,int side) {
		// TODO Auto-generated method stub
		int bmob=0;
		int blargediag=0;
		blargediag=isbislargeDiag(squ,side);
		bmob=bishopMob(squ, side);
		//System.out.println(side+" bishop at: "+squ+" blargediag: "+ blargediag+" bmob:"+ bmob);

		return bmob*weights[20]+blargediag*weights[21]+weights[96];
	}

	int isbislargeDiag(int squ, int side) {
		// TODO Auto-generated method stub
		int col=squ%8;
		int row=squ/8;
		if(row+col==7)
			return 1;
		if(row==col)
			return 1;
		return 0;
	}

	double knightValue(int squ,int side) {
		// TODO Auto-generated method stub
		int kniMob=knightMob(squ, side);
		int kniSup=knightSup(squ,side);
		int kniPerip=knightPer(squ);
		int kniperval=0;
		if(kniPerip==0)
			kniperval=(int) weights[32];
		else if(kniPerip==1)
			kniperval=(int) weights[33];
		else if(kniPerip==2)
			kniperval=(int) weights[34];
		else
			kniperval=(int) weights[35];
		//System.out.println(side+" knight at: "+squ+" knimob:"+ kniMob+"  kniperip: "+kniPerip);
		return kniMob*weights[30]+weights[95]+kniSup*weights[31]+kniperval;
	}

	private int knightPer(int squ) {
		// TODO Auto-generated method stub
		int col = squ%8;
		int row = squ/8;
		if(col==0||col==7||row==0||row==7)
			return 0;
		else if(col==1||col==6||row==1||row==6)
			return 1;
		else if(col==2||col==5||row==2||row==5)
			return 2;

		return 3;
	}
	private int knightSup(int squ, int side) {
		// TODO Auto-generated method stub
		int col=squ%8;
		int row=squ/8;
		if(side==0){
			long mypawn=myboard.wP;
			if(row==0||row==1)
				return 0;
			else{
				long p=1;
				if(col==0){
					p<<=(squ-7);
					if((mypawn&p)!=0)
						return 1;
				}
				else if(col==7){
					p=1;
					p<<=(squ-9);
					if((mypawn&p)!=0)
						return 1;
				}
				else{
					p=1;
					p<<=(squ-7);
					if((mypawn&p)!=0)
						return 1;
					p=1;
					p<<=(squ-9);
					if((mypawn&p)!=0)
						return 1;
				}
			}
		}
		else{
			long mypawn=myboard.bP;
			if(row==6||row==7)
				return 0;
			else{
				long p=1;
				if(col==0){
					p<<=(squ+9);
					if((mypawn&p)!=0)
						return 1;
				}
				else if(col==7){
					p=1;
					p<<=(squ+7);
					if((mypawn&p)!=0)
						return 1;
				}
				else{
					p=1;
					p<<=(squ+7);
					if((mypawn&p)!=0)
						return 1;
					p=1;
					p<<=(squ+9);
					if((mypawn&p)!=0)
						return 1;
				}
			}
		}
		return 0;
	}
	double pawnValue(int squ,int side) {
		// TODO Auto-generated method stub
		int isoPawn=0;
		int doubPawn=0;
		int passPawn=0;
		int rookbhdpasspawn=0;
		int backPawn=0;
		int passPawnrank=0;
		int blockedPawnpen=0;
		int blockedpasspawnpen=0;
		isoPawn=isoPawn(squ,side);
		doubPawn=doubledPawn(squ, side);
		passPawn=passedPawn(squ, side);
		if(isoPawn==1)
			backPawn=0;
		else{
			backPawn=backPawn(squ, side);
		}
		if(passPawn==1){
			rookbhdpasspawn=isrookbehind(squ,side);
			passPawnrank=squ/8;
			blockedpasspawnpen=blockedPasspawnpen(squ,side);// by enemy piece 
			if(side==1){
				passPawnrank= 7-passPawnrank;
			}
		}
		if (side ==0)
		{
			if(squ==11||squ==12)
				blockedPawnpen=blockedPawnpen(squ,side);// by own pieces
		}
		if(side==1){
			if(squ==51||squ==52)
				blockedPawnpen=blockedPawnpen(squ,side);// by own pieces
		}
		//passPawnrank*=passPawnrank; // give a lot importance 
		//position startpos moves e2e4 g8f6 b1c3 e7e5 d2d3 f8b4 c1g5 b4c3 b2c3 b8c6 g1f3 d7d6 d3d4 e5d4 f3d4 c8g4 f2f3 c6d4 c3d4 g4d7 f1c4 a7a5 a1b1 b7b6 e4e5 h7h6 e5f6 h6g5 f6g7 d8e7 d1e2
		//position startpos moves e2e4 e7e5 f2f4 g8f6 d2d3 b8c6 b1c3 f8c5 g1f3 f6g4 d3d4 e5d4 c3d5 g4f6 f1d3 f6d5 e4d5 c6e7 e1f2 e7d5 h1e1 e8f8 e1e5 d5e3 c1e3 d4e3 f2g3 d7d6 e5e4 d6d5 e4e5 f7f6 e5e3 c5e3 d1e2 e3c5 c2c4 d5c4 d3c4 d8e8 e2e8 f8e8 a1e1 e8d8 c4e6 c8e6 e1e6 h8e8 e6e8 d8e8 g3g4 e8d7 h2h3 a8e8 b2b3 e8e2 g2g3 e2a2 f3e1 a2e2 e1d3 c5d4 b3b4 e2d2 d3c5 d4c5 b4c5 d7c6 g4f5 d2a2 f5e4 c6c5 g3g4 a2h2 e4f5 h2h3 g4g5 f6g5 f5g5 c5d5 g5g4 h3a3 f4f5

		/*System.out.println("side: "+side+"  squ: "+ squ);
		System.out.println("iso: "+isoPawn);
		System.out.println("doub "+doubPawn);
		System.out.println("pass "+passPawn);
		System.out.println("pass rank: "+passPawnrank);
		System.out.println("rookbhd "+rookbhdpasspawn);*/
		//System.out.println(side+" pawn at: "+squ+" isopawn:"+ isoPawn+" doubPawn: "+doubPawn+" rookbhd: "+rookbhdpasspawn+
		//		" backPawn: "+backPawn+" passPawnrank: "+ passPawnrank+"  blockePawnpen: "+ blockedPawnpen+" blockpass:"+
		//		blockedpasspawnpen);
		return isoPawn*weights[40]+doubPawn*weights[41]+passPawn*weights[42]+rookbhdpasspawn*weights[43]+
				backPawn*weights[44]+passPawnrank*weights[45]+blockedPawnpen*weights[46]+blockedpasspawnpen*weights[47]+
				weights[94];
	}
	int blockedPasspawnpen(int squ, int side) {
		// TODO Auto-generated method stub
		long enemypieces=0;
		int pos=0;
		if(side==1){
			enemypieces=myboard.whitePieces;
			pos=squ-8;
		}
		else{
			enemypieces=myboard.blackPieces;
			pos=squ+8;
		}
		long a=1;
		a<<=pos;
		if((enemypieces&a)!=0)
			return 1;
		return 0;
	}

	// returns if the pawn is undeveloped and blocked which prevents development
	int blockedPawnpen(int squ, int side) {
		// TODO Auto-generated method stub
		long mypiece=0;
		int dir=-8;
		if(side==0){
			mypiece=myboard.whitePieces;
			dir=8;
		}
		else{
			mypiece=myboard.blackPieces;
		}
		int squ1=squ+dir;
		long pos=1;
		pos<<=squ1;
		if((mypiece&pos)!=0)
			return 1;
		return 0;
	}

	//1 means the pawn is backward
	//0 otherwise
	int backPawn(int squ, int side) {
		// TODO Auto-generated method stub
		int row=squ/8;
		int col=squ%8;
		long mypawns=0;
		long a=1;
		if(side==0){
			mypawns=myboard.wP;
			if(col==0){
				for(int i=1;i<=row;i++){
					a=1;
					if(((a<<(i*8+col+1))&mypawns)!=0)
						return 0;

				}
			}else if(col==7){
				for(int i=1;i<=row;i++){
					a=1;
					if(((a<<(i*8+col-1))&mypawns)!=0)
						return 0;

				}
			}else{
				for(int i=1;i<=row;i++){
					a=1;
					if(((a<<(i*8+col+1))&mypawns)!=0||((a<<(i*8+col-1))&mypawns)!=0)
						return 0;

				}

			}
		}
		else if(side==1){
			mypawns=myboard.bP;
			if(col==0){
				for(int i=6;i>=row;i--){
					a=1;
					if(((a<<(i*8+col+1))&mypawns)!=0)
						return 0;

				}
			}else if(col==7){
				for(int i=6;i>=row;i--){
					a=1;
					if(((a<<(i*8+col-1))&mypawns)!=0)
						return 0;

				}
			}else{
				for(int i=6;i>=row;i--){
					a=1;
					if(((a<<(i*8+col+1))&mypawns)!=0||((a<<(i*8+col-1))&mypawns)!=0)
						return 0;

				}

			}
		}
		return 1;
	}

	// this just checks if a rook is behind the given square
	// now working only to check rook behind passed pawns
	int isrookbehind(int squ, int side) {
		// TODO Auto-generated method stub
		long pos=1;
		int col=squ%8;
		int row=squ/8;
		long my=0;
		if(side==0){
			my=myboard.wR;
			for(int i =0;i<row;i++){
				pos=(1L<<(8*i+col));
				if((pos&my)!=0)
					return 1;
			}
		}
		//position startpos moves e2e4 e7e5 f2f4 g8f6 d2d3 b8c6 b1c3 f8c5 g1f3 f6g4 d3d4 e5d4 c3d5 g4f6 f1d3 f6d5 e4d5 c6e7 e1f2 e7d5 h1e1 e8f8 e1e5 d5e3 c1e3 d4e3 f2g3 d7d6 e5e4 d6d5 e4e5 f7f6 e5e3 c5e3 d1e2 e3c5 c2c4 d5c4 d3c4 d8e8 e2e8 f8e8 a1e1 e8d8 c4e6 c8e6 e1e6 h8e8 e6e8 d8e8 g3g4 e8d7 h2h3 a8e8 b2b3 e8e2 g2g3 e2a2 f3e1 a2e2 e1d3 c5d4 b3b4 e2d2 d3c5 d4c5 b4c5 d7c6 g4f5 d2a2 f5e4 c6c5 g3g4 a2h2 e4f5 h2h3 g4g5 f6g5 f5g5 c5d5 g5g4 h3a3 f4f5 a3a2 g4f3 a2b2 f3g3 b2a2 g3f3 a2b2 f3g3

		else{
			my=myboard.bR;
			for(int i = row;i<8;i++){
				pos=(1L<<(8*i+col));
				if((pos&my)!=0)
					return 1;
			}
		}

		return 0;
	}

	double rookValue(int squ,int side) {
		// TODO Auto-generated method stub
		int ropenfile=isrookOpenFile(squ,side);
		int rookseventh=isrookSeventh(squ,side);
		int rookmob=rookMob(squ,side);
		int rookcon=isrookconnected(squ, side);
		double ropenfileweight=0;
		if(ropenfile==2)
			ropenfileweight=weights[50];
		if(ropenfile==1)
			ropenfileweight=weights[51];
		if(ropenfile==0)
			ropenfileweight=weights[55];
		//System.out.println("square"+squ+"rook open file ?"+ ropenfile);
		//System.out.println(side+" rook at: "+squ+" rookmob:"+ rookmob+" ropenfile: "+ropenfile+" rooksev: "+rookseventh+
		//		"rookcon: "+rookcon);
		return ropenfileweight+rookseventh*weights[52]+rookmob*weights[53]+rookcon*weights[54]+weights[97];
	}

	int isrookSeventh(int squ, int side) {
		// TODO Auto-generated method stub
		if(squ/8==(6-side*5))
			return 1;
		return 0;
	}

	// returns 0 if closed 1 if half-open 2 if open
	int isrookOpenFile(int squ, int side) {
		// TODO Auto-generated method stub
		int col=squ%8;
		long mypawn=0;
		long enempawn=0;
		long pos=1;
		int me=0;
		int enemy=0;
		if(side==0){
			mypawn=myboard.wP;
			enempawn=myboard.bP;
		}
		if(side==1){
			mypawn=myboard.bP;
			enempawn=myboard.wP;
		}
		for(int i=1;i<7;i++){
			pos=1;
			pos<<=(i*8)+col;
			if((pos&mypawn)!=0){
				me=1;
			}
			else if((pos&enempawn)!=0)
			{
				enemy=1;
			}
		}
		if(enemy==1&&me==1)
			return 0;
		else if(enemy==0)
			return 2;
		else if( enemy == 1&&me==0)
			return 1;

		return 4;
	}

	int isrookconnected(int squ,int side){
		long my=1;
		my<<=squ;
		int row=squ/8;
		int col=squ%8;
		long all=0;
		long myrooks=1;
		if(side==0)
		{
			myrooks=myboard.wR^my;
			all=myboard.whitePieces|myboard.blackPieces;
		}
		else
		{
			myrooks=myboard.bR^my;
			all=myboard.whitePieces|myboard.blackPieces;
		}
		long rookrow=(all&~my);
		for(int i=col-1;i>0;i--){
			my=1;
			my<<=(8*row)+i;
			if((my&rookrow)!=0){
				if((myrooks&my)!=0)
					return 1;
				else
					break;
			}
		}
		for(int i=col+1;i<8;i++){
			my=1;
			my<<=(8*row)+i;
			if((my&rookrow)!=0){
				if((myrooks&my)!=0)
					return 1;
				else
					return 0;
			}
		}
		return 0;
	}

	// burada bana bu tarafin bu noktada queeni varmis bilgisi geldi 
	// bu bilgiyle ben queen parametrelerini cagiriyorum
	double queenValue(int squ,int side) {
		int qmob=queenMob(squ,side);
		//System.out.println(side+" queen at: "+squ+" queenmob:"+ qmob);
		return qmob*weights[60]+weights[98];
	}
	//returns if a pawn is isolated
	int isoPawn(int squ, int side) {
		int count=0;
		// TODO Auto-generated method stub
		long my=0;
		int limit=6,i=0;
		if(side==0){
			my=myboard.wP;
		}
		if(side==1){
			my=myboard.bP;
		}
		int col=squ%8;
		if(col==7){
			while(i++!=limit){
				long right=1;
				right<<=(i*8+col-1);
				if((right&my)!=0){
					count++;
					break;
				}
			}
		}
		else if(col==0){
			while(i++!=limit){
				long left=1;
				left<<=(i*8+col+1);
				if((left&my)!=0){
					count++;
					break;
				}
			}
		}
		else{
			while(i++!=limit){
				long right=1;
				right<<=(i*8+col-1);
				long left=1;
				left<<=(i*8+col+1);
				if((right&my)!=0||(left&my)!=0){
					count++;
					break;
				}
			}
		}
		if(count==0)
			return 1;

		return 0;
	}

	//returns 1 if the pawn is doubled
	int doubledPawn(int squ, int side) {
		int count=0;
		long squy=1;
		// TODO Auto-generated method stub
		long my=0;
		if(side==0){
			my=myboard.wP;
		}
		if(side==1){
			my=myboard.bP;
		}
		int pos=squ;
		int gir=0;
		while(pos<55){
			pos+=8;
			squy=1;
			squy<<=pos;
			if((my&squy)!=0)//double detected
			{
				count++;
				gir=1;
				break;
			}
		}
		pos=squ;
		while(pos>=8&&gir==0){
			pos-=8;
			squy=1;
			squy<<=pos;
			if((my&squy)!=0)//double detected
			{
				count++;
				break;
			}
		}
		return count;
	}

	int passedPawn(int squ, int side){
		int count=0;
		long enemypawn=0;
		int dir=1;
		int initrow=1;
		if(side==0){
			enemypawn=myboard.bP;
			dir=-1;
			initrow=6;
		}
		if(side==1){
			enemypawn=myboard.wP;

		}
		int col=squ%8;
		int row=squ/8;
		long left=1;
		long right=1;
		long center=1;
		if(col==0){
			while(initrow!=row){
				left=1;
				center=1;
				center<<=initrow*8+col;
				left<<=initrow*8+col+1;
				if((enemypawn&left)!=0||(enemypawn&center)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		else if(col==7)
		{
			while(initrow!=row){
				right=1;
				center=1;
				center<<=initrow*8+col;
				right<<=initrow*8+col-1;
				if((enemypawn&right)!=0||(enemypawn&center)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		else{
			while(initrow!=row){
				left=1;
				center=1;
				right=1;
				center<<=initrow*8+col;
				right<<=initrow*8+col-1;
				left<<=initrow*8+col+1;
				if((enemypawn&right)!=0||(enemypawn&center)!=0||(enemypawn&left)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		return 1;
	}

	int rookMob(int squ,int side){
		int count=0;// # of available moves
		tempreach=0;
		count +=horizontalMob(squ, side,'R');
		count+=verticalMob(squ, side,'R');
		kingadjacCheck(tempreach, side, 'R');
		return count;
	}
	int knightMob(int squ,int side){
		int count=0;// # of available moves
		long mypiece=0;
		long reach=0;
		char piece = 'N'; 
		if(side==0){
			mypiece=myboard.whitePieces;
		}
		else {
			mypiece=myboard.blackPieces;
		}
		int row=squ/8;
		int col=squ%8;
		if(row+1<8&&col+2<8){
			reach|=(1L<<8*(row+1)+col+2);
			if((mypiece&(1L<<8*(row+1)+col+2))==0)
				count++;
		}
		if(row+2<8&&col+1<8){reach|=(1L<<8*(row+2)+col+1);
		if((mypiece&(1L<<8*(row+2)+col+1))==0)
			count++;
		}
		if(row+1<8&&col-2>=0){reach|=(1L<<8*(row+1)+col-2);
		if((mypiece&(1L<<8*(row+1)+col-2))==0)
			count++;
		}
		if(row+2<8&&col-1>=0){reach|=(1L<<8*(row+2)+col-1);
		if((mypiece&(1L<<8*(row+2)+col-1))==0)
			count++;
		}
		if(row-1>=0&&col+2<8){reach|=(1L<<8*(row-1)+col+2);
		if((mypiece&(1L<<8*(row-1)+col+2))==0)
			count++;
		}
		if(row-2>=0&&col+1<8){reach|=(1L<<8*(row-2)+col+1);
		if((mypiece&(1L<<8*(row-2)+col+1))==0)
			count++;
		}
		if(row-1>=0&&col-2>=0){reach|=(1L<<8*(row-1)+col-2);
		if((mypiece&(1L<<8*(row-1)+col-2))==0)
			count++;
		}
		if(row-2>=0&&col-1>=0){reach|=(1L<<8*(row-2)+col-1);
		if((mypiece&(1L<<8*(row-2)+col-1))==0)
			count++;
		}
		if(side==0){
			wReach|=reach;
		}
		if(side==1){
			bReach|=reach;
		}
		reachCalc(piece, reach, side);
		kingadjacCheck(reach,side,piece);
		//count=bitCounter(reach);
		return count;
	}
	// counts the number of 1's 
	int bitCounter(long reach) {
		// TODO Auto-generated method stub

		return 0;
	}

	//Calculate mobility of queens amount of squares queens can go to
	int queenMob(int squ, int side) {
		// TODO Auto-generated method stub
		int count=0;// # of available moves
		tempreach=0;
		count+=horizontalMob(squ, side,'Q');
		count+=verticalMob(squ, side,'Q');
		count+=diagMob(squ, side,'Q');
		count+=antiDMob(squ, side,'Q');
		kingadjacCheck(tempreach, side, 'Q');
		return  count;
	}
	int bishopMob(int squ, int side) {
		// TODO Auto-generated method stub
		int count=0;// # of available moves
		tempreach=0;
		count+=diagMob(squ, side,'B');
		count+=antiDMob(squ, side,'B');
		kingadjacCheck(tempreach, side, 'B');
		return count;
	}
	int diagMob(int squ,int side , char piece){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
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
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		reachCalc(piece,reach,side);
		if(side==0){
			wReach|=reach;

		}
		else{
			bReach|=reach;
		}
		tempreach|=reach;
		return count;
	}
	private void reachCalc(char piece, long reach, int side) {
		// TODO Auto-generated method stub
		if(side==0){
			if(piece=='B')
				pieceReach[2]|=reach;
			else if(piece=='Q')
				pieceReach[4]|=reach;
			else if(piece=='R')
				pieceReach[3]|=reach;
			else if(piece=='N')
				pieceReach[1]|=reach;
			else if(piece=='P')
				pieceReach[0]|=reach;
			else if(piece=='K')
				pieceReach[5]|=reach;
		}
		else{
			if(piece=='B')
				pieceReach[8]|=reach;
			else if(piece=='Q')
				pieceReach[10]|=reach;
			else if(piece=='R')
				pieceReach[9]|=reach;
			else if(piece=='N')
				pieceReach[7]|=reach;
			else if(piece=='P')
				pieceReach[6]|=reach;
			else if(piece=='K')
				pieceReach[11]|=reach;
		}
	}
	int antiDMob(int squ,int side , char piece){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
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
				break;
			}
			else{
				count++;
				reach|=squbit;

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

				break;
			}
			else{
				count++;
				reach|=squbit;

			}
		}
		if(side==0){
			wReach|=reach;

		}
		else{
			bReach|=reach;
		}
		tempreach|=reach;
		reachCalc(piece, reach, side);
		return count;
	}
	void kingadjacCheck(long reach, int side, char piece) {
		// TODO Auto-generated method stub
		double piecepoint=0;
		if(piece=='N')piecepoint=weights[95];else if(piece=='B')piecepoint=weights[96];
		else if(piece=='R')piecepoint=weights[97];else if(piece=='Q')piecepoint=weights[98];
		if(side==0){
			if((reach&whitekingadjacent)!=0)
			{
				whitekingdefendedpoint+=piecepoint;
			}
			if((reach&blackkingadjacent)!=0)
			{
				blackkingattackedpoint+=piecepoint;
			}
		}
		if(side==1){
			if((reach&whitekingadjacent)!=0)
			{
				whitekingattackedpoint+=piecepoint;
			}
			if((reach&blackkingadjacent)!=0)
			{
				blackkingdefendedpoint+=piecepoint;
			}
		}

	}
	int horizontalMob(int squ,int side,char piece){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
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
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		tempreach|=reach;
		reachCalc(piece, reach, side);
		return count;
	}
	int verticalMob(int squ,int side, char piece){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
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
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}

		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		tempreach|=reach;
		reachCalc(piece, reach, side);
		return count;
	}

	//returns material count 
	// weights are initialized manually
	public int materialcount(){
		int score=0;
		long pos=0b1;
		for(int i=0;i<64;i++){
			pos=1;
			pos<<=i;
			if((myboard.wP&pos)!=0){
				score+=100;
			}
			else if((myboard.bP&pos)!=0){
				score-=100;

			}
			else if((myboard.wN&pos)!=0){
				score+=300;

			}
			else if((myboard.bN&pos)!=0){
				score-=300;
			}
			else if((myboard.wB&pos)!=0){
				score+=300;
			}
			else if((myboard.bB&pos)!=0){	
				score-=300;
			}
			else if((myboard.wR&pos)!=0){
				score+=500;
			}
			else if((myboard.bR&pos)!=0){
				score-=500;
			}
			else if((myboard.wQ&pos)!=0){
				score+=900;
			}	
			else if((myboard.bQ&pos)!=0){
				score-=900;
			}	
			else if((myboard.wQ&pos)!=0){
				score+=9000;
			}	
			else if((myboard.bQ&pos)!=0){
				score-=9000;
			}	
		}

		return score;
	}
}

